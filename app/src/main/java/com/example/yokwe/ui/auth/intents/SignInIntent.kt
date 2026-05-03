package com.example.yokwe.ui.auth.intents

sealed class SignInIntent {
    data class EnterEmail(val email: String) : SignInIntent()
    data class EnterPassword(val password: String) : SignInIntent()
    object Submit : SignInIntent()
}