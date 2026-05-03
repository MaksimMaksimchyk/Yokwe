package com.example.yokwe.ui.auth.states

sealed class AuthState {
    object Loading : AuthState()
    object NotAuthenticated : AuthState()
    data class Authenticated(val familyId: String) : AuthState()
}