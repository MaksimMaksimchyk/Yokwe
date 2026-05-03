package com.example.yokwe.domain.models

import java.util.Date

data class User(
    val id: String,
    val email: String,
    val familyId: String,
    val createdAt: Date
)
