package noshtek.back_pain_prototype.ui.common

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import noshtek.back_pain_prototype.ui.theme.CardShape
import noshtek.back_pain_prototype.ui.theme.HeroShape
import noshtek.back_pain_prototype.ui.theme.SpineIQTheme
import noshtek.back_pain_prototype.ui.theme.brandGradient

/**
 * Tinted shadow — on the dark theme this reads as a neon glow emitted by the
 * surface rather than paper elevation. The ambient/spot tint applies on
 * API 28+; on 26–27 it degrades to a default shadow, never an error.
 */
fun Modifier.softShadow(
    color: Color,
    shape: Shape,
    elevation: Dp = 16.dp,
    alpha: Float = 0.16f,
): Modifier = shadow(
    elevation = elevation,
    shape = shape,
    clip = false,
    ambientColor = color.copy(alpha = alpha),
    spotColor = color.copy(alpha = alpha),
)

/**
 * Quiet content panel: dark surface with a plain hairline border. Featured
 * content uses [GlowCard] (aurora border + glow) instead.
 */
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    shape: Shape = CardShape,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    border: Boolean = true,
    shadowElevation: Dp = 12.dp,
    contentPadding: PaddingValues = PaddingValues(20.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier
            .fillMaxWidth()
            .softShadow(SpineIQTheme.colors.shadowTint, shape, elevation = shadowElevation, alpha = 0.18f)
            .clip(shape)
            .background(containerColor)
            .then(
                if (border) Modifier.border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape)
                else Modifier
            )
            .padding(contentPadding),
        content = content,
    )
}

/** Tappable panel with a spring press-scale + ripple — for list rows and shortcuts. */
@Composable
fun PressableCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = CardShape,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    border: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(20.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "press-scale",
    )
    Column(
        modifier
            .fillMaxWidth()
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .softShadow(
                SpineIQTheme.colors.shadowTint, shape,
                elevation = if (pressed) 6.dp else 12.dp,
                alpha = if (pressed) 0.30f else 0.18f,
            )
            .clip(shape)
            .background(containerColor)
            .then(
                if (border) Modifier.border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape)
                else Modifier
            )
            .clickable(
                interactionSource = interaction,
                indication = ripple(color = SpineIQTheme.colors.accent),
            ) { onClick() }
            .padding(contentPadding),
        content = content,
    )
}

/**
 * Aurora-gradient hero surface with nebula light blooms and an inner glass rim.
 * Content (score, badges, etc.) is laid out on top via the [BoxScope] slot.
 */
@Composable
fun GradientHeroCard(
    modifier: Modifier = Modifier,
    shape: Shape = HeroShape,
    contentPadding: PaddingValues = PaddingValues(horizontal = 24.dp, vertical = 28.dp),
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier
            .fillMaxWidth()
            .neonGlow(SpineIQTheme.colors.glow, shape, elevation = 24.dp, alpha = 0.45f)
            .clip(shape)
            .background(brandGradient())
            .drawBehind {
                // Soft light blooms + a darkening base so ink text stays legible.
                drawRect(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.18f)),
                    )
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.12f),
                    radius = size.maxDimension * 0.42f,
                    center = Offset(size.width * 0.86f, size.height * 0.06f),
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.06f),
                    radius = size.minDimension * 0.55f,
                    center = Offset(size.width * 0.08f, size.height * 0.98f),
                )
            }
            .border(1.dp, Color.White.copy(alpha = 0.22f), shape)
            .padding(contentPadding),
        content = content,
    )
}
