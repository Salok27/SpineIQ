package noshtek.back_pain_prototype.ui.assessment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Work
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
import noshtek.back_pain_prototype.ui.gamification.JourneyProgressIndicator

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
            title = "Review",
            onBack = { navController.popBackStack() }
        )
        JourneyProgressIndicator(currentStep = 6)

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Review the information below before computing the score.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.entrance(0),
            )

            // Personal
            SectionCard(title = "Personal", icon = Icons.Filled.Person, modifier = Modifier.entrance(1)) {
                ReviewRow("Name", session.userName)
                ReviewRow("Age", "${session.userAgeYears} yrs")
                ReviewRow("Weight / Height", "${"%.1f".format(session.userWeightKg)} kg · ${"%.0f".format(session.userHeightCm)} cm")
            }

            // Occupation
            SectionCard(title = "Occupation", icon = Icons.Filled.Work, accent = MaterialTheme.colorScheme.secondary, modifier = Modifier.entrance(2)) {
                ReviewRow("Type", session.occupation.occupationType?.name?.replace('_', ' ') ?: "—")
                ReviewRow("Sitting", "${"%.0f".format(session.occupation.sittingHoursPerDay)} hrs/day")
                ReviewRow("Lifting", session.occupation.liftingLevel.name.lowercase().replaceFirstChar { it.uppercase() })
            }

            // Lifestyle
            SectionCard(title = "Lifestyle", icon = Icons.AutoMirrored.Filled.DirectionsWalk, accent = MaterialTheme.colorScheme.secondary, modifier = Modifier.entrance(3)) {
                ReviewRow("Sleep", "${"%.0f".format(session.lifestyle.sleepHoursPerNight)} hrs · ${session.lifestyle.sleepQuality.name}")
                ReviewRow("Walking", "${"%.0f".format(session.lifestyle.walkingMinutesPerDay)} min/day")
                ReviewRow("Exercise", "${session.lifestyle.exerciseDaysPerWeek} days/wk")
            }

            // Pain
            SectionCard(title = "Pain", icon = Icons.Filled.Healing, accent = MaterialTheme.colorScheme.tertiary, modifier = Modifier.entrance(4)) {
                ReviewRow("Locations", session.pain.painLocations.joinToString { it.name.replace('_', ' ').lowercase() }.ifBlank { "—" })
                ReviewRow("VAS Score", "${session.pain.vasScore} / 10")
                ReviewRow("Duration", session.pain.painDuration.name.lowercase().replaceFirstChar { it.uppercase() })
                ReviewRow("Radiculopathy", session.pain.radiculopathySeverity.name.lowercase().replaceFirstChar { it.uppercase() })
            }

            // Functional
            SectionCard(title = "Functional (Modified ODI)", icon = Icons.Filled.Accessibility, modifier = Modifier.entrance(5)) {
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
                AppCard(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    border = false,
                    modifier = Modifier.entrance(6),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Icon(Icons.Filled.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Text(
                            "RED FLAG CONFIRMED — score will be Severe / Urgent",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            } else {
                SectionCard(title = "Red Flags", icon = Icons.Filled.Warning, modifier = Modifier.entrance(6)) {
                    ReviewRow("Status", "None confirmed")
                }
            }

            val error = session.error
            if (error != null) {
                Text(
                    error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            NextButton(
                label = "Compute Score & Complete",
                loading = session.isScoring,
                onClick = {
                    viewModel.computeAndComplete { assessmentId ->
                        navController.navigate(Screen.Results.route(assessmentId, celebrate = true)) {
                            popUpTo(Screen.AssessmentGraph.route) { inclusive = true }
                        }
                    }
                },
            )

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
