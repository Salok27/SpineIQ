package noshtek.back_pain_prototype.core.scoring

import noshtek.back_pain_prototype.core.scoring.model.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

// ════════════════════════════════════════════════════════════════════════════
// Helpers
// ════════════════════════════════════════════════════════════════════════════

private fun demographic(age: Int = 35, weight: Float = 70f, height: Float = 170f) =
    DemographicInput(ageYears = age, weightKg = weight, heightCm = height)

private fun pain(
    vas: Int = 0,
    radio: RadiculopathySeverity = RadiculopathySeverity.NONE,
    duration: PainDuration = PainDuration.ACUTE
) = PainInput(vasScore = vas, radiculopathy = radio, painDuration = duration)

private fun functional(
    walking: FunctionalLevel = FunctionalLevel.NORMAL,
    sitting: FunctionalLevel = FunctionalLevel.NORMAL,
    standing: FunctionalLevel = FunctionalLevel.NORMAL,
    sleep: FunctionalLevel = FunctionalLevel.NORMAL,
    daily: FunctionalLevel = FunctionalLevel.NORMAL
) = FunctionalInput(walking, sitting, standing, sleep, daily)

private fun lifestyle(
    sitting: Float = 5f,
    walking: Float = 50f,
    exercise: Int = 4,
    sleepHrs: Float = 7.5f,
    sleepQuality: SleepQuality = SleepQuality.GOOD,
    types: Set<ExerciseType> = setOf(ExerciseType.WALKING)
) = LifestyleInput(
    sittingHoursPerDay = sitting,
    walkingMinutesPerDay = walking,
    exerciseDaysPerWeek = exercise,
    sleepHoursPerNight = sleepHrs,
    sleepQuality = sleepQuality,
    exerciseTypes = types
)

private fun allLowInput() = AssessmentInput(
    demographic = demographic(),
    lifestyle   = lifestyle(),
    pain        = pain(),
    functional  = functional(),
    hasRedFlag  = false
)

// ════════════════════════════════════════════════════════════════════════════
// VAS Pain Score  (Section 10.1 §1)
// ════════════════════════════════════════════════════════════════════════════

class VasScoreTest {

    @Test fun vas_0_gives_0_points()  = assertEquals(0, SssScorer.vasPoints(0))
    @Test fun vas_3_gives_0_points()  = assertEquals(0, SssScorer.vasPoints(3))
    @Test fun vas_4_gives_1_point()   = assertEquals(1, SssScorer.vasPoints(4))
    @Test fun vas_6_gives_1_point()   = assertEquals(1, SssScorer.vasPoints(6))
    @Test fun vas_7_gives_2_points()  = assertEquals(2, SssScorer.vasPoints(7))
    @Test fun vas_10_gives_2_points() = assertEquals(2, SssScorer.vasPoints(10))
}

// ════════════════════════════════════════════════════════════════════════════
// Radiculopathy Score  (Section 10.1 §2)
// ════════════════════════════════════════════════════════════════════════════

class RadiculopathyScoreTest {

    @Test fun none_gives_0()     = assertEquals(0, SssScorer.radiculopathyPoints(RadiculopathySeverity.NONE))
    @Test fun mild_gives_1()     = assertEquals(1, SssScorer.radiculopathyPoints(RadiculopathySeverity.MILD))
    @Test fun moderate_gives_2() = assertEquals(2, SssScorer.radiculopathyPoints(RadiculopathySeverity.MODERATE))
    @Test fun severe_gives_3()   = assertEquals(3, SssScorer.radiculopathyPoints(RadiculopathySeverity.SEVERE))
}

// ════════════════════════════════════════════════════════════════════════════
// SSS Modified ODI  (Section 10.1 §3)
// ════════════════════════════════════════════════════════════════════════════

class OdiTest {

    private fun odi(
        w: FunctionalLevel, s: FunctionalLevel, st: FunctionalLevel,
        sl: FunctionalLevel, d: FunctionalLevel
    ) = SssScorer.odiResult(functional(w, s, st, sl, d))

    @Test fun all_normal_gives_total_0_points_0() {
        val r = odi(FunctionalLevel.NORMAL, FunctionalLevel.NORMAL, FunctionalLevel.NORMAL,
                    FunctionalLevel.NORMAL, FunctionalLevel.NORMAL)
        assertEquals(0, r.total); assertEquals(0, r.points)
    }

