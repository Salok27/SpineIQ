package noshtek.back_pain_prototype.core.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import noshtek.back_pain_prototype.core.scoring.model.FunctionalLevel

/**
 * SSS Modified ODI functional assessment section (Screen 7, Section 8.5).
 *
 * Five activities scored Normal(0) / Mild Difficulty(1) / Severe Difficulty(2).
 * Matches the SSS Modified ODI variant exactly (OQ-04).
 */
@Entity(
    tableName = "functional_data",
    foreignKeys = [
        ForeignKey(
            entity = AssessmentRecordEntity::class,
            parentColumns = ["id"],
            childColumns = ["assessment_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class FunctionalDataEntity(

    @PrimaryKey
    @ColumnInfo(name = "assessment_id")
    val assessmentId: String,

    @ColumnInfo(name = "walking")
    val walking: FunctionalLevel,

    @ColumnInfo(name = "sitting")
    val sitting: FunctionalLevel,

    @ColumnInfo(name = "standing")
    val standing: FunctionalLevel,

    @ColumnInfo(name = "sleep")
    val sleep: FunctionalLevel,

    @ColumnInfo(name = "daily_activities")
    val dailyActivities: FunctionalLevel
)
