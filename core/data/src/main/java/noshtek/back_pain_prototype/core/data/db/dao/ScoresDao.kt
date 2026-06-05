package noshtek.back_pain_prototype.core.data.db.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import noshtek.back_pain_prototype.core.data.db.entity.ScoresRecordEntity

@Dao
interface ScoresDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScores(scores: ScoresRecordEntity)

    @Query("SELECT * FROM scores_records WHERE assessment_id = :assessmentId")
    fun getScores(assessmentId: String): Flow<ScoresRecordEntity?>

    @Query("SELECT * FROM scores_records WHERE assessment_id = :assessmentId")
    suspend fun getScoresOnce(assessmentId: String): ScoresRecordEntity?

    /** All scores for a patient's completed assessments, newest first — for longitudinal reporting. */
    @Query("""
        SELECT s.* FROM scores_records s
        INNER JOIN assessment_records r ON s.assessment_id = r.id
        WHERE r.patient_id = :patientId
        ORDER BY r.assessment_date DESC
    """)
    fun getScoresHistoryForPatient(patientId: String): Flow<List<ScoresRecordEntity>>
}
