package com.example.quizapp.data

import android.util.Log
import com.example.quizapp.data.firebase.history.HistoryRepository as FirebaseHistoryRepository
import com.example.quizapp.data.firebase.quiz.QuizRepository as FirebaseQuizRepository
import com.example.quizapp.data.room.history.HistoryRepository as RoomHistoryRepository
import com.example.quizapp.data.room.quiz.QuestionRepository as RoomQuestionRepository
import com.example.quizapp.data.room.user.UserRepository as RoomUserRepository
import com.example.quizapp.domain.Question
import kotlinx.coroutines.flow.first

class SyncRepository(
    private val firebaseQuizRepository: FirebaseQuizRepository,
    private val firebaseHistoryRepository: FirebaseHistoryRepository,
    private val roomQuestionRepository: RoomQuestionRepository,
    private val roomHistoryRepository: RoomHistoryRepository,
    private val roomUserRepository: RoomUserRepository
) {

    suspend fun syncQuizzes() {
        try {
            val quizzes = firebaseQuizRepository.getAll().first()

            quizzes.forEach { quiz ->
                quiz.questions.forEachIndexed { index, quizQuestion ->
                    val question = Question(
                        id = "${quiz.id}_q${index}",
                        quizId = quiz.id,
                        title = quiz.title,
                        subtitle = quiz.subtitle,
                        question = quizQuestion.question,
                        correctAnswer = quizQuestion.correct,
                        option1 = quizQuestion.options.getOrNull(0) ?: "",
                        option2 = quizQuestion.options.getOrNull(1) ?: "",
                        option3 = quizQuestion.options.getOrNull(2) ?: "",
                        option4 = quizQuestion.options.getOrNull(3) ?: ""
                    )
                    roomQuestionRepository.insert(question)
                }
            }

            Log.d("SyncRepository", "Quizzes synced successfully")
        } catch (e: Exception) {
            Log.e("SyncRepository", "Error syncing quizzes", e)
        }
    }

    suspend fun syncUserHistory(userId: String) {
        try {
            val histories = firebaseHistoryRepository.getAllByUser(userId).first()

            histories.forEach { history ->
                roomHistoryRepository.insert(history)
            }

            Log.d("SyncRepository", "History synced successfully")
        } catch (e: Exception) {
            Log.e("SyncRepository", "Error syncing history", e)
        }
    }
}

