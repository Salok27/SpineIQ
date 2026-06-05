package noshtek.back_pain_prototype.navigation

sealed class Screen(val route: String) {
    object Home            : Screen("home")
    object PatientList     : Screen("patients")
    object PatientInfo     : Screen("patient_info?patientId={patientId}") {
        fun route(patientId: String) = "patient_info?patientId=$patientId"
        const val NEW = "patient_info"
    }
    object PatientHistory  : Screen("patient_history/{patientId}") {
        fun route(patientId: String) = "patient_history/$patientId"
    }
    // Nested assessment graph — all assessment screens share one AssessmentSessionViewModel scoped to this
    object AssessmentGraph : Screen("assessment_graph")
    // patientId carried in Occupation so the shared ViewModel can call initSession on first load
    object Occupation      : Screen("occupation/{patientId}") {
        fun route(patientId: String) = "occupation/$patientId"
    }
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
