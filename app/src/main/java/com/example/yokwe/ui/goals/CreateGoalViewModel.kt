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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class CreateGoalViewModel @Inject constructor(
    private val goalRepository: GoalRepository,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
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
                val userId = auth.currentUser?.uid ?: error("Юзер не залогинен")
                val userDoc = firestore.collection("users").document(userId).get().await()
                val familyId = userDoc.getString("familyId") ?: error("Семья не найдена")
                val type = _state.value.selectedType ?: error("Тип цели не выбран")
                val title = _state.value.title.takeIf { it.isNotBlank() }
                    ?: error("Не заполнено название цели")

                val goal = when (type) {
                    "financial" -> Goal.FinancialGoal(
                        id = "",
                        familyId = familyId,
                        createdBy = userId,
                        createdAt = Date(),
                        status = GoalStatus.ACTIVE,
                        title = title,
                        targetAmount = _state.value.targetAmount.toDoubleOrNull()
                            ?: error("Неправильно введена сумма"),
                        currentAmount = 0.0,
                        currency = "USD"
                    )

                    "habit" -> Goal.DailyHabitGoal(
                        id = "",
                        familyId = familyId,
                        createdBy = userId,
                        createdAt = Date(),
                        status = GoalStatus.ACTIVE,
                        title = title,
                        completedDates = emptyList()
                    )

                    "one_time" -> Goal.OneTimeGoal(
                        id = "",
                        familyId = familyId,
                        createdBy = userId,
                        createdAt = Date(),
                        status = GoalStatus.ACTIVE,
                        title = title,
                        isDone = false
                    )

                    else -> error("Unknown type")
                }

                goalRepository.addGoal(goal)
                _state.update { it.copy(isLoading = false, isSaved = true) }

            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }
}