package noshtek.back_pain_prototype.core.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import noshtek.back_pain_prototype.core.data.model.Gender

/**
 * Persisted patient profile (Section 15.2).
 *
 * Dates are stored as epoch-day Longs; timestamps as epoch-milli Longs.
 * BMI is NOT stored here — it is computed at assessment time and stored in ScoresRecordEntity.
 */
@Entity(tableName = "patient_profiles")
data class PatientProfileEntity(

    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,                         // UUID string

    @ColumnInfo(name = "full_name")
    val fullName: String,

    /** Epoch days — use Converters.localDateToLong / longToLocalDate. */
    @ColumnInfo(name = "date_of_birth")
    val dateOfBirth: Long,

    @ColumnInfo(name = "gender")
    val gender: Gender,

    @ColumnInfo(name = "height_cm")
    val heightCm: Float,

    @ColumnInfo(name = "weight_kg")
    val weightKg: Float,

    @ColumnInfo(name = "phone_number")
    val phoneNumber: String? = null,

    /** Free-form OPD/patient ID — no external system validation (OQ-10). */
    @ColumnInfo(name = "patient_id_external")
    val patientIdExternal: String? = null,

    /** Epoch millis. */
    @ColumnInfo(name = "created_at")
    val createdAt: Long,

    /** Epoch millis. */
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long
)
