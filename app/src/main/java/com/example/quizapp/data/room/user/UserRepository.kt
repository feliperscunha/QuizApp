package com.example.quizapp.data.room.user

import com.example.quizapp.domain.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun insert(id: String, email: String)

    suspend fun delete(id: String)

    fun getAll(): Flow<List<User>>

    suspend fun getBy(id: String): User?
}