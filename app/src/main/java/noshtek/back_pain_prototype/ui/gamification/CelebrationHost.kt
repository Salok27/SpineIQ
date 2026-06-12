package noshtek.back_pain_prototype.ui.gamification

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import noshtek.back_pain_prototype.core.data.gamification.Achievement
import noshtek.back_pain_prototype.core.data.gamification.GameLevel
import noshtek.back_pain_prototype.ui.common.GlassCard
import noshtek.back_pain_prototype.ui.common.MotionTokens
import noshtek.back_pain_prototype.ui.theme.Ink
import noshtek.back_pain_prototype.ui.theme.SpineIQTheme

/**
 * Global celebration layer mounted ABOVE the NavHost in MainActivity. When
 * idle it emits nothing at all — no box, no pointer handling — so it can
 * never eat a tap. Toasts pass touches through; overlay celebrations scrim
 * the screen and dismiss on tap or after a short auto-timeout.
 */
@Composable
fun CelebrationHost(viewModel: CelebrationViewModel = hiltViewModel()) {
    val current by viewModel.current.collectAsStateWithLifecycle()
    when (val celebration = current) {
        null -> Unit
        is Celebration.Toast -> RewardToast(
            coins = celebration.coins,
            xp = celebration.xp,
            onTimeout = viewModel::dismissCurrent,
        )
        is Celebration.LevelUpOverlay -> CelebrationOverlay(onDismiss = viewModel::dismissCurrent) {
            LevelUpContent(celebration.level)
        }
        is Celebration.AchievementOverlay -> CelebrationOverlay(onDismiss = viewModel::dismissCurrent) {
            AchievementContent(celebration.achievement)
        }
        is Celebration.StreakOverlay -> CelebrationOverlay(onDismiss = viewModel::dismissCurrent) {
            StreakContent(celebration.days, celebration.coinBonus)
        }
    }
}

/** Scrim + confetti + centred glass card; tap anywhere or wait to dismiss. */
@Composable
private fun CelebrationOverlay(
    onDismiss: () -> Unit,
    content: @Composable () -> Unit,
) {
    LaunchedEffect(Unit) {
        delay(2_800)
        onDismiss()
    }
    Box(
        Modifier
            .fillMaxSize()
            .background(Ink.copy(alpha = 0.62f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss,
            ),
        contentAlignment = Alignment.Center,
    ) {
        ConfettiBurst(Modifier.fillMaxSize(), particleCount = 110)
        GlassCard(
            modifier = Modifier
                .padding(horizontal = 40.dp)
                .popIn(),
        ) {
            content()
        }
    }
}

/** Overshoot scale-in for the celebration card. */
@Composable
private fun Modifier.popIn(): Modifier {
    var started by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { started = true }
    val animated by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (started) 1f else 0.7f,
        animationSpec = tween(MotionTokens.DurationMedium, easing = MotionTokens.Overshoot),
        label = "pop-in-scale",
    )
    return this.scale(animated).graphicsLayer { alpha = ((animated - 0.7f) / 0.3f).coerceIn(0f, 1f) }
}

@Composable
private fun LevelUpContent(level: GameLevel) {
    val colors = SpineIQTheme.colors
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(contentAlignment = Alignment.Center) {
            RayDisc()
            Box(
                Modifier
                    .size(76.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    "${level.number}",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = colors.reward,
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            "Level up!",
            style = MaterialTheme.typography.labelLarge,
            color = colors.rewardText,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            level.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
        )
    }
}

/** Slowly rotating ray wedges behind the level number. */
@Composable
private fun RayDisc(diameter: androidx.compose.ui.unit.Dp = 140.dp) {
    val colors = SpineIQTheme.colors
    val transition = rememberInfiniteTransition(label = "rays")
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(8_000, easing = MotionTokens.Linear)),
        label = "ray-rotation",
    )
    val rayColor = colors.reward.copy(alpha = 0.16f)
    Canvas(Modifier.size(diameter)) {
        for (i in 0 until 12) {
            drawArc(
                color = rayColor,
                startAngle = rotation + i * 30f - 7f,
                sweepAngle = 14f,
                useCenter = true,
            )
        }
    }
}

@Composable
private fun AchievementContent(achievement: Achievement) {
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AchievementBadge(
            title = "",
            icon = achievementIcon(achievement.id),
            unlocked = true,
            size = 96.dp,
        )
        Text(
            "Achievement unlocked",
            style = MaterialTheme.typography.labelLarge,
            color = SpineIQTheme.colors.rewardText,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            achievement.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(10.dp))
        RewardChip(coins = achievement.coinReward, xp = achievement.xpReward, emphasized = true)
    }
}

@Composable
private fun StreakContent(days: Int, coinBonus: Int) {
    val colors = SpineIQTheme.colors
    val transition = rememberInfiniteTransition(label = "streak-flame")
    val pulse by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            tween(900, easing = MotionTokens.Standard),
            androidx.compose.animation.core.RepeatMode.Reverse,
        ),
        label = "streak-pulse",
    )
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            Icons.Filled.LocalFireDepartment,
            contentDescription = null,
            tint = colors.streak,
            modifier = Modifier
                .size(64.dp)
                .graphicsLayer { scaleX = pulse; scaleY = pulse },
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "$days-day streak!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "Consistency is the best medicine for your back.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(10.dp))
        RewardChip(coins = coinBonus, emphasized = true)
    }
}
