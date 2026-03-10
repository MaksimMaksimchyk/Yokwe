package com.example.yokwe.data.mappers

import com.example.yokwe.data.dto.FamilyDTO
import com.example.yokwe.domain.models.Family
import com.google.firebase.Timestamp

fun FamilyDTO.toDomain() = Family(
    id = id,
    members = members,
    inviteCode = inviteCode,
    createdAt = createdAt.toDate()
)

fun Family.toDTO() = FamilyDTO(
    id = id,
    members = members,
    inviteCode = inviteCode,
    createdAt = Timestamp(createdAt)
)