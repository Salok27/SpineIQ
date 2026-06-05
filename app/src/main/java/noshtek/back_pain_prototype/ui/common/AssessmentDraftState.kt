package noshtek.back_pain_prototype.ui.common

import noshtek.back_pain_prototype.core.data.model.*
import noshtek.back_pain_prototype.core.scoring.model.*

data class OccupationDraft(
    val occupationType: OccupationType? = null,
    val sittingHoursPerDay: Float = 6f,
    val standingHoursPerDay: Float = 2f,
    val drivingHoursPerDay: Float = 0f,
    val liftingLevel: LiftingLevel = LiftingLevel.NONE,
    val workPatternNotes: String = ""
)

data class LifestyleDraft(
    val sleepHoursPerNight: Float = 7f,
    val sleepQuality: SleepQuality = SleepQuality.GOOD,
    val walkingMinutesPerDay: Float = 30f,
    val dailySteps: Int? = null,
    val exerciseDaysPerWeek: Int = 2,
    val exerciseTypes: Set<ExerciseType> = setOf(ExerciseType.WALKING),
    val activeMinutesPerDay: Int? = null,
    val sedentaryTimeMinutesPerDay: Int? = null,
    val restingHeartRate: Int? = null,
    val averageHeartRate: Int? = null
)

data class PainDraft(
    val painLocations: Set<PainLocation> = emptySet(),
    val vasScore: Int = 0,
    val painDuration: PainDuration = PainDuration.ACUTE,
    val painPattern: PainPattern = PainPattern.CONSTANT,
    val painTriggers: Set<PainTrigger> = emptySet(),
    val radiculopathySeverity: RadiculopathySeverity = RadiculopathySeverity.NONE,
    val radiationLocation: RadiationLocation? = null,
    val functionalLimitationsText: String = "",
    val functionalLimitationSeverity: FunctionalLimitationSeverity = FunctionalLimitationSeverity.NONE
)

data class FunctionalDraft(
    val walking: FunctionalLevel = FunctionalLevel.NORMAL,
    val sitting: FunctionalLevel = FunctionalLevel.NORMAL,
    val standing: FunctionalLevel = FunctionalLevel.NORMAL,
    val sleep: FunctionalLevel = FunctionalLevel.NORMAL,
    val dailyActivities: FunctionalLevel = FunctionalLevel.NORMAL
) {
    val odiTotal: Int get() = walking.points + sitting.points + standing.points + sleep.points + dailyActivities.points
}

data class RedFlagDraft(
    val historyCancer: Boolean = false,
    val unexplainedWeightLoss: Boolean = false,
    val feverOrInfection: Boolean = false,
    val recentMajorTrauma: Boolean = false,
    val bowelBladderDysfunction: Boolean = false,
    val saddleAnaesthesia: Boolean = false,
    val progressiveNeurologicalDeficit: Boolean = false,
    val otherSeriousPathologySuspicion: Boolean = false
) {
    val hasAnyRedFlag: Boolean
        get() = historyCancer || unexplainedWeightLoss || feverOrInfection ||
                recentMajorTrauma || bowelBladderDysfunction || saddleAnaesthesia ||
                progressiveNeurologicalDeficit || otherSeriousPathologySuspicion
}

data class AssessmentSession(
    val isLoading: Boolean = false,
    val error: String? = null,
    val patientId: String = "",
    val patientName: String = "",
    val patientAgeYears: Int = 0,
    val patientWeightKg: Float = 70f,
    val patientHeightCm: Float = 170f,
    val assessmentId: String = "",
    val occupation: OccupationDraft = OccupationDraft(),
    val lifestyle: LifestyleDraft = LifestyleDraft(),
    val pain: PainDraft = PainDraft(),
    val functional: FunctionalDraft = FunctionalDraft(),
    val redFlags: RedFlagDraft = RedFlagDraft(),
    val scoringResult: noshtek.back_pain_prototype.core.scoring.model.ScoringResult? = null,
    val isScoring: Boolean = false
)
