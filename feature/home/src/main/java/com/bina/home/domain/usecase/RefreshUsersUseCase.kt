package com.bina.home.domain.usecase

import com.bina.home.domain.repository.UsersRepository

internal class RefreshUsersUseCase(
    private val repository: UsersRepository
) {
    suspend operator fun invoke() = repository.refreshUsers()
}