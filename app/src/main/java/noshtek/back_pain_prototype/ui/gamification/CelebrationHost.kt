package noshtek.back_pain_prototype.ui.gamification

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import noshtek.back_pain_prototype.core.data.gamification.Milestone
import noshtek.back_pain_prototype.ui.common.GlassCard
import noshtek.back_pain_prototype.ui.common.MotionTokens
import noshtek.back_pain_prototype.ui.theme.Ink
import noshtek.back_pain_prototype.ui.theme.SpineIQTheme

/**
 * Global celebration layer mounted ABOVE the NavHost in MainActivity. When idle
 * it emits nothing at all — no box, no pointer handling — so it can never eat a
 * tap. Toasts pass touches through; overlay celebrations scrim the screen and
 * dismiss on tap or after a short auto-timeout.
 */
@Composable
fun CelebrationHost(viewModel: CelebrationViewModel = hiltViewModel()) {
    val current by viewModel.current.collectAsStateWithLifecycle()
    when (val celebration = current) {
        null -> Unit
        is Celebration.Toast -> RewardToast(
            message = celebration.message,
            onTimeout = viewModel::dismissCurrent,
        )
        is Celebration.MilestoneOverlay -> CelebrationOverlay(onDismiss = viewModel::dismissCurrent) {
            MilestoneContent(celebration.milestone)
        }
        is Celebration.StreakOverlay -> CelebrationOverlay(onDismiss = viewModel::dismissCurrent) {
            StreakContent(celebration.days)
        }
    }
}

/** Scrim + confetti + centred soft card; tap anywhere or wait to dismiss. */
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
            .background(Ink.copy(alpha = 0.55f))
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
    val animated by animateFloatAsState(
        targetValue = if (started) 1f else 0.7f,
        animationSpec = tween(MotionTokens.DurationMedium, easing = MotionTokens.Overshoot),
        label = "pop-in-scale",
    )
    return this.scale(animated).graphicsLayer { alpha = ((animated - 0.7f) / 0.3f).coerceIn(0f, 1f) }
}

@Composable
private fun MilestoneContent(milestone: Milestone) {
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        MilestoneBadge(
            title = "",
            icon = milestoneIcon(milestone.id),
            unlocked = true,
            size = 96.dp,
        )
        Text(
            "Milestone reached",
            style = MaterialTheme.typography.labelLarge,
            color = SpineIQTheme.colors.rewardText,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            milestone.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            milestone.description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun StreakContent(days: Int) {
    val colors = SpineIQTheme.colors
    val transition = rememberInfiniteTransition(label = "streak-flame")
    val pulse by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            tween(900, easing = MotionTokens.Standard),
            RepeatMode.Reverse,
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
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "Consistency is the best medicine for your back.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}
