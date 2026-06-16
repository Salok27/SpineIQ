package noshtek.back_pain_prototype.core.data.gamification

import noshtek.back_pain_prototype.core.scoring.model.BackPainRiskClassification
import noshtek.back_pain_prototype.core.scoring.model.BmiCategory
import noshtek.back_pain_prototype.core.scoring.model.RiskTier
import noshtek.back_pain_prototype.core.scoring.model.SleepQuality
import noshtek.back_pain_prototype.core.scoring.model.SssSeverityTier

enum class RitualCategory { MOVEMENT, POSTURE, STRENGTH, RECOVERY, HYDRATION }

/**
 * A daily micro-habit. [matches] selects the ritual from the user's real risk
 * factors. Persistence stores only completion facts keyed by [id] (one row per
 * (ritualId, day)), so the catalog can grow without schema changes.
 */
data class Ritual(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: RitualCategory,
    val basePriority: Int,
    val matches: (ClinicalInputs) -> Boolean,
)

/** A ritual paired with whether it has been completed today. */
data class RitualStatus(val ritual: Ritual, val done: Boolean)

/**
 * Static ritual catalog + a pure, deterministic personalization rule that maps
 * a user's clinical picture to a small daily set. Same input → same set, so the
 * list is stable across recomposition and process death.
 */
object RitualCatalog {

    private const val MIN_RITUALS = 3
    private const val MAX_RITUALS = 5
    private const val BASELINE_ID = "morning_stretch"
    private val DEFAULT_SET = setOf(BASELINE_ID, "daily_walk", "posture_check", "breathe")

    val ALL: List<Ritual> = listOf(
        Ritual(BASELINE_ID, "Morning stretch", "2 min of gentle mobility", RitualCategory.MOVEMENT, 100) { true },
        Ritual("posture_check", "Posture reset", "Roll your shoulders, lengthen up", RitualCategory.POSTURE, 92) { it.sittingRisk != RiskTier.LOW },
        Ritual("stand_break", "Stand & move", "Break up a long sit", RitualCategory.MOVEMENT, 84) { it.sittingRisk == RiskTier.HIGH },
        Ritual("daily_walk", "Take a walk", "10–15 minutes outdoors", RitualCategory.MOVEMENT, 88) { it.walkingRisk != RiskTier.LOW },
        Ritual("mobility_flow", "Mobility flow", "Slow, spine-friendly movement", RitualCategory.RECOVERY, 80) { it.exerciseRisk != RiskTier.LOW },
        Ritual("core_activation", "Core activation", "Gentle core engagement", RitualCategory.STRENGTH, 72) { it.exerciseRisk != RiskTier.LOW },
        Ritual("sleep_winddown", "Sleep wind-down", "Ease into restful sleep", RitualCategory.RECOVERY, 78) {
            it.sleepRisk != RiskTier.LOW || it.sleepQuality == SleepQuality.POOR || it.sleepQuality == SleepQuality.FAIR
        },
        Ritual("hydrate", "Hydrate", "Drink a glass of water", RitualCategory.HYDRATION, 60) {
            it.bmiCategory == BmiCategory.OVERWEIGHT || it.bmiCategory == BmiCategory.OBESE
        },
        Ritual("breathe", "Breathe", "2 minutes of calm breathing", RitualCategory.RECOVERY, 64) { true },
    )

    fun byId(id: String): Ritual? = ALL.firstOrNull { it.id == id }

    /**
     * The personalized daily set (3..5 rituals). Deterministic. At severe tiers,
     * higher-load STRENGTH rituals are never prescribed (safety). The baseline
     * ritual is always present so a new user (null clinical) still gets a
     * sensible set.
     */
    fun personalizedFor(clinical: ClinicalInputs?): List<Ritual> {
        if (clinical == null) {
            return ALL.filter { it.id in DEFAULT_SET }.sortedByDescending { it.basePriority }
        }
        val severe = isSevere(clinical)
        val candidates = ALL.filter { !(severe && it.category == RitualCategory.STRENGTH) }
        val matched = candidates.filter { it.matches(clinical) }.ifEmpty { candidates }
        val ranked = matched.sortedByDescending { it.basePriority }
        val withBaseline =
            if (ranked.any { it.id == BASELINE_ID }) ranked
            else listOfNotNull(byId(BASELINE_ID)) + ranked
        val result = withBaseline.distinctBy { it.id }.take(MAX_RITUALS)
        return if (result.size >= MIN_RITUALS) result
        else (result + candidates.sortedByDescending { it.basePriority })
            .distinctBy { it.id }.take(MIN_RITUALS)
    }

    private fun isSevere(c: ClinicalInputs): Boolean =
        c.severityTier == SssSeverityTier.SEVERE_HIGH_RISK ||
            c.classification == BackPainRiskClassification.SEVERE_URGENT ||
            c.classification == BackPainRiskClassification.HIGH
}
