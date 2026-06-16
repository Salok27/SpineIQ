@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)

package noshtek.back_pain_prototype.ui.journey

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import noshtek.back_pain_prototype.core.data.gamification.VitalityStage
import noshtek.back_pain_prototype.ui.common.AnimatedCountText
import noshtek.back_pain_prototype.ui.common.GlowCard
import noshtek.back_pain_prototype.ui.common.LivingSpine
import noshtek.back_pain_prototype.ui.common.MicroLabel
import noshtek.back_pain_prototype.ui.common.NebulaBackground
import noshtek.back_pain_prototype.ui.common.ScreenHeader
import noshtek.back_pain_prototype.ui.common.entrance
import noshtek.back_pain_prototype.ui.gamification.MilestoneBadge
import noshtek.back_pain_prototype.ui.gamification.StreakFlame
import noshtek.back_pain_prototype.ui.gamification.milestoneIcon
import noshtek.back_pain_prototype.ui.theme.HeroShape
import noshtek.back_pain_prototype.ui.theme.SpineIQTheme

private fun stageLabel(stage: VitalityStage): String = when (stage) {
    VitalityStage.DIM -> "Dim — let's brighten it"
    VitalityStage.FLICKER -> "Flickering — keep going"
    VitalityStage.STEADY -> "Steady"
    VitalityStage.BRIGHT -> "Bright"
    VitalityStage.RADIANT -> "Radiant"
}

/**
 * Journey — the recovery hub. A Living Spine that mirrors your vitality, your
 * streak, and the milestones along your recovery path (unlocked + how-close
 * hints on the rest).
 */
@Composable
fun JourneyScreen(
    navController: NavController,
    viewModel: JourneyViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    NebulaBackground {
        Column(Modifier.fillMaxSize()) {
            ScreenHeader(title = "Your journey", subtitle = "Recovery path & milestones")

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Vitality hero
                GlowCard(modifier = Modifier.entrance(0), shape = HeroShape) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        LivingSpine(vitality = state.vitality, modifier = Modifier.size(116.dp, 188.dp))
                        Spacer(Modifier.width(8.dp))
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            MicroLabel("Spine vitality")
                            Row(verticalAlignment = Alignment.Bottom) {
                                AnimatedCountText(
                                    target = state.vitality,
                                    style = MaterialTheme.typography.displayMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(
                                    " / 100",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(bottom = 4.dp),
                                )
                            }
                            Text(
                                stageLabel(state.stage),
                                style = MaterialTheme.typography.titleSmall,
                                color = SpineIQTheme.colors.accentText,
                                fontWeight = FontWeight.SemiBold,
                            )
                            StreakFlame(
                                streakDays = state.streakDays,
                                activeToday = state.ritualsDoneToday > 0,
                            )
                        }
                    }
                }

                // Milestones
                Column(Modifier.entrance(1)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            MicroLabel("Milestones")
                            Text(
                                "Your recovery path",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        Text(
                            "${state.unlockedCount} of ${state.milestones.size}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SpineIQTheme.colors.rewardText,
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        state.milestones.forEach { item ->
                            MilestoneBadge(
                                title = item.milestone.title,
                                icon = milestoneIcon(item.milestone.id),
                                unlocked = item.unlocked,
                                progress = item.progress,
                                size = 76.dp,
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Reassess regularly and keep your daily rituals to brighten your spine and reach new milestones.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
