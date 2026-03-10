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
class CreateFamilyViewModel @Inject constructor(private val authRepository: AuthRepository) :
    ViewModel() {

    private val _state = MutableStateFlow(CreateFamilyState())
    val state = _state.asStateFlow()

    fun handleIntent(intent: CreateFamilyIntent) {
        when (intent) {
            is CreateFamilyIntent.EnterEmail -> {
                _state.update { it.copy(email = intent.email) }
            }

            is CreateFamilyIntent.EnterPassword -> {
                _state.update { it.copy(password = intent.password) }
            }

            CreateFamilyIntent.Submit -> {
                submit()
            }

            is CreateFamilyIntent.Done -> {}
            CreateFamilyIntent.CopyInviteCode -> {}
        }
    }

    fun submit() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                val inviteCode = authRepository.createFamily(
                    email = _state.value.email,
                    password = _state.value.password
                )
                _state.update { it.copy(inviteCode = inviteCode) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message ?: "Submit user error") }
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }
}