package com.example.yokwe.ui.pet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yokwe.domain.repositories.PetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PetViewModel @Inject constructor(
    private val petRepository: PetRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PetState())
    val state: StateFlow<PetState> = _state.asStateFlow()

    fun loadPet(familyId: String) {
        petRepository.getPetFlow(familyId)
            .catch { e ->
                _state.update { it.copy(error = e.message, isLoading = false) }
            }
            .onEach { pet ->
                _state.update {
                    it.copy(
                        pet = pet,
                        isLoading = false,
                        error = null
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun addExperience(amount: Int) {
        viewModelScope.launch {
            val familyId = _state.value.pet?.familyId ?: return@launch
            petRepository.addExperience(familyId, amount)
                .onSuccess { updatedPet ->
                    _state.update { it.copy(pet = updatedPet) }
                }
                .onFailure { error ->
                    _state.update { it.copy(error = error.message) }
                }
        }
    }

    fun updateMood(mood: String) {
        viewModelScope.launch {
            val familyId = _state.value.pet?.familyId ?: return@launch
            petRepository.updateMood(familyId, mood)
                .onSuccess { updatedPet ->
                    _state.update { it.copy(pet = updatedPet) }
                }
        }
    }

    fun updateLastMessage(message: String) {
        viewModelScope.launch {
            val familyId = _state.value.pet?.familyId ?: return@launch
            petRepository.updateLastMessage(familyId, message)
        }
    }
}