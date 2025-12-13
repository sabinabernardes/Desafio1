package com.bina.home.domain.usecase

import app.cash.turbine.test
import com.bina.home.domain.model.User
import com.bina.home.domain.repository.UsersRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveUsersUseCaseTest {

    private val repo = mockk<UsersRepository>()
    private lateinit var useCase: ObserveUsersUseCase

    @Before
    fun setup() { useCase = ObserveUsersUseCase(repo) }

    @Test
    fun `invoke should return repository flow`() = runTest {
        val users = listOf(User(img = null, name = "n", id = "1", username = "u"))
        val repoFlow = flow { emit(users) }
        every { repo.observeUsers() } returns repoFlow

        useCase().test {
            assertEquals(users, awaitItem())
            cancelAndConsumeRemainingEvents()
        }
    }
}
