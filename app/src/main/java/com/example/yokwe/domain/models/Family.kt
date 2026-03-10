package com.example.yokwe.domain.models

import java.util.Date

data class Family(
    val id: String,
    val members: List<String>,
    val inviteCode: String,
    val createdAt: Date
)
