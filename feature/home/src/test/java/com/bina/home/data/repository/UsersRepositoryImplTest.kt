package com.bina.home.data.repository

import com.bina.home.data.localdatasource.UsersLocalDataSource
import com.bina.home.data.remotedatasource.UsersRemoteDataSource
import com.bina.home.data.mapper.toDomain
import com.bina.home.data.model.UserDto
import com.bina.home.domain.model.User
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import app.cash.turbine.test
import io.mockk.every
import kotlinx.coroutines.flow.flow
import kotlin.test.assertFailsWith

@OptIn(ExperimentalCoroutinesApi::class)
class UsersRepositoryImplTest {

    private val local: UsersLocalDataSource = mockk(relaxed = true)
    private val remote: UsersRemoteDataSource = mockk(relaxed = true)
    private val errorMapper: ErrorMapper = mockk(relaxed = true)
    private lateinit var repository: UsersRepositoryImpl

    @Before
    fun setup() {
        repository = UsersRepositoryImpl(local, remote, errorMapper)
    }

    @Test
    fun `observeUsers should map local dto to domain`() = runTest {
        val dtos = listOf(UserDto("img", "name", "1", "username"))
        every { local.getUsers() } returns flow { emit(dtos) }

        repository.observeUsers().test {
            assertEquals(dtos.map { it.toDomain() }, awaitItem())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `refreshUsers should fetch remote and insert into local`() = runTest {
        val remoteUsers = listOf(UserDto("img2", "Name2", "2", "username2"))
        every { remote.getUsers() } returns flow { emit(remoteUsers) }

        repository.refreshUsers()

        coVerify { local.insertUsers(remoteUsers) }
    }

    @Test
    fun `refreshUsers throws when remote fails and does not insert`() = runTest {
        every { remote.getUsers() } returns flow<List<UserDto>> { throw Exception("Network error") }
        every { errorMapper.mapErrorToUserError(any()) } returns com.bina.home.utils.UserError.Unknown

        val ex = assertFailsWith<Exception> { repository.refreshUsers() }

        coVerify(exactly = 0) { local.insertUsers(any()) }
    }

    @Test
    fun `observeUsers should return empty list when local has no data`() = runTest {
        val emptyDtos = emptyList<UserDto>()
        every { local.getUsers() } returns flow { emit(emptyDtos) }

        repository.observeUsers().test {
            assertEquals(emptyList<User>(), awaitItem())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `refreshUsers should handle empty list from remote`() = runTest {
        val emptyRemoteUsers = emptyList<UserDto>()
        every { remote.getUsers() } returns flow { emit(emptyRemoteUsers) }

        repository.refreshUsers()

        coVerify { local.insertUsers(emptyRemoteUsers) }
    }
}


