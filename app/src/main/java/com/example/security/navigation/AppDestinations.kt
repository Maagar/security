package com.example.security.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home_screen")
    object SignIn : Screen("sign_in_screen")
    object SignUp : Screen("sign_Up_screen")

    object PinSetup : Screen("pin_setup_screen")
    object PinLogin : Screen("pin_login_screen")
    object SecretNote : Screen("secret_note_screen")
    object SecurityAlert : Screen("security_alert_screen")
}