package com.example.security.presentation.screen.PinInputScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.security.R
import com.example.security.presentation.screen.PinInputScreen.component.PinDot

@Composable
fun PinScreen(
    title: String,
    error: String? = null,
    onPinFilled: (String) -> Unit,
    onPinChange: () -> Unit = {},
    showBiometricIcon: Boolean = false,
    onBiometricClick: () -> Unit = {}
) {
    var pin by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(title) {
        pin = ""
    }

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(error) {
        if (error != null) {
            kotlinx.coroutines.delay(300)
            pin = ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = title, style = MaterialTheme.typography.headlineMedium)

        if (error != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = error, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(32.dp))

        BasicTextField(
            value = pin, onValueChange = { newValue ->
                if (newValue.length <= 4 && newValue.all { it.isDigit() }) {
                    pin = newValue

                    onPinChange()

                    if (newValue.length == 4) {
                        onPinFilled(newValue)
                    }
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            modifier = Modifier.focusRequester(focusRequester),
            decorationBox = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(4) { index ->
                        val isFilled = index < pin.length
                        PinDot(isFilled = isFilled)
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Enter a 4-digit Pin", style = MaterialTheme.typography.bodySmall)

        Spacer(modifier = Modifier.height(32.dp))
        if (showBiometricIcon) {
            Icon(
                painter = painterResource(id = R.drawable.fingerprint),
                contentDescription = "Biometric Login",
                modifier = Modifier
                    .size(64.dp)
                    .clickable { onBiometricClick() },
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Use Biometric Login", style = MaterialTheme.typography.bodySmall)
        }

    }
}