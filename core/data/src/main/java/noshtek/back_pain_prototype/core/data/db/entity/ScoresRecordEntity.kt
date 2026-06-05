package noshtek.back_pain_prototype.core.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import noshtek.back_pain_prototype.core.scoring.model.*

/**
 * Computed scores stored alongside the completed assessment (Section 15.2).
 *
 * All score components are stored so the report can be regenerated without
 * re-running the scoring engine. The [rawSSSScore] is the arithmetic sum
 * before any red-flag override, preserved for audit.
 */
@Entity(
    tableName = "scores_records",
    foreignKeys = [
        ForeignKey(
            entity = AssessmentRecordEntity::class,
            parentColumns = ["id"],
            childColumns = ["assessment_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ScoresRecordEntity(

    @PrimaryKey
    @ColumnInfo(name = "assessment_id")
    val assessmentId: String,

    // ── SSS components ────────────────────────────────────────────────────────

    /** Raw VAS input (0–10). */
    @ColumnInfo(name = "vas_score")
    val vasScore: Int,

    /** Derived SSS points (0, 1, or 2). */
    @ColumnInfo(name = "vas_points")
    val vasPoints: Int,

    /** Radiculopathy severity level (0–3). */
    @ColumnInfo(name = "radiculopathy_score")
    val radiculopathyScore: Int,

    /** ODI total: sum of 5 activity scores (0–10). */
    @ColumnInfo(name = "odi_score")
    val odiScore: Int,

    /** ODI SSS points (0, 1, or 2). */
    @ColumnInfo(name = "odi_points")
    val odiPoints: Int,

    /** Computed BMI value. */
    @ColumnInfo(name = "bmi_score")
    val bmiScore: Float,

    @ColumnInfo(name = "bmi_category")
    val bmiCategory: BmiCategory,

    /** BMI SSS points (0, 1, or 2). */
    @ColumnInfo(name = "bmi_points")
    val bmiPoints: Int,

    @ColumnInfo(name = "chronicity_points")
    val chronicityPoints: Int,

    /** 0 if no red flags; 11 if any red flag confirmed. */
    @ColumnInfo(name = "red_flag_score")
    val redFlagScore: Int,

    /** Arithmetic sum of all SSS components — stored for audit regardless of red-flag override. */
    @ColumnInfo(name = "raw_sss_score")
    val rawSSSScore: Int,

    /** Displayed score: rawSSSScore, or 11 when any red flag is present. */
    @ColumnInfo(name = "total_sss_score")
    val totalSSSScore: Int,

    @ColumnInfo(name = "sss_severity_tier")
    val sssSeverityTier: SssSeverityTier,

    // ── Lifestyle components ──────────────────────────────────────────────────

    @ColumnInfo(name = "age_group")
    val ageGroup: AgeGroup,

    @ColumnInfo(name = "sitting_risk")
    val sittingRisk: RiskTier,

    @ColumnInfo(name = "walking_risk")
    val walkingRisk: RiskTier,

    /** Post-modifier exercise risk tier. */
    @ColumnInfo(name = "exercise_risk")
    val exerciseRisk: RiskTier,

    /** Post-modifier sleep risk tier. */
    @ColumnInfo(name = "sleep_risk")
    val sleepRisk: RiskTier,

    @ColumnInfo(name = "sleep_quality_modifier")
    val sleepQualityModifier: SleepQuality,

    /** True if a high-impact/spine-loading exercise type was selected (Section 9.6). */
    @ColumnInfo(name = "exercise_type_modifier")
    val exerciseTypeModifier: Boolean,

    @ColumnInfo(name = "lifestyle_risk_tier")
    val lifestyleRiskTier: RiskTier,

    // ── Composite ─────────────────────────────────────────────────────────────

    @ColumnInfo(name = "back_pain_risk_classification")
    val backPainRiskClassification: BackPainRiskClassification,

    /** Epoch millis — when the scoring engine ran. */
    @ColumnInfo(name = "computed_at")
    val computedAt: Long
) {
    companion object {
        /** Maps a [ScoringResult] to a persistable entity. */
        fun fromScoringResult(
            assessmentId: String,
            result: noshtek.back_pain_prototype.core.scoring.model.ScoringResult,
            computedAt: Long
        ): ScoresRecordEntity = ScoresRecordEntity(
            assessmentId              = assessmentId,
            vasScore                  = result.sss.vasPoints,   // vasPoints doubles as the score indicator; raw VAS is in PainDataEntity
            vasPoints                 = result.sss.vasPoints,
            radiculopathyScore        = result.sss.radiculopathyPoints,
            odiScore                  = result.sss.odi.total,
            odiPoints                 = result.sss.odi.points,
            bmiScore                  = result.sss.bmiValue,
            bmiCategory               = result.sss.bmiCategory,
            bmiPoints                 = result.sss.bmiPoints,
            chronicityPoints          = result.sss.chronicityPoints,
            redFlagScore              = result.sss.redFlagScore,
            rawSSSScore               = result.sss.rawSSSScore,
            totalSSSScore             = result.sss.totalSSSScore,
            sssSeverityTier           = result.sss.severityTier,
            ageGroup                  = result.lifestyle.ageGroup,
            sittingRisk               = result.lifestyle.sittingRisk,
            walkingRisk               = result.lifestyle.walkingRisk,
            exerciseRisk              = result.lifestyle.exerciseRisk,
            sleepRisk                 = result.lifestyle.sleepRisk,
            sleepQualityModifier      = result.lifestyle.sleepQualityModifier,
            exerciseTypeModifier      = result.lifestyle.exerciseTypeModifierApplied,
            lifestyleRiskTier         = result.lifestyle.lifestyleRiskTier,
            backPainRiskClassification = result.backPainRiskClassification,
            computedAt                = computedAt
        )
    }
}
