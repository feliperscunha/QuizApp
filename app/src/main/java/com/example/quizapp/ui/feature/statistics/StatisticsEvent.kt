package com.example.quizapp.ui.feature.statistics

sealed interface StatisticsEvent {
    data object NavigateBack : StatisticsEvent
}

