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

    private val homeUiStateFlow = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = homeUiStateFlow.asStateFlow()

    private var latestUsers: List<UserUi> = emptyList()
    private var refreshJob: Job? = null

    init {
        observeUsers()
        refresh()
    }

    private fun observeUsers() {
        viewModelScope.launch {
            observeUsersUseCase()
                .map { userList -> userList.map { user -> user.toUi() } }
                .distinctUntilChanged()
                .collect { userUiList ->
                    latestUsers = userUiList
                    if (homeUiStateFlow.value.content !is HomeUiState.Content.Error) {
                        homeUiStateFlow.value = homeUiStateFlow.value.copy(
                            content = if (userUiList.isEmpty()) HomeUiState.Content.Empty
                            else HomeUiState.Content.Success(userUiList)
                        )
                    }
                }
        }
    }

    fun refresh() {
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            homeUiStateFlow.value = homeUiStateFlow.value.copy(isRefreshing = true)
            try {
                refreshUsersUseCase()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val message = when (e) {
                    is NetworkException -> e.message ?: "Sem internet. Conecte-se e tente novamente."
                    else -> e.message ?: "Erro desconhecido"
                }
                homeUiStateFlow.value = homeUiStateFlow.value.copy(
                    content = HomeUiState.Content.Error(message)
                )
            } finally {
                homeUiStateFlow.value = homeUiStateFlow.value.copy(isRefreshing = false)
            }
        }
    }
}
