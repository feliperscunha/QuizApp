package com.example.quizapp.ui.feature.history

sealed interface HistoryEvent {
    data object NavigateBack : HistoryEvent
}

