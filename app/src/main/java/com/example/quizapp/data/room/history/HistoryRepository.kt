package com.example.quizapp.data.room.history

import com.example.quizapp.domain.History
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {

    suspend fun insert(history: History)

    fun getAll(): Flow<List<History>>

    suspend fun getBy(id: String): History?
}