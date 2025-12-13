package com.bina.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bina.home.domain.usecase.ObserveUsersUseCase
import com.bina.home.domain.usecase.RefreshUsersUseCase
import com.bina.home.presentation.screen.toUi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class HomeViewModel(
    observeUsersUseCase: ObserveUsersUseCase,
    private val refreshUsersUseCase: RefreshUsersUseCase
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> =
        observeUsersUseCase()
            .map { users ->
                HomeUiState.Success(users.map { it.toUi() }) as HomeUiState
            }
            .catch { e ->
                emit(HomeUiState.Error(e.message ?: "Erro desconhecido"))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = HomeUiState.Loading
            )

    init { refresh() }

    fun refresh() = viewModelScope.launch {
        runCatching { refreshUsersUseCase() }
    }
}

