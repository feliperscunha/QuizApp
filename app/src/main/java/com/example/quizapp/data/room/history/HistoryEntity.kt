package com.example.quizapp.data.room.history

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Timestamp

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey val id: String = java.util.UUID.randomUUID().toString(),
    val userId: String,
    val quizId: String,
    val score: Int,
    val time: Double,
    val date: String = Timestamp(System.currentTimeMillis()).toString(),
    val syncedToFirebase: Boolean = false
)
