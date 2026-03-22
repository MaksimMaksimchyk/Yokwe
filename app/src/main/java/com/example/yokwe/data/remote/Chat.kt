package com.example.yokwe.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatRequest(
    val model: String = "deepseek/deepseek-chat-v3-0324", //либо openrouter/free если начнут требовать деньги
    val messages: List<Message>,
    @SerialName("max_tokens")
    val maxTokens: Int = 100,
    val temperature: Double = 0.7 //на всякий случай
)

@Serializable
data class Message(
    val role: String, // "system", "user", "assistant"
    val content: String? = null
)

@Serializable
data class ChatResponse(
    val choices: List<Choice>
)

@Serializable
data class Choice(
    val message: Message
)