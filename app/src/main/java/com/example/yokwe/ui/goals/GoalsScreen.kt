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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.SettingsBackupRestore
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.yokwe.domain.models.Goal
import com.example.yokwe.domain.models.GoalStatus
import java.util.Calendar
import java.util.Date

@Composable
fun GoalsScreen(
    modifier: Modifier = Modifier,
    viewModel: GoalsViewModel = hiltViewModel(),
    onAddGoalClick: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val dialogState by viewModel.dialogState.collectAsStateWithLifecycle()

    val activeGoals = state.goals.filter { it.status == GoalStatus.ACTIVE }
    val completedGoals = state.goals.filter { it.status == GoalStatus.COMPLETED }

    Box(modifier = modifier.fillMaxSize()) {
        // Отображение диалога для финансовой цели
        dialogState?.let { dialog ->
            AddProgressDialog(
                goalTitle = dialog.goalTitle,
                currentAmount = dialog.currentAmount,
                targetAmount = dialog.targetAmount,
                currency = dialog.currency,
                onDismiss = { viewModel.handleIntent(GoalsIntent.HideAddProgressDialog) },
                onConfirm = { amount ->
                    viewModel.handleIntent(GoalsIntent.AddProgress(dialog.goalId, amount))
                    viewModel.handleIntent(GoalsIntent.HideAddProgressDialog)
                }
            )
        }

        //Основной контент
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
                //Секция активных задач
                if (activeGoals.isNotEmpty()) {
                    item {
                        Text(
                            text = "Активные", style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                        )
                    }
                    items(activeGoals) { goal ->
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
                                        GoalsIntent.ToggleGoal(goal.id, true)
                                    )

                                    else -> {}
                                }
                            },
                            onAddProgress = {
                                if (goal is Goal.FinancialGoal) {
                                    viewModel.handleIntent(GoalsIntent.ShowAddProgressDialog(goal.id))
                                }
                            },
                            onDelete = {
                                viewModel.handleIntent(GoalsIntent.DeleteGoal(goal.id))
                            }
                        )
                    }
                }

                // Секция "Завершённые" (только если есть завершённые цели)
                if (completedGoals.isNotEmpty()) {
                    item {
                        Text(
                            text = "Завершённые",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                        )
                    }
                    items(completedGoals) { goal ->
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
                            onAddProgress = {
                                if (goal is Goal.FinancialGoal) {
                                    viewModel.handleIntent(GoalsIntent.ShowAddProgressDialog(goal.id))
                                }
                            },
                            onDelete = {
                                viewModel.handleIntent(GoalsIntent.DeleteGoal(goal.id))
                            }
                        )
                    }
                }

                //Сообщение при пустом списке
                if (state.goals.isEmpty()) {
                    item {
                        Text(
                            text = "У вас пока нет целей. Нажмите +, чтобы добавить",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            textAlign = TextAlign.Center
                        )
                    }
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
    onAddProgress: () -> Unit,
    onDelete: () -> Unit
) {
    val isCompleted = goal.status == GoalStatus.COMPLETED

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted)
                MaterialTheme.colorScheme.surfaceVariant
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(top = 12.dp, start = 12.dp, end = 12.dp)) {
            when (goal) {
                is Goal.FinancialGoal -> FinancialGoalCard(
                    goal = goal,
                    onAddProgress = onAddProgress,
                    isCompleted = isCompleted,
                    onDelete = onDelete
                )

                is Goal.DailyHabitGoal -> DailyHabitGoalCard(
                    goal = goal,
                    onToggle = onToggle,
                    isCompleted = isCompleted,
                    onDelete = onDelete
                )

                is Goal.OneTimeGoal -> OneTimeGoalCard(
                    goal = goal,
                    onToggle = onToggle,
                    isCompleted = isCompleted,
                    onDelete = onDelete
                )
            }
        }

    }
}

@Composable
fun DeleteButton(onDelete: () -> Unit) {
    IconButton(onClick = onDelete) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Удалить",
            tint = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
fun FinancialGoalCard(
    goal: Goal.FinancialGoal,
    onAddProgress: () -> Unit,
    isCompleted: Boolean,
    onDelete: () -> Unit
) {
    val progress = (goal.currentAmount / goal.targetAmount).coerceIn(0.0, 1.0)
    val progressPercent = (progress * 100).toInt()

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Круговая диаграмма
            Box(
                modifier = Modifier.size(70.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { progress.toFloat() },
                    modifier = Modifier.fillMaxSize(),
                    color = if (isCompleted)
                        MaterialTheme.colorScheme.secondary
                    else
                        MaterialTheme.colorScheme.primary,
                    strokeWidth = 6.dp,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    strokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap,
                )
                Text(
                    text = "$progressPercent%",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(16.dp))

            // Информация
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = goal.title,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (isCompleted) TextDecoration.LineThrough else null
                )
                Text(
                    text = "${goal.currentAmount} / ${goal.targetAmount} ${goal.currency}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                enabled = !isCompleted,
                onClick =  onAddProgress
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Добавить сумму",
                    tint = MaterialTheme.colorScheme.error
                )
            }
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterEnd
            ) {
                DeleteButton(onDelete = onDelete)
            }
        }
    }
}

@Composable
fun DailyHabitGoalCard(
    goal: Goal.DailyHabitGoal,
    onToggle: (Boolean) -> Unit,
    isCompleted: Boolean,
    onDelete: () -> Unit
) {
    val today = Date()
    val isDoneToday = goal.completedDates.any { isSameDay(it, today) }
    val habitProgress = (goal.completedDates.size.toFloat() / 30f).coerceIn(0f, 1f) // цель 30 дней

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Круговая диаграмма
            Box(
                modifier = Modifier.size(70.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { habitProgress },
                    modifier = Modifier.fillMaxSize(),
                    color = if (isCompleted)
                        MaterialTheme.colorScheme.secondary
                    else
                        MaterialTheme.colorScheme.primary,
                    strokeWidth = 6.dp,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    strokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap,
                )
                Text(
                    text = "${goal.completedDates.size}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(16.dp))

            // Информация
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = goal.title,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (isCompleted) TextDecoration.LineThrough else null
                )
                Text(
                    text = "Выполнено: ${goal.completedDates.size} / 30 дней",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = { onToggle(!isDoneToday) }
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    modifier = Modifier.alpha(if (isDoneToday) 1f else 0.3f),
                    contentDescription = "Отметить сегодня",
                    tint = MaterialTheme.colorScheme.error
                )
            }
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterEnd
            ) {
                DeleteButton(onDelete = onDelete)
            }
        }
    }
}

@Composable
fun OneTimeGoalCard(
    goal: Goal.OneTimeGoal,
    onToggle: (Boolean) -> Unit,
    isCompleted: Boolean,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = goal.title,
                style = MaterialTheme.typography.titleMedium,
                textDecoration = if (goal.isDone) TextDecoration.LineThrough else null,
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(1f))
            if (isCompleted) {
                IconButton(
                    onClick = { onToggle(false) }

                ) {
                    Icon(
                        imageVector = Icons.Default.SettingsBackupRestore,
                        contentDescription = "Отменить выполнение",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            } else {
                IconButton(
                    onClick = { onToggle(true) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Выполнить",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterEnd
            ) {
                DeleteButton(onDelete = onDelete)
            }
        }
    }
}

fun isSameDay(date1: Date, date2: Date): Boolean {
    val cal1 = Calendar.getInstance().apply { time = date1 }
    val cal2 = Calendar.getInstance().apply { time = date2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}