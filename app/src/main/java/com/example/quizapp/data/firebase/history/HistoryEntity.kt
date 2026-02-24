package com.example.quizapp.data.firebase.history

import kotlinx.serialization.Serializable

@Serializable
data class HistoryEntity(
    val id: String = "",
    val quizId: String = "",
    val userId: String = "",
    val score: Int = 0,
    val time: Double = 0.0,
    val date: String = ""
)
