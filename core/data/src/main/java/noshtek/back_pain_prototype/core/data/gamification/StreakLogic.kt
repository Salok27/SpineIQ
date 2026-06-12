package noshtek.back_pain_prototype.core.data.gamification

import java.time.LocalDate

data class StreakAdvance(
    val newStreak: Int,
    val newLongest: Int,
    /** Milestone day-counts newly crossed by this advance, ascending. */
    val crossedMilestones: List<Int>,
    /** False when the advance is a same-day no-op. */
    val changed: Boolean,
)

/**
 * Pure streak rules. A streak-qualifying event is a daily check-in or an
 * assessment completion (per-step saves are excluded — they can be repeated
 * by back-navigating within one assessment). Day boundaries use the
 * device-local calendar date; travel across timezones can at worst gain or
 * lose one day, accepted for Phase 1.
 *
 * Streak breaks are applied lazily: nothing runs at midnight. Writes go
 * through [advance] on the next qualifying event, and reads use
 * [effectiveStreak] so a stale stored value displays as 0.
 */
object StreakLogic {

    fun advance(
        lastActivityDay: LocalDate?,
        today: LocalDate,
        currentStreak: Int,
        longestStreak: Int,
    ): StreakAdvance {
        if (lastActivityDay == today) {
            return StreakAdvance(currentStreak, longestStreak, emptyList(), changed = false)
        }
        val newStreak = if (lastActivityDay == today.minusDays(1)) currentStreak + 1 else 1
        val newLongest = maxOf(longestStreak, newStreak)
        val crossed = Economy.STREAK_MILESTONE_COINS.keys
            .filter { it in (currentStreak + 1)..newStreak }
            .sorted()
        return StreakAdvance(newStreak, newLongest, crossed, changed = true)
    }

    /** What the streak counts as right now: 0 if the last activity is older than yesterday. */
    fun effectiveStreak(lastActivityDay: LocalDate?, today: LocalDate, storedStreak: Int): Int =
        if (lastActivityDay != null && !lastActivityDay.isBefore(today.minusDays(1))) storedStreak else 0
}
