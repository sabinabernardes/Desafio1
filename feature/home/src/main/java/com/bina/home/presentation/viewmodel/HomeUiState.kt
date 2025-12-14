package com.bina.home.presentation.viewmodel

import com.bina.home.presentation.screen.UserUi

sealed class HomeUiState {
    data object Loading : HomeUiState()
    data class Success(val users: List<UserUi>) : HomeUiState()
    data object Empty : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}
