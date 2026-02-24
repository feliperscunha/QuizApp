package com.example.quizapp.ui.feature.statistics

data class UserStatistics(
    val totalQuizzesTaken: Int,
    val averageScore: Double,
    val bestScore: Int,
    val totalTime: Double,
    val averageTime: Double
)

