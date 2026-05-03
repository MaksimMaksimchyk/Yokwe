package com.example.yokwe.domain.repositories

import com.example.yokwe.domain.models.Pet
import kotlinx.coroutines.flow.Flow

interface PetRepository {
    fun getPetFlow(familyId: String): Flow<Pet>
    suspend fun addExperience(familyId: String, amount: Int): Result<Pet>
    suspend fun updateLastMessage(familyId: String, message: String): Result<Pet>
    suspend fun updateLastEvent(familyId: String, event: String): Result<Pet>
    suspend fun getCurrentPetLevel(familyId: String): Int

}