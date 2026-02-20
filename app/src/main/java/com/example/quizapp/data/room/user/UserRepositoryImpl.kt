package com.example.quizapp.data.room.user

import com.example.quizapp.domain.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRepositoryImpl(
    private val dao: UserDao
) : UserRepository {
    override suspend fun insert(id: String, email: String) {
        val entity = dao.getBy(id)?.copy(email = email)
            ?: UserEntity(
                id = id,
                email = email
            )

        dao.insert(entity)
    }

    override suspend fun delete(id: String) {
        val existingEntity = dao.getBy(id) ?: return
        dao.delete(existingEntity)
    }

    override fun getAll(): Flow<List<User>> {
        return dao.getAll().map { entities ->
            entities.map { entity ->
                User(
                    id = entity.id,
                    email = entity.email
                )
            }
        }
    }

    override suspend fun getBy(id: String): User? {
        return dao.getBy(id)?.let { entity ->
            User(
                id = entity.id,
                email = entity.email
            )
        }
    }
}