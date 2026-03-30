package com.example.yokwe.data.repositories

import com.example.yokwe.data.dto.GoalDTO
import com.example.yokwe.data.mappers.toDomain
import com.example.yokwe.data.mappers.toDto
import com.example.yokwe.domain.models.Goal
import com.example.yokwe.domain.repositories.GoalsRepository
import com.example.yokwe.domain.models.GoalStats
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GoalsRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : GoalsRepository {

    override suspend fun addGoal(goal: Goal) {
        val dto = goal.toDto().copy(id = "") // id будет присвоен через Firestore
        val docRef = firestore.collection("goals").document()
        firestore.collection("goals").document(docRef.id).set(dto.copy(id = docRef.id)).await()
    }

    override suspend fun updateGoal(goal: Goal) {
        val dto = goal.toDto()
        firestore.collection("goals").document(dto.id).set(dto).await()
    }

    override suspend fun deleteGoal(goalId: String) {
        firestore.collection("goals").document(goalId).delete().await()
    }

    override fun getGoalsFlow(familyId: String): Flow<List<Goal>> = callbackFlow {
        val snapshotListener = firestore.collection("goals")
            .whereEqualTo("familyId", familyId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val goals = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(GoalDTO::class.java)?.toDomain()
                } ?: emptyList()
                trySend(goals)
            }
        awaitClose { snapshotListener.remove() }
    }

}