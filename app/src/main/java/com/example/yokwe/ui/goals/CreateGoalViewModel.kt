package com.example.yokwe.ui.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yokwe.domain.interactors.GoalsInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateGoalViewModel @Inject constructor(
    private val goalsInteractor: GoalsInteractor
) : ViewModel() {

    private val _state = MutableStateFlow(CreateGoalState())
    val state = _state.asStateFlow()

    fun handleIntent(intent: CreateGoalIntent) {
        when (intent) {
            is CreateGoalIntent.SelectFinancial -> selectType("financial")
            is CreateGoalIntent.SelectHabit -> selectType("habit")
            is CreateGoalIntent.SelectOneTime -> selectType("one_time")
            is CreateGoalIntent.EnterTitle -> _state.update { it.copy(title = intent.value) }
            is CreateGoalIntent.EnterTargetAmount -> _state.update { it.copy(targetAmount = intent.value) }
            is CreateGoalIntent.Save -> saveGoal()
            is CreateGoalIntent.NavigateBack -> _state.update { it.copy(isSaved = false) }
        }
    }

    private fun selectType(type: String) {
        _state.update {
            it.copy(
                selectedType = type,
                step = CreateGoalStep.ENTER_DETAILS
            )
        }
    }

    private fun saveGoal() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {

                val type = _state.value.selectedType ?: error("Тип цели не выбран")
                val title = _state.value.title.takeIf { it.isNotBlank() }
                    ?: error("Не заполнено название цели")
                val targetAmount = _state.value.targetAmount.toDoubleOrNull()
                    ?: if (type == "financial") error("Неправильно введена сумма") else 0.0

                goalsInteractor.addGoal(type = type, title = title, targetAmount = targetAmount)

                _state.update { it.copy(isLoading = false, isSaved = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }
}