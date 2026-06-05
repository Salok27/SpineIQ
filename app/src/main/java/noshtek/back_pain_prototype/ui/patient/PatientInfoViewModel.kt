package noshtek.back_pain_prototype.ui.patient

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import noshtek.back_pain_prototype.core.data.db.entity.PatientProfileEntity
import noshtek.back_pain_prototype.core.data.model.Gender
import noshtek.back_pain_prototype.core.data.repository.PatientRepository
import java.time.Instant
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

data class PatientFormState(
    val isLoading: Boolean = false,
    val isExistingPatient: Boolean = false,
    val patientId: String = "",
    val fullName: String = "",
    val birthYear: String = "1990",
    val birthMonth: String = "1",
    val birthDay: String = "1",
    val gender: Gender = Gender.PREFER_NOT_TO_SAY,
    val heightCm: Float = 170f,
    val weightKg: Float = 70f,
    val phoneNumber: String = "",
    val patientIdExternal: String = "",
    val isSaved: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class PatientInfoViewModel @Inject constructor(
    private val patientRepository: PatientRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val existingPatientId: String? = savedStateHandle["patientId"]

    private val _state = MutableStateFlow(PatientFormState())
    val state: StateFlow<PatientFormState> = _state.asStateFlow()

    private var originalCreatedAt: Long = 0L

    init {
        if (!existingPatientId.isNullOrBlank()) {
            _state.update { it.copy(isLoading = true, isExistingPatient = true, patientId = existingPatientId) }
            viewModelScope.launch {
                val patient = patientRepository.getPatient(existingPatientId).firstOrNull()
                if (patient != null) {
                    originalCreatedAt = patient.createdAt
                    val dob = LocalDate.ofEpochDay(patient.dateOfBirth)
                    _state.update {
                        it.copy(
                            isLoading = false,
                            fullName = patient.fullName,
                            birthYear = dob.year.toString(),
                            birthMonth = dob.monthValue.toString(),
                            birthDay = dob.dayOfMonth.toString(),
                            gender = patient.gender,
                            heightCm = patient.heightCm,
                            weightKg = patient.weightKg,
                            phoneNumber = patient.phoneNumber ?: "",
                            patientIdExternal = patient.patientIdExternal ?: ""
                        )
                    }
                } else {
                    _state.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    fun setFullName(v: String) = _state.update { it.copy(fullName = v, error = null) }
    fun setBirthYear(v: String) = _state.update { it.copy(birthYear = v) }
    fun setBirthMonth(v: String) = _state.update { it.copy(birthMonth = v) }
    fun setBirthDay(v: String) = _state.update { it.copy(birthDay = v) }
    fun setGender(v: Gender) = _state.update { it.copy(gender = v) }
    fun setHeightCm(v: Float) = _state.update { it.copy(heightCm = v) }
    fun setWeightKg(v: Float) = _state.update { it.copy(weightKg = v) }
    fun setPhoneNumber(v: String) = _state.update { it.copy(phoneNumber = v) }
    fun setPatientIdExternal(v: String) = _state.update { it.copy(patientIdExternal = v) }
    fun clearError() = _state.update { it.copy(error = null) }

    fun savePatient() {
        val s = _state.value
        if (s.fullName.isBlank()) {
            _state.update { it.copy(error = "Patient name is required") }
            return
        }
        val dob = try {
            LocalDate.of(
                s.birthYear.toInt(),
                s.birthMonth.toInt(),
                s.birthDay.toInt()
            )
        } catch (e: Exception) {
            _state.update { it.copy(error = "Invalid date of birth") }
            return
        }
        viewModelScope.launch {
            val now = Instant.now().toEpochMilli()
            if (s.isExistingPatient) {
                patientRepository.updatePatient(
                    PatientProfileEntity(
                        id = s.patientId,
                        fullName = s.fullName,
                        dateOfBirth = dob.toEpochDay(),
                        gender = s.gender,
                        heightCm = s.heightCm,
                        weightKg = s.weightKg,
                        phoneNumber = s.phoneNumber.ifBlank { null },
                        patientIdExternal = s.patientIdExternal.ifBlank { null },
                        createdAt = originalCreatedAt,
                        updatedAt = now
                    )
                )
                _state.update { it.copy(isSaved = true) }
            } else {
                val id = UUID.randomUUID().toString()
                patientRepository.createPatient(
                    PatientProfileEntity(
                        id = id,
                        fullName = s.fullName,
                        dateOfBirth = dob.toEpochDay(),
                        gender = s.gender,
                        heightCm = s.heightCm,
                        weightKg = s.weightKg,
                        phoneNumber = s.phoneNumber.ifBlank { null },
                        patientIdExternal = s.patientIdExternal.ifBlank { null },
                        createdAt = now,
                        updatedAt = now
                    )
                )
                _state.update { it.copy(patientId = id, isSaved = true) }
            }
        }
    }
}
