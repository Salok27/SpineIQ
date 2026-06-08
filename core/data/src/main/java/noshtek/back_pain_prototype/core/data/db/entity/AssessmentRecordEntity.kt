package noshtek.back_pain_prototype.core.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import noshtek.back_pain_prototype.core.data.model.AssessmentStatus

/**
 * Top-level assessment record linking the user profile to one assessment session (Section 15.2).
 * Section data is stored in separate entities all linked via [id]. Cascade delete removes all
 * section data when the user profile is deleted (Section 15.3).
 */
@Entity(
    tableName = "assessment_records",
    foreignKeys = [
        ForeignKey(
            entity = UserProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("user_id"), Index("status")]
)
data class AssessmentRecordEntity(

    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "user_id")
    val userId: String,

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
