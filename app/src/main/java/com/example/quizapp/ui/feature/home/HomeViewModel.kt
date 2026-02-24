package com.example.quizapp.ui.feature.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.data.firebase.quiz.QuizRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(
    quizRepository: QuizRepository
) : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    val quizzes = quizRepository.getAll()
        .catch {
            Log.e("HomeViewModel", "Error getting quizzes", it)
            emit(emptyList())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onEvent(event: HomeEvent) {
        when (event) {
            HomeEvent.Logout -> {
                auth.signOut()
            }
            else -> {
                // Navigation events handled in the screen
            }
        }
    }
}

