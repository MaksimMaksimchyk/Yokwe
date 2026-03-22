package com.example.yokwe.data.mappers

import com.example.yokwe.data.dto.GoalDTO
import com.example.yokwe.domain.models.Goal
import com.example.yokwe.domain.models.GoalStatus
import com.google.firebase.Timestamp
import java.util.Date

fun GoalDTO.toDomain(): Goal {
    return when (type) {
        "financial" -> Goal.FinancialGoal(
            id = id,
            familyId = familyId,
            createdBy = createdBy,
            createdAt = createdAt.toDate(),
            status = GoalStatus.valueOf(status),
            title = title,
            targetAmount = targetAmount,
            currentAmount = currentAmount,
            currency = currency
        )

        "habit" -> Goal.DailyHabitGoal(
            id = id,
            familyId = familyId,
            createdBy = createdBy,
            createdAt = createdAt.toDate(),
            status = GoalStatus.valueOf(status),
            title = title,
            completedDates = completedDates.map { Date(it) }
        )

        "one_time" -> Goal.OneTimeGoal(
            id = id,
            familyId = familyId,
            createdBy = createdBy,
            createdAt = createdAt.toDate(),
            status = GoalStatus.valueOf(status),
            title = title,
            isDone = done
        )

        else -> throw IllegalArgumentException("Неизвестный тип цели: $type")
    }
}

fun Goal.toDto(): GoalDTO {

    return when (this) {
        is Goal.FinancialGoal -> GoalDTO(
            id = id,
            familyId = familyId,
            createdBy = createdBy,
            createdAt = Timestamp(createdAt),
            status = status.name,
            type = "financial",
            title = title,
            targetAmount = targetAmount,
            currentAmount = currentAmount,
            currency = currency
        )
        is Goal.DailyHabitGoal -> GoalDTO(
            id = id,
            familyId = familyId,
            createdBy = createdBy,
            createdAt = Timestamp(createdAt),
            status = status.name,
            type = "habit",
            title = title,
            completedDates = completedDates.map { it.time }
        )
        is Goal.OneTimeGoal -> GoalDTO(
            id = id,
            familyId = familyId,
            createdBy = createdBy,
            createdAt = Timestamp(createdAt),
            status = status.name,
            type = "one_time",
            title = title,
            done = isDone
        )
    }
}