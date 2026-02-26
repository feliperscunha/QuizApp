package com.example.quizapp.ui.feature.leaderboard

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.data.firebase.history.HistoryRepository
import com.example.quizapp.data.firebase.user.UserInfoRepository
import com.example.quizapp.ui.UIEvent
import com.google.firebase.database.FirebaseDatabase
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

    private val userInfoRepository = UserInfoRepository(
        FirebaseDatabase.getInstance("https://quizapp-88330-default-rtdb.firebaseio.com/")
    )

    init {
        loadLeaderboard()
    }

    private fun loadLeaderboard() {
        viewModelScope.launch {
            try {
                historyRepository.getAll().collect { histories ->

                    val userStatsWithoutUsername = histories.groupBy { it.userId }
                        .map { (userId, userHistories) ->
                            val totalScore = userHistories.sumOf { it.score }
                            val quizzesTaken = userHistories.size
                            val averageScore = if (quizzesTaken > 0) {
                                totalScore.toDouble() / quizzesTaken
                            } else {
                                0.0
                            }

                            val totalTime = userHistories.sumOf { it.time }
                            val averageTime = if (quizzesTaken > 0) {
                                totalTime / quizzesTaken
                            } else {
                                0.0
                            }

                            Triple(userId, LeaderboardEntry(
                                userId = userId,
                                username = "", // Será preenchido depois
                                totalScore = totalScore,
                                quizzesTaken = quizzesTaken,
                                averageScore = averageScore,
                                averageTime = averageTime,
                                rank = 0
                            ), userHistories)
                        }


                    val userStatsWithUsername = userStatsWithoutUsername.map { (userId, entry, _) ->
                        try {
                            val userInfo = userInfoRepository.getUserInfo(userId)
                            entry.copy(username = userInfo.username)
                        } catch (e: Exception) {
                            Log.w("LeaderboardViewModel", "Failed to get username for $userId", e)
                            entry.copy(username = "Usuário ${userId.take(4)}")
                        }
                    }

                    val sortedStats = userStatsWithUsername
                        .sortedByDescending { it.totalScore }
                        .mapIndexed { index, entry ->
                            entry.copy(rank = index + 1)
                        }

                    leaderboard = sortedStats
                    Log.d("LeaderboardViewModel", "Loaded ${sortedStats.size} users in leaderboard")
                }
            } catch (e: Exception) {
                Log.e("LeaderboardViewModel", "Error loading leaderboard", e)
                leaderboard = emptyList()
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

