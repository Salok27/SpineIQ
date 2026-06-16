package noshtek.back_pain_prototype.ui.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import noshtek.back_pain_prototype.core.data.db.entity.AssessmentRecordEntity
import noshtek.back_pain_prototype.core.data.db.entity.ScoresRecordEntity
import noshtek.back_pain_prototype.core.data.gamification.GamificationManager
import noshtek.back_pain_prototype.core.data.gamification.SpineVitality
import noshtek.back_pain_prototype.core.data.repository.AssessmentRepository
import noshtek.back_pain_prototype.core.data.repository.UserProfileRepository
import java.time.LocalDate
import javax.inject.Inject

data class AssessmentSummary(
    val record: AssessmentRecordEntity,
    val scores: ScoresRecordEntity
)

data class ProgressUiState(
    val isLoading: Boolean = true,
    val hasEnoughData: Boolean = false,
    val assessments: List<AssessmentSummary> = emptyList(),
    val latestScoreDelta: Int? = null,
    val latestAssessmentDate: LocalDate? = null,
    val previousAssessmentDate: LocalDate? = null,
    // Stat-strip tiles (V2)
    val streakDays: Int = 0,
    val vitality: Int = SpineVitality.NEUTRAL_BASE_VITALITY,
)

@HiltViewModel
class ProgressViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val assessmentRepository: AssessmentRepository,
    private val gamificationManager: GamificationManager,
) : ViewModel() {

    private val _state = MutableStateFlow(ProgressUiState())
    val state: StateFlow<ProgressUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            userProfileRepository.getUserProfile()
                .filterNotNull()
                .flatMapLatest { profile ->
                    combine(
                        assessmentRepository.getAssessmentsForUser(profile.id),
                        assessmentRepository.getScoresHistory(profile.id),
                        gamificationManager.snapshot,
                    ) { records, scoresList, snapshot ->
                        val completedRecords = records.filter { it.completedAt != null }
                        // Match each completed record with its scores
                        // Chronological oldest→newest. assessment_date is epoch *days*, so
                        // multiple assessments on the same day tie on a date-only sort; a stable
                        // sort would then leak the DAO's created_at DESC order through, rendering
                        // the trend charts (and habit/delta readouts) newest-first. Tie-break on
                        // created_at to match the SQL ordering and guarantee true chronology.
                        val summaries = completedRecords.mapNotNull { record ->
                            val scores = scoresList.find { it.assessmentId == record.id }
                            scores?.let { AssessmentSummary(record, it) }
                        }.sortedWith(compareBy({ it.record.assessmentDate }, { it.record.createdAt }))

                        val hasEnough = summaries.size >= 2
                        val delta = if (hasEnough) {
                            val last = summaries.last().scores.totalSSSScore
                            val prev = summaries[summaries.size - 2].scores.totalSSSScore
                            prev - last // positive = improvement
                        } else null

                        ProgressUiState(
                            isLoading = false,
                            hasEnoughData = hasEnough,
                            assessments = summaries,
                            latestScoreDelta = delta,
                            latestAssessmentDate = summaries.lastOrNull()?.let {
                                LocalDate.ofEpochDay(it.record.assessmentDate)
                            },
                            previousAssessmentDate = if (summaries.size >= 2) {
                                LocalDate.ofEpochDay(summaries[summaries.size - 2].record.assessmentDate)
                            } else null,
                            streakDays = snapshot.effectiveStreakDays,
                            vitality = snapshot.vitality,
                        )
                    }
                }
                .catch { _state.update { it.copy(isLoading = false) } }
                .collect { _state.value = it }
        }
    }
}
