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

    @Query("SELECT * FROM questions ORDER BY id ASC")
    fun getAll(): Flow<List<HistoryEntity>>

    @Query("SELECT * FROM questions WHERE id = :id")
    suspend fun getBy (id: Long): HistoryEntity?
}