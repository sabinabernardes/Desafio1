package com.bina.home.data.local.datasource

import com.bina.home.data.model.UserDto
import kotlinx.coroutines.flow.Flow

internal interface UsersLocalDataSource {
    fun getUsers(): Flow<List<UserDto>>
    suspend fun insertUsers(users: List<UserDto>)
    suspend fun deleteAllUsers()
}

