package com.example.yokwe.data.repositories

import com.example.yokwe.data.dto.FamilyDTO
import com.example.yokwe.data.dto.PetDTO
import com.example.yokwe.data.dto.UserDTO
import com.example.yokwe.domain.repositories.FamilyRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FamilyRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : FamilyRepository {

    companion object {
        const val MAX_FAMILY_SIZE = 2
    }

    override suspend fun createFamily(email: String, password: String): String {
        //Новый пользователь
        val authResult = auth.createUserWithEmailAndPassword(email, password).await()
        val newUserId = authResult.user?.uid ?: throw Exception("Ошибка создания пользователя")
        //Генерация инвайт-кода
        val inviteCode = (1..6).map { "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".random() }.joinToString("")

        // Ссылки на документы фаерстор
        val familyRef = firestore.collection("families").document()
        val familyId = familyRef.id
        val userRef = firestore.collection("users").document(newUserId)
        val petRef = firestore.collection("pets").document(familyId)

        // Новые объекты для записи
        val newFamily =
            FamilyDTO(id = familyId, members = listOf(newUserId), inviteCode = inviteCode)
        val newUser = UserDTO(id = newUserId, email = email, familyId = familyId)
        val newPet = PetDTO(familyId = familyId)

        // Групповая запись в фаерстор
        firestore.runBatch { batch ->
            batch.set(familyRef, newFamily)
            batch.set(userRef, newUser)
            batch.set(petRef, newPet)
        }.await()

        return familyId
    }

    override suspend fun joinToFamily(
        email: String,
        password: String,
        inviteCode: String
    ): String {
        //Новый пользователь
        val authResult = auth.createUserWithEmailAndPassword(email, password).await()
        val newUserId = authResult.user?.uid ?: throw Exception("Auth failed")

        //Поиск семьи по коду:
        val family = searchFamily(inviteCode)
        val familyId = family.id

        // Ссылки на документы фаерстор
        val userRef = firestore.collection("users").document(newUserId)
        val familyRef = firestore.collection("families").document(familyId)

        // Новые объекты для записи
        val newUser = UserDTO(id = newUserId, email = email, familyId = familyId)
        val updatedMembers = family.members + newUserId

        // Групповая запись в фаерстор
        firestore.runBatch { batch ->
            batch.set(userRef, newUser)
            batch.update(familyRef, "members", updatedMembers)
        }.await()

        return familyId
    }

    override suspend fun signIn(email: String, password: String): String {
        val authResult = auth.signInWithEmailAndPassword(email, password).await()
        val userId = authResult.user?.uid ?: ("Error")
        val userDoc = firestore.collection("users").document(userId).get().await()
        val familyId = userDoc.getString("familyId") ?: error("")

        return familyId
    }

    //Поиск семьи по коду:
    override suspend fun searchFamily(inviteCode: String): FamilyDTO {
        val searchFamilyQuery = firestore.collection("families")
            .whereEqualTo("inviteCode", inviteCode).get().await()

        val familyDoc =
            searchFamilyQuery.documents.firstOrNull() ?: throw Exception("Семья не найдена")

        val family = familyDoc.toObject(FamilyDTO::class.java)?.copy(id = familyDoc.id)
            ?: throw Exception("Ошибка чтения данных о семье из БД")
        if (family.members.size >= MAX_FAMILY_SIZE) throw Exception("В семье уже есть 2 участника")

        return family
    }

    override suspend fun getCurrentFamilyId(): String {
        val userId = auth.currentUser?.uid ?: error("Юзер не залогинен")
        val userDoc = firestore.collection("users").document(userId).get().await()
        val familyId = userDoc.getString("familyId") ?: error("Семья не найдена")

        return familyId
    }

    override suspend fun getCurrentUser(): FirebaseUser {
        val user = auth.currentUser ?: error("Юзер не залогинен")
        return user
    }


}