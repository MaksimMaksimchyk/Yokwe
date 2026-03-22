package com.example.yokwe.domain.models

data class Pet(
    val familyId: String,
    var level: Int,
    var experience: Int,
    var mood: PetMood,
    var lastMessage: String = "Привет! Я ваш общий питомец!"
) {
    val maxExperience: Int
        get() = 100 * level

    val progressToNextLevel: Float
        get() = experience.toFloat() / maxExperience
}