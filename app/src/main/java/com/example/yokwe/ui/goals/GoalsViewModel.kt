package com.example.yokwe.ui.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yokwe.domain.interactors.GoalsInteractor
import com.example.yokwe.domain.models.Goal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GoalsViewModel @Inject constructor(
    private val goalsInteractor: GoalsInteractor
) : ViewModel() {
    private val _state = MutableStateFlow(GoalsState())
    val state = _state.asStateFlow()

    private val _dialogState = MutableStateFlow<DialogState?>(null)
    val dialogState: StateFlow<DialogState?> = _dialogState.asStateFlow()

    private var familyId: String? = null

    init {
        loadGoalsAndObserve()
    }

    private fun loadGoalsAndObserve() {
        _state.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val goalsFlow = goalsInteractor.getGoalsFlow()

                goalsFlow.catch { e ->
                    _state.update { it.copy(error = e.message, isLoading = false) }
                }.collect { goals ->
                    _state.update {
                        it.copy(
                            goals = goals,
                            isLoading = false,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun handleIntent(intent: GoalsIntent) {
        when (intent) {
            is GoalsIntent.ToggleGoal -> toggleGoal(intent.goalId, intent.isCompleted)
            is GoalsIntent.AddProgress -> addMoney(intent.goalId, intent.amount)
            is GoalsIntent.DeleteGoal -> deleteGoal(intent.goalId)
            is GoalsIntent.ShowAddProgressDialog -> showAddProgressDialog(intent.goalId)
            GoalsIntent.HideAddProgressDialog -> hideAddProgressDialog()
        }
    }

    private fun showAddProgressDialog(goalId: String) {
        val goal = _state.value.goals.find { it.id == goalId } as? Goal.FinancialGoal
        if (goal != null) {
            _dialogState.update {
                DialogState(
                    goalId = goalId,
                    goalTitle = goal.title,
                    currentAmount = goal.currentAmount,
                    targetAmount = goal.targetAmount,
                    currency = goal.currency
                )
            }
        }
    }

    private fun hideAddProgressDialog() {
        _dialogState.update { null }
    }

    private fun toggleGoal(goalId: String, isCompleted: Boolean) {
        viewModelScope.launch {
            val goal = _state.value.goals.find { it.id == goalId } ?: return@launch
            goalsInteractor.toggleGoal(goal, isCompleted)
        }
    }

    private fun addMoney(goalId: String, amount: Double) {
        viewModelScope.launch {
            val goal =
                _state.value.goals.find { it.id == goalId } as? Goal.FinancialGoal ?: return@launch
            goalsInteractor.addMoney(goal, amount)
        }
    }

    private fun deleteGoal(goalId: String) {
        viewModelScope.launch {
            goalsInteractor.deleteGoal(goalId)
        }
    }

}