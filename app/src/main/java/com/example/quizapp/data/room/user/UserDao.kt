package com.example.quizapp.data.room.user

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert (entity: UserEntity)

    @Delete
    suspend fun delete (entity: UserEntity)

    @Query("SELECT * FROM users ORDER BY id ASC")
    fun getAll(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getBy (id: Long): UserEntity?
}