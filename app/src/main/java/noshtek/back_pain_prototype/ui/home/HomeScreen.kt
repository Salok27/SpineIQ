package noshtek.back_pain_prototype.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.TipsAndUpdates
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import noshtek.back_pain_prototype.core.data.gamification.Economy
import noshtek.back_pain_prototype.navigation.Screen
import noshtek.back_pain_prototype.ui.avatar.AvatarSize
import noshtek.back_pain_prototype.ui.avatar.AvatarWithLevelRing
import noshtek.back_pain_prototype.ui.common.AnimatedCountText
import noshtek.back_pain_prototype.ui.common.GlassCard
import noshtek.back_pain_prototype.ui.common.GlassOnGradient
import noshtek.back_pain_prototype.ui.common.GradientHeroCard
import noshtek.back_pain_prototype.ui.common.PressableCard
import noshtek.back_pain_prototype.ui.common.PrimaryButton
import noshtek.back_pain_prototype.ui.common.ScreenHeader
import noshtek.back_pain_prototype.ui.common.ShimmerBox
import noshtek.back_pain_prototype.ui.common.SssTierBadge
import noshtek.back_pain_prototype.ui.common.TextActionButton
import noshtek.back_pain_prototype.ui.common.entrance
import noshtek.back_pain_prototype.ui.gamification.AchievementBadge
import noshtek.back_pain_prototype.ui.gamification.CoinBalancePill
import noshtek.back_pain_prototype.ui.gamification.DailyCheckInCard
import noshtek.back_pain_prototype.ui.gamification.RewardChip
import noshtek.back_pain_prototype.ui.gamification.StreakFlame
import noshtek.back_pain_prototype.ui.gamification.XpLevelBar
import noshtek.back_pain_prototype.ui.gamification.achievementIcon
import noshtek.back_pain_prototype.ui.theme.CardShape
import noshtek.back_pain_prototype.ui.theme.HeroShape
import noshtek.back_pain_prototype.ui.theme.SpineIQTheme
import noshtek.back_pain_prototype.ui.theme.rewardGradient
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val MotivationalTips = listOf(
    "Small daily movements beat occasional big workouts.",
    "Your spine loves variety — change position every 30 minutes.",
    "Good sleep posture is recovery time for your back.",
    "Walking is one of the best medicines for back pain.",
    "Strong core, happy spine. A little goes a long way.",
    "Stress shows up in your back — breathe and unwind.",
    "Hydrated discs are happy discs. Drink up.",
)

