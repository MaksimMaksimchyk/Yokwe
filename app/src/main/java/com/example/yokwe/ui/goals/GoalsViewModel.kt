package com.example.yokwe.ui.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yokwe.domain.models.Goal
import com.example.yokwe.domain.models.GoalStatus
import com.example.yokwe.domain.repositories.GoalRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class GoalsViewModel @Inject constructor(
    private val goalRepository: GoalRepository,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {
    private val _state = MutableStateFlow(GoalsState())
    val state = _state.asStateFlow()

    private var familyId: String? = null

    init {
        loadFamilyIdAndObserve()
    }

    private fun loadFamilyIdAndObserve() {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                val userDoc = firestore.collection("users").document(userId).get().await()
                familyId = userDoc.getString("familyId")

                familyId?.let { id ->
                    goalRepository.observeGoals(id)
                        .catch { e ->
                            _state.update { it.copy(error = e.message, isLoading = false) }
                        }
                        .onEach { goals ->
                            _state.update {
                                it.copy(
                                    goals = goals,
                                    isLoading = false,
                                    error = null
                                )
                            }
                        }
                        .launchIn(viewModelScope)
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }


    fun handleIntent(intent: GoalsIntent) {
        when (intent) {
            is GoalsIntent.ToggleGoal -> toggleGoal(intent.goalId, intent.isCompleted)
            is GoalsIntent.AddProgress -> addProgress(intent.goalId, intent.amount)
            is GoalsIntent.DeleteGoal -> deleteGoal(intent.goalId)
        }
    }

    private fun toggleGoal(goalId: String, isCompleted: Boolean) {
        viewModelScope.launch {
            val goal = _state.value.goals.find { it.id == goalId } ?: return@launch
            val updatedGoal = when (goal) {
                is Goal.OneTimeGoal -> goal.copy(
                    isDone = isCompleted,
                    status = if (isCompleted) GoalStatus.COMPLETED else GoalStatus.ACTIVE
                )

                is Goal.DailyHabitGoal -> {
                    val today = Date()
                    val newDates = if (isCompleted) {
                        // добавляем, если сегодня ещё не отмечено
                        if (goal.completedDates.none { isSameDay(it, today) }) {
                            goal.completedDates + today
                        } else {
                            goal.completedDates
                        }
                    } else {
                        // удаляем сегодняшнюю отметку
                        goal.completedDates.filter { !isSameDay(it, today) }
                    }
                    goal.copy(completedDates = newDates)
                }

                else -> return@launch // финансовые цели обрабатываются отдельно
            }
            goalRepository.updateGoal(updatedGoal)
            _state.update { state ->
                state.copy(goals = state.goals.map { if (it.id == goalId) updatedGoal else it })
            }
        }
    }

    private fun addProgress(goalId: String, amount: Double) {
        viewModelScope.launch {
            val goal =
                _state.value.goals.find { it.id == goalId } as? Goal.FinancialGoal ?: return@launch
            val newCurrent = goal.currentAmount + amount
            val updatedGoal = goal.copy(
                currentAmount = newCurrent,
                status = if (newCurrent >= goal.targetAmount) GoalStatus.COMPLETED else GoalStatus.ACTIVE
            )
            goalRepository.updateGoal(updatedGoal)
            _state.update { state ->
                state.copy(goals = state.goals.map { if (it.id == goalId) updatedGoal else it })
            }
        }
    }

    private fun deleteGoal(goalId: String) {
        viewModelScope.launch {
            goalRepository.deleteGoal(goalId)
        }
    }

    private fun isSameDay(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance().apply { time = date1 }
        val cal2 = Calendar.getInstance().apply { time = date2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
}