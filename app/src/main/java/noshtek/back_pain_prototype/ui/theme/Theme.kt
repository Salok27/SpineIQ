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
    secondary              = Teal600,          // teal accent
    onSecondary            = Color.White,
    secondaryContainer     = Teal50,
    onSecondaryContainer   = Teal900,
    tertiary               = Sky500,           // cyan — second data accent (charts)
    onTertiary             = Color.White,
    tertiaryContainer      = Sky100,
    onTertiaryContainer    = Sky900,
    error                  = Error,
    onError                = Color.White,
    errorContainer         = ErrorContainer,
    onErrorContainer       = RiskSevereUrgent,
    background             = Slate50,
    onBackground           = Ink,
    surface                = Color.White,
    onSurface              = Ink,
    surfaceVariant         = Slate100,
    onSurfaceVariant       = SlateText,
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
    secondaryContainer     = Teal800,
    onSecondaryContainer   = Teal50,
    tertiary               = Sky400,
    onTertiary             = Sky900,
    tertiaryContainer      = Color(0xFF075985),
    onTertiaryContainer    = Sky100,
    error                  = RiskHighContainer,
    onError                = RiskSevereUrgent,
    errorContainer         = RiskSevereUrgent,
    onErrorContainer       = RiskHighContainer,
    background             = SlateD900,
    onBackground           = Slate200,
    surface                = SlateD100,
    onSurface              = Slate200,
    surfaceVariant         = SlateD200,
    onSurfaceVariant       = SlateD600,
    outline                = SlateD500,
    outlineVariant         = SlateD700,
    surfaceContainerLowest = SlateD900,
    surfaceContainerLow    = SlateD50,
    surfaceContainer       = SlateD100,
    surfaceContainerHigh   = SlateD200,
)

// ── Extended colors (the teal "accent identity" + gradients M3 has no slot for) ─

@Immutable
data class SpineIQColors(
    val accent: Color,
    val onAccent: Color,
    val accentContainer: Color,
    val onAccentContainer: Color,
    /** AA-safe accent for teal text/icons on white surfaces (accent fails AA for small text). */
    val accentText: Color,
    val success: Color,
    val onSuccess: Color,
    val successContainer: Color,
    /** Bright spec success — large fills / indicators only. */
    val successFill: Color,
    val warning: Color,
    val warningContainer: Color,
    /** Bright spec warning — large fills only. */
    val warningFill: Color,
    /** Signature brand gradient: blue → sky → teal. Hero surfaces + primary CTAs. */
    val brandStops: List<Color>,
    /** Soft tinted colour for elevation glows. */
    val shadowTint: Color,
    val isDark: Boolean,
)

private val LightSpineIQColors = SpineIQColors(
    accent            = Teal600,
    onAccent          = Color.White,
    accentContainer   = Teal50,
    onAccentContainer = Teal900,
    accentText        = Teal700,
    success           = Success,
    onSuccess         = Color.White,
    successContainer  = SuccessContainer,
    successFill       = SuccessFill,
    warning           = Warning,
    warningContainer  = WarningContainer,
    warningFill       = WarningFill,
    brandStops        = listOf(Blue600, Sky500, Teal600),
    shadowTint        = Blue600,
    isDark            = false,
)

private val DarkSpineIQColors = SpineIQColors(
    accent            = Teal300,
    onAccent          = Teal900,
    accentContainer   = Teal800,
    onAccentContainer = Teal50,
    accentText        = Teal300,
    success           = Color(0xFF34D399),     // Emerald-400
    onSuccess         = Color(0xFF052E16),
    successContainer  = Color(0xFF065F46),     // Emerald-800
    successFill       = Color(0xFF34D399),
    warning           = Color(0xFFFBBF24),     // Amber-300
    warningContainer  = Color(0xFF78350F),     // Amber-900
    warningFill       = Color(0xFFFBBF24),
    brandStops        = listOf(Blue500, Sky400, Teal300),
    shadowTint        = Blue500,
    isDark            = true,
)

private val LocalSpineIQColors = staticCompositionLocalOf { LightSpineIQColors }

/** Accessor for Design-System tokens, e.g. `SpineIQTheme.colors.accent`. */
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
