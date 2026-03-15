package com.example.yokwe.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {
    private val _state = MutableStateFlow(HomeScreenState())
    val state: StateFlow<HomeScreenState> = _state.asStateFlow()

    fun loadFamilyInfo(familyId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val familyDoc = firestore.collection("families")
                .document(familyId)
                .get()
                .await()

            val members = familyDoc.get("members") as? List<*> ?: emptyList<Any>()
            val inviteCode = familyDoc.getString("inviteCode")

            _state.update {
                it.copy(
                    membersCount = members.size,
                    inviteCode = inviteCode,
                    isLoading = false
                )
            }
        }
    }

}