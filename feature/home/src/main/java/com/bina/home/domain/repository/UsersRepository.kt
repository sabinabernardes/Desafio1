package com.bina.home.domain.repository

import com.bina.home.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UsersRepository {
    fun observeUsers(): Flow<List<User>>
    suspend fun refreshUsers()
}
