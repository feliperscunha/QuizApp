package com.example.quizapp.data.firebase.quiz


data class QuizEntity(
    var id: String = "",
    var title: String = "",
    var subtitle: String = "",
    var questionList: List<QuizQuestionEntity> = emptyList()
)

data class QuizQuestionEntity(
    var question: String = "",
    var options: List<String> = emptyList(),
    var correct: String = ""
)

