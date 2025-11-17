package com.runanywhere.startup_hackathon20.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.runanywhere.startup_hackathon20.ui.screens.*
import com.runanywhere.startup_hackathon20.viewmodel.*

/**
 * Navigation routes
 */
sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object CaseFiles : Screen("case_files")
    object CaseDetail : Screen("case_detail/{caseId}") {
        fun createRoute(caseId: Int) = "case_detail/$caseId"
    }

    object Quiz : Screen("quiz/{caseId}") {
        fun createRoute(caseId: Int) = "quiz/$caseId"
    }

    object CaseSolved : Screen("case_solved/{caseId}") {
        fun createRoute(caseId: Int) = "case_solved/$caseId"
    }

    object AIChief : Screen("ai_chief")
    object DailyMystery : Screen("daily_mystery")
    object Profile : Screen("profile")
}

/**
 * Main navigation graph
 */
@Composable
fun DetectiveNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Splash.route
) {
    // Shared ViewModels
    val gameViewModel: GameViewModel = viewModel()
    val aiViewModel: AIViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                gameViewModel = gameViewModel,
                onNavigateToCaseFiles = {
                    navController.navigate(Screen.CaseFiles.route)
                },
                onNavigateToDailyMystery = {
                    navController.navigate(Screen.DailyMystery.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                onNavigateToAIChief = {
                    navController.navigate(Screen.AIChief.route)
                }
            )
        }

        composable(Screen.CaseFiles.route) {
            CaseFilesScreen(
                gameViewModel = gameViewModel,
                onCaseSelected = { caseId ->
                    navController.navigate(Screen.Quiz.createRoute(caseId))
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.Quiz.route,
            arguments = listOf(
                navArgument("caseId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val caseId = backStackEntry.arguments?.getInt("caseId") ?: 0
            QuizScreen(
                caseId = caseId,
                gameViewModel = gameViewModel,
                aiViewModel = aiViewModel,
                onCaseCompleted = {
                    navController.navigate(Screen.CaseSolved.createRoute(caseId)) {
                        popUpTo(Screen.Quiz.route) { inclusive = true }
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.CaseSolved.route,
            arguments = listOf(
                navArgument("caseId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val caseId = backStackEntry.arguments?.getInt("caseId") ?: 0
            CaseSolvedScreen(
                caseId = caseId,
                gameViewModel = gameViewModel,
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToNextCase = { nextCaseId ->
                    navController.navigate(Screen.Quiz.createRoute(nextCaseId)) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
        }

        composable(Screen.AIChief.route) {
            AIChiefScreen(
                aiViewModel = aiViewModel,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.DailyMystery.route) {
            DailyMysteryScreen(
                gameViewModel = gameViewModel,
                aiViewModel = aiViewModel,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                gameViewModel = gameViewModel,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
