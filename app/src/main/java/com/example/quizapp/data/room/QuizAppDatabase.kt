package com.example.quizapp.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.quizapp.data.room.history.HistoryDao
import com.example.quizapp.data.room.history.HistoryEntity
import com.example.quizapp.data.room.quiz.QuestionDao
import com.example.quizapp.data.room.quiz.QuestionEntity
import com.example.quizapp.data.room.user.UserDao
import com.example.quizapp.data.room.user.UserEntity

@Database(
    entities = [UserEntity::class, QuestionEntity::class, HistoryEntity::class],
    version = 2
)
abstract class QuizAppDatabase : RoomDatabase() {

    abstract val questionDao: QuestionDao
    abstract val userDao: UserDao
    abstract val historyDao: HistoryDao
}

object UserDatabaseProvider {

    @Volatile
    private var INSTANCE: QuizAppDatabase? = null

    fun provide(context: Context): QuizAppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                QuizAppDatabase::class.java,
                "quiz-app"
            ).build()
            INSTANCE = instance
            instance
        }
    }
}