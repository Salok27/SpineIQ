package noshtek.back_pain_prototype.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
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

// ── AURA — single light scheme (the Aura prototype is light-only by design) ──

private val AuraColorScheme = lightColorScheme(
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
    onError                = Color(0xFFFFFFFF),
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
    surfaceContainerLowest = Panel,
    surfaceContainerLow    = Color(0xFFFCF9F4),
    surfaceContainer       = PanelHigh,
    surfaceContainerHigh   = DeepSpaceLow,
    surfaceContainerHighest = PanelHigher,
)

// ── Extended colors (Aura identity + gradients M3 has no slot for) ───────────

@Immutable
data class SpineIQColors(
    val accent: Color,
    val onAccent: Color,
    val accentContainer: Color,
    val onAccentContainer: Color,
    /** AA-safe accent for small sage text/icons on light panels. */
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
    /** Signature wellness gradient: sage → sky → coral. Hero surfaces + primary CTAs. */
    val brandStops: List<Color>,
    /** Tint colour for soft drop shadows. */
    val shadowTint: Color,
    // ── Engagement — rewards/progression only, never clinical ───────────────
    val reward: Color,
    val onReward: Color,
    val rewardContainer: Color,
    val onRewardContainer: Color,
    /** AA-safe violet for small reward text/icons on panels. */
    val rewardText: Color,
    /** Reward gradient: sky → violet → purple (vitality, milestones, celebrations). */
    val rewardStops: List<Color>,
    val coin: Color,
    /** AA-safe gold for coin amounts on light panels. */
    val coinText: Color,
    val coinContainer: Color,
    val streak: Color,
    /** AA-safe streak text on panels. */
    val streakText: Color,
    /** Frosted surface fill / border (no real backdrop blur at minSdk 26). */
    val glassSurface: Color,
    val glassBorder: Color,
    /** Raised panel tone for nested surfaces. */
    val surfaceHigh: Color,
    /** Default soft-shadow tint (warm taupe). */
    val glow: Color,
    val isDark: Boolean,
)

private val AuraSpineIQColors = SpineIQColors(
    accent            = CyanDim,
    onAccent          = OnCyan,
    accentContainer   = CyanContainer,
    onAccentContainer = OnCyanContainer,
    accentText        = CyanText,
    success           = Success,
    onSuccess         = Color(0xFFFFFFFF),
    successContainer  = SuccessContainer,
    successFill       = SuccessFill,
    warning           = Warning,
    warningContainer  = WarningContainer,
    warningFill       = WarningFill,
    // Deep sage → teal → coral: rich enough for crisp white text on CTAs/hero
    // and a legible gauge arc on white, while the lighter CyanDim/IndigoGlow/
    // Magenta accents stay for chips, blooms and tints.
    brandStops        = listOf(Color(0xFF3E7A57), Color(0xFF3D8196), Color(0xFFC25E43)),
    shadowTint        = Color(0xFFB8A990),
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
    glassSurface      = Panel.copy(alpha = 0.72f),
    glassBorder       = Ink.copy(alpha = 0.06f),
    surfaceHigh       = PanelHigh,
    glow              = Color(0xFFB8A990),
    isDark            = false,
)

private val LocalSpineIQColors = staticCompositionLocalOf { AuraSpineIQColors }

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
    // Aura is light-only: the system dark setting is intentionally ignored so
    // the calm wellness identity stays consistent across devices.
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            (view.context as? Activity)?.window?.let { window ->
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = true
                controller.isAppearanceLightNavigationBars = true
            }
        }
    }

    CompositionLocalProvider(LocalSpineIQColors provides AuraSpineIQColors) {
        MaterialTheme(
            colorScheme = AuraColorScheme,
            typography = Typography,
            shapes = SpineIQShapes,
            content = content
        )
    }
}
