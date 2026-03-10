package com.example.yokwe.ui.auth

sealed class CreateFamilyIntent {
    data class EnterEmail(val email: String) : CreateFamilyIntent()
    data class EnterPassword(val password: String) : CreateFamilyIntent()
    object Submit : CreateFamilyIntent()
    object CopyInviteCode : CreateFamilyIntent()
    object Done : CreateFamilyIntent()
}