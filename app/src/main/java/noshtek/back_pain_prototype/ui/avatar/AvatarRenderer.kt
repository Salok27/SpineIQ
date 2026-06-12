package noshtek.back_pain_prototype.ui.avatar

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import noshtek.back_pain_prototype.core.data.gamification.AvatarCatalog
import noshtek.back_pain_prototype.core.data.gamification.AvatarCategory
import noshtek.back_pain_prototype.ui.common.MotionTokens
import noshtek.back_pain_prototype.ui.theme.PillShape
import noshtek.back_pain_prototype.ui.theme.SpineIQTheme
import noshtek.back_pain_prototype.ui.theme.rewardGradient

enum class AvatarSize(val dp: Dp) { Small(48.dp), Medium(96.dp), Large(160.dp) }

/** Fixed paint order: clothing under hair, accessories always on top. */
private val DrawOrder = listOf(
    AvatarCategory.BOTTOMS,
    AvatarCategory.TOPS,
    AvatarCategory.HAIR,
    AvatarCategory.ACCESSORIES,
)

/**
 * The SpineIQ mascot wearing [spec]. Layers draw in a 0..100 unit space and
 * are uniformly scaled, so the same code serves thumbnails and the hero.
 * Categories not in the spec fall back to the free default (accessories have
 * none and render bare); unknown ids are skipped.
 */
@Composable
fun Avatar(
    spec: AvatarSpec,
    modifier: Modifier = Modifier,
    size: AvatarSize = AvatarSize.Medium,
    background: Color = Color.Unspecified,
) {
    Canvas(modifier.size(size.dp)) {
        if (background.isSpecified) {
            drawCircle(background)
        }
        val scale = this.size.minDimension / 100f
        val dx = (this.size.width - 100f * scale) / 2f
        val dy = (this.size.height - 100f * scale) / 2f
        withTransform({
            translate(dx, dy)
            scale(scale, scale, pivot = Offset.Zero)
        }) {
            with(BodyLayer) { draw() }
            for (category in DrawOrder) {
                val itemId = spec.equipped[category] ?: AvatarCatalog.DEFAULTS[category]?.id ?: continue
                val layer = AvatarRegistry.layers[itemId] ?: continue
                with(layer) { draw() }
            }
        }
    }
}

/**
 * Dashboard hero presentation: avatar inside a 270° reward-gradient level
 * ring (the ScoreGauge idiom) with a "LV n" pill anchored below.
 */
@Composable
fun AvatarWithLevelRing(
    spec: AvatarSpec,
    level: Int,
    levelProgress: Float,
    modifier: Modifier = Modifier,
    size: AvatarSize = AvatarSize.Large,
    onGradient: Boolean = true,
) {
    val rewardStops = SpineIQTheme.colors.rewardStops
    val trackColor =
        if (onGradient) Color.White.copy(alpha = 0.25f)
        else SpineIQTheme.colors.rewardContainer
    val animated by animateFloatAsState(
        targetValue = levelProgress.coerceIn(0f, 1f),
        animationSpec = tween(MotionTokens.DurationScore, easing = MotionTokens.Emphasized),
        label = "level-ring",
    )
    Box(modifier.size(size.dp + 28.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val stroke = 6.dp.toPx()
            val inset = stroke / 2f
            val arcSize = Size(this.size.width - stroke, this.size.height - stroke)
            drawArc(
                color = trackColor,
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = Stroke(stroke, cap = StrokeCap.Round),
            )
            if (animated > 0f) {
                drawArc(
                    brush = Brush.linearGradient(rewardStops),
                    startAngle = 135f,
                    sweepAngle = 270f * animated,
                    useCenter = false,
                    topLeft = Offset(inset, inset),
                    size = arcSize,
                    style = Stroke(stroke, cap = StrokeCap.Round),
                )
            }
        }
        Avatar(
            spec = spec,
            size = size,
            background = if (onGradient) Color.White.copy(alpha = 0.16f) else Color.Unspecified,
            modifier = Modifier.padding(10.dp),
        )
        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .offset(y = 4.dp)
                .clip(PillShape)
                .background(rewardGradient())
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text(
                "LV $level",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
            )
        }
    }
}
