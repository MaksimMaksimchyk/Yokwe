package com.example.yokwe.data.repositories

import android.util.Log
import com.example.yokwe.BuildConfig
import com.example.yokwe.data.remote.ChatRequest
import com.example.yokwe.data.remote.Message
import com.example.yokwe.data.remote.OpenRouterService
import com.example.yokwe.domain.models.PetEvent
import com.example.yokwe.domain.repositories.AiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.json.Json
import javax.inject.Inject

class AiRepositoryImpl @Inject constructor(
    private val openRouterService: OpenRouterService
) : AiRepository {

    init {
        Log.d("AiRepository", "API Key loaded: ${BuildConfig.OPENROUTER_API_KEY.take(10)}...")
    }

    companion object {
        private const val TIMEOUT_MS = 40000L // 40 секунд таймаут
    }

    override suspend fun generatePetMessage(
        event: PetEvent,
        petLevel: Int,
        taskName: String?,
        goalName: String?,
        newLevel: Int?
    ): String = withContext(Dispatchers.IO) {
        Log.d("AiRepository", "Starting generatePetMessage for event: $event")

        val result = withTimeoutOrNull(TIMEOUT_MS) {
            try {
                Log.d("AiRepository", "Making API call...")

                val systemPrompt = getSystemPrompt(petLevel)
                val userPrompt = getUserPrompt(event, taskName, goalName, newLevel)

                Log.d("AiRepository", "System prompt: $systemPrompt")
                Log.d("AiRepository", "User prompt: $userPrompt")

                val request = ChatRequest(
                    messages = listOf(
                        Message("system", systemPrompt),
                        Message("user", userPrompt)
                    ),
                    maxTokens = 100
                )
                Log.d(
                    "AiRepository",
                    "Request body: ${Json.encodeToString(ChatRequest.serializer(), request)}"
                )

                val response = openRouterService.getCompletion(
                    apiKey = "Bearer ${BuildConfig.OPENROUTER_API_KEY}",
                    request = request
                )

                response.choices.firstOrNull()?.message?.content?.trim()
            } catch (e: Exception) {
                Log.e("AiRepository", "Error", e)
                null
            }
        }

        result ?: getFallbackMessage(event, taskName, goalName, newLevel)
    }

    private fun getSystemPrompt(level: Int): String {
        return when (level) {
            in 1..3 -> """
                Ты маленький игривый питомец. 
                Ты только учишься понимать людей. 
                Говори просто, используй смайлики. 
                Радуйся даже маленьким успехам.
                Отвечай коротко (1-2 предложения).
            """.trimIndent()

            in 4..6 -> """
                Ты подросший питомец Yokwe. Ты поддерживаешь своих хозяев и даёшь простые советы.
                Твой тон дружелюбный и заботливый. Иногда можешь подшутить.
                Отвечай коротко (2-4 предложения). Используй смайлики.
            """.trimIndent()

            else -> """
                Ты мудрый питомец Yokwe, прошедший с хозяевами долгий путь.
                Ты даёшь глубокие советы, говоришь о важном.
                Твои слова полны любви и поддержки. Отвечай не очень длинно (3-5 предложений).
            """.trimIndent()
        }
    }

    private fun getUserPrompt(
        event: PetEvent,
        taskName: String?,
        goalName: String?,
        newLevel: Int?
    ): String {
        return when (event) {
            PetEvent.TASK_COMPLETED -> {
                "Мои хозяева только что выполнили задачу${if (taskName != null) " '$taskName'" else ""}! Что скажешь?"
            }

            PetEvent.TASK_MISSED -> {
                "Мои хозяева забыли выполнить задачу${if (taskName != null) " '$taskName'" else ""}. Как мягко напомнить им?"
            }

            PetEvent.GOAL_REACHED -> {
                "Мои хозяева достигли цели${if (goalName != null) " '$goalName'" else ""}! Поздравь их!"
            }

            PetEvent.LEVEL_UP -> {
                "Я только что повысил уровень до $newLevel! Что сказать хозяевам?"
            }

            PetEvent.ADDED_PROGRESS -> {
                "Хозяева добавили прогресс в финансовую цель${if (goalName != null) " '$goalName'" else ""}. Подбодри их!"
            }

            PetEvent.HABIT_COMPLETED -> {
                "Хозяева выполнили привычку${if (taskName != null) " '$taskName'" else ""}. Похвали их!"
            }
        }
    }

    private fun getFallbackMessage(
        event: PetEvent,
        taskName: String?,
        goalName: String?,
        newLevel: Int?
    ): String {
        val fallbacks = when (event) {
            PetEvent.TASK_COMPLETED -> listOf(
                "Ура! Задача выполнена! Так держать!",
                "Отлично! Я горжусь тобой!",
                "Ещё одна задача готова! Ты молодец!"
            )

            PetEvent.TASK_MISSED -> listOf(
                "Не забывай про задачи! Мы верим в тебя!",
                "Ничего страшного, в следующий раз обязательно получится!",
                "Давай, ты сможешь! Начни с маленького шага!"
            )

            PetEvent.GOAL_REACHED -> listOf(
                "Поздравляю с достижением цели! Это победа!",
                "Цель достигнута! Вы невероятные!",
                "Мечты сбываются! Продолжайте в том же духе!"
            )

            PetEvent.LEVEL_UP -> listOf(
                "Ура! Я вырос до $newLevel уровня! Спасибо вам!",
                "Йеее! Новый уровень! Продолжайте в том же духе!",
                "Я становлюсь сильнее вместе с вами!"
            )

            PetEvent.ADDED_PROGRESS -> listOf(
                "Ещё немного и цель будет достигнута!",
                "Отлично! Вы приближаетесь к цели!",
                "Каждый шаг приближает к мечте!"
            )

            PetEvent.HABIT_COMPLETED -> listOf(
                "Отличная привычка! Так держать!",
                "Ещё один день продуктивности!",
                "Ты становишься лучше каждый день!"
            )
        }
        return fallbacks.random()
    }
}