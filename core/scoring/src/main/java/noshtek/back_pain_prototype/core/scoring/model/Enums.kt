package noshtek.back_pain_prototype.core.scoring.model

enum class AgeGroup { YOUNG_ADULT, MID_ADULT, PRE_SENIOR, SENIOR }

enum class BmiCategory { UNDERWEIGHT, NORMAL, OVERWEIGHT, OBESE }

enum class PainDuration {
    /** < 3 weeks */
    ACUTE,
    /** 3–6 weeks */
    SUBACUTE,
    /** > 6 weeks */
    CHRONIC
}

/** 0–3 points per SSS Section 2 */
enum class RadiculopathySeverity(val points: Int) {
    NONE(0),
    MILD(1),
    MODERATE(2),
    SEVERE(3)
}

/** 0–2 points each, per SSS Modified ODI */
enum class FunctionalLevel(val points: Int) {
    NORMAL(0),
    MILD_DIFFICULTY(1),
    SEVERE_DIFFICULTY(2)
}

enum class SleepQuality { POOR, FAIR, GOOD, EXCELLENT }

enum class ExerciseType {
    WALKING, RUNNING, CYCLING, SWIMMING, GYM_WEIGHTS, YOGA_PILATES, OTHER, NONE;

    /** True for types the spec classifies as high-impact or spine-loading. */
    val isHighImpact: Boolean
        get() = this == RUNNING || this == GYM_WEIGHTS || this == OTHER
}

/** Three-tier risk used for each lifestyle component and the aggregate. */
enum class RiskTier {
    LOW, MODERATE, HIGH;

    fun stepTowardHigh(): RiskTier = when (this) {
        LOW -> MODERATE
        MODERATE -> HIGH
        HIGH -> HIGH
    }

    fun stepTowardLow(): RiskTier = when (this) {
        HIGH -> MODERATE
        MODERATE -> LOW
        LOW -> LOW
    }
}

enum class SssSeverityTier {
    LOW,
    MILD_MODERATE,
    MODERATE_SEVERE,
    SEVERE_HIGH_RISK
}

/** SSS × Lifestyle combination matrix output (Section 10.3). */
enum class BackPainRiskClassification {
    LOW,
    LOW_MODERATE,
    MILD_MODERATE,
    MODERATE,
    MODERATE_HIGH,
    HIGH,
    SEVERE_URGENT
}
