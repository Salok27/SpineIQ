package noshtek.back_pain_prototype.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Brush

/** Diagonal blue → indigo → violet brand gradient (top-start → bottom-end). */
@Composable
@ReadOnlyComposable
fun brandGradient(): Brush = Brush.linearGradient(SpineIQTheme.colors.brandStops)

/** Horizontal variant of the brand gradient — used on wide hero banners. */
@Composable
@ReadOnlyComposable
fun brandGradientHorizontal(): Brush =
    Brush.horizontalGradient(SpineIQTheme.colors.brandStops)