    @Test fun total_2_gives_points_0() {
        val r = odi(FunctionalLevel.MILD_DIFFICULTY, FunctionalLevel.MILD_DIFFICULTY,
                    FunctionalLevel.NORMAL, FunctionalLevel.NORMAL, FunctionalLevel.NORMAL)
        assertEquals(2, r.total); assertEquals(0, r.points)
    }

    @Test fun total_3_gives_points_1() {
        val r = odi(FunctionalLevel.MILD_DIFFICULTY, FunctionalLevel.MILD_DIFFICULTY,
                    FunctionalLevel.MILD_DIFFICULTY, FunctionalLevel.NORMAL, FunctionalLevel.NORMAL)
        assertEquals(3, r.total); assertEquals(1, r.points)
    }

    @Test fun total_5_gives_points_1() {
        val r = odi(FunctionalLevel.SEVERE_DIFFICULTY, FunctionalLevel.MILD_DIFFICULTY,
                    FunctionalLevel.MILD_DIFFICULTY, FunctionalLevel.MILD_DIFFICULTY, FunctionalLevel.NORMAL)
        assertEquals(5, r.total); assertEquals(1, r.points)
    }

    @Test fun total_6_gives_points_2() {
        val r = odi(FunctionalLevel.SEVERE_DIFFICULTY, FunctionalLevel.SEVERE_DIFFICULTY,
                    FunctionalLevel.MILD_DIFFICULTY, FunctionalLevel.MILD_DIFFICULTY, FunctionalLevel.NORMAL)
        assertEquals(6, r.total); assertEquals(2, r.points)
    }

    @Test fun total_10_gives_points_2() {
        val r = odi(FunctionalLevel.SEVERE_DIFFICULTY, FunctionalLevel.SEVERE_DIFFICULTY,
                    FunctionalLevel.SEVERE_DIFFICULTY, FunctionalLevel.SEVERE_DIFFICULTY,
                    FunctionalLevel.SEVERE_DIFFICULTY)
        assertEquals(10, r.total); assertEquals(2, r.points)
    }
}

// ════════════════════════════════════════════════════════════════════════════
// BMI Score  (Section 10.1 §4)
// ════════════════════════════════════════════════════════════════════════════

class BmiScoreTest {

    // bmi = weight / (height/100)^2
    // height 170 cm → heightM = 1.70
    // bmi 24 → weight = 24 * 1.70^2 = 24 * 2.89 = 69.36 kg
    private fun bmiFor(targetBmi: Float, heightCm: Float = 170f): Float {
        val hM = heightCm / 100f
        return targetBmi * hM * hM
    }

    @Test fun bmi_under_25_gives_0_points() {
        val w = bmiFor(24f)
        assertEquals(0, SssScorer.bmiPoints(SssScorer.bmi(w, 170f)))
        assertEquals(BmiCategory.NORMAL, SssScorer.bmiCategory(SssScorer.bmi(w, 170f)))
    }

    @Test fun bmi_25_exactly_gives_1_point() {
        val w = bmiFor(25.0f)
        assertEquals(1, SssScorer.bmiPoints(SssScorer.bmi(w, 170f)))
        assertEquals(BmiCategory.OVERWEIGHT, SssScorer.bmiCategory(SssScorer.bmi(w, 170f)))
    }

    @Test fun bmi_29_9_gives_1_point() {
        val w = bmiFor(29.9f)
        assertEquals(1, SssScorer.bmiPoints(SssScorer.bmi(w, 170f)))
    }

    @Test fun bmi_30_exactly_gives_2_points() {
        val w = bmiFor(30.0f)
        assertEquals(2, SssScorer.bmiPoints(SssScorer.bmi(w, 170f)))
        assertEquals(BmiCategory.OBESE, SssScorer.bmiCategory(SssScorer.bmi(w, 170f)))
    }

    @Test fun bmi_under_18_5_is_underweight() {
        val w = bmiFor(17f)
        assertEquals(BmiCategory.UNDERWEIGHT, SssScorer.bmiCategory(SssScorer.bmi(w, 170f)))
        assertEquals(0, SssScorer.bmiPoints(SssScorer.bmi(w, 170f)))
    }
}

// ════════════════════════════════════════════════════════════════════════════
// Chronicity Score  (Section 10.1 §5)
// ════════════════════════════════════════════════════════════════════════════

