package noshtek.back_pain_prototype.core.data.gamification

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MilestoneCatalogTest {

    private val zero = MilestoneContext()
    private val maxed = MilestoneContext(
        completedAssessmentCount = 100,
        currentStreak = 60,
        longestStreak = 60,
        checkInCount = 500,
        totalRitualCompletions = 500,
        peakVitality = 100,
    )

    @Test
    fun `all milestone ids are unique`() {
        val ids = MilestoneCatalog.ALL.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun `nothing unlocks at zero, everything unlocks when maxed`() {
        assertTrue(MilestoneCatalog.ALL.none { it.isUnlocked(zero) })
        assertTrue(MilestoneCatalog.ALL.all { it.isUnlocked(maxed) })
    }

    @Test
    fun `predicates are monotonic in their drivers`() {
        // A maxed context must unlock every milestone that a zero context unlocks (vacuously true here),
        // and must never *lose* an unlock relative to a partial context.
        val partial = MilestoneContext(
            completedAssessmentCount = 3,
            longestStreak = 7,
            checkInCount = 10,
            totalRitualCompletions = 25,
            peakVitality = 75,
        )
        MilestoneCatalog.ALL.forEach { m ->
            if (m.isUnlocked(partial)) {
                assertTrue("${m.id} regressed when context grew", m.isUnlocked(maxed))
            }
        }
    }

    @Test
    fun `progress hints stay within 0 to 1`() {
        val contexts = listOf(zero, maxed, MilestoneContext(completedAssessmentCount = 2, peakVitality = 60, longestStreak = 5))
        MilestoneCatalog.ALL.forEach { m ->
            m.progress?.let { p ->
                contexts.forEach { ctx ->
                    val v = p(ctx)
                    assertTrue("${m.id} progress out of range: $v", v in 0f..1f)
                }
            }
        }
    }

    @Test
    fun `first step unlocks after one assessment`() {
        val m = MilestoneCatalog.byId("first_assessment")!!
        assertFalse(m.isUnlocked(zero))
        assertTrue(m.isUnlocked(MilestoneContext(completedAssessmentCount = 1)))
    }
}
