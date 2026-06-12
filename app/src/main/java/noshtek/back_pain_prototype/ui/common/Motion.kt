package noshtek.back_pain_prototype.ui.common

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Design-System-2.0 motion language. Every animation in the app pulls its
 * timing and easing from here so motion feels intentional and consistent.
 */
object MotionTokens {
    const val DurationFast = 180
    const val DurationMedium = 320
    const val DurationSlow = 520
    const val DurationScore = 750
    const val DurationCelebration = 1200   // reward-overlay card lifecycle
    const val DurationConfetti = 1800      // confetti particle fall

    /** Snappy-in, gentle-out — the default for entrances and emphasis. */
    val Emphasized: Easing = CubicBezierEasing(0.2f, 0f, 0f, 1f)
    val Standard: Easing = FastOutSlowInEasing
    val Linear: Easing = LinearEasing

    /** Springy overshoot for celebratory pop-ins (badges, check circles). */
    val Overshoot: Easing = CubicBezierEasing(0.34f, 1.56f, 0.64f, 1f)
}

/**
 * Fade + rise entrance. Apply to cards / list items; pass an increasing [index]
 * for a staggered reveal. Animates cheap layer properties only (alpha + Y).
 */
@Composable
fun Modifier.entrance(
    index: Int = 0,
    delayPerItem: Int = 55,
    rise: Dp = 22.dp,
    durationMillis: Int = MotionTokens.DurationMedium,
): Modifier {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    val progress by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(
            durationMillis = durationMillis,
            delayMillis = index * delayPerItem,
            easing = MotionTokens.Emphasized,
        ),
        label = "entrance",
    )
    return this.graphicsLayer {
        alpha = progress
        translationY = (1f - progress) * rise.toPx()
    }
}

/** A number that counts up from 0 to [target] on first appearance / change. */
@Composable
fun AnimatedCountText(
    target: Int,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    color: Color = Color.Unspecified,
    fontWeight: FontWeight? = null,
    durationMillis: Int = MotionTokens.DurationScore,
    prefix: String = "",
    suffix: String = "",
) {
    var started by remember { mutableStateOf(false) }
    LaunchedEffect(target) { started = true }
    val value by animateIntAsState(
        targetValue = if (started) target else 0,
        animationSpec = tween(durationMillis, easing = MotionTokens.Emphasized),
        label = "count",
    )
    Text(
        text = "$prefix$value$suffix",
        modifier = modifier,
        style = style,
        color = color,
        fontWeight = fontWeight,
    )
}

/** Lightweight shimmer placeholder used for skeleton loading states. */
@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp),
) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val anim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1200, easing = MotionTokens.Linear)),
        label = "shimmer-x",
    )
    val base = MaterialTheme.colorScheme.surfaceVariant
    val highlight = MaterialTheme.colorScheme.surfaceContainerHigh
    Box(
        modifier
            .clip(shape)
            .drawWithCache {
                val sweep = size.width * 1.5f
                val start = anim * (size.width + sweep) - sweep
                val brush = Brush.linearGradient(
                    colors = listOf(base, highlight, base),
                    start = Offset(start, 0f),
                    end = Offset(start + sweep, size.height),
                )
                onDrawBehind { drawRect(brush) }
            }
    )
}
