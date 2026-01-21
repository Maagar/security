package com.example.security.data.repository

import android.content.Context

interface SettingsRepository {
    suspend fun setBiometricEnabled(enabled: Boolean)
    suspend fun isBiometricEnabled(): Boolean
}

class SettingsRepositoryImpl(context: Context) : SettingsRepository {
    private val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    override suspend fun setBiometricEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("BIOMETRIC_ENABLED", enabled).apply()
    }

    override suspend fun isBiometricEnabled(): Boolean {
        return prefs.getBoolean("BIOMETRIC_ENABLED", false)
    }
}
