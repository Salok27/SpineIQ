package noshtek.back_pain_prototype.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Brush

/** Diagonal blue → sky → teal brand gradient (top-start → bottom-end). */
@Composable
@ReadOnlyComposable
fun brandGradient(): Brush = Brush.linearGradient(SpineIQTheme.colors.brandStops)

/** Horizontal variant of the brand gradient — used on wide hero banners. */
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
