package com.example.yokwe.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    familyId: String
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val currentUser = FirebaseAuth.getInstance().currentUser

    LaunchedEffect(familyId) {
        viewModel.loadFamilyInfo(familyId)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Добро пожаловать!",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Email: ${currentUser?.email ?: "Неизвестно"}",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "Family ID: $familyId",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(32.dp))
        if (state.membersCount == 1 && state.inviteCode != null && !state.isLoading) {
            InviteCodeBanner(inviteCode = state.inviteCode!!)
        }
        Button(onClick = {
            FirebaseAuth.getInstance().signOut()
        }) { Text("Выйти") }
    }
}

@Composable
fun InviteCodeBanner(
    inviteCode: String
) {
    val clipboard = LocalClipboardManager.current

    Text(text = "Код приглашения: ", style = MaterialTheme.typography.headlineMedium)
    Spacer(modifier = Modifier.height(16.dp))
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Text(
            text = inviteCode,
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            textAlign = TextAlign.Center
        )
    }
    Spacer(modifier = Modifier.height(16.dp))
    Button(
        onClick = {
            clipboard.setText(AnnotatedString(inviteCode))
        }, modifier = Modifier.fillMaxWidth()
    ) {
        Text("Копировать код")
    }
    Spacer(modifier = Modifier.height(16.dp))

}