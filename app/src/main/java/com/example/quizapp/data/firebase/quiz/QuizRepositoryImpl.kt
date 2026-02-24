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
                    val quizzes = snapshot.children.mapNotNull { it.getValue(QuizEntity::class.java) }
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
                    trySend(domainQuizzes).isSuccess
                }

                override fun onCancelled(error: DatabaseError) {
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

