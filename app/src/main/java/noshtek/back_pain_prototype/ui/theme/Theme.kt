package noshtek.back_pain_prototype.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary                = Blue600,
    onPrimary              = androidx.compose.ui.graphics.Color.White,
    primaryContainer       = Blue50,
    onPrimaryContainer     = Blue900,
    secondary              = Teal600,
    onSecondary            = androidx.compose.ui.graphics.Color.White,
    secondaryContainer     = Teal50,
    onSecondaryContainer   = Teal900,
    tertiary               = Green600,
    onTertiary             = androidx.compose.ui.graphics.Color.White,
    tertiaryContainer      = Green50,
    onTertiaryContainer    = Green900,
    error                  = RiskHigh,
    onError                = androidx.compose.ui.graphics.Color.White,
    errorContainer         = RiskHighContainer,
    onErrorContainer       = RiskSevereUrgent,
    background             = Slate50,
    onBackground           = Slate900,
    surface                = androidx.compose.ui.graphics.Color.White,
    onSurface              = Slate900,
    surfaceVariant         = Slate100,
    onSurfaceVariant       = Slate500,
    outline                = Slate300,
    outlineVariant         = Slate200,
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
    tertiary               = Green300,
    onTertiary             = Green900,
    tertiaryContainer      = Green700,
    onTertiaryContainer    = Green50,
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
    surfaceContainerLow    = SlateD50,
    surfaceContainer       = SlateD100,
    surfaceContainerHigh   = SlateD200,
)

@Composable
fun SpineIQTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
