package noshtek.back_pain_prototype.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import noshtek.back_pain_prototype.core.data.repository.PatientRepository
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    patientRepository: PatientRepository
) : ViewModel() {

    val patientCount: StateFlow<Int> = patientRepository
        .getPatientCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)
}
