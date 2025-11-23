package com.example.security.presentation.util

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

fun startPhoneNumberVerification(
    activity: Activity,
    phoneNumber: String,
    onCodeSent: (String) -> Unit,
    onVerificationCompleted: (PhoneAuthCredential) -> Unit,
    onVerificationFailed: (Exception) -> Unit
) {
    val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
        .setPhoneNumber(phoneNumber)
        .setTimeout(60L, TimeUnit.SECONDS)
        .setActivity(activity)
        .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                onVerificationCompleted(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                onVerificationFailed(e)
            }

            override fun onCodeSent( verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                onCodeSent(verificationId)
            }
        })
        .build()
    PhoneAuthProvider.verifyPhoneNumber(options)
}