package com.example.quizapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.quizapp.ui.feature.history.HistoryScreen
import com.example.quizapp.ui.feature.home.HomeScreen
import com.example.quizapp.ui.feature.leaderboard.LeaderboardScreen
import com.example.quizapp.ui.feature.login.LoginScreen
import com.example.quizapp.ui.feature.quizexecution.QuizExecutionScreen
import com.example.quizapp.ui.feature.signup.SignupScreen
import com.example.quizapp.ui.feature.statistics.StatisticsScreen
import kotlinx.serialization.Serializable

@Serializable
object LoginRoute

@Serializable
object SignupRoute

@Serializable
object HomeRoute

@Serializable
data class QuizExecutionRoute(val quizId: String)

@Serializable
object HistoryRoute

@Serializable
object StatisticsRoute

@Serializable
object LeaderboardRoute

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
                    navController.navigate(HomeRoute) {
                        popUpTo(LoginRoute) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                navigateToSignupScreen = {
                    navController.navigate(SignupRoute)
                }
            )
        }

        composable<SignupRoute> {
            SignupScreen (
                navigateToListScreen = {
                    navController.navigate(HomeRoute) {
                        popUpTo(LoginRoute) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                navigateToLoginScreen = {
                    navController.navigate(LoginRoute)
                }
            )
        }

        composable<HomeRoute> {
            HomeScreen(
                navigateToQuizExecution = { quizId ->
                    navController.navigate(QuizExecutionRoute(quizId = quizId))
                },
                navigateToHistory = {
                    navController.navigate(HistoryRoute)
                },
                navigateToStatistics = {
                    navController.navigate(StatisticsRoute)
                },
                navigateToLeaderboard = {
                    navController.navigate(LeaderboardRoute)
                },
                navigateToLogin = {
                    navController.navigate(LoginRoute) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable<QuizExecutionRoute> { backStackEntry ->
            val quizExecutionRoute = backStackEntry.toRoute<QuizExecutionRoute>()
            QuizExecutionScreen(
                quizId = quizExecutionRoute.quizId,
                navigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<HistoryRoute> {
            HistoryScreen(
                navigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<StatisticsRoute> {
            StatisticsScreen(
                navigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<LeaderboardRoute> {
            LeaderboardScreen(
                navigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}