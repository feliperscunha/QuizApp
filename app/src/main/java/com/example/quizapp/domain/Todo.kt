package com.example.quizapp.domain

data class Todo(
    val id: String,
    val title: String,
    val description: String?,
    val isCompleted: Boolean,
    val userId: String = ""
)

// fake objects

val todo1 = Todo("1", "Todo 1", "Description 1", false)
val todo2 = Todo("2", "Todo 2", "Description 2", true)
val todo3 = Todo("3", "Todo 3", "Description 3", false)