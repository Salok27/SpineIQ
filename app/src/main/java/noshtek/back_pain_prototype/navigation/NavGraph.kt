package noshtek.back_pain_prototype.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import noshtek.back_pain_prototype.ui.common.MotionTokens
import noshtek.back_pain_prototype.ui.home.HomeScreen
import noshtek.back_pain_prototype.ui.onboarding.OnboardingScreen
import noshtek.back_pain_prototype.ui.profile.ProfileScreen
import noshtek.back_pain_prototype.ui.progress.ProgressScreen
import noshtek.back_pain_prototype.ui.results.FullReportScreen
import noshtek.back_pain_prototype.ui.results.ResultsDashboardScreen
import noshtek.back_pain_prototype.ui.settings.SettingsScreen

// ── Shared transition language ────────────────────────────────────────────────
// Top-level screens: gentle fade + zoom. Wizard steps: horizontal slide that
// reinforces forward/back progression. All timed from the motion tokens.
private val medium get() = tween<Float>(MotionTokens.DurationMedium, easing = MotionTokens.Emphasized)
private val fast get() = tween<Float>(MotionTokens.DurationFast, easing = MotionTokens.Emphasized)
private val slide get() = tween<androidx.compose.ui.unit.IntOffset>(MotionTokens.DurationMedium, easing = MotionTokens.Emphasized)

@Composable
fun SpineIQNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Onboarding.route,
        enterTransition = { fadeIn(medium) + scaleIn(initialScale = 0.94f, animationSpec = medium) },
        exitTransition = { fadeOut(fast) },
        popEnterTransition = { fadeIn(medium) },
        popExitTransition = { fadeOut(fast) + scaleOut(targetScale = 0.96f, animationSpec = medium) },
    ) {

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

        // Assessment wizard — all 6 screens share one ViewModel scoped to this graph
        // entry, and slide horizontally between steps.
        navigation(
            startDestination = Screen.Occupation.route,
            route = Screen.AssessmentGraph.route,
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, slide) + fadeIn(medium)
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, slide) + fadeOut(fast)
            },
            popEnterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, slide) + fadeIn(medium)
            },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, slide) + fadeOut(fast)
            },
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
