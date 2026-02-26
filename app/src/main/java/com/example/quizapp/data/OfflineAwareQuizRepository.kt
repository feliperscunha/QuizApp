package com.example.quizapp.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.example.quizapp.domain.Quiz
import com.example.quizapp.data.firebase.quiz.QuizRepository as FirebaseQuizRepository
import com.example.quizapp.data.room.quiz.QuestionRepository as RoomQuestionRepository
import com.example.quizapp.domain.Question
import com.example.quizapp.domain.QuizQuestion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class OfflineAwareQuizRepository(
    private val context: Context,
    private val firebaseRepository: FirebaseQuizRepository,
    private val roomRepository: RoomQuestionRepository
) {

    fun getAll(): Flow<List<Quiz>> = flow {
        if (isOnline()) {
            // Try to get from Firebase first
            try {
                firebaseRepository.getAll().collect { quizzes ->
                    // Cache to Room
                    saveToRoom(quizzes)
                    emit(quizzes)
                }
            } catch (e: Exception) {
                Log.w("OfflineAwareQuizRepo", "Firebase failed, falling back to Room", e)
                // Fall back to Room
                roomRepository.getAll().collect { questions ->
                    emit(convertQuestionsToQuizzes(questions))
                }
            }
        } else {
            Log.d("OfflineAwareQuizRepo", "Offline mode - loading from Room")
            roomRepository.getAll().collect { questions ->
                emit(convertQuestionsToQuizzes(questions))
            }
        }
    }.catch { e ->
        Log.e("OfflineAwareQuizRepo", "Error getting quizzes", e)
        // Last resort: try Room
        roomRepository.getAll().collect { questions ->
            emit(convertQuestionsToQuizzes(questions))
        }
    }

    private fun convertQuestionsToQuizzes(questions: List<Question>): List<Quiz> {
        return questions.groupBy { it.quizId }
            .map { (quizId, questionList) ->
                val firstQuestion = questionList.firstOrNull()
                Quiz(
                    id = quizId,
                    title = firstQuestion?.title ?: "",
                    subtitle = firstQuestion?.subtitle ?: "",
                    questions = questionList.map { q ->
                        QuizQuestion(
                            question = q.question,
                            options = listOf(q.option1, q.option2, q.option3, q.option4),
                            correct = q.correctAnswer
                        )
                    }
                )
            }
    }

    suspend fun getBy(id: String): Quiz? {
        return if (isOnline()) {
            try {
                firebaseRepository.getBy(id)?.also { quiz ->
                    // Cache to Room
                    saveToRoom(listOf(quiz))
                }
            } catch (e: Exception) {
                Log.w("OfflineAwareQuizRepo", "Firebase failed, loading from Room", e)
                getByFromRoom(id)
            }
        } else {
            Log.d("OfflineAwareQuizRepo", "Offline mode - loading from Room")
            getByFromRoom(id)
        }
    }

    private suspend fun getByFromRoom(quizId: String): Quiz? {
        val questions = roomRepository.getByQuizId(quizId)
        if (questions.isEmpty()) return null

        val firstQuestion = questions.first()
        return Quiz(
            id = quizId,
            title = firstQuestion.title,
            subtitle = firstQuestion.subtitle,
            questions = questions.map { q ->
                QuizQuestion(
                    question = q.question,
                    options = listOf(q.option1, q.option2, q.option3, q.option4),
                    correct = q.correctAnswer
                )
            }
        )
    }

    private suspend fun saveToRoom(quizzes: List<Quiz>) {
        try {
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
                    roomRepository.insert(question)
                }
            }
            Log.d("OfflineAwareQuizRepo", "Cached ${quizzes.size} quizzes to Room")
        } catch (e: Exception) {
            Log.e("OfflineAwareQuizRepo", "Error caching to Room", e)
        }
    }

    private fun isOnline(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}

