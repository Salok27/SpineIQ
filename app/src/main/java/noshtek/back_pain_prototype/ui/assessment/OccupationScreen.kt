package noshtek.back_pain_prototype.ui.assessment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import noshtek.back_pain_prototype.core.data.model.LiftingLevel
import noshtek.back_pain_prototype.core.data.model.OccupationType
import noshtek.back_pain_prototype.navigation.Screen
import noshtek.back_pain_prototype.ui.common.*

@Composable
fun OccupationScreen(
    navController: NavController,
    viewModel: AssessmentSessionViewModel
) {
    val session by viewModel.session.collectAsStateWithLifecycle()
    var showError by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.initSession()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        SpineIQTopBar(
            title = "Occupation  (1 / 6)",
            onBack = { navController.popBackStack() }
        )

        if (session.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Column
        }

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Hi ${session.userName}, let's start with your work pattern.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            SectionCard(title = "Occupation Type") {
                // FlowRow so each chip sizes to its label and wraps to the next
                // line — "Office Worker" / "Manual Labor" no longer truncate.
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OccupationType.entries.forEach { type ->
                        FilterChip(
                            selected = session.occupation.occupationType == type,
                            onClick = { viewModel.updateOccupation { copy(occupationType = type) } },
                            label = { Text(type.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() }) }
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                RequiredFieldError(show = showError && session.occupation.occupationType == null)
            }

            SectionCard(title = "Daily Hours") {
                SliderWithLabel(
                    label = "Sitting",
                    value = session.occupation.sittingHoursPerDay,
                    onValueChange = { viewModel.updateOccupation { copy(sittingHoursPerDay = it) } },
                    valueRange = 0f..16f,
                    unit = "hrs"
                )
                Spacer(Modifier.height(8.dp))
                SliderWithLabel(
                    label = "Standing",
                    value = session.occupation.standingHoursPerDay,
                    onValueChange = { viewModel.updateOccupation { copy(standingHoursPerDay = it) } },
                    valueRange = 0f..16f,
                    unit = "hrs"
                )
                Spacer(Modifier.height(8.dp))
                SliderWithLabel(
                    label = "Driving",
                    value = session.occupation.drivingHoursPerDay,
                    onValueChange = { viewModel.updateOccupation { copy(drivingHoursPerDay = it) } },
                    valueRange = 0f..12f,
                    unit = "hrs"
                )
            }

            SectionCard(title = "Lifting Level") {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LiftingLevel.entries.forEach { level ->
                        FilterChip(
                            selected = session.occupation.liftingLevel == level,
                            onClick = { viewModel.updateOccupation { copy(liftingLevel = level) } },
                            label = { Text(level.name.lowercase().replaceFirstChar { it.uppercase() }) }
                        )
                    }
                }
            }

            SectionCard(title = "Notes (optional)") {
                OutlinedTextField(
                    value = session.occupation.workPatternNotes,
                    onValueChange = { viewModel.updateOccupation { copy(workPatternNotes = it) } },
                    placeholder = { Text("Work pattern notes…") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }

            NextButton(
                label = "Next: Lifestyle",
                onClick = {
                    if (session.occupation.occupationType == null) {
                        showError = true
                    } else {
                        viewModel.persistOccupation()
                        navController.navigate(Screen.Lifestyle.route)
                    }
                }
            )

            Spacer(Modifier.height(8.dp))
        }
    }
}