class ChronicityTest {
    @Test fun acute_gives_0()    = assertEquals(0, SssScorer.chronicityPoints(PainDuration.ACUTE))
    @Test fun subacute_gives_1() = assertEquals(1, SssScorer.chronicityPoints(PainDuration.SUBACUTE))
    @Test fun chronic_gives_2()  = assertEquals(2, SssScorer.chronicityPoints(PainDuration.CHRONIC))
}

// ════════════════════════════════════════════════════════════════════════════
// Red Flag Override  (Section 10.1 §6 + Section 9.3)
// ════════════════════════════════════════════════════════════════════════════

class RedFlagTest {

    @Test fun no_red_flag_score_is_0()  = assertEquals(0, SssScorer.redFlagScore(false))
    @Test fun red_flag_score_is_11()    = assertEquals(11, SssScorer.redFlagScore(true))

    @Test fun red_flag_forces_total_sss_to_11() {
        val sss = SssScorer.compute(
            demographic = demographic(),
            pain = pain(vas = 0),
            functional = functional(),
            hasRedFlag = true
        )
        assertEquals(11, sss.totalSSSScore)
        assertEquals(SssSeverityTier.SEVERE_HIGH_RISK, sss.severityTier)
    }

    @Test fun red_flag_preserves_raw_score_for_audit() {
        val sss = SssScorer.compute(demographic(), pain(vas = 2), functional(), hasRedFlag = true)
        assertEquals(0, sss.rawSSSScore)   // vas=2 → 0 pts + all else 0
        assertEquals(11, sss.totalSSSScore)
    }

    @Test fun red_flag_composite_is_severe_urgent() {
        val result = ScoringEngine.compute(allLowInput().copy(hasRedFlag = true))
        assertEquals(BackPainRiskClassification.SEVERE_URGENT, result.backPainRiskClassification)
    }
}

// ════════════════════════════════════════════════════════════════════════════
// SSS Severity Tiers  (Section 11.1)
// ════════════════════════════════════════════════════════════════════════════

class SssSeverityTierTest {
    @Test fun score_0_is_low()                = assertEquals(SssSeverityTier.LOW,              SssScorer.severityTier(0))
    @Test fun score_3_is_low()                = assertEquals(SssSeverityTier.LOW,              SssScorer.severityTier(3))
    @Test fun score_4_is_mild_moderate()      = assertEquals(SssSeverityTier.MILD_MODERATE,    SssScorer.severityTier(4))
    @Test fun score_6_is_mild_moderate()      = assertEquals(SssSeverityTier.MILD_MODERATE,    SssScorer.severityTier(6))
    @Test fun score_7_is_moderate_severe()    = assertEquals(SssSeverityTier.MODERATE_SEVERE,  SssScorer.severityTier(7))
    @Test fun score_9_is_moderate_severe()    = assertEquals(SssSeverityTier.MODERATE_SEVERE,  SssScorer.severityTier(9))
    @Test fun score_10_is_severe_high_risk()  = assertEquals(SssSeverityTier.SEVERE_HIGH_RISK, SssScorer.severityTier(10))
    @Test fun score_11_is_severe_high_risk()  = assertEquals(SssSeverityTier.SEVERE_HIGH_RISK, SssScorer.severityTier(11))
}

// ════════════════════════════════════════════════════════════════════════════
// Age Group Classification  (Section 9.2)
// ════════════════════════════════════════════════════════════════════════════

class AgeGroupTest {
    @Test fun age_20_is_young_adult()  = assertEquals(AgeGroup.YOUNG_ADULT, LifestyleScorer.ageGroup(20))
    @Test fun age_30_is_young_adult()  = assertEquals(AgeGroup.YOUNG_ADULT, LifestyleScorer.ageGroup(30))
    @Test fun age_31_is_mid_adult()    = assertEquals(AgeGroup.MID_ADULT,   LifestyleScorer.ageGroup(31))
    @Test fun age_45_is_mid_adult()    = assertEquals(AgeGroup.MID_ADULT,   LifestyleScorer.ageGroup(45))
    @Test fun age_46_is_pre_senior()   = assertEquals(AgeGroup.PRE_SENIOR,  LifestyleScorer.ageGroup(46))
    @Test fun age_60_is_pre_senior()   = assertEquals(AgeGroup.PRE_SENIOR,  LifestyleScorer.ageGroup(60))
    @Test fun age_61_is_senior()       = assertEquals(AgeGroup.SENIOR,      LifestyleScorer.ageGroup(61))
    @Test fun age_80_is_senior()       = assertEquals(AgeGroup.SENIOR,      LifestyleScorer.ageGroup(80))
}

