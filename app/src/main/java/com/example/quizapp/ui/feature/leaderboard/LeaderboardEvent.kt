package com.example.quizapp.ui.feature.leaderboard

sealed interface LeaderboardEvent {
    data object NavigateBack : LeaderboardEvent
}
