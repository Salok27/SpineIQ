package noshtek.back_pain_prototype.core.data.repository

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import noshtek.back_pain_prototype.core.data.db.SpineIQDatabase
import noshtek.back_pain_prototype.core.data.db.dao.AvatarDao
import noshtek.back_pain_prototype.core.data.db.dao.GamificationDao
import noshtek.back_pain_prototype.core.data.db.dao.UserProfileDao
import noshtek.back_pain_prototype.core.data.db.entity.AchievementUnlockEntity
import noshtek.back_pain_prototype.core.data.db.entity.AvatarItemEntity
import noshtek.back_pain_prototype.core.data.db.entity.DailyCheckInEntity
import noshtek.back_pain_prototype.core.data.db.entity.GamificationStateEntity
import noshtek.back_pain_prototype.core.data.db.entity.RewardLedgerEntity
import noshtek.back_pain_prototype.core.data.gamification.Achievement
import noshtek.back_pain_prototype.core.data.gamification.AchievementContext
import noshtek.back_pain_prototype.core.data.gamification.AssessmentStep
import noshtek.back_pain_prototype.core.data.gamification.AvatarCatalog
import noshtek.back_pain_prototype.core.data.gamification.AvatarCatalogItem
import noshtek.back_pain_prototype.core.data.gamification.CheckInMood
import noshtek.back_pain_prototype.core.data.gamification.DedupeKeys
import noshtek.back_pain_prototype.core.data.gamification.PurchaseResult
import noshtek.back_pain_prototype.core.data.gamification.RewardType
import noshtek.back_pain_prototype.core.data.gamification.StreakAdvance
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Transactional persistence layer for the gamification system. Every grant or
 * spend goes through the reward ledger inside one Room transaction, so coins
 * and XP can never be paid out twice or partially. All gamification tables
 * cascade-delete from user_profiles, so the existing "delete all data" flow
 * wipes them with no extra code (Section 15.3).
 */
