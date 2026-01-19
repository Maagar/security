package com.example.security.presentation.screen.SignIn.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.security.R
import com.example.security.presentation.component.EmailField
import com.example.security.presentation.component.PasswordField

@Composable
fun LoginFormContent(
    email: String, onEmailChange: (String) -> Unit,
    password: String, onPasswordChange: (String) -> Unit,
    showPassword: Boolean, onShowPasswordChange: () -> Unit,
    onSignInClick: () -> Unit,
    navigateToSignUp: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        Text("Sign in to Security", style = MaterialTheme.typography.headlineMedium)

        EmailField(email = email, onEmailChange = onEmailChange)

        PasswordField(
            password = password, onPasswordChange = onPasswordChange,
            showPassword = showPassword, onShowPasswordChange = onShowPasswordChange
        )

        Button(modifier = Modifier.padding(12.dp), onClick = onSignInClick) {
            Text(stringResource(R.string.sign_in))
        }

        Row {
            Text("Don't have an account? ")
            Text(
                "Sign up",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { navigateToSignUp() }
                ))
        }
    }


}