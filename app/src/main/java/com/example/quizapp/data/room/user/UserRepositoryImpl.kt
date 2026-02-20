package com.example.quizapp.data.room.user

import com.example.quizapp.domain.Todo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRepositoryImpl(
    private val dao: UserDao
): UserRepository {
    override suspend fun insert(title: String, description: String?, id: Long?) {
        val entity = id?.let {
            dao.getBy(it)?.copy(
                title = title,
                description = description
            )
        } ?: UserEntity (
            title = title,
            description = description,
            isCompleted = false
        )

        dao.insert(entity)
    }

    override suspend fun updateCompleted(id: Long, isCompleted: Boolean) {
        val existingEntity = dao.getBy(id) ?: return
        val updatedEntity = existingEntity.copy(isCompleted = isCompleted)
        dao.insert(updatedEntity)
    }

    override suspend fun delete(id: Long) {
        val existingEntity = dao.getBy(id) ?: return
        dao.delete(existingEntity)
    }

    override fun getAll(): Flow<List<Todo>> {
        return dao.getAll().map { entities ->
            entities.map { entity ->
                Todo(
                    id = entity.id,
                    title = entity.title,
                    description = entity.description,
                    isCompleted = entity.isCompleted
                )
            }
        }
    }

    override suspend fun getBy(id: Long): Todo? {
        return dao.getBy(id)?.let { entity ->
            Todo(
                id = entity.id,
                title = entity.title,
                description = entity.description,
                isCompleted = entity.isCompleted
            )
        }
    }
}