package com.example.yokwe.ui.goals

sealed class GoalsIntent {
    data class ToggleGoal(val goalId: String, val isCompleted: Boolean) : GoalsIntent()
    data class AddProgress(val goalId: String, val amount: Double) : GoalsIntent()
    data class DeleteGoal(val goalId: String): GoalsIntent()
}