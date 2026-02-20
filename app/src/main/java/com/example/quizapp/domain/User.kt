package com.example.quizapp.domain

data class User(
    val id: String,
    val email: String,
)

// fake objects

val user1 = User("1", "enzo@teste.com")
val user2 = User("2", "felipe@teste.com")
val user3 = User("3", "joao@teste.com")