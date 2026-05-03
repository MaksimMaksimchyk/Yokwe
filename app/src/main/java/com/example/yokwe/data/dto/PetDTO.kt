package com.example.yokwe.data.dto


data class PetDTO(
    val familyId: String = "",
    var level: Int = 1,
    var experience: Int = 0,
    var lastMessage: String = "Привет! Я ваш новый питомец!",
    val lastEvent: String = ""
)
