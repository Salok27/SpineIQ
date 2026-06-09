package noshtek.back_pain_prototype.core.scoring

import noshtek.back_pain_prototype.core.scoring.model.*

/** Pure functions for each SSS component (Section 10.1). */
internal object SssScorer {

    // ── Section 1: VAS Pain Score ─────────────────────────────────────────────

    fun vasPoints(vas: Int): Int = when {
        vas <= 3 -> 0
        vas <= 6 -> 1
        else     -> 2
    }

    // ── Section 2: Radiculopathy ──────────────────────────────────────────────

    fun radiculopathyPoints(severity: RadiculopathySeverity): Int = severity.points

    // ── Section 3: SSS Modified ODI ───────────────────────────────────────────

    fun odiResult(functional: FunctionalInput): OdiResult {
        val total = functional.walking.points +
                    functional.sitting.points +
                    functional.standing.points +
                    functional.sleep.points +
                    functional.dailyActivities.points
        val points = when {
            total <= 2 -> 0
            total <= 5 -> 1
            else       -> 2
        }
        return OdiResult(total = total, points = points)
    }

    // ── Section 4: BMI Mechanical Load ───────────────────────────────────────

    fun bmi(weightKg: Float, heightCm: Float): Float {
        if (heightCm <= 0f) return 0f   // guard invalid input: avoids divide-by-zero → Infinity leaking into scores/UI
        val heightM = heightCm / 100f
        return weightKg / (heightM * heightM)
    }

    fun bmiCategory(bmi: Float): BmiCategory = when {
        bmi < 18.5f -> BmiCategory.UNDERWEIGHT
        bmi < 25.0f -> BmiCategory.NORMAL
        bmi < 30.0f -> BmiCategory.OVERWEIGHT
        else        -> BmiCategory.OBESE
    }

    fun bmiPoints(bmi: Float): Int = when {
        bmi < 25.0f -> 0
        bmi < 30.0f -> 1
        else        -> 2
    }

    // ── Section 5: Chronicity ─────────────────────────────────────────────────

    fun chronicityPoints(duration: PainDuration): Int = when (duration) {
        PainDuration.ACUTE    -> 0
        PainDuration.SUBACUTE -> 1
        PainDuration.CHRONIC  -> 2
    }

    // ── Section 6: Red Flag ───────────────────────────────────────────────────

    fun redFlagScore(hasRedFlag: Boolean): Int = if (hasRedFlag) 11 else 0

    // ── Aggregate ─────────────────────────────────────────────────────────────

    fun severityTier(totalSSSScore: Int): SssSeverityTier = when {
        totalSSSScore <= 3  -> SssSeverityTier.LOW
        totalSSSScore <= 6  -> SssSeverityTier.MILD_MODERATE
        totalSSSScore <= 9  -> SssSeverityTier.MODERATE_SEVERE
        else                -> SssSeverityTier.SEVERE_HIGH_RISK
    }

    fun compute(
        demographic: noshtek.back_pain_prototype.core.scoring.model.DemographicInput,
        pain: PainInput,
        functional: FunctionalInput,
        hasRedFlag: Boolean
    ): SssResult {
        val bmiValue   = bmi(demographic.weightKg, demographic.heightCm)
        val bmiCat     = bmiCategory(bmiValue)
        val bmiPts     = bmiPoints(bmiValue)
        val vasPts     = vasPoints(pain.vasScore)
        val radioPts   = radiculopathyPoints(pain.radiculopathy)
        val odi        = odiResult(functional)
        val chronPts   = chronicityPoints(pain.painDuration)
        val rfScore    = redFlagScore(hasRedFlag)
        val rawSSS     = vasPts + radioPts + odi.points + bmiPts + chronPts
        val totalSSS   = if (hasRedFlag) 11 else rawSSS

        return SssResult(
            vasPoints           = vasPts,
            radiculopathyPoints = radioPts,
            odi                 = odi,
            bmiValue            = bmiValue,
            bmiCategory         = bmiCat,
            bmiPoints           = bmiPts,
            chronicityPoints    = chronPts,
            redFlagScore        = rfScore,
            rawSSSScore         = rawSSS,
            totalSSSScore       = totalSSS,
            severityTier        = severityTier(totalSSS)
        )
    }
}
