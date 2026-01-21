package com.example.security.presentation.screen.Home

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.security.presentation.screen.Home.component.SignOutButton
import com.example.security.presentation.screen.viewModel.HomeViewModel
import org.koin.compose.koinInject

@Composable
fun HomeScreen(
    onNavigateToSignIn: () -> Unit
) {
    val viewModel: HomeViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.isSignedOut) {
        if (uiState.isSignedOut) {
            viewModel.onSignOutHandled()
            onNavigateToSignIn()
        }
    }

    LaunchedEffect(uiState.signOutError) {
        uiState.signOutError?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Home Screen")

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Biometric Enabled: ")
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = uiState.isBiometricEnabled,
                    onCheckedChange = { viewModel.onToggleBiometric(it)}
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            SignOutButton(
                onSignOutClick = {
                    viewModel.onSignOutClick()
                    viewModel.onToggleBiometric(false)
                },
                enabled = !uiState.isLoading
            )
        }

        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}