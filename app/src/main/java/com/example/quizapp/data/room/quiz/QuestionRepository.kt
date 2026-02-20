package com.example.quizapp.data.room.quiz

import com.example.quizapp.domain.Question
import kotlinx.coroutines.flow.Flow

interface QuestionRepository {

    suspend fun insert(question: Question)

    fun getAll(): Flow<List<Question>>

    suspend fun getBy(id: String): Question?
}