package com.example.security.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import com.example.security.presentation.screen.SignIn.SignInScreen
import com.example.security.presentation.screen.SignUp.SignUpScreen

@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Screen.signIn.route
    ) {
        composable(Screen.signIn.route) {
            SignInScreen(modifier, navigateToSignUp = {
                navController.navigate(Screen.signUp.route)
            })
        }
        composable(Screen.signUp.route) {
            SignUpScreen(modifier, navigateToSignIn = {
                navController.navigate(Screen.signIn.route)
            })
        }
    }
}