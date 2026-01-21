package com.example.security.data.repository

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec


class CryptoManager {
    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }

    private val TRANSFORMATION = "AES/GCM/NoPadding"
    private val ALIAS = "secret_note_key"

    private fun getKey(): SecretKey {
        val existingKey = keyStore.getEntry(ALIAS, null) as? KeyStore.SecretKeyEntry
        return existingKey?.secretKey ?: createKey()
    }

    private fun createKey(): SecretKey {
        return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
            .apply {
                init(
                    KeyGenParameterSpec.Builder(
                        ALIAS,
                        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                    )
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        .build()
                )
            }.generateKey()
    }

    fun encrypt(data: String): EncryptedData {
        if (data.isEmpty()) return EncryptedData("", "")

        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getKey())

        val iv = cipher.iv
        val encryptedBytes = cipher.doFinal(data.toByteArray(Charsets.UTF_8))

        return EncryptedData(
            cipherText = Base64.encodeToString(encryptedBytes, Base64.DEFAULT),
            iv = Base64.encodeToString(iv, Base64.DEFAULT)
        )
    }

    fun decrypt(cipherText: String, iv: String): String {
        if (cipherText.isEmpty() || iv.isEmpty()) return ""

        return try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val spec = GCMParameterSpec(128, Base64.decode(iv, Base64.DEFAULT))
            cipher.init(Cipher.DECRYPT_MODE, getKey(), spec)

            val decodedBytes = Base64.decode(cipherText, Base64.DEFAULT)
            val decryptedBytes = cipher.doFinal(decodedBytes)

            String(decryptedBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
            "Error decryption"
        }

    }
}


data class EncryptedData(val cipherText: String, val iv: String)