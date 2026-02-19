package com.example.quizapp.ui.feature.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.navigation.ListRoute
import com.example.quizapp.navigation.SignupRoute
import com.example.quizapp.ui.UIEvent
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginViewModel() : ViewModel() {
    private val auth : FirebaseAuth = FirebaseAuth.getInstance()

    var email by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var loading by mutableStateOf(false)
        private set


    private val _uiEvent = Channel<UIEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        checkAuthStatus()
    }

    fun checkAuthStatus () {
        if (auth.currentUser != null) {
            viewModelScope.launch {
                _uiEvent.send(UIEvent.Navigate(ListRoute))
            }
        }
    }

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged -> {
                email = event.email
            }
            is LoginEvent.PasswordChanged -> {
                password = event.password
            }

            LoginEvent.Login -> {
                login()
            }
            LoginEvent.NavigateToSignup -> {
                navigateToSignup()
            }
        }
    }

    private fun navigateToSignup() {
        viewModelScope.launch {
            _uiEvent.send(UIEvent.Navigate(SignupRoute))
        }
    }

    private fun login () {
        loading = true
        viewModelScope.launch {
            try {
                if (email.isBlank()) {
                    _uiEvent.send(UIEvent.ShowSnackBar(
                        message = "The email can't be empty"
                    ))
                    return@launch
                }
                if (password.isBlank()) {
                    _uiEvent.send(UIEvent.ShowSnackBar(
                        message = "The password can't be empty"
                    ))
                    return@launch
                }
                auth.signInWithEmailAndPassword(email, password).await()
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