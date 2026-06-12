package noshtek.back_pain_prototype.core.data.gamification

/**
 * Transient reward events for celebration UI. Emitted only after the backing
 * transaction commits, so the UI never celebrates a rolled-back grant. Events
 * are fire-and-forget: a missed event loses an animation, never currency.
 */
sealed interface GamificationEvent {
    data class CoinsEarned(val amount: Int, val newBalance: Int) : GamificationEvent
    data class XpEarned(val amount: Int, val newXp: Int) : GamificationEvent
    data class LevelUp(val newLevel: GameLevel) : GamificationEvent
    data class AchievementUnlocked(val achievement: Achievement) : GamificationEvent
    data class StreakMilestoneReached(val days: Int, val coinBonus: Int) : GamificationEvent
    data class CheckInRecorded(val mood: CheckInMood, val streakDays: Int) : GamificationEvent
}
