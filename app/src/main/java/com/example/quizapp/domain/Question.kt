package com.example.quizapp.domain

data class Question(
    val id: String,
    val quizId: String,
    val title: String,
    val subtitle: String,
    val question: String,
    val correctAnswer: String,
    val option1: String,
    val option2: String,
    val option3: String,
    val option4: String,
)

// fake objects

val question1 = Question("1", "1", "title1", "subtitle1","Question 1", "Correct Answer 1", "Option 1", "Option 2", "Option 3", "Option 4")
val question2 = Question("2", "1", "title2", "subtitle2", "Question 2", "Correct Answer 2", "Option 1", "Option 2", "Option 3", "Option 4")
val question3 = Question("3", "1", "title3", "subtitle3", "Question 3", "Correct Answer 3", "Option 1", "Option 2", "Option 3", "Option 4")