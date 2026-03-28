package com.example.yokwe.ui.auth.states

data class SignInState (
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)