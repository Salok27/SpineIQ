package noshtek.back_pain_prototype.ui.achievements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import noshtek.back_pain_prototype.core.data.gamification.Achievement
import noshtek.back_pain_prototype.core.data.gamification.AchievementCatalog
import noshtek.back_pain_prototype.core.data.gamification.GamificationManager
import javax.inject.Inject

data class AchievementUi(
    val achievement: Achievement,
    val unlocked: Boolean,
    val unlockedAt: Long?,
    /** 0..1 hint for locked badges; null when unlocked or no meaningful partial progress. */
    val progress: Float?,
)

data class AchievementsUiState(
    val isLoading: Boolean = true,
    val items: List<AchievementUi> = emptyList(),
    val unlockedCount: Int = 0,
    val totalCount: Int = AchievementCatalog.ALL.size,
)

@HiltViewModel
class AchievementsViewModel @Inject constructor(
    private val manager: GamificationManager,
) : ViewModel() {

    // Snapshot is in the combine so progress hints refresh after any grant,
    // not only when an unlock row appears.
    val uiState: StateFlow<AchievementsUiState> =
        combine(manager.unlocks, manager.snapshot) { unlocks, _ ->
            val context = runCatching { manager.achievementContext() }.getOrNull()
            val unlockedAtById = unlocks.associate { it.achievementId to it.unlockedAt }
            val items = AchievementCatalog.ALL.map { achievement ->
                val unlocked = achievement.id in unlockedAtById
                AchievementUi(
                    achievement = achievement,
                    unlocked = unlocked,
                    unlockedAt = unlockedAtById[achievement.id],
                    progress = if (unlocked || context == null) null
                    else achievement.progress?.invoke(context),
                )
            }
            AchievementsUiState(
                isLoading = false,
                items = items,
                unlockedCount = unlockedAtById.size,
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AchievementsUiState())
}