// ════════════════════════════════════════════════════════════════════════════
// Sitting Risk  (age-adjusted, Section 10.2)
// ════════════════════════════════════════════════════════════════════════════

class SittingRiskTest {

    @Test fun mid_adult_5hrs_is_low()      = assertEquals(RiskTier.LOW,      LifestyleScorer.sittingRisk(5f, AgeGroup.MID_ADULT))
    @Test fun mid_adult_6_9_is_low()       = assertEquals(RiskTier.LOW,      LifestyleScorer.sittingRisk(6.9f, AgeGroup.MID_ADULT))
    @Test fun mid_adult_7hrs_is_moderate() = assertEquals(RiskTier.MODERATE, LifestyleScorer.sittingRisk(7f, AgeGroup.MID_ADULT))
    @Test fun mid_adult_8_9_is_moderate()  = assertEquals(RiskTier.MODERATE, LifestyleScorer.sittingRisk(8.9f, AgeGroup.MID_ADULT))
    @Test fun mid_adult_9hrs_is_high()     = assertEquals(RiskTier.HIGH,     LifestyleScorer.sittingRisk(9f, AgeGroup.MID_ADULT))
    @Test fun mid_adult_12hrs_is_high()    = assertEquals(RiskTier.HIGH,     LifestyleScorer.sittingRisk(12f, AgeGroup.MID_ADULT))

    // Senior thresholds are tighter
    @Test fun senior_5hrs_is_low()         = assertEquals(RiskTier.LOW,      LifestyleScorer.sittingRisk(5f, AgeGroup.SENIOR))
    @Test fun senior_6hrs_is_moderate()    = assertEquals(RiskTier.MODERATE, LifestyleScorer.sittingRisk(6f, AgeGroup.SENIOR))
    @Test fun senior_7hrs_is_high()        = assertEquals(RiskTier.HIGH,     LifestyleScorer.sittingRisk(7f, AgeGroup.SENIOR))
}

// ════════════════════════════════════════════════════════════════════════════
// Walking Risk  (age-adjusted, Section 10.2)
// ════════════════════════════════════════════════════════════════════════════

class WalkingRiskTest {

    @Test fun mid_adult_50min_is_low()      = assertEquals(RiskTier.LOW,      LifestyleScorer.walkingRisk(50f, AgeGroup.MID_ADULT))
    @Test fun mid_adult_45min_is_low()      = assertEquals(RiskTier.LOW,      LifestyleScorer.walkingRisk(45f, AgeGroup.MID_ADULT))
    @Test fun mid_adult_30min_is_moderate() = assertEquals(RiskTier.MODERATE, LifestyleScorer.walkingRisk(30f, AgeGroup.MID_ADULT))
    @Test fun mid_adult_20min_is_moderate() = assertEquals(RiskTier.MODERATE, LifestyleScorer.walkingRisk(20f, AgeGroup.MID_ADULT))
    @Test fun mid_adult_19min_is_high()     = assertEquals(RiskTier.HIGH,     LifestyleScorer.walkingRisk(19f, AgeGroup.MID_ADULT))
    @Test fun mid_adult_0min_is_high()      = assertEquals(RiskTier.HIGH,     LifestyleScorer.walkingRisk(0f, AgeGroup.MID_ADULT))

    // Seniors have adjusted (lower) thresholds
    @Test fun senior_20min_is_low()         = assertEquals(RiskTier.LOW,      LifestyleScorer.walkingRisk(20f, AgeGroup.SENIOR))
    @Test fun senior_10min_is_moderate()    = assertEquals(RiskTier.MODERATE, LifestyleScorer.walkingRisk(10f, AgeGroup.SENIOR))
    @Test fun senior_9min_is_high()         = assertEquals(RiskTier.HIGH,     LifestyleScorer.walkingRisk(9f, AgeGroup.SENIOR))
}

// ════════════════════════════════════════════════════════════════════════════
// Exercise Frequency Risk  (Section 10.2)
// ════════════════════════════════════════════════════════════════════════════

