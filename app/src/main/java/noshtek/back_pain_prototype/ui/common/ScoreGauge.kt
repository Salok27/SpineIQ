package noshtek.back_pain_prototype.ui.common

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Animated 270° holo-gauge. The progress arc sweeps from 0 to [progress] on
 * first appearance using the score-motion timing. The arc is drawn twice — a
 * wide low-alpha pass underneath gives the crisp arc a neon halo. The centre
 * is a free content slot for the score numeral.
 */
@Composable
fun ScoreGauge(
    progress: Float,
    modifier: Modifier = Modifier,
    diameter: Dp = 132.dp,
    strokeWidth: Dp = 14.dp,
    trackColor: Color,
    progressColor: Color = Color.Unspecified,
    brush: Brush? = null,
    startAngle: Float = 135f,
    maxSweep: Float = 270f,
    content: @Composable BoxScope.() -> Unit = {},
) {
    var started by remember { mutableStateOf(false) }
    LaunchedEffect(progress) { started = true }
    val animated by animateFloatAsState(
        targetValue = if (started) progress.coerceIn(0f, 1f) else 0f,
        animationSpec = tween(MotionTokens.DurationScore, easing = MotionTokens.Emphasized),
        label = "gauge-sweep",
    )

    Box(modifier.size(diameter), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val stroke = strokeWidth.toPx()
            val topLeft = Offset(stroke / 2f, stroke / 2f)
            val arcSize = Size(size.width - stroke, size.height - stroke)

            drawArc(
                color = trackColor,
                startAngle = startAngle,
                sweepAngle = maxSweep,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
            )
            val sweep = maxSweep * animated
            if (sweep > 0f) {
                // Halo pass: same arc, wider stroke at low alpha = neon bleed.
                if (brush != null) {
                    drawArc(
                        brush = brush,
                        startAngle = startAngle,
                        sweepAngle = sweep,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = stroke * 2.1f, cap = StrokeCap.Round),
                        alpha = 0.22f,
                    )
                    drawArc(
                        brush = brush,
                        startAngle = startAngle,
                        sweepAngle = sweep,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = stroke, cap = StrokeCap.Round),
                    )
                } else {
                    drawArc(
                        color = progressColor,
                        startAngle = startAngle,
                        sweepAngle = sweep,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = stroke * 2.1f, cap = StrokeCap.Round),
                        alpha = 0.22f,
                    )
                    drawArc(
                        color = progressColor,
                        startAngle = startAngle,
                        sweepAngle = sweep,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = stroke, cap = StrokeCap.Round),
                    )
                }
            }
        }
        content()
    }
}
