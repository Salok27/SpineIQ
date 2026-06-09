package noshtek.back_pain_prototype.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import noshtek.back_pain_prototype.core.data.repository.UserProfileRepository
import javax.inject.Inject

data class SettingsUiState(
    val reminderEnabled: Boolean = true,
    val reminderIntervalWeeks: Int = 4,
    val isDeleting: Boolean = false,
    val dataDeleted: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    fun setReminderEnabled(enabled: Boolean) {
        _state.update { it.copy(reminderEnabled = enabled) }
        // TODO: wire to WorkManager / AlarmManager for FR-18 push reminders
    }

    fun setReminderInterval(weeks: Int) {
        _state.update { it.copy(reminderIntervalWeeks = weeks.coerceIn(1, 12)) }
    }

    fun deleteAllData() {
        _state.update { it.copy(isDeleting = true) }
        viewModelScope.launch {
            try {
                userProfileRepository.deleteAllData()
                _state.update { it.copy(isDeleting = false, dataDeleted = true) }
            } catch (e: Exception) {
                // Don't crash and don't strand the spinner; leave dataDeleted false so the
                // screen stays put rather than navigating to a half-deleted broken state.
                _state.update { it.copy(isDeleting = false) }
            }
        }
    }
}
