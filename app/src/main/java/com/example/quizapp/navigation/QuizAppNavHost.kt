package com.example.quizapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.quizapp.ui.feature.addedit.AddEditScreen
import com.example.quizapp.ui.feature.list.ListScreen
import com.example.quizapp.ui.feature.login.LoginScreen
import com.example.quizapp.ui.feature.signup.SignupScreen
import kotlinx.serialization.Serializable

@Serializable
object LoginRoute

@Serializable
object SignupRoute

@Serializable
object ListRoute

@Serializable
data class AddEditRoute(val id: String? = null)

@Composable
fun QuizAppNavHost() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = LoginRoute
    ) {
        composable<LoginRoute> {
            LoginScreen (
                navigateToListScreen = {
                    navController.navigate(ListRoute)
                },
                navigateToSignupScreen = {
                    navController.navigate(SignupRoute)
                }
            )
        }

        composable<SignupRoute> {
            SignupScreen (
                navigateToListScreen = {
                    navController.navigate(ListRoute)
                },
                navigateToLoginScreen = {
                    navController.navigate(LoginRoute)
                }
            )
        }

        composable<ListRoute> {
            ListScreen(
                navigateToAddEditScreen = { id ->
                    navController.navigate(AddEditRoute(id = id))
                },
                navigateToLoginScreen = {
                    navController.navigate(LoginRoute)
                }
            )
        }

        composable<AddEditRoute> { backStackEntry ->
            val addEditRoute = backStackEntry.toRoute<AddEditRoute>()
            AddEditScreen(
                id = addEditRoute.id,
                navigateBack = {
                    navController.popBackStack()
                },
            )
        }
    }
}