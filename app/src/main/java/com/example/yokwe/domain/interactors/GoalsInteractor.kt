package com.example.yokwe.domain.interactors

import com.example.yokwe.domain.models.Goal
import com.example.yokwe.domain.models.GoalStatus
import com.example.yokwe.domain.models.PetEvents
import com.example.yokwe.domain.repositories.AiRepository
import com.example.yokwe.domain.repositories.FamilyRepository
import com.example.yokwe.domain.repositories.GoalsRepository
import com.example.yokwe.domain.repositories.PetRepository
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

class GoalsInteractor @Inject constructor(
    private val familyRepository: FamilyRepository,
    private val petRepository: PetRepository,
    private val goalsRepository: GoalsRepository,
    private val aiRepository: AiRepository
) {

    companion object {
        const val HABIT_EXP_GAIN = 10
        const val ONE_TIME_GOAL_EXP_GAIN = 15
        const val COMPLETE_FINANCE_GOAL_EXP_GAIN = 50
        const val ADD_PROGRESS_FINANCE_GOAL_EXP_GAIN = 5
    }

    suspend fun addGoal(type: String, title: String, targetAmount: Double = 0.0) {
        val user = familyRepository.getCurrentUser()
        val familyId = familyRepository.getCurrentFamilyId()

        val goal = when (type) {
            "financial" -> Goal.FinancialGoal(
                id = "",
                familyId = familyId,
                createdBy = user.uid,
                createdAt = Date(),
                status = GoalStatus.ACTIVE,
                title = title,
                targetAmount = targetAmount,
                currentAmount = 0.0,
                currency = "USD"
            )

            "habit" -> Goal.DailyHabitGoal(
                id = "",
                familyId = familyId,
                createdBy = user.uid,
                createdAt = Date(),
                status = GoalStatus.ACTIVE,
                title = title,
                completedDates = emptyList()
            )

            "one_time" -> Goal.OneTimeGoal(
                id = "",
                familyId = familyId,
                createdBy = user.uid,
                createdAt = Date(),
                status = GoalStatus.ACTIVE,
                title = title,
                isDone = false
            )

            else -> error("Unknown type")
        }
        goalsRepository.addGoal(goal)
        petRepository.updateLastEvent(familyId, "${user.email} создал задачу ${title}")
        handlePetReaction(familyId = familyId, event = PetEvents.GOAL_ADDED, goalName = title)
    }

    suspend fun toggleGoal(goal: Goal, isCompleted: Boolean) {
        //ОБновление цели в репозитории
        val updatedGoal = when (goal) {
            is Goal.OneTimeGoal -> {
                val newStatus = if (isCompleted) GoalStatus.COMPLETED else GoalStatus.ACTIVE
                goal.copy(isDone = isCompleted, status = newStatus)
            }

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

            else -> return // финансовые цели обрабатываются отдельно
        }
        goalsRepository.updateGoal(updatedGoal)

        // Добавляем опыт питомцу и генерируем реплику
        if (isCompleted) {
            val familyId = familyRepository.getCurrentFamilyId()
            val user = familyRepository.getCurrentUser()

            val expAmount = when (goal) {
                is Goal.DailyHabitGoal -> HABIT_EXP_GAIN
                is Goal.OneTimeGoal -> ONE_TIME_GOAL_EXP_GAIN
            }
            petRepository.addExperience(familyId, expAmount)

            val event = when (goal) {
                is Goal.DailyHabitGoal -> PetEvents.HABIT_COMPLETED
                is Goal.OneTimeGoal -> PetEvents.TASK_COMPLETED
            }

            petRepository.updateLastEvent(
                familyId = familyId,
                "${user.email} выполнил задачу ${goal.title}"
            )
            handlePetReaction(familyId = familyId, event = event, goalName = goal.title)
        }

    }

    suspend fun addMoney(goal: Goal.FinancialGoal, amount: Double) {
        val user = familyRepository.getCurrentUser()
        val newCurrent = goal.currentAmount + amount
        val wasCompleted = newCurrent >= goal.targetAmount
        val newStatus = if (wasCompleted) GoalStatus.COMPLETED else GoalStatus.ACTIVE
        val updatedGoal = goal.copy(
            currentAmount = newCurrent,
            status = newStatus
        )
        goalsRepository.updateGoal(updatedGoal)

        val expAmount =
            if (wasCompleted) COMPLETE_FINANCE_GOAL_EXP_GAIN else ADD_PROGRESS_FINANCE_GOAL_EXP_GAIN
        val familyId = familyRepository.getCurrentFamilyId()
        petRepository.addExperience(familyId, expAmount)

        if (wasCompleted) {
            petRepository.updateLastEvent(familyId, "${user.email} выполнил задачу ${goal.title}")
        } else {
            petRepository.updateLastEvent(
                familyId,
                "${user.email} добавил $${amount} на цель ${goal.title}"
            )
        }

        val event = if (wasCompleted) PetEvents.FINANCIAL_GOAL_REACHED else PetEvents.ADDED_PROGRESS
        handlePetReaction(familyId = familyId, event = event, goalName = goal.title)
    }

    suspend fun deleteGoal(goalId: String) {
        goalsRepository.deleteGoal(goalId)
    }

    suspend fun getGoalsFlow(): Flow<List<Goal>> {
        val familyId = familyRepository.getCurrentFamilyId()
        return goalsRepository.getGoalsFlow(familyId)
    }

    private fun isSameDay(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance().apply { time = date1 }
        val cal2 = Calendar.getInstance().apply { time = date2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private suspend fun handlePetReaction(familyId: String, event: PetEvents, goalName: String) {
        val level = petRepository.getCurrentPetLevel(familyId)
        val message = aiRepository.generatePetMessage(event, level, goalName)
        petRepository.updateLastMessage(familyId, message)
    }

}