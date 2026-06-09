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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import noshtek.back_pain_prototype.core.data.db.entity.ScoresRecordEntity
import noshtek.back_pain_prototype.navigation.Screen
import noshtek.back_pain_prototype.ui.common.LabelledScoreBar
import noshtek.back_pain_prototype.ui.common.LifestyleTierBadge
import noshtek.back_pain_prototype.ui.common.RiskTileSmall
import noshtek.back_pain_prototype.ui.common.ScoreHeroCard
import noshtek.back_pain_prototype.ui.common.SectionCard
import noshtek.back_pain_prototype.ui.common.SssTierBadge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsDashboardScreen(
    navController: NavController,
    viewModel: ResultsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Your Results", style = MaterialTheme.typography.titleLarge)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
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
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    // Hero card — composite result + SSS score
                    ScoreHeroCard(
                        scoreLabel = "SSS Score",
                        scoreValue = "${scores.totalSSSScore} / 11",
                        classification = scores.backPainRiskClassification,
                    )

                    // SSS breakdown
                    SectionCard(title = "Spine Severity System (SSS)") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                "Total Score",
                                style = MaterialTheme.typography.titleSmall,
                            )
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
                    SectionCard(title = "Lifestyle Risk") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("Overall Tier", style = MaterialTheme.typography.titleSmall)
                            LifestyleTierBadge(scores.lifestyleRiskTier)
                        }
                        Spacer(Modifier.height(12.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            RiskTileSmall("Sitting Risk", scores.sittingRisk)
                            RiskTileSmall("Walking Risk", scores.walkingRisk)
                            RiskTileSmall("Exercise Risk", scores.exerciseRisk)
                            RiskTileSmall("Sleep Risk", scores.sleepRisk)
                        }
                    }

                    // BMI
                    SectionCard(title = "BMI") {
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
                                "${"%.1f".format(scores.bmiScore)}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }

                    Button(
                        onClick = { navController.navigate(Screen.FullReport.route(scores.assessmentId)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Text(
                            "View Full Report",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text("Back to Home")
                    }

                    Spacer(Modifier.height(8.dp))
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
