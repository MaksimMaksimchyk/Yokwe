package com.example.yokwe.ui.home

import com.example.yokwe.domain.models.Pet
import com.example.yokwe.domain.models.GoalStats
import com.example.yokwe.domain.models.User
import com.google.firebase.auth.FirebaseAuth

data class HomeScreenState(
    val membersCount: Int = 0,
    val inviteCode: String? = null,
    var isLoading: Boolean = true,
    val goalStats: GoalStats = GoalStats(),
    val pet: Pet? = null,
    val currentUserEmail: String? = FirebaseAuth.getInstance().currentUser?.email,
    val error: String? = null
)
