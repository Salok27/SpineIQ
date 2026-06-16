package noshtek.back_pain_prototype.core.data.gamification

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The reward-ledger primary key is the idempotency mechanism, so the dedupe-key
 * formats must be stable and distinct across event types, and the LIKE patterns
 * must match the keys they count.
 */
class AuraDedupeKeysTest {

    @Test
    fun `key formats are stable`() {
        assertEquals("step:a1:PAIN", DedupeKeys.step("a1", AssessmentStep.PAIN))
        assertEquals("complete:a1", DedupeKeys.completion("a1"))
        assertEquals("checkin:42", DedupeKeys.checkIn(42))
        assertEquals("ritual:daily_walk:42", DedupeKeys.ritual("daily_walk", 42))
        assertEquals("streak_milestone:7", DedupeKeys.streakMilestone(7))
        assertEquals("milestone:first_assessment", DedupeKeys.milestone("first_assessment"))
    }

    @Test
    fun `keys are distinct across event types`() {
        val keys = listOf(
            DedupeKeys.step("a", AssessmentStep.PAIN),
            DedupeKeys.completion("a"),
            DedupeKeys.checkIn(1),
            DedupeKeys.ritual("r", 1),
            DedupeKeys.streakMilestone(3),
            DedupeKeys.milestone("m"),
        )
        assertEquals(keys.size, keys.toSet().size)
    }

    @Test
    fun `step pattern matches step keys for the same section`() {
        val pattern = DedupeKeys.stepPattern(AssessmentStep.FUNCTIONAL)
        assertEquals("step:%:FUNCTIONAL", pattern)
        // The SQL LIKE wildcard '%' stands in for the assessment id.
        val key = DedupeKeys.step("any-assessment-id", AssessmentStep.FUNCTIONAL)
        assertTrue(key.startsWith("step:") && key.endsWith(":FUNCTIONAL"))
    }

    @Test
    fun `completion pattern is the prefix of every completion key`() {
        assertEquals("complete:%", DedupeKeys.COMPLETION_PATTERN)
        assertTrue(DedupeKeys.completion("xyz").startsWith("complete:"))
    }

    @Test
    fun `ritual keys vary by ritual and day`() {
        assertTrue(DedupeKeys.ritual("a", 1) != DedupeKeys.ritual("b", 1))
        assertTrue(DedupeKeys.ritual("a", 1) != DedupeKeys.ritual("a", 2))
    }
}
