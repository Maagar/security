package com.example.security.presentation.screen.SignUp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.security.R
import com.example.security.presentation.screen.viewModel.AuthViewModel
import org.koin.compose.koinInject

@Composable
fun SignUpScreen(modifier: Modifier = Modifier, navigateToSignIn: () -> Unit) {
    val authViewModel: AuthViewModel = koinInject()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }


    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Sign up to Security", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.padding(16.dp),
            placeholder = { Text("Email") },
            label = { Text("Email") })

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            placeholder = { Text("Phone Number") },
            label = { Text("Phone Number") })

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text("Password") },
            label = { Text("Password") })

        Button(modifier = Modifier.padding(12.dp), onClick = { }) {
            Text(stringResource(R.string.sign_up))
        }
        Row {
            Text("Already have an account? ")
            Text(
                "Sign in",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { navigateToSignIn() }
                ))
        }
    }
}