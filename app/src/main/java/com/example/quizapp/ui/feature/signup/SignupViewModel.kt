package com.example.quizapp.ui.feature.signup

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.data.SyncRepository
import com.example.quizapp.data.firebase.history.HistoryRepositoryImpl
import com.example.quizapp.data.firebase.quiz.QuizRepositoryImpl
import com.example.quizapp.data.room.QuizAppDatabase
import com.example.quizapp.data.room.history.HistoryRepositoryImpl as RoomHistoryRepositoryImpl
import com.example.quizapp.data.room.quiz.QuestionRepositoryImpl
import com.example.quizapp.data.room.user.UserRepositoryImpl
import com.example.quizapp.navigation.HomeRoute
import com.example.quizapp.navigation.LoginRoute
import com.example.quizapp.ui.UIEvent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SignupViewModel(
    private val database: QuizAppDatabase? = null
) : ViewModel() {

    private val auth : FirebaseAuth = FirebaseAuth.getInstance()

    var email by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var loading by mutableStateOf(false)
        private set

    private val _uiEvent = Channel<UIEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: SignupEvent) {
        when (event) {
            is SignupEvent.EmailChanged -> {
                email = event.email
            }
            is SignupEvent.PasswordChanged -> {
                password = event.password
            }

            SignupEvent.Signup -> {
                signup()
            }
            SignupEvent.NavigateToLogin -> {
                navigateToLogin()
            }
        }
    }

    private fun navigateToLogin() {
        viewModelScope.launch {
            _uiEvent.send(UIEvent.Navigate(LoginRoute))
        }
    }

    private fun signup () {
        loading = true
        viewModelScope.launch {
            try {
                if (email.isBlank()) {
                    _uiEvent.send(UIEvent.ShowSnackBar(
                        message = "The email can\'t be empty"
                    ))
                    return@launch
                }

                if (password.isBlank()) {
                    _uiEvent.send(UIEvent.ShowSnackBar(
                        message = "The password can\'t be empty"
                    ))
                    return@launch
                }
                auth.createUserWithEmailAndPassword(email, password).await()

                // Sync data from Firebase to Room for offline access
                syncDataAfterSignup()

                _uiEvent.send(UIEvent.Navigate(HomeRoute))
            } catch (e: Exception) {
                _uiEvent.send(UIEvent.ShowSnackBar(
                    message = e.message ?: "Something went wrong, please try again."
                ))
            } finally {
                loading = false
            }
        }
    }

    private suspend fun syncDataAfterSignup() {
        database ?: run {
            Log.w("SignupViewModel", "Database not provided, skipping sync")
            return
        }

        try {
            val userId = auth.currentUser?.uid ?: return

            // Initialize Firebase repositories
            val firebaseDb = FirebaseDatabase.getInstance()
            val firebaseQuizRepo = QuizRepositoryImpl(firebaseDb)
            val firebaseHistoryRepo = HistoryRepositoryImpl(firebaseDb)

            // Initialize Room repositories
            val roomQuestionRepo = QuestionRepositoryImpl(database.questionDao)
            val roomHistoryRepo = RoomHistoryRepositoryImpl(database.historyDao)
            val roomUserRepo = UserRepositoryImpl(database.userDao)

            // Create sync repository
            val syncRepo = SyncRepository(
                firebaseQuizRepository = firebaseQuizRepo,
                firebaseHistoryRepository = firebaseHistoryRepo,
                roomQuestionRepository = roomQuestionRepo,
                roomHistoryRepository = roomHistoryRepo,
                roomUserRepository = roomUserRepo
            )

            // Sync quizzes (new users won't have history yet)
            Log.d("SignupViewModel", "Starting data sync...")
            syncRepo.syncQuizzes()
            Log.d("SignupViewModel", "Data sync completed successfully")

        } catch (e: Exception) {
            Log.e("SignupViewModel", "Error syncing data", e)
        }
    }
}

