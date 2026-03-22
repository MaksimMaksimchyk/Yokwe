package com.example.yokwe.domain.models

data class PetEventData(
    val event: PetEvent,
    val taskName: String? = null,
    val goalName: String? = null,
    val newLevel: Int? = null,
    val userId: String? = null,
    val userEmail: String? = null,
    val amount: Double? = null
)