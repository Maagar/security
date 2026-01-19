package com.example.security.presentation.screen.Home.component

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun SignOutButton(
    onSignOutClick: () -> Unit,
    enabled: Boolean
) {
    Button(onClick = onSignOutClick, enabled = enabled) {
        Text("Sign Out")
    }
}