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
            date = history.date
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
}