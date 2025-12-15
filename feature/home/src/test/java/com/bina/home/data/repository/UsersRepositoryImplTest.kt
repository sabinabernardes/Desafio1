package com.bina.home.data.repository

import app.cash.turbine.test
import com.bina.home.data.localdatasource.UsersLocalDataSource
import com.bina.home.data.mapper.toDomain
import com.bina.home.data.model.UserDto
import com.bina.home.data.remotedatasource.UsersRemoteDataSource
import com.bina.home.domain.model.User
import com.bina.home.utils.UserError
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.BeforeTest
import kotlin.test.assertFailsWith

@OptIn(ExperimentalCoroutinesApi::class)
class UsersRepositoryImplTest {

    private val local: UsersLocalDataSource = mockk(relaxed = true)
    private val remote: UsersRemoteDataSource = mockk(relaxed = true)
    private val errorMapper: ErrorMapper = mockk(relaxed = true)
    private lateinit var repository: UsersRepositoryImpl

    @BeforeTest
    fun setup() {
        repository = UsersRepositoryImpl(local, remote, errorMapper)
    }

    @Test
    fun `observeUsers should map local dto to domain`() = runTest {
        val dtos = listOf(UserDto("img", "name", "1", "username"))
        every { local.getUsers() } returns flowOf(dtos)

        repository.observeUsers().test {
            assertEquals(dtos.map { it.toDomain() }, awaitItem())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `observeUsers should return empty list when local has no data`() = runTest {
        every { local.getUsers() } returns flowOf(emptyList())

        repository.observeUsers().test {
            assertEquals(emptyList<User>(), awaitItem())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `refreshUsers should fetch remote and insert into local`() = runTest {
        val remoteUsers = listOf(UserDto("img2", "Name2", "2", "username2"))
        every { remote.getUsers() } returns flowOf(remoteUsers)

        repository.refreshUsers()

        coVerify(exactly = 1) { local.insertUsers(remoteUsers) }
    }

    @Test
    fun `refreshUsers should handle empty list from remote`() = runTest {
        val emptyRemoteUsers = emptyList<UserDto>()
        every { remote.getUsers() } returns flowOf(emptyRemoteUsers)

        repository.refreshUsers()

        coVerify(exactly = 1) { local.insertUsers(emptyRemoteUsers) }
    }

    @Test
    fun `refreshUsers should NOT throw when remote fails with Network error and does not insert`() = runTest {
        every { remote.getUsers() } returns flow { throw Exception("Network error") }
        every { errorMapper.mapErrorToUserError(any()) } returns UserError.Network

        repository.refreshUsers()

        coVerify(exactly = 0) { local.insertUsers(any()) }
    }

    @Test
    fun `refreshUsers should throw when remote fails with Unknown error and does not insert`() = runTest {
        every { remote.getUsers() } returns flow { throw Exception("Boom") }
        every { errorMapper.mapErrorToUserError(any()) } returns UserError.Unknown

        assertFailsWith<Exception> { repository.refreshUsers() }

        coVerify(exactly = 0) { local.insertUsers(any()) }
    }
}
