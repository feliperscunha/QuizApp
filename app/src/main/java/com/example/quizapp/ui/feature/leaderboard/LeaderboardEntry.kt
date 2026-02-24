package com.example.quizapp.ui.feature.leaderboard

data class LeaderboardEntry(
    val userId: String,
    val username: String,
    val totalScore: Int,
    val quizzesTaken: Int,
    val averageScore: Double,
    val averageTime: Double, // Tempo médio em segundos
    val rank: Int
)

