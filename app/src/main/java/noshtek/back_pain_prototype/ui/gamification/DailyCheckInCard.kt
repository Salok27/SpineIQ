package noshtek.back_pain_prototype.ui.gamification

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.SentimentNeutral
import androidx.compose.material.icons.filled.SentimentSatisfiedAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import noshtek.back_pain_prototype.core.data.gamification.CheckInMood
import noshtek.back_pain_prototype.ui.common.GlassCard
import noshtek.back_pain_prototype.ui.common.MicroLabel
import noshtek.back_pain_prototype.ui.theme.SpineIQTheme

private data class MoodOption(
    val mood: CheckInMood,
    val label: String,
    val icon: ImageVector,
    val tint: Color,
)

/**
 * Dashboard daily check-in: one mood tap per day earns a small reward and
 * keeps the streak alive. Mood tints are feelings-feedback colours, not
 * clinical risk colours.
 */
@Composable
fun DailyCheckInCard(
    checkedInToday: Boolean,
    todayMood: CheckInMood?,
    streakDays: Int,
    last7Days: List<Boolean>,
    onCheckIn: (CheckInMood) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = SpineIQTheme.colors
    val moods = remember(colors) {
        listOf(
            MoodOption(CheckInMood.BETTER, "Better", Icons.Filled.SentimentSatisfiedAlt, colors.successFill),
            MoodOption(CheckInMood.SAME, "Same", Icons.Filled.SentimentNeutral, colors.warningFill),
            MoodOption(CheckInMood.WORSE, "Worse", Icons.Filled.SentimentDissatisfied, Color(0xFFEF4444)),
        )
    }

    GlassCard(modifier = modifier) {
        MicroLabel("Today")
        Spacer(Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Daily check-in",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
            )
            StreakFlame(streakDays = streakDays, activeToday = checkedInToday)
        }
        Spacer(Modifier.height(12.dp))
        AnimatedContent(
            targetState = checkedInToday,
            transitionSpec = {
                (fadeIn() + slideInVertically { it / 4 }).togetherWith(fadeOut())
            },
            label = "check-in-content",
        ) { done ->
            if (!done) {
                Column {
                    Text(
                        "How's your back feeling today?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        moods.forEach { option ->
                            MoodButton(option = option, onClick = { onCheckIn(option.mood) })
                        }
                    }
                }
            } else {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val selected = moods.firstOrNull { it.mood == todayMood }
                        if (selected != null) {
                            Icon(
                                selected.icon,
                                contentDescription = null,
                                tint = selected.tint,
                                modifier = Modifier.size(24.dp),
                            )
                            Spacer(Modifier.width(8.dp))
                        }
                        Text(
                            "Checked in" + (todayMood?.let { " — feeling ${it.name.lowercase()}" } ?: ""),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Spacer(Modifier.width(6.dp))
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = null,
                            tint = colors.successFill,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    WeekDots(last7Days = last7Days)
                }
            }
        }
    }
}

@Composable
private fun MoodButton(
    option: MoodOption,
    onClick: () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.88f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "mood-press",
    )
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier
                .size(46.dp)
                .graphicsLayer { scaleX = scale; scaleY = scale }
                .clip(CircleShape)
                .background(option.tint.copy(alpha = 0.14f))
                .border(1.dp, option.tint.copy(alpha = 0.55f), CircleShape)
                .clickable(interactionSource = interaction, indication = ripple(), onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(option.icon, contentDescription = option.label, tint = option.tint, modifier = Modifier.size(26.dp))
        }
        Spacer(Modifier.height(4.dp))
        Text(
            option.label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/** Last 7 days, oldest → today; filled reward dots for checked-in days. */
@Composable
private fun WeekDots(last7Days: List<Boolean>) {
    val colors = SpineIQTheme.colors
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(
            "This week",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.width(2.dp))
        last7Days.forEachIndexed { index, checked ->
            val isToday = index == last7Days.lastIndex
            Box(
                Modifier
                    .size(if (isToday) 10.dp else 8.dp)
                    .clip(CircleShape)
                    .background(if (checked) colors.reward else MaterialTheme.colorScheme.outlineVariant)
            )
        }
    }
}
