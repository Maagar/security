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

data class AuthUIState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoginSuccess: Boolean = false,
    val shouldStartPhoneNumberVerification: Boolean = false,
    val isCodeSent: Boolean = false,
    val verificationId: String? = null,
)

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    val authState: StateFlow<FirebaseUser?> = repository.getAuthState()

    private val _uiState = MutableStateFlow(AuthUIState())
    val uiState: StateFlow<AuthUIState> = _uiState.asStateFlow()

    fun onLoginClick(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(error = "Email and password cannot be blank") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, isLoginSuccess = false) }

            val result = repository.signIn(email, password)
            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, isLoginSuccess = true) }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, error = error.message) }
            }

        }
    }

    fun onRegisterClick(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(error = "Fill out all fields") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val result = repository.signUp(email, password)
            result.onSuccess {
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        shouldStartPhoneNumberVerification = true,
                        error = null
                    )
                }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, error = error.message) }
            }
        }
    }

    fun onCodeSent(verificationId: String) {
        _uiState.update {
            it.copy(
                isCodeSent = true,
                verificationId = verificationId,
                shouldStartPhoneNumberVerification = false
            )
        }
    }

    fun onVerifySmsCode(code: String) {
        val verificationId = _uiState.value.verificationId
        if (verificationId == null) {
            _uiState.update { it.copy(error = "Verification ID is missing") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = repository.linkPhoneNumber(verificationId, code)

            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, isLoginSuccess = true) }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, error = error.message) }
            }
        }
    }

    fun onRegistrationFailedOrCancelled() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            repository.deleteAccount()

            _uiState.update { AuthUIState() }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

}