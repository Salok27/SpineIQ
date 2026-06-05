package noshtek.back_pain_prototype.core.data.db.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import noshtek.back_pain_prototype.core.data.db.entity.PatientProfileEntity

@Dao
interface PatientDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertPatient(patient: PatientProfileEntity)

    @Update
    suspend fun updatePatient(patient: PatientProfileEntity)

    /** Cascade delete removes all assessments and section data for this patient. */
    @Query("DELETE FROM patient_profiles WHERE id = :patientId")
    suspend fun deletePatient(patientId: String)

    @Query("SELECT * FROM patient_profiles WHERE id = :patientId")
    fun getPatient(patientId: String): Flow<PatientProfileEntity?>

    @Query("SELECT * FROM patient_profiles ORDER BY full_name ASC")
    fun getAllPatients(): Flow<List<PatientProfileEntity>>

    /** Case-insensitive partial match on full_name and patient_id_external. */
    @Query("""
        SELECT * FROM patient_profiles
        WHERE full_name LIKE '%' || :query || '%'
           OR patient_id_external LIKE '%' || :query || '%'
        ORDER BY full_name ASC
    """)
    fun searchPatients(query: String): Flow<List<PatientProfileEntity>>

    @Query("SELECT COUNT(*) FROM patient_profiles")
    fun getPatientCount(): Flow<Int>
}
