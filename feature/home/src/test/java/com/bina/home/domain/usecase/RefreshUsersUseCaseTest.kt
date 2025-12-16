package com.bina.home.domain.usecase

import com.bina.home.data.remote.exception.NetworkException
import com.bina.home.domain.repository.UsersRepository
import com.bina.home.helper.TestExceptionBuilder
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertFailsWith

@OptIn(ExperimentalCoroutinesApi::class)
class RefreshUsersUseCaseTest {

    private val usersRepository = mockk<UsersRepository>(relaxed = true)
    private lateinit var useCase: RefreshUsersUseCase

    @Before
    fun setup() { useCase = RefreshUsersUseCase(usersRepository) }

    @Test
    fun `invoke should call repository refresh`() = runTest {
        coEvery { usersRepository.refreshUsers() } returns Unit

        useCase()

        coVerify(exactly = 1) { usersRepository.refreshUsers() }
    }

    @Test
    fun `invoke should throw NetworkException when repository throws`() = runTest {
        val networkException = TestExceptionBuilder.createNetworkException()
        coEvery { usersRepository.refreshUsers() } throws networkException

        val thrown = assertFailsWith<NetworkException> { useCase() }
        assert(thrown.message?.contains("No internet") == true)
    }

    @Test
    fun `invoke should throw generic exception from repository`() = runTest {
        val apiErrorException = TestExceptionBuilder.createException(TestExceptionBuilder.API_ERROR_MESSAGE)
        coEvery { usersRepository.refreshUsers() } throws apiErrorException

        val thrown = assertFailsWith<Exception> { useCase() }
        assert(thrown.message == TestExceptionBuilder.API_ERROR_MESSAGE)
    }

    @Test
    fun `invoke should propagate timeout exception`() = runTest {
        val timeoutException = TestExceptionBuilder.createRuntimeException(TestExceptionBuilder.REQUEST_TIMEOUT_ERROR_MESSAGE)
        coEvery { usersRepository.refreshUsers() } throws timeoutException

        val thrown = assertFailsWith<RuntimeException> { useCase() }
        assert(thrown.message?.contains("timeout") == true)
    }

    @Test
    fun `invoke should handle database write failure`() = runTest {
        val databaseWriteException = TestExceptionBuilder.createException(TestExceptionBuilder.DATABASE_WRITE_ERROR_MESSAGE)
        coEvery { usersRepository.refreshUsers() } throws databaseWriteException

        val thrown = assertFailsWith<Exception> { useCase() }
        assert(thrown.message?.contains("database") == true)
    }
}