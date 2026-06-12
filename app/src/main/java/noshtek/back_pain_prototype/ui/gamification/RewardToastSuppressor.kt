package noshtek.back_pain_prototype.ui.gamification

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Prevents double reward feedback: the wizard's stage-complete interstitial
 * already shows "+10 / +20 XP" inline, so the global coin/XP toast for the
 * same grant must stay quiet. The wizard arms a short window right before
 * persisting; CelebrationViewModel checks it before showing a toast.
 * Overlay celebrations (level-up, achievements, streaks) are never suppressed.
 */
@Singleton
class RewardToastSuppressor @Inject constructor() {

    @Volatile
    private var suppressUntilMillis: Long = 0L

    fun suppress(windowMillis: Long = 2_500) {
        suppressUntilMillis = System.currentTimeMillis() + windowMillis
    }

    val isActive: Boolean
        get() = System.currentTimeMillis() < suppressUntilMillis
}
