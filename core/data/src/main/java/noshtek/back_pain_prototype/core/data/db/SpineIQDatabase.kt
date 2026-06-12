package noshtek.back_pain_prototype.core.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import noshtek.back_pain_prototype.core.data.db.converters.Converters
import noshtek.back_pain_prototype.core.data.db.dao.AssessmentDao
import noshtek.back_pain_prototype.core.data.db.dao.AvatarDao
import noshtek.back_pain_prototype.core.data.db.dao.GamificationDao
import noshtek.back_pain_prototype.core.data.db.dao.ScoresDao
import noshtek.back_pain_prototype.core.data.db.dao.UserProfileDao
import noshtek.back_pain_prototype.core.data.db.entity.*

/**
 * SpineIQ Room database — encrypted at rest via SQLCipher (NFR-05).
 *
 * Version history:
 *   1 — initial schema (Phase 1, clinic-mode prototype)
 *   2 — D2C pivot: patient_profiles → user_profiles, patient_id → user_id (fallbackToDestructiveMigration)
 *   3 — V2 gamification: gamification_state, reward_ledger, achievement_unlocks,
 *       daily_checkins, avatar_items (fallbackToDestructiveMigration)
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
        ScoresRecordEntity::class,
        GamificationStateEntity::class,
        RewardLedgerEntity::class,
        AchievementUnlockEntity::class,
        DailyCheckInEntity::class,
        AvatarItemEntity::class
    ],
    version = 3,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class SpineIQDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun assessmentDao(): AssessmentDao
    abstract fun scoresDao(): ScoresDao
    abstract fun gamificationDao(): GamificationDao
    abstract fun avatarDao(): AvatarDao

    companion object {
        const val DATABASE_NAME = "spineiq.db"
    }
}
