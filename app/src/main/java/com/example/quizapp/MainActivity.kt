package com.example.quizapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.ui.Modifier
import com.example.quizapp.navigation.QuizAppNavHost
import com.example.quizapp.ui.theme.QuizAppTheme
import com.google.firebase.database.FirebaseDatabase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable Firebase offline persistence
        try {
            FirebaseDatabase.getInstance("https://quizapp-88330-default-rtdb.firebaseio.com/")
                .setPersistenceEnabled(true)
            Log.d("MainActivity", "Firebase offline persistence enabled")
        } catch (e: Exception) {
            Log.w("MainActivity", "Firebase persistence already enabled or failed", e)
        }

        enableEdgeToEdge()
        setContent {
            Box (
                modifier = Modifier
                    .safeDrawingPadding()
            ) {
                QuizAppTheme {
                    QuizAppNavHost()
                }
            }
        }
    }
}