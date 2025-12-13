package com.bina.home.data.remotedatasource

import com.bina.home.data.model.UserDto
import kotlinx.coroutines.flow.Flow

internal interface UsersRemoteDataSource {
    fun getUsers(): Flow<List<UserDto>>
}