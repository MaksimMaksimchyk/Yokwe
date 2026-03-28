package com.example.yokwe.data.repositories

import com.example.yokwe.domain.repositories.AuthRepository
import com.example.yokwe.ui.auth.states.AuthState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override fun observeAuthState(): Flow<AuthState> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val currentUser = firebaseAuth.currentUser

            if (currentUser == null) {
                trySend(AuthState.NotAuthenticated)
            } else {
                launch {
                    val familyId = getFamilyIdWithRetry(currentUser.uid)
                    if (familyId != null) {
                        trySend(AuthState.Authenticated(familyId))
                    } else {
                        auth.signOut()
                        trySend(AuthState.NotAuthenticated)
                    }
                }
            }
        }

        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    private suspend fun getFamilyIdWithRetry(uid: String): String? {
        var retryCount = 0
        while (retryCount < 5) {
            try {
                val userDoc = firestore.collection("users").document(uid).get().await()
                val familyId = userDoc.getString("familyId")
                if (familyId != null) return familyId
            } catch (e: Exception) {
                // Логируем ошибку, если нужно
            }
            delay(500)
            retryCount++
        }
        return null
    }
}