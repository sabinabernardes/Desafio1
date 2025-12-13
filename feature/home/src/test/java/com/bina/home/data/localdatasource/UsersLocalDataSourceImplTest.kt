package com.bina.home.data.localdatasource

import com.bina.home.data.database.UserDao
import com.bina.home.data.mapper.toEntity
import com.bina.home.data.model.UserDto
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class UsersLocalDataSourceImplTest {

    private val userDao: UserDao = mockk(relaxed = true)
    private lateinit var localDataSource: UsersLocalDataSourceImpl

    @Before fun setUp() {
        localDataSource = UsersLocalDataSourceImpl(userDao)
    }

    @Test
    fun `getUsers should map dao entities to dto`() = runTest {
        val entities = listOf(UserDto("img", "name", "1", "username").toEntity())
        every { userDao.observeAllUsers() } returns flow { emit(entities) }

        val result = localDataSource.getUsers().first()

        assertEquals(listOf(UserDto("img", "name", "1", "username")), result)
    }

    @Test
    fun `insertUsers should call dao with entities`() = runTest {
        val users = listOf(UserDto("img", "name", "1", "username"))

        localDataSource.insertUsers(users)

        coVerify { userDao.insertUsers(users.map { it.toEntity() }) }
    }

    @Test
    fun `deleteAllUsers should call dao clear`() = runTest {
        localDataSource.deleteAllUsers()
        coVerify { userDao.clearUsers() }
    }
}
