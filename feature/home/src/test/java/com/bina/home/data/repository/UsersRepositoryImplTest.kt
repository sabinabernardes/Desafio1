package com.bina.home.data.repository

import com.bina.home.data.localdatasource.UsersLocalDataSource
import com.bina.home.data.remotedatasource.UsersRemoteDataSource
import com.bina.home.data.mapper.toDomain
import com.bina.home.data.model.UserDto
import com.bina.home.domain.model.User
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import app.cash.turbine.test

@OptIn(ExperimentalCoroutinesApi::class)
class UsersRepositoryImplTest {
    private lateinit var localDataSource: UsersLocalDataSource
    private lateinit var remoteDataSource: UsersRemoteDataSource
    private lateinit var repository: UsersRepositoryImpl

    @Before
    fun setUp() {
        localDataSource = mockk(relaxed = true)
        remoteDataSource = mockk(relaxed = true)
        repository = UsersRepositoryImpl(localDataSource, remoteDataSource)
    }

    @Test
    fun `given cache has users when getUsers called then emit cached users`() = runTest {
        // given
        val cachedUsers = listOf(UserDto("img", "name", "1", "username"))
        coEvery { localDataSource.getUsers() } returns kotlinx.coroutines.flow.flow { emit(cachedUsers) }
        coEvery { remoteDataSource.getUsers() } returns kotlinx.coroutines.flow.flow { emit(emptyList()) }
        // when / then
        repository.getUsers().test {
            val item = awaitItem()
            assertEquals(cachedUsers.map { it.toDomain() }, item)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given cache empty and api throws when getUsers called then emit empty list`() = runTest {
        // given
        coEvery { localDataSource.getUsers() } returns kotlinx.coroutines.flow.flow { emit(emptyList()) }
        coEvery { remoteDataSource.getUsers() } returns kotlinx.coroutines.flow.flow { throw Exception("Network error") }
        // when / then
        repository.getUsers().test {
            val item = awaitItem()
            assertEquals(emptyList<User>(), item)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given cache has users and api throws when getUsers called then emit cached users`() = runTest {
        // given
        val cachedUsers = listOf(UserDto("img", "CacheName", "3", "cacheuser"))
        coEvery { localDataSource.getUsers() } returns kotlinx.coroutines.flow.flow { emit(cachedUsers) }
        coEvery { remoteDataSource.getUsers() } returns kotlinx.coroutines.flow.flow { throw Exception("Network error") }
        // when / then
        repository.getUsers().test {
            val item = awaitItem()
            assertEquals(cachedUsers.map { it.toDomain() }, item)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given remote has users when getUsers called then emit remote users and update cache`() = runTest {
        // given
        val cachedUsers = emptyList<UserDto>()
        val remoteUsers = listOf(UserDto("img2", "Name2", "2", "username2"))
        coEvery { localDataSource.getUsers() } returns kotlinx.coroutines.flow.flow {
            emit(cachedUsers)
            delay(100)
            emit(remoteUsers)
        }
        coEvery { remoteDataSource.getUsers() } returns kotlinx.coroutines.flow.flow { emit(remoteUsers) }
        coEvery { localDataSource.insertUsers(remoteUsers) } returns Unit

        // when / then:
        repository.getUsers().test {
            val first = awaitItem()
            val second = awaitItem()
            assertEquals(remoteUsers.map { it.toDomain() }, second)
            cancelAndIgnoreRemainingEvents()
        }
        coVerify { localDataSource.insertUsers(remoteUsers) }
    }
}