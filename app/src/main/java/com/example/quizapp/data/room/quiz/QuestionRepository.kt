package com.example.quizapp.data.room.quiz

import com.example.quizapp.domain.Todo
import kotlinx.coroutines.flow.Flow

interface QuestionRepository {

    suspend fun insert (title: String, description: String?, id: Long? = null)

    suspend fun updateCompleted(id: Long, isCompleted: Boolean)

    suspend fun delete (id: Long)

    fun getAll(): Flow<List<Todo>>

    suspend fun getBy (id: Long): Todo?
}