package noshtek.back_pain_prototype.core.data.repository

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import noshtek.back_pain_prototype.core.data.db.SpineIQDatabase
import noshtek.back_pain_prototype.core.data.db.dao.AssessmentDao
import noshtek.back_pain_prototype.core.data.db.dao.FullAssessmentData
import noshtek.back_pain_prototype.core.data.db.dao.ScoresDao
import noshtek.back_pain_prototype.core.data.db.entity.*
import noshtek.back_pain_prototype.core.data.model.AssessmentStatus
import noshtek.back_pain_prototype.core.scoring.model.ScoringResult
import java.time.Instant
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AssessmentRepository @Inject constructor(
    private val db: SpineIQDatabase,
    private val assessmentDao: AssessmentDao,
    private val scoresDao: ScoresDao
) {
    // ── Start / lifecycle ─────────────────────────────────────────────────────

    /**
     * Creates a new IN_PROGRESS assessment record linked to the user and returns its ID.
     * Section data rows are written separately as the user progresses through screens.
     */
    suspend fun startAssessment(userId: String): String {
        // Discard any abandoned in-progress draft before starting a new one. The FK cascade
        // removes its section rows, preventing unbounded orphan accumulation (there is no
        // resume UI yet — Section 15.5). Completed assessments are untouched.
        assessmentDao.deleteInProgressAssessments()
        val id = UUID.randomUUID().toString()
        val now = Instant.now().toEpochMilli()
        assessmentDao.insertAssessmentRecord(
            AssessmentRecordEntity(
                id             = id,
                userId         = userId,
                assessmentDate = LocalDate.now().toEpochDay(),
                status         = AssessmentStatus.IN_PROGRESS,
                createdAt      = now
            )
        )
        return id
    }

    /**
     * Marks the assessment COMPLETED, stores computed scores, records completedAt.
     * Must be called after all section data has been saved.
     */
    suspend fun completeAssessment(assessmentId: String, scoringResult: ScoringResult) {
        val now = Instant.now().toEpochMilli()
        // Atomic: the scores insert and the status flip commit together or roll back together,
        // so a failure can never leave a COMPLETED record with no scores row (which the history
        // INNER JOIN would silently drop, and Results would render as "not available").
        // Scores first, status last, so even a non-transactional failure stays resumable.
        db.withTransaction {
            val record = assessmentDao.getAssessmentRecordOnce(assessmentId) ?: return@withTransaction
            scoresDao.insertScores(
                ScoresRecordEntity.fromScoringResult(assessmentId, scoringResult, now)
            )
            assessmentDao.updateAssessmentRecord(
                record.copy(status = AssessmentStatus.COMPLETED, completedAt = now)
            )
        }
    }

    // ── Section persistence ───────────────────────────────────────────────────

    suspend fun saveOccupationData(data: OccupationDataEntity) =
        assessmentDao.upsertOccupationData(data)

    suspend fun saveLifestyleData(data: LifestyleDataEntity) =
        assessmentDao.upsertLifestyleData(data)

    suspend fun savePainData(data: PainDataEntity) =
        assessmentDao.upsertPainData(data)

    suspend fun saveFunctionalData(data: FunctionalDataEntity) =
        assessmentDao.upsertFunctionalData(data)

    suspend fun saveRedFlagData(data: RedFlagDataEntity) =
        assessmentDao.upsertRedFlagData(data)

    // ── Reads ─────────────────────────────────────────────────────────────────

    fun getAssessmentsForUser(userId: String): Flow<List<AssessmentRecordEntity>> =
        assessmentDao.getAssessmentsForUser(userId)

    fun getCompletedAssessmentCount(userId: String): Flow<Int> =
        assessmentDao.getCompletedAssessmentCount(userId)

    /** Emits any in-progress assessment on app launch so the UI can offer resume/discard (Section 15.5). */
    fun getInProgressAssessment(): Flow<AssessmentRecordEntity?> =
        assessmentDao.getInProgressAssessment()

    suspend fun getFullAssessment(assessmentId: String): FullAssessmentData? =
        assessmentDao.getFullAssessment(assessmentId)

    fun getScores(assessmentId: String): Flow<ScoresRecordEntity?> =
        scoresDao.getScores(assessmentId)

    fun getScoresHistory(userId: String): Flow<List<ScoresRecordEntity>> =
        scoresDao.getScoresHistoryForUser(userId)

    fun getHeartRateHistory(userId: String): Flow<List<LifestyleDataEntity>> =
        assessmentDao.getHeartRateHistory(userId)
}
