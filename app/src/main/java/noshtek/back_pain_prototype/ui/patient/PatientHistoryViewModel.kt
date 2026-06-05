package noshtek.back_pain_prototype.ui.patient

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import noshtek.back_pain_prototype.core.data.db.entity.AssessmentRecordEntity
import noshtek.back_pain_prototype.core.data.db.entity.ScoresRecordEntity
import noshtek.back_pain_prototype.core.data.repository.AssessmentRepository
import noshtek.back_pain_prototype.core.data.repository.PatientRepository
import javax.inject.Inject

data class PatientHistoryUiState(
    val patientName: String = "",
    val assessments: List<AssessmentRecordEntity> = emptyList(),
    val scoresMap: Map<String, ScoresRecordEntity> = emptyMap()
)

@HiltViewModel
class PatientHistoryViewModel @Inject constructor(
    private val assessmentRepository: AssessmentRepository,
    private val patientRepository: PatientRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val patientId: String = checkNotNull(savedStateHandle["patientId"])

    val state: StateFlow<PatientHistoryUiState> = combine(
        patientRepository.getPatient(patientId),
        assessmentRepository.getAssessmentsForPatient(patientId),
        assessmentRepository.getScoresHistory(patientId)
    ) { patient, assessments, scoresList ->
        PatientHistoryUiState(
            patientName = patient?.fullName ?: "",
            assessments = assessments,
            scoresMap = scoresList.associateBy { it.assessmentId }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PatientHistoryUiState())
}
