package com.example.security.presentation.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.security.R

@Composable
fun PasswordField(
    password: String, onPasswordChange: (String) -> Unit,
    showPassword: Boolean, onShowPasswordChange: () -> Unit
) {
    OutlinedTextField(
        value = password, onValueChange = onPasswordChange,
        label = { Text("Password") },
        singleLine = true,
        modifier = Modifier.padding(8.dp).fillMaxWidth(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password
        ),
        trailingIcon = {
            IconButton(onClick = onShowPasswordChange) {
                Icon(
                    painter = painterResource(id = if (showPassword) R.drawable.visibility_off else R.drawable.visibility),
                    contentDescription = null
                )
            }
        },
        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation()
    )
}