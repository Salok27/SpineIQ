@file:OptIn(androidx.compose.ui.text.ExperimentalTextApi::class)

package noshtek.back_pain_prototype.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import noshtek.back_pain_prototype.R

// ── AURA dual-font system ────────────────────────────────────────────────────
// Fraunces (variable, OFL) — a soft humanist serif — drives display/headline
// and all hero numerals, giving the calm, premium "Oak/Headspace" warmth.
// Plus Jakarta Sans (variable, OFL) stays on title/body/label for clean,
// humanist legibility. Both are single variable TTFs driving the `wght` axis
// (API 26+). If a resource ever fails to load, Compose falls back to the
// platform default — never a crash.

private fun fraunces(weight: FontWeight) = Font(
    resId = R.font.fraunces,
    weight = weight,
    variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight)),
)

private fun jakarta(weight: FontWeight) = Font(
    resId = R.font.plus_jakarta_sans,
    weight = weight,
    variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight)),
)

private val Fraunces = FontFamily(
    fraunces(FontWeight.Normal),
    fraunces(FontWeight.Medium),
    fraunces(FontWeight.SemiBold),
    fraunces(FontWeight.Bold),
)

private val Jakarta = FontFamily(
    jakarta(FontWeight.Normal),
    jakarta(FontWeight.Medium),
    jakarta(FontWeight.SemiBold),
    jakarta(FontWeight.Bold),
)

// Display/headline: Fraunces with near-zero tracking and airy line heights for
// a soft, editorial wellness feel. Title/body/label: Plus Jakarta Sans with
// gentle tracking (the old wide-tracked uppercase "micro-label" is dropped —
// see MicroLabel, which now renders sentence case).
val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = Fraunces, fontWeight = FontWeight.Bold,
        fontSize = 52.sp, lineHeight = 60.sp, letterSpacing = (-0.4).sp,
    ),
    displayMedium = TextStyle(
        fontFamily = Fraunces, fontWeight = FontWeight.Bold,
        fontSize = 40.sp, lineHeight = 48.sp, letterSpacing = (-0.2).sp,
    ),
    displaySmall = TextStyle(
        fontFamily = Fraunces, fontWeight = FontWeight.Bold,
        fontSize = 32.sp, lineHeight = 40.sp, letterSpacing = 0.sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = Fraunces, fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp, lineHeight = 36.sp, letterSpacing = 0.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = Fraunces, fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp, lineHeight = 32.sp, letterSpacing = 0.sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = Fraunces, fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp, lineHeight = 28.sp, letterSpacing = 0.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = Jakarta, fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp, lineHeight = 26.sp, letterSpacing = 0.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = Jakarta, fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp, lineHeight = 22.sp, letterSpacing = 0.1.sp,
    ),
    titleSmall = TextStyle(
        fontFamily = Jakarta, fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = Jakarta, fontWeight = FontWeight.Normal,
        fontSize = 16.sp, lineHeight = 26.sp, letterSpacing = 0.1.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = Jakarta, fontWeight = FontWeight.Normal,
        fontSize = 14.sp, lineHeight = 22.sp, letterSpacing = 0.1.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = Jakarta, fontWeight = FontWeight.Normal,
        fontSize = 12.sp, lineHeight = 18.sp, letterSpacing = 0.1.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = Jakarta, fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.2.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = Jakarta, fontWeight = FontWeight.Medium,
        fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.5.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = Jakarta, fontWeight = FontWeight.Medium,
        fontSize = 11.sp, lineHeight = 16.sp, letterSpacing = 0.6.sp,
    ),
)
