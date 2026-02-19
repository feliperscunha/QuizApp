package com.example.quizapp.data.firebase

import kotlinx.serialization.Serializable

@Serializable
data class TodoEntity(
    val id: String = "",
    val title: String = "",
    val description: String? = null,
    val isCompleted: Boolean = false,
    val userId: String = ""
)
