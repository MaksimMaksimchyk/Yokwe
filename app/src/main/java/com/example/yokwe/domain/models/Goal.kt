package com.example.yokwe.domain.models

import java.util.Date

sealed class Goal {
    abstract val id: String
    abstract val familyId: String
    abstract val createdBy: String
    abstract val createdAt: Date
    abstract val status: GoalStatus

    data class FinancialGoal(
        override val id: String,
        override val familyId: String,
        override val createdBy: String,
        override val createdAt: Date,
        override val status: GoalStatus,
        val title: String,
        val targetAmount: Double,
        val currentAmount: Double,
        val currency: String = "USD"
    ) : Goal()

    data class DailyHabitGoal(
        override val id: String,
        override val familyId: String,
        override val createdBy: String,
        override val createdAt: Date,
        override val status: GoalStatus,
        val title: String,
        val completedDates: List<Date> = emptyList()
    ) : Goal()

    data class OneTimeGoal(
        override val id: String,
        override val familyId: String,
        override val createdBy: String,
        override val createdAt: Date,
        override val status: GoalStatus,
        val title: String,
        val isDone: Boolean = false
    ) : Goal()
}

enum class GoalStatus {
    ACTIVE, COMPLETED, FAILED
}
