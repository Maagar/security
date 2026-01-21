package com.example.security.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import com.example.security.presentation.screen.Home.HomeScreen
import com.example.security.presentation.screen.PinInputScreen.PinScreen
import com.example.security.presentation.screen.SignIn.SignInScreen
import com.example.security.presentation.screen.SignUp.SignUpScreen
import com.example.security.presentation.screen.viewModel.AuthViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    val authViewModel: AuthViewModel = koinViewModel()

    val startRoute by authViewModel.startDestination.collectAsState()

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
                LaunchedEffect(uiState.isPinVerified) {
                    if (uiState.isPinVerified) {
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
                    }
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToSignIn = {
                        navController.navigate(Screen.SignIn.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}