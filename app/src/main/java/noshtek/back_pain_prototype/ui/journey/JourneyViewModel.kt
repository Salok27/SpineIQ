package noshtek.back_pain_prototype.ui.journey

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import noshtek.back_pain_prototype.core.data.gamification.GamificationManager
import noshtek.back_pain_prototype.core.data.gamification.Milestone
import noshtek.back_pain_prototype.core.data.gamification.MilestoneCatalog
import noshtek.back_pain_prototype.core.data.gamification.MilestoneContext
import noshtek.back_pain_prototype.core.data.gamification.SpineVitality
import noshtek.back_pain_prototype.core.data.gamification.VitalityStage
import javax.inject.Inject

data class MilestoneUi(
    val milestone: Milestone,
    val unlocked: Boolean,
    /** 0..1 progress hint for locked milestones, if any. */
    val progress: Float?,
)

data class JourneyUiState(
    val isLoading: Boolean = true,
    val vitality: Int = SpineVitality.NEUTRAL_BASE_VITALITY,
    val stage: VitalityStage = VitalityStage.STEADY,
    val streakDays: Int = 0,
    val longestStreakDays: Int = 0,
    val ritualsDoneToday: Int = 0,
    val ritualsTotalToday: Int = 0,
    val milestones: List<MilestoneUi> = emptyList(),
    val unlockedCount: Int = 0,
)

@HiltViewModel
class JourneyViewModel @Inject constructor(
    private val gamificationManager: GamificationManager,
) : ViewModel() {

    private val _state = MutableStateFlow(JourneyUiState())
    val state: StateFlow<JourneyUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                gamificationManager.snapshot,
                gamificationManager.milestoneUnlocks,
            ) { snapshot, unlocks -> snapshot to unlocks }
                .catch { _state.value = _state.value.copy(isLoading = false) }
                .collect { (snapshot, unlocks) ->
                    val ctx = runCatching { gamificationManager.milestoneContext() }
                        .getOrDefault(MilestoneContext())
                    val unlockedIds = unlocks.mapTo(mutableSetOf()) { it.achievementId }
                    val milestones = MilestoneCatalog.ALL.map { m ->
                        val unlocked = m.id in unlockedIds
                        MilestoneUi(
                            milestone = m,
                            unlocked = unlocked,
                            progress = if (unlocked) null else m.progress?.invoke(ctx),
                        )
                    }
                    _state.value = JourneyUiState(
                        isLoading = false,
                        vitality = snapshot.vitality,
                        stage = snapshot.stage,
                        streakDays = snapshot.effectiveStreakDays,
                        longestStreakDays = snapshot.longestStreakDays,
                        ritualsDoneToday = snapshot.ritualsDoneToday,
                        ritualsTotalToday = snapshot.ritualsTotalToday,
                        milestones = milestones,
                        unlockedCount = unlockedIds.size,
                    )
                }
        }
    }
}
