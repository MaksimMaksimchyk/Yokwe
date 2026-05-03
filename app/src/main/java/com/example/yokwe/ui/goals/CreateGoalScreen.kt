package com.example.yokwe.ui.goals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun CreateGoalScreen(
    viewModel: CreateGoalViewModel = hiltViewModel(),
    onGoalSaved: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) onGoalSaved()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (state.step) {
            CreateGoalStep.SELECT_TYPE_OF_GOAL -> TypeSelectionStep(viewModel)
            CreateGoalStep.ENTER_DETAILS -> DetailsStep(viewModel, state)
        }
    }
}

@Composable
fun TypeSelectionStep(viewModel: CreateGoalViewModel) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text("Выберите тип цели", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(24.dp))
        Button(onClick = { viewModel.handleIntent(CreateGoalIntent.SelectFinancial) }) {
            Text("Финансовая цель")
        }
        Spacer(Modifier.height(12.dp))
        Button(onClick = { viewModel.handleIntent(CreateGoalIntent.SelectHabit) }) {
            Text("Привычка")
        }
        Spacer(Modifier.height(12.dp))
        Button(onClick = { viewModel.handleIntent(CreateGoalIntent.SelectOneTime) }) {
            Text("Задача")
        }
    }
}

@Composable
fun DetailsStep(viewModel: CreateGoalViewModel, state: CreateGoalState) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text("Детали", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = state.title,
            onValueChange = { viewModel.handleIntent(CreateGoalIntent.EnterTitle(it)) },
            label = { Text("Название") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading
        )
        if (state.selectedType == "financial") {
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = state.targetAmount,
                onValueChange = { viewModel.handleIntent(CreateGoalIntent.EnterTargetAmount(it)) },
                label = { Text("Целевая сумма (USD)") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = { viewModel.handleIntent(CreateGoalIntent.Save) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading && state.title.isNotBlank()
        ) {
            if (state.isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp))
            else Text("Сохранить")
        }
        if (state.error != null) {
            Spacer(Modifier.height(16.dp))
            Text(state.error!!, color = MaterialTheme.colorScheme.error)
        }
    }
}