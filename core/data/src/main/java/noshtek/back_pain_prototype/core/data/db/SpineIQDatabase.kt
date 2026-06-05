package noshtek.back_pain_prototype.core.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import noshtek.back_pain_prototype.core.data.db.converters.Converters
import noshtek.back_pain_prototype.core.data.db.dao.AssessmentDao
import noshtek.back_pain_prototype.core.data.db.dao.PatientDao
import noshtek.back_pain_prototype.core.data.db.dao.ScoresDao
import noshtek.back_pain_prototype.core.data.db.entity.*

/**
 * SpineIQ Room database — encrypted at rest via SQLCipher (NFR-05).
 *
 * Version history:
 *   1 — initial schema (Phase 1)
 *
 * Schema JSON is exported to core/data/schemas/ for migration tracking.
 * Always increment [version] and provide a Migration when changing entities.
 */
@Database(
    entities = [
        PatientProfileEntity::class,
        AssessmentRecordEntity::class,
        OccupationDataEntity::class,
        LifestyleDataEntity::class,
        PainDataEntity::class,
        FunctionalDataEntity::class,
        RedFlagDataEntity::class,
        ScoresRecordEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class SpineIQDatabase : RoomDatabase() {
    abstract fun patientDao(): PatientDao
    abstract fun assessmentDao(): AssessmentDao
    abstract fun scoresDao(): ScoresDao

    companion object {
        const val DATABASE_NAME = "spineiq.db"
    }
}
