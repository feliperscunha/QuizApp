package com.example.quizapp.data.room.history

import com.example.quizapp.domain.History
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HistoryRepositoryImpl(
    private val dao: HistoryDao
): HistoryRepository {
    override suspend fun insert(history: History) {
        val entity = HistoryEntity(
            id = history.id,
            userId = history.userId,
            quizId = history.quizId,
            score = history.score,
            time = history.time,
            date = history.date,
            syncedToFirebase = false
        )

        dao.insert(entity)
    }

    override fun getAll(): Flow<List<History>> {
        return dao.getAll().map { entities ->
            entities.map { entity ->
                History(
                    id = entity.id,
                    quizId = entity.quizId,
                    userId = entity.userId,
                    score = entity.score,
                    time = entity.time,
                    date = entity.date
                )
            }
        }
    }

    override suspend fun getBy(id: String): History? {
        return dao.getBy(id)?.let { entity ->
            History(
                id = entity.id,
                quizId = entity.quizId,
                userId = entity.userId,
                score = entity.score,
                time = entity.time,
                date = entity.date
            )
        }
    }

    override fun getAllByUser(userId: String): Flow<List<History>> {
        return dao.getAllByUser(userId).map { entities ->
            entities.map { entity ->
                History(
                    id = entity.id,
                    quizId = entity.quizId,
                    userId = entity.userId,
                    score = entity.score,
                    time = entity.time,
                    date = entity.date
                )
            }
        }
    }

    override fun getUserQuizHistory(userId: String, quizId: String): Flow<List<History>> {
        return dao.getUserQuizHistory(userId, quizId).map { entities ->
            entities.map { entity ->
                History(
                    id = entity.id,
                    quizId = entity.quizId,
                    userId = entity.userId,
                    score = entity.score,
                    time = entity.time,
                    date = entity.date
                )
            }
        }
    }

    override suspend fun getUnsyncedEntries(): List<History> {
        return dao.getUnsyncedEntries().map { entity ->
            History(
                id = entity.id,
                quizId = entity.quizId,
                userId = entity.userId,
                score = entity.score,
                time = entity.time,
                date = entity.date
            )
        }
    }

    override suspend fun markAsSynced(id: String) {
        dao.markAsSynced(id)
    }
}