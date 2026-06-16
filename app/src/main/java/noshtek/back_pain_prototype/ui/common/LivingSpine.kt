package noshtek.back_pain_prototype.ui.common

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import noshtek.back_pain_prototype.ui.theme.SpineGlowBright
import noshtek.back_pain_prototype.ui.theme.SpineGlowDim
import noshtek.back_pain_prototype.ui.theme.SpineGlowMid
import noshtek.back_pain_prototype.ui.theme.SpineGlowRadiant
import kotlin.math.PI
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * The Living Spine — the Aura hero. A stack of vertebrae on a gentle S-curve
 * that visibly "heals" as [vitality] (0..100) rises: it straightens, brightens,
 * and lights up vertebra-by-vertebra from the base upward. The glow runs a COOL
 * slate→teal→aqua→radiant ramp (never the clinical green→red), so it reads as
 * vitality, not a medical risk signal. Vitality changes animate smoothly (the
 * "heal"); a slow breath keeps it alive at rest.
 *
 * The caller sizes it via [modifier] (e.g. `Modifier.size(160.dp, 220.dp)`).
 */
@Composable
fun LivingSpine(
    vitality: Int,
    modifier: Modifier = Modifier,
    vertebraeCount: Int = 9,
) {
    val healed by animateFloatAsState(
        targetValue = vitality.coerceIn(0, 100) / 100f,
        animationSpec = tween(MotionTokens.DurationScore, easing = MotionTokens.Emphasized),
        label = "spine-heal",
    )
    val breath by rememberInfiniteTransition(label = "spine-breath").animateFloat(
        initialValue = 0.8f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3800, easing = MotionTokens.Standard), RepeatMode.Reverse),
        label = "spine-breath-alpha",
    )

    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val topY = h * 0.10f
        val botY = h * 0.92f
        val span = botY - topY
        val glow = spineGlowColor(healed)

        // Posture: more lateral sway when dim, straightening as it heals.
        val amplitude = w * 0.14f * (1f - healed * 0.72f)

        // Aura bloom behind the column.
        drawCircle(
            brush = Brush.radialGradient(
                listOf(glow.copy(alpha = (0.10f + 0.30f * healed) * breath), Color.Transparent),
                center = Offset(cx, h * 0.5f),
                radius = w * 0.55f,
            ),
            radius = w * 0.55f,
            center = Offset(cx, h * 0.5f),
        )

        val n = vertebraeCount
        val litCount = (healed * n).roundToInt()
        for (i in 0 until n) {
            val t = i / (n - 1f)                 // 0 = top, 1 = bottom
            val y = topY + span * t
            val x = cx + amplitude * sin(t * PI.toFloat() * 2f)
            val fromBottom = n - 1 - i
            val lit = fromBottom < litCount

            val vertebraColor = if (lit) glow else lerp(SpineGlowDim, Color.White, 0.35f)
            val vw = w * (0.30f - 0.07f * t)
            val vh = (span / n) * 0.66f

            if (lit) {
                drawRoundRect(
                    color = glow.copy(alpha = 0.22f * breath),
                    topLeft = Offset(x - vw * 0.8f, y - vh * 0.85f),
                    size = Size(vw * 1.6f, vh * 1.7f),
                    cornerRadius = CornerRadius(vh),
                )
            }
            drawRoundRect(
                color = vertebraColor.copy(alpha = if (lit) 0.95f else 0.45f),
                topLeft = Offset(x - vw / 2f, y - vh / 2f),
                size = Size(vw, vh),
                cornerRadius = CornerRadius(vh / 2f),
            )
        }
    }
}

/** Cool vitality ramp: dim slate → teal → aqua → radiant mint. */
private fun spineGlowColor(t: Float): Color = when {
    t < 0.33f -> lerp(SpineGlowDim, SpineGlowMid, t / 0.33f)
    t < 0.66f -> lerp(SpineGlowMid, SpineGlowBright, (t - 0.33f) / 0.33f)
    else -> lerp(SpineGlowBright, SpineGlowRadiant, ((t - 0.66f) / 0.34f).coerceIn(0f, 1f))
}
