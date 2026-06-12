package noshtek.back_pain_prototype.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ── NEON AURORA — single dark scheme (DS 3.0 is dark-only by design) ────────

private val AuroraColorScheme = darkColorScheme(
    primary                = Cyan,
    onPrimary              = OnCyan,
    primaryContainer       = CyanContainer,
    onPrimaryContainer     = OnCyanContainer,
    secondary              = IndigoGlow,
    onSecondary            = OnIndigo,
    secondaryContainer     = IndigoContainer,
    onSecondaryContainer   = OnIndigoContainer,
    tertiary               = Magenta,
    onTertiary             = OnMagenta,
    tertiaryContainer      = MagentaContainer,
    onTertiaryContainer    = OnMagentaContainer,
    error                  = Error,
    onError                = Color(0xFF3A0D11),
    errorContainer         = ErrorContainer,
    onErrorContainer       = OnErrorContainer,
    background             = DeepSpace,
    onBackground           = Starlight,
    surface                = Panel,
    onSurface              = Starlight,
    surfaceVariant         = PanelHigh,
    onSurfaceVariant       = StarDim,
    outline                = HairlineBright,
    outlineVariant         = Hairline,
    surfaceContainerLowest = DeepSpaceLow,
    surfaceContainerLow    = Color(0xFF0A1120),
    surfaceContainer       = Panel,
    surfaceContainerHigh   = PanelHigh,
    surfaceContainerHighest = PanelHigher,
)

// ── Extended colors (aurora identity + gradients M3 has no slot for) ─────────

@Immutable
data class SpineIQColors(
    val accent: Color,
    val onAccent: Color,
    val accentContainer: Color,
    val onAccentContainer: Color,
    /** AA-safe accent for small cyan text/icons on dark panels. */
    val accentText: Color,
    val success: Color,
    val onSuccess: Color,
    val successContainer: Color,
    /** Bright success — large fills / indicators only. */
    val successFill: Color,
    val warning: Color,
    val warningContainer: Color,
    /** Bright warning — large fills only. */
    val warningFill: Color,
    /** Signature aurora gradient: cyan → indigo → magenta. Hero surfaces + primary CTAs. */
    val brandStops: List<Color>,
    /** Tint colour for neon glow shadows. */
    val shadowTint: Color,
    // ── Gamification — rewards/progression only, never clinical ─────────────
    val reward: Color,
    val onReward: Color,
    val rewardContainer: Color,
    val onRewardContainer: Color,
    /** AA-safe violet for small reward text/icons on panels. */
    val rewardText: Color,
    /** Reward gradient: indigo → violet → purple (XP bars, level rings, celebrations). */
    val rewardStops: List<Color>,
    val coin: Color,
    /** AA-safe gold for coin amounts on dark panels. */
    val coinText: Color,
    val coinContainer: Color,
    val streak: Color,
    /** AA-safe streak text on panels. */
    val streakText: Color,
    /** Faux-glass surface fill / border (no real backdrop blur at minSdk 26). */
    val glassSurface: Color,
    val glassBorder: Color,
    /** Raised panel tone for nested surfaces. */
    val surfaceHigh: Color,
    /** Default neon glow tint (cyan). */
    val glow: Color,
    val isDark: Boolean,
)

private val AuroraSpineIQColors = SpineIQColors(
    accent            = CyanDim,
    onAccent          = OnCyan,
    accentContainer   = CyanContainer,
    onAccentContainer = OnCyanContainer,
    accentText        = CyanText,
    success           = Success,
    onSuccess         = Color(0xFF052E16),
    successContainer  = SuccessContainer,
    successFill       = SuccessFill,
    warning           = Warning,
    warningContainer  = WarningContainer,
    warningFill       = WarningFill,
    brandStops        = listOf(CyanDim, IndigoGlow, Magenta),
    shadowTint        = Cyan,
    reward            = RewardViolet,
    onReward          = RewardVioletDeep,
    rewardContainer   = RewardVioletContainer,
    onRewardContainer = OnRewardVioletContainer,
    rewardText        = RewardVioletBright,
    rewardStops       = listOf(IndigoGlow, RewardViolet, PurpleGlow),
    coin              = CoinGold,
    coinText          = CoinText,
    coinContainer     = CoinContainer,
    streak            = StreakOrange,
    streakText        = StreakGlow,
    glassSurface      = PanelHigh.copy(alpha = 0.62f),
    glassBorder       = Color.White.copy(alpha = 0.10f),
    surfaceHigh       = PanelHigh,
    glow              = Cyan,
    isDark            = true,
)

private val LocalSpineIQColors = staticCompositionLocalOf { AuroraSpineIQColors }

/** Accessor for Design-System tokens, e.g. `SpineIQTheme.colors.accent`. */
object SpineIQTheme {
    val colors: SpineIQColors
        @Composable @ReadOnlyComposable get() = LocalSpineIQColors.current
}

@Composable
fun SpineIQTheme(
    @Suppress("UNUSED_PARAMETER") darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // DS 3.0 "Neon Aurora" is dark-only: the system setting is intentionally
    // ignored so the neon identity stays consistent.
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            (view.context as? Activity)?.window?.let { window ->
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = false
                controller.isAppearanceLightNavigationBars = false
            }
        }
    }

    CompositionLocalProvider(LocalSpineIQColors provides AuroraSpineIQColors) {
        MaterialTheme(
            colorScheme = AuroraColorScheme,
            typography = Typography,
            shapes = SpineIQShapes,
            content = content
        )
    }
}
