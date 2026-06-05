package noshtek.back_pain_prototype.core.scoring

import noshtek.back_pain_prototype.core.scoring.model.*
import noshtek.back_pain_prototype.core.scoring.thresholds.AgeThresholds

/** Pure functions for lifestyle component scoring (Section 10.2). */
internal object LifestyleScorer {

    // ── Age group ─────────────────────────────────────────────────────────────

    fun ageGroup(ageYears: Int): AgeGroup = when {
        ageYears <= 30 -> AgeGroup.YOUNG_ADULT
        ageYears <= 45 -> AgeGroup.MID_ADULT
        ageYears <= 60 -> AgeGroup.PRE_SENIOR
        else           -> AgeGroup.SENIOR
    }

    // ── Component scorers ─────────────────────────────────────────────────────

    fun sittingRisk(hoursPerDay: Float, group: AgeGroup): RiskTier {
        val t = AgeThresholds.sitting(group)
        return when {
            hoursPerDay < t.lowMaxExclusive    -> RiskTier.LOW
            hoursPerDay >= t.highMinInclusive  -> RiskTier.HIGH
            else                               -> RiskTier.MODERATE
        }
    }

    fun walkingRisk(minutesPerDay: Float, group: AgeGroup): RiskTier {
        val t = AgeThresholds.walking(group)
        return when {
            minutesPerDay >= t.lowMinInclusive -> RiskTier.LOW
            minutesPerDay < t.highMaxExclusive -> RiskTier.HIGH
            else                               -> RiskTier.MODERATE
        }
    }

    fun exerciseFrequencyRisk(daysPerWeek: Int): RiskTier = when {
        daysPerWeek >= 3 -> RiskTier.LOW
        daysPerWeek >= 1 -> RiskTier.MODERATE
        else             -> RiskTier.HIGH
    }

    /**
     * Sleep hours risk (before quality modifier).
     * Spec bands: 7–8 hrs = Low; 5–6 hrs = Moderate; < 5 hrs = High.
     * Values > 8 are treated as Low (spec does not penalise oversleeping).
     */
    fun sleepHoursRisk(hoursPerNight: Float): RiskTier = when {
        hoursPerNight < 5f -> RiskTier.HIGH
        hoursPerNight < 7f -> RiskTier.MODERATE
        else               -> RiskTier.LOW
    }

    // ── Modifiers ─────────────────────────────────────────────────────────────

    /**
     * Applies the sleep-quality modifier to the sleep-hours risk tier (Section 10.2).
     *
     * Excellent → improve one step toward Low (MODERATE becomes LOW).
     * Good      → no change.
     * Fair/Poor → one step toward High (LOW→MODERATE, MODERATE→HIGH).
     */
    fun applySleepQualityModifier(baseRisk: RiskTier, quality: SleepQuality): RiskTier =
        when (quality) {
            SleepQuality.EXCELLENT -> baseRisk.stepTowardLow()
            SleepQuality.GOOD      -> baseRisk
            SleepQuality.FAIR      -> baseRisk.stepTowardHigh()
            SleepQuality.POOR      -> baseRisk.stepTowardHigh()
        }

    /**
     * True if the selected exercise types include at least one high-impact/spine-loading type
     * and the selection is not exclusively [ExerciseType.NONE].
     */
    fun exerciseTypeModifierApplies(types: Set<ExerciseType>): Boolean =
        types.any { it.isHighImpact } && types != setOf(ExerciseType.NONE)

    /** Applies high-impact exercise modifier: adjusts exercise risk one step toward High. */
    fun applyExerciseTypeModifier(baseRisk: RiskTier, types: Set<ExerciseType>): RiskTier =
        if (exerciseTypeModifierApplies(types)) baseRisk.stepTowardHigh() else baseRisk

    // ── Aggregate lifestyle risk ──────────────────────────────────────────────

    /**
     * Combines four post-modifier component tiers into a single lifestyle risk tier.
     *
     * High Risk    → any component is High
     * Moderate Risk → one or more components are Moderate, none are High
     * Low Risk     → all components are Low
     */
    fun aggregateLifestyleRisk(
        sitting: RiskTier,
        walking: RiskTier,
        exercise: RiskTier,
        sleep: RiskTier
    ): RiskTier {
        val components = listOf(sitting, walking, exercise, sleep)
        return when {
            components.any { it == RiskTier.HIGH }     -> RiskTier.HIGH
            components.any { it == RiskTier.MODERATE } -> RiskTier.MODERATE
            else                                        -> RiskTier.LOW
        }
    }

    // ── Top-level compute ─────────────────────────────────────────────────────

    fun compute(lifestyle: LifestyleInput, group: AgeGroup): LifestyleResult {
        val sittingRisk  = sittingRisk(lifestyle.sittingHoursPerDay, group)
        val walkingRisk  = walkingRisk(lifestyle.walkingMinutesPerDay, group)

        val exerciseBase = exerciseFrequencyRisk(lifestyle.exerciseDaysPerWeek)
        val modApplied   = exerciseTypeModifierApplies(lifestyle.exerciseTypes)
        val exerciseRisk = applyExerciseTypeModifier(exerciseBase, lifestyle.exerciseTypes)

        val sleepBase    = sleepHoursRisk(lifestyle.sleepHoursPerNight)
        val sleepRisk    = applySleepQualityModifier(sleepBase, lifestyle.sleepQuality)

        val aggregate    = aggregateLifestyleRisk(sittingRisk, walkingRisk, exerciseRisk, sleepRisk)

        return LifestyleResult(
            ageGroup                   = group,
            sittingRisk                = sittingRisk,
            walkingRisk                = walkingRisk,
            exerciseBaseRisk           = exerciseBase,
            exerciseRisk               = exerciseRisk,
            exerciseTypeModifierApplied = modApplied,
            sleepBaseRisk              = sleepBase,
            sleepRisk                  = sleepRisk,
            sleepQualityModifier       = lifestyle.sleepQuality,
            lifestyleRiskTier          = aggregate
        )
    }
}
