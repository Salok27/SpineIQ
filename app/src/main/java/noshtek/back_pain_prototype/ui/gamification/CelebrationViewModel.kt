package noshtek.back_pain_prototype.ui.gamification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import noshtek.back_pain_prototype.core.data.gamification.GamificationEvent
import noshtek.back_pain_prototype.core.data.gamification.GamificationManager
import noshtek.back_pain_prototype.core.data.gamification.Milestone
import javax.inject.Inject

/** One celebration to render. Toasts are non-blocking; overlays take the screen. */
sealed interface Celebration {
    data class Toast(val message: String) : Celebration
    data class MilestoneOverlay(val milestone: Milestone) : Celebration
    data class StreakOverlay(val days: Int) : Celebration
}

/**
 * Activity-scoped funnel from GamificationManager.events to the CelebrationHost
 * layer above the NavHost. Ritual completions and meaningful vitality jumps
 * surface as non-blocking toasts (suppressed in contexts that show the reward
 * inline); milestones and streak milestones queue FIFO as full overlays so
 * nothing plays on top of anything.
 */
@HiltViewModel
class CelebrationViewModel @Inject constructor(
    manager: GamificationManager,
    private val toastSuppressor: RewardToastSuppressor,
) : ViewModel() {

    private val queue = ArrayDeque<Celebration>()
    private val _current = MutableStateFlow<Celebration?>(null)
    val current: StateFlow<Celebration?> = _current.asStateFlow()

    init {
        viewModelScope.launch {
            manager.events.collect { event ->
                when (event) {
                    is GamificationEvent.RitualCompleted ->
                        if (!toastSuppressor.isActive) {
                            enqueue(Celebration.Toast("Ritual done · ${event.ritualsDoneToday}/${event.ritualsTotalToday} today"))
                        }
                    is GamificationEvent.VitalityImproved ->
                        // Only surface meaningful jumps (mostly post-assessment); small
                        // ritual/check-in nudges are reflected on the Living Spine itself.
                        if (event.to - event.from >= 4 && !toastSuppressor.isActive) {
                            enqueue(Celebration.Toast("Spine vitality ${event.from} → ${event.to}"))
                        }
                    is GamificationEvent.MilestoneReached ->
                        enqueue(Celebration.MilestoneOverlay(event.milestone))
                    is GamificationEvent.StreakMilestone ->
                        enqueue(Celebration.StreakOverlay(event.days))
                    is GamificationEvent.CheckInRecorded -> Unit // the check-in card reacts itself
                }
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
