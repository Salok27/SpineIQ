package noshtek.back_pain_prototype.ui.gamification

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import noshtek.back_pain_prototype.ui.common.MotionTokens
import noshtek.back_pain_prototype.ui.theme.SpineIQTheme
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

/**
 * Immutable per-particle launch parameters. Positions are derived
 * analytically from one progress value each frame — no per-particle
 * animation state, no allocation in the draw loop.
 */
private class ConfettiParticle(
    val x0: Float,            // fraction of width
    val y0: Float,            // fraction of height (starts above the top edge)
    val vx: Float,            // fraction of width per second
    val vy: Float,            // fraction of height per second
    val spin: Float,          // degrees per second
    val colorIndex: Int,
    val sizeDp: Float,
    val isCircle: Boolean,
    val sway: Float,
)

/**
 * Celebration confetti drawn with a single Canvas and a single Animatable —
 * recomposition cost never scales with [particleCount]. Two emitters at the
 * top quarter-points rain particles under simple gravity; alpha ramps out
 * over the final 20%. Purely decorative: never intercepts touch input.
 */
@Composable
fun ConfettiBurst(
    modifier: Modifier = Modifier,
    particleCount: Int = 120,
    durationMillis: Int = MotionTokens.DurationConfetti,
    colors: List<Color>? = null,
    onFinished: () -> Unit = {},
) {
    val themeColors = SpineIQTheme.colors
    val palette = colors
        ?: remember(themeColors) { themeColors.brandStops + themeColors.rewardStops + themeColors.coin }
    val particles = remember {
        val random = Random(System.nanoTime())
        List(particleCount.coerceAtMost(150)) { i ->
            val emitter = if (i % 2 == 0) 0.25f else 0.75f
            ConfettiParticle(
                x0 = emitter + (random.nextFloat() - 0.5f) * 0.25f,
                y0 = -0.04f - random.nextFloat() * 0.12f,
                vx = (random.nextFloat() - 0.5f) * 0.5f,
                vy = 0.45f + random.nextFloat() * 0.55f,
                spin = (random.nextFloat() - 0.5f) * 720f,
                colorIndex = i % palette.size,
                sizeDp = 5f + random.nextFloat() * 5f,
                isCircle = random.nextFloat() < 0.3f,
                sway = 1f + random.nextFloat() * 2.5f,
            )
        }
    }
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        progress.animateTo(1f, tween(durationMillis, easing = MotionTokens.Linear))
        onFinished()
    }
    Canvas(modifier) {
        val t = progress.value
        if (t <= 0f || t >= 1f) return@Canvas
        val seconds = t * durationMillis / 1000f
        val fade = if (t > 0.8f) (1f - t) / 0.2f else 1f
        for (p in particles) {
            val x = (p.x0 + p.vx * seconds + sin(seconds * p.sway * PI.toFloat()) * 0.03f) * size.width
            val y = (p.y0 + p.vy * seconds + 0.35f * seconds * seconds) * size.height
            if (y > size.height + 24f) continue
            val color = palette[p.colorIndex].copy(alpha = fade)
            val px = p.sizeDp.dp.toPx()
            if (p.isCircle) {
                drawCircle(color, radius = px / 2f, center = Offset(x, y))
            } else {
                rotate(degrees = p.spin * seconds, pivot = Offset(x, y)) {
                    drawRect(color, topLeft = Offset(x - px / 2f, y - px * 0.3f), size = Size(px, px * 0.6f))
                }
            }
        }
    }
}
