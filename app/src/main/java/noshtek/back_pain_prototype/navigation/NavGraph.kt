package noshtek.back_pain_prototype.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import noshtek.back_pain_prototype.ui.achievements.AchievementsScreen
import noshtek.back_pain_prototype.ui.assessment.*
import noshtek.back_pain_prototype.ui.common.MotionTokens
import noshtek.back_pain_prototype.ui.home.HomeScreen
import noshtek.back_pain_prototype.ui.onboarding.OnboardingScreen
import noshtek.back_pain_prototype.ui.profile.ProfileScreen
import noshtek.back_pain_prototype.ui.progress.ProgressScreen
import noshtek.back_pain_prototype.ui.results.FullReportScreen
import noshtek.back_pain_prototype.ui.results.ResultsDashboardScreen
import noshtek.back_pain_prototype.ui.settings.SettingsScreen
import noshtek.back_pain_prototype.ui.shop.ShopScreen

// ── Shared transition language ────────────────────────────────────────────────
// Top-level screens: gentle fade + zoom. Wizard steps: horizontal slide that
// reinforces forward/back progression. Hub tabs: fast cross-fade so tab
// switches feel instant. All timed from the motion tokens.
private val medium get() = tween<Float>(MotionTokens.DurationMedium, easing = MotionTokens.Emphasized)
private val fast get() = tween<Float>(MotionTokens.DurationFast, easing = MotionTokens.Emphasized)
private val slide get() = tween<androidx.compose.ui.unit.IntOffset>(MotionTokens.DurationMedium, easing = MotionTokens.Emphasized)

@Composable
fun SpineIQNavGraph() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = currentRoute in BottomNavDestination.routes

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        // Screens keep handling their own status-bar insets; the only inset
        // this Scaffold contributes is the bottom bar height while visible.
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(slide) { it } + fadeIn(medium),
                exit = slideOutVertically(slide) { it } + fadeOut(fast),
            ) {
                SpineIQNavBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Onboarding.route,
            modifier = Modifier.padding(padding),
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

            // Hub tabs cross-fade between each other (no zoom) so switching feels instant.
            composable(Screen.Home.route, enterTransition = { hubTransitionEnter() }) {
                HomeScreen(navController = navController)
            }

            composable(Screen.Progress.route, enterTransition = { hubTransitionEnter() }) {
                ProgressScreen(navController = navController)
            }

            composable(Screen.Shop.route, enterTransition = { hubTransitionEnter() }) {
                ShopScreen(navController = navController)
            }

            composable(Screen.Achievements.route, enterTransition = { hubTransitionEnter() }) {
                AchievementsScreen(navController = navController)
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
                arguments = listOf(
                    navArgument("assessmentId") { type = NavType.StringType },
                    navArgument("celebrate") {
                        type = NavType.BoolType
                        defaultValue = false
                    },
                )
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
}

private fun AnimatedContentTransitionScope<androidx.navigation.NavBackStackEntry>.hubTransitionEnter() =
    if (initialState.destination.route in BottomNavDestination.routes) fadeIn(fast)
    else fadeIn(medium) + scaleIn(initialScale = 0.94f, animationSpec = medium)
