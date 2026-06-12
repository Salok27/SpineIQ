package noshtek.back_pain_prototype.core.data.gamification

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EconomyCatalogInvariantsTest {

    @Test
    fun `full assessment totals match the advertised reward`() {
        assertEquals(100, Economy.COINS_PER_FULL_ASSESSMENT)
        assertEquals(200, Economy.XP_PER_FULL_ASSESSMENT)
        assertEquals(AssessmentStep.entries.size, Economy.STEPS_PER_ASSESSMENT)
    }

    @Test
    fun `streak milestone bonuses grow with streak length`() {
        val sorted = Economy.STREAK_MILESTONE_COINS.entries.sortedBy { it.key }
        for (i in 1 until sorted.size) {
            assertTrue(sorted[i].value > sorted[i - 1].value)
        }
    }

    @Test
    fun `avatar item ids are unique`() {
        val ids = AvatarCatalog.ALL.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun `avatar prices are non-negative and defaults are free`() {
        for (item in AvatarCatalog.ALL) {
            assertTrue("${item.id} price", item.priceCoins >= 0)
            if (item.isDefault) assertEquals("${item.id} default must be free", 0, item.priceCoins)
        }
    }

    @Test
    fun `hair tops and bottoms have exactly one default each, accessories none`() {
        val defaultsByCategory = AvatarCatalog.ALL.filter { it.isDefault }.groupBy { it.category }
        assertEquals(1, defaultsByCategory[AvatarCategory.HAIR]?.size)
        assertEquals(1, defaultsByCategory[AvatarCategory.TOPS]?.size)
        assertEquals(1, defaultsByCategory[AvatarCategory.BOTTOMS]?.size)
        assertTrue(defaultsByCategory[AvatarCategory.ACCESSORIES].isNullOrEmpty())
        assertEquals(AvatarCatalog.DEFAULTS.values.toSet(), AvatarCatalog.ALL.filter { it.isDefault }.toSet())
    }

    @Test
    fun `non-default items are purchasable with a positive price`() {
        for (item in AvatarCatalog.ALL.filterNot { it.isDefault }) {
            assertTrue("${item.id} must cost coins", item.priceCoins > 0)
        }
    }

    @Test
    fun `dedupe keys are stable and distinct across reward types`() {
        val keys = listOf(
            DedupeKeys.step("a1", AssessmentStep.PAIN),
            DedupeKeys.completion("a1"),
            DedupeKeys.checkIn(20_000),
            DedupeKeys.streakMilestone(3),
            DedupeKeys.achievement("first_assessment"),
            DedupeKeys.purchase("top_hoodie"),
        )
        assertEquals(keys.size, keys.toSet().size)
        assertEquals("step:a1:PAIN", DedupeKeys.step("a1", AssessmentStep.PAIN))
        // The LIKE pattern used for achievement counting must match its key format.
        assertTrue(DedupeKeys.step("a1", AssessmentStep.PAIN).startsWith("step:"))
        assertTrue(DedupeKeys.stepPattern(AssessmentStep.PAIN).endsWith(":PAIN"))
    }
}
