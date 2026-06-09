package noshtek.back_pain_prototype.ui.results

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import noshtek.back_pain_prototype.core.data.repository.AssessmentRepository
import noshtek.back_pain_prototype.core.data.repository.UserProfileRepository
import noshtek.back_pain_prototype.core.pdf.PdfExporter
import noshtek.back_pain_prototype.core.pdf.PdfReportInput
import java.time.LocalDate
import java.time.Period
import javax.inject.Inject

data class PdfExportState(
    val isGenerating: Boolean = false,
    val shareUri: Uri? = null,
    val error: String? = null
)

@HiltViewModel
class PdfExportViewModel @Inject constructor(
    application: Application,
    private val assessmentRepository: AssessmentRepository,
    private val userProfileRepository: UserProfileRepository,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val assessmentId: String? = savedStateHandle["assessmentId"]
    private val pdfExporter = PdfExporter(application.applicationContext)

    private val _state = MutableStateFlow(PdfExportState())
    val state: StateFlow<PdfExportState> = _state.asStateFlow()

    fun generateAndShare() {
        if (_state.value.isGenerating) return
        val id = assessmentId
        if (id.isNullOrEmpty()) {
            _state.update { it.copy(error = "Assessment not found") }
            return
        }
        _state.update { it.copy(isGenerating = true, error = null) }

        viewModelScope.launch {
            try {
                val fullData = assessmentRepository.getFullAssessment(id)
                    ?: throw IllegalStateException("Assessment data not found")
                val scores = assessmentRepository.getScores(id).firstOrNull()
                    ?: throw IllegalStateException("Scores not found")

                val profile = userProfileRepository.getUserProfile().firstOrNull()
                    ?: throw IllegalStateException("Profile not found")

                val age = try {
                    Period.between(LocalDate.ofEpochDay(profile.dateOfBirth), LocalDate.now()).years
                } catch (e: Exception) { 0 }

                val input = PdfReportInput(
                    userName = profile.fullName,
                    userAge = age,
                    userGender = profile.gender,
                    fullData = fullData,
                    scores = scores
                )

                val file = pdfExporter.generatePdf(input)
                val uri = FileProvider.getUriForFile(
                    getApplication(),
                    "${getApplication<Application>().packageName}.fileprovider",
                    file
                )
                _state.update { it.copy(isGenerating = false, shareUri = uri) }
            } catch (e: Exception) {
                _state.update { it.copy(isGenerating = false, error = e.message ?: "PDF generation failed") }
            }
        }
    }

    fun buildShareIntent(uri: Uri): Intent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_SUBJECT, "My SpineIQ Back Pain Assessment")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    fun clearShareUri() { _state.update { it.copy(shareUri = null) } }
}