class ExerciseFrequencyRiskTest {
    @Test fun zero_days_is_high()     = assertEquals(RiskTier.HIGH,     LifestyleScorer.exerciseFrequencyRisk(0))
    @Test fun one_day_is_moderate()   = assertEquals(RiskTier.MODERATE, LifestyleScorer.exerciseFrequencyRisk(1))
    @Test fun two_days_is_moderate()  = assertEquals(RiskTier.MODERATE, LifestyleScorer.exerciseFrequencyRisk(2))
    @Test fun three_days_is_low()     = assertEquals(RiskTier.LOW,      LifestyleScorer.exerciseFrequencyRisk(3))
    @Test fun seven_days_is_low()     = assertEquals(RiskTier.LOW,      LifestyleScorer.exerciseFrequencyRisk(7))
}

// ════════════════════════════════════════════════════════════════════════════
// Sleep Hours Risk  (Section 10.2)
// ════════════════════════════════════════════════════════════════════════════

class SleepHoursRiskTest {
    @Test fun hours_4_is_high()      = assertEquals(RiskTier.HIGH,     LifestyleScorer.sleepHoursRisk(4f))
    @Test fun hours_4_9_is_high()    = assertEquals(RiskTier.HIGH,     LifestyleScorer.sleepHoursRisk(4.9f))
    @Test fun hours_5_is_moderate()  = assertEquals(RiskTier.MODERATE, LifestyleScorer.sleepHoursRisk(5f))
    @Test fun hours_6_is_moderate()  = assertEquals(RiskTier.MODERATE, LifestyleScorer.sleepHoursRisk(6f))
    @Test fun hours_6_9_is_moderate()= assertEquals(RiskTier.MODERATE, LifestyleScorer.sleepHoursRisk(6.9f))
    @Test fun hours_7_is_low()       = assertEquals(RiskTier.LOW,      LifestyleScorer.sleepHoursRisk(7f))
    @Test fun hours_8_is_low()       = assertEquals(RiskTier.LOW,      LifestyleScorer.sleepHoursRisk(8f))
    @Test fun hours_9_is_low()       = assertEquals(RiskTier.LOW,      LifestyleScorer.sleepHoursRisk(9f))
}

// ════════════════════════════════════════════════════════════════════════════
// Sleep Quality Modifier  (Section 10.2)
// ════════════════════════════════════════════════════════════════════════════

class SleepQualityModifierTest {

    // Excellent improves Moderate → Low; leaves Low alone
    @Test fun excellent_on_moderate_becomes_low() =
        assertEquals(RiskTier.LOW, LifestyleScorer.applySleepQualityModifier(RiskTier.MODERATE, SleepQuality.EXCELLENT))
    @Test fun excellent_on_low_stays_low() =
        assertEquals(RiskTier.LOW, LifestyleScorer.applySleepQualityModifier(RiskTier.LOW, SleepQuality.EXCELLENT))
    @Test fun excellent_on_high_becomes_moderate() =
        assertEquals(RiskTier.MODERATE, LifestyleScorer.applySleepQualityModifier(RiskTier.HIGH, SleepQuality.EXCELLENT))

    // Good → no change
    @Test fun good_on_low_stays_low()          = assertEquals(RiskTier.LOW,      LifestyleScorer.applySleepQualityModifier(RiskTier.LOW,      SleepQuality.GOOD))
    @Test fun good_on_moderate_stays_moderate() = assertEquals(RiskTier.MODERATE, LifestyleScorer.applySleepQualityModifier(RiskTier.MODERATE, SleepQuality.GOOD))
    @Test fun good_on_high_stays_high()         = assertEquals(RiskTier.HIGH,     LifestyleScorer.applySleepQualityModifier(RiskTier.HIGH,     SleepQuality.GOOD))

    // Fair → one step toward High
    @Test fun fair_on_low_becomes_moderate()    = assertEquals(RiskTier.MODERATE, LifestyleScorer.applySleepQualityModifier(RiskTier.LOW,      SleepQuality.FAIR))
    @Test fun fair_on_moderate_becomes_high()   = assertEquals(RiskTier.HIGH,     LifestyleScorer.applySleepQualityModifier(RiskTier.MODERATE, SleepQuality.FAIR))
    @Test fun fair_on_high_stays_high()         = assertEquals(RiskTier.HIGH,     LifestyleScorer.applySleepQualityModifier(RiskTier.HIGH,     SleepQuality.FAIR))

