package com.bina.home.data.repository

import com.bina.home.data.localdatasource.UsersLocalDataSource
import com.bina.home.data.mapper.toDomain
import com.bina.home.data.remotedatasource.UsersRemoteDataSource
import com.bina.home.domain.model.User
import com.bina.home.domain.repository.UsersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class UsersRepositoryImpl(
    private val localDataSource: UsersLocalDataSource,
    private val remoteDataSource: UsersRemoteDataSource
) : UsersRepository {
    override fun observeUsers(): Flow<List<User>> =
        localDataSource.getUsers().map { it.map { dto -> dto.toDomain() } }

    override suspend fun refreshUsers() {
        remoteDataSource.getUsers().collect { apiList ->
            localDataSource.insertUsers(apiList)
        }
    }
}