package noshtek.back_pain_prototype.ui.common

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import noshtek.back_pain_prototype.ui.theme.ButtonShape
import noshtek.back_pain_prototype.ui.theme.SpineIQTheme
import noshtek.back_pain_prototype.ui.theme.brandGradient

private const val PRESSED_SCALE = 0.97f

@Composable
private fun rememberPressScale(interaction: MutableInteractionSource, active: Boolean): Float {
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed && active) PRESSED_SCALE else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "press-scale",
    )
    return scale
}

/**
 * The primary call-to-action: brand-gradient fill, soft glow, spring press-scale,
 * plus first-class loading and disabled states and an optional leading icon.
 * Width is caller-controlled (pass `Modifier.fillMaxWidth()` or `weight()`).
 */
@Composable
fun PrimaryButton(
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    icon: ImageVector? = null,
    height: Dp = 56.dp,
) {
    val interaction = remember { MutableInteractionSource() }
    val active = enabled && !loading
    val scale = rememberPressScale(interaction, active)

    Box(
        modifier
            .height(height)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                alpha = if (enabled) 1f else 0.55f
            }
            .then(
                if (active) Modifier.softShadow(SpineIQTheme.colors.shadowTint, ButtonShape, elevation = 16.dp, alpha = 0.38f)
                else Modifier
            )
            .clip(ButtonShape)
            .then(
                if (active) Modifier.background(brandGradient())
                else Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
            )
            .clickable(
                interactionSource = interaction,
                indication = ripple(color = Color.White),
                enabled = active,
            ) { onClick() }
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center,
    ) {
        val contentColor = if (active) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                color = Color.White,
                strokeWidth = 2.5.dp,
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                if (icon != null) {
                    Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(20.dp))
                }
                Text(label, style = MaterialTheme.typography.labelLarge, color = contentColor, fontWeight = FontWeight.Bold)
            }
        }
    }
}

/** Medium-emphasis tonal button (soft container) with the same interaction feel. */
@Composable
fun SecondaryButton(
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    icon: ImageVector? = null,
    contentColor: Color = MaterialTheme.colorScheme.primary,
    height: Dp = 52.dp,
) {
    val interaction = remember { MutableInteractionSource() }
    val active = enabled && !loading
    val scale = rememberPressScale(interaction, active)
    val resolvedContent = if (active) contentColor else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier
            .height(height)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                alpha = if (enabled) 1f else 0.55f
            }
            .clip(ButtonShape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.5.dp, resolvedContent.copy(alpha = 0.35f), ButtonShape)
            .clickable(
                interactionSource = interaction,
                indication = ripple(color = contentColor),
                enabled = active,
            ) { onClick() }
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = resolvedContent,
                strokeWidth = 2.5.dp,
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (icon != null) {
                    Icon(icon, contentDescription = null, tint = resolvedContent, modifier = Modifier.size(18.dp))
                }
                Text(label, style = MaterialTheme.typography.labelLarge, color = resolvedContent, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

/** Low-emphasis text action (Skip / Cancel). */
@Composable
fun TextActionButton(
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    height: Dp = 52.dp,
) {
    val interaction = remember { MutableInteractionSource() }
    val scale = rememberPressScale(interaction, true)
    Box(
        modifier
            .height(height)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(ButtonShape)
            .clickable(interactionSource = interaction, indication = ripple(color = color)) { onClick() }
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, style = MaterialTheme.typography.labelLarge, color = color, fontWeight = FontWeight.SemiBold)
    }
}
