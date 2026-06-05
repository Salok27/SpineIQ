package noshtek.back_pain_prototype.core.scoring.thresholds

import noshtek.back_pain_prototype.core.scoring.model.AgeGroup

/**
 * Sitting thresholds in hours/day.
 *
 * [lowMaxExclusive]: sitting below this value scores Low.
 * [highMinInclusive]: sitting at or above this value scores High.
 * Moderate is everything in between.
 *
 * Source: Daily Habit Snapshot reference table (SSS v1.0).
 * The Low and High anchor points are taken from the "good" and "high risk" columns;
 * the Moderate band is interpolated between them per OQ-06.
 */
data class SittingThreshold(val lowMaxExclusive: Float, val highMinInclusive: Float)

/**
 * Walking thresholds in minutes/day.
 *
 * [lowMinInclusive]: walking at or above this value scores Low.
 * [highMaxExclusive]: walking below this value scores High.
 * Moderate is everything in between.
 */
data class WalkingThreshold(val lowMinInclusive: Float, val highMaxExclusive: Float)

/** Age-adjusted thresholds per the Daily Habit Snapshot reference table. */
object AgeThresholds {

    fun sitting(ageGroup: AgeGroup): SittingThreshold = when (ageGroup) {
        AgeGroup.YOUNG_ADULT -> SittingThreshold(lowMaxExclusive = 7f, highMinInclusive = 10f)
        AgeGroup.MID_ADULT   -> SittingThreshold(lowMaxExclusive = 7f, highMinInclusive = 9f)
        AgeGroup.PRE_SENIOR  -> SittingThreshold(lowMaxExclusive = 6f, highMinInclusive = 8f)
        AgeGroup.SENIOR      -> SittingThreshold(lowMaxExclusive = 6f, highMinInclusive = 7f)
    }

    fun walking(ageGroup: AgeGroup): WalkingThreshold = when (ageGroup) {
        AgeGroup.YOUNG_ADULT -> WalkingThreshold(lowMinInclusive = 45f, highMaxExclusive = 20f)
        AgeGroup.MID_ADULT   -> WalkingThreshold(lowMinInclusive = 45f, highMaxExclusive = 20f)
        AgeGroup.PRE_SENIOR  -> WalkingThreshold(lowMinInclusive = 30f, highMaxExclusive = 15f)
        AgeGroup.SENIOR      -> WalkingThreshold(lowMinInclusive = 20f, highMaxExclusive = 10f)
    }
}
