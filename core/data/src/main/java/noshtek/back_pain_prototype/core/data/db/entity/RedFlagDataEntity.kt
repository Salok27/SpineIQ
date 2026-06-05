package noshtek.back_pain_prototype.core.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Red Flag Screening section (Screen 8, Section 8.6).
 *
 * Any field set to true triggers the red-flag override rule (Section 9.3):
 * the displayed SSS score is forced to 11 and severity to Severe/High Risk.
 */
@Entity(
    tableName = "red_flag_data",
    foreignKeys = [
        ForeignKey(
            entity = AssessmentRecordEntity::class,
            parentColumns = ["id"],
            childColumns = ["assessment_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class RedFlagDataEntity(

    @PrimaryKey
    @ColumnInfo(name = "assessment_id")
    val assessmentId: String,

    @ColumnInfo(name = "history_cancer")
    val historyCancer: Boolean = false,

    @ColumnInfo(name = "unexplained_weight_loss")
    val unexplainedWeightLoss: Boolean = false,

    @ColumnInfo(name = "fever_or_infection")
    val feverOrInfection: Boolean = false,

    @ColumnInfo(name = "recent_major_trauma")
    val recentMajorTrauma: Boolean = false,

    @ColumnInfo(name = "bowel_bladder_dysfunction")
    val bowelBladderDysfunction: Boolean = false,

    @ColumnInfo(name = "saddle_anaesthesia")
    val saddleAnaesthesia: Boolean = false,

    @ColumnInfo(name = "progressive_neurological_deficit")
    val progressiveNeurologicalDeficit: Boolean = false,

    @ColumnInfo(name = "other_serious_pathology_suspicion")
    val otherSeriousPathologySuspicion: Boolean = false
) {
    /** True if any red flag is confirmed — drives the override rule (Section 9.3). */
    val hasAnyRedFlag: Boolean
        get() = historyCancer || unexplainedWeightLoss || feverOrInfection ||
                recentMajorTrauma || bowelBladderDysfunction || saddleAnaesthesia ||
                progressiveNeurologicalDeficit || otherSeriousPathologySuspicion
}
