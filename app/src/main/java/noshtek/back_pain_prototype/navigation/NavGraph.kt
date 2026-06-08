package noshtek.back_pain_prototype.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import noshtek.back_pain_prototype.ui.assessment.*
import noshtek.back_pain_prototype.ui.home.HomeScreen
import noshtek.back_pain_prototype.ui.onboarding.OnboardingScreen
import noshtek.back_pain_prototype.ui.profile.ProfileScreen
import noshtek.back_pain_prototype.ui.progress.ProgressScreen
import noshtek.back_pain_prototype.ui.results.FullReportScreen
import noshtek.back_pain_prototype.ui.results.ResultsDashboardScreen
import noshtek.back_pain_prototype.ui.settings.SettingsScreen

@Composable
fun SpineIQNavGraph() {
    val navController = rememberNavController()

    // Onboarding checks for an existing profile and routes to Home automatically if one exists.
    NavHost(navController = navController, startDestination = Screen.Onboarding.route) {

        composable(Screen.Onboarding.route) {
            OnboardingScreen(navController = navController)
        }

        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }

        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }

        composable(Screen.Progress.route) {
            ProgressScreen(navController = navController)
        }

        // Assessment wizard — all 6 screens share one ViewModel scoped to this graph entry
        navigation(
            startDestination = Screen.Occupation.route,
            route = Screen.AssessmentGraph.route
        ) {
            composable(Screen.Occupation.route) { entry ->
                val parentEntry = remember(entry) {
                    navController.getBackStackEntry(Screen.AssessmentGraph.route)
                }
                OccupationScreen(
                    navController = navController,
                    viewModel = hiltViewModel(parentEntry)
                )
            }
            composable(Screen.Lifestyle.route) { entry ->
                val parentEntry = remember(entry) {
                    navController.getBackStackEntry(Screen.AssessmentGraph.route)
                }
                LifestyleScreen(navController = navController, viewModel = hiltViewModel(parentEntry))
            }
            composable(Screen.Pain.route) { entry ->
                val parentEntry = remember(entry) {
                    navController.getBackStackEntry(Screen.AssessmentGraph.route)
                }
                PainScreen(navController = navController, viewModel = hiltViewModel(parentEntry))
            }
            composable(Screen.Functional.route) { entry ->
                val parentEntry = remember(entry) {
                    navController.getBackStackEntry(Screen.AssessmentGraph.route)
                }
                FunctionalScreen(navController = navController, viewModel = hiltViewModel(parentEntry))
            }
            composable(Screen.RedFlag.route) { entry ->
                val parentEntry = remember(entry) {
                    navController.getBackStackEntry(Screen.AssessmentGraph.route)
                }
                RedFlagScreen(navController = navController, viewModel = hiltViewModel(parentEntry))
            }
            composable(Screen.Review.route) { entry ->
                val parentEntry = remember(entry) {
                    navController.getBackStackEntry(Screen.AssessmentGraph.route)
                }
                ReviewScreen(navController = navController, viewModel = hiltViewModel(parentEntry))
            }
        }

        composable(
            route = Screen.Results.route,
            arguments = listOf(navArgument("assessmentId") { type = NavType.StringType })
        ) {
            ResultsDashboardScreen(navController = navController)
        }

        composable(
            route = Screen.FullReport.route,
            arguments = listOf(navArgument("assessmentId") { type = NavType.StringType })
        ) {
            FullReportScreen(navController = navController)
        }

        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }
    }
}
