package com.example.yokwe.ui.auth.states

data class JoinToFamilyState(
    val email: String = "",
    val password: String = "",
    val inviteCode: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)