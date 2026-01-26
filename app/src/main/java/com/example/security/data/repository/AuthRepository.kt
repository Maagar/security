package com.example.security.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.tasks.await

interface AuthRepository {
    val currentUser: FirebaseUser?
    fun getAuthState(): StateFlow<FirebaseUser?>
    suspend fun signIn(email: String, password: String): Result<FirebaseUser>
    suspend fun signUp(email: String, password: String): Result<FirebaseUser>
    suspend fun linkPhoneNumber(verificationId: String, smsCode: String): Result<FirebaseUser>
    suspend fun deleteAccount(): Result<Unit>
    suspend fun signOut(): Result<Unit>
    suspend fun verify2FALogin(verificationId: String, smsCode: String): Result<Unit>
    fun observeUserStatus(): kotlinx.coroutines.flow.Flow<String>
    suspend fun updateFcmToken()
}

class AuthRepositoryImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override val currentUser: FirebaseUser?
        get() = auth.currentUser


    override fun getAuthState(): StateFlow<FirebaseUser?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser)
        }
        auth.addAuthStateListener(authStateListener)

        awaitClose { auth.removeAuthStateListener(authStateListener) }
    }.stateIn(
        scope = CoroutineScope(Dispatchers.IO),
        initialValue = auth.currentUser,
        started = SharingStarted.WhileSubscribed(5000)
    )

    override suspend fun signIn(email: String, password: String): Result<FirebaseUser> {
        return runCatching {
            val userCredentials = auth.signInWithEmailAndPassword(email, password).await()
            val user = userCredentials.user!!
            checkUserStatusOrThrow(user.uid)
            user
        }
    }

    override suspend fun signUp(email: String, password: String): Result<FirebaseUser> {
        return runCatching {
            val userCredentials = auth.createUserWithEmailAndPassword(email, password).await()
            userCredentials.user!!
        }
    }

    override suspend fun linkPhoneNumber(
        verificationId: String,
        smsCode: String
    ): Result<FirebaseUser> {
        return runCatching {
            val currentUser =
                auth.currentUser ?: throw Exception("No user loggged in to link phone number")

            val credential = PhoneAuthProvider.getCredential(verificationId, smsCode)
            val result = currentUser.linkWithCredential(credential).await()
            result.user ?: throw Exception("Linking failed: User is null")
        }
    }

    override suspend fun deleteAccount(): Result<Unit> {
        return runCatching {
            auth.currentUser?.delete()?.await()
            Unit
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return runCatching {
            auth.signOut()
        }
    }

    override suspend fun verify2FALogin(verificationId: String, smsCode: String): Result<Unit> {
        return runCatching {
            val user = auth.currentUser ?: throw Exception("No user logged in")
            val credential = PhoneAuthProvider.getCredential(verificationId, smsCode)
            user.reauthenticate(credential).await()
            checkUserStatusOrThrow(user.uid)
            Unit
        }
    }

    override fun observeUserStatus(): Flow<String> = callbackFlow {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            close()
            return@callbackFlow
        }
        val listener = firestore.collection("users").document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    if (error.code == FirebaseFirestoreException.Code.PERMISSION_DENIED) {
                        android.util.Log.d(
                            "AuthRepository",
                            "Wylogowano - zatrzymuję nasłuch statusu"
                        )
                        close()
                    } else {
                        close(error)
                    }
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val status = snapshot.getString("accountStatus") ?: "ACTIVE"
                    trySend(status)
                }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun updateFcmToken() {
        val userId = auth.currentUser?.uid ?: return
        try {
            val token = FirebaseMessaging.getInstance().token.await()
            val data = mapOf("fcmToken" to token)
            firestore.collection("users").document(userId)
                .set(data, SetOptions.merge())
                .await()

            Log.d("FCM", "Token zaktualizowany: $token")
        } catch (e: Exception) {
            Log.e("FCM", "Błąd zapisu tokena", e)
        }
    }

    private suspend fun checkUserStatusOrThrow(uid: String) {
        val snapshot = firestore.collection("users").document(uid).get().await()
        val status = snapshot.getString("accountStatus") ?: "ACTIVE"

        if (status == "BANNED" || status == "LOCKED") {
            auth.signOut()
            throw Exception("Twoje konto zostało zablokowane przez administratora.")
        }
    }
}