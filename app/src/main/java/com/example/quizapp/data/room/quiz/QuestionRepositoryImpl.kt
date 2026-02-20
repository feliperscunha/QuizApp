package com.example.quizapp.data.room.quiz

import com.example.quizapp.domain.Question
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class QuestionRepositoryImpl(
    private val dao: QuestionDao
): QuestionRepository {
    override suspend fun insert(question: Question) {
        val entity = QuestionEntity(
            id = question.id,
            quizId = question.quizId,
            title = question.title,
            subtitle = question.subtitle,
            question = question.question,
            correctAnswer = question.correctAnswer,
            option1 = question.option1,
            option2 = question.option2,
            option3 = question.option3,
            option4 = question.option4
        )

        dao.insert(entity)
    }

    override fun getAll(): Flow<List<Question>> {
        return dao.getAll().map { entities ->
            entities.map { entity ->
                Question(
                    id = entity.id,
                    quizId = entity.quizId,
                    title = entity.title,
                    subtitle = entity.subtitle,
                    question = entity.question,
                    correctAnswer = entity.correctAnswer,
                    option1 = entity.option1,
                    option2 = entity.option2,
                    option3 = entity.option3,
                    option4 = entity.option4
                )
            }
        }
    }

    override suspend fun getBy(id: String): Question? {
        return dao.getBy(id)?.let { entity ->
            Question(
                id = entity.id,
                quizId = entity.quizId,
                title = entity.title,
                subtitle = entity.subtitle,
                question = entity.question,
                correctAnswer = entity.correctAnswer,
                option1 = entity.option1,
                option2 = entity.option2,
                option3 = entity.option3,
                option4 = entity.option4
            )
        }
    }
}