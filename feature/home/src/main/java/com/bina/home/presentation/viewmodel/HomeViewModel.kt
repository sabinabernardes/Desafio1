package com.bina.home.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bina.home.domain.usecase.ObserveUsersUseCase
import com.bina.home.domain.usecase.RefreshUsersUseCase
import com.bina.home.presentation.screen.toUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class HomeViewModel(
    observeUsersUseCase: ObserveUsersUseCase,
    private val refreshUsersUseCase: RefreshUsersUseCase
) : ViewModel() {

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _initialRefreshDone = MutableStateFlow(false)

    val uiState: StateFlow<HomeUiState> =
        combine(
            observeUsersUseCase(),
            _isRefreshing,
            _initialRefreshDone
        ) { users, refreshing, initialDone ->

            val userUiList = users.map { it.toUi() }

            when {
                userUiList.isEmpty() && (!initialDone || refreshing) ->
                    HomeUiState.Loading

                userUiList.isEmpty() ->
                    HomeUiState.Empty

                else ->
                    HomeUiState.Success(userUiList)
            }
        }
            .distinctUntilChanged()
            .catch { e ->
                emit(HomeUiState.Error(e.message ?: "Erro desconhecido"))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = HomeUiState.Loading
            )

    init {
        refresh()
    }

    fun refresh() {
        if (_isRefreshing.value) return

        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                refreshUsersUseCase()
            } catch (e: Exception) {
                // Offline-first
                Log.e("HomeViewModel", "Erro ao atualizar contatos", e)
            } finally {
                _isRefreshing.value = false
                _initialRefreshDone.value = true
            }
        }
    }
}
