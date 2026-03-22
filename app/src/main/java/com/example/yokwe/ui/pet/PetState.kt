package com.example.yokwe.ui.pet

import com.example.yokwe.domain.models.Pet

data class PetState(
    val pet: Pet? = null,
    val isLoading: Boolean = true,
    val isThinking: Boolean = false,
    val error: String? = null
)
