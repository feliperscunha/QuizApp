package com.example.quizapp.ui.feature.leaderboard

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.data.firebase.history.HistoryRepository
import com.example.quizapp.ui.UIEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class LeaderboardViewModel(
    private val historyRepository: HistoryRepository
) : ViewModel() {

    var leaderboard by mutableStateOf<List<LeaderboardEntry>>(emptyList())
        private set

    private val _uiEvent = Channel<UIEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        loadLeaderboard()
    }

    private fun loadLeaderboard() {
        viewModelScope.launch {
            // Get all histories from Firebase
            // Group by userId and calculate statistics
            try {
                // Since we need all user histories, we'll need to query the Firebase database
                // For now, we'll use a simplified approach
                // In a production app, you'd want to have a separate leaderboard collection

                // This is a placeholder - you'll need to implement a way to get all histories
                // from all users in your Firebase structure
                val entries = listOf<LeaderboardEntry>()
                leaderboard = entries
            } catch (e: Exception) {
                Log.e("LeaderboardViewModel", "Error loading leaderboard", e)
            }
        }
    }

    fun onEvent(event: LeaderboardEvent) {
        when (event) {
            LeaderboardEvent.NavigateBack -> {
                viewModelScope.launch {
                    _uiEvent.send(UIEvent.NavigateBack)
                }
            }
        }
    }
}

