package com.example.yokwe.data.repositories

import com.example.yokwe.data.dto.PetDTO
import com.example.yokwe.data.mappers.toDomain
import com.example.yokwe.domain.models.Pet
import com.example.yokwe.domain.repositories.PetRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PetRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : PetRepository {

    override fun getPetFlow(familyId: String): Flow<Pet> = callbackFlow {
        val snapshotListener = firestore.collection("pets")
            .document(familyId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val pet = snapshot?.toObject(PetDTO::class.java)?.toDomain()
                if (pet != null) {
                    trySend(pet)
                }
            }
        awaitClose { snapshotListener.remove() }
    }

    override suspend fun addExperience(familyId: String, amount: Int): Result<Pet> {
        return try {
            val petRef = firestore.collection("pets").document(familyId)

            val result = firestore.runTransaction { transaction ->
                val snapshot = transaction.get(petRef)
                val petDto =
                    snapshot.toObject(PetDTO::class.java) ?: throw Exception("Pet not found")
                val currentPet = petDto.toDomain()

                // Новый опыт и уровень
                var newExperience = currentPet.experience + amount
                var newLevel = currentPet.level

                while (newExperience >= newLevel * 100) {
                    newExperience -= newLevel * 100
                    newLevel++
                }

                val updatedPet = PetDTO(
                    familyId = familyId,
                    level = newLevel,
                    experience = newExperience,
                    mood = petDto.mood,
                    lastMessage = petDto.lastMessage
                )

                transaction.set(petRef, updatedPet)
                updatedPet.toDomain()
            }.await()

            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateMood(familyId: String, mood: String): Result<Pet> {
        return try {
            val petRef = firestore.collection("pets").document(familyId)
            petRef.update("mood", mood).await()

            val snapshot = petRef.get().await()
            val pet = snapshot.toObject(PetDTO::class.java)?.toDomain()
            Result.success(pet ?: throw Exception("Pet not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateLastMessage(familyId: String, message: String): Result<Pet> {
        return try {
            val petRef = firestore.collection("pets").document(familyId)
            petRef.update("lastMessage", message).await()

            val snapshot = petRef.get().await()
            val pet = snapshot.toObject(PetDTO::class.java)?.toDomain()
            Result.success(pet ?: throw Exception("Pet not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}