package com.example.yokwe.data.dto

import com.google.firebase.Timestamp

data class UserDTO(
    val id: String = "",
    val email: String = "",
    val familyId: String = "",
    val createdAt: Timestamp = Timestamp.now()
)
