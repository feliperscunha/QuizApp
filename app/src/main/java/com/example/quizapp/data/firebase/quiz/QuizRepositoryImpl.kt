package com.example.quizapp.data.firebase.quiz

import com.example.quizapp.domain.Quiz
import com.example.quizapp.domain.QuizQuestion
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class QuizRepositoryImpl(
    private val db: FirebaseDatabase
) : QuizRepository {

    override suspend fun insert(quiz: Quiz) {
        val quizzesRef = db.reference.child("quizzes")
        val quizEntity = QuizEntity(
            id = quiz.id,
            title = quiz.title,
            subtitle = quiz.subtitle,
            questionList = quiz.questions.map { q ->
                QuizQuestionEntity(
                    question = q.question,
                    options = q.options,
                    correct = q.correct
                )
            }
        )
        quizzesRef.child(quiz.id).setValue(quizEntity).await()
    }

    override suspend fun delete(id: String) {
        db.reference.child("quizzes").child(id).removeValue().await()
    }

    override fun getAll(): Flow<List<Quiz>> {
        val ref = db.reference.child("quizzes")
        return callbackFlow {
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    android.util.Log.d("QuizRepository", "=== Firebase Data Debug ===")
                    android.util.Log.d("QuizRepository", "Snapshot exists: ${snapshot.exists()}")
                    android.util.Log.d("QuizRepository", "Children count: ${snapshot.childrenCount}")
                    android.util.Log.d("QuizRepository", "Raw value: ${snapshot.value}")

                    snapshot.children.forEachIndexed { index, childSnapshot ->
                        android.util.Log.d("QuizRepository", "Child $index key: ${childSnapshot.key}")
                        android.util.Log.d("QuizRepository", "Child $index value: ${childSnapshot.value}")
                    }

                    val quizzes = snapshot.children.mapNotNull {
                        try {
                            val entity = it.getValue(QuizEntity::class.java)
                            android.util.Log.d("QuizRepository", "Parsed entity: $entity")
                            entity
                        } catch (e: Exception) {
                            android.util.Log.e("QuizRepository", "Error parsing quiz entity from ${it.key}", e)
                            null
                        }
                    }

                    android.util.Log.d("QuizRepository", "Total quizzes parsed: ${quizzes.size}")

                    val domainQuizzes = quizzes.map { entity ->
                        Quiz(
                            id = entity.id,
                            title = entity.title,
                            subtitle = entity.subtitle,
                            questions = entity.questionList.map { q ->
                                QuizQuestion(
                                    question = q.question,
                                    options = q.options,
                                    correct = q.correct
                                )
                            }
                        )
                    }

                    android.util.Log.d("QuizRepository", "Sending ${domainQuizzes.size} quizzes to UI")
                    trySend(domainQuizzes).isSuccess
                }

                override fun onCancelled(error: DatabaseError) {
                    android.util.Log.e("QuizRepository", "Firebase error: ${error.message}", error.toException())
                    close(error.toException())
                }
            }
            ref.addValueEventListener(listener)
            awaitClose { ref.removeEventListener(listener) }
        }
    }

    override suspend fun getBy(id: String): Quiz? {
        val snapshot = db.reference.child("quizzes").child(id).get().await()
        return snapshot.getValue(QuizEntity::class.java)?.let { entity ->
            Quiz(
                id = entity.id,
                title = entity.title,
                subtitle = entity.subtitle,
                questions = entity.questionList.map { q ->
                    QuizQuestion(
                        question = q.question,
                        options = q.options,
                        correct = q.correct
                    )
                }
            )
        }
    }
}

