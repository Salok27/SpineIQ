package noshtek.back_pain_prototype.ui.gamification

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import noshtek.back_pain_prototype.ui.common.MicroLabel
import noshtek.back_pain_prototype.ui.common.MotionTokens
import noshtek.back_pain_prototype.ui.theme.SpineIQTheme

/**
 * The assessment journey: six nodes on a path instead of a percent bar.
 * Completed stages are reward-filled check nodes, the current stage pulses,
 * and the per-stage reward is previewed alongside. One Canvas, one infinite
 * halo animation — cheap to render.
 */
@Composable
fun JourneyProgressIndicator(
    currentStep: Int,
    totalSteps: Int = 6,
    modifier: Modifier = Modifier,
) {
    val colors = SpineIQTheme.colors
    val rewardStops = colors.rewardStops
    val reward = colors.reward
    val trackColor = MaterialTheme.colorScheme.outlineVariant
    val futureFill = MaterialTheme.colorScheme.surfaceVariant
    val checkColor = androidx.compose.ui.graphics.Color.White

    // Track fill animates to the current node on each step change.
    val fillFraction by animateFloatAsState(
        targetValue = (currentStep - 1).coerceAtLeast(0) / (totalSteps - 1).toFloat(),
        animationSpec = tween(MotionTokens.DurationSlow, easing = MotionTokens.Emphasized),
        label = "journey-fill",
    )
    val halo = rememberInfiniteTransition(label = "journey-halo")
    val haloProgress by halo.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1_400, easing = MotionTokens.Standard)),
        label = "journey-halo-progress",
    )

    Column(modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        MicroLabel(
            "Stage $currentStep of $totalSteps",
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.height(8.dp))
        Canvas(
            Modifier
                .fillMaxWidth()
                .height(36.dp)
        ) {
            val nodeRadius = 6.dp.toPx()
            val currentRadius = 8.dp.toPx()
            val maxHalo = 15.dp.toPx()
            val trackWidth = 3.dp.toPx()
            val edge = maxHalo
            val usable = size.width - edge * 2f
            val centerY = size.height / 2f
            fun nodeX(index: Int) = edge + usable * index / (totalSteps - 1).toFloat()

            // Track: future first, then animated reward fill on top.
            drawLine(
                color = trackColor,
                start = Offset(nodeX(0), centerY),
                end = Offset(nodeX(totalSteps - 1), centerY),
                strokeWidth = trackWidth,
                cap = StrokeCap.Round,
            )
            if (fillFraction > 0f) {
                drawLine(
                    brush = Brush.horizontalGradient(rewardStops),
                    start = Offset(nodeX(0), centerY),
                    end = Offset(edge + usable * fillFraction, centerY),
                    strokeWidth = trackWidth,
                    cap = StrokeCap.Round,
                )
            }

            for (index in 0 until totalSteps) {
                val x = nodeX(index)
                val center = Offset(x, centerY)
                when {
                    index < currentStep - 1 -> {
                        // Completed: filled node + check mark.
                        drawCircle(reward, nodeRadius, center)
                        val c = nodeRadius * 0.55f
                        drawLine(
                            checkColor,
                            Offset(x - c, centerY),
                            Offset(x - c * 0.2f, centerY + c * 0.8f),
                            strokeWidth = 1.8.dp.toPx(),
                            cap = StrokeCap.Round,
                        )
                        drawLine(
                            checkColor,
                            Offset(x - c * 0.2f, centerY + c * 0.8f),
                            Offset(x + c, centerY - c * 0.6f),
                            strokeWidth = 1.8.dp.toPx(),
                            cap = StrokeCap.Round,
                        )
                    }
                    index == currentStep - 1 -> {
                        // Current: gradient node + expanding halo pulse.
                        val haloRadius = currentRadius + (maxHalo - currentRadius) * haloProgress
                        drawCircle(
                            color = reward.copy(alpha = 0.35f * (1f - haloProgress)),
                            radius = haloRadius,
                            center = center,
                        )
                        drawCircle(
                            brush = Brush.linearGradient(
                                rewardStops,
                                start = Offset(x - currentRadius, centerY - currentRadius),
                                end = Offset(x + currentRadius, centerY + currentRadius),
                            ),
                            radius = currentRadius,
                            center = center,
                        )
                    }
                    else -> {
                        // Future: hollow node.
                        drawCircle(futureFill, nodeRadius, center)
                        drawCircle(trackColor, nodeRadius, center, style = Stroke(1.5.dp.toPx()))
                    }
                }
            }
        }
    }
}
