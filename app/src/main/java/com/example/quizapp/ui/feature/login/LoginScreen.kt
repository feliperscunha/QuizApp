package com.example.quizapp.ui.feature.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.quizapp.ui.theme.QuizAppTheme
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quizapp.navigation.ListRoute
import com.example.quizapp.navigation.SignupRoute
import com.example.quizapp.ui.UIEvent
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll


@Composable
fun LoginScreen(
    navigateToListScreen: () -> Unit,
    navigateToSignupScreen: () -> Unit
) {
    val viewModel = viewModel<LoginViewModel> {
        LoginViewModel()
    }

    val email = viewModel.email
    val password = viewModel.password
    val loading = viewModel.loading

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UIEvent.ShowSnackBar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                    )
                }
                UIEvent.NavigateBack -> {
                }
                is UIEvent.Navigate<*> -> {
                    when(event.route) {
                        is SignupRoute -> {
                            navigateToSignupScreen()
                        }
                        is ListRoute -> {
                            navigateToListScreen()
                        }
                    }
                }
            }
        }
    }

    LoginContent (
        email,
        password,
        loading,
        snackbarHostState,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun LoginContent(
    email: String,
    password: String,
    loading: Boolean,
    snackbarHostState: SnackbarHostState,
    onEvent: (LoginEvent) -> Unit
) {
    Scaffold (
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Login Page",
                fontSize = 32.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    onEvent(
                        LoginEvent.EmailChanged(it)
                    )
                },
                placeholder = {
                    Text("Email")
                },
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    onEvent(
                        LoginEvent.PasswordChanged(it)
                    )
                },
                placeholder = {
                    Text("Password")
                },
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    onEvent(LoginEvent.Login)
                },
                enabled = !loading
            ) {
                Text(text = "Login")
            }

            TextButton(
                onClick = {
                    onEvent(LoginEvent.NavigateToSignup)
                }
            ) {
                Text(text = "Don't have an account? Sign up")
            }
        }
    }
}

@Preview
@Composable
private fun LoginContentPreview() {
    QuizAppTheme {
        LoginContent (
            email = "teste@gmail",
            password = "123456",
            loading = false,
            snackbarHostState = SnackbarHostState(),
            onEvent = {}
        )
    }
}