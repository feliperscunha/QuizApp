package com.example.quizapp.data.room.history

import com.example.quizapp.domain.History
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {

    suspend fun insert(history: History)

    fun getAll(): Flow<List<History>>

    suspend fun getBy(id: String): History?

    fun getAllByUser(userId: String): Flow<List<History>>

    fun getUserQuizHistory(userId: String, quizId: String): Flow<List<History>>

    suspend fun getUnsyncedEntries(): List<History>

    suspend fun markAsSynced(id: String)
}