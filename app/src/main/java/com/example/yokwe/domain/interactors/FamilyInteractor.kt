package com.example.yokwe.domain.interactors

import com.example.yokwe.domain.models.Family
import com.example.yokwe.domain.models.Pet
import com.example.yokwe.domain.repositories.FamilyRepository
import com.example.yokwe.domain.repositories.GoalsRepository
import com.example.yokwe.domain.repositories.PetRepository
import com.example.yokwe.domain.models.GoalStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FamilyInteractor @Inject constructor(
    private val familyRepository: FamilyRepository,
    private val petRepository: PetRepository,
    private val goalsRepository: GoalsRepository
) {

    suspend fun createFamily(email: String, password: String): String {
        val familyId = familyRepository.createFamily(email, password)
        petRepository.updateLastEvent(familyId, "$email создал новую семью")
        return familyId
    }

    suspend fun joinToFamily(email: String, password: String, inviteCode: String): String {
        val mutualFamilyId = familyRepository.joinToFamily(email, password, inviteCode)
        petRepository.updateLastEvent(mutualFamilyId, "$email присоединился к семье")
        return mutualFamilyId
    }

    suspend fun signIn(email: String, password: String): String {
        val signedFamilyId = familyRepository.signIn(email, password)
        return signedFamilyId
    }

    fun getPetFlow(familyId: String): Flow<Pet> {
        return petRepository.getPetFlow(familyId)
    }

    fun getGoalsStatsFlow(familyId: String): Flow<GoalStats> {
        return goalsRepository.getGoalsStatsFlow(familyId)
    }

    fun getFamilyFlow(familyId: String): Flow<Family> {
        return familyRepository.getFamilyFlow(familyId)
    }


}