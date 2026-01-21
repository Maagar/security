package com.example.security.presentation.screen.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.security.data.repository.CryptoManager
import com.example.security.data.repository.NoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NoteUiState(
    val isLoading: Boolean = false,
    val noteText: String = "",
    val statusMessage: String? = null,
)

class SecretNoteViewModel(
    private val repository: NoteRepository,
    private val cryptoManager: CryptoManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(NoteUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadNote()
    }

    fun onNoteChange(text: String) {
        _uiState.value = _uiState.value.copy(noteText = text)
    }

    fun saveNote() {
        val textToSave = _uiState.value.noteText
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, statusMessage = null) }
            val encryptedData = cryptoManager.encrypt(textToSave)
            val result = repository.saveNote(encryptedData.cipherText, encryptedData.iv)

            result.onSuccess {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        statusMessage = "Note encrypted and saved to cloud"
                    )
                }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        statusMessage = "Error saving note: ${exception.message}"
                    )
                }
            }
        }
    }

    private fun loadNote() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = repository.getNote()
            result.onSuccess { data ->
                if (data != null) {
                    val decryptedText = cryptoManager.decrypt(data.first, data.second)
                    _uiState.update { it.copy(isLoading = false, noteText = decryptedText) }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        statusMessage = "Error loading note: ${exception.message}"
                    )
                }
            }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(statusMessage = null) }
    }
}