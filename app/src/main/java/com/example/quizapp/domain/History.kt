package com.example.quizapp.domain

import java.sql.Timestamp

data class History(
    val id: String,
    val quizId: String,
    val userId: String,
    val score: Int,
    val time: Double,
    val date: String = Timestamp(System.currentTimeMillis()).toString(),
)

// fake objects

val History1 = History("1", "1", "1", 4, 30.0, "2024-06-01")
val History2 = History("2", "2", "2", 3, 20.0, "2024-06-02")
val History3 = History("3", "3", "3", 5, 33.0, "2024-06-03")
