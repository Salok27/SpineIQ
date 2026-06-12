@file:OptIn(androidx.compose.ui.text.ExperimentalTextApi::class)

package noshtek.back_pain_prototype.ui.common

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import noshtek.back_pain_prototype.ui.theme.CardShape
import noshtek.back_pain_prototype.ui.theme.ChipShape
import noshtek.back_pain_prototype.ui.theme.CyanDim
import noshtek.back_pain_prototype.ui.theme.DeepSpace
import noshtek.back_pain_prototype.ui.theme.IndigoGlow
import noshtek.back_pain_prototype.ui.theme.Magenta
import noshtek.back_pain_prototype.ui.theme.SpineIQTheme
import noshtek.back_pain_prototype.ui.theme.auroraBorderBrush
import noshtek.back_pain_prototype.ui.theme.brandGradient

// ── NEON AURORA primitives (DS 3.0) ──────────────────────────────────────────
// The shared visual vocabulary of the redesign: deep-space backgrounds with
// nebula blooms, glowing panels with aurora hairline borders, wide-tracked
// micro-labels and gradient hero text.

/**
 * Full-screen deep-space backdrop with three large, very-low-alpha radial
 * nebula blooms (cyan, magenta, indigo). Every screen lays its content on top
 * of this so panels read as glass floating in space.
 */
@Composable
fun NebulaBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier
            .fillMaxSize()
            .background(DeepSpace)
            .drawBehind {
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(CyanDim.copy(alpha = 0.09f), Color.Transparent),
                        center = Offset(size.width * 0.12f, size.height * 0.05f),
                        radius = size.width * 0.85f,
                    ),
                    radius = size.width * 0.85f,
                    center = Offset(size.width * 0.12f, size.height * 0.05f),
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(Magenta.copy(alpha = 0.07f), Color.Transparent),
                        center = Offset(size.width * 0.95f, size.height * 0.32f),
                        radius = size.width * 0.70f,
                    ),
                    radius = size.width * 0.70f,
                    center = Offset(size.width * 0.95f, size.height * 0.32f),
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(IndigoGlow.copy(alpha = 0.08f), Color.Transparent),
                        center = Offset(size.width * 0.40f, size.height * 1.02f),
                        radius = size.width * 0.95f,
                    ),
                    radius = size.width * 0.95f,
                    center = Offset(size.width * 0.40f, size.height * 1.02f),
                )
            },
        content = content,
    )
}

/**
 * Neon glow shadow — the dark-theme replacement for paper elevation. A tinted
 * shadow reads as light emitted by the panel rather than light blocked by it.
 */
fun Modifier.neonGlow(
    color: Color,
    shape: Shape,
    elevation: Dp = 18.dp,
    alpha: Float = 0.45f,
): Modifier = softShadow(color, shape, elevation, alpha)

/**
 * The DS 3.0 feature panel: dark surface + 1dp aurora hairline border + soft
 * tinted glow. Use for hero/featured content; plain [AppCard] stays quieter.
 */
@Composable
fun GlowCard(
    modifier: Modifier = Modifier,
    shape: Shape = CardShape,
    contentPadding: PaddingValues = PaddingValues(20.dp),
    glowColor: Color = SpineIQTheme.colors.shadowTint,
    borderAlpha: Float = 0.30f,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier
            .fillMaxWidth()
            .neonGlow(glowColor, shape, elevation = 16.dp, alpha = 0.35f)
            .clip(shape)
            .background(containerColor)
            .border(1.dp, auroraBorderBrush(borderAlpha), shape)
            .padding(contentPadding),
        content = content,
    )
}

/**
 * Wide-tracked uppercase caption — the HUD-style micro-label used for section
 * eyebrows ("DAILY MISSIONS", "STAGE 2/6", "OVERALL RISK").
 */
@Composable
fun MicroLabel(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    textAlign: TextAlign? = null,
) {
    Text(
        text = text.uppercase(),
        modifier = modifier,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.SemiBold,
        color = color,
        textAlign = textAlign,
    )
}

/** Aurora-gradient text for hero numerals and the wordmark. */
@Composable
fun AuroraText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.headlineLarge,
    textAlign: TextAlign? = null,
) {
    Text(
        text = text,
        modifier = modifier,
        style = style.merge(TextStyle(brush = brandGradient())),
        textAlign = textAlign,
    )
}

/** Holo input styling shared by every OutlinedTextField: dark fill, cyan focus glow border. */
@Composable
fun auroraTextFieldColors(): TextFieldColors = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
    focusedLabelColor = SpineIQTheme.colors.accentText,
    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    cursorColor = MaterialTheme.colorScheme.primary,
)

/**
 * DS 3.0 selectable pill chip — replaces every M3 FilterChip in the app.
 * Selected: cyan-tinted fill + aurora hairline + glow. Unselected: quiet dark
 * pill. Spring press-scale matches the button language.
 */
@Composable
fun SelectableChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed && enabled) 0.94f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "chip-press",
    )
    val background by animateColorAsState(
        targetValue = when {
            selected -> MaterialTheme.colorScheme.primaryContainer
            else -> MaterialTheme.colorScheme.surfaceContainerHighest
        },
        animationSpec = tween(MotionTokens.DurationFast),
        label = "chip-bg",
    )
    val textColor by animateColorAsState(
        targetValue = when {
            !enabled -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            selected -> SpineIQTheme.colors.accentText
            else -> MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = tween(MotionTokens.DurationFast),
        label = "chip-text",
    )
    val borderBrush =
        if (selected) auroraBorderBrush(0.85f)
        else SolidColor(MaterialTheme.colorScheme.outlineVariant)

    Box(
        modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                alpha = if (enabled) 1f else 0.6f
            }
            .then(
                if (selected) Modifier.neonGlow(SpineIQTheme.colors.glow, ChipShape, elevation = 10.dp, alpha = 0.35f)
                else Modifier
            )
            .clip(ChipShape)
            .background(background)
            .border(1.dp, borderBrush, ChipShape)
            .clickable(
                interactionSource = interaction,
                indication = ripple(color = SpineIQTheme.colors.accent),
                enabled = enabled,
            ) { onClick() }
            .padding(horizontal = 16.dp, vertical = 9.dp),
        contentAlignment = androidx.compose.ui.Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = textColor,
        )
    }
}
