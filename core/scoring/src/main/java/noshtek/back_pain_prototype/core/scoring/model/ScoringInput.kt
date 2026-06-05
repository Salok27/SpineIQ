package noshtek.back_pain_prototype.core.scoring.model

/**
 * Patient demographics needed by the scoring engine.
 * Age is passed as a pre-calculated integer (years) so the engine stays
 * dependency-free (no java.time).
 */
data class DemographicInput(
    val ageYears: Int,
    val weightKg: Float,
    val heightCm: Float
)

data class LifestyleInput(
    val sittingHoursPerDay: Float,
    val walkingMinutesPerDay: Float,
    val exerciseDaysPerWeek: Int,
    val sleepHoursPerNight: Float,
    val sleepQuality: SleepQuality,
    /** Multi-select; must contain at least one entry (use [ExerciseType.NONE] when none). */
    val exerciseTypes: Set<ExerciseType>
)

data class PainInput(
    /** VAS 0–10 */
    val vasScore: Int,
    val radiculopathy: RadiculopathySeverity,
    val painDuration: PainDuration
)

data class FunctionalInput(
    val walking: FunctionalLevel,
    val sitting: FunctionalLevel,
    val standing: FunctionalLevel,
    val sleep: FunctionalLevel,
    val dailyActivities: FunctionalLevel
)

/** All inputs needed for a single assessment computation. */
data class AssessmentInput(
    val demographic: DemographicInput,
    val lifestyle: LifestyleInput,
    val pain: PainInput,
    val functional: FunctionalInput,
    /** True if any red flag checklist item was confirmed. */
    val hasRedFlag: Boolean
)
