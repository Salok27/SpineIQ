package noshtek.back_pain_prototype.core.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import noshtek.back_pain_prototype.core.data.db.converters.Converters
import noshtek.back_pain_prototype.core.data.db.dao.AssessmentDao
import noshtek.back_pain_prototype.core.data.db.dao.ScoresDao
import noshtek.back_pain_prototype.core.data.db.dao.UserProfileDao
import noshtek.back_pain_prototype.core.data.db.entity.*

/**
 * SpineIQ Room database — encrypted at rest via SQLCipher (NFR-05).
 *
 * Version history:
 *   1 — initial schema (Phase 1, clinic-mode prototype)
 *   2 — D2C pivot: patient_profiles → user_profiles, patient_id → user_id (fallbackToDestructiveMigration)
 *
 * Schema JSON is exported to core/data/schemas/ for migration tracking.
 */
@Database(
    entities = [
        UserProfileEntity::class,
        AssessmentRecordEntity::class,
        OccupationDataEntity::class,
        LifestyleDataEntity::class,
        PainDataEntity::class,
        FunctionalDataEntity::class,
        RedFlagDataEntity::class,
        ScoresRecordEntity::class
    ],
    version = 2,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class SpineIQDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun assessmentDao(): AssessmentDao
    abstract fun scoresDao(): ScoresDao

    companion object {
        const val DATABASE_NAME = "spineiq.db"
    }
}
