package noshtek.back_pain_prototype.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary                = Blue600,
    onPrimary              = Color.White,
    primaryContainer       = Blue50,
    onPrimaryContainer     = Blue900,
    secondary              = Teal600,
    onSecondary            = Color.White,
    secondaryContainer     = Teal50,
    onSecondaryContainer   = Teal900,
    tertiary               = Violet600,
    onTertiary             = Color.White,
    tertiaryContainer      = Violet50,
    onTertiaryContainer    = Violet900,
    error                  = RiskHigh,
    onError                = Color.White,
    errorContainer         = RiskHighContainer,
    onErrorContainer       = RiskSevereUrgent,
    background             = Slate50,
    onBackground           = Slate900,
    surface                = Color.White,
    onSurface              = Slate900,
    surfaceVariant         = Slate100,
    onSurfaceVariant       = Slate500,
    outline                = Slate300,
    outlineVariant         = Slate200,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow    = Slate50,
    surfaceContainer       = Slate100,
    surfaceContainerHigh   = Slate200,
)

private val DarkColorScheme = darkColorScheme(
    primary                = Blue300,
    onPrimary              = BlueOnDark,
    primaryContainer       = Blue800,
    onPrimaryContainer     = Blue50,
    secondary              = Teal300,
    onSecondary            = Teal900,
    secondaryContainer     = Teal700,
    onSecondaryContainer   = Teal50,
    tertiary               = Violet300,
    onTertiary             = Violet900,
    tertiaryContainer      = Violet700,
    onTertiaryContainer    = Violet50,
    error                  = RiskHighContainer,
    onError                = RiskSevereUrgent,
    errorContainer         = RiskSevereUrgent,
    onErrorContainer       = RiskHighContainer,
    background             = SlateD900,
    onBackground           = Slate200,
    surface                = SlateD100,
    onSurface              = Slate200,
    surfaceVariant         = SlateD300,
    onSurfaceVariant       = SlateD600,
    outline                = SlateD500,
    outlineVariant         = SlateD700,
    surfaceContainerLowest = SlateD900,
    surfaceContainerLow    = SlateD50,
    surfaceContainer       = SlateD100,
    surfaceContainerHigh   = SlateD200,
)

// ── Extended colors (the "violet identity" + gradients M3 has no slot for) ────

@Immutable
data class SpineIQColors(
    val accent: Color,
    val onAccent: Color,
    val accentContainer: Color,
    val onAccentContainer: Color,
    val success: Color,
    val onSuccess: Color,
    val successContainer: Color,
    val warning: Color,
    val warningContainer: Color,
    /** Signature brand gradient: blue → indigo → violet. Hero surfaces + primary CTAs. */
    val brandStops: List<Color>,
    /** Soft tinted colour for elevation glows. */
    val shadowTint: Color,
    val isDark: Boolean,
)

private val LightSpineIQColors = SpineIQColors(
    accent            = Indigo600,
    onAccent          = Color.White,
    accentContainer   = Indigo50,
    onAccentContainer = Indigo900,
    success           = Success,
    onSuccess         = Color.White,
    successContainer  = SuccessContainer,
    warning           = Warning,
    warningContainer  = WarningContainer,
    brandStops        = listOf(Blue600, Indigo600, Violet600),
    shadowTint        = Indigo600,
    isDark            = false,
)

private val DarkSpineIQColors = SpineIQColors(
    accent            = Violet300,
    onAccent          = Violet900,
    accentContainer   = Indigo700,
    onAccentContainer = Indigo50,
    success           = Green300,
    onSuccess         = Green900,
    successContainer  = Green700,
    warning           = Color(0xFFFBBF24),
    warningContainer  = Color(0xFF78350F),
    brandStops        = listOf(Blue500, Indigo500, Violet500),
    shadowTint        = Violet500,
    isDark            = true,
)

private val LocalSpineIQColors = staticCompositionLocalOf { LightSpineIQColors }

/** Accessor for Design-System-2.0 tokens, e.g. `SpineIQTheme.colors.accent`. */
object SpineIQTheme {
    val colors: SpineIQColors
        @Composable @ReadOnlyComposable get() = LocalSpineIQColors.current
}

@Composable
fun SpineIQTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val extendedColors = if (darkTheme) DarkSpineIQColors else LightSpineIQColors

    // Keep status/navigation bar icons legible against our edge-to-edge surfaces.
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            (view.context as? Activity)?.window?.let { window ->
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = !darkTheme
                controller.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(LocalSpineIQColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = SpineIQShapes,
            content = content
        )
    }
}
