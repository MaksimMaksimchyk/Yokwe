package com.example.yokwe.ui.goals

import com.example.yokwe.domain.models.Goal

data class GoalsState(
    val goals: List<Goal> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
