package com.example.yokwe.ui.auth.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yokwe.domain.interactors.FamilyInteractor
import com.example.yokwe.ui.auth.intents.CreateFamilyIntent
import com.example.yokwe.ui.auth.states.CreateFamilyState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateFamilyViewModel @Inject constructor(private val familyInteractor: FamilyInteractor) :
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
        }
    }

    fun submit() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                familyInteractor.createFamily(
                    email = _state.value.email,
                    password = _state.value.password
                )
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message ?: "Ошибка при создании семьи") }
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }
}