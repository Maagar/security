package com.example.security.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

interface NoteRepository {
    suspend fun saveNote(cipherText: String, iv: String): Result<Unit>
    suspend fun getNote(): Result<Pair<String, String>?>
}

class NoteRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : NoteRepository {
    override suspend fun saveNote(
        cipherText: String,
        iv: String
    ): Result<Unit> {
        return runCatching {
            val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")
            val noteData = hashMapOf(
                "cipherText" to cipherText,
                "iv" to iv,
                "timestamp" to System.currentTimeMillis()
            )

            firestore.collection("users")
                .document(userId)
                .collection("secrets")
                .document("my_note")
                .set(noteData)
                .await()

            Unit
        }
    }

    override suspend fun getNote(): Result<Pair<String, String>?> {
        return runCatching {
            val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")

            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("secrets")
                .document("my_note")
                .get()
                .await()

            if (snapshot.exists()) {
                val cipher = snapshot.getString("cipherText") ?: ""
                val iv = snapshot.getString("iv") ?: ""
                Pair(cipher, iv)
            } else {
                null
            }
        }
    }

}