package com.example.yokwe.domain.repositories

import com.example.yokwe.domain.models.PetEvents

interface AiRepository {
    suspend fun generatePetMessage(
        event: PetEvents,
        petLevel: Int,
        goalName: String? = null
    ): String
}