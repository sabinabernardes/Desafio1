package com.bina.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bina.home.data.remote.exception.NetworkException
import com.bina.home.domain.usecase.ObserveUsersUseCase
import com.bina.home.domain.usecase.RefreshUsersUseCase
import com.bina.home.presentation.screen.UserUi
import com.bina.home.presentation.screen.toUi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

internal class HomeViewModel(
    private val observeUsersUseCase: ObserveUsersUseCase,
    private val refreshUsersUseCase: RefreshUsersUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var latestUsers: List<UserUi> = emptyList()
    private var refreshJob: Job? = null

    init {
        observeUsers()
        refresh()
    }

    private fun observeUsers() {
        viewModelScope.launch {
            observeUsersUseCase()
                .map { it.map { u -> u.toUi() } }
                .distinctUntilChanged()
                .collect { list ->
                    latestUsers = list
                    if (_uiState.value.content !is HomeUiState.Content.Error) {
                        _uiState.value = _uiState.value.copy(
                            content = if (list.isEmpty()) HomeUiState.Content.Empty
                            else HomeUiState.Content.Success(list)
                        )
                    }
                }
        }
    }

    fun refresh() {
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true)
            try {
                refreshUsersUseCase()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val message = when (e) {
                    is NetworkException -> e.message ?: "Sem internet. Conecte-se e tente novamente."
                    else -> e.message ?: "Erro desconhecido"
                }
                _uiState.value = _uiState.value.copy(
                    content = HomeUiState.Content.Error(message)
                )
            } finally {
                _uiState.value = _uiState.value.copy(isRefreshing = false)
            }
        }
    }
}
