package com.example.security.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

interface NoteRepository {
    suspend fun addNote(id: String, cipherText: String, iv: String): Result<Unit>

    suspend fun getAllNotes(): Result<List<Triple<String, String, String>>>

    suspend fun deleteNote(id: String): Result<Unit>
}

private val COLLECTION_NAME = "secrets"

class NoteRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : NoteRepository {
    override suspend fun addNote(
        id: String,
        cipherText: String,
        iv: String
    ): Result<Unit> {
        return runCatching {
            val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")
            val currentToken = FirebaseMessaging.getInstance().token.await()
            val noteData = hashMapOf(
                "cipherText" to cipherText,
                "iv" to iv,
                "timestamp" to System.currentTimeMillis(),
                "senderToken" to currentToken
            )

            firestore.collection("users")
                .document(userId)
                .collection(COLLECTION_NAME)
                .document(id)
                .set(noteData)
                .await()

            Unit
        }
    }

    override suspend fun getAllNotes(): Result<List<Triple<String, String, String>>> {
        return runCatching {
            val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")

            val snapshot = firestore.collection("users")
                .document(userId)
                .collection(COLLECTION_NAME)
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                val cipher = doc.getString("cipherText")
                val iv = doc.getString("iv")

                if (!cipher.isNullOrBlank() && !iv.isNullOrBlank()) {
                    Triple(doc.id, cipher, iv)
                } else {
                    null
                }
            }
        }
    }

    override suspend fun deleteNote(id: String): Result<Unit> {
        return runCatching {
            val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")

            firestore.collection("users")
                .document(userId)
                .collection(COLLECTION_NAME)
                .document(id)
                .delete()
                .await()
            Unit
        }
    }
}