@Singleton
class GamificationRepository @Inject constructor(
    private val db: SpineIQDatabase,
    private val gamificationDao: GamificationDao,
    private val avatarDao: AvatarDao,
    private val userProfileDao: UserProfileDao,
) {

    /** State immediately before and after a successful grant — lets callers detect level-ups. */
    data class GrantOutcome(
        val before: GamificationStateEntity,
        val after: GamificationStateEntity,
    )

    // ── Reads ─────────────────────────────────────────────────────────────────

    fun observeState(): Flow<GamificationStateEntity?> = gamificationDao.observeState()

    suspend fun getStateOnce(): GamificationStateEntity? = gamificationDao.getStateOnce()

    fun observeUnlocks(): Flow<List<AchievementUnlockEntity>> = gamificationDao.observeUnlocks()

    fun observeOwnedItems(): Flow<List<AvatarItemEntity>> = avatarDao.observeOwned()

    fun observeCheckInHistory(): Flow<List<DailyCheckInEntity>> = gamificationDao.observeCheckInHistory()

    // ── Grants ────────────────────────────────────────────────────────────────

    /**
     * Atomic dedupe-checked grant. Returns null when [dedupeKey] was already
     * used (the reward stays granted exactly once) or when no profile exists.
     */
    suspend fun tryGrant(dedupeKey: String, type: RewardType, coins: Int, xp: Int): GrantOutcome? {
        val userId = currentUserId() ?: return null
        return db.withTransaction {
            val now = Instant.now().toEpochMilli()
            val inserted = gamificationDao.insertLedgerEntry(
                RewardLedgerEntity(dedupeKey, userId, type, coins, xp, now)
            )
            if (inserted == -1L) return@withTransaction null
            val before = getOrCreateState(userId)
            val after = before.copy(
                coins = (before.coins + coins).coerceAtLeast(0),
                xp = (before.xp + xp).coerceAtLeast(0),
                updatedAt = now,
            )
            gamificationDao.upsertState(after)
            GrantOutcome(before, after)
        }
    }

    /** Returns false when a check-in for that day already exists or no profile exists. */
    suspend fun tryRecordCheckIn(epochDay: Long, mood: CheckInMood): Boolean {
        val userId = currentUserId() ?: return false
        return gamificationDao.insertCheckIn(
            DailyCheckInEntity(epochDay, userId, mood, Instant.now().toEpochMilli())
        ) != -1L
    }

    suspend fun applyStreakAdvance(advance: StreakAdvance, today: LocalDate) {
        val userId = currentUserId() ?: return
        db.withTransaction {
            val state = getOrCreateState(userId)
            gamificationDao.upsertState(
                state.copy(
                    currentStreakDays = advance.newStreak,
                    longestStreakDays = advance.newLongest,
                    lastActivityDay = today.toEpochDay(),
                    updatedAt = Instant.now().toEpochMilli(),
                )
            )
        }
    }

    /**
     * Unlock + reward in one transaction. Returns null when already unlocked.
     */
    suspend fun tryUnlockAchievement(achievement: Achievement): GrantOutcome? {
        val userId = currentUserId() ?: return null
        return db.withTransaction {
            val now = Instant.now().toEpochMilli()
            val inserted = gamificationDao.insertUnlock(
                AchievementUnlockEntity(achievement.id, userId, now)
            )
            if (inserted == -1L) return@withTransaction null
            val ledger = gamificationDao.insertLedgerEntry(
                RewardLedgerEntity(
                    DedupeKeys.achievement(achievement.id), userId, RewardType.ACHIEVEMENT,
                    achievement.coinReward, achievement.xpReward, now,
                )
            )
            val before = getOrCreateState(userId)
            if (ledger == -1L) return@withTransaction GrantOutcome(before, before)
            val after = before.copy(
                coins = before.coins + achievement.coinReward,
                xp = before.xp + achievement.xpReward,
                updatedAt = now,
            )
            gamificationDao.upsertState(after)
            GrantOutcome(before, after)
        }
    }

    // ── Shop ──────────────────────────────────────────────────────────────────

    suspend fun tryPurchase(item: AvatarCatalogItem): PurchaseResult {
        if (item.isDefault) return PurchaseResult.AlreadyOwned
        val userId = currentUserId() ?: return PurchaseResult.InsufficientCoins
        return db.withTransaction {
            val now = Instant.now().toEpochMilli()
            val state = getOrCreateState(userId)
            if (state.coins < item.priceCoins) return@withTransaction PurchaseResult.InsufficientCoins
            val owned = avatarDao.insertOwned(
                AvatarItemEntity(item.id, userId, item.category, equipped = false, purchasedAt = now)
            )
            if (owned == -1L) return@withTransaction PurchaseResult.AlreadyOwned
            gamificationDao.insertLedgerEntry(
                RewardLedgerEntity(
                    DedupeKeys.purchase(item.id), userId, RewardType.PURCHASE,
                    -item.priceCoins, 0, now,
                )
            )
            gamificationDao.upsertState(state.copy(coins = state.coins - item.priceCoins, updatedAt = now))
            PurchaseResult.Success
        }
    }

    /**
     * Equips one item per category. Equipping a free default just clears the
     * category — defaults have no DB row and render as the fallback layer.
     */
    suspend fun equipItem(itemId: String) {
        val item = AvatarCatalog.byId(itemId) ?: return
        db.withTransaction {
            avatarDao.unequipCategory(item.category)
            if (!item.isDefault) avatarDao.equip(itemId)
        }
    }

    suspend fun unequipItem(itemId: String) = avatarDao.unequip(itemId)

    // ── Achievement evaluation inputs ─────────────────────────────────────────

    suspend fun getUnlockedIdsOnce(): List<String> = gamificationDao.getUnlockedIdsOnce()

    suspend fun buildAchievementContext(): AchievementContext {
        val state = gamificationDao.getStateOnce()
        return AchievementContext(
            // Ledger counts only include post-gamification activity, which is
            // exact here: the v3 schema bump wipes earlier data anyway.
            completedAssessmentCount = gamificationDao.countLedgerEntriesMatching("complete:%"),
            currentStreak = state?.currentStreakDays ?: 0,
            longestStreak = state?.longestStreakDays ?: 0,
            checkInCount = gamificationDao.getCheckInCount(),
            stepCompletionCounts = AssessmentStep.entries.associateWith { step ->
                gamificationDao.countLedgerEntriesMatching(DedupeKeys.stepPattern(step))
            },
            totalXp = state?.xp ?: 0,
            ownedItemCount = avatarDao.getOwnedCount(),
        )
    }

    // ── Internals ─────────────────────────────────────────────────────────────

    private suspend fun currentUserId(): String? =
        userProfileDao.getUserProfile().firstOrNull()?.id

    private suspend fun getOrCreateState(userId: String): GamificationStateEntity =
        gamificationDao.getStateOnce()
            ?: GamificationStateEntity(userId = userId, updatedAt = Instant.now().toEpochMilli())
}
