package com.example.yokwe.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yokwe.domain.repositories.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JoinToFamilyViewModel @Inject constructor(private val authRepository: AuthRepository) :
    ViewModel() {

    private val _state = MutableStateFlow(JoinToFamilyState())
    val state = _state.asStateFlow()

    fun handleIntent(intent: JoinToFamilyIntent) {
        when (intent) {
            is JoinToFamilyIntent.EnterEmail -> _state.update { it.copy(email = intent.email) }
            is JoinToFamilyIntent.EnterInviteCode -> _state.update { it.copy(inviteCode = intent.inviteCode.uppercase()) }
            is JoinToFamilyIntent.EnterPassword -> _state.update { it.copy(password = intent.password) }
            is JoinToFamilyIntent.NavigateToMain -> _state.update { it.copy(isSuccess = false) }
            is JoinToFamilyIntent.Submit -> submit()
        }
    }

    private fun submit() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                val familyId = authRepository.joinToFamily(
                    email = _state.value.email,
                    password = _state.value.password,
                    inviteCode = _state.value.inviteCode
                )
                _state.update {
                    it.copy(isLoading = false, isSuccess = true, familyId = familyId)
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(isLoading = false, error = e.message ?: "Ошибка присоединения к семье")
                }
            } finally {
                _state.update {
                    it.copy(isLoading = false)
                }
            }
        }
    }

}