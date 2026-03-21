package com.example.yokwe.ui.goals

data class CreateGoalState(
    val step: CreateGoalStep = CreateGoalStep.SELECT_TYPE_OF_GOAL,
    val selectedType: String? = null,
    val title: String = "",
    val targetAmount: String = "",
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

enum class CreateGoalStep {
    SELECT_TYPE_OF_GOAL,
    ENTER_DETAILS
}