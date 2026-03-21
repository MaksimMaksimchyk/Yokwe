package com.example.yokwe.ui.goals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.yokwe.domain.models.Goal
import java.util.Calendar
import java.util.Date

@Composable
fun GoalsScreen(
    modifier: Modifier = Modifier,
    viewModel: GoalsViewModel = hiltViewModel(),
    onAddGoalClick: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize()) {
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (state.error != null) {
            Text(
                text = "Ошибка: ${state.error}",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.goals) { goal ->
                    GoalCard(
                        goal = goal,
                        onToggle = { isCompleted ->
                            when (goal) {
                                is Goal.OneTimeGoal -> viewModel.handleIntent(
                                    GoalsIntent.ToggleGoal(
                                        goal.id,
                                        isCompleted
                                    )
                                )

                                is Goal.DailyHabitGoal -> viewModel.handleIntent(
                                    GoalsIntent.ToggleGoal(
                                        goal.id,
                                        true
                                    )
                                )

                                else -> {}
                            }
                        },
                        onAddProgress = { amount ->
                            if (goal is Goal.FinancialGoal) {
                                viewModel.handleIntent(GoalsIntent.AddProgress(goal.id, amount))
                            }
                        },
                        onDelete = {
                            viewModel.handleIntent(GoalsIntent.DeleteGoal(goal.id))
                        }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = onAddGoalClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Добавить цель")
        }
    }
}

@Composable
fun GoalCard(
    goal: Goal,
    onToggle: (Boolean) -> Unit,
    onAddProgress: (Double) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            when (goal) {
                is Goal.FinancialGoal -> FinancialGoalCard(goal, onAddProgress)
                is Goal.DailyHabitGoal -> DailyHabitGoalCard(goal, onToggle)
                is Goal.OneTimeGoal -> OneTimeGoalCard(goal, onToggle)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Удалить")
            }
        }
    }
}

@Composable
fun FinancialGoalCard(goal: Goal.FinancialGoal, onAddProgress: (Double) -> Unit) {
    val progress = (goal.currentAmount / goal.targetAmount).coerceIn(0.0, 1.0)
    Column {
        Text(goal.title, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { progress.toFloat() },
            modifier = Modifier.fillMaxWidth(),
            color = ProgressIndicatorDefaults.linearColor,
            trackColor = ProgressIndicatorDefaults.linearTrackColor,
            strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
        )
        Spacer(Modifier.height(4.dp))
        Text("${goal.currentAmount} / ${goal.targetAmount} ${goal.currency}")
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("Сумма") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = { /* нужно реализовать ввод суммы */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Добавить")
        }
    }
}

@Composable
fun DailyHabitGoalCard(goal: Goal.DailyHabitGoal, onToggle: (Boolean) -> Unit) {
    val today = Date()
    val isDoneToday = goal.completedDates.any { isSameDay(it, today) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(goal.title, style = MaterialTheme.typography.titleMedium)
            Text("Привычка", style = MaterialTheme.typography.bodySmall)
        }
        Button(onClick = { onToggle(!isDoneToday) }) {
            Text(if (isDoneToday) "Отметить" else "Выполнено")
        }
    }
}

@Composable
fun OneTimeGoalCard(goal: Goal.OneTimeGoal, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            goal.title,
            style = MaterialTheme.typography.titleMedium,
            textDecoration = if (goal.isDone) TextDecoration.LineThrough else null
        )
        Checkbox(checked = goal.isDone, onCheckedChange = onToggle)
    }
}

fun isSameDay(date1: Date, date2: Date): Boolean {
    val cal1 = Calendar.getInstance().apply { time = date1 }
    val cal2 = Calendar.getInstance().apply { time = date2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}