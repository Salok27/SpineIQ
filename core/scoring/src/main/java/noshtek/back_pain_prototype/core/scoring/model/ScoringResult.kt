package noshtek.back_pain_prototype.core.scoring.model

/** ODI sub-result (Section 10.1 §3). */
data class OdiResult(
    /** Raw sum of 5 activity scores, 0–10. */
    val total: Int,
    /** SSS points derived from total (0, 1, or 2). */
    val points: Int
)

/** All SSS component scores and the computed totals. */
data class SssResult(
    val vasPoints: Int,
    val radiculopathyPoints: Int,
    val odi: OdiResult,
    val bmiValue: Float,
    val bmiCategory: BmiCategory,
    val bmiPoints: Int,
    val chronicityPoints: Int,
    /** 0 when no red flags; 11 when any red flag present. */
    val redFlagScore: Int,
    /** Arithmetic sum of all components (ignores red-flag override). Stored for audit. */
    val rawSSSScore: Int,
    /** Displayed score: equals rawSSSScore unless a red flag is present, in which case 11. */
    val totalSSSScore: Int,
    val severityTier: SssSeverityTier
)

/** Per-component lifestyle risk tiers and aggregate. */
data class LifestyleResult(
    val ageGroup: AgeGroup,
    val sittingRisk: RiskTier,
    val walkingRisk: RiskTier,
    /** Exercise frequency tier, before exercise-type modifier. */
    val exerciseBaseRisk: RiskTier,
    /** Exercise frequency tier, after high-impact type modifier applied (if any). */
    val exerciseRisk: RiskTier,
    val exerciseTypeModifierApplied: Boolean,
    /** Sleep hours tier, before quality modifier. */
    val sleepBaseRisk: RiskTier,
    /** Sleep hours tier, after sleep-quality modifier applied. */
    val sleepRisk: RiskTier,
    val sleepQualityModifier: SleepQuality,
    /** Aggregate of all four post-modifier component tiers. */
    val lifestyleRiskTier: RiskTier
)

/** Full output of one assessment computation. */
data class ScoringResult(
    val sss: SssResult,
    val lifestyle: LifestyleResult,
    val backPainRiskClassification: BackPainRiskClassification
)
