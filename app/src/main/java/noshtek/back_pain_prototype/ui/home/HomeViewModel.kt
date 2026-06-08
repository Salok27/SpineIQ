package noshtek.back_pain_prototype.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import noshtek.back_pain_prototype.core.data.db.entity.ScoresRecordEntity
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
    val completedAssessmentCount: Int = 0
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val assessmentRepository: AssessmentRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            userProfileRepository.getUserProfile()
                .filterNotNull()
                .flatMapLatest { profile ->
                    combine(
                        assessmentRepository.getAssessmentsForUser(profile.id),
                        assessmentRepository.getScoresHistory(profile.id),
                        assessmentRepository.getCompletedAssessmentCount(profile.id)
                    ) { records, scoresList, count ->
                        val latestCompleted = records.firstOrNull { it.completedAt != null }
                        val latestScores = latestCompleted?.let { r -> scoresList.find { it.assessmentId == r.id } }
                        HomeUiState(
                            isLoading = false,
                            userName = profile.fullName,
                            userId = profile.id,
                            lastAssessmentId = latestCompleted?.id,
                            lastAssessmentDate = latestCompleted?.let { LocalDate.ofEpochDay(it.assessmentDate) },
                            lastScores = latestScores,
                            completedAssessmentCount = count
                        )
                    }
                }
                .catch { _state.update { it.copy(isLoading = false) } }
                .collect { _state.value = it }
        }
    }
}
