package com.example.yokwe.data.repositories

import com.example.yokwe.data.dto.FamilyDTO
import com.example.yokwe.data.dto.PetDTO
import com.example.yokwe.data.dto.UserDTO
import com.example.yokwe.domain.repositories.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    companion object {
        const val MAX_FAMILY_SIZE = 2
    }

    override suspend fun createFamily(
        email: String,
        password: String
    ): String {

        //Создание нового пользователя
        val authResult = auth.createUserWithEmailAndPassword(email, password).await()
        val newUserId = authResult.user?.uid ?: ("Error")

        //Генерация кода приглашения
        val inviteCode = (1..6).map { "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".random() }.joinToString("")

        //Создание семьи
        val familyRef = firestore.collection("families").document()
        val newFamily =
            FamilyDTO(id = familyRef.id, members = listOf(newUserId), inviteCode = inviteCode)
        familyRef.set(newFamily).await()

        //Добавление юзера в коллекцию
        val newUser = UserDTO(id = newUserId, email = email, familyId = familyRef.id)
        firestore.collection("users").document(newUserId).set(newUser).await()


        //Создание питомца
        val newPet = PetDTO(familyId = familyRef.id)
        firestore.collection("pets").document(familyRef.id).set(newPet).await()

        return inviteCode
    }

    override suspend fun joinToFamily(
        email: String,
        password: String,
        inviteCode: String
    ): String {
        //Создание нового пользователя
        val authResult = auth.createUserWithEmailAndPassword(email, password).await()
        val newUserId = authResult.user?.uid ?: ("Error")

        //Поиск семьи по коду:
        val searchFamilyQuery = firestore.collection("families")
            .whereEqualTo("inviteCode", inviteCode).get().await()

        val familyDoc =
            searchFamilyQuery.documents.firstOrNull() ?: throw Exception("Семья не найдена")

        val family = familyDoc.toObject(FamilyDTO::class.java)?.copy(id = familyDoc.id)
            ?: throw Exception("Ошибка чтения данных о семье из БД")
        if (family.members.size >= MAX_FAMILY_SIZE) throw Exception("В семье уже есть 2 участника")

        //Добавление юзера в коллекцию
        val newUser = UserDTO(id = newUserId, email = email, familyId = family.id)
        firestore.collection("users").document(newUserId).set(newUser).await()

        //Добавление юзера в семью
        val updatedMembers = family.members + newUserId
        firestore.collection("families").document(family.id).update("members", updatedMembers)
            .await()

        return family.id
    }

    override suspend fun signIn(email: String, password: String): String {
        val authResult = auth.signInWithEmailAndPassword(email, password).await()
        val userId = authResult.user?.uid ?: ("Error")
        val userDoc = firestore.collection("users").document(userId).get().await()
        val familyId = userDoc.getString("familyId") ?: error("")

        return familyId
    }
}