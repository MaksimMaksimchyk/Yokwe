package com.example.yokwe.ui.auth.states

data class CreateFamilyState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)