package com.example.yokwe.domain.repositories

import com.example.yokwe.domain.models.Goal
import com.example.yokwe.domain.models.GoalStats
import kotlinx.coroutines.flow.Flow

interface GoalsRepository {
    suspend fun addGoal(goal: Goal)
    suspend fun updateGoal(goal: Goal)
    suspend fun deleteGoal(goalId: String)
    fun getGoalsFlow(familyId: String): Flow<List<Goal>>
    fun getGoalsStatsFlow(familyId: String): Flow<GoalStats>
}