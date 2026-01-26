package com.example.security.presentation.screen.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.identity.util.UUID
import com.example.security.data.repository.CryptoManager
import com.example.security.data.repository.NoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EncryptedNote(
    val id: String,
    val content: String
)

data class NoteUiState(
    val isLoading: Boolean = false,
    val notes: List<EncryptedNote> = emptyList(),
    val currentInput: String = "",
    val statusMessage: String? = null,
)

class SecretNoteViewModel(
    private val repository: NoteRepository,
    private val cryptoManager: CryptoManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(NoteUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadNotes()
    }

    fun onInputChange(text: String) {
        _uiState.update { it.copy(currentInput = text) }
    }

    fun addNote() {
        val textToSave = _uiState.value.currentInput
        if (textToSave.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, statusMessage = null) }

            val encryptedData = cryptoManager.encrypt(textToSave)

            val newNoteId = UUID.randomUUID().toString()
            val result = repository.addNote(newNoteId, encryptedData.cipherText, encryptedData.iv)

            result.onSuccess {
                _uiState.update {
                    it.copy(
                        statusMessage = "Note encrypted & added!",
                        currentInput = ""
                    )
                }
                loadNotes()
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(isLoading = false, statusMessage = "Error: ${exception.message}")
                }
            }
        }
    }

    private fun loadNotes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val result = repository.getAllNotes()

            result.onSuccess { rawNotes ->
                val decryptedNotes = rawNotes.mapNotNull { (id, cipherText, iv) ->
                    try {
                        val text = cryptoManager.decrypt(cipherText, iv)
                        EncryptedNote(id, text)
                    } catch (e: Exception) {
                        null
                    }
                }
                _uiState.update { it.copy(isLoading = false, notes = decryptedNotes) }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(isLoading = false, statusMessage = "Error loading: ${exception.message}")
                }
            }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(statusMessage = null) }
    }
}