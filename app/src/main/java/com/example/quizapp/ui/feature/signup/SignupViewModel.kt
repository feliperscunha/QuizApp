package com.example.quizapp.ui.feature.signup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.data.TodoRepository
import com.example.quizapp.navigation.ListRoute
import com.example.quizapp.navigation.LoginRoute
import com.example.quizapp.ui.UIEvent
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SignupViewModel() : ViewModel() {

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
                _uiEvent.send(UIEvent.Navigate(ListRoute))
            } catch (e: Exception) {
                _uiEvent.send(UIEvent.ShowSnackBar(
                    message = e.message ?: "Something went wrong, please try again."
                ))
            } finally {
                loading = false
            }
        }
    }
}