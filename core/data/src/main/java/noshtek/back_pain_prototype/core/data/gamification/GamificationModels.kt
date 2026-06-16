package noshtek.back_pain_prototype.core.data.gamification

/**
 * The five wizard sections that persist data. The Review step completes the
 * assessment instead of saving a section, so it has no step event — the
 * completion event covers it.
 */
enum class AssessmentStep { OCCUPATION, LIFESTYLE, PAIN, FUNCTIONAL, RED_FLAGS }

enum class CheckInMood { BETTER, SAME, WORSE }

/** What an append-only reward-ledger row records (Aura logs progress events, not currency). */
enum class RewardType { STEP, COMPLETION, CHECK_IN, RITUAL, STREAK_MILESTONE, MILESTONE }

sealed interface CheckInResult {
    data class Done(val streakDays: Int) : CheckInResult
    data object AlreadyCheckedInToday : CheckInResult
}

sealed interface RitualResult {
    data class Done(val ritualsDoneToday: Int) : RitualResult
    data object AlreadyDoneToday : RitualResult
}

/**
 * Read model for the engagement layer: the Living Spine vitality + stage, the
 * streak, today's check-in, and today's personalized rituals with done flags.
 */
data class GamificationSnapshot(
    val vitality: Int = SpineVitality.NEUTRAL_BASE_VITALITY,
    val stage: VitalityStage = VitalityStage.STEADY,
    val peakVitality: Int = 0,
    val effectiveStreakDays: Int = 0,
    val longestStreakDays: Int = 0,
    val checkedInToday: Boolean = false,
    val rituals: List<RitualStatus> = emptyList(),
    val ritualsDoneToday: Int = 0,
    val ritualsTotalToday: Int = 0,
)

/**
 * Single source of truth for reward-ledger dedupe keys. The ledger's primary
 * key on these strings is what makes every event idempotent, so the formats
 * here must never drift from the LIKE patterns used for counting.
 */
object DedupeKeys {
    fun step(assessmentId: String, step: AssessmentStep) = "step:$assessmentId:${step.name}"
    fun completion(assessmentId: String) = "complete:$assessmentId"
    fun checkIn(epochDay: Long) = "checkin:$epochDay"
    fun ritual(ritualId: String, epochDay: Long) = "ritual:$ritualId:$epochDay"
    fun streakMilestone(days: Int) = "streak_milestone:$days"
    fun milestone(milestoneId: String) = "milestone:$milestoneId"

    /** SQL LIKE pattern matching every step event of one section type. */
    fun stepPattern(step: AssessmentStep) = "step:%:${step.name}"

    /** SQL LIKE pattern matching every completed-assessment event. */
    const val COMPLETION_PATTERN = "complete:%"
}
