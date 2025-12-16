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

    @Test
    fun `invoke should return empty list when repository is empty`() = runTest {
        val repoFlow = flow { emit(emptyList<User>()) }
        every { repo.observeUsers() } returns repoFlow

        useCase().test {
            assertEquals(emptyList(), awaitItem())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `invoke should propagate error from repository`() = runTest {
        val testException = Exception("Database error")
        val repoFlow = flow<List<User>> { throw testException }
        every { repo.observeUsers() } returns repoFlow

        useCase().test {
            val error = awaitError()
            assertEquals("Database error", error.message)
        }
    }

    @Test
    fun `invoke should emit multiple updates from repository`() = runTest {
        val firstUsers = listOf(User(img = null, name = "User1", id = "1", username = "u1"))
        val secondUsers = listOf(
            User(img = null, name = "User1", id = "1", username = "u1"),
            User(img = null, name = "User2", id = "2", username = "u2")
        )

        val repoFlow = flow {
            emit(firstUsers)
            emit(secondUsers)
        }
        every { repo.observeUsers() } returns repoFlow

        useCase().test {
            assertEquals(firstUsers, awaitItem())
            assertEquals(secondUsers, awaitItem())
            cancelAndConsumeRemainingEvents()
        }
    }
}
