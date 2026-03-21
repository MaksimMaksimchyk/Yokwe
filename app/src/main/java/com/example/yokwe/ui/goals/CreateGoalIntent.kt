package com.example.yokwe.ui.goals

sealed class CreateGoalIntent {
    object SelectFinancial : CreateGoalIntent()
    object SelectHabit : CreateGoalIntent()
    object SelectOneTime : CreateGoalIntent()
    data class EnterTitle(val value: String) : CreateGoalIntent()
    data class EnterTargetAmount(val value: String) : CreateGoalIntent()
    object Save : CreateGoalIntent()
    object NavigateBack : CreateGoalIntent()
}