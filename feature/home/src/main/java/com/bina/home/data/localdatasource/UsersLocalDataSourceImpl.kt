package com.bina.home.data.localdatasource

import com.bina.home.data.database.UserDao
import com.bina.home.data.mapper.toDto
import com.bina.home.data.mapper.toEntity
import com.bina.home.data.model.UserDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class UsersLocalDataSourceImpl(
    private val userDao: UserDao
) : UsersLocalDataSource {

    override fun getUsers(): Flow<List<UserDto>> =
        userDao.observeAllUsers()
            .map { list -> list.map { it.toDto() } }

    override suspend fun insertUsers(users: List<UserDto>) {
        userDao.insertUsers(users.map { it.toEntity() })
    }

    override suspend fun deleteAllUsers() {
        userDao.clearUsers()
    }
}


