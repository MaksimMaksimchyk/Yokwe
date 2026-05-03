package com.example.yokwe.ui.auth.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yokwe.domain.interactors.FamilyInteractor
import com.example.yokwe.ui.auth.intents.SignInIntent
import com.example.yokwe.ui.auth.states.SignInState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(private val familyInteractor: FamilyInteractor) :
    ViewModel() {
    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    fun handleIntent(intent: SignInIntent) {
        when (intent) {
            is SignInIntent.EnterEmail -> _state.update { it.copy(email = intent.email) }
            is SignInIntent.EnterPassword -> _state.update { it.copy(password = intent.password) }
            SignInIntent.Submit -> submit()
        }
    }

    private fun submit() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                familyInteractor.signIn(_state.value.email, _state.value.password)
                _state.update { it.copy(isLoading = false, isSuccess = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message ?: "Ошибка входа") }
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

}