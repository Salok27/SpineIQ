package noshtek.back_pain_prototype.core.data.gamification

/**
 * The coin/XP economy in one place. Coins are spendable (avatar shop); XP is
 * monotonic and only drives levels. Tuning principle: completing assessments
 * stays the dominant coin source; daily consistency is rewarded through
 * streak milestones rather than a large check-in payout.
 */
object Economy {
    const val COINS_PER_STEP = 10
    const val XP_PER_STEP = 20

    const val COINS_PER_COMPLETION = 50
    const val XP_PER_COMPLETION = 100

    const val COINS_PER_CHECKIN = 5
    const val XP_PER_CHECKIN = 15

    /** One-time-ever coin bonuses, keyed by streak length in days. */
    val STREAK_MILESTONE_COINS: Map<Int, Int> = mapOf(3 to 50, 7 to 100, 14 to 150, 30 to 300)

    /** Five sections persist data (Review completes instead of saving). */
    const val STEPS_PER_ASSESSMENT = 5

    val COINS_PER_FULL_ASSESSMENT = COINS_PER_STEP * STEPS_PER_ASSESSMENT + COINS_PER_COMPLETION
    val XP_PER_FULL_ASSESSMENT = XP_PER_STEP * STEPS_PER_ASSESSMENT + XP_PER_COMPLETION
}
