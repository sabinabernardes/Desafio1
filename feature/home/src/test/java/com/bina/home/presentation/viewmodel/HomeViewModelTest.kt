package com.bina.home.presentation.viewmodel

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.bina.home.data.remote.exception.NetworkException
import com.bina.home.domain.model.User
import com.bina.home.domain.usecase.ObserveUsersUseCase
import com.bina.home.domain.usecase.RefreshUsersUseCase
import com.bina.home.rule.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    private val observeUsersUseCase: ObserveUsersUseCase = mockk()
    private val refreshUsersUseCase: RefreshUsersUseCase = mockk()

    @Test
    fun `init calls refresh`() = runTest {
        every { observeUsersUseCase() } returns flowOf(emptyList())
        coEvery { refreshUsersUseCase() } returns Unit

        val viewModel = HomeViewModel(observeUsersUseCase, refreshUsersUseCase)

        viewModel.uiState.test {
            runCurrent()
            advanceUntilIdle()
            cancelAndConsumeRemainingEvents()
        }

        coVerify(exactly = 1) { refreshUsersUseCase() }
    }

    @Test
    fun `refresh can be called manually`() = runTest {
        every { observeUsersUseCase() } returns flowOf(emptyList())
        coEvery { refreshUsersUseCase() } returns Unit

        val viewModel = HomeViewModel(observeUsersUseCase, refreshUsersUseCase)

        viewModel.uiState.test {
            runCurrent()
            advanceUntilIdle()

            viewModel.refresh()
            advanceUntilIdle()

            cancelAndConsumeRemainingEvents()
        }

        coVerify(exactly = 2) { refreshUsersUseCase() }
    }

    @Test
    fun `empty database and successful refresh shows Empty`() = runTest {
        val databaseFlow = MutableStateFlow<List<User>>(emptyList())
        every { observeUsersUseCase() } returns databaseFlow
        coEvery { refreshUsersUseCase() } returns Unit

        val viewModel = HomeViewModel(observeUsersUseCase, refreshUsersUseCase)

        viewModel.uiState.test {
            runCurrent()

            val emptyState = awaitState { state ->
                state.content is HomeUiState.Content.Empty
            }
            assertTrue(emptyState.content is HomeUiState.Content.Empty)

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `airplane mode and empty database shows network Error`() = runTest {
        val databaseFlow = MutableStateFlow<List<User>>(emptyList())
        every { observeUsersUseCase() } returns databaseFlow
        coEvery { refreshUsersUseCase() } throws NetworkException("No internet connection", null)

        val viewModel = HomeViewModel(observeUsersUseCase, refreshUsersUseCase)

        viewModel.uiState.test {
            runCurrent()

            val errorState = awaitState { state ->
                state.content is HomeUiState.Content.Error
            }
            val errorContent = errorState.content as HomeUiState.Content.Error
            assertTrue(errorContent.message.isNotBlank())

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `isRefreshing is true during refresh and false after completion`() = runTest {
        every { observeUsersUseCase() } returns flowOf(emptyList())

        val refreshGate = CompletableDeferred<Unit>()
        var refreshCallCount = 0

        coEvery { refreshUsersUseCase() } coAnswers {
            refreshCallCount++
            if (refreshCallCount == 1) Unit else refreshGate.await()
        }

        val viewModel = HomeViewModel(observeUsersUseCase, refreshUsersUseCase)

        viewModel.uiState.test {
            runCurrent()
            advanceUntilIdle()

            viewModel.refresh()
            runCurrent()

            val refreshingEnabledState = awaitState { state -> state.isRefreshing }
            assertTrue(refreshingEnabledState.isRefreshing)

            refreshGate.complete(Unit)
            advanceUntilIdle()

            val refreshingDisabledState = awaitState { state -> !state.isRefreshing }
            assertFalse(refreshingDisabledState.isRefreshing)

            cancelAndConsumeRemainingEvents()
        }
    }

    private suspend fun ReceiveTurbine<HomeUiState>.awaitState(
        timeoutMillis: Long = 3_000,
        predicate: (HomeUiState) -> Boolean
    ): HomeUiState = withTimeout(timeoutMillis) {
        var latestState: HomeUiState

        while (true) {
            latestState = awaitItem()
            if (predicate(latestState)) break
        }

        latestState
    }
}
