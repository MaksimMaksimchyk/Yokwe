package com.example.yokwe.domain.repositories

import com.example.yokwe.data.dto.FamilyDTO
import com.example.yokwe.domain.models.Family
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface FamilyRepository {
    suspend fun createFamily(email: String, password: String): String
    suspend fun joinToFamily(email: String, password: String, inviteCode: String): String
    suspend fun signIn(email: String, password: String): String
    suspend fun searchFamily(inviteCode: String): FamilyDTO
    suspend fun getCurrentFamilyId(): String
    suspend fun getCurrentUser(): FirebaseUser
    fun getFamilyFlow(familyId: String): Flow<Family>

}