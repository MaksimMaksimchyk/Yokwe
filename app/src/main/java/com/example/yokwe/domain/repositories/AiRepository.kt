package com.example.yokwe.domain.repositories

import com.example.yokwe.domain.models.PetEvent

interface AiRepository {
    suspend fun generatePetMessage(
        event: PetEvent,
        petLevel: Int,
        taskName: String? = null,
        goalName: String? = null,
        newLevel: Int? = null
    ): String
}