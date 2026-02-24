package com.example.quizapp.ui.feature.quizexecution

sealed interface QuizExecutionEvent {
    data class SelectAnswer(val questionIndex: Int, val answer: String) : QuizExecutionEvent
    data object NextQuestion : QuizExecutionEvent
    data object SubmitQuiz : QuizExecutionEvent
    data object NavigateBack : QuizExecutionEvent
}

