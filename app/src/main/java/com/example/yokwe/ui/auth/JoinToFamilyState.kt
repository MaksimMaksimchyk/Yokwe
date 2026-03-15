package com.example.yokwe.ui.auth

data class JoinToFamilyState(
    val email: String = "",
    val password: String = "",
    val inviteCode: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val familyId: String = "",
    val error: String? = null
)