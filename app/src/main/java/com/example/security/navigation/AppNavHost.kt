package com.example.security.navigation

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import com.example.security.presentation.screen.Home.HomeScreen
import com.example.security.presentation.screen.PinInputScreen.PinScreen
import com.example.security.presentation.screen.Secret.SecretNoteSCreen
import com.example.security.presentation.screen.SecurityAlert.SecurityAlertScreen
import com.example.security.presentation.screen.SignIn.SignInScreen
import com.example.security.presentation.screen.SignUp.SignUpScreen
import com.example.security.presentation.screen.viewModel.AuthViewModel
import com.example.security.presentation.util.BiometricPromptManager

@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier, authViewModel: AuthViewModel) {

    val startRoute by authViewModel.startDestination.collectAsState()

    val isLocked by authViewModel.isAccountLocked.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(isLocked) {
        if (isLocked) {
            Toast.makeText(context, "Account has been locked remotely", Toast.LENGTH_LONG).show()
            navController.navigate(Screen.SignIn.route) {
                popUpTo(Screen.Home.route) { inclusive = true }
            }
            authViewModel.resetLockStatus()
        }
    }

    if (startRoute == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        NavHost(
            navController = navController,
            startDestination = startRoute!!
        ) {

            composable(Screen.SignIn.route) {
                SignInScreen(
                    navigateToSignUp = {
                        navController.navigate(Screen.SignUp.route) {
                            popUpTo(Screen.SignIn.route) { inclusive = true }
                        }
                    },
                    navigateToPinSetup = {
                        navController.navigate(Screen.PinSetup.route) {
                            popUpTo(Screen.SignIn.route) { inclusive = true }
                        }
                    })
            }

            composable(Screen.SignUp.route) {
                SignUpScreen(
                    modifier,
                    navigateToSignIn = {
                        navController.navigate(Screen.SignIn.route) {
                            popUpTo(Screen.SignUp.route) { inclusive = true }
                        }
                    },
                    navigateToPinSetup = {
                        navController.navigate(Screen.PinSetup.route) {
                            popUpTo(Screen.SignUp.route) { inclusive = true }
                        }
                    })
            }
            composable(Screen.PinSetup.route) {
                val uiState by authViewModel.uiState.collectAsState()
                val title =
                    if (uiState.isPinConfirmStep) "Confirm the Pin code" else "Set up a new PIN code"

                LaunchedEffect(uiState.isPinSet) {
                    if (uiState.isPinSet) {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.PinSetup.route) { inclusive = true }
                        }
                    }
                }
                PinScreen(
                    title = title,
                    error = uiState.pinError,
                    onPinFilled = { pin ->
                        authViewModel.onSetPin(pin)
                    },
                    onPinChange = {
                        authViewModel.clearError()
                    }
                )
            }

            composable(Screen.PinLogin.route) {
                val uiState by authViewModel.uiState.collectAsState()
                val isBiometricEnabled by authViewModel.isBiometricEnabled.collectAsState()

                val context = LocalContext.current
                val biometricManager = remember {
                    BiometricPromptManager(context as AppCompatActivity)
                }

                val biometricResult by biometricManager.promptResults.collectAsState(null)

                LaunchedEffect(uiState.destinationAfterLogin) {
                    uiState.destinationAfterLogin?.let { target ->
                        navController.navigate(target) {
                            popUpTo(Screen.PinLogin.route) { inclusive = true }
                        }
                        authViewModel.onNavigationConsumed()
                    }
                }

                LaunchedEffect(biometricResult) {
                    if (biometricResult is BiometricPromptManager.BiometricResult.AuthenticationSuccess) {
                        authViewModel.onBiometricSuccess()
                    }
                }

                LaunchedEffect(Unit) {
                    if (isBiometricEnabled) {
                        biometricManager.showBiometricPrompt()
                    }
                }

                LaunchedEffect(uiState.isPinVerified) {
                    if (uiState.isPinVerified && uiState.destinationAfterLogin == null) {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.PinLogin.route) { inclusive = true }
                        }
                    }
                }
                PinScreen(
                    title = "Enter your PIN code",
                    error = uiState.pinError,
                    onPinFilled = { pin ->
                        authViewModel.onVerifyPin(pin)
                    },
                    onPinChange = {
                        authViewModel.clearError()
                    },
                    showBiometricIcon = isBiometricEnabled,
                    onBiometricClick = {
                        biometricManager.showBiometricPrompt()
                    }
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToSignIn = {
                        navController.navigate(Screen.SignIn.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    },
                    onNavigateToSecretNote = {
                        navController.navigate(Screen.SecretNote.route)
                    },
                    onNavigateToAlerts = {
                        navController.navigate(Screen.SecurityAlert.route)
                    }
                )
            }

            composable(Screen.SecretNote.route) {
                SecretNoteSCreen(
                    onNavigateBack = { navController.navigate(Screen.Home.route) })
            }

            composable(Screen.SecurityAlert.route) {
                SecurityAlertScreen(
                    onNavigateBack = { navController.navigate(Screen.Home.route) })
            }
        }
    }
}