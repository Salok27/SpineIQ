package noshtek.back_pain_prototype.ui.results

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
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
import noshtek.back_pain_prototype.ui.common.CompositeBadge
import noshtek.back_pain_prototype.ui.common.LifestyleTierBadge
import noshtek.back_pain_prototype.ui.common.RiskTileSmall
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
                title = { Text("Assessment Results") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        when {
            state.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            state.scores == null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Composite result — most prominent
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Back Pain Risk Classification",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(12.dp))
                            CompositeBadge(scores.backPainRiskClassification)
                        }
                    }

                    // SSS tier
                    SectionCard(title = "Spine Severity System (SSS)") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Total Score: ${scores.totalSSSScore}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            SssTierBadge(scores.sssSeverityTier)
                        }
                        Spacer(Modifier.height(8.dp))
                        SssScoreBreakdown(scores)
                    }

                    // Lifestyle tier
                    SectionCard(title = "Lifestyle Risk") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Lifestyle Tier", style = MaterialTheme.typography.bodyLarge)
                            LifestyleTierBadge(scores.lifestyleRiskTier)
                        }
                        Spacer(Modifier.height(8.dp))
                        RiskTileSmall("Sitting Risk", scores.sittingRisk)
                        Spacer(Modifier.height(4.dp))
                        RiskTileSmall("Walking Risk", scores.walkingRisk)
                        Spacer(Modifier.height(4.dp))
                        RiskTileSmall("Exercise Risk", scores.exerciseRisk)
                        Spacer(Modifier.height(4.dp))
                        RiskTileSmall("Sleep Risk", scores.sleepRisk)
                    }

                    // BMI
                    SectionCard(title = "BMI") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("BMI", style = MaterialTheme.typography.bodyMedium)
                                Text(scores.bmiCategory.name.lowercase().replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.bodySmall)
                            }
                            Text("${"%.1f".format(scores.bmiScore)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            navController.navigate(Screen.FullReport.route(scores.assessmentId))
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("View Full Report")
                    }

                    OutlinedButton(
                        onClick = {
                            navController.navigate(Screen.PatientList.route) {
                                popUpTo(Screen.Home.route)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Back to Patients")
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
        "VAS Points" to "${scores.vasPoints}",
        "Radiculopathy Points" to "${scores.radiculopathyScore}",
        "ODI Points" to "${scores.odiPoints}  (raw: ${scores.odiScore}/10)",
        "BMI Points" to "${scores.bmiPoints}",
        "Chronicity Points" to "${scores.chronicityPoints}",
        "Red Flag Score" to if (scores.redFlagScore > 0) "11 (override)" else "0"
    )
    rows.forEach { (label, value) ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
        }
    }
}
