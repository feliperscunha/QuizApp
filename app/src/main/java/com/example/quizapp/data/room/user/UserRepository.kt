package com.example.quizapp.data.room.user

import com.example.quizapp.domain.Todo
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun insert (title: String, description: String?, id: Long? = null)

    suspend fun updateCompleted(id: Long, isCompleted: Boolean)

    suspend fun delete (id: Long)

    fun getAll(): Flow<List<Todo>>

    suspend fun getBy (id: Long): Todo?
}