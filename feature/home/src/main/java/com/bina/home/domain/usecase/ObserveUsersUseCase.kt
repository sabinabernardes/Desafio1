package com.bina.home.domain.usecase

import com.bina.home.domain.model.User
import com.bina.home.domain.repository.UsersRepository
import kotlinx.coroutines.flow.Flow

internal class ObserveUsersUseCase(
    private val repository: UsersRepository
) {
    operator fun invoke(): Flow<List<User>> = repository.observeUsers()
}