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
import io.mockk.every
import kotlinx.coroutines.flow.flow
import kotlin.test.assertFailsWith

@OptIn(ExperimentalCoroutinesApi::class)
class UsersRepositoryImplTest {

    private val local: UsersLocalDataSource = mockk(relaxed = true)
    private val remote: UsersRemoteDataSource = mockk(relaxed = true)
    private lateinit var repo: UsersRepositoryImpl

    @Before fun setup() { repo = UsersRepositoryImpl(local, remote) }

    @Test
    fun `observeUsers should map local dto to domain`() = runTest {
        val dtos = listOf(UserDto("img", "name", "1", "username"))
        every { local.getUsers() } returns flow { emit(dtos) }

        repo.observeUsers().test {
            assertEquals(dtos.map { it.toDomain() }, awaitItem())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `refreshUsers should fetch remote and insert into local`() = runTest {
        val remoteUsers = listOf(UserDto("img2", "Name2", "2", "username2"))
        every { remote.getUsers() } returns flow { emit(remoteUsers) }

        repo.refreshUsers()

        coVerify { local.insertUsers(remoteUsers) }
    }

    @Test
    fun `refreshUsers throws when remote fails and does not insert`() = runTest {
        every { remote.getUsers() } returns flow<List<UserDto>> { throw Exception("Network error") }

        val ex = assertFailsWith<Exception> { repo.refreshUsers() }
        assertEquals("Network error", ex.message)

        coVerify(exactly = 0) { local.insertUsers(any()) }
    }
}
