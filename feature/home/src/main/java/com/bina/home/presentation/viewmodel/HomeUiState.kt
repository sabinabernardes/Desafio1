package com.bina.home.presentation.viewmodel

import androidx.compose.runtime.Immutable
import com.bina.home.presentation.screen.UserUi

@Immutable
data class HomeUiState(
    val content: Content = Content.Loading,
    val isRefreshing: Boolean = false
) {
    @Immutable
    sealed interface Content {
        data object Loading : Content
        data object Empty : Content
        data class Success(val users: List<UserUi>) : Content
        data class Error(val message: String) : Content
    }
}
