package com.example.yokwe.ui.goals

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun AddProgressDialog(
    goalTitle: String,
    currentAmount: Double,
    targetAmount: Double,
    currency: String,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    val amount = amountText.toDoubleOrNull() ?: 0.0
    val isValid = amount > 0 && (currentAmount + amount) <= targetAmount

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Добавить сумму") },
        text = {
            Column {
                Text("Цель: $goalTitle")
                Text("Текущая: $currentAmount $currency")
                Text("Осталось: ${targetAmount - currentAmount} $currency")
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Сумма ($currency)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = amountText.isNotEmpty() && !isValid,
                    supportingText = {
                        if (amountText.isNotEmpty() && !isValid) {
                            Text("Сумма не может превышать остаток")
                        }
                    }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(amount) },
                enabled = isValid
            ) {
                Text("Добавить")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}