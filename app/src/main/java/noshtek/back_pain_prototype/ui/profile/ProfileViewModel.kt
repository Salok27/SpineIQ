package noshtek.back_pain_prototype.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import noshtek.back_pain_prototype.core.data.db.entity.UserProfileEntity
import noshtek.back_pain_prototype.core.data.model.Gender
import noshtek.back_pain_prototype.core.data.repository.UserProfileRepository
import java.time.Instant
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

data class ProfileUiState(
    val isLoading: Boolean = true,
    val fullName: String = "",
    val dateOfBirth: LocalDate = LocalDate.now().minusYears(30),
    val gender: Gender = Gender.PREFER_NOT_TO_SAY,
    val heightCm: String = "",
    val weightKg: String = "",
    val isSaving: Boolean = false,
    val saved: Boolean = false,
    val error: String? = null,
    val isEditMode: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    private var existingProfileId: String? = null

    init {
        viewModelScope.launch {
            val existing = userProfileRepository.getUserProfile().firstOrNull()
            if (existing != null) {
                existingProfileId = existing.id
                _state.update {
                    it.copy(
                        isLoading = false,
                        isEditMode = true,
                        fullName = existing.fullName,
                        dateOfBirth = LocalDate.ofEpochDay(existing.dateOfBirth),
                        gender = existing.gender,
                        heightCm = existing.heightCm.toFieldString(),
                        weightKg = existing.weightKg.toFieldString()
                    )
                }
            } else {
                _state.update { it.copy(isLoading = false, isEditMode = false) }
            }
        }
    }

    fun onNameChange(value: String) = _state.update { it.copy(fullName = value) }
    fun onDobChange(value: LocalDate) = _state.update { it.copy(dateOfBirth = value) }
    fun onGenderChange(value: Gender) = _state.update { it.copy(gender = value) }
    fun onHeightChange(value: String) = _state.update { it.copy(heightCm = value) }
    fun onWeightChange(value: String) = _state.update { it.copy(weightKg = value) }

    fun saveProfile() {
        val s = _state.value
        val height = s.heightCm.toFloatOrNull()
        val weight = s.weightKg.toFloatOrNull()
        if (s.fullName.isBlank() || height == null || weight == null) {
            _state.update { it.copy(error = "Please fill in all required fields.") }
            return
        }
        if (height !in 50f..260f || weight !in 20f..500f) {
            _state.update { it.copy(error = "Enter a realistic height (50–260 cm) and weight (20–500 kg).") }
            return
        }
        if (s.dateOfBirth.isAfter(LocalDate.now())) {
            _state.update { it.copy(error = "Date of birth can't be in the future.") }
            return
        }
        _state.update { it.copy(isSaving = true, error = null) }
        viewModelScope.launch {
            val now = Instant.now().toEpochMilli()
            val id = existingProfileId ?: UUID.randomUUID().toString()
            val profile = UserProfileEntity(
                id = id,
                fullName = s.fullName.trim(),
                dateOfBirth = s.dateOfBirth.toEpochDay(),
                gender = s.gender,
                heightCm = height,
                weightKg = weight,
                createdAt = now,
                updatedAt = now
            )
            if (existingProfileId == null) {
                userProfileRepository.createProfile(profile)
            } else {
                userProfileRepository.updateProfile(profile)
            }
            _state.update { it.copy(isSaving = false, saved = true) }
        }
    }

    fun clearError() = _state.update { it.copy(error = null) }
}

/** Renders a stored measurement without a redundant trailing ".0" while keeping real decimals. */
private fun Float.toFieldString(): String =
    if (this == toLong().toFloat()) toLong().toString() else toString()
