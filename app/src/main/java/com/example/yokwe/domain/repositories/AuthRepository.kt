package com.example.yokwe.domain.repositories

interface AuthRepository {
    suspend fun createFamily(email: String, password: String): String
}