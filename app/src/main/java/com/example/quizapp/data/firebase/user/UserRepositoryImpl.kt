package com.example.quizapp.data.firebase.user

import com.example.quizapp.domain.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class UserRepositoryImpl(
    private val db: FirebaseDatabase
) : UserRepository {

    override suspend fun insert(user: User) {
        val userEntity = UserEntity(
            id = user.id,
            email = user.email,
            username = user.username
        )
        db.reference.child("users").child(user.id).setValue(userEntity).await()
    }

    override suspend fun getById(userId: String): User? {
        val snapshot = db.reference.child("users").child(userId).get().await()
        return snapshot.getValue(UserEntity::class.java)?.let { entity ->
            User(
                id = entity.id,
                email = entity.email,
                username = entity.username
            )
        }
    }

    override fun getByIdFlow(userId: String): Flow<User?> = callbackFlow {
        val ref = db.reference.child("users").child(userId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val entity = snapshot.getValue(UserEntity::class.java)
                val user = entity?.let {
                    User(
                        id = it.id,
                        email = it.email,
                        username = it.username
                    )
                }
                trySend(user).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }
}

