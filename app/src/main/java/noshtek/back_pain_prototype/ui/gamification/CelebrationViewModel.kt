package noshtek.back_pain_prototype.ui.gamification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import noshtek.back_pain_prototype.core.data.gamification.Achievement
import noshtek.back_pain_prototype.core.data.gamification.GameLevel
import noshtek.back_pain_prototype.core.data.gamification.GamificationEvent
import noshtek.back_pain_prototype.core.data.gamification.GamificationManager
import javax.inject.Inject

/** One celebration to render. Toasts are non-blocking; overlays take the screen. */
sealed interface Celebration {
    data class Toast(val coins: Int, val xp: Int) : Celebration
    data class LevelUpOverlay(val level: GameLevel) : Celebration
    data class AchievementOverlay(val achievement: Achievement) : Celebration
    data class StreakOverlay(val days: Int, val coinBonus: Int) : Celebration
}

/**
 * Activity-scoped funnel from GamificationManager.events to the
 * CelebrationHost layer above the NavHost. Coin/XP events arriving in a burst
 * (step + achievement + level-up from one action) coalesce into a single
 * toast; overlay celebrations queue FIFO so nothing plays on top of anything.
 */
@HiltViewModel
class CelebrationViewModel @Inject constructor(
    manager: GamificationManager,
    private val toastSuppressor: RewardToastSuppressor,
) : ViewModel() {

    private val queue = ArrayDeque<Celebration>()
    private val _current = MutableStateFlow<Celebration?>(null)
    val current: StateFlow<Celebration?> = _current.asStateFlow()

    private var pendingCoins = 0
    private var pendingXp = 0
    private var flushJob: Job? = null

    init {
        viewModelScope.launch {
            manager.events.collect { event ->
                when (event) {
                    is GamificationEvent.CoinsEarned -> {
                        pendingCoins += event.amount
                        scheduleToastFlush()
                    }
                    is GamificationEvent.XpEarned -> {
                        pendingXp += event.amount
                        scheduleToastFlush()
                    }
                    is GamificationEvent.LevelUp ->
                        enqueue(Celebration.LevelUpOverlay(event.newLevel))
                    is GamificationEvent.AchievementUnlocked ->
                        enqueue(Celebration.AchievementOverlay(event.achievement))
                    is GamificationEvent.StreakMilestoneReached ->
                        enqueue(Celebration.StreakOverlay(event.days, event.coinBonus))
                    is GamificationEvent.CheckInRecorded -> Unit // the check-in card reacts itself
                }
            }
        }
    }

    private fun scheduleToastFlush() {
        flushJob?.cancel()
        flushJob = viewModelScope.launch {
            delay(300) // coalesce a burst of grants into one toast
            val coins = pendingCoins
            val xp = pendingXp
            pendingCoins = 0
            pendingXp = 0
            if ((coins > 0 || xp > 0) && !toastSuppressor.isActive) {
                enqueue(Celebration.Toast(coins, xp))
            }
        }
    }

    private fun enqueue(celebration: Celebration) {
        if (_current.value == null) {
            _current.value = celebration
        } else {
            queue.addLast(celebration)
        }
    }

    fun dismissCurrent() {
        _current.value = queue.removeFirstOrNull()
    }
}
