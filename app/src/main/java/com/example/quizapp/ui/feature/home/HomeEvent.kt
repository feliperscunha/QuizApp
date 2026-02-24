package com.example.quizapp.ui.feature.home

sealed interface HomeEvent {
    data class SelectQuiz(val quizId: String) : HomeEvent
    data object NavigateToHistory : HomeEvent
    data object NavigateToStatistics : HomeEvent
    data object NavigateToLeaderboard : HomeEvent
    data object Logout : HomeEvent
}
