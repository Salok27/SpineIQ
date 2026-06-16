package noshtek.back_pain_prototype.core.data.gamification

import noshtek.back_pain_prototype.core.scoring.model.BackPainRiskClassification
import noshtek.back_pain_prototype.core.scoring.model.BmiCategory
import noshtek.back_pain_prototype.core.scoring.model.RiskTier
import noshtek.back_pain_prototype.core.scoring.model.SleepQuality
import noshtek.back_pain_prototype.core.scoring.model.SssSeverityTier
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RitualCatalogTest {

    private fun clinical(
        severity: SssSeverityTier = SssSeverityTier.LOW,
        classification: BackPainRiskClassification = BackPainRiskClassification.LOW,
        sitting: RiskTier = RiskTier.LOW,
        walking: RiskTier = RiskTier.LOW,
        exercise: RiskTier = RiskTier.LOW,
        sleep: RiskTier = RiskTier.LOW,
        sleepQuality: SleepQuality = SleepQuality.GOOD,
        bmi: BmiCategory = BmiCategory.NORMAL,
    ) = ClinicalInputs(
        severityTier = severity,
        lifestyleRisk = RiskTier.LOW,
        classification = classification,
        sittingRisk = sitting,
        walkingRisk = walking,
        exerciseRisk = exercise,
        sleepRisk = sleep,
        sleepQuality = sleepQuality,
        bmiCategory = bmi,
    )

    @Test
    fun `all ritual ids are unique`() {
        val ids = RitualCatalog.ALL.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun `null clinical yields a sensible non-empty default set`() {
        val rituals = RitualCatalog.personalizedFor(null)
        assertTrue(rituals.isNotEmpty())
        assertTrue(rituals.any { it.id == "morning_stretch" })
    }

    @Test
    fun `personalized set size is always within 3 to 5`() {
        val cases = listOf(
            RitualCatalog.personalizedFor(null),
            RitualCatalog.personalizedFor(clinical()),
            RitualCatalog.personalizedFor(
                clinical(sitting = RiskTier.HIGH, walking = RiskTier.HIGH, exercise = RiskTier.HIGH, sleep = RiskTier.HIGH, bmi = BmiCategory.OBESE),
            ),
            RitualCatalog.personalizedFor(
                clinical(severity = SssSeverityTier.SEVERE_HIGH_RISK, classification = BackPainRiskClassification.SEVERE_URGENT),
            ),
        )
        cases.forEach { assertTrue("size=${it.size}", it.size in 3..5) }
    }

    @Test
    fun `severe tiers never prescribe high-load strength rituals`() {
        val severe = RitualCatalog.personalizedFor(
            clinical(
                severity = SssSeverityTier.SEVERE_HIGH_RISK,
                classification = BackPainRiskClassification.SEVERE_URGENT,
                exercise = RiskTier.HIGH,
            ),
        )
        assertTrue(severe.none { it.category == RitualCategory.STRENGTH })
    }

    @Test
    fun `baseline ritual is always present`() {
        listOf(
            RitualCatalog.personalizedFor(null),
            RitualCatalog.personalizedFor(clinical()),
            RitualCatalog.personalizedFor(clinical(sitting = RiskTier.HIGH)),
        ).forEach { assertTrue(it.any { r -> r.id == "morning_stretch" }) }
    }

    @Test
    fun `personalization is deterministic`() {
        val c = clinical(sitting = RiskTier.HIGH, sleep = RiskTier.HIGH)
        assertEquals(
            RitualCatalog.personalizedFor(c).map { it.id },
            RitualCatalog.personalizedFor(c).map { it.id },
        )
    }
}
