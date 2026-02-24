package com.example.quizapp.data.firebase.quiz

import com.example.quizapp.domain.Quiz
import kotlinx.coroutines.flow.Flow

interface QuizRepository {

    suspend fun insert(quiz: Quiz)

    suspend fun delete(id: String)

    fun getAll(): Flow<List<Quiz>>

    suspend fun getBy(id: String): Quiz?
}

