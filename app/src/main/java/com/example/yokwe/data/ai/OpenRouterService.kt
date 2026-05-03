package com.example.yokwe.data.ai


import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface OpenRouterService {
    @POST("chat/completions")
    @Headers("Content-Type: application/json")
    suspend fun getCompletion(
        @Header("Authorization") apiKey: String,
        @Body request: ChatRequest
    ): ChatResponse
}