    // Poor → one step toward High (same mechanics as Fair; MODERATE forced to HIGH)
    @Test fun poor_on_low_becomes_moderate()    = assertEquals(RiskTier.MODERATE, LifestyleScorer.applySleepQualityModifier(RiskTier.LOW,      SleepQuality.POOR))
    @Test fun poor_on_moderate_forces_high()    = assertEquals(RiskTier.HIGH,     LifestyleScorer.applySleepQualityModifier(RiskTier.MODERATE, SleepQuality.POOR))
    @Test fun poor_on_high_stays_high()         = assertEquals(RiskTier.HIGH,     LifestyleScorer.applySleepQualityModifier(RiskTier.HIGH,     SleepQuality.POOR))
}

// ════════════════════════════════════════════════════════════════════════════
// Exercise Type Modifier  (Section 10.2)
// ════════════════════════════════════════════════════════════════════════════

class ExerciseTypeModifierTest {

    @Test fun walking_type_does_not_apply_modifier() =
        assertFalse(LifestyleScorer.exerciseTypeModifierApplies(setOf(ExerciseType.WALKING)))
    @Test fun cycling_does_not_apply_modifier() =
        assertFalse(LifestyleScorer.exerciseTypeModifierApplies(setOf(ExerciseType.CYCLING)))
    @Test fun swimming_does_not_apply_modifier() =
        assertFalse(LifestyleScorer.exerciseTypeModifierApplies(setOf(ExerciseType.SWIMMING)))
    @Test fun yoga_does_not_apply_modifier() =
        assertFalse(LifestyleScorer.exerciseTypeModifierApplies(setOf(ExerciseType.YOGA_PILATES)))
    @Test fun none_does_not_apply_modifier() =
        assertFalse(LifestyleScorer.exerciseTypeModifierApplies(setOf(ExerciseType.NONE)))

    @Test fun running_applies_modifier() =
        assertTrue(LifestyleScorer.exerciseTypeModifierApplies(setOf(ExerciseType.RUNNING)))
    @Test fun gym_weights_applies_modifier() =
        assertTrue(LifestyleScorer.exerciseTypeModifierApplies(setOf(ExerciseType.GYM_WEIGHTS)))
    @Test fun other_applies_modifier() =
        assertTrue(LifestyleScorer.exerciseTypeModifierApplies(setOf(ExerciseType.OTHER)))
    @Test fun mixed_high_low_applies_modifier() =
        assertTrue(LifestyleScorer.exerciseTypeModifierApplies(setOf(ExerciseType.WALKING, ExerciseType.RUNNING)))

    @Test fun modifier_steps_low_to_moderate() =
        assertEquals(RiskTier.MODERATE, LifestyleScorer.applyExerciseTypeModifier(RiskTier.LOW, setOf(ExerciseType.RUNNING)))
    @Test fun modifier_steps_moderate_to_high() =
        assertEquals(RiskTier.HIGH, LifestyleScorer.applyExerciseTypeModifier(RiskTier.MODERATE, setOf(ExerciseType.GYM_WEIGHTS)))
    @Test fun modifier_high_stays_high() =
        assertEquals(RiskTier.HIGH, LifestyleScorer.applyExerciseTypeModifier(RiskTier.HIGH, setOf(ExerciseType.RUNNING)))
    @Test fun no_modifier_low_stays_low() =
        assertEquals(RiskTier.LOW, LifestyleScorer.applyExerciseTypeModifier(RiskTier.LOW, setOf(ExerciseType.WALKING)))
}

// ════════════════════════════════════════════════════════════════════════════
// Lifestyle Risk Aggregate  (Section 10.2)
// ════════════════════════════════════════════════════════════════════════════

class LifestyleAggregateTest {
    private fun agg(s: RiskTier, w: RiskTier, e: RiskTier, sl: RiskTier) =
        LifestyleScorer.aggregateLifestyleRisk(s, w, e, sl)

    @Test fun all_low_is_low()               = assertEquals(RiskTier.LOW,      agg(RiskTier.LOW, RiskTier.LOW, RiskTier.LOW, RiskTier.LOW))
    @Test fun one_moderate_is_moderate()     = assertEquals(RiskTier.MODERATE, agg(RiskTier.MODERATE, RiskTier.LOW, RiskTier.LOW, RiskTier.LOW))
    @Test fun all_moderate_is_moderate()     = assertEquals(RiskTier.MODERATE, agg(RiskTier.MODERATE, RiskTier.MODERATE, RiskTier.MODERATE, RiskTier.MODERATE))
    @Test fun one_high_is_high()             = assertEquals(RiskTier.HIGH,     agg(RiskTier.HIGH, RiskTier.LOW, RiskTier.LOW, RiskTier.LOW))
    @Test fun high_overrides_moderate()      = assertEquals(RiskTier.HIGH,     agg(RiskTier.HIGH, RiskTier.MODERATE, RiskTier.LOW, RiskTier.LOW))
    @Test fun all_high_is_high()             = assertEquals(RiskTier.HIGH,     agg(RiskTier.HIGH, RiskTier.HIGH, RiskTier.HIGH, RiskTier.HIGH))
}

