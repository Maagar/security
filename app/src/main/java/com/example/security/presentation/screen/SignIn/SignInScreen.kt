package com.example.security.presentation.screen.SignIn

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.koin.compose.koinInject

@Composable
fun SignInScreen(name: String, modifier: Modifier = Modifier) {
    val signInViewModel: SignInViewModel = koinInject()

    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}