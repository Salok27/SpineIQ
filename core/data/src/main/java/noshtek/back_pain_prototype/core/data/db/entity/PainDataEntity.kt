package noshtek.back_pain_prototype.core.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import noshtek.back_pain_prototype.core.data.model.*
import noshtek.back_pain_prototype.core.scoring.model.PainDuration
import noshtek.back_pain_prototype.core.scoring.model.RadiculopathySeverity

/** Pain Assessment section data (Screen 6, Section 8.4). */
@Entity(
    tableName = "pain_data",
    foreignKeys = [
        ForeignKey(
            entity = AssessmentRecordEntity::class,
            parentColumns = ["id"],
            childColumns = ["assessment_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PainDataEntity(

    @PrimaryKey
    @ColumnInfo(name = "assessment_id")
    val assessmentId: String,

    /** Comma-separated PainLocation names; converted by Converters (FR-05). */
    @ColumnInfo(name = "pain_locations")
    val painLocations: Set<PainLocation>,

    /** VAS 0–10. */
    @ColumnInfo(name = "vas_score")
    val vasScore: Int,

    @ColumnInfo(name = "pain_duration")
    val painDuration: PainDuration,

    @ColumnInfo(name = "pain_pattern")
    val painPattern: PainPattern,

    /** Comma-separated PainTrigger names. */
    @ColumnInfo(name = "pain_triggers")
    val painTriggers: Set<PainTrigger>,

    @ColumnInfo(name = "radiculopathy_severity")
    val radiculopathySeverity: RadiculopathySeverity,

    /** Null when radiculopathySeverity == NONE. */
    @ColumnInfo(name = "radiation_location")
    val radiationLocation: RadiationLocation? = null,

    /** Free-text functional limitations description. */
    @ColumnInfo(name = "functional_limitations_text")
    val functionalLimitationsText: String? = null,

    /** Structured severity rating for functional limitations. */
    @ColumnInfo(name = "functional_limitation_severity")
    val functionalLimitationSeverity: FunctionalLimitationSeverity? = null
)
