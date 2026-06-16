package noshtek.back_pain_prototype.core.data.gamification

import noshtek.back_pain_prototype.core.scoring.model.BackPainRiskClassification
import noshtek.back_pain_prototype.core.scoring.model.BmiCategory
import noshtek.back_pain_prototype.core.scoring.model.RiskTier
import noshtek.back_pain_prototype.core.scoring.model.SleepQuality
import noshtek.back_pain_prototype.core.scoring.model.SssSeverityTier
import kotlin.math.roundToInt

/** Visualization stages for the Living Spine (drives glow brightness + posture). */
enum class VitalityStage { DIM, FLICKER, STEADY, BRIGHT, RADIANT }

/**
 * The minimal clinical picture the engagement layer reads, mapped from the
 * latest stored scores. Pure (enums only) — no Room/DB types — so the vitality
 * and ritual rules stay as testable as the scoring engine.
 */
data class ClinicalInputs(
    val severityTier: SssSeverityTier,
    val lifestyleRisk: RiskTier,
    val classification: BackPainRiskClassification,
    val sittingRisk: RiskTier,
    val walkingRisk: RiskTier,
    val exerciseRisk: RiskTier,
    val sleepRisk: RiskTier,
    val sleepQuality: SleepQuality,
    val bmiCategory: BmiCategory,
)

/**
 * Pure, deterministic Spine Vitality (0..100) — mirrors the ScoringEngine
 * style (side-effect-free, inputs in, number out). Vitality is dominated by the
 * user's real clinical picture (~70%) and polished by recent habit adherence
 * (~30%), so the Living Spine heals as the user genuinely improves AND stays
 * consistent, but habits alone can never fake a healthy spine.
 *
 * Monotonic: better clinical tiers and higher adherence never lower vitality.
 */
object SpineVitality {

    data class AdherenceWindow(
        /** 0..1 — personalized rituals completed over the last 7 days. */
        val ritualCompletionRate7d: Float,
        val effectiveStreakDays: Int,
        val checkedInToday: Boolean,
    )

    const val CLINICAL_WEIGHT = 70f
    const val ADHERENCE_WEIGHT = 30f

    /** A new user (no assessment yet) sits at a neutral midpoint — "dim but alive", never 0. */
    const val NEUTRAL_BASE = 0.5f

    /** The resulting vitality (0..100) for a brand-new user with no activity yet. */
    const val NEUTRAL_BASE_VITALITY = 50

    fun compute(clinical: ClinicalInputs?, adherence: AdherenceWindow): Int {
        val raw = clinicalBase(clinical) * CLINICAL_WEIGHT + adherenceScore(adherence) * ADHERENCE_WEIGHT
        return raw.roundToInt().coerceIn(0, 100)
    }

    fun stageFor(vitality: Int): VitalityStage = when {
        vitality < 25 -> VitalityStage.DIM
        vitality < 50 -> VitalityStage.FLICKER
        vitality < 75 -> VitalityStage.STEADY
        vitality < 90 -> VitalityStage.BRIGHT
        else -> VitalityStage.RADIANT
    }

    /** 0..1 clinical health, anchored on the composite classification with SSS + lifestyle detail. */
    fun clinicalBase(clinical: ClinicalInputs?): Float {
        if (clinical == null) return NEUTRAL_BASE
        val byClass = when (clinical.classification) {
            BackPainRiskClassification.LOW           -> 1.00f
            BackPainRiskClassification.LOW_MODERATE  -> 0.90f
            BackPainRiskClassification.MILD_MODERATE -> 0.75f
            BackPainRiskClassification.MODERATE      -> 0.60f
            BackPainRiskClassification.MODERATE_HIGH -> 0.45f
            BackPainRiskClassification.HIGH          -> 0.30f
            BackPainRiskClassification.SEVERE_URGENT -> 0.12f
        }
        val bySss = when (clinical.severityTier) {
            SssSeverityTier.LOW              -> 1.00f
            SssSeverityTier.MILD_MODERATE    -> 0.72f
            SssSeverityTier.MODERATE_SEVERE  -> 0.45f
            SssSeverityTier.SEVERE_HIGH_RISK -> 0.18f
        }
        val byLifestyle = when (clinical.lifestyleRisk) {
            RiskTier.LOW      -> 1.00f
            RiskTier.MODERATE -> 0.60f
            RiskTier.HIGH     -> 0.30f
        }
        return (byClass * 0.5f + bySss * 0.3f + byLifestyle * 0.2f).coerceIn(0f, 1f)
    }

    /** 0..1 adherence: rituals dominate; streak + today's check-in add polish. */
    fun adherenceScore(a: AdherenceWindow): Float {
        val ritual = a.ritualCompletionRate7d.coerceIn(0f, 1f)
        val streak = (a.effectiveStreakDays / 14f).coerceIn(0f, 1f)
        val checkIn = if (a.checkedInToday) 1f else 0f
        return (ritual * 0.6f + streak * 0.3f + checkIn * 0.1f).coerceIn(0f, 1f)
    }
}
