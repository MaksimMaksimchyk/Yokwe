package com.example.yokwe.data.mappers

import com.example.yokwe.data.dto.PetDTO
import com.example.yokwe.domain.models.Pet
import com.example.yokwe.domain.models.PetMood

fun PetDTO.toDomain() = Pet(
    familyId = familyId,
    level = level,
    experience = experience,
    mood = try {
        PetMood.valueOf(mood.uppercase())
    } catch (e: Exception) {
        PetMood.NEUTRAL
    },
    lastMessage = lastMessage
)

fun Pet.toDTO() = PetDTO (
    familyId = familyId,
    level = level,
    experience = experience,
    mood = mood.name.lowercase(),
    lastMessage = lastMessage
    )