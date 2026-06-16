package noshtek.back_pain_prototype.core.data.gamification

/**
 * Transient celebration events for the Aura engagement UI. Emitted only after
 * the backing transaction commits, so the UI never celebrates a rolled-back
 * change. Fire-and-forget: a missed event loses an animation, never progress.
 */
sealed interface GamificationEvent {
    data class RitualCompleted(
        val ritual: Ritual,
        val ritualsDoneToday: Int,
        val ritualsTotalToday: Int,
    ) : GamificationEvent

    data class MilestoneReached(val milestone: Milestone) : GamificationEvent

    /** The Living Spine grew brighter — drives the heal animation + glow bloom. */
    data class VitalityImproved(
        val from: Int,
        val to: Int,
        val stage: VitalityStage,
    ) : GamificationEvent

    data class StreakMilestone(val days: Int) : GamificationEvent

    data class CheckInRecorded(val mood: CheckInMood, val streakDays: Int) : GamificationEvent
}
