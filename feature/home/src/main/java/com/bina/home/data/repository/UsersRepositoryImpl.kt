package com.bina.home.data.repository

import com.bina.home.data.localdatasource.UsersLocalDataSource
import com.bina.home.data.mapper.toDomain
import com.bina.home.data.remotedatasource.UsersRemoteDataSource
import com.bina.home.domain.model.User
import com.bina.home.domain.repository.UsersRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

internal class UsersRepositoryImpl(
    private val localDataSource: UsersLocalDataSource,
    private val remoteDataSource: UsersRemoteDataSource
) : UsersRepository {
    override suspend fun getUsers(): Flow<List<User>> = flow {
        coroutineScope {
            launch {
                try {
                    remoteDataSource.getUsers().collect { apiList ->
                        localDataSource.insertUsers(apiList)
                    }
                } catch (_: Throwable) {
                }
            }

            emitAll(
                localDataSource.getUsers().map { list ->
                    list.map { it.toDomain() }
                }
            )
        }
    }.catch {
        emit(emptyList())
    }
}