package noshtek.back_pain_prototype.core.data.gamification

/**
 * Inputs an achievement predicate can see. Built by the manager from current
 * persisted counts after each gamification event. All values are monotonic
 * (they only grow), so evaluating against slightly stale context can never
 * unlock something incorrectly — at worst an unlock is delayed one event.
 */
data class AchievementContext(
    val completedAssessmentCount: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val checkInCount: Int = 0,
    val stepCompletionCounts: Map<AssessmentStep, Int> = emptyMap(),
    val totalXp: Int = 0,
    val ownedItemCount: Int = 0,
)

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val coinReward: Int,
    val xpReward: Int,
    /** Optional 0..1 progress hint for locked-badge UI; null = no meaningful partial progress. */
    val progress: ((AchievementContext) -> Float)? = null,
    val isUnlocked: (AchievementContext) -> Boolean,
)

/**
 * Static achievement definitions. Persistence stores only unlock state
 * (achievement_unlocks rows keyed by these ids), so the catalog can grow
 * freely without schema changes.
 */
object AchievementCatalog {

    private fun ratio(have: Int, need: Int): Float = (have.toFloat() / need).coerceIn(0f, 1f)

    val ALL: List<Achievement> = listOf(
        Achievement(
            "first_assessment", "First Assessment",
            "Complete your first spine assessment.",
            coinReward = 25, xpReward = 50,
            progress = { ratio(it.completedAssessmentCount, 1) },
            isUnlocked = { it.completedAssessmentCount >= 1 },
        ),
        Achievement(
            "assessment_master", "Assessment Master",
            "Complete 5 spine assessments.",
            coinReward = 75, xpReward = 150,
            progress = { ratio(it.completedAssessmentCount, 5) },
            isUnlocked = { it.completedAssessmentCount >= 5 },
        ),
        Achievement(
            "recovery_champion", "Recovery Champion",
            "Complete 10 spine assessments.",
            coinReward = 150, xpReward = 300,
            progress = { ratio(it.completedAssessmentCount, 10) },
            isUnlocked = { it.completedAssessmentCount >= 10 },
        ),
        Achievement(
            "pain_detective", "Pain Detective",
            "Complete the pain section 3 times.",
            coinReward = 30, xpReward = 60,
            progress = { ratio(it.stepCompletionCounts[AssessmentStep.PAIN] ?: 0, 3) },
            isUnlocked = { (it.stepCompletionCounts[AssessmentStep.PAIN] ?: 0) >= 3 },
        ),
        Achievement(
            "functional_expert", "Functional Expert",
            "Complete the functional section 3 times.",
            coinReward = 30, xpReward = 60,
            progress = { ratio(it.stepCompletionCounts[AssessmentStep.FUNCTIONAL] ?: 0, 3) },
            isUnlocked = { (it.stepCompletionCounts[AssessmentStep.FUNCTIONAL] ?: 0) >= 3 },
        ),
        Achievement(
            "red_flag_aware", "Red Flag Aware",
            "Complete the red-flag screening 3 times.",
            coinReward = 30, xpReward = 60,
            progress = { ratio(it.stepCompletionCounts[AssessmentStep.RED_FLAGS] ?: 0, 3) },
            isUnlocked = { (it.stepCompletionCounts[AssessmentStep.RED_FLAGS] ?: 0) >= 3 },
        ),
        Achievement(
            "daily_devotee", "Daily Devotee",
            "Check in 10 times.",
            coinReward = 40, xpReward = 80,
            progress = { ratio(it.checkInCount, 10) },
            isUnlocked = { it.checkInCount >= 10 },
        ),
        Achievement(
            "streak_3", "On a Roll",
            "Reach a 3-day streak.",
            coinReward = 30, xpReward = 60,
            progress = { ratio(it.longestStreak, 3) },
            isUnlocked = { it.longestStreak >= 3 },
        ),
        Achievement(
            "streak_7", "Week Warrior",
            "Reach a 7-day streak.",
            coinReward = 60, xpReward = 120,
            progress = { ratio(it.longestStreak, 7) },
            isUnlocked = { it.longestStreak >= 7 },
        ),
        Achievement(
            "streak_30", "Habit Hero",
            "Reach a 30-day streak.",
            coinReward = 200, xpReward = 400,
            progress = { ratio(it.longestStreak, 30) },
            isUnlocked = { it.longestStreak >= 30 },
        ),
        Achievement(
            "first_purchase", "Fresh Look",
            "Buy your first avatar item.",
            coinReward = 20, xpReward = 40,
            progress = { ratio(it.ownedItemCount, 1) },
            isUnlocked = { it.ownedItemCount >= 1 },
        ),
    )

    fun byId(id: String): Achievement? = ALL.firstOrNull { it.id == id }
}
