package com.example.yokwe.domain.repositories

import com.example.yokwe.ui.auth.states.AuthState
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun observeAuthState(): Flow<AuthState>
}