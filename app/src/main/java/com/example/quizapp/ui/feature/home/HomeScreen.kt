package com.example.quizapp.ui.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quizapp.data.firebase.quiz.QuizRepositoryImpl
import com.example.quizapp.domain.Quiz
import com.google.firebase.database.FirebaseDatabase

@Composable
fun HomeScreen(
    navigateToQuizExecution: (quizId: String) -> Unit,
    navigateToHistory: () -> Unit,
    navigateToStatistics: () -> Unit,
    navigateToLeaderboard: () -> Unit,
    navigateToLogin: () -> Unit
) {
    val repository = QuizRepositoryImpl(db = FirebaseDatabase.getInstance())
    val viewModel = viewModel<HomeViewModel> {
        HomeViewModel(quizRepository = repository)
    }

    val quizzes by viewModel.quizzes.collectAsState()

    HomeContent(
        quizzes = quizzes,
        onEvent = { event ->
            when (event) {
                is HomeEvent.SelectQuiz -> navigateToQuizExecution(event.quizId)
                HomeEvent.NavigateToHistory -> navigateToHistory()
                HomeEvent.NavigateToStatistics -> navigateToStatistics()
                HomeEvent.NavigateToLeaderboard -> navigateToLeaderboard()
                HomeEvent.Logout -> {
                    viewModel.onEvent(event)
                    navigateToLogin()
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    quizzes: List<Quiz>,
    onEvent: (HomeEvent) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quiz App") },
                actions = {
                    IconButton(onClick = { onEvent(HomeEvent.NavigateToHistory) }) {
                        Icon(Icons.AutoMirrored.Filled.List, contentDescription = "History")
                    }
                    IconButton(onClick = { onEvent(HomeEvent.NavigateToStatistics) }) {
                        Icon(Icons.Default.Person, contentDescription = "Statistics")
                    }
                    IconButton(onClick = { onEvent(HomeEvent.NavigateToLeaderboard) }) {
                        Icon(Icons.Default.Star, contentDescription = "Leaderboard")
                    }
                    IconButton(onClick = { onEvent(HomeEvent.Logout) }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout")
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
            Text(
                text = "Available Quizzes",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (quizzes.isEmpty()) {
                Text(
                    text = "No quizzes available",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(quizzes) { quiz ->
                        QuizCard(
                            quiz = quiz,
                            onClick = { onEvent(HomeEvent.SelectQuiz(quiz.id)) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizCard(
    quiz: Quiz,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = quiz.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = quiz.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${quiz.questions.size} questions",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

