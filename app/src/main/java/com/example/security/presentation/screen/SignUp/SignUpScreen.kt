package com.example.security.presentation.screen.SignUp

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.security.presentation.component.LoadingOverlay
import com.example.security.presentation.screen.SignUp.component.RegistrationFormContent
import com.example.security.presentation.screen.SignUp.component.SmsVerificationContent
import com.example.security.presentation.screen.viewModel.AuthViewModel
import com.example.security.presentation.util.startPhoneNumberVerification
import org.koin.compose.koinInject

@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    navigateToSignIn: () -> Unit,
    navigateToHome: () -> Unit
) {
    val authViewModel: AuthViewModel = koinInject()
    val uiState by authViewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val activity = context as? Activity

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var phoneNumber by rememberSaveable { mutableStateOf("") }
    var smsCode by rememberSaveable { mutableStateOf("") }

    var showPassword by rememberSaveable { mutableStateOf(false) }
    val isBusy =
        uiState.isLoading || (uiState.shouldStartPhoneNumberVerification && !uiState.isCodeSent)

    LaunchedEffect(uiState.isLoginSuccess) {
        if (uiState.isLoginSuccess) {
            navigateToHome()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { errorMsg ->
            Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
            Log.e("error:", errorMsg)
            authViewModel.clearError()
        }
    }

    LaunchedEffect(uiState.isPhoneNumberMissing) {
        if (uiState.isPhoneNumberMissing && phoneNumber.isNotBlank()) {
            authViewModel.onMissingPhoneNumberSubmitted(phoneNumber)
        }
    }

    LaunchedEffect(uiState.shouldStartPhoneNumberVerification) {
        if (uiState.shouldStartPhoneNumberVerification && activity != null && uiState.tempPhoneNumber != null) {
            startPhoneNumberVerification(
                activity = activity,
                phoneNumber = uiState.tempPhoneNumber!!,
                onCodeSent = { verificationId ->
                    authViewModel.onCodeSent(verificationId)
                },
                onVerificationCompleted = { credential ->
                    val code = credential.smsCode
                    if (code != null) {
                        authViewModel.onCodeSent("auto_verify_dummy_id")
                        authViewModel.onVerifySmsCode(code)
                    }
                },
                onVerificationFailed = { e ->
                    Toast.makeText(context, "SMS error: ${e.message}", Toast.LENGTH_LONG).show()
                    authViewModel.onRegistrationFailedOrCancelled()
                }
            )
        }
    }

    BackHandler(enabled = uiState.isCodeSent) {
        authViewModel.onRegistrationFailedOrCancelled()
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (uiState.isCodeSent) {
            SmsVerificationContent(
                smsCode = smsCode,
                onSmsCodeChange = { smsCode = it },
                onVerifyClick = { authViewModel.onVerifySmsCode(smsCode) },
                onCancelClick = { authViewModel.onRegistrationFailedOrCancelled() }
            )
        } else {
            RegistrationFormContent(
                email = email,
                onEmailChange = { email = it },
                password = password,
                onPasswordChange = { password = it },
                showPassword = showPassword,
                onShowPasswordChange = { showPassword = !showPassword },
                phoneNumber = phoneNumber,
                onPhoneNumberChange = { phoneNumber = it },
                onSignUpClick = { authViewModel.onRegisterClick(email, password) },
                navigateToSignIn = navigateToSignIn
            )
        }
        LoadingOverlay(isBusy)
    }
}