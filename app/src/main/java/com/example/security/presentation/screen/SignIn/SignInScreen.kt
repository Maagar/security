package com.example.security.presentation.screen.SignIn

import androidx.compose.foundation.layout.Box
import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.security.presentation.component.LoadingOverlay
import com.example.security.presentation.screen.SignIn.component.LoginFormContent
import com.example.security.presentation.screen.SignIn.component.MissingPhoneNumberContent
import com.example.security.presentation.screen.SignUp.component.SmsVerificationContent
import com.example.security.presentation.screen.viewModel.AuthViewModel
import com.example.security.presentation.util.startPhoneNumberVerification
import org.koin.androidx.compose.koinViewModel

@Composable
fun SignInScreen(navigateToSignUp: () -> Unit, navigateToHome: () -> Unit) {

    val authViewModel: AuthViewModel = koinViewModel()

    val uiState by authViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val activity = context as? Activity

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var showPassword by rememberSaveable { mutableStateOf(false) }
    var smsCode by rememberSaveable { mutableStateOf("") }

    val isBusy = uiState.isLoading || (uiState.shouldStartPhoneNumberVerification && !uiState.isCodeSent)

    LaunchedEffect(uiState.isLoginSuccess) {
        if (uiState.isLoginSuccess) {
            navigateToHome()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { errorMsg ->
            Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
            authViewModel.clearError()
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
                    authViewModel.clearError()
                })
        }

    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when {
            uiState.isCodeSent -> {
                SmsVerificationContent(
                    smsCode = smsCode,
                    onSmsCodeChange = { smsCode = it },
                    onVerifyClick = { authViewModel.onVerifySmsCode(smsCode) },
                    onCancelClick = { authViewModel.onRegistrationFailedOrCancelled() }
                )
            }

            uiState.isPhoneNumberMissing -> {
                MissingPhoneNumberContent(onPhoneSubmitted = {
                    authViewModel.onMissingPhoneNumberSubmitted(
                        it
                    )
                })
            }

            else -> {
                LoginFormContent(
                    email = email,
                    onEmailChange = { email = it },
                    password = password,
                    onPasswordChange = { password = it },
                    showPassword = showPassword,
                    onShowPasswordChange = { showPassword = !showPassword },
                    onSignInClick = { authViewModel.onLoginClick(email, password) },
                    navigateToSignUp = navigateToSignUp
                )
            }
        }

        if (isBusy) {
            LoadingOverlay(isBusy)
        }
    }
}