package com.bina.home.domain.usecase

import app.cash.turbine.test
import com.bina.home.domain.model.User
import com.bina.home.domain.repository.UsersRepository
import com.bina.home.helper.TestUserBuilder
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

    private val usersRepository = mockk<UsersRepository>()
    private lateinit var useCase: ObserveUsersUseCase

    @Before
    fun setup() { useCase = ObserveUsersUseCase(usersRepository) }

    @Test
    fun `invoke should return repository flow`() = runTest {
        val singleUserList = listOf(TestUserBuilder.JOHN_DOE)
        val repositoryFlow = flow { emit(singleUserList) }
        every { usersRepository.observeUsers() } returns repositoryFlow

        useCase().test {
            assertEquals(singleUserList, awaitItem())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `invoke should return empty list when repository is empty`() = runTest {
        val repositoryFlow = flow { emit(emptyList<User>()) }
        every { usersRepository.observeUsers() } returns repositoryFlow

        useCase().test {
            assertEquals(emptyList(), awaitItem())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `invoke should propagate error from repository`() = runTest {
        val testException = Exception("Database error")
        val repositoryFlow = flow<List<User>> { throw testException }
        every { usersRepository.observeUsers() } returns repositoryFlow

        useCase().test {
            val error = awaitError()
            assertEquals("Database error", error.message)
        }
    }

    @Test
    fun `invoke should emit multiple updates from repository`() = runTest {
        val firstEmissionUserSet = TestUserBuilder.createUserList(TestUserBuilder.ALICE_JOHNSON)
        val secondEmissionUserSet = TestUserBuilder.createUserList(
            TestUserBuilder.ALICE_JOHNSON,
            TestUserBuilder.BOB_SMITH
        )

        val repositoryFlow = flow {
            emit(firstEmissionUserSet)
            emit(secondEmissionUserSet)
        }
        every { usersRepository.observeUsers() } returns repositoryFlow

        useCase().test {
            assertEquals(firstEmissionUserSet, awaitItem())
            assertEquals(secondEmissionUserSet, awaitItem())
            cancelAndConsumeRemainingEvents()
        }
    }
}
