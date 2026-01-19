package com.example.security.presentation.screen.SignIn.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.security.presentation.component.PhoneNumberField

@Composable
fun MissingPhoneNumberContent(onPhoneSubmitted: (String) -> Unit) {
    var phoneNumber by rememberSaveable { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Two-Factor authentication requires a phone number.")

        Spacer(modifier = Modifier.height(8.dp))

        PhoneNumberField(phoneNumber = phoneNumber, onPhoneNumberChange = {phoneNumber = it})

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { onPhoneSubmitted(phoneNumber) }) {
            Text("Submit")
        }
    }
}