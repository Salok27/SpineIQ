package noshtek.back_pain_prototype.core.data.gamification

/**
 * The five wizard sections that persist data. The Review step completes the
 * assessment instead of saving a section, so it has no step reward — the
 * completion reward covers it.
 */
enum class AssessmentStep { OCCUPATION, LIFESTYLE, PAIN, FUNCTIONAL, RED_FLAGS }

enum class CheckInMood { BETTER, SAME, WORSE }

enum class RewardType { STEP, COMPLETION, CHECK_IN, STREAK_MILESTONE, ACHIEVEMENT, PURCHASE }

sealed interface CheckInResult {
    data class Done(val streakDays: Int) : CheckInResult
    data object AlreadyCheckedInToday : CheckInResult
}

sealed interface PurchaseResult {
    data object Success : PurchaseResult
    data object InsufficientCoins : PurchaseResult
    data object AlreadyOwned : PurchaseResult
    data object UnknownItem : PurchaseResult
}

/** Read model combining persisted state with derived level/streak values. */
data class GamificationSnapshot(
    val coins: Int = 0,
    val xp: Int = 0,
    val level: GameLevel = LevelTable.LEVELS.first(),
    val nextLevel: GameLevel? = LevelTable.LEVELS.getOrNull(1),
    val progressToNext: Float = 0f,
    val xpIntoLevel: Int = 0,
    val xpForNextLevel: Int? = LevelTable.LEVELS.getOrNull(1)?.xpThreshold,
    val effectiveStreakDays: Int = 0,
    val longestStreakDays: Int = 0,
    val checkedInToday: Boolean = false,
)

/**
 * Single source of truth for reward-ledger dedupe keys. The ledger's primary
 * key on these strings is what makes every grant idempotent, so the formats
 * here must never drift from the patterns used for achievement counting.
 */
object DedupeKeys {
    fun step(assessmentId: String, step: AssessmentStep) = "step:$assessmentId:${step.name}"
    fun completion(assessmentId: String) = "complete:$assessmentId"
    fun checkIn(epochDay: Long) = "checkin:$epochDay"
    fun streakMilestone(days: Int) = "streak_milestone:$days"
    fun achievement(achievementId: String) = "achievement:$achievementId"
    fun purchase(itemId: String) = "purchase:$itemId"

    /** SQL LIKE pattern matching every step grant of one section type. */
    fun stepPattern(step: AssessmentStep) = "step:%:${step.name}"
}
