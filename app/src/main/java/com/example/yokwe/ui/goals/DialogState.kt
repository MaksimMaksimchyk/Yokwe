package com.example.yokwe.ui.goals

data class DialogState(
    val goalId: String,
    val goalTitle: String,
    val currentAmount: Double,
    val targetAmount: Double,
    val currency: String
)
