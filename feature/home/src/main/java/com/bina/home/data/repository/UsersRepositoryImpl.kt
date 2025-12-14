package com.bina.home.data.repository

import com.bina.home.data.localdatasource.UsersLocalDataSource
import com.bina.home.data.mapper.toDomain
import com.bina.home.data.remotedatasource.UsersRemoteDataSource
import com.bina.home.domain.model.User
import com.bina.home.domain.repository.UsersRepository
import com.bina.home.utils.UserError
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take

internal class UsersRepositoryImpl(
    private val localDataSource: UsersLocalDataSource,
    private val remoteDataSource: UsersRemoteDataSource,
    private val errorMapper: ErrorMapper
) : UsersRepository {

    override fun observeUsers(): Flow<List<User>> =
        localDataSource.getUsers()
            .distinctUntilChanged()
            .map { it.map { dto -> dto.toDomain() } }

    override suspend fun refreshUsers() {
        try {
            remoteDataSource.getUsers()
                .take(1)
                .collect { apiList ->
                    localDataSource.insertUsers(apiList)
                }
        } catch (e: Exception) {
            if (e is CancellationException) throw e

            val userError = errorMapper.mapErrorToUserError(e)

            if (userError == UserError.Network) return

            throw Exception(userError.getMessage(), e)
        }
    }
}