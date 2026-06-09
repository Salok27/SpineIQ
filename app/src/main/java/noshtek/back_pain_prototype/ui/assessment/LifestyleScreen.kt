package noshtek.back_pain_prototype.ui.assessment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import noshtek.back_pain_prototype.core.scoring.model.ExerciseType
import noshtek.back_pain_prototype.core.scoring.model.SleepQuality
import noshtek.back_pain_prototype.navigation.Screen
import noshtek.back_pain_prototype.ui.common.*

@Composable
fun LifestyleScreen(
    navController: NavController,
    viewModel: AssessmentSessionViewModel
) {
    val session by viewModel.session.collectAsStateWithLifecycle()
    val lifestyle = session.lifestyle

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        SpineIQTopBar(
            title = "Lifestyle  (2 / 6)",
            onBack = { navController.popBackStack() }
        )
        WizardProgressBar(currentStep = 2, totalSteps = 6)

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SectionCard(title = "Sleep", icon = Icons.Filled.Bedtime, accent = MaterialTheme.colorScheme.tertiary) {
                SliderWithLabel(
                    label = "Sleep Duration",
                    value = lifestyle.sleepHoursPerNight,
                    onValueChange = { viewModel.updateLifestyle { copy(sleepHoursPerNight = it) } },
                    valueRange = 3f..12f,
                    unit = "hrs"
                )
                Spacer(Modifier.height(12.dp))
                Text("Sleep Quality", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(4.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SleepQuality.entries.forEach { quality ->
                        FilterChip(
                            selected = lifestyle.sleepQuality == quality,
                            onClick = { viewModel.updateLifestyle { copy(sleepQuality = quality) } },
                            label = { Text(quality.name.lowercase().replaceFirstChar { it.uppercase() }) }
                        )
                    }
                }
            }

            SectionCard(title = "Activity", icon = Icons.AutoMirrored.Filled.DirectionsRun, accent = MaterialTheme.colorScheme.secondary) {
                SliderWithLabel(
                    label = "Walking",
                    value = lifestyle.walkingMinutesPerDay,
                    onValueChange = { viewModel.updateLifestyle { copy(walkingMinutesPerDay = it) } },
                    valueRange = 0f..120f,
                    unit = "min/day"
                )
                Spacer(Modifier.height(12.dp))
                SliderWithLabel(
                    label = "Exercise Days",
                    value = lifestyle.exerciseDaysPerWeek.toFloat(),
                    onValueChange = { viewModel.updateLifestyle { copy(exerciseDaysPerWeek = it.toInt()) } },
                    valueRange = 0f..7f,
                    steps = 6,
                    unit = "days/wk"
                )
            }

            SectionCard(title = "Exercise Types", icon = Icons.Filled.FitnessCenter) {
                Text(
                    "Select all that apply",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ExerciseType.entries.forEach { type ->
                        val selected = type in lifestyle.exerciseTypes
                        FilterChip(
                            selected = selected,
                            onClick = {
                                viewModel.updateLifestyle {
                                    val updated = if (selected) exerciseTypes - type else exerciseTypes + type
                                    copy(exerciseTypes = updated)
                                }
                            },
                            label = { Text(type.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() }) }
                        )
                    }
                }
            }

            NextButton(
                label = "Next: Pain",
                onClick = {
                    viewModel.persistLifestyle()
                    navController.navigate(Screen.Pain.route)
                }
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}
