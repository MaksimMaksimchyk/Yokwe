package com.example.yokwe.domain.repositories

import com.example.yokwe.domain.models.Goal
import kotlinx.coroutines.flow.Flow

interface GoalRepository {
    suspend fun addGoal(goal: Goal)
    suspend fun updateGoal(goal: Goal)
    suspend fun deleteGoal(goalId: String)
    fun observeGoals(familyId: String): Flow<List<Goal>>
}