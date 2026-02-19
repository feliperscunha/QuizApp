package com.example.quizapp.data.firebase

import com.example.quizapp.domain.Todo
import kotlinx.coroutines.flow.Flow

interface TodoRepository {

    suspend fun insert (title: String, description: String?, id: String? = null, userId: String)

    suspend fun updateCompleted(id: String, isCompleted: Boolean, userId: String)

    suspend fun delete (id: String, userId: String)

    fun getAll(userId: String): Flow<List<Todo>>

    suspend fun getBy (id: String, userId: String): Todo?
}