package com.example.yokwe.ui.auth

sealed class JoinToFamilyIntent {
    data class EnterEmail(val email: String) : JoinToFamilyIntent()
    data class EnterPassword(val password: String) : JoinToFamilyIntent()
    data class EnterInviteCode(val inviteCode: String) : JoinToFamilyIntent()
    object Submit : JoinToFamilyIntent()
    object NavigateToMain : JoinToFamilyIntent()
}