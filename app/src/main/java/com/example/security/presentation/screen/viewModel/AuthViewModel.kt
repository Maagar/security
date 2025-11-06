package com.example.security.presentation.screen.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.security.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class authUIState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val operationSuccess: Boolean = false
)

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    val authState: StateFlow<FirebaseUser?> = repository.getAuthState()

    private val _uiState = MutableStateFlow(authUIState())
    val uiState: StateFlow<authUIState> = _uiState.asStateFlow()

    fun onLoginClick(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, operationSuccess = false) }

        }
    }
}