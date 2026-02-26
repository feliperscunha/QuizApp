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

    var username by mutableStateOf("")
        private set

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
            is SignupEvent.UsernameChanged -> {
                username = event.username
            }
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
                if (username.isBlank()) {
                    _uiEvent.send(UIEvent.ShowSnackBar(
                        message = "O nome de usuário não pode estar vazio"
                    ))
                    return@launch
                }

                if (email.isBlank()) {
                    _uiEvent.send(UIEvent.ShowSnackBar(
                        message = "O email não pode estar vazio"
                    ))
                    return@launch
                }

                if (password.isBlank()) {
                    _uiEvent.send(UIEvent.ShowSnackBar(
                        message = "A senha não pode estar vazia"
                    ))
                    return@launch
                }

                auth.createUserWithEmailAndPassword(email, password).await()

                val userId = auth.currentUser?.uid ?: return@launch
                saveUserToFirebase(userId, email, username)

                syncDataAfterSignup()

                _uiEvent.send(UIEvent.Navigate(HomeRoute))
            } catch (e: Exception) {
                _uiEvent.send(UIEvent.ShowSnackBar(
                    message = e.message ?: "Algo deu errado, tente novamente."
                ))
            } finally {
                loading = false
            }
        }
    }

    private suspend fun saveUserToFirebase(userId: String, email: String, username: String) {
        try {
            val firebaseDb = FirebaseDatabase.getInstance("https://quizapp-88330-default-rtdb.firebaseio.com/")
            val userRepo = com.example.quizapp.data.firebase.user.UserRepositoryImpl(firebaseDb)

            val user = com.example.quizapp.domain.User(
                id = userId,
                email = email,
                username = username
            )

            userRepo.insert(user)
            Log.d("SignupViewModel", "User saved to Firebase: $username")
        } catch (e: Exception) {
            Log.e("SignupViewModel", "Error saving user to Firebase", e)
        }
    }

    private suspend fun syncDataAfterSignup() {
        database ?: run {
            Log.w("SignupViewModel", "Database not provided, skipping sync")
            return
        }

        try {
            val userId = auth.currentUser?.uid ?: return

            val firebaseDb = FirebaseDatabase.getInstance("https://quizapp-88330-default-rtdb.firebaseio.com/")
            val firebaseQuizRepo = QuizRepositoryImpl(firebaseDb)
            val firebaseHistoryRepo = HistoryRepositoryImpl(firebaseDb)

            val roomQuestionRepo = QuestionRepositoryImpl(database.questionDao)
            val roomHistoryRepo = RoomHistoryRepositoryImpl(database.historyDao)
            val roomUserRepo = UserRepositoryImpl(database.userDao)

            val syncRepo = SyncRepository(
                firebaseQuizRepository = firebaseQuizRepo,
                firebaseHistoryRepository = firebaseHistoryRepo,
                roomQuestionRepository = roomQuestionRepo,
                roomHistoryRepository = roomHistoryRepo,
                roomUserRepository = roomUserRepo
            )

            Log.d("SignupViewModel", "Starting data sync...")
            syncRepo.syncQuizzes()
            Log.d("SignupViewModel", "Data sync completed successfully")

        } catch (e: Exception) {
            Log.e("SignupViewModel", "Error syncing data", e)
        }
    }
}

