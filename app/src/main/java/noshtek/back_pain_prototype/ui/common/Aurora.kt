@file:OptIn(androidx.compose.ui.text.ExperimentalTextApi::class)

package noshtek.back_pain_prototype.ui.common

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// ── AURA primitives ──────────────────────────────────────────────────────────
// The shared visual vocabulary of the redesign: a warm-paper canvas lit by
// slowly drifting organic blobs (sage / coral / sky), soft elevated cards with
// gentle drop shadows, sentence-case eyebrows and gradient hero text.

/**
 * Full-screen warm-paper backdrop with two or three large, very-low-alpha
 * organic light blooms (sage, coral, sky) that drift slowly so the background
 * feels alive and breathing. Every screen lays its content on top of this.
 */
@Composable
fun NebulaBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val transition = rememberInfiniteTransition(label = "aura-bg")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(20000, easing = LinearEasing)),
        label = "aura-phase",
    )
    Box(
        modifier
            .fillMaxSize()
            .background(DeepSpace)
            .drawBehind {
                val w = size.width
                val h = size.height
                fun blob(cx: Float, cy: Float, r: Float, color: Color, a: Float) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(color.copy(alpha = a), Color.Transparent),
                            center = Offset(cx, cy),
                            radius = r,
                        ),
                        radius = r,
                        center = Offset(cx, cy),
                    )
                }
                // Sage bloom — top-left, drifting in a small orbit.
                blob(
                    w * (0.18f + 0.05f * cos(phase)),
                    h * (0.08f + 0.03f * sin(phase)),
                    w * 0.90f, CyanDim, 0.16f,
                )
                // Coral bloom — top-right.
                blob(
                    w * (0.92f - 0.04f * sin(phase)),
                    h * (0.30f + 0.04f * cos(phase * 0.8f)),
                    w * 0.72f, Magenta, 0.12f,
                )
                // Sky bloom — bottom-centre.
                blob(
                    w * (0.42f + 0.06f * sin(phase * 0.6f)),
                    h * (1.00f + 0.02f * cos(phase)),
                    w * 0.98f, IndigoGlow, 0.14f,
                )
            },
        content = content,
    )
}

/**
 * Soft drop shadow — kept under the legacy name. On the light Aura theme this is
 * a gentle warm-taupe shadow (no neon emission). The ambient/spot tint applies
 * on API 28+; on 26–27 it degrades to a default shadow, never an error.
 */
fun Modifier.neonGlow(
    color: Color,
    shape: Shape,
    elevation: Dp = 12.dp,
    alpha: Float = 0.22f,
): Modifier = softShadow(color, shape, elevation, alpha)

/**
 * The feature panel: white surface, large soft radius, gentle drop shadow and a
 * whisper-thin sage→coral hairline. Use for hero/featured content; plain
 * [AppCard] stays quieter.
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
            .neonGlow(glowColor, shape, elevation = 14.dp, alpha = 0.22f)
            .clip(shape)
            .background(containerColor)
            .border(1.dp, auroraBorderBrush(borderAlpha), shape)
            .padding(contentPadding),
        content = content,
    )
}

/**
 * Sentence-case section eyebrow — the calm label used above content groups
 * ("Today's rituals", "Stage 2 of 6", "Overall risk"). The old wide-tracked
 * uppercase HUD label is gone; this reads soft and humanist.
 */
@Composable
fun MicroLabel(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    textAlign: TextAlign? = null,
) {
    Text(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        color = color,
        textAlign = textAlign,
    )
}

/** Wellness-gradient text for hero numerals and the wordmark (sage → sky → coral). */
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

/** Input styling shared by every OutlinedTextField: soft fill, sage focus border. */
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
 * Selectable pill chip — replaces every M3 FilterChip in the app. Selected:
 * sage-tinted fill + sage→coral hairline + soft lift. Unselected: quiet pill.
 * Spring press-scale matches the button language.
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
                if (selected) Modifier.neonGlow(SpineIQTheme.colors.accent, ChipShape, elevation = 8.dp, alpha = 0.18f)
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
            .padding(horizontal = 16.dp, vertical = 10.dp),
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
