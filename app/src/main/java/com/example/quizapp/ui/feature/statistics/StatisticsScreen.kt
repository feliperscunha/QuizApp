package com.example.quizapp.ui.feature.statistics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quizapp.data.firebase.history.HistoryRepositoryImpl
import com.example.quizapp.ui.UIEvent
import com.google.firebase.database.FirebaseDatabase

@Composable
fun StatisticsScreen(
    navigateBack: () -> Unit
) {
    val repository = HistoryRepositoryImpl(db = FirebaseDatabase.getInstance())
    val viewModel = viewModel<StatisticsViewModel> {
        StatisticsViewModel(historyRepository = repository)
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                UIEvent.NavigateBack -> navigateBack()
                else -> {}
            }
        }
    }

    StatisticsContent(
        statistics = viewModel.statistics,
        onEvent = viewModel::onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsContent(
    statistics: UserStatistics?,
    onEvent: (StatisticsEvent) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Statistics") },
                navigationIcon = {
                    IconButton(onClick = { onEvent(StatisticsEvent.NavigateBack) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (statistics == null) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Loading statistics...")
                }
            } else {
                Text(
                    text = "Your Performance",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                StatisticCard(
                    title = "Total Quizzes Taken",
                    value = statistics.totalQuizzesTaken.toString()
                )

                Spacer(modifier = Modifier.height(16.dp))

                StatisticCard(
                    title = "Average Score",
                    value = String.format("%.1f", statistics.averageScore)
                )

                Spacer(modifier = Modifier.height(16.dp))

                StatisticCard(
                    title = "Best Score",
                    value = statistics.bestScore.toString()
                )

                Spacer(modifier = Modifier.height(16.dp))

                StatisticCard(
                    title = "Average Time",
                    value = String.format("%.1f seconds", statistics.averageTime)
                )

                Spacer(modifier = Modifier.height(16.dp))

                StatisticCard(
                    title = "Total Time Spent",
                    value = String.format("%.1f seconds", statistics.totalTime)
                )
            }
        }
    }
}

@Composable
fun StatisticCard(
    title: String,
    value: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

