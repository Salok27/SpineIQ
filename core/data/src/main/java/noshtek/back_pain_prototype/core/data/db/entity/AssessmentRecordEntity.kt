package noshtek.back_pain_prototype.core.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import noshtek.back_pain_prototype.core.data.model.AssessmentStatus

/**
 * Top-level assessment record linking a patient to one assessment session (Section 15.2).
 *
 * Section data is stored in separate entities (OccupationDataEntity, LifestyleDataEntity, etc.)
 * all linked via [assessmentId]. Cascade delete ensures no orphaned section data remains
 * when a patient profile is deleted.
 */
@Entity(
    tableName = "assessment_records",
    foreignKeys = [
        ForeignKey(
            entity = PatientProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["patient_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("patient_id"), Index("status")]
)
data class AssessmentRecordEntity(

    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "patient_id")
    val patientId: String,

    /** Epoch days. */
    @ColumnInfo(name = "assessment_date")
    val assessmentDate: Long,

    @ColumnInfo(name = "status")
    val status: AssessmentStatus,

    /** Epoch millis. */
    @ColumnInfo(name = "created_at")
    val createdAt: Long,

    /** Epoch millis — null until assessment is COMPLETED. */
    @ColumnInfo(name = "completed_at")
    val completedAt: Long? = null
)
