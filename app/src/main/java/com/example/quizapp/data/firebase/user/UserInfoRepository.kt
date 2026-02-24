package com.example.quizapp.data.firebase.user

import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

data class UserInfo(
    val userId: String,
    val username: String = "Usuário",
    val email: String = "usuario@app.com"
)

class UserInfoRepository(
    private val db: FirebaseDatabase
) {
    /**
     * Busca o username do usuário do Firebase Database
     * Se não encontrar, retorna username genérico
     */
    suspend fun getUserInfo(userId: String): UserInfo {
        return try {
            val snapshot = db.reference.child("users").child(userId).get().await()
            val entity = snapshot.getValue(UserEntity::class.java)

            if (entity != null) {
                UserInfo(
                    userId = userId,
                    username = entity.username.ifEmpty { "Usuário ${userId.take(4)}" },
                    email = entity.email
                )
            } else {
                UserInfo(userId, "Usuário ${userId.take(4)}", "usuario@app.com")
            }
        } catch (e: Exception) {
            UserInfo(userId, "Usuário ${userId.take(4)}", "usuario@app.com")
        }
    }
}

