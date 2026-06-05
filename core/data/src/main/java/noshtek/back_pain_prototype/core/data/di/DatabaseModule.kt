package noshtek.back_pain_prototype.core.data.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import noshtek.back_pain_prototype.core.data.db.DatabaseKeyProvider
import noshtek.back_pain_prototype.core.data.db.SpineIQDatabase
import noshtek.back_pain_prototype.core.data.db.dao.AssessmentDao
import noshtek.back_pain_prototype.core.data.db.dao.PatientDao
import noshtek.back_pain_prototype.core.data.db.dao.ScoresDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideSpineIQDatabase(
        @ApplicationContext context: Context,
        keyProvider: DatabaseKeyProvider
    ): SpineIQDatabase {
        val passphrase = keyProvider.getOrCreatePassphrase()
        val factory = SupportOpenHelperFactory(passphrase)
        return Room.databaseBuilder(
            context,
            SpineIQDatabase::class.java,
            SpineIQDatabase.DATABASE_NAME
        )
            .openHelperFactory(factory)
            .fallbackToDestructiveMigration()   // safe for Phase 1 prototype; replace with Migrations before production
            .build()
    }

    @Provides
    fun providePatientDao(db: SpineIQDatabase): PatientDao = db.patientDao()

    @Provides
    fun provideAssessmentDao(db: SpineIQDatabase): AssessmentDao = db.assessmentDao()

    @Provides
    fun provideScoresDao(db: SpineIQDatabase): ScoresDao = db.scoresDao()
}
