package com.example.security.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.tasks.await

interface AuthRepository {
    fun getAuthState(): StateFlow<FirebaseUser?>
    suspend fun signIn(email: String, password: String): Result<FirebaseUser>
    suspend fun signUp(email: String, password: String): Result<FirebaseUser>
    suspend fun linkPhoneNumber(verificationId: String, smsCode: String): Result<FirebaseUser>
    suspend fun deleteAccount(): Result<Unit>
    suspend fun signOut()
}

class AuthRepositoryImpl(
    private val auth: FirebaseAuth
) : AuthRepository {

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
            userCredentials.user!!
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
            val currentUser = auth.currentUser ?: throw Exception("No user loggged in to link phone number")

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

    override suspend fun signOut() {
        auth.signOut()
    }
}