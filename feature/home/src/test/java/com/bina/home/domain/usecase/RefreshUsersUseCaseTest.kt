package com.bina.home.domain.usecase

import com.bina.home.data.remote.exception.NetworkException
import com.bina.home.domain.repository.UsersRepository
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

    private val repo = mockk<UsersRepository>(relaxed = true)
    private lateinit var useCase: RefreshUsersUseCase

    @Before
    fun setup() { useCase = RefreshUsersUseCase(repo) }

    @Test
    fun `invoke should call repository refresh`() = runTest {
        coEvery { repo.refreshUsers() } returns Unit

        useCase()

        coVerify(exactly = 1) { repo.refreshUsers() }
    }

    @Test
    fun `invoke should throw NetworkException when repository throws`() = runTest {
        val networkException = NetworkException("No internet connection", null)
        coEvery { repo.refreshUsers() } throws networkException

        val thrown = assertFailsWith<NetworkException> { useCase() }
        assert(thrown.message?.contains("No internet") == true)
    }

    @Test
    fun `invoke should throw generic exception from repository`() = runTest {
        val testException = Exception("API error")
        coEvery { repo.refreshUsers() } throws testException

        val thrown = assertFailsWith<Exception> { useCase() }
        assert(thrown.message == "API error")
    }

    @Test
    fun `invoke should propagate timeout exception`() = runTest {
        val timeoutException = RuntimeException("Request timeout")
        coEvery { repo.refreshUsers() } throws timeoutException

        val thrown = assertFailsWith<RuntimeException> { useCase() }
        assert(thrown.message?.contains("timeout") == true)
    }

    @Test
    fun `invoke should handle database write failure`() = runTest {
        val dbException = Exception("Failed to write to database")
        coEvery { repo.refreshUsers() } throws dbException

        val thrown = assertFailsWith<Exception> { useCase() }
        assert(thrown.message?.contains("database") == true)
    }
}