/**
 * V2 dashboard — the engagement hub: avatar + level hero, daily check-in,
 * goals, rewards preview, achievements strip, last result and insight.
 */
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val fmt = remember { DateTimeFormatter.ofPattern("d MMM yyyy") }
    val greeting = remember {
        when (LocalTime.now().hour) {
            in 0..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            else -> "Good evening"
        }
    }
    val tip = remember { MotivationalTips[(LocalDate.now().dayOfYear) % MotivationalTips.size] }

    fun navigateToTab(route: String) {
        navController.navigate(route) {
            popUpTo(Screen.Home.route) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    Column(Modifier.fillMaxSize()) {
        ScreenHeader(
            title = "SpineIQ",
            trailing = {
                CoinBalancePill(coins = state.coins, onClick = { navigateToTab(Screen.Shop.route) })
                Spacer(Modifier.width(4.dp))
                IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            },
        )

        if (state.isLoading) {
            HomeLoading()
            return@Column
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // 1 ── Avatar hero
            GradientHeroCard(modifier = Modifier.entrance(0)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AvatarWithLevelRing(
                        spec = state.equippedSpec,
                        level = state.level.number,
                        levelProgress = state.levelProgress,
                        size = AvatarSize.Large,
                    )
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            "$greeting,",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.85f),
                        )
                        Text(
                            state.userName.substringBefore(' ').ifBlank { "there" },
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                        )
                        GlassOnGradient {
                            Text(
                                state.level.name,
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        XpLevelBar(
                            level = state.level.number,
                            levelName = state.level.name,
                            xpIntoLevel = state.xpIntoLevel,
                            xpForNextLevel = state.xpForNextLevel,
                            compact = true,
                            onGradient = true,
                        )
                        StreakFlame(
                            streakDays = state.streakDays,
                            activeToday = state.checkedInToday || state.assessmentCompletedToday,
                            onGradient = true,
                        )
                    }
                }
            }

            // 2 ── Daily check-in
            DailyCheckInCard(
                checkedInToday = state.checkedInToday,
                todayMood = state.todayMood,
                streakDays = state.streakDays,
                last7Days = state.last7Days,
                onCheckIn = viewModel::checkIn,
                modifier = Modifier.entrance(1),
            )

            // 3 ── Today's goals
            DailyGoalsCard(
                checkedIn = state.checkedInToday,
                assessmentDone = state.assessmentCompletedToday,
                modifier = Modifier.entrance(2),
            )

            // 4 ── Primary CTA + reward preview
            Column(Modifier.entrance(3), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PrimaryButton(
                    onClick = { navController.navigate(Screen.AssessmentGraph.route) },
                    label = "Start New Assessment",
                    icon = Icons.Filled.Add,
                    modifier = Modifier.fillMaxWidth(),
                )
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RewardChip(
                        coins = Economy.COINS_PER_FULL_ASSESSMENT,
                        xp = Economy.XP_PER_FULL_ASSESSMENT,
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "earned on completion",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            // 5 ── Recent achievements
            if (state.recentAchievements.isNotEmpty()) {
                Column(Modifier.entrance(4)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Achievements",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                        )
                        TextActionButton(
                            onClick = { navigateToTab(Screen.Achievements.route) },
                            label = "View all",
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(state.recentAchievements, key = { it.achievement.id }) { item ->
                            AchievementBadge(
                                title = item.achievement.title,
                                icon = achievementIcon(item.achievement.id),
                                unlocked = item.unlocked,
                                progress = item.progress,
                                size = 64.dp,
                                onClick = { navigateToTab(Screen.Achievements.route) },
                            )
                        }
                    }
                }
            }

            // 6 ── Last assessment summary
            if (state.lastAssessmentId != null && state.lastScores != null) {
                PressableCard(
                    onClick = { navController.navigate(Screen.FullReport.route(state.lastAssessmentId!!)) },
                    modifier = Modifier.entrance(5),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Text(
                                "LAST ASSESSMENT",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                AnimatedCountText(
                                    target = state.lastScores!!.totalSSSScore,
                                    style = MaterialTheme.typography.displaySmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                                Text(
                                    "/ 11",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(bottom = 4.dp),
                                )
                            }
                            SssTierBadge(state.lastScores!!.sssSeverityTier)
                            state.lastAssessmentDate?.let {
                                Text(
                                    it.format(fmt),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                        Icon(
                            Icons.Filled.ChevronRight,
                            contentDescription = "View report",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            // 7 ── Motivational insight
            GlassCard(modifier = Modifier.entrance(6)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .width(3.dp)
                            .height(40.dp)
                            .clip(CircleShape)
                            .background(rewardGradient())
                    )
                    Spacer(Modifier.width(12.dp))
                    Icon(
                        Icons.Outlined.TipsAndUpdates,
                        contentDescription = null,
                        tint = SpineIQTheme.colors.rewardText,
                        modifier = Modifier.size(22.dp),
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        tip,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }

            // 8 ── Privacy assurance + footer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .entrance(7)
                    .clip(CardShape)
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Icon(
                    Icons.Filled.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(18.dp),
                )
                Text(
                    "Private & encrypted — your health data never leaves this device.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Text(
                "SpineIQ · SSS v1.0",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        }
    }
}

@Composable
private fun DailyGoalsCard(
    checkedIn: Boolean,
    assessmentDone: Boolean,
    modifier: Modifier = Modifier,
) {
    GlassCard(modifier = modifier) {
        Text(
            "Today's goals",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(10.dp))
        GoalRow(
            label = "Daily check-in",
            done = checkedIn,
            coins = Economy.COINS_PER_CHECKIN,
            xp = Economy.XP_PER_CHECKIN,
        )
        Spacer(Modifier.height(10.dp))
        GoalRow(
            label = "Complete an assessment",
            done = assessmentDone,
            coins = Economy.COINS_PER_FULL_ASSESSMENT,
            xp = Economy.XP_PER_FULL_ASSESSMENT,
        )
    }
}

@Composable
private fun GoalRow(
    label: String,
    done: Boolean,
    coins: Int,
    xp: Int,
) {
    val colors = SpineIQTheme.colors
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (done) {
            Box(
                Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(colors.reward),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Filled.Check,
                    contentDescription = "Done",
                    tint = Color.White,
                    modifier = Modifier.size(14.dp),
                )
            }
        } else {
            Icon(
                Icons.Outlined.Circle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(22.dp),
            )
        }
        Spacer(Modifier.width(10.dp))
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (done) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.weight(1f),
        )
        RewardChip(coins = coins, xp = xp)
    }
}

@Composable
private fun HomeLoading(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ShimmerBox(
            Modifier
                .fillMaxWidth()
                .height(196.dp),
            shape = HeroShape,
        )
        ShimmerBox(
            Modifier
                .fillMaxWidth()
                .height(150.dp),
            shape = CardShape,
        )
        ShimmerBox(
            Modifier
                .fillMaxWidth()
                .height(120.dp),
            shape = CardShape,
        )
    }
}
