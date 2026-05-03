package com.example.yokwe.data.mappers

import com.example.yokwe.data.dto.PetDTO
import com.example.yokwe.domain.models.Pet

fun PetDTO.toDomain() = Pet(
    familyId = familyId,
    level = level,
    experience = experience,
    lastMessage = lastMessage,
    lastEvent = lastEvent
)

fun Pet.toDTO() = PetDTO(
    familyId = familyId,
    level = level,
    experience = experience,
    lastMessage = lastMessage,
    lastEvent = lastEvent
)