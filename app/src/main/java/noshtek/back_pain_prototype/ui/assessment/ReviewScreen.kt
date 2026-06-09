package noshtek.back_pain_prototype.ui.assessment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import noshtek.back_pain_prototype.navigation.Screen
import noshtek.back_pain_prototype.ui.common.*

@Composable
fun ReviewScreen(
    navController: NavController,
    viewModel: AssessmentSessionViewModel
) {
    val session by viewModel.session.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        SpineIQTopBar(
            title = "Review  (6 / 6)",
            onBack = { navController.popBackStack() }
        )

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Review the information below before computing the score.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Personal
            SectionCard(title = "Personal") {
                ReviewRow("Name", session.userName)
                ReviewRow("Age", "${session.userAgeYears} yrs")
                ReviewRow("Weight / Height", "${"%.1f".format(session.userWeightKg)} kg · ${"%.0f".format(session.userHeightCm)} cm")
            }

            // Occupation
            SectionCard(title = "Occupation") {
                ReviewRow("Type", session.occupation.occupationType?.name?.replace('_', ' ') ?: "—")
                ReviewRow("Sitting", "${"%.0f".format(session.occupation.sittingHoursPerDay)} hrs/day")
                ReviewRow("Lifting", session.occupation.liftingLevel.name.lowercase().replaceFirstChar { it.uppercase() })
            }

            // Lifestyle
            SectionCard(title = "Lifestyle") {
                ReviewRow("Sleep", "${"%.0f".format(session.lifestyle.sleepHoursPerNight)} hrs · ${session.lifestyle.sleepQuality.name}")
                ReviewRow("Walking", "${"%.0f".format(session.lifestyle.walkingMinutesPerDay)} min/day")
                ReviewRow("Exercise", "${session.lifestyle.exerciseDaysPerWeek} days/wk")
            }

            // Pain
            SectionCard(title = "Pain") {
                ReviewRow("Locations", session.pain.painLocations.joinToString { it.name.replace('_', ' ').lowercase() }.ifBlank { "—" })
                ReviewRow("VAS Score", "${session.pain.vasScore} / 10")
                ReviewRow("Duration", session.pain.painDuration.name.lowercase().replaceFirstChar { it.uppercase() })
                ReviewRow("Radiculopathy", session.pain.radiculopathySeverity.name.lowercase().replaceFirstChar { it.uppercase() })
            }

            // Functional
            SectionCard(title = "Functional (Modified ODI)") {
                ReviewRow("Walking", session.functional.walking.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() })
                ReviewRow("Sitting", session.functional.sitting.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() })
                ReviewRow("Standing", session.functional.standing.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() })
                ReviewRow("Sleep", session.functional.sleep.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() })
                ReviewRow("Daily Activities", session.functional.dailyActivities.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() })
                Spacer(Modifier.height(4.dp))
                ReviewRow("ODI Total", "${session.functional.odiTotal} / 10", bold = true)
            }

            // Red flags
            if (session.redFlags.hasAnyRedFlag) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Text(
                        "RED FLAG CONFIRMED — score will be Severe / Urgent",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                SectionCard(title = "Red Flags") {
                    ReviewRow("Status", "None confirmed")
                }
            }

            if (session.isScoring) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                val error = session.error
                if (error != null) {
                    Text(
                        error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Button(
                    onClick = {
                        viewModel.computeAndComplete { assessmentId ->
                            navController.navigate(Screen.Results.route(assessmentId)) {
                                popUpTo(Screen.AssessmentGraph.route) { inclusive = true }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Compute Score & Complete")
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ReviewRow(label: String, value: String, bold: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal
        )
    }
}
