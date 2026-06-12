package noshtek.back_pain_prototype.core.data.gamification

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LevelTableTest {

    @Test
    fun `levels are sorted by ascending threshold starting at zero`() {
        assertEquals(0, LevelTable.LEVELS.first().xpThreshold)
        assertEquals(LevelTable.LEVELS.sortedBy { it.xpThreshold }, LevelTable.LEVELS)
        assertEquals(LevelTable.LEVELS.map { it.number }, (1..LevelTable.LEVELS.size).toList())
    }

    @Test
    fun `zero xp is level 1 Beginner`() {
        val level = LevelTable.levelFor(0)
        assertEquals(1, level.number)
        assertEquals("Beginner", level.name)
    }

    @Test
    fun `exact threshold boundary promotes to the new level`() {
        for (level in LevelTable.LEVELS) {
            assertEquals(level, LevelTable.levelFor(level.xpThreshold))
        }
        // One XP below a threshold stays on the previous level.
        for (i in 1 until LevelTable.LEVELS.size) {
            assertEquals(LevelTable.LEVELS[i - 1], LevelTable.levelFor(LevelTable.LEVELS[i].xpThreshold - 1))
        }
    }

    @Test
    fun `negative xp is treated as zero`() {
        assertEquals(LevelTable.LEVELS.first(), LevelTable.levelFor(-50))
        assertEquals(0, LevelTable.xpIntoLevel(-50))
    }

    @Test
    fun `progress at max level is exactly 1`() {
        val maxThreshold = LevelTable.LEVELS.last().xpThreshold
        assertEquals(1f, LevelTable.progressToNext(maxThreshold), 0f)
        assertEquals(1f, LevelTable.progressToNext(maxThreshold + 99_999), 0f)
        assertNull(LevelTable.nextLevel(maxThreshold))
        assertNull(LevelTable.xpForNextLevel(maxThreshold))
    }

    @Test
    fun `progress halfway through a band is one half`() {
        // Level 1 band is 0..100.
        assertEquals(0.5f, LevelTable.progressToNext(50), 1e-6f)
        assertEquals(50, LevelTable.xpIntoLevel(50))
        assertEquals(100, LevelTable.xpForNextLevel(50))
    }

    @Test
    fun `progress is always within 0 and 1`() {
        for (xp in 0..6000 step 7) {
            val p = LevelTable.progressToNext(xp)
            assertTrue("progress $p out of range at xp=$xp", p in 0f..1f)
        }
    }
}
