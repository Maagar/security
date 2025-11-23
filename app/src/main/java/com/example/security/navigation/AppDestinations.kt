package com.example.security.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home_screen")
    object SignIn : Screen("sign_in_screen")
    object SignUp : Screen("sign_Up_screen")
}