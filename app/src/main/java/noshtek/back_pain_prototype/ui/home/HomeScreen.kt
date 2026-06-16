package noshtek.back_pain_prototype.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import noshtek.back_pain_prototype.navigation.Screen
import noshtek.back_pain_prototype.ui.common.AnimatedCountText
import noshtek.back_pain_prototype.ui.common.GlassCard
import noshtek.back_pain_prototype.ui.common.GlowCard
import noshtek.back_pain_prototype.ui.common.LivingSpine
import noshtek.back_pain_prototype.ui.common.MicroLabel
import noshtek.back_pain_prototype.ui.common.NebulaBackground
import noshtek.back_pain_prototype.ui.common.PressableCard
import noshtek.back_pain_prototype.ui.common.PrimaryButton
import noshtek.back_pain_prototype.ui.common.RitualRow
import noshtek.back_pain_prototype.ui.common.ScreenHeader
import noshtek.back_pain_prototype.ui.common.ShimmerBox
import noshtek.back_pain_prototype.ui.common.SssTierBadge
import noshtek.back_pain_prototype.ui.common.breathe
import noshtek.back_pain_prototype.ui.common.entrance
import noshtek.back_pain_prototype.ui.common.ritualIcon
import noshtek.back_pain_prototype.ui.gamification.DailyCheckInCard
import noshtek.back_pain_prototype.ui.gamification.StreakFlame
import noshtek.back_pain_prototype.ui.theme.ButtonShape
import noshtek.back_pain_prototype.ui.theme.CardShape
import noshtek.back_pain_prototype.ui.theme.HeroShape
import noshtek.back_pain_prototype.ui.theme.SpineIQTheme
import noshtek.back_pain_prototype.ui.theme.brandGradient
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
 * Home — the calm command center: a Living Spine that reflects your vitality,
 * a daily check-in, today's personalized rituals, the assessment CTA, your last
 * result and a daily insight.
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

    NebulaBackground {
        Column(Modifier.fillMaxSize()) {
            ScreenHeader(
                title = "SpineIQ",
                trailing = {
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
                // 1 ── Living Spine hero
                GlowCard(
                    modifier = Modifier.entrance(0),
                    shape = HeroShape,
                ) {
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        LivingSpine(
                            vitality = state.vitality,
                            modifier = Modifier.size(116.dp, 188.dp),
                        )
                        Spacer(Modifier.width(8.dp))
                        Column(
                            Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            MicroLabel(greeting)
                            Text(
                                state.userName.substringBefore(' ').ifBlank { "there" },
                                style = MaterialTheme.typography.displaySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Row(verticalAlignment = Alignment.Bottom) {
                                AnimatedCountText(
                                    target = state.vitality,
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(
                                    " spine vitality",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(bottom = 3.dp),
                                )
                            }
                            StreakFlame(
                                streakDays = state.streakDays,
                                activeToday = state.checkedInToday || state.ritualsDoneToday > 0,
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

                // 3 ── Today's personalized rituals
                if (state.rituals.isNotEmpty()) {
                    GlowCard(modifier = Modifier.entrance(2)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                MicroLabel("Today's rituals")
                                Text(
                                    "Personalized for your spine",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                            Text(
                                "${state.ritualsDoneToday}/${state.ritualsTotalToday}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = SpineIQTheme.colors.accentText,
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            state.rituals.forEach { status ->
                                RitualRow(
                                    title = status.ritual.title,
                                    subtitle = status.ritual.subtitle,
                                    icon = ritualIcon(status.ritual.category),
                                    done = status.done,
                                    onToggle = { viewModel.completeRitual(status.ritual.id) },
                                )
                            }
                        }
                    }
                }

                // 4 ── Primary CTA
                Column(Modifier.entrance(3), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    PrimaryButton(
                        onClick = { navController.navigate(Screen.AssessmentGraph.route) },
                        label = "Start New Assessment",
                        icon = Icons.Filled.Add,
                        modifier = Modifier
                            .fillMaxWidth()
                            .breathe(),
                    )
                    Text(
                        "A few minutes · refreshes your Spine Vitality",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                    )
                }

                // 5 ── Last assessment summary
                if (state.lastAssessmentId != null && state.lastScores != null) {
                    PressableCard(
                        onClick = { navController.navigate(Screen.FullReport.route(state.lastAssessmentId!!)) },
                        modifier = Modifier.entrance(4),
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
                                MicroLabel("Last assessment")
                                Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    AnimatedCountText(
                                        target = state.lastScores!!.totalSSSScore,
                                        style = MaterialTheme.typography.displaySmall,
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

                // 6 ── Daily insight
                GlassCard(modifier = Modifier.entrance(5)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier
                                .width(3.dp)
                                .height(40.dp)
                                .clip(CircleShape)
                                .background(brandGradient())
                        )
                        Spacer(Modifier.width(12.dp))
                        Icon(
                            Icons.Outlined.TipsAndUpdates,
                            contentDescription = null,
                            tint = SpineIQTheme.colors.accentText,
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

                // 7 ── Privacy assurance + footer
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .entrance(6)
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
