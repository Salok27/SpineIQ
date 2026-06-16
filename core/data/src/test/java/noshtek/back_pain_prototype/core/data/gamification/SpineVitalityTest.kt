package noshtek.back_pain_prototype.core.data.gamification

import noshtek.back_pain_prototype.core.scoring.model.BackPainRiskClassification
import noshtek.back_pain_prototype.core.scoring.model.BmiCategory
import noshtek.back_pain_prototype.core.scoring.model.RiskTier
import noshtek.back_pain_prototype.core.scoring.model.SleepQuality
import noshtek.back_pain_prototype.core.scoring.model.SssSeverityTier
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SpineVitalityTest {

    private fun clinical(
        severity: SssSeverityTier = SssSeverityTier.LOW,
        lifestyle: RiskTier = RiskTier.LOW,
        classification: BackPainRiskClassification = BackPainRiskClassification.LOW,
    ) = ClinicalInputs(
        severityTier = severity,
        lifestyleRisk = lifestyle,
        classification = classification,
        sittingRisk = RiskTier.LOW,
        walkingRisk = RiskTier.LOW,
        exerciseRisk = RiskTier.LOW,
        sleepRisk = RiskTier.LOW,
        sleepQuality = SleepQuality.GOOD,
        bmiCategory = BmiCategory.NORMAL,
    )

    private fun adherence(rate: Float = 0f, streak: Int = 0, checkedIn: Boolean = false) =
        SpineVitality.AdherenceWindow(rate, streak, checkedIn)

    @Test
    fun `vitality is always within 0 to 100`() {
        val best = SpineVitality.compute(clinical(), adherence(1f, 30, true))
        val worst = SpineVitality.compute(
            clinical(SssSeverityTier.SEVERE_HIGH_RISK, RiskTier.HIGH, BackPainRiskClassification.SEVERE_URGENT),
            adherence(0f, 0, false),
        )
        assertTrue(best in 0..100)
        assertTrue(worst in 0..100)
        assertTrue("best healthier than worst", best > worst)
    }

    @Test
    fun `best case is radiant, worst case is dim`() {
        val best = SpineVitality.compute(clinical(), adherence(1f, 30, true))
        val worst = SpineVitality.compute(
            clinical(SssSeverityTier.SEVERE_HIGH_RISK, RiskTier.HIGH, BackPainRiskClassification.SEVERE_URGENT),
            adherence(0f, 0, false),
        )
        assertEquals(100, best)
        assertEquals(VitalityStage.DIM, SpineVitality.stageFor(worst))
    }

    @Test
    fun `null clinical uses the neutral base, never zero`() {
        // clinicalBase 0.5 * 70 + 0 adherence = 35 — "dim but alive".
        assertEquals(35, SpineVitality.compute(null, adherence()))
    }

    @Test
    fun `better clinical tiers never lower vitality`() {
        val a = adherence(0.5f, 5, true)
        val low = SpineVitality.compute(clinical(), a)
        val mid = SpineVitality.compute(
            clinical(SssSeverityTier.MODERATE_SEVERE, RiskTier.MODERATE, BackPainRiskClassification.MODERATE), a,
        )
        val high = SpineVitality.compute(
            clinical(SssSeverityTier.SEVERE_HIGH_RISK, RiskTier.HIGH, BackPainRiskClassification.SEVERE_URGENT), a,
        )
        assertTrue(low >= mid)
        assertTrue(mid >= high)
    }

    @Test
    fun `more adherence never lowers vitality`() {
        val c = clinical(SssSeverityTier.MILD_MODERATE, RiskTier.MODERATE, BackPainRiskClassification.MILD_MODERATE)
        val none = SpineVitality.compute(c, adherence(0f, 0, false))
        val some = SpineVitality.compute(c, adherence(0.5f, 5, false))
        val lots = SpineVitality.compute(c, adherence(1f, 30, true))
        assertTrue(some >= none)
        assertTrue(lots >= some)
    }

    @Test
    fun `compute is deterministic`() {
        val c = clinical(SssSeverityTier.MILD_MODERATE, RiskTier.MODERATE, BackPainRiskClassification.MODERATE)
        val a = adherence(0.4f, 6, true)
        assertEquals(SpineVitality.compute(c, a), SpineVitality.compute(c, a))
    }

    @Test
    fun `stage thresholds are correct at boundaries`() {
        assertEquals(VitalityStage.DIM, SpineVitality.stageFor(0))
        assertEquals(VitalityStage.DIM, SpineVitality.stageFor(24))
        assertEquals(VitalityStage.FLICKER, SpineVitality.stageFor(25))
        assertEquals(VitalityStage.FLICKER, SpineVitality.stageFor(49))
        assertEquals(VitalityStage.STEADY, SpineVitality.stageFor(50))
        assertEquals(VitalityStage.STEADY, SpineVitality.stageFor(74))
        assertEquals(VitalityStage.BRIGHT, SpineVitality.stageFor(75))
        assertEquals(VitalityStage.BRIGHT, SpineVitality.stageFor(89))
        assertEquals(VitalityStage.RADIANT, SpineVitality.stageFor(90))
        assertEquals(VitalityStage.RADIANT, SpineVitality.stageFor(100))
    }
}
