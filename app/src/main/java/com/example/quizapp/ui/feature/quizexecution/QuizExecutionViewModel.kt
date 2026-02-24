package com.example.quizapp.ui.feature.quizexecution

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.data.firebase.history.HistoryRepository
import com.example.quizapp.data.firebase.quiz.QuizRepository
import com.example.quizapp.domain.History
import com.example.quizapp.domain.Quiz
import com.example.quizapp.ui.UIEvent
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.sql.Timestamp

class QuizExecutionViewModel(
    private val quizId: String,
    private val quizRepository: QuizRepository,
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    var quiz by mutableStateOf<Quiz?>(null)
        private set

    var currentQuestionIndex by mutableIntStateOf(0)
        private set

    var selectedAnswers by mutableStateOf<Map<Int, String>>(emptyMap())
        private set

    var isQuizCompleted by mutableStateOf(false)
        private set

    var score by mutableIntStateOf(0)
        private set

    private var startTime by mutableLongStateOf(0L)

    private val _uiEvent = Channel<UIEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        loadQuiz()
        startTime = System.currentTimeMillis()
    }

    private fun loadQuiz() {
        viewModelScope.launch {
            try {
                quiz = quizRepository.getBy(quizId)
            } catch (e: Exception) {
                Log.e("QuizExecutionViewModel", "Error loading quiz", e)
                _uiEvent.send(UIEvent.ShowSnackBar("Error loading quiz"))
            }
        }
    }

    fun onEvent(event: QuizExecutionEvent) {
        when (event) {
            is QuizExecutionEvent.SelectAnswer -> {
                selectedAnswers = selectedAnswers + (event.questionIndex to event.answer)
            }
            QuizExecutionEvent.NextQuestion -> {
                if (currentQuestionIndex < (quiz?.questions?.size ?: 0) - 1) {
                    currentQuestionIndex++
                }
            }
            QuizExecutionEvent.SubmitQuiz -> {
                submitQuiz()
            }
            QuizExecutionEvent.NavigateBack -> {
                viewModelScope.launch {
                    _uiEvent.send(UIEvent.NavigateBack)
                }
            }
        }
    }

    private fun submitQuiz() {
        viewModelScope.launch {
            val currentQuiz = quiz ?: return@launch

            // Calculate score
            var correctAnswers = 0
            currentQuiz.questions.forEachIndexed { index, question ->
                if (selectedAnswers[index] == question.correct) {
                    correctAnswers++
                }
            }
            score = correctAnswers

            // Calculate time taken in seconds
            val endTime = System.currentTimeMillis()
            val timeTaken = (endTime - startTime) / 1000.0

            // Save to history
            val userId = auth.currentUser?.uid ?: ""
            val history = History(
                id = "",
                quizId = quizId,
                userId = userId,
                score = score,
                time = timeTaken,
                date = Timestamp(System.currentTimeMillis()).toString()
            )

            try {
                historyRepository.insert(history)
                isQuizCompleted = true
                _uiEvent.send(UIEvent.ShowSnackBar("Quiz completed! Score: $score/${currentQuiz.questions.size}"))
            } catch (e: Exception) {
                Log.e("QuizExecutionViewModel", "Error saving history", e)
                _uiEvent.send(UIEvent.ShowSnackBar("Error saving results"))
            }
        }
    }
}

