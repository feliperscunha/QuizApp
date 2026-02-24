package com.example.quizapp.ui.feature.statistics

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.data.firebase.history.HistoryRepository
import com.example.quizapp.ui.UIEvent
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class StatisticsViewModel(
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    var statistics by mutableStateOf<UserStatistics?>(null)
        private set

    private val _uiEvent = Channel<UIEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        loadStatistics()
    }

    private fun loadStatistics() {
        viewModelScope.launch {
            historyRepository.getAllByUser(auth.currentUser?.uid ?: "")
                .catch {
                    Log.e("StatisticsViewModel", "Error loading statistics", it)
                    emit(emptyList())
                }
                .collect { histories ->
                    if (histories.isEmpty()) {
                        statistics = UserStatistics(
                            totalQuizzesTaken = 0,
                            averageScore = 0.0,
                            bestScore = 0,
                            totalTime = 0.0,
                            averageTime = 0.0
                        )
                    } else {
                        val totalQuizzes = histories.size
                        val averageScore = histories.map { it.score }.average()
                        val bestScore = histories.maxOfOrNull { it.score } ?: 0
                        val totalTime = histories.sumOf { it.time }
                        val averageTime = totalTime / totalQuizzes

                        statistics = UserStatistics(
                            totalQuizzesTaken = totalQuizzes,
                            averageScore = averageScore,
                            bestScore = bestScore,
                            totalTime = totalTime,
                            averageTime = averageTime
                        )
                    }
                }
        }
    }

    fun onEvent(event: StatisticsEvent) {
        when (event) {
            StatisticsEvent.NavigateBack -> {
                viewModelScope.launch {
                    _uiEvent.send(UIEvent.NavigateBack)
                }
            }
        }
    }
}

