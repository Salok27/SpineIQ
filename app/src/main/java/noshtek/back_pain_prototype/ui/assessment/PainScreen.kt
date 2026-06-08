package noshtek.back_pain_prototype.ui.assessment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import noshtek.back_pain_prototype.core.data.model.*
import noshtek.back_pain_prototype.core.scoring.model.PainDuration
import noshtek.back_pain_prototype.core.scoring.model.RadiculopathySeverity
import noshtek.back_pain_prototype.navigation.Screen
import noshtek.back_pain_prototype.ui.common.*

@Composable
fun PainScreen(
    navController: NavController,
    viewModel: AssessmentSessionViewModel
) {
    val session by viewModel.session.collectAsStateWithLifecycle()
    val pain = session.pain
    var showError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        SpineIQTopBar(
            title = "Pain Assessment  (3 / 6)",
            onBack = { navController.popBackStack() }
        )

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Pain locations
            SectionCard(title = "Pain Locations") {
                Text("Select all that apply", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(8.dp))
                PainLocation.entries.chunked(2).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        row.forEach { loc ->
                            val selected = loc in pain.painLocations
                            FilterChip(
                                selected = selected,
                                onClick = {
                                    viewModel.updatePain {
                                        copy(painLocations = if (selected) painLocations - loc else painLocations + loc)
                                    }
                                },
                                label = {
                                    Text(
                                        loc.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() },
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (row.size == 1) Spacer(Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(4.dp))
                }
                RequiredFieldError(show = showError && pain.painLocations.isEmpty(), message = "Select at least one location")
            }

            // VAS
            SectionCard(title = "Pain Intensity (VAS)") {
                SliderWithLabel(
                    label = "0 = No pain  ·  10 = Worst pain",
                    value = pain.vasScore.toFloat(),
                    onValueChange = { viewModel.updatePain { copy(vasScore = it.toInt()) } },
                    valueRange = 0f..10f,
                    steps = 9
                )
                Text(
                    "Current: ${pain.vasScore}/10",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Duration & pattern
            SectionCard(title = "Pain Duration") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PainDuration.entries.forEach { dur ->
                        FilterChip(
                            selected = pain.painDuration == dur,
                            onClick = { viewModel.updatePain { copy(painDuration = dur) } },
                            label = {
                                Text(
                                    when (dur) {
                                        PainDuration.ACUTE    -> "Acute\n(<3 wks)"
                                        PainDuration.SUBACUTE -> "Subacute\n(3–6 wks)"
                                        PainDuration.CHRONIC  -> "Chronic\n(>6 wks)"
                                    },
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            SectionCard(title = "Pain Pattern") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    PainPattern.entries.forEach { pattern ->
                        FilterChip(
                            selected = pain.painPattern == pattern,
                            onClick = { viewModel.updatePain { copy(painPattern = pattern) } },
                            label = {
                                Text(
                                    pattern.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() },
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Triggers
            SectionCard(title = "Pain Triggers") {
                Text("Select all that apply", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(8.dp))
                PainTrigger.entries.chunked(2).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        row.forEach { trigger ->
                            val selected = trigger in pain.painTriggers
                            FilterChip(
                                selected = selected,
                                onClick = {
                                    viewModel.updatePain {
                                        copy(painTriggers = if (selected) painTriggers - trigger else painTriggers + trigger)
                                    }
                                },
                                label = {
                                    Text(
                                        trigger.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() },
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (row.size == 1) Spacer(Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(4.dp))
                }
            }

            // Radiculopathy
            SectionCard(title = "Radiculopathy / Leg Pain") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    RadiculopathySeverity.entries.forEach { sev ->
                        FilterChip(
                            selected = pain.radiculopathySeverity == sev,
                            onClick = { viewModel.updatePain { copy(radiculopathySeverity = sev, radiationLocation = if (sev == RadiculopathySeverity.NONE) null else radiationLocation) } },
                            label = {
                                Text(
                                    sev.name.lowercase().replaceFirstChar { it.uppercase() },
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                if (pain.radiculopathySeverity != RadiculopathySeverity.NONE) {
                    Spacer(Modifier.height(8.dp))
                    Text("Radiation Side", style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        RadiationLocation.entries.forEach { loc ->
                            FilterChip(
                                selected = pain.radiationLocation == loc,
                                onClick = { viewModel.updatePain { copy(radiationLocation = loc) } },
                                label = {
                                    Text(
                                        loc.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() },
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Functional limitation severity
            SectionCard(title = "Functional Limitation Severity") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FunctionalLimitationSeverity.entries.forEach { sev ->
                        FilterChip(
                            selected = pain.functionalLimitationSeverity == sev,
                            onClick = { viewModel.updatePain { copy(functionalLimitationSeverity = sev) } },
                            label = {
                                Text(
                                    sev.name.lowercase().replaceFirstChar { it.uppercase() },
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = pain.functionalLimitationsText,
                    onValueChange = { viewModel.updatePain { copy(functionalLimitationsText = it) } },
                    placeholder = { Text("Describe limitations (optional)…") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }

            NextButton(
                label = "Next: Functional",
                onClick = {
                    if (pain.painLocations.isEmpty()) {
                        showError = true
                    } else {
                        viewModel.persistPain()
                        navController.navigate(Screen.Functional.route)
                    }
                }
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}
