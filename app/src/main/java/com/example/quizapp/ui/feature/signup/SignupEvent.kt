package com.example.quizapp.ui.feature.signup

sealed interface SignupEvent {
    data class EmailChanged(val email: String) : SignupEvent
    data class PasswordChanged(val password: String) : SignupEvent
    data object Signup : SignupEvent
    data object NavigateToLogin : SignupEvent
}