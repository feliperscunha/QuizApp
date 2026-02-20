package com.example.quizapp.data.firebase

import com.example.quizapp.domain.Todo
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class TodoRepositoryImpl(
    private val db: FirebaseDatabase
) : TodoRepository {
    override suspend fun insert(title: String, description: String?, id: String?, userId: String) {
        val userTodosRef = db.reference.child("todos").child(userId)
        if (id != null) {
            val updates = mapOf<String, Any?>(
                "title" to title,
                "description" to description
            )
            userTodosRef.child(id).updateChildren(updates).await()
        } else {
            // It's a new item
            val todoId = userTodosRef.push().key!!
            val todo = TodoEntity(todoId, title, description, false, userId)
            userTodosRef.child(todoId).setValue(todo).await()
        }
    }

    override suspend fun updateCompleted(id: String, isCompleted: Boolean, userId: String) {
        db.reference.child("todos").child(userId).child(id).child("isCompleted").setValue(isCompleted).await()
    }

    override suspend fun delete(id: String, userId: String) {
        db.reference.child("todos").child(userId).child(id).removeValue().await()
    }

    override fun getAll(userId: String): Flow<List<Todo>> {
        val ref = db.reference.child("todos").child(userId)
        return callbackFlow {
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val todos = snapshot.children.mapNotNull { it.getValue(TodoEntity::class.java) }
                    val domainTodos = todos.map { entity ->
                        Todo(
                            id = entity.id,
                            title = entity.title,
                            description = entity.description,
                            isCompleted = entity.isCompleted,
                            userId = entity.userId
                        )
                    }
                    trySend(domainTodos).isSuccess
                }

                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            }
            ref.addValueEventListener(listener)
            awaitClose { ref.removeEventListener(listener) }
        }
    }

    override suspend fun getBy(id: String, userId: String): Todo? {
        val snapshot = db.reference.child("todos").child(userId).child(id).get().await()
        return snapshot.getValue(TodoEntity::class.java)?.let {
            Todo(
                id = it.id,
                title = it.title,
                description = it.description,
                isCompleted = it.isCompleted,
                userId = it.userId
            )
        }
    }
}
