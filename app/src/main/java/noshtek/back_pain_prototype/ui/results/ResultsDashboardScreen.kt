package noshtek.back_pain_prototype.ui.results

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import noshtek.back_pain_prototype.core.data.db.entity.ScoresRecordEntity
import noshtek.back_pain_prototype.navigation.Screen
import noshtek.back_pain_prototype.ui.common.LabelledScoreBar
import noshtek.back_pain_prototype.ui.common.LifestyleTierBadge
import noshtek.back_pain_prototype.ui.common.PrimaryButton
import noshtek.back_pain_prototype.ui.common.RiskTileSmall
import noshtek.back_pain_prototype.ui.common.ScoreHeroCard
import noshtek.back_pain_prototype.ui.common.SecondaryButton
import noshtek.back_pain_prototype.ui.common.SectionCard
import noshtek.back_pain_prototype.ui.common.SssTierBadge
import noshtek.back_pain_prototype.ui.common.entrance
import noshtek.back_pain_prototype.core.data.gamification.Economy
import noshtek.back_pain_prototype.ui.gamification.ConfettiBurst
import noshtek.back_pain_prototype.ui.gamification.RewardChip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsDashboardScreen(
    navController: NavController,
    viewModel: ResultsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    // One-shot: survives rotation/process death so confetti never replays.
    var celebrated by rememberSaveable { mutableStateOf(false) }
    val showConfetti = viewModel.celebrate && !celebrated

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Your Results", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
        }
    ) { padding ->
        when {
            state.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            state.scores == null -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Results not available.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            else -> {
                val scores = state.scores!!
                Box(Modifier.padding(padding).fillMaxSize()) {
                if (showConfetti) {
                    // Decorative only — drawn above the content, never interactive.
                    ConfettiBurst(
                        modifier = Modifier
                            .fillMaxSize()
                            .zIndex(1f),
                        particleCount = 120,
                        onFinished = { celebrated = true },
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    // Hero card — animated gauge + composite classification
                    ScoreHeroCard(
                        scoreLabel = "SSS Score",
                        score = scores.totalSSSScore,
                        maxScore = 11,
                        classification = scores.backPainRiskClassification,
                        modifier = Modifier.entrance(0),
                    )

                    if (viewModel.celebrate) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .entrance(1),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RewardChip(
                                coins = Economy.COINS_PER_FULL_ASSESSMENT,
                                xp = Economy.XP_PER_FULL_ASSESSMENT,
                                emphasized = true,
                            )
                            Text(
                                "  Assessment complete!",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }

                    // SSS breakdown
                    SectionCard(
                        title = "Spine Severity System (SSS)",
                        icon = Icons.Filled.MonitorHeart,
                        accent = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.entrance(1),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("Total Score", style = MaterialTheme.typography.titleSmall)
                            SssTierBadge(scores.sssSeverityTier)
                        }
                        Spacer(Modifier.height(10.dp))
                        LabelledScoreBar(
                            label = "SSS Total",
                            score = scores.totalSSSScore,
                            maxScore = 11,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Spacer(Modifier.height(12.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        Spacer(Modifier.height(8.dp))
                        SssScoreBreakdown(scores)
                    }

                    // Lifestyle risk
                    SectionCard(
                        title = "Lifestyle Risk",
                        icon = Icons.AutoMirrored.Filled.DirectionsWalk,
                        accent = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.entrance(2),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("Overall Tier", style = MaterialTheme.typography.titleSmall)
                            LifestyleTierBadge(scores.lifestyleRiskTier)
                        }
                        Spacer(Modifier.height(12.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            RiskTileSmall("Sitting Risk", scores.sittingRisk)
                            RiskTileSmall("Walking Risk", scores.walkingRisk)
                            RiskTileSmall("Exercise Risk", scores.exerciseRisk)
                            RiskTileSmall("Sleep Risk", scores.sleepRisk)
                        }
                    }

                    // BMI
                    SectionCard(
                        title = "BMI",
                        icon = Icons.Filled.MonitorWeight,
                        accent = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.entrance(3),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column {
                                Text(
                                    scores.bmiCategory.name.lowercase().replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.titleSmall,
                                )
                                Text(
                                    "Body Mass Index",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            Text(
                                "%.1f".format(scores.bmiScore),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.tertiary,
                            )
                        }
                    }

                    PrimaryButton(
                        onClick = { navController.navigate(Screen.FullReport.route(scores.assessmentId)) },
                        label = "View Full Report",
                        icon = Icons.AutoMirrored.Filled.Article,
                        modifier = Modifier
                            .fillMaxWidth()
                            .entrance(4),
                    )

                    SecondaryButton(
                        onClick = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        },
                        label = "Back to Home",
                        modifier = Modifier
                            .fillMaxWidth()
                            .entrance(5),
                    )

                    Spacer(Modifier.height(8.dp))
                }
                }
            }
        }
    }
}

@Composable
private fun SssScoreBreakdown(scores: ScoresRecordEntity) {
    val rows = listOf(
        "VAS Points"         to "${scores.vasPoints}",
        "Radiculopathy"      to "${scores.radiculopathyScore} pts",
        "ODI Points"         to "${scores.odiPoints}  (raw: ${scores.odiScore}/10)",
        "BMI Points"         to "${scores.bmiPoints}",
        "Chronicity Points"  to "${scores.chronicityPoints}",
        "Red Flag Override"  to if (scores.redFlagScore > 0) "Yes — 11" else "No",
    )
    Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
        rows.forEach { (label, value) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    label,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    value,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = if (label == "Red Flag Override" && scores.redFlagScore > 0)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}
