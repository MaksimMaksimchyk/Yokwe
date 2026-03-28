package com.example.yokwe.data.repositories

import android.util.Log
import com.example.yokwe.BuildConfig
import com.example.yokwe.data.ai.ChatRequest
import com.example.yokwe.data.ai.Message
import com.example.yokwe.data.ai.OpenRouterService
import com.example.yokwe.domain.models.PetEvents
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
        Log.d("AiRepository", "API Key loaded: ${BuildConfig.OPENROUTER_API_KEY.take(5)}...")
    }
    companion object {
        private const val TIMEOUT_MS = 10000L // 10 секунд таймаут
    }

    override suspend fun generatePetMessage(
        event: PetEvents,
        petLevel: Int,
        goalName: String?
    ): String = withContext(Dispatchers.IO) {
        Log.d("AiRepository", "Starting generatePetMessage for event: $event")

        val result = withTimeoutOrNull(TIMEOUT_MS) {
            try {
                Log.d("AiRepository", "Making API call...")

                val systemPrompt = getSystemPrompt(petLevel)
                val userPrompt = getUserPrompt(event, goalName)

                Log.d("AiRepository", "System prompt: $systemPrompt")
                Log.d("AiRepository", "User prompt: $userPrompt")

                val request = ChatRequest(
                    messages = listOf(
                        Message("system", systemPrompt),
                        Message("user", userPrompt)
                    ),
                    maxTokens = 200
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

        result ?: getFallbackMessage()
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
                Ты подросший питомец. Ты поддерживаешь своих хозяев и даёшь простые советы.
                Твой тон дружелюбный и заботливый. Иногда можешь подшутить.
                Отвечай коротко (2-4 предложения). Используй смайлики.
            """.trimIndent()

            else -> """
                Ты мудрый питомец, прошедший с хозяевами долгий путь.
                Ты даёшь глубокие советы, говоришь о важном.
                Твои слова полны любви и поддержки. Отвечай не очень длинно (3-5 предложений).
            """.trimIndent()
        }
    }

    private fun getUserPrompt(
        event: PetEvents,
        goalName: String?
    ): String {
        return when (event) {
            PetEvents.TASK_COMPLETED -> {
                "Мои хозяева только что выполнили задачу ${goalName}! Что скажешь?"
            }

            PetEvents.FINANCIAL_GOAL_REACHED -> {
                "Мои хозяева достигли цели ${goalName}! Поздравь их!"
            }

            PetEvents.ADDED_PROGRESS -> {
                "Хозяева добавили прогресс в финансовую цель ${goalName}. Подбодри их!"
            }

            PetEvents.HABIT_COMPLETED -> {
                "Хозяева выполнили привычку ${goalName}. Похвали их!"
            }

            PetEvents.GOAL_ADDED -> {
                "Пользователь добавил задачу ${goalName}. Что ты можешь ему посоветовать? "
            }
        }
    }

    private fun getFallbackMessage(): String {
        return "Zzz..."
    }
}