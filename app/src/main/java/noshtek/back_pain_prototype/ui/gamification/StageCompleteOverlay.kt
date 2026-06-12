package noshtek.back_pain_prototype.ui.gamification

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import noshtek.back_pain_prototype.core.data.gamification.Economy
import noshtek.back_pain_prototype.ui.common.GlassCard
import noshtek.back_pain_prototype.ui.common.MotionTokens
import noshtek.back_pain_prototype.ui.theme.Ink
import noshtek.back_pain_prototype.ui.theme.SpineIQTheme
import noshtek.back_pain_prototype.ui.theme.rewardGradient

/**
 * Brief stage-complete interstitial shown on Next: a check pop-in, the
 * stage's reward, and a small confetti burst, then [onFinished] runs the
 * persist + navigate (auto after ~1.1s, or immediately on tap). Renders
 * nothing while [visible] is false; onFinished fires exactly once.
 */
@Composable
fun StageCompleteOverlay(
    visible: Boolean,
    stepLabel: String,
    onFinished: () -> Unit,
    coins: Int = Economy.COINS_PER_STEP,
    xp: Int = Economy.XP_PER_STEP,
) {
    if (!visible) return

    var consumed by remember { mutableStateOf(false) }
    fun finish() {
        if (!consumed) {
            consumed = true
            onFinished()
        }
    }
    LaunchedEffect(Unit) {
        delay(1_150)
        finish()
    }

    var started by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { started = true }
    val scrimAlpha by animateFloatAsState(
        targetValue = if (started) 0.40f else 0f,
        animationSpec = tween(MotionTokens.DurationFast),
        label = "stage-scrim",
    )
    val pop by animateFloatAsState(
        targetValue = if (started) 1f else 0.6f,
        animationSpec = tween(MotionTokens.DurationMedium, easing = MotionTokens.Overshoot),
        label = "stage-pop",
    )

    Box(
        Modifier
            .fillMaxSize()
            .background(Ink.copy(alpha = scrimAlpha))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = ::finish,
            ),
        contentAlignment = Alignment.Center,
    ) {
        ConfettiBurst(Modifier.fillMaxSize(), particleCount = 40, durationMillis = 900)
        GlassCard(
            modifier = Modifier
                .padding(horizontal = 48.dp)
                .scale(pop)
                .graphicsLayer { alpha = ((pop - 0.6f) / 0.4f).coerceIn(0f, 1f) },
        ) {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(rewardGradient()),
                    contentAlignment = Alignment.Center,
                ) {
                    androidx.compose.material3.Icon(
                        Icons.Filled.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(30.dp),
                    )
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    stepLabel,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                )
                Spacer(Modifier.height(10.dp))
                RewardChip(coins = coins, xp = xp, emphasized = true)
                Spacer(Modifier.height(6.dp))
                Text(
                    "Tap to continue",
                    style = MaterialTheme.typography.labelSmall,
                    color = SpineIQTheme.colors.rewardText,
                )
            }
        }
    }
}
