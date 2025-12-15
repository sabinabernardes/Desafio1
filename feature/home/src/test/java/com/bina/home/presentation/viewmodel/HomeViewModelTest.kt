package com.bina.home.presentation.viewmodel

import app.cash.turbine.test
import com.bina.home.rule.MainDispatcherRule
import com.bina.home.domain.usecase.ObserveUsersUseCase
import com.bina.home.domain.usecase.RefreshUsersUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    private val observeUseCase: ObserveUsersUseCase = mockk()
    private val refreshUseCase: RefreshUsersUseCase = mockk(relaxed = true)

    @Test
    fun `uiState emits Error when observe throws`() = runTest {
        every { observeUseCase() } returns flow { throw RuntimeException("boom") }
        coEvery { refreshUseCase() } returns Unit

        val vm = HomeViewModel(observeUseCase, refreshUseCase)

        vm.uiState.test {
            val first = awaitItem()

            when (first) {
                HomeUiState.Loading -> {
                    advanceUntilIdle()
                    val second = awaitItem()
                    assertTrue(second is HomeUiState.Error)
                    assertEquals("boom", second.message)
                }
                is HomeUiState.Error -> {
                    assertEquals("boom", first.message)
                }
                else -> error("Estado inesperado: $first")
            }

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `init calls refresh`() = runTest {
        // given
        every { observeUseCase() } returns flowOf(emptyList())
        coEvery { refreshUseCase() } returns Unit

        // when
        HomeViewModel(observeUseCase, refreshUseCase)

        advanceUntilIdle()

        // then
        coVerify(exactly = 1) { refreshUseCase() }
    }

    @Test
    fun `refresh can be called manually`() = runTest {
        // given
        every { observeUseCase() } returns flowOf(emptyList())
        coEvery { refreshUseCase() } returns Unit
        val vm = HomeViewModel(observeUseCase, refreshUseCase)

        advanceUntilIdle()

        // when
        vm.refresh()
        advanceUntilIdle()

        // then
        coVerify(exactly = 2) { refreshUseCase() }
    }

    @Test
    fun `isRefreshing flag is set during refresh`() = runTest {
        // given
        every { observeUseCase() } returns flowOf(emptyList())
        coEvery { refreshUseCase() } coAnswers { delay(100) }

        val vm = HomeViewModel(observeUseCase, refreshUseCase)
        advanceUntilIdle()

        // when/then -
        vm.isRefreshing.test {
            assertEquals(false, awaitItem())

            vm.refresh()

            assertEquals(true, awaitItem())

            advanceUntilIdle()

            assertEquals(false, awaitItem())

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `uiState emits Loading then Empty when list is empty`() = runTest {
        // given
        every { observeUseCase() } returns flowOf(emptyList())

        val gate = CompletableDeferred<Unit>()
        coEvery { refreshUseCase() } coAnswers { gate.await() }

        val vm = HomeViewModel(observeUseCase, refreshUseCase)

        vm.uiState.test {
            assertEquals(HomeUiState.Loading, awaitItem())

            gate.complete(Unit)
            advanceUntilIdle()

            assertEquals(HomeUiState.Empty, awaitItem())

            cancelAndConsumeRemainingEvents()
        }
    }
}
