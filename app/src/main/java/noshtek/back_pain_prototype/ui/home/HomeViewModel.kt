package noshtek.back_pain_prototype.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import noshtek.back_pain_prototype.core.data.db.entity.ScoresRecordEntity
import noshtek.back_pain_prototype.core.data.gamification.Achievement
import noshtek.back_pain_prototype.core.data.gamification.AchievementCatalog
import noshtek.back_pain_prototype.core.data.gamification.CheckInMood
import noshtek.back_pain_prototype.core.data.gamification.GameLevel
import noshtek.back_pain_prototype.core.data.gamification.GamificationManager
import noshtek.back_pain_prototype.core.data.gamification.GamificationSnapshot
import noshtek.back_pain_prototype.core.data.gamification.LevelTable
import noshtek.back_pain_prototype.core.data.repository.AssessmentRepository
import noshtek.back_pain_prototype.core.data.repository.UserProfileRepository
import noshtek.back_pain_prototype.ui.avatar.AvatarSpec
import java.time.LocalDate
import javax.inject.Inject

/** Achievement strip entry: recently unlocked badges plus the next locked goal. */
data class HomeAchievementUi(
    val achievement: Achievement,
    val unlocked: Boolean,
    val progress: Float?,
)

data class HomeUiState(
    val isLoading: Boolean = true,
    val userName: String = "",
    val userId: String = "",
    val lastAssessmentId: String? = null,
    val lastAssessmentDate: LocalDate? = null,
    val lastScores: ScoresRecordEntity? = null,
    val completedAssessmentCount: Int = 0,
    // ── Gamification ──────────────────────────────────────────────────────────
    val coins: Int = 0,
    val level: GameLevel = LevelTable.LEVELS.first(),
    val xpIntoLevel: Int = 0,
    val xpForNextLevel: Int? = LevelTable.LEVELS.getOrNull(1)?.xpThreshold,
    val levelProgress: Float = 0f,
    val streakDays: Int = 0,
    val checkedInToday: Boolean = false,
    val todayMood: CheckInMood? = null,
    /** Oldest → today; true = checked in that day. */
    val last7Days: List<Boolean> = List(7) { false },
    val equippedSpec: AvatarSpec = AvatarSpec.Default,
    val assessmentCompletedToday: Boolean = false,
    val recentAchievements: List<HomeAchievementUi> = emptyList(),
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val assessmentRepository: AssessmentRepository,
    private val gamificationManager: GamificationManager,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    private data class GamificationHomeData(
        val snapshot: GamificationSnapshot,
        val todayMood: CheckInMood?,
        val last7Days: List<Boolean>,
        val equippedSpec: AvatarSpec,
        val recentAchievements: List<HomeAchievementUi>,
    )

    init {
        val gamification = combine(
            gamificationManager.snapshot,
            gamificationManager.ownedItems,
            gamificationManager.unlocks,
            gamificationManager.checkInHistory,
        ) { snapshot, owned, unlocks, checkIns ->
            val today = LocalDate.now().toEpochDay()
            val checkedDays = checkIns.mapTo(mutableSetOf()) { it.checkInDay }
            val unlockedIds = unlocks.map { it.achievementId }
            val context = runCatching { gamificationManager.achievementContext() }.getOrNull()
            val recentUnlocked = unlocks.take(2).mapNotNull { unlock ->
                AchievementCatalog.byId(unlock.achievementId)
                    ?.let { HomeAchievementUi(it, unlocked = true, progress = null) }
            }
            val nextLocked = AchievementCatalog.ALL
                .firstOrNull { it.id !in unlockedIds }
                ?.let { achievement ->
                    HomeAchievementUi(
                        achievement = achievement,
                        unlocked = false,
                        progress = context?.let { ctx -> achievement.progress?.invoke(ctx) },
                    )
                }
            GamificationHomeData(
                snapshot = snapshot,
                todayMood = checkIns.firstOrNull { it.checkInDay == today }?.mood,
                last7Days = (6 downTo 0).map { (today - it) in checkedDays },
                equippedSpec = AvatarSpec(
                    owned.filter { it.equipped }.associate { it.category to it.itemId }
                ),
                recentAchievements = recentUnlocked + listOfNotNull(nextLocked),
            )
        }

        viewModelScope.launch {
            userProfileRepository.getUserProfile()
                .filterNotNull()
                .flatMapLatest { profile ->
                    combine(
                        assessmentRepository.getAssessmentsForUser(profile.id),
                        assessmentRepository.getScoresHistory(profile.id),
                        assessmentRepository.getCompletedAssessmentCount(profile.id),
                        gamification,
                    ) { records, scoresList, count, game ->
                        // Select by completedAt (epoch millis) so the *most recently completed*
                        // assessment wins even when several share the same epoch-day assessment_date.
                        val latestCompleted = records
                            .filter { it.completedAt != null }
                            .maxByOrNull { it.completedAt!! }
                        val latestScores = latestCompleted?.let { r -> scoresList.find { it.assessmentId == r.id } }
                        val latestDate = latestCompleted?.let { LocalDate.ofEpochDay(it.assessmentDate) }
                        HomeUiState(
                            isLoading = false,
                            userName = profile.fullName,
                            userId = profile.id,
                            lastAssessmentId = latestCompleted?.id,
                            lastAssessmentDate = latestDate,
                            lastScores = latestScores,
                            completedAssessmentCount = count,
                            coins = game.snapshot.coins,
                            level = game.snapshot.level,
                            xpIntoLevel = game.snapshot.xpIntoLevel,
                            xpForNextLevel = game.snapshot.xpForNextLevel,
                            levelProgress = game.snapshot.progressToNext,
                            streakDays = game.snapshot.effectiveStreakDays,
                            checkedInToday = game.snapshot.checkedInToday,
                            todayMood = game.todayMood,
                            last7Days = game.last7Days,
                            equippedSpec = game.equippedSpec,
                            assessmentCompletedToday = latestDate == LocalDate.now(),
                            recentAchievements = game.recentAchievements,
                        )
                    }
                }
                .catch { _state.update { it.copy(isLoading = false) } }
                .collect { _state.value = it }
        }
    }

    /** Gamification must never block the dashboard — failures are swallowed. */
    fun checkIn(mood: CheckInMood) {
        viewModelScope.launch {
            runCatching { gamificationManager.checkInToday(mood) }
        }
    }
}
