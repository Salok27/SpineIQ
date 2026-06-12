package noshtek.back_pain_prototype.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Brush

/** Diagonal cyan → indigo → magenta aurora gradient (top-start → bottom-end). */
@Composable
@ReadOnlyComposable
fun brandGradient(): Brush = Brush.linearGradient(SpineIQTheme.colors.brandStops)

/** Horizontal variant of the aurora gradient — used on wide hero banners. */
@Composable
@ReadOnlyComposable
fun brandGradientHorizontal(): Brush =
    Brush.horizontalGradient(SpineIQTheme.colors.brandStops)

/** Diagonal indigo → violet → purple reward gradient — XP bars, level rings, celebrations. */
@Composable
@ReadOnlyComposable
fun rewardGradient(): Brush = Brush.linearGradient(SpineIQTheme.colors.rewardStops)

/** Horizontal variant of the reward gradient — progress tracks and wide fills. */
@Composable
@ReadOnlyComposable
fun rewardGradientHorizontal(): Brush =
    Brush.horizontalGradient(SpineIQTheme.colors.rewardStops)

/** Radial gold sheen for coin glyphs and coin surfaces. */
@Composable
@ReadOnlyComposable
fun coinGradient(): Brush =
    Brush.radialGradient(listOf(CoinGoldLight, CoinGold, CoinGoldDeep))

/**
 * Faded aurora sweep for 1dp panel borders — the signature hairline that makes
 * dark panels read as lit from within. Alpha keeps it a glint, not a stripe.
 */
@Composable
@ReadOnlyComposable
fun auroraBorderBrush(alpha: Float = 0.35f): Brush =
    Brush.linearGradient(SpineIQTheme.colors.brandStops.map { it.copy(alpha = alpha) })

/** Reward-tinted variant of the border hairline — equipped/unlocked states. */
@Composable
@ReadOnlyComposable
fun rewardBorderBrush(alpha: Float = 0.45f): Brush =
    Brush.linearGradient(SpineIQTheme.colors.rewardStops.map { it.copy(alpha = alpha) })
