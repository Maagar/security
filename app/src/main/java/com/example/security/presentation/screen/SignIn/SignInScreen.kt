package com.example.security.presentation.screen.SignIn

import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.security.R
import org.koin.compose.koinInject

@Composable
fun SignInScreen(modifier: Modifier = Modifier, navigateToSignUp: () -> Unit) {
    val signInViewModel: SignInViewModel = koinInject()
    val email by signInViewModel.email.collectAsState()
    val password by signInViewModel.password.collectAsState()

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Security", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = email,
            onValueChange = { signInViewModel.updateEmail(it) },
            modifier = Modifier.padding(16.dp)
        )
        OutlinedTextField(value = password, onValueChange = { signInViewModel.updatePassword(it) })
        Button(onClick = { signInViewModel.signIn() }) {
            Text(stringResource(R.string.sign_in))
        }
        Row {
            Text("Don't have an account? ")
            Text(
                "Sign up",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable(
                    onClick = { navigateToSignUp }
                ))
        }
    }
}