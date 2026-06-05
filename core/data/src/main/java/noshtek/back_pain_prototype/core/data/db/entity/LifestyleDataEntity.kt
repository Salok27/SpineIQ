package noshtek.back_pain_prototype.core.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import noshtek.back_pain_prototype.core.data.model.DataSource
import noshtek.back_pain_prototype.core.scoring.model.ExerciseType
import noshtek.back_pain_prototype.core.scoring.model.SleepQuality

/**
 * Lifestyle & Health Data section (Screen 5, Sections 8.3, 14.7).
 *
 * Each field that may be auto-filled from Health Connect carries a [DataSource] column
 * so provenance is preserved per the spec (Section 14.7, AC-02).
 *
 * Fields that are always manual (sittingHoursPerDay, walkingMinutesPerDay,
 * exerciseDaysPerWeek, exerciseTypes, sleepQuality) carry no source column.
 */
@Entity(
    tableName = "lifestyle_data",
    foreignKeys = [
        ForeignKey(
            entity = AssessmentRecordEntity::class,
            parentColumns = ["id"],
            childColumns = ["assessment_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class LifestyleDataEntity(

    @PrimaryKey
    @ColumnInfo(name = "assessment_id")
    val assessmentId: String,

    // ── Always-manual fields ─────────────────────────────────────────────────

    @ColumnInfo(name = "sitting_hours_per_day")
    val sittingHoursPerDay: Float,

    @ColumnInfo(name = "walking_minutes_per_day")
    val walkingMinutesPerDay: Float,

    @ColumnInfo(name = "exercise_days_per_week")
    val exerciseDaysPerWeek: Int,

    /** Comma-separated ExerciseType names; converted by Converters. */
    @ColumnInfo(name = "exercise_types")
    val exerciseTypes: Set<ExerciseType>,

    @ColumnInfo(name = "sleep_quality")
    val sleepQuality: SleepQuality,

    /** True if any selected exercise type is high-impact/spine-loading (Section 9.6). */
    @ColumnInfo(name = "exercise_type_modifier_applied")
    val exerciseTypeModifierApplied: Boolean,

    // ── Health Connect or manual ─────────────────────────────────────────────

    @ColumnInfo(name = "sleep_hours_per_night")
    val sleepHoursPerNight: Float,

    @ColumnInfo(name = "data_source_sleep_hours")
    val dataSourceSleepHours: DataSource,

    @ColumnInfo(name = "daily_steps")
    val dailySteps: Int? = null,

    @ColumnInfo(name = "data_source_steps")
    val dataSourceSteps: DataSource = DataSource.MANUAL,

    @ColumnInfo(name = "active_minutes_per_day")
    val activeMinutesPerDay: Int? = null,

    @ColumnInfo(name = "data_source_active_minutes")
    val dataSourceActiveMinutes: DataSource = DataSource.MANUAL,

    @ColumnInfo(name = "sedentary_time_minutes_per_day")
    val sedentaryTimeMinutesPerDay: Int? = null,

    @ColumnInfo(name = "data_source_sedentary_time")
    val dataSourceSedentaryTime: DataSource = DataSource.MANUAL,

    /** Optional wearable data — displayed as longitudinal trend (Section 9.7). */
    @ColumnInfo(name = "resting_heart_rate")
    val restingHeartRate: Int? = null,

    @ColumnInfo(name = "data_source_resting_heart_rate")
    val dataSourceRestingHeartRate: DataSource = DataSource.MANUAL,

    /** Optional wearable data — displayed as longitudinal trend (Section 9.7). */
    @ColumnInfo(name = "average_heart_rate")
    val averageHeartRate: Int? = null,

    @ColumnInfo(name = "data_source_average_heart_rate")
    val dataSourceAverageHeartRate: DataSource = DataSource.MANUAL
)
