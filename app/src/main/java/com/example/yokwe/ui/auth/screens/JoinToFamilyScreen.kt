package com.example.yokwe.ui.auth.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.yokwe.ui.auth.viewmodels.JoinToFamilyViewModel
import com.example.yokwe.ui.auth.intents.JoinToFamilyIntent

@Composable
fun JoinToFamilyScreen(
    modifier: Modifier = Modifier,
    viewModel: JoinToFamilyViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Присоединиться к семье", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))

        //Поле для инвайт кода
        OutlinedTextField(
            value = state.inviteCode,
            onValueChange = {
                viewModel.handleIntent(JoinToFamilyIntent.EnterInviteCode(it))
            },
            label = { Text("Код приглашения") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading,
            singleLine = true,
            placeholder = { Text("ABC123") }
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Поле для email
        OutlinedTextField(
            value = state.email,
            onValueChange = { viewModel.handleIntent(JoinToFamilyIntent.EnterEmail(it)) },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading,
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Поле для пароля
        OutlinedTextField(
            value = state.password,
            onValueChange = { viewModel.handleIntent(JoinToFamilyIntent.EnterPassword(it)) },
            label = { Text("Пароль") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading,
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Кнопка регистрации
        Button(
            onClick = { viewModel.handleIntent(JoinToFamilyIntent.Submit) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading &&
                    state.inviteCode.isNotBlank() &&
                    state.email.isNotBlank() &&
                    state.password.isNotBlank()
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Присоединиться")
            }
        }

        if (!state.error.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = state.error!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

    }

}