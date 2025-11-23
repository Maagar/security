package com.example.security.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import com.example.security.presentation.screen.Home.HomeScreen
import com.example.security.presentation.screen.SignIn.SignInScreen
import com.example.security.presentation.screen.SignUp.SignUpScreen

@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Screen.SignIn.route
    ) {
        composable(Screen.SignIn.route) {
            SignInScreen(modifier, navigateToSignUp = {
                navController.navigate(Screen.SignUp.route)
            })
        }
        composable(Screen.SignUp.route) {
            SignUpScreen(
                modifier,
                navigateToSignIn = {
                    navController.navigate(Screen.SignIn.route)
                },
                navigateToHome = {
                    navController.navigate(Screen.Home.route)
                })
        }
        composable(Screen.Home.route) {
            HomeScreen()
        }
    }
}