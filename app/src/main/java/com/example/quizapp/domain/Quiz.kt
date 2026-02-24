package com.example.quizapp.domain

data class Quiz(
    val id: String,
    val title: String,
    val subtitle: String,
    val questions: List<QuizQuestion>
)

data class QuizQuestion(
    val question: String,
    val options: List<String>,
    val correct: String
)

