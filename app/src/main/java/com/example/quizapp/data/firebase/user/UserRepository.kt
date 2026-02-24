package com.example.quizapp.data.firebase.user

import com.example.quizapp.domain.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun insert(user: User)
    suspend fun getById(userId: String): User?
    fun getByIdFlow(userId: String): Flow<User?>
}

