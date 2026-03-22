package com.example.yokwe.data.remote


import retrofit2.http.*

interface OpenRouterService {
    @POST("chat/completions")
    @Headers("Content-Type: application/json")
    suspend fun getCompletion(
        @Header("Authorization") apiKey: String,
        @Body request: ChatRequest
    ): ChatResponse
}