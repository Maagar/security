package com.example.security.presentation.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun PhoneNumberField(
    phoneNumber: String, onPhoneNumberChange: (String) -> Unit
) {
    val countryCode = "+48"
    val maxDigits = 9

    val displayValue = if (phoneNumber.startsWith(countryCode)) {
        phoneNumber.removePrefix(countryCode)
    } else {
        phoneNumber
    }

    OutlinedTextField(
        value = displayValue,
        onValueChange = { newValue ->
            val digitsOnly = newValue.filter { it.isDigit() }
            if (digitsOnly.length <= maxDigits) {
                if (digitsOnly.isEmpty()) {
                    onPhoneNumberChange("")
                } else {
                    onPhoneNumberChange(countryCode + digitsOnly)
                }
            }
        },
        prefix = {Text(countryCode)},
        label = { Text("Phone Number") },
        placeholder = { Text("000 000 000") },
        singleLine = true,
        visualTransformation = PhoneNumberTransformation(),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Phone
        )
    )
}

class PhoneNumberTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length >= 9) text.text.substring(0..8) else text.text
        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            if ((i + 1) % 3 == 0 && i != 8 && i != trimmed.lastIndex) {
                out += " "
            }
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 3) return offset
                if (offset <= 6) return offset + 1
                return offset + 2
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 3) return offset
                if (offset <= 7) return offset - 1
                return offset - 2
            }
        }

        return TransformedText(AnnotatedString(out), offsetMapping)
    }
}