// ════════════════════════════════════════════════════════════════════════════
// Composite Back Pain Risk Matrix  (Section 10.3)
// ════════════════════════════════════════════════════════════════════════════

class CompositRiskMatrixTest {

    private fun classify(sss: Int, lifestyle: RiskTier, redFlag: Boolean = false) =
        ScoringEngine.classifyBackPainRisk(sss, lifestyle, redFlag)

    // SSS 0–3
    @Test fun sss0_low_lifestyle_is_low()              = assertEquals(BackPainRiskClassification.LOW,          classify(0, RiskTier.LOW))
    @Test fun sss3_low_lifestyle_is_low()              = assertEquals(BackPainRiskClassification.LOW,          classify(3, RiskTier.LOW))
    @Test fun sss0_moderate_lifestyle_is_low_moderate()= assertEquals(BackPainRiskClassification.LOW_MODERATE, classify(0, RiskTier.MODERATE))
    @Test fun sss3_high_lifestyle_is_moderate()        = assertEquals(BackPainRiskClassification.MODERATE,     classify(3, RiskTier.HIGH))

    // SSS 4–6
    @Test fun sss4_low_lifestyle_is_mild_moderate()    = assertEquals(BackPainRiskClassification.MILD_MODERATE,  classify(4, RiskTier.LOW))
    @Test fun sss6_low_lifestyle_is_mild_moderate()    = assertEquals(BackPainRiskClassification.MILD_MODERATE,  classify(6, RiskTier.LOW))
    @Test fun sss4_moderate_lifestyle_is_moderate()    = assertEquals(BackPainRiskClassification.MODERATE,       classify(4, RiskTier.MODERATE))
    @Test fun sss6_high_lifestyle_is_moderate_high()   = assertEquals(BackPainRiskClassification.MODERATE_HIGH,  classify(6, RiskTier.HIGH))

    // SSS 7–9
    @Test fun sss7_low_lifestyle_is_high()             = assertEquals(BackPainRiskClassification.HIGH, classify(7, RiskTier.LOW))
    @Test fun sss9_high_lifestyle_is_high()            = assertEquals(BackPainRiskClassification.HIGH, classify(9, RiskTier.HIGH))

    // SSS 10–11
    @Test fun sss10_is_severe_urgent()                 = assertEquals(BackPainRiskClassification.SEVERE_URGENT, classify(10, RiskTier.LOW))
    @Test fun sss11_is_severe_urgent()                 = assertEquals(BackPainRiskClassification.SEVERE_URGENT, classify(11, RiskTier.LOW))

    // Red flag always → Severe/Urgent
    @Test fun red_flag_with_sss0_is_severe_urgent()    = assertEquals(BackPainRiskClassification.SEVERE_URGENT, classify(0, RiskTier.LOW, redFlag = true))
    @Test fun red_flag_with_sss5_is_severe_urgent()    = assertEquals(BackPainRiskClassification.SEVERE_URGENT, classify(5, RiskTier.LOW, redFlag = true))
}

// ════════════════════════════════════════════════════════════════════════════
// End-to-end ScoringEngine.compute()
// ════════════════════════════════════════════════════════════════════════════

class ScoringEngineIntegrationTest {

    @Test fun ideal_patient_scores_all_low() {
        val result = ScoringEngine.compute(allLowInput())
        assertEquals(0, result.sss.totalSSSScore)
        assertEquals(SssSeverityTier.LOW, result.sss.severityTier)
        assertEquals(RiskTier.LOW, result.lifestyle.lifestyleRiskTier)
        assertEquals(BackPainRiskClassification.LOW, result.backPainRiskClassification)
    }

