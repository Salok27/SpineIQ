package noshtek.back_pain_prototype.ui.gamification

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import noshtek.back_pain_prototype.ui.common.neonGlow
import noshtek.back_pain_prototype.ui.theme.BadgeShape
import noshtek.back_pain_prototype.ui.theme.SpineIQTheme

/** UI icon registry for AchievementCatalog ids (the catalog itself stays Android-free). */
fun achievementIcon(achievementId: String): ImageVector = when (achievementId) {
    "first_assessment" -> Icons.Filled.Verified
    "assessment_master" -> Icons.Filled.WorkspacePremium
    "recovery_champion" -> Icons.Filled.MilitaryTech
    "pain_detective" -> Icons.Filled.Psychology
    "functional_expert" -> Icons.Filled.FitnessCenter
    "red_flag_aware" -> Icons.Filled.Flag
    "daily_devotee" -> Icons.Filled.EventAvailable
    "streak_3" -> Icons.Filled.LocalFireDepartment
    "streak_7" -> Icons.Filled.Whatshot
    "streak_30" -> Icons.Filled.AutoAwesome
    "first_purchase" -> Icons.Filled.ShoppingBag
    else -> Icons.Filled.EmojiEvents
}

/**
 * Collectible achievement tile. Unlocked: reward container + gradient ring.
 * Locked: greyed silhouette with a lock glyph and an optional partial
 * [progress] ring (0..1) as the "how close am I" hint.
 */
@Composable
fun AchievementBadge(
    title: String,
    icon: ImageVector,
    unlocked: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 72.dp,
    progress: Float? = null,
    onClick: (() -> Unit)? = null,
) {
    val colors = SpineIQTheme.colors
    val rewardStops = colors.rewardStops
    val ringColor = colors.reward
    val outline = MaterialTheme.colorScheme.outlineVariant
    Column(
        modifier
            .width(size + 16.dp)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            Modifier
                .size(size)
                .then(
                    if (unlocked) Modifier.neonGlow(colors.reward, BadgeShape, elevation = 12.dp, alpha = 0.38f)
                    else Modifier
                )
                .drawBehind {
                    val stroke = Stroke(width = 2.dp.toPx())
                    val inset = stroke.width / 2f
                    val arcSize = Size(this.size.width - stroke.width, this.size.height - stroke.width)
                    val corner = 18.dp.toPx()
                    when {
                        unlocked -> drawRoundRect(
                            brush = Brush.linearGradient(rewardStops),
                            topLeft = Offset(inset, inset),
                            size = arcSize,
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(corner),
                            style = stroke,
                        )
                        progress != null && progress > 0f -> {
                            drawRoundRect(
                                color = outline,
                                topLeft = Offset(inset, inset),
                                size = arcSize,
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(corner),
                                style = stroke,
                            )
                            drawArc(
                                color = ringColor,
                                startAngle = -90f,
                                sweepAngle = progress.coerceIn(0f, 1f) * 360f,
                                useCenter = false,
                                topLeft = Offset(inset, inset),
                                size = arcSize,
                                style = stroke,
                            )
                        }
                        else -> drawRoundRect(
                            color = outline,
                            topLeft = Offset(inset, inset),
                            size = arcSize,
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(corner),
                            style = stroke,
                        )
                    }
                }
                .padding(4.dp)
                .clip(BadgeShape)
                .background(if (unlocked) colors.rewardContainer else MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (unlocked) colors.reward else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .size(size * 0.42f)
                    .alpha(if (unlocked) 1f else 0.45f),
            )
            if (!unlocked) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = "Locked",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(6.dp)
                        .size(size * 0.2f),
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = if (unlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            maxLines = 2,
        )
    }
}
