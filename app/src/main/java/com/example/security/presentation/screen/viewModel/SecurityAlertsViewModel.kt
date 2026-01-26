package com.example.security.presentation.screen.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class SecurityAlert(
    val id: String,
    val title: String,
    val rawJson: String,
    val timestamp: Long = 0
)

class SecurityAlertsViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _alerts = MutableStateFlow<List<SecurityAlert>>(emptyList())
    val alerts = _alerts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        fetchAlerts()
    }

    private fun fetchAlerts() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val snapshot = firestore.collection("users")
                    .document(userId)
                    .collection("secrets")
                    .get()
                    .await()

                val suspiciousAlerts = snapshot.documents.mapNotNull { doc ->
                    if (doc.contains("cipherText") && doc.contains("iv")) {
                        return@mapNotNull null
                    }

                    val dataMap = doc.data ?: return@mapNotNull null

                    val jsonString = dataMap.entries.joinToString(separator = "\n") { (key, value) ->
                        "\"$key\": \"$value\""
                    }

                    SecurityAlert(
                        id = doc.id,
                        title = doc.getString("title") ?: "Suspicious Note without a title",
                        rawJson = "{\n$jsonString\n}"
                    )
                }
                _alerts.value = suspiciousAlerts.sortedByDescending { it.timestamp }
            } catch (e: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resolveAlert(alertId: String) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            firestore.collection("users")
                .document(userId)
                .collection("secrets")
                .document(alertId)
                .delete()
                .await()
            fetchAlerts()
        }
    }
}