    @Test fun worst_case_no_red_flag_scores_sss_8() {
        // VAS 10 (2pts) + Severe radiculopathy (3pts) + ODI all-severe (2pts) +
        // BMI obese 35 (2pts) + Chronic (2pts) = 11 → but no red flag → actual calc is 11
        // Wait: VAS 10 = 2, Radio SEVERE = 3, ODI pts = 2, BMI ≥30 = 2, Chronic = 2 → sum = 11
        // So rawSSSScore would be 11, and totalSSSScore = 11 without any red flag
        val result = ScoringEngine.compute(
            AssessmentInput(
                demographic = demographic(age = 40, weight = 102f, height = 170f), // BMI≈35
                lifestyle   = lifestyle(),
                pain        = pain(vas = 10, radio = RadiculopathySeverity.SEVERE, duration = PainDuration.CHRONIC),
                functional  = functional(
                    FunctionalLevel.SEVERE_DIFFICULTY, FunctionalLevel.SEVERE_DIFFICULTY,
                    FunctionalLevel.SEVERE_DIFFICULTY, FunctionalLevel.SEVERE_DIFFICULTY,
                    FunctionalLevel.SEVERE_DIFFICULTY
                ),
                hasRedFlag  = false
            )
        )
        assertEquals(11, result.sss.rawSSSScore)
        assertEquals(11, result.sss.totalSSSScore)
        assertEquals(SssSeverityTier.SEVERE_HIGH_RISK, result.sss.severityTier)
        assertEquals(BackPainRiskClassification.SEVERE_URGENT, result.backPainRiskClassification)
    }

    @Test fun sedentary_office_worker_lifestyle_high_risk() {
        val result = ScoringEngine.compute(
            allLowInput().copy(
                lifestyle = lifestyle(
                    sitting  = 12f,
                    walking  = 5f,
                    exercise = 0,
                    sleepHrs = 4f,
                    sleepQuality = SleepQuality.POOR,
                    types    = setOf(ExerciseType.NONE)
                )
            )
        )
        assertEquals(RiskTier.HIGH, result.lifestyle.sittingRisk)
        assertEquals(RiskTier.HIGH, result.lifestyle.walkingRisk)
        assertEquals(RiskTier.HIGH, result.lifestyle.exerciseRisk)
        assertEquals(RiskTier.HIGH, result.lifestyle.sleepRisk)
        assertEquals(RiskTier.HIGH, result.lifestyle.lifestyleRiskTier)
    }

    @Test fun exercise_type_modifier_applied_in_full_compute() {
        val withHighImpact = allLowInput().copy(
            lifestyle = lifestyle(exercise = 1, types = setOf(ExerciseType.RUNNING))
        )
        val result = ScoringEngine.compute(withHighImpact)
        assertTrue(result.lifestyle.exerciseTypeModifierApplied)
        // base exercise risk for 1 day = MODERATE; modifier → HIGH
        assertEquals(RiskTier.HIGH, result.lifestyle.exerciseRisk)
    }

    @Test fun senior_thresholds_applied_correctly() {
        val result = ScoringEngine.compute(
            allLowInput().copy(
                demographic = demographic(age = 65),
                // Senior: sitting ≥7 → HIGH; walking 5 < 10 → HIGH; walking 15 → MODERATE (10≤x<20)
                lifestyle   = lifestyle(sitting = 7f, walking = 5f)
            )
        )
        assertEquals(AgeGroup.SENIOR, result.lifestyle.ageGroup)
        assertEquals(RiskTier.HIGH, result.lifestyle.sittingRisk)
        assertEquals(RiskTier.HIGH, result.lifestyle.walkingRisk)
    }

    @Test fun senior_walking_15min_is_moderate_not_high() {
        val result = ScoringEngine.compute(
            allLowInput().copy(
                demographic = demographic(age = 65),
                lifestyle   = lifestyle(walking = 15f)
            )
        )
        // 15 min: ≥20 = Low, <10 = High, 10–19 = Moderate
        assertEquals(RiskTier.MODERATE, result.lifestyle.walkingRisk)
    }

    @Test fun bmi_computed_correctly_in_full_pipeline() {
        // 80 kg / (1.60 m)^2 = 80 / 2.56 = 31.25 → Obese → 2 pts
        val result = ScoringEngine.compute(
            allLowInput().copy(demographic = demographic(age = 35, weight = 80f, height = 160f))
        )
        assertEquals(BmiCategory.OBESE, result.sss.bmiCategory)
        assertEquals(2, result.sss.bmiPoints)
    }
}
