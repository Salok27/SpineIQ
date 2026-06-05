package noshtek.back_pain_prototype.core.data.db.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import noshtek.back_pain_prototype.core.data.db.entity.*
import noshtek.back_pain_prototype.core.data.model.AssessmentStatus

@Dao
interface AssessmentDao {

    // ── AssessmentRecord ──────────────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAssessmentRecord(record: AssessmentRecordEntity)

    @Update
    suspend fun updateAssessmentRecord(record: AssessmentRecordEntity)

    @Query("SELECT * FROM assessment_records WHERE id = :assessmentId")
    suspend fun getAssessmentRecordOnce(assessmentId: String): AssessmentRecordEntity?

    @Query("SELECT * FROM assessment_records WHERE patient_id = :patientId ORDER BY assessment_date DESC")
    fun getAssessmentsForPatient(patientId: String): Flow<List<AssessmentRecordEntity>>

    /** Returns any single in-progress assessment across all patients (Section 15.5). */
    @Query("SELECT * FROM assessment_records WHERE status = 'IN_PROGRESS' ORDER BY created_at DESC LIMIT 1")
    fun getInProgressAssessment(): Flow<AssessmentRecordEntity?>

    @Query("SELECT COUNT(*) FROM assessment_records WHERE patient_id = :patientId AND status = 'COMPLETED'")
    fun getCompletedAssessmentCount(patientId: String): Flow<Int>

    // ── OccupationData ────────────────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertOccupationData(data: OccupationDataEntity)

    @Query("SELECT * FROM occupation_data WHERE assessment_id = :assessmentId")
    suspend fun getOccupationData(assessmentId: String): OccupationDataEntity?

    // ── LifestyleData ─────────────────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertLifestyleData(data: LifestyleDataEntity)

    @Query("SELECT * FROM lifestyle_data WHERE assessment_id = :assessmentId")
    suspend fun getLifestyleData(assessmentId: String): LifestyleDataEntity?

    // ── PainData ──────────────────────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPainData(data: PainDataEntity)

    @Query("SELECT * FROM pain_data WHERE assessment_id = :assessmentId")
    suspend fun getPainData(assessmentId: String): PainDataEntity?

    // ── FunctionalData ────────────────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertFunctionalData(data: FunctionalDataEntity)

    @Query("SELECT * FROM functional_data WHERE assessment_id = :assessmentId")
    suspend fun getFunctionalData(assessmentId: String): FunctionalDataEntity?

    // ── RedFlagData ───────────────────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRedFlagData(data: RedFlagDataEntity)

    @Query("SELECT * FROM red_flag_data WHERE assessment_id = :assessmentId")
    suspend fun getRedFlagData(assessmentId: String): RedFlagDataEntity?

    // ── Heart rate trend (Section 9.7, FR-16) ─────────────────────────────────

    /**
     * Returns lifestyle rows for a patient that have heart rate data,
     * ordered chronologically for trend visualisation.
     */
    @Query("""
        SELECT l.* FROM lifestyle_data l
        INNER JOIN assessment_records r ON l.assessment_id = r.id
        WHERE r.patient_id = :patientId
          AND r.status = 'COMPLETED'
          AND (l.resting_heart_rate IS NOT NULL OR l.average_heart_rate IS NOT NULL)
        ORDER BY r.assessment_date ASC
    """)
    fun getHeartRateHistory(patientId: String): Flow<List<LifestyleDataEntity>>

    // ── Full assessment load ──────────────────────────────────────────────────

    @Transaction
    suspend fun getFullAssessment(assessmentId: String): FullAssessmentData? {
        val record = getAssessmentRecordOnce(assessmentId) ?: return null
        return FullAssessmentData(
            record     = record,
            occupation = getOccupationData(assessmentId),
            lifestyle  = getLifestyleData(assessmentId),
            pain       = getPainData(assessmentId),
            functional = getFunctionalData(assessmentId),
            redFlags   = getRedFlagData(assessmentId)
        )
    }
}

/** All sections for one assessment, assembled in memory. */
data class FullAssessmentData(
    val record: AssessmentRecordEntity,
    val occupation: OccupationDataEntity?,
    val lifestyle: LifestyleDataEntity?,
    val pain: PainDataEntity?,
    val functional: FunctionalDataEntity?,
    val redFlags: RedFlagDataEntity?
)
