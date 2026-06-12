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

// ── NEON AURORA dual-font system ─────────────────────────────────────────────
// Space Grotesk (variable, OFL) drives display/headline/title — geometric and
// techy, it carries the futuristic identity and all hero numerals.
// Plus Jakarta Sans (variable, OFL) stays on body/label for long-form
// legibility. Both are single variable TTFs driving the `wght` axis (API 26+).
// If a resource ever fails to load, Compose falls back to the platform
// default — never a crash.

private fun grotesk(weight: FontWeight) = Font(
    resId = R.font.space_grotesk,
    weight = weight,
    variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight)),
)

private fun jakarta(weight: FontWeight) = Font(
    resId = R.font.plus_jakarta_sans,
    weight = weight,
    variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight)),
)

// Space Grotesk's wght axis spans 300–700 — Bold (700) is its heaviest cut.
private val Grotesk = FontFamily(
    grotesk(FontWeight.Normal),
    grotesk(FontWeight.Medium),
    grotesk(FontWeight.SemiBold),
    grotesk(FontWeight.Bold),
)

private val Jakarta = FontFamily(
    jakarta(FontWeight.Normal),
    jakarta(FontWeight.Medium),
    jakarta(FontWeight.SemiBold),
    jakarta(FontWeight.Bold),
)

// Display/headline: Grotesk Bold with tight tracking for the sci-fi HUD feel.
// Labels: wide positive tracking — the uppercase "micro-label" signature.
val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = Grotesk, fontWeight = FontWeight.Bold,
        fontSize = 52.sp, lineHeight = 56.sp, letterSpacing = (-1.2).sp,
    ),
    displayMedium = TextStyle(
        fontFamily = Grotesk, fontWeight = FontWeight.Bold,
        fontSize = 40.sp, lineHeight = 46.sp, letterSpacing = (-0.8).sp,
    ),
    displaySmall = TextStyle(
        fontFamily = Grotesk, fontWeight = FontWeight.Bold,
        fontSize = 32.sp, lineHeight = 40.sp, letterSpacing = (-0.5).sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = Grotesk, fontWeight = FontWeight.Bold,
        fontSize = 28.sp, lineHeight = 36.sp, letterSpacing = (-0.4).sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = Grotesk, fontWeight = FontWeight.Bold,
        fontSize = 24.sp, lineHeight = 32.sp, letterSpacing = (-0.3).sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = Grotesk, fontWeight = FontWeight.Bold,
        fontSize = 20.sp, lineHeight = 28.sp, letterSpacing = (-0.2).sp,
    ),
    titleLarge = TextStyle(
        fontFamily = Grotesk, fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp, lineHeight = 26.sp, letterSpacing = 0.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = Grotesk, fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp, lineHeight = 22.sp, letterSpacing = 0.1.sp,
    ),
    titleSmall = TextStyle(
        fontFamily = Grotesk, fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.2.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = Jakarta, fontWeight = FontWeight.Normal,
        fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.1.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = Jakarta, fontWeight = FontWeight.Normal,
        fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = Jakarta, fontWeight = FontWeight.Normal,
        fontSize = 12.sp, lineHeight = 17.sp, letterSpacing = 0.2.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = Jakarta, fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.2.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = Jakarta, fontWeight = FontWeight.Medium,
        fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 1.2.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = Jakarta, fontWeight = FontWeight.Medium,
        fontSize = 11.sp, lineHeight = 16.sp, letterSpacing = 1.4.sp,
    ),
)
