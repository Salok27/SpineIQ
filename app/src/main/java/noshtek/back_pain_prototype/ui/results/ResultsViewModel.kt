package noshtek.back_pain_prototype.ui.results

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import noshtek.back_pain_prototype.core.data.db.dao.FullAssessmentData
import noshtek.back_pain_prototype.core.data.db.entity.ScoresRecordEntity
import noshtek.back_pain_prototype.core.data.repository.AssessmentRepository
import javax.inject.Inject

data class ResultsUiState(
    val isLoading: Boolean = true,
    val scores: ScoresRecordEntity? = null,
    val fullData: FullAssessmentData? = null,
    val error: String? = null
)

@HiltViewModel
class ResultsViewModel @Inject constructor(
    private val assessmentRepository: AssessmentRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val assessmentId: String? = savedStateHandle["assessmentId"]

    private val _state = MutableStateFlow(ResultsUiState())
    val state: StateFlow<ResultsUiState> = _state.asStateFlow()

    init {
        val id = assessmentId
        if (id.isNullOrEmpty()) {
            // Missing/blank nav arg — surface a graceful message instead of throwing in the
            // constructor (checkNotNull would have crashed ViewModel creation).
            _state.update { it.copy(isLoading = false, error = "Assessment not found.") }
        } else {
            viewModelScope.launch {
                assessmentRepository.getScores(id)
                    .catch { _state.update { it.copy(isLoading = false, error = "Couldn't load your results.") } }
                    .collect { scores ->
                        _state.update { it.copy(isLoading = false, scores = scores) }
                    }
            }
            viewModelScope.launch {
                try {
                    val full = assessmentRepository.getFullAssessment(id)
                    _state.update { it.copy(fullData = full) }
                } catch (e: Exception) {
                    _state.update { it.copy(isLoading = false, error = "Couldn't load your results.") }
                }
            }
        }
    }
}
