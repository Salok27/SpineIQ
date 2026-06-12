package noshtek.back_pain_prototype.core.data.gamification

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class StreakLogicTest {

    private val today: LocalDate = LocalDate.of(2026, 6, 12)

    @Test
    fun `first ever activity starts a streak of 1`() {
        val advance = StreakLogic.advance(lastActivityDay = null, today = today, currentStreak = 0, longestStreak = 0)
        assertEquals(1, advance.newStreak)
        assertEquals(1, advance.newLongest)
        assertTrue(advance.changed)
        assertTrue(advance.crossedMilestones.isEmpty())
    }

    @Test
    fun `same-day activity is a no-op`() {
        val advance = StreakLogic.advance(today, today, currentStreak = 4, longestStreak = 6)
        assertFalse(advance.changed)
        assertEquals(4, advance.newStreak)
        assertEquals(6, advance.newLongest)
        assertTrue(advance.crossedMilestones.isEmpty())
    }

    @Test
    fun `consecutive day increments the streak`() {
        val advance = StreakLogic.advance(today.minusDays(1), today, currentStreak = 1, longestStreak = 1)
        assertEquals(2, advance.newStreak)
        assertEquals(2, advance.newLongest)
    }

    @Test
    fun `a gap resets the streak to 1 but keeps the longest`() {
        val advance = StreakLogic.advance(today.minusDays(3), today, currentStreak = 9, longestStreak = 9)
        assertEquals(1, advance.newStreak)
        assertEquals(9, advance.newLongest)
        assertTrue(advance.crossedMilestones.isEmpty())
    }

    @Test
    fun `crossing a milestone reports it`() {
        val advance = StreakLogic.advance(today.minusDays(1), today, currentStreak = 2, longestStreak = 2)
        assertEquals(3, advance.newStreak)
        assertEquals(listOf(3), advance.crossedMilestones)
    }

    @Test
    fun `milestones are only reported on the exact crossing`() {
        val advance = StreakLogic.advance(today.minusDays(1), today, currentStreak = 3, longestStreak = 3)
        assertEquals(4, advance.newStreak)
        assertTrue(advance.crossedMilestones.isEmpty())
    }

    @Test
    fun `all milestones exist in the economy table`() {
        for (days in listOf(3, 7, 14, 30)) {
            assertTrue(Economy.STREAK_MILESTONE_COINS.containsKey(days))
        }
    }

    @Test
    fun `effective streak is intact when last activity was today or yesterday`() {
        assertEquals(5, StreakLogic.effectiveStreak(today, today, 5))
        assertEquals(5, StreakLogic.effectiveStreak(today.minusDays(1), today, 5))
    }

    @Test
    fun `effective streak shows 0 once a day is missed`() {
        assertEquals(0, StreakLogic.effectiveStreak(today.minusDays(2), today, 5))
        assertEquals(0, StreakLogic.effectiveStreak(null, today, 5))
    }

    @Test
    fun `future lastActivityDay resets rather than increments`() {
        // Device clock moved backwards: treat as a fresh start, never crash.
        val advance = StreakLogic.advance(today.plusDays(2), today, currentStreak = 4, longestStreak = 4)
        assertEquals(1, advance.newStreak)
    }
}
