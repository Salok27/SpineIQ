package noshtek.back_pain_prototype.core.data.gamification

data class GameLevel(val number: Int, val name: String, val xpThreshold: Int)

/**
 * Level progression derived purely from total XP — the level is never
 * persisted, so the table can be retuned without migrating data.
 */
object LevelTable {
    val LEVELS: List<GameLevel> = listOf(
        GameLevel(1, "Beginner", 0),
        GameLevel(2, "Explorer", 100),
        GameLevel(3, "Spine Explorer", 250),
        GameLevel(4, "Recovery Champion", 500),
        GameLevel(5, "Wellness Warrior", 1000),
        GameLevel(6, "Mobility Master", 2000),
        GameLevel(7, "Spine Guardian", 3500),
        GameLevel(8, "Back Health Expert", 5500),
    )

    fun levelFor(xp: Int): GameLevel {
        val clamped = xp.coerceAtLeast(0)
        return LEVELS.last { clamped >= it.xpThreshold }
    }

    fun nextLevel(xp: Int): GameLevel? {
        val clamped = xp.coerceAtLeast(0)
        return LEVELS.firstOrNull { clamped < it.xpThreshold }
    }

    /** 0..1 progress within the current level band; 1 at max level. */
    fun progressToNext(xp: Int): Float {
        val current = levelFor(xp)
        val next = nextLevel(xp) ?: return 1f
        val band = next.xpThreshold - current.xpThreshold
        return (xp.coerceAtLeast(0) - current.xpThreshold).toFloat() / band
    }

    fun xpIntoLevel(xp: Int): Int = xp.coerceAtLeast(0) - levelFor(xp).xpThreshold

    /** XP span of the current level band, or null at max level. */
    fun xpForNextLevel(xp: Int): Int? =
        nextLevel(xp)?.let { it.xpThreshold - levelFor(xp).xpThreshold }
}
