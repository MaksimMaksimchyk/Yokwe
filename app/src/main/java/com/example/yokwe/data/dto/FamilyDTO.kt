package com.example.yokwe.data.dto

import com.google.firebase.Timestamp

data class FamilyDTO(
    val id: String = "",
    val members: List<String> = emptyList(),
    val inviteCode: String = "",
    val createdAt: Timestamp = Timestamp.now()
)
