package com.example.quizapp.ui.feature.leaderboard

data class LeaderboardEntry(
    val userId: String,
    val totalScore: Int,
    val quizzesTaken: Int,
    val averageScore: Double,
    val rank: Int
)

