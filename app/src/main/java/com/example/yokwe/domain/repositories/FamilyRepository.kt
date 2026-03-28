package com.example.yokwe.domain.repositories

import com.example.yokwe.data.dto.FamilyDTO
import com.google.firebase.auth.FirebaseUser

interface FamilyRepository {
    suspend fun createFamily(email: String, password: String): String
    suspend fun joinToFamily(email: String, password: String, inviteCode: String): String
    suspend fun signIn(email: String, password: String): String
    suspend fun searchFamily(inviteCode: String): FamilyDTO
    suspend fun getCurrentFamilyId(): String
    suspend fun getCurrentUser(): FirebaseUser

}