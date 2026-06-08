package noshtek.back_pain_prototype.core.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import noshtek.back_pain_prototype.core.data.model.Gender

/**
 * Single user profile for this installation (FR-01, Section 15.2).
 * One record per install — no patient list, no multi-user support (FR-14, OQ-02).
 */
@Entity(tableName = "user_profiles")
data class UserProfileEntity(

    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

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

    /** Epoch millis. */
    @ColumnInfo(name = "created_at")
    val createdAt: Long,

    /** Epoch millis. */
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long
)
