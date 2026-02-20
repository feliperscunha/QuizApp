package com.example.quizapp.data.room.quiz

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert (entity: QuestionEntity)

    @Delete
    suspend fun delete (entity: QuestionEntity)

    @Query("SELECT * FROM questions ORDER BY id ASC")
    fun getAll(): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE id = :id")
    suspend fun getBy (id: Long): QuestionEntity?
}