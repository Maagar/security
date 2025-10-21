package com.example.security.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home_screen")
    object signIn : Screen("sign_in_screen")
    object signUp : Screen("sign_Up_screen")
}