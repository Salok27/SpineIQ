package noshtek.back_pain_prototype.ui.gamification

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import noshtek.back_pain_prototype.ui.common.AnimatedCountText
import noshtek.back_pain_prototype.ui.common.MotionTokens
import noshtek.back_pain_prototype.ui.common.neonGlow
import noshtek.back_pain_prototype.ui.theme.PillShape
import noshtek.back_pain_prototype.ui.theme.SpineGlowBright
import noshtek.back_pain_prototype.ui.theme.SpineGlowMid
import noshtek.back_pain_prototype.ui.theme.SpineIQTheme

// ── Vitality pill ───────────────────────────────────────────────────────────

/**
 * Compact Spine Vitality readout (0..100) with a soft cool-glow accent. Counts
 * up on appearance. Used in headers and the Journey hub; the Home hero uses the
 * full Living Spine instead.
 */
@Composable
fun VitalityPill(
    vitality: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .clip(PillShape)
            .background(SpineGlowMid.copy(alpha = 0.14f))
            .border(1.dp, SpineGlowMid.copy(alpha = 0.45f), PillShape)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            Icons.Filled.AutoAwesome,
            contentDescription = null,
            tint = SpineGlowBright,
            modifier = Modifier.size(15.dp),
        )
        Spacer(Modifier.width(6.dp))
        AnimatedCountText(
            target = vitality,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
            suffix = " vitality",
        )
    }
}

// ── Streak flame ──────────────────────────────────────────────────────────────

/** Streak display: pulsing flame when today's activity keeps the streak alive. */
@Composable
fun StreakFlame(
    streakDays: Int,
    activeToday: Boolean,
    modifier: Modifier = Modifier,
    onGradient: Boolean = false,
) {
    val colors = SpineIQTheme.colors
    val active = activeToday && streakDays > 0
    val scale: Float
    val alpha: Float
    if (active) {
        val transition = rememberInfiniteTransition(label = "flame")
        scale = transition.animateFloat(
            initialValue = 1f, targetValue = 1.08f,
            animationSpec = infiniteRepeatable(tween(1100, easing = MotionTokens.Standard), RepeatMode.Reverse),
            label = "flame-scale",
        ).value
        alpha = transition.animateFloat(
            initialValue = 0.85f, targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(1100, easing = MotionTokens.Standard), RepeatMode.Reverse),
            label = "flame-alpha",
        ).value
    } else {
        scale = 1f; alpha = 1f
    }
    val flameTint = when {
        active -> colors.streak
        onGradient -> Color.White.copy(alpha = 0.6f)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    val textColor = when {
        onGradient -> Color.White
        active -> colors.streakText
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = if (active) Icons.Filled.LocalFireDepartment else Icons.Outlined.LocalFireDepartment,
            contentDescription = null,
            tint = flameTint,
            modifier = Modifier
                .size(20.dp)
                .graphicsLayer { scaleX = scale; scaleY = scale; this.alpha = alpha },
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = if (streakDays == 1) "1 day streak" else "$streakDays day streak",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = textColor,
        )
    }
}
