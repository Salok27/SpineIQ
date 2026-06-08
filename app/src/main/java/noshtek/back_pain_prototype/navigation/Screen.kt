package noshtek.back_pain_prototype.navigation

sealed class Screen(val route: String) {
    object Onboarding      : Screen("onboarding")
    object Profile         : Screen("profile")
    object Home            : Screen("home")
    object Progress        : Screen("progress")

    // Nested assessment graph — all 6 wizard screens share one AssessmentSessionViewModel
    object AssessmentGraph : Screen("assessment_graph")
    object Occupation      : Screen("occupation")
    object Lifestyle       : Screen("lifestyle")
    object Pain            : Screen("pain")
    object Functional      : Screen("functional")
    object RedFlag         : Screen("red_flag")
    object Review          : Screen("review")

    object Results         : Screen("results/{assessmentId}") {
        fun route(assessmentId: String) = "results/$assessmentId"
    }
    object FullReport      : Screen("full_report/{assessmentId}") {
        fun route(assessmentId: String) = "full_report/$assessmentId"
    }
    object Settings        : Screen("settings")
}
