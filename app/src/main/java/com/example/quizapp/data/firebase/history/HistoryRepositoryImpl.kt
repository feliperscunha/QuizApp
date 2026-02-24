package com.example.quizapp.data.firebase.history

import com.example.quizapp.domain.History
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.sql.Timestamp

class HistoryRepositoryImpl(
    private val db: FirebaseDatabase
) : HistoryRepository {

    override suspend fun insert(history: History) {
        val userHistoryRef = db.reference.child("history").child(history.userId)

        val historyId = history.id.ifEmpty { userHistoryRef.push().key!! }
        val historyEntity = HistoryEntity(
            id = historyId,
            quizId = history.quizId,
            userId = history.userId,
            score = history.score,
            time = history.time,
            date = history.date.ifEmpty { Timestamp(System.currentTimeMillis()).toString() }
        )

        userHistoryRef.child(historyId).setValue(historyEntity).await()
    }

    override suspend fun delete(id: String, userId: String) {
        db.reference.child("history").child(userId).child(id).removeValue().await()
    }

    override fun getAllByUser(userId: String): Flow<List<History>> {
        val ref = db.reference.child("history").child(userId)
        return callbackFlow {
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val histories = snapshot.children.mapNotNull { it.getValue(HistoryEntity::class.java) }
                    val domainHistories = histories.map { entity ->
                        History(
                            id = entity.id,
                            quizId = entity.quizId,
                            userId = entity.userId,
                            score = entity.score,
                            time = entity.time,
                            date = entity.date
                        )
                    }
                    trySend(domainHistories).isSuccess
                }

                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            }
            ref.addValueEventListener(listener)
            awaitClose { ref.removeEventListener(listener) }
        }
    }

    override fun getAllByQuiz(quizId: String): Flow<List<History>> {
        val ref = db.reference.child("history")
        return callbackFlow {
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val histories = mutableListOf<History>()
                    // Iterate through all users
                    snapshot.children.forEach { userSnapshot ->
                        // Get all history entries for each user
                        userSnapshot.children.mapNotNull {
                            it.getValue(HistoryEntity::class.java)
                        }.filter {
                            it.quizId == quizId
                        }.forEach { entity ->
                            histories.add(History(
                                id = entity.id,
                                quizId = entity.quizId,
                                userId = entity.userId,
                                score = entity.score,
                                time = entity.time,
                                date = entity.date
                            ))
                        }
                    }
                    trySend(histories).isSuccess
                }

                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            }
            ref.addValueEventListener(listener)
            awaitClose { ref.removeEventListener(listener) }
        }
    }

    override suspend fun getBy(id: String, userId: String): History? {
        val snapshot = db.reference.child("history").child(userId).child(id).get().await()
        return snapshot.getValue(HistoryEntity::class.java)?.let { entity ->
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

    override fun getUserQuizHistory(userId: String, quizId: String): Flow<List<History>> {
        val ref = db.reference.child("history").child(userId)
        return callbackFlow {
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val histories = snapshot.children.mapNotNull {
                        it.getValue(HistoryEntity::class.java)
                    }.filter {
                        it.quizId == quizId
                    }.map { entity ->
                        History(
                            id = entity.id,
                            quizId = entity.quizId,
                            userId = entity.userId,
                            score = entity.score,
                            time = entity.time,
                            date = entity.date
                        )
                    }
                    trySend(histories).isSuccess
                }

                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            }
            ref.addValueEventListener(listener)
            awaitClose { ref.removeEventListener(listener) }
        }
    }
}

