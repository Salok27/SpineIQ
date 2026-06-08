package noshtek.back_pain_prototype.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import noshtek.back_pain_prototype.core.data.repository.UserProfileRepository
import javax.inject.Inject

data class OnboardingUiState(
    val isLoading: Boolean = true,
    val hasProfile: Boolean = false,
    val currentPage: Int = 0
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingUiState())
    val state: StateFlow<OnboardingUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val has = userProfileRepository.hasProfile()
            _state.value = OnboardingUiState(isLoading = false, hasProfile = has)
        }
    }

    fun nextPage() {
        _state.value = _state.value.copy(currentPage = _state.value.currentPage + 1)
    }
}
