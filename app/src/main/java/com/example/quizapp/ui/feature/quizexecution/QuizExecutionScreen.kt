package com.example.quizapp.ui.feature.quizexecution

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quizapp.data.firebase.history.HistoryRepositoryImpl
import com.example.quizapp.data.firebase.quiz.QuizRepositoryImpl
import com.example.quizapp.ui.UIEvent
import com.google.firebase.database.FirebaseDatabase

@Composable
fun QuizExecutionScreen(
    quizId: String,
    navigateBack: () -> Unit
) {
    val db = FirebaseDatabase.getInstance("https://quizapp-88330-default-rtdb.firebaseio.com/")
    val quizRepository = QuizRepositoryImpl(db = db)
    val historyRepository = HistoryRepositoryImpl(db = db)

    val viewModel = viewModel<QuizExecutionViewModel> {
        QuizExecutionViewModel(
            quizId = quizId,
            quizRepository = quizRepository,
            historyRepository = historyRepository
        )
    }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UIEvent.ShowSnackBar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                UIEvent.NavigateBack -> {
                    navigateBack()
                }
                else -> {}
            }
        }
    }

    QuizExecutionContent(
        quiz = viewModel.quiz,
        currentQuestionIndex = viewModel.currentQuestionIndex,
        selectedAnswers = viewModel.selectedAnswers,
        isQuizCompleted = viewModel.isQuizCompleted,
        score = viewModel.score,
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizExecutionContent(
    quiz: com.example.quizapp.domain.Quiz?,
    currentQuestionIndex: Int,
    selectedAnswers: Map<Int, String>,
    isQuizCompleted: Boolean,
    score: Int,
    snackbarHostState: SnackbarHostState,
    onEvent: (QuizExecutionEvent) -> Unit
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(quiz?.title ?: "Quiz") },
                navigationIcon = {
                    IconButton(onClick = { onEvent(QuizExecutionEvent.NavigateBack) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (quiz == null) {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Loading quiz...")
            }
        } else if (isQuizCompleted) {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Quiz Completed!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Your Score: $score/${quiz.questions.size}",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = { onEvent(QuizExecutionEvent.NavigateBack) }) {
                    Text("Back to Home")
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Progress indicator
                LinearProgressIndicator(
                    progress = { (currentQuestionIndex + 1).toFloat() / quiz.questions.size },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Question ${currentQuestionIndex + 1} of ${quiz.questions.size}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(24.dp))

                val currentQuestion = quiz.questions[currentQuestionIndex]

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = currentQuestion.question,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        currentQuestion.options.forEach { option ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = selectedAnswers[currentQuestionIndex] == option,
                                        onClick = {
                                            onEvent(
                                                QuizExecutionEvent.SelectAnswer(
                                                    currentQuestionIndex,
                                                    option
                                                )
                                            )
                                        }
                                    )
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedAnswers[currentQuestionIndex] == option,
                                    onClick = {
                                        onEvent(
                                            QuizExecutionEvent.SelectAnswer(
                                                currentQuestionIndex,
                                                option
                                            )
                                        )
                                    }
                                )
                                Text(
                                    text = option,
                                    modifier = Modifier.padding(start = 8.dp),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (currentQuestionIndex < quiz.questions.size - 1) {
                        Button(
                            onClick = { onEvent(QuizExecutionEvent.NextQuestion) },
                            enabled = selectedAnswers.containsKey(currentQuestionIndex),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Next Question")
                        }
                    } else {
                        Button(
                            onClick = { onEvent(QuizExecutionEvent.SubmitQuiz) },
                            enabled = selectedAnswers.size == quiz.questions.size,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Submit Quiz")
                        }
                    }
                }
            }
        }
    }
}

