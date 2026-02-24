package com.example.quizapp.data.firebase.quiz

import kotlinx.serialization.Serializable

@Serializable
data class QuizEntity(
    val id: String = "",
    val title: String = "",
    val subtitle: String = "",
    val questionList: List<QuizQuestionEntity> = emptyList()
)

@Serializable
data class QuizQuestionEntity(
    val question: String = "",
    val options: List<String> = emptyList(),
    val correct: String = ""
)

