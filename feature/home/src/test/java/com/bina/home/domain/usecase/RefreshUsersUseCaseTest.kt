package com.bina.home.domain.usecase

import com.bina.home.domain.repository.UsersRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import kotlin.test.Test

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
}