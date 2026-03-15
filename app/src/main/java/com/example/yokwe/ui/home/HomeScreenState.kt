package com.example.yokwe.ui.home

data class HomeScreenState(
    val membersCount: Int = 0,
    val inviteCode: String? = null,
    var isLoading: Boolean = true
)
