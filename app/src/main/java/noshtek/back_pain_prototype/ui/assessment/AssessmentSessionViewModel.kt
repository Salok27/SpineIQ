package noshtek.back_pain_prototype.ui.assessment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import noshtek.back_pain_prototype.core.data.db.entity.*
import noshtek.back_pain_prototype.core.data.model.AssessmentStatus
import noshtek.back_pain_prototype.core.data.model.DataSource
import noshtek.back_pain_prototype.core.data.repository.AssessmentRepository
import noshtek.back_pain_prototype.core.data.repository.PatientRepository
import noshtek.back_pain_prototype.core.scoring.ScoringEngine
import noshtek.back_pain_prototype.core.scoring.model.*
import noshtek.back_pain_prototype.ui.common.*
import java.time.Instant
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AssessmentSessionViewModel @Inject constructor(
    private val assessmentRepository: AssessmentRepository,
    private val patientRepository: PatientRepository
) : ViewModel() {

    private val _session = MutableStateFlow(AssessmentSession())
    val session: StateFlow<AssessmentSession> = _session.asStateFlow()

    fun initSession(patientId: String) {
        if (_session.value.patientId == patientId && _session.value.assessmentId.isNotEmpty()) return
        viewModelScope.launch {
            _session.update { it.copy(isLoading = true) }
            patientRepository.getPatient(patientId).collect { patient ->
                if (patient == null) { _session.update { it.copy(isLoading = false) }; return@collect }
                val age = run {
                    val dob = LocalDate.ofEpochDay(patient.dateOfBirth)
                    java.time.Period.between(dob, LocalDate.now()).years
                }
                if (_session.value.assessmentId.isEmpty()) {
                    val aId = assessmentRepository.startAssessment(patientId)
                    _session.update { it.copy(
                        isLoading = false,
                        patientId = patientId,
                        patientName = patient.fullName,
                        patientAgeYears = age,
                        patientWeightKg = patient.weightKg,
                        patientHeightCm = patient.heightCm,
                        assessmentId = aId
                    )}
                } else {
                    _session.update { it.copy(isLoading = false, patientName = patient.fullName, patientAgeYears = age, patientWeightKg = patient.weightKg, patientHeightCm = patient.heightCm) }
                }
                return@collect // only need first emission to init
            }
        }
    }

    fun updateOccupation(block: OccupationDraft.() -> OccupationDraft) {
        _session.update { it.copy(occupation = it.occupation.block()) }
    }

    fun updateLifestyle(block: LifestyleDraft.() -> LifestyleDraft) {
        _session.update { it.copy(lifestyle = it.lifestyle.block()) }
    }

    fun updatePain(block: PainDraft.() -> PainDraft) {
        _session.update { it.copy(pain = it.pain.block()) }
    }

    fun updateFunctional(block: FunctionalDraft.() -> FunctionalDraft) {
        _session.update { it.copy(functional = it.functional.block()) }
    }

    fun updateRedFlags(block: RedFlagDraft.() -> RedFlagDraft) {
        _session.update { it.copy(redFlags = it.redFlags.block()) }
    }

    fun persistOccupation() {
        val s = _session.value; if (s.assessmentId.isEmpty()) return
        viewModelScope.launch {
            assessmentRepository.saveOccupationData(OccupationDataEntity(
                assessmentId = s.assessmentId,
                occupationType = s.occupation.occupationType ?: return@launch,
                sittingHoursPerDay = s.occupation.sittingHoursPerDay,
                standingHoursPerDay = s.occupation.standingHoursPerDay,
                drivingHoursPerDay = s.occupation.drivingHoursPerDay,
                liftingLevel = s.occupation.liftingLevel,
                workPatternNotes = s.occupation.workPatternNotes.ifBlank { null }
            ))
        }
    }

    fun persistLifestyle() {
        val s = _session.value; if (s.assessmentId.isEmpty()) return
        viewModelScope.launch {
            assessmentRepository.saveLifestyleData(LifestyleDataEntity(
                assessmentId = s.assessmentId,
                sittingHoursPerDay = s.occupation.sittingHoursPerDay,
                walkingMinutesPerDay = s.lifestyle.walkingMinutesPerDay,
                exerciseDaysPerWeek = s.lifestyle.exerciseDaysPerWeek,
                exerciseTypes = s.lifestyle.exerciseTypes,
                sleepQuality = s.lifestyle.sleepQuality,
                exerciseTypeModifierApplied = s.lifestyle.exerciseTypes.any { it.isHighImpact },
                sleepHoursPerNight = s.lifestyle.sleepHoursPerNight,
                dataSourceSleepHours = DataSource.MANUAL,
                dailySteps = s.lifestyle.dailySteps,
                activeMinutesPerDay = s.lifestyle.activeMinutesPerDay,
                sedentaryTimeMinutesPerDay = s.lifestyle.sedentaryTimeMinutesPerDay,
                restingHeartRate = s.lifestyle.restingHeartRate,
                averageHeartRate = s.lifestyle.averageHeartRate
            ))
        }
    }

    fun persistPain() {
        val s = _session.value; if (s.assessmentId.isEmpty()) return
        viewModelScope.launch {
            assessmentRepository.savePainData(PainDataEntity(
                assessmentId = s.assessmentId,
                painLocations = s.pain.painLocations,
                vasScore = s.pain.vasScore,
                painDuration = s.pain.painDuration,
                painPattern = s.pain.painPattern,
                painTriggers = s.pain.painTriggers,
                radiculopathySeverity = s.pain.radiculopathySeverity,
                radiationLocation = s.pain.radiationLocation,
                functionalLimitationsText = s.pain.functionalLimitationsText.ifBlank { null },
                functionalLimitationSeverity = s.pain.functionalLimitationSeverity
            ))
        }
    }

    fun persistFunctional() {
        val s = _session.value; if (s.assessmentId.isEmpty()) return
        viewModelScope.launch {
            assessmentRepository.saveFunctionalData(FunctionalDataEntity(
                assessmentId = s.assessmentId,
                walking = s.functional.walking,
                sitting = s.functional.sitting,
                standing = s.functional.standing,
                sleep = s.functional.sleep,
                dailyActivities = s.functional.dailyActivities
            ))
        }
    }

    fun persistRedFlags() {
        val s = _session.value; if (s.assessmentId.isEmpty()) return
        viewModelScope.launch {
            assessmentRepository.saveRedFlagData(RedFlagDataEntity(
                assessmentId = s.assessmentId,
                historyCancer = s.redFlags.historyCancer,
                unexplainedWeightLoss = s.redFlags.unexplainedWeightLoss,
                feverOrInfection = s.redFlags.feverOrInfection,
                recentMajorTrauma = s.redFlags.recentMajorTrauma,
                bowelBladderDysfunction = s.redFlags.bowelBladderDysfunction,
                saddleAnaesthesia = s.redFlags.saddleAnaesthesia,
                progressiveNeurologicalDeficit = s.redFlags.progressiveNeurologicalDeficit,
                otherSeriousPathologySuspicion = s.redFlags.otherSeriousPathologySuspicion
            ))
        }
    }

    fun computeAndComplete(onComplete: (String) -> Unit) {
        val s = _session.value
        _session.update { it.copy(isScoring = true) }
        viewModelScope.launch {
            val input = AssessmentInput(
                demographic = DemographicInput(s.patientAgeYears, s.patientWeightKg, s.patientHeightCm),
                lifestyle = LifestyleInput(
                    sittingHoursPerDay = s.occupation.sittingHoursPerDay,
                    walkingMinutesPerDay = s.lifestyle.walkingMinutesPerDay,
                    exerciseDaysPerWeek = s.lifestyle.exerciseDaysPerWeek,
                    sleepHoursPerNight = s.lifestyle.sleepHoursPerNight,
                    sleepQuality = s.lifestyle.sleepQuality,
                    exerciseTypes = s.lifestyle.exerciseTypes
                ),
                pain = PainInput(s.pain.vasScore, s.pain.radiculopathySeverity, s.pain.painDuration),
                functional = FunctionalInput(s.functional.walking, s.functional.sitting, s.functional.standing, s.functional.sleep, s.functional.dailyActivities),
                hasRedFlag = s.redFlags.hasAnyRedFlag
            )
            val result = ScoringEngine.compute(input)
            assessmentRepository.completeAssessment(s.assessmentId, result)
            _session.update { it.copy(isScoring = false, scoringResult = result) }
            onComplete(s.assessmentId)
        }
    }

    fun resetSession() { _session.value = AssessmentSession() }
}
