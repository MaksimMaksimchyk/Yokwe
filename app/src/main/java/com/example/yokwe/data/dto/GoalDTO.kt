package com.example.yokwe.data.dto

import com.google.firebase.Timestamp

data class GoalDTO(
    val id: String = "",
    val familyId: String = "",
    val createdBy: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val status: String = "ACTIVE",
    val type: String = "",
    val title: String = "",
    val targetAmount: Double = 0.0,
    val currentAmount: Double = 0.0,
    val currency: String = "USD",
    val completedDates: List<Long> = emptyList(),
    val done: Boolean = false
)
