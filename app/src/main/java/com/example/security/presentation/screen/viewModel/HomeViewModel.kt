package com.example.security.presentation.screen.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.security.data.repository.AuthRepository
import com.example.security.data.repository.PinRepository
import com.example.security.data.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = false,
    val signOutError: String? = null,
    val isSignedOut: Boolean = false,
    val isBiometricEnabled: Boolean = false
)

class HomeViewModel(
    private val repository: AuthRepository,
    private val pinRepository: PinRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            val enabled = settingsRepository.isBiometricEnabled()
            _uiState.update { it.copy(isBiometricEnabled = enabled) }
        }
    }

    fun onToggleBiometric(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setBiometricEnabled(enabled)
            _uiState.update { it.copy(isBiometricEnabled = enabled) }
        }
    }

    fun onSignOutClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, signOutError = null) }

            val result = repository.signOut()

            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, isSignedOut = true) }
                pinRepository.clearPin()
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, signOutError = error.message ?: "Sign out failed") }
            }
        }
    }

    fun onSignOutHandled() {
        _uiState.update { it.copy(isSignedOut = false) }
    }
}