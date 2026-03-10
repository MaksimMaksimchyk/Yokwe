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

        //Создание питомца
        val newPet = PetDTO(familyId = familyRef.id)
        firestore.collection("pets").document(familyRef.id).set(newPet).await()

        //Добавление юзера в коллекцию
        val newUser = UserDTO(id = newUserId, email = email, familyId = familyRef.id)
        firestore.collection("users").document(newUserId).set(newUser).await()


        return inviteCode
    }
}