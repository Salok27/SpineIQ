package noshtek.back_pain_prototype.ui.common

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WaterDrop
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import noshtek.back_pain_prototype.core.data.gamification.RitualCategory
import noshtek.back_pain_prototype.ui.theme.CardShape
import noshtek.back_pain_prototype.ui.theme.SpineIQTheme

/** Leading icon for a ritual, by category. */
fun ritualIcon(category: RitualCategory): ImageVector = when (category) {
    RitualCategory.MOVEMENT -> Icons.Filled.DirectionsWalk
    RitualCategory.POSTURE -> Icons.Filled.AccessibilityNew
    RitualCategory.STRENGTH -> Icons.Filled.FitnessCenter
    RitualCategory.RECOVERY -> Icons.Filled.Spa
    RitualCategory.HYDRATION -> Icons.Filled.WaterDrop
}

/**
 * One daily ritual as a tappable full-width row: a soft category icon, the
 * title + subtitle, and a check toggle that springs and fills sage when done.
 * Completing is a one-way action for the day (the ledger keeps it idempotent),
 * so once [done] it reads as gently settled rather than interactive.
 */
@Composable
fun RitualRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    done: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = SpineIQTheme.colors
    val checkScale by animateFloatAsState(
        targetValue = if (done) 1f else 0.9f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "ritual-check",
    )
    Row(
        modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(if (done) colors.accentContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceContainerHigh)
            .then(
                if (done) Modifier
                else Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(color = colors.accent),
                    onClick = onToggle,
                )
            )
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(colors.accent.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = colors.accentText, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textDecoration = if (done) TextDecoration.LineThrough else null,
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(Modifier.width(12.dp))
        Box(
            Modifier
                .size(28.dp)
                .graphicsLayer { scaleX = checkScale; scaleY = checkScale }
                .clip(CircleShape)
                .background(if (done) colors.accent else MaterialTheme.colorScheme.surface)
                .border(
                    1.5.dp,
                    if (done) colors.accent else MaterialTheme.colorScheme.outline,
                    CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (done) {
                Icon(
                    Icons.Filled.Check,
                    contentDescription = "Done",
                    tint = colors.onAccent,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}
