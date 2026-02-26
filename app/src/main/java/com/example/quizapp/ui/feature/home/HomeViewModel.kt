package com.example.quizapp.ui.feature.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.data.OfflineAwareQuizRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(
    quizRepository: OfflineAwareQuizRepository
) : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    val quizzes = quizRepository.getAll()
        .onEach { quizList ->
            Log.d("HomeViewModel", "Received ${quizList.size} quizzes from repository")
            quizList.forEachIndexed { index, quiz ->
                Log.d("HomeViewModel", "Quiz $index: id=${quiz.id}, title=${quiz.title}, questions=${quiz.questions.size}")
            }
        }
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

            }
        }
    }
}

