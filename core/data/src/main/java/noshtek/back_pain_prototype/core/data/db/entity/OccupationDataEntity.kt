package noshtek.back_pain_prototype.core.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import noshtek.back_pain_prototype.core.data.model.LiftingLevel
import noshtek.back_pain_prototype.core.data.model.OccupationType

/** Occupation & Work Pattern section data (Screen 4, Section 8.2). */
@Entity(
    tableName = "occupation_data",
    foreignKeys = [
        ForeignKey(
            entity = AssessmentRecordEntity::class,
            parentColumns = ["id"],
            childColumns = ["assessment_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class OccupationDataEntity(

    /** Same value as AssessmentRecordEntity.id — one section row per assessment. */
    @PrimaryKey
    @ColumnInfo(name = "assessment_id")
    val assessmentId: String,

    @ColumnInfo(name = "occupation_type")
    val occupationType: OccupationType,

    @ColumnInfo(name = "sitting_hours_per_day")
    val sittingHoursPerDay: Float,

    @ColumnInfo(name = "standing_hours_per_day")
    val standingHoursPerDay: Float,

    @ColumnInfo(name = "driving_hours_per_day")
    val drivingHoursPerDay: Float,

    @ColumnInfo(name = "lifting_level")
    val liftingLevel: LiftingLevel,

    @ColumnInfo(name = "work_pattern_notes")
    val workPatternNotes: String? = null
)
