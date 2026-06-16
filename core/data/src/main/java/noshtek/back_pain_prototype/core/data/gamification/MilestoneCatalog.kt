package noshtek.back_pain_prototype.core.data.gamification

enum class MilestoneCategory { JOURNEY, VITALITY, CONSISTENCY }

/**
 * Inputs a milestone predicate can see. Built by the manager from current
 * persisted counts after each event. All values are monotonic (only grow), so
 * evaluating against slightly stale context can never unlock incorrectly — at
 * worst an unlock is delayed one event.
 */
data class MilestoneContext(
    val completedAssessmentCount: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val checkInCount: Int = 0,
    val totalRitualCompletions: Int = 0,
    /** Monotonic high-water mark of Spine Vitality. */
    val peakVitality: Int = 0,
)

data class Milestone(
    val id: String,
    val title: String,
    val description: String,
    val category: MilestoneCategory,
    /** Optional 0..1 progress hint for locked milestones; null = no partial progress. */
    val progress: ((MilestoneContext) -> Float)? = null,
    val isUnlocked: (MilestoneContext) -> Boolean,
)

/**
 * Recovery-journey milestones — replaces the old badge catalog. Persistence
 * stores only the unlock fact (keyed by id), so the catalog can grow without
 * schema changes. Every predicate is monotonic.
 */
object MilestoneCatalog {

    private fun ratio(have: Int, need: Int): Float = (have.toFloat() / need).coerceIn(0f, 1f)

    val ALL: List<Milestone> = listOf(
        Milestone(
            "first_assessment", "First Step",
            "Complete your first spine assessment.", MilestoneCategory.JOURNEY,
            progress = { ratio(it.completedAssessmentCount, 1) },
            isUnlocked = { it.completedAssessmentCount >= 1 },
        ),
        Milestone(
            "reassess_3", "Staying Aware",
            "Complete 3 spine assessments.", MilestoneCategory.JOURNEY,
            progress = { ratio(it.completedAssessmentCount, 3) },
            isUnlocked = { it.completedAssessmentCount >= 3 },
        ),
        Milestone(
            "reassess_10", "Dedicated to Recovery",
            "Complete 10 spine assessments.", MilestoneCategory.JOURNEY,
            progress = { ratio(it.completedAssessmentCount, 10) },
            isUnlocked = { it.completedAssessmentCount >= 10 },
        ),
        Milestone(
            "vitality_steady", "Finding Balance",
            "Reach 50 Spine Vitality.", MilestoneCategory.VITALITY,
            progress = { ratio(it.peakVitality, 50) },
            isUnlocked = { it.peakVitality >= 50 },
        ),
        Milestone(
            "vitality_bright", "Glowing",
            "Reach 75 Spine Vitality.", MilestoneCategory.VITALITY,
            progress = { ratio(it.peakVitality, 75) },
            isUnlocked = { it.peakVitality >= 75 },
        ),
        Milestone(
            "vitality_radiant", "Radiant Spine",
            "Reach 90 Spine Vitality.", MilestoneCategory.VITALITY,
            progress = { ratio(it.peakVitality, 90) },
            isUnlocked = { it.peakVitality >= 90 },
        ),
        Milestone(
            "streak_3", "On a Roll",
            "Reach a 3-day streak.", MilestoneCategory.CONSISTENCY,
            progress = { ratio(it.longestStreak, 3) },
            isUnlocked = { it.longestStreak >= 3 },
        ),
        Milestone(
            "streak_7", "Week Warrior",
            "Reach a 7-day streak.", MilestoneCategory.CONSISTENCY,
            progress = { ratio(it.longestStreak, 7) },
            isUnlocked = { it.longestStreak >= 7 },
        ),
        Milestone(
            "streak_30", "Habit Hero",
            "Reach a 30-day streak.", MilestoneCategory.CONSISTENCY,
            progress = { ratio(it.longestStreak, 30) },
            isUnlocked = { it.longestStreak >= 30 },
        ),
        Milestone(
            "rituals_25", "Ritual Keeper",
            "Complete 25 daily rituals.", MilestoneCategory.CONSISTENCY,
            progress = { ratio(it.totalRitualCompletions, 25) },
            isUnlocked = { it.totalRitualCompletions >= 25 },
        ),
        Milestone(
            "rituals_100", "Centurion",
            "Complete 100 daily rituals.", MilestoneCategory.CONSISTENCY,
            progress = { ratio(it.totalRitualCompletions, 100) },
            isUnlocked = { it.totalRitualCompletions >= 100 },
        ),
        Milestone(
            "checkins_10", "Daily Devotee",
            "Check in 10 times.", MilestoneCategory.CONSISTENCY,
            progress = { ratio(it.checkInCount, 10) },
            isUnlocked = { it.checkInCount >= 10 },
        ),
    )

    fun byId(id: String): Milestone? = ALL.firstOrNull { it.id == id }
}
