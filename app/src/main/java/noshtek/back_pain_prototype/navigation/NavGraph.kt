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
import noshtek.back_pain_prototype.ui.patient.*
import noshtek.back_pain_prototype.ui.results.FullReportScreen
import noshtek.back_pain_prototype.ui.results.ResultsDashboardScreen
import noshtek.back_pain_prototype.ui.settings.SettingsScreen

@Composable
fun SpineIQNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Home.route) {

        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }

        composable(Screen.PatientList.route) {
            PatientListScreen(navController = navController)
        }

        composable(
            route = Screen.PatientInfo.route,
            arguments = listOf(navArgument("patientId") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { entry ->
            PatientInformationScreen(
                navController = navController,
                patientId = entry.arguments?.getString("patientId")
            )
        }

        composable(
            route = Screen.PatientHistory.route,
            arguments = listOf(navArgument("patientId") { type = NavType.StringType })
        ) { entry ->
            PatientHistoryScreen(
                navController = navController,
                patientId = checkNotNull(entry.arguments?.getString("patientId"))
            )
        }

        // Assessment wizard — all 6 screens share one ViewModel scoped to this graph entry
        navigation(
            startDestination = Screen.Occupation.route,
            route = Screen.AssessmentGraph.route
        ) {
            composable(
                route = Screen.Occupation.route,
                arguments = listOf(navArgument("patientId") { type = NavType.StringType })
            ) { entry ->
                val parentEntry = remember(entry) {
                    navController.getBackStackEntry(Screen.AssessmentGraph.route)
                }
                val vm: AssessmentSessionViewModel = hiltViewModel(parentEntry)
                OccupationScreen(
                    navController = navController,
                    viewModel = vm,
                    patientId = checkNotNull(entry.arguments?.getString("patientId"))
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
