package noshtek.back_pain_prototype.core.scoring

import noshtek.back_pain_prototype.core.scoring.model.*

/**
 * Top-level entry point for SpineIQ assessment scoring.
 *
 * All functions are pure (no side effects, no I/O). The engine can be called
 * from any thread without synchronisation.
 *
 * Usage:
 *   val result = ScoringEngine.compute(input)
 */
object ScoringEngine {

    fun compute(input: AssessmentInput): ScoringResult {
        val group      = LifestyleScorer.ageGroup(input.demographic.ageYears)
        val sss        = SssScorer.compute(input.demographic, input.pain, input.functional, input.hasRedFlag)
        val lifestyle  = LifestyleScorer.compute(input.lifestyle, group)
        val composite  = classifyBackPainRisk(sss.totalSSSScore, lifestyle.lifestyleRiskTier, input.hasRedFlag)

        return ScoringResult(
            sss                       = sss,
            lifestyle                 = lifestyle,
            backPainRiskClassification = composite
        )
    }

    // ── SSS × Lifestyle combination matrix (Section 10.3) ────────────────────

    internal fun classifyBackPainRisk(
        totalSSSScore: Int,
        lifestyleRisk: RiskTier,
        hasRedFlag: Boolean
    ): BackPainRiskClassification {
        if (hasRedFlag || totalSSSScore >= 10) return BackPainRiskClassification.SEVERE_URGENT
        return when {
            totalSSSScore >= 7 -> BackPainRiskClassification.HIGH
            totalSSSScore >= 4 -> when (lifestyleRisk) {
                RiskTier.LOW      -> BackPainRiskClassification.MILD_MODERATE
                RiskTier.MODERATE -> BackPainRiskClassification.MODERATE
                RiskTier.HIGH     -> BackPainRiskClassification.MODERATE_HIGH
            }
            else -> when (lifestyleRisk) {   // 0–3
                RiskTier.LOW      -> BackPainRiskClassification.LOW
                RiskTier.MODERATE -> BackPainRiskClassification.LOW_MODERATE
                RiskTier.HIGH     -> BackPainRiskClassification.MODERATE
            }
        }
    }
}
