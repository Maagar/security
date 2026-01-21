package com.example.security.data.repository

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

interface PinRepository {
    suspend fun savePin(pin: String)
    suspend fun validatePin(pin: String): Boolean
    suspend fun hasPin(): Boolean
    suspend fun clearPin()
}

class PinRepositoryImpl(context: Context) : PinRepository {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_app_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    override suspend fun savePin(pin: String) {
        sharedPreferences.edit().putString("USER_PIN", pin).apply()
    }

    override suspend fun validatePin(pin: String): Boolean {
        val savedPin = sharedPreferences.getString("USER_PIN", null)
        return savedPin == pin
    }

    override suspend fun hasPin(): Boolean {
        return sharedPreferences.contains("USER_PIN")
    }

    override suspend fun clearPin() {
        sharedPreferences.edit().remove("USER_PIN").apply()
    }
}