package com.example.yokwe.ui.home

import com.example.yokwe.domain.models.PetEventData
import com.example.yokwe.ui.goals.GoalStats

data class HomeScreenState(
    val membersCount: Int = 0,
    val inviteCode: String? = null,
    var isLoading: Boolean = true,
    val goalStats: GoalStats = GoalStats(),
    val lastEvent: PetEventData? = null,
    val error: String? = null
)
