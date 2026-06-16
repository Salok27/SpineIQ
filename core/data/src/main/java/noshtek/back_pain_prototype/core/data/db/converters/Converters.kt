package noshtek.back_pain_prototype.core.data.db.converters

import androidx.room.TypeConverter
import noshtek.back_pain_prototype.core.data.gamification.CheckInMood
import noshtek.back_pain_prototype.core.data.gamification.RewardType
import noshtek.back_pain_prototype.core.data.model.*
import noshtek.back_pain_prototype.core.scoring.model.*

/**
 * Room TypeConverters for all custom types stored in the SpineIQ database.
 *
 * Dates are stored as epoch-day Long values.
 * Enums are stored as their .name String values for human-readability and schema stability.
 * Multi-select sets are stored as comma-separated enum name strings.
 */
class Converters {

    // ── LocalDate (epoch days) ────────────────────────────────────────────────

    @TypeConverter fun localDateToLong(value: java.time.LocalDate?): Long? = value?.toEpochDay()
    @TypeConverter fun longToLocalDate(value: Long?): java.time.LocalDate? =
        value?.let { java.time.LocalDate.ofEpochDay(it) }

    // ── DataEnums (core:data) ─────────────────────────────────────────────────

    @TypeConverter fun genderToString(v: Gender?): String? = v?.name
    @TypeConverter fun stringToGender(v: String?): Gender? = v?.let { Gender.valueOf(it) }

    @TypeConverter fun assessmentStatusToString(v: AssessmentStatus): String = v.name
    @TypeConverter fun stringToAssessmentStatus(v: String): AssessmentStatus = AssessmentStatus.valueOf(v)

    @TypeConverter fun occupationTypeToString(v: OccupationType): String = v.name
    @TypeConverter fun stringToOccupationType(v: String): OccupationType = OccupationType.valueOf(v)

    @TypeConverter fun liftingLevelToString(v: LiftingLevel): String = v.name
    @TypeConverter fun stringToLiftingLevel(v: String): LiftingLevel = LiftingLevel.valueOf(v)

    @TypeConverter fun painPatternToString(v: PainPattern): String = v.name
    @TypeConverter fun stringToPainPattern(v: String): PainPattern = PainPattern.valueOf(v)

    @TypeConverter fun radiationLocationToString(v: RadiationLocation?): String? = v?.name
    @TypeConverter fun stringToRadiationLocation(v: String?): RadiationLocation? =
        v?.let { RadiationLocation.valueOf(it) }

    @TypeConverter fun functionalLimitationSeverityToString(v: FunctionalLimitationSeverity?): String? = v?.name
    @TypeConverter fun stringToFunctionalLimitationSeverity(v: String?): FunctionalLimitationSeverity? =
        v?.let { FunctionalLimitationSeverity.valueOf(it) }

    @TypeConverter fun dataSourceToString(v: DataSource): String = v.name
    @TypeConverter fun stringToDataSource(v: String): DataSource = DataSource.valueOf(v)

    // ── Set<PainLocation> ─────────────────────────────────────────────────────

    @TypeConverter fun painLocationsToString(v: Set<PainLocation>): String =
        v.joinToString(",") { it.name }

    @TypeConverter fun stringToPainLocations(v: String): Set<PainLocation> {
        if (v.isBlank()) return emptySet()
        return v.split(",").mapTo(mutableSetOf()) { PainLocation.valueOf(it.trim()) }
    }

    // ── Set<PainTrigger> ─────────────────────────────────────────────────────

    @TypeConverter fun painTriggersToString(v: Set<PainTrigger>): String =
        v.joinToString(",") { it.name }

    @TypeConverter fun stringToPainTriggers(v: String): Set<PainTrigger> {
        if (v.isBlank()) return emptySet()
        return v.split(",").mapTo(mutableSetOf()) { PainTrigger.valueOf(it.trim()) }
    }

    // ── Scoring enums (core:scoring) ──────────────────────────────────────────

    @TypeConverter fun sleepQualityToString(v: SleepQuality): String = v.name
    @TypeConverter fun stringToSleepQuality(v: String): SleepQuality = SleepQuality.valueOf(v)

    @TypeConverter fun painDurationToString(v: PainDuration): String = v.name
    @TypeConverter fun stringToPainDuration(v: String): PainDuration = PainDuration.valueOf(v)

    @TypeConverter fun radiculopathySeverityToString(v: RadiculopathySeverity): String = v.name
    @TypeConverter fun stringToRadiculopathySeverity(v: String): RadiculopathySeverity =
        RadiculopathySeverity.valueOf(v)

    @TypeConverter fun functionalLevelToString(v: FunctionalLevel): String = v.name
    @TypeConverter fun stringToFunctionalLevel(v: String): FunctionalLevel = FunctionalLevel.valueOf(v)

    @TypeConverter fun exerciseTypesToString(v: Set<ExerciseType>): String =
        v.joinToString(",") { it.name }

    @TypeConverter fun stringToExerciseTypes(v: String): Set<ExerciseType> {
        if (v.isBlank()) return emptySet()
        return v.split(",").mapTo(mutableSetOf()) { ExerciseType.valueOf(it.trim()) }
    }

    @TypeConverter fun riskTierToString(v: RiskTier): String = v.name
    @TypeConverter fun stringToRiskTier(v: String): RiskTier = RiskTier.valueOf(v)

    @TypeConverter fun sssSeverityTierToString(v: SssSeverityTier): String = v.name
    @TypeConverter fun stringToSssSeverityTier(v: String): SssSeverityTier = SssSeverityTier.valueOf(v)

    @TypeConverter fun backPainRiskToString(v: BackPainRiskClassification): String = v.name
    @TypeConverter fun stringToBackPainRisk(v: String): BackPainRiskClassification =
        BackPainRiskClassification.valueOf(v)

    @TypeConverter fun bmiCategoryToString(v: BmiCategory): String = v.name
    @TypeConverter fun stringToBmiCategory(v: String): BmiCategory = BmiCategory.valueOf(v)

    @TypeConverter fun ageGroupToString(v: AgeGroup): String = v.name
    @TypeConverter fun stringToAgeGroup(v: String): AgeGroup = AgeGroup.valueOf(v)

    // ── Gamification enums ────────────────────────────────────────────────────

    @TypeConverter fun checkInMoodToString(v: CheckInMood): String = v.name
    @TypeConverter fun stringToCheckInMood(v: String): CheckInMood = CheckInMood.valueOf(v)

    @TypeConverter fun rewardTypeToString(v: RewardType): String = v.name
    @TypeConverter fun stringToRewardType(v: String): RewardType = RewardType.valueOf(v)
}
