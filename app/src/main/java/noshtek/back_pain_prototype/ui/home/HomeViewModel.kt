package noshtek.back_pain_prototype.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import noshtek.back_pain_prototype.core.data.db.entity.ScoresRecordEntity
import noshtek.back_pain_prototype.core.data.gamification.CheckInMood
import noshtek.back_pain_prototype.core.data.gamification.GamificationManager
import noshtek.back_pain_prototype.core.data.gamification.GamificationSnapshot
import noshtek.back_pain_prototype.core.data.gamification.RitualStatus
import noshtek.back_pain_prototype.core.data.gamification.SpineVitality
import noshtek.back_pain_prototype.core.data.repository.AssessmentRepository
import noshtek.back_pain_prototype.core.data.repository.UserProfileRepository
import java.time.LocalDate
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = true,
    val userName: String = "",
    val userId: String = "",
    val lastAssessmentId: String? = null,
    val lastAssessmentDate: LocalDate? = null,
    val lastScores: ScoresRecordEntity? = null,
    val completedAssessmentCount: Int = 0,
    // ── Aura engagement ─────────────────────────────────────────────────────
    val vitality: Int = SpineVitality.NEUTRAL_BASE_VITALITY,
    val streakDays: Int = 0,
    val checkedInToday: Boolean = false,
    val todayMood: CheckInMood? = null,
    /** Oldest → today; true = checked in that day. */
    val last7Days: List<Boolean> = List(7) { false },
    val rituals: List<RitualStatus> = emptyList(),
    val ritualsDoneToday: Int = 0,
    val ritualsTotalToday: Int = 0,
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
    )

    init {
        val gamification = combine(
            gamificationManager.snapshot,
            gamificationManager.checkInHistory,
        ) { snapshot, checkIns ->
            val today = LocalDate.now().toEpochDay()
            val checkedDays = checkIns.mapTo(mutableSetOf()) { it.checkInDay }
            GamificationHomeData(
                snapshot = snapshot,
                todayMood = checkIns.firstOrNull { it.checkInDay == today }?.mood,
                last7Days = (6 downTo 0).map { (today - it) in checkedDays },
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
                            vitality = game.snapshot.vitality,
                            streakDays = game.snapshot.effectiveStreakDays,
                            checkedInToday = game.snapshot.checkedInToday,
                            todayMood = game.todayMood,
                            last7Days = game.last7Days,
                            rituals = game.snapshot.rituals,
                            ritualsDoneToday = game.snapshot.ritualsDoneToday,
                            ritualsTotalToday = game.snapshot.ritualsTotalToday,
                        )
                    }
                }
                .catch { _state.update { it.copy(isLoading = false) } }
                .collect { _state.value = it }
        }
    }

    /** Engagement must never block the dashboard — failures are swallowed. */
    fun checkIn(mood: CheckInMood) {
        viewModelScope.launch {
            runCatching { gamificationManager.checkInToday(mood) }
        }
    }

    fun completeRitual(ritualId: String) {
        viewModelScope.launch {
            runCatching { gamificationManager.completeRitual(ritualId) }
        }
    }
}
