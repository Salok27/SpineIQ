package noshtek.back_pain_prototype.core.data.repository

import kotlinx.coroutines.flow.Flow
import noshtek.back_pain_prototype.core.data.db.dao.PatientDao
import noshtek.back_pain_prototype.core.data.db.entity.PatientProfileEntity
import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PatientRepository @Inject constructor(
    private val patientDao: PatientDao
) {
    fun getAllPatients(): Flow<List<PatientProfileEntity>> =
        patientDao.getAllPatients()

    fun getPatient(id: String): Flow<PatientProfileEntity?> =
        patientDao.getPatient(id)

    fun searchPatients(query: String): Flow<List<PatientProfileEntity>> =
        patientDao.searchPatients(query)

    fun getPatientCount(): Flow<Int> =
        patientDao.getPatientCount()

    suspend fun createPatient(patient: PatientProfileEntity): String {
        patientDao.insertPatient(patient)
        return patient.id
    }

    suspend fun updatePatient(patient: PatientProfileEntity) {
        val now = Instant.now().toEpochMilli()
        patientDao.updatePatient(patient.copy(updatedAt = now))
    }

    /** Cascade delete removes all assessments and section data for this patient (Section 15.3). */
    suspend fun deletePatient(id: String) {
        patientDao.deletePatient(id)
    }
}
