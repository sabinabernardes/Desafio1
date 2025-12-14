package com.bina.home.data.localdatasource

import com.bina.home.data.database.UserDao
import com.bina.home.data.mapper.toEntity
import com.bina.home.data.model.UserDto
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class UsersLocalDataSourceImplTest {

    private val userDao: UserDao = mockk(relaxed = true)
    private lateinit var localDataSource: UsersLocalDataSourceImpl

    @Before
    fun setUp() {
        localDataSource = UsersLocalDataSourceImpl(userDao)
    }

    @Test
    fun `getUsers should map dao entities to dto`() = runTest {
        // given
        val entities = listOf(UserDto("img", "name", "1", "username").toEntity())
        every { userDao.observeAllUsers() } returns flowOf(entities)

        // when
        val result = localDataSource.getUsers().first()

        // then
        assertEquals(listOf(UserDto("img", "name", "1", "username")), result)
    }

    @Test
    fun `insertUsers should call dao with entities`() = runTest {
        // given
        val users = listOf(UserDto("img", "name", "1", "username"))

        // when
        localDataSource.insertUsers(users)

        // then
        coVerify(exactly = 1) { userDao.insertUsers(users.map { it.toEntity() }) }
    }

    @Test
    fun `deleteAllUsers should call dao clear`() = runTest {
        // when
        localDataSource.deleteAllUsers()

        // then
        coVerify(exactly = 1) { userDao.clearUsers() }
    }
}
