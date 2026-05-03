package com.example.yokwe.domain.models

data class Pet(
    val familyId: String,
    var level: Int,
    var experience: Int,
    var lastMessage: String,
    val lastEvent: String
) {
    val maxExperience: Int
        get() = 100 * level

    val progressToNextLevel: Float
        get() = experience.toFloat() / maxExperience
}