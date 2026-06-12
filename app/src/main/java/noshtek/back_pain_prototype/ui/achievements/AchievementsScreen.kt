package noshtek.back_pain_prototype.ui.achievements

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import noshtek.back_pain_prototype.ui.common.GlassCard
import noshtek.back_pain_prototype.ui.common.MotionTokens
import noshtek.back_pain_prototype.ui.common.ScreenHeader
import noshtek.back_pain_prototype.ui.common.entrance
import noshtek.back_pain_prototype.ui.gamification.AchievementBadge
import noshtek.back_pain_prototype.ui.gamification.RewardChip
import noshtek.back_pain_prototype.ui.gamification.achievementIcon
import noshtek.back_pain_prototype.ui.theme.PillShape
import noshtek.back_pain_prototype.ui.theme.SheetShape
import noshtek.back_pain_prototype.ui.theme.SpineIQTheme
import noshtek.back_pain_prototype.ui.theme.rewardGradientHorizontal
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Awards gallery — every achievement in catalog order so the path of
 * upcoming goals stays visible; locked badges show partial-progress rings.
 */
@Composable
fun AchievementsScreen(
    navController: NavController,
    viewModel: AchievementsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var detail by remember { mutableStateOf<AchievementUi?>(null) }

    Column(Modifier.fillMaxSize()) {
        ScreenHeader(
            title = "Awards",
            subtitle = "${state.unlockedCount} of ${state.totalCount} unlocked",
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            item(span = { GridItemSpan(3) }) {
                SummaryCard(
                    unlockedCount = state.unlockedCount,
                    totalCount = state.totalCount,
                    modifier = Modifier.entrance(0),
                )
            }
            itemsIndexed(state.items, key = { _, it -> it.achievement.id }) { index, item ->
                Box(contentAlignment = Alignment.Center) {
                    AchievementBadge(
                        title = item.achievement.title,
                        icon = achievementIcon(item.achievement.id),
                        unlocked = item.unlocked,
                        progress = item.progress,
                        size = 84.dp,
                        onClick = { detail = item },
                        modifier = Modifier.entrance(index + 1),
                    )
                }
            }
        }
    }

    detail?.let { item ->
        AchievementDetailSheet(item = item, onDismiss = { detail = null })
    }
}

@Composable
private fun SummaryCard(
    unlockedCount: Int,
    totalCount: Int,
    modifier: Modifier = Modifier,
) {
    val colors = SpineIQTheme.colors
    val target = if (totalCount == 0) 0f else unlockedCount.toFloat() / totalCount
    val animated by animateFloatAsState(
        targetValue = target,
        animationSpec = tween(MotionTokens.DurationScore, easing = MotionTokens.Emphasized),
        label = "awards-progress",
    )
    GlassCard(modifier = modifier.padding(bottom = 4.dp)) {
        Text(
            "Your collection",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "Earn awards by assessing, checking in and building streaks.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(12.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(PillShape)
                .background(colors.rewardContainer)
        ) {
            Box(
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animated)
                    .clip(PillShape)
                    .background(rewardGradientHorizontal())
            )
        }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
private fun AchievementDetailSheet(
    item: AchievementUi,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismiss, shape = SheetShape) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AchievementBadge(
                title = "",
                icon = achievementIcon(item.achievement.id),
                unlocked = item.unlocked,
                progress = item.progress,
                size = 112.dp,
            )
            Text(
                item.achievement.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                item.achievement.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(14.dp))
            RewardChip(coins = item.achievement.coinReward, xp = item.achievement.xpReward)
            Spacer(Modifier.height(14.dp))
            if (item.unlocked && item.unlockedAt != null) {
                Text(
                    "Unlocked " + Instant.ofEpochMilli(item.unlockedAt)
                        .atZone(ZoneId.systemDefault()).toLocalDate()
                        .format(DateTimeFormatter.ofPattern("d MMM yyyy")),
                    style = MaterialTheme.typography.labelMedium,
                    color = SpineIQTheme.colors.rewardText,
                    fontWeight = FontWeight.Bold,
                )
            } else {
                Text(
                    "How to earn: ${item.achievement.description}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
