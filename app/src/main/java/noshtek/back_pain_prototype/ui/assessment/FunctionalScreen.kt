package noshtek.back_pain_prototype.ui.assessment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import noshtek.back_pain_prototype.core.scoring.model.FunctionalLevel
import noshtek.back_pain_prototype.navigation.Screen
import noshtek.back_pain_prototype.ui.common.*

@Composable
fun FunctionalScreen(
    navController: NavController,
    viewModel: AssessmentSessionViewModel
) {
    val session by viewModel.session.collectAsStateWithLifecycle()
    val functional = session.functional

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        SpineIQTopBar(
            title = "Functional  (4 / 6)",
            onBack = { navController.popBackStack() }
        )
        WizardProgressBar(currentStep = 4, totalSteps = 6)

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Rate your ability to perform each activity.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            FunctionalRow(
                label = "Walking",
                selected = functional.walking,
                onSelect = { viewModel.updateFunctional { copy(walking = it) } }
            )
            FunctionalRow(
                label = "Sitting",
                selected = functional.sitting,
                onSelect = { viewModel.updateFunctional { copy(sitting = it) } }
            )
            FunctionalRow(
                label = "Standing",
                selected = functional.standing,
                onSelect = { viewModel.updateFunctional { copy(standing = it) } }
            )
            FunctionalRow(
                label = "Sleep",
                selected = functional.sleep,
                onSelect = { viewModel.updateFunctional { copy(sleep = it) } }
            )
            FunctionalRow(
                label = "Daily Activities",
                selected = functional.dailyActivities,
                onSelect = { viewModel.updateFunctional { copy(dailyActivities = it) } }
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Modified ODI Total", style = MaterialTheme.typography.labelLarge)
                        Text("0 = full function  ·  10 = severe disability", style = MaterialTheme.typography.bodySmall)
                    }
                    Text(
                        "${functional.odiTotal} / 10",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            NextButton(
                label = "Next: Red Flags",
                onClick = {
                    viewModel.persistFunctional()
                    navController.navigate(Screen.RedFlag.route)
                }
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun FunctionalRow(
    label: String,
    selected: FunctionalLevel,
    onSelect: (FunctionalLevel) -> Unit
) {
    SectionCard(title = label) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FunctionalLevel.entries.forEach { level ->
                FilterChip(
                    selected = selected == level,
                    onClick = { onSelect(level) },
                    label = {
                        Text(
                            when (level) {
                                FunctionalLevel.NORMAL -> "Normal"
                                FunctionalLevel.MILD_DIFFICULTY -> "Mild"
                                FunctionalLevel.SEVERE_DIFFICULTY -> "Severe"
                            }
                        )
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
