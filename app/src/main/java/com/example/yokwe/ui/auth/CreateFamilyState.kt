package com.example.yokwe.ui.auth

data class CreateFamilyState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val inviteCode: String? = null,
    val error: String? = null
)