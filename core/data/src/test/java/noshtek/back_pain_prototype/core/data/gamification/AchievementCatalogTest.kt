package noshtek.back_pain_prototype.core.data.gamification

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AchievementCatalogTest {

    @Test
    fun `achievement ids are unique`() {
        val ids = AchievementCatalog.ALL.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun `rewards are non-negative`() {
        for (a in AchievementCatalog.ALL) {
            assertTrue("${a.id} coinReward", a.coinReward >= 0)
            assertTrue("${a.id} xpReward", a.xpReward >= 0)
        }
    }

    @Test
    fun `nothing unlocks on a fresh context`() {
        val fresh = AchievementContext()
        for (a in AchievementCatalog.ALL) {
            assertFalse("${a.id} unlocked on fresh context", a.isUnlocked(fresh))
        }
    }

    @Test
    fun `first assessment unlocks on one completion`() {
        val ctx = AchievementContext(completedAssessmentCount = 1)
        assertTrue(AchievementCatalog.byId("first_assessment")!!.isUnlocked(ctx))
        assertFalse(AchievementCatalog.byId("assessment_master")!!.isUnlocked(ctx))
        assertFalse(AchievementCatalog.byId("recovery_champion")!!.isUnlocked(ctx))
    }

    @Test
    fun `assessment count tiers unlock at 5 and 10`() {
        assertTrue(AchievementCatalog.byId("assessment_master")!!.isUnlocked(AchievementContext(completedAssessmentCount = 5)))
        assertFalse(AchievementCatalog.byId("recovery_champion")!!.isUnlocked(AchievementContext(completedAssessmentCount = 9)))
        assertTrue(AchievementCatalog.byId("recovery_champion")!!.isUnlocked(AchievementContext(completedAssessmentCount = 10)))
    }

    @Test
    fun `section achievements track their own step only`() {
        val ctx = AchievementContext(stepCompletionCounts = mapOf(AssessmentStep.PAIN to 3))
        assertTrue(AchievementCatalog.byId("pain_detective")!!.isUnlocked(ctx))
        assertFalse(AchievementCatalog.byId("functional_expert")!!.isUnlocked(ctx))
        assertFalse(AchievementCatalog.byId("red_flag_aware")!!.isUnlocked(ctx))
    }

    @Test
    fun `streak achievements use the longest streak so a reset cannot relock them`() {
        val ctx = AchievementContext(currentStreak = 0, longestStreak = 7)
        assertTrue(AchievementCatalog.byId("streak_3")!!.isUnlocked(ctx))
        assertTrue(AchievementCatalog.byId("streak_7")!!.isUnlocked(ctx))
        assertFalse(AchievementCatalog.byId("streak_30")!!.isUnlocked(ctx))
    }

    @Test
    fun `progress hints stay within 0 and 1 and reach 1 when unlocked`() {
        val maxed = AchievementContext(
            completedAssessmentCount = 100,
            currentStreak = 100,
            longestStreak = 100,
            checkInCount = 100,
            stepCompletionCounts = AssessmentStep.entries.associateWith { 100 },
            totalXp = 100_000,
            ownedItemCount = 100,
        )
        for (a in AchievementCatalog.ALL) {
            val fresh = a.progress?.invoke(AchievementContext()) ?: continue
            assertTrue("${a.id} fresh progress $fresh", fresh in 0f..1f)
            assertEquals("${a.id} maxed progress", 1f, a.progress!!.invoke(maxed), 0f)
        }
    }
}
