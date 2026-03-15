package com.example.yokwe.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _state = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState = _state.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val authStatListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            _state.value = AuthState.NotAuthenticated
        } else {
            viewModelScope.launch {
                try {
                    var retryCount = 0
                    var userDoc =
                        firestore.collection("users").document(currentUser.uid).get().await()

                    while (!userDoc.exists() && retryCount < 5) {
                        delay(200)
                        userDoc =
                            firestore.collection("users").document(currentUser.uid).get().await()
                        retryCount++
                    }

                    val familyId = userDoc.getString("familyId")
                    if (familyId != null) {
                        _state.value = AuthState.Authenticated(familyId)
                    } else {
                        auth.signOut()
                        _state.value = AuthState.NotAuthenticated
                    }
                } catch (e: Exception) {
                    _state.value = AuthState.NotAuthenticated
                    _error.value = "${e.message}"
                }
            }
        }
    }

    init {
        auth.addAuthStateListener(authStatListener)
    }

    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(authStatListener)
    }

    fun clearError() {
        _error.value = null
    }

}