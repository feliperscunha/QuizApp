package com.example.quizapp.data.room.history

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert (entity: HistoryEntity)

    @Query("SELECT * FROM history ORDER BY id ASC")
    fun getAll(): Flow<List<HistoryEntity>>

    @Query("SELECT * FROM history WHERE id = :id")
    suspend fun getBy (id: String): HistoryEntity?

    @Query("SELECT * FROM history WHERE userId = :userId ORDER BY id ASC")
    fun getAllByUser(userId: String): Flow<List<HistoryEntity>>

    @Query("SELECT * FROM history WHERE userId = :userId AND quizId = :quizId ORDER BY id ASC")
    fun getUserQuizHistory(userId: String, quizId: String): Flow<List<HistoryEntity>>

    @Query("SELECT * FROM history WHERE syncedToFirebase = 0")
    suspend fun getUnsyncedEntries(): List<HistoryEntity>

    @Query("UPDATE history SET syncedToFirebase = 1 WHERE id = :id")
    suspend fun markAsSynced(id: String)
}