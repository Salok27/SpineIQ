package noshtek.back_pain_prototype.navigation

sealed class Screen(val route: String) {
    object Onboarding      : Screen("onboarding")
    object Profile         : Screen("profile")
    object Home            : Screen("home")
    object Progress        : Screen("progress")
    object Shop            : Screen("shop")
    object Achievements    : Screen("achievements")

    // Nested assessment graph — all 6 wizard screens share one AssessmentSessionViewModel
    object AssessmentGraph : Screen("assessment_graph")
    object Occupation      : Screen("occupation")
    object Lifestyle       : Screen("lifestyle")
    object Pain            : Screen("pain")
    object Functional      : Screen("functional")
    object RedFlag         : Screen("red_flag")
    object Review          : Screen("review")

    object Results         : Screen("results/{assessmentId}?celebrate={celebrate}") {
        /** [celebrate] plays the completion celebration — true only when arriving fresh from the wizard. */
        fun route(assessmentId: String, celebrate: Boolean = false) =
            "results/$assessmentId?celebrate=$celebrate"
    }
    object FullReport      : Screen("full_report/{assessmentId}") {
        fun route(assessmentId: String) = "full_report/$assessmentId"
    }
    object Settings        : Screen("settings")
}
