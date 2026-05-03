package com.example.yokwe.data.mappers

import com.example.yokwe.data.dto.UserDTO
import com.example.yokwe.domain.models.User
import com.google.firebase.Timestamp

fun UserDTO.toDomain() = User(
    id = id,
    email = email,
    familyId = familyId,
    createdAt = createdAt.toDate(),
)

fun User.toDTO() = UserDTO(
    id = id,
    email = email,
    familyId = familyId,
    createdAt = Timestamp(createdAt)
)