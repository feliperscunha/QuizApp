package com.example.quizapp.domain

data class User(
    val id: String,
    val email: String,
    val username: String = ""
)

val user1 = User("1", "enzo@teste.com", "Enzo")
val user2 = User("2", "felipe@teste.com", "Felipe")
val user3 = User("3", "joao@teste.com", "João")

