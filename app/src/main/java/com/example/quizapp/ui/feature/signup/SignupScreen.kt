package com.example.quizapp.ui.feature.signup

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
import com.example.quizapp.data.room.UserDatabaseProvider
import com.example.quizapp.navigation.HomeRoute
import com.example.quizapp.navigation.LoginRoute
import com.example.quizapp.ui.UIEvent


@Composable
fun SignupScreen (
    navigateToListScreen: () -> Unit,
    navigateToLoginScreen: () -> Unit
) {
    val context = LocalContext.current
    val database = UserDatabaseProvider.provide(context)

    val viewModel = viewModel<SignupViewModel> {
        SignupViewModel(database)
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
                        is LoginRoute -> {
                            navigateToLoginScreen()
                        }
                        is HomeRoute -> {
                            navigateToListScreen()
                        }
                    }
                }
            }
        }
    }

    SignupContent (
        email,
        password,
        loading,
        snackbarHostState,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun SignupContent(
    email: String,
    password: String,
    loading: Boolean,
    snackbarHostState: SnackbarHostState,
    onEvent: (SignupEvent) -> Unit
) {
    Scaffold (
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ){ paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Signup Page",
                fontSize = 32.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    onEvent(
                        SignupEvent.EmailChanged(it)
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
                        SignupEvent.PasswordChanged(it)
                    )
                },
                placeholder = {
                    Text("Password")
                },
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    onEvent(SignupEvent.Signup)
                },
                enabled = !loading
            ) {
                Text(text = "Register")
            }

            TextButton(
                onClick = {
                    onEvent(SignupEvent.NavigateToLogin)
                }
            ) {
                Text(text = "Already have an account? Login")
            }
        }
    }
}

@Preview
@Composable
private fun SignupContentPreview() {
    QuizAppTheme {
        SignupContent (
            email = "teste@gmail",
            password = "123456",
            loading = false,
            snackbarHostState = SnackbarHostState(),
            onEvent = {}
        )
    }
}