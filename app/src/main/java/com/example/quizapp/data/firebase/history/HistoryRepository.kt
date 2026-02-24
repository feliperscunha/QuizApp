package com.example.quizapp.data.firebase.history

import com.example.quizapp.domain.History
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {

    suspend fun insert(history: History)

    suspend fun delete(id: String, userId: String)

    fun getAll(): Flow<List<History>>

    fun getAllByUser(userId: String): Flow<List<History>>

    fun getAllByQuiz(quizId: String): Flow<List<History>>

    suspend fun getBy(id: String, userId: String): History?

    fun getUserQuizHistory(userId: String, quizId: String): Flow<List<History>>
}

