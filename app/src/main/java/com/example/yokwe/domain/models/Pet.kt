package com.example.yokwe.domain.models

data class Pet(
    val familyId: String,
    var level: Int,
    var experience: Int,
    var mood: PetMood,
    var lastMessage: String
)


