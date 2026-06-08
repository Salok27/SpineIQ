package noshtek.back_pain_prototype.core.data.repository

import kotlinx.coroutines.flow.Flow
import noshtek.back_pain_prototype.core.data.db.dao.UserProfileDao
import noshtek.back_pain_prototype.core.data.db.entity.UserProfileEntity
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileRepository @Inject constructor(
    private val userProfileDao: UserProfileDao
) {
    fun getUserProfile(): Flow<UserProfileEntity?> =
        userProfileDao.getUserProfile()

    suspend fun hasProfile(): Boolean =
        userProfileDao.getProfileCount() > 0

    suspend fun createProfile(profile: UserProfileEntity) {
        userProfileDao.insertUserProfile(profile)
    }

    suspend fun updateProfile(profile: UserProfileEntity) {
        val now = Instant.now().toEpochMilli()
        userProfileDao.updateUserProfile(profile.copy(updatedAt = now))
    }

    /** Cascade-deletes the profile and all linked assessments/scores (Section 15.3). */
    suspend fun deleteAllData() {
        userProfileDao.deleteAllProfiles()
    }
}
