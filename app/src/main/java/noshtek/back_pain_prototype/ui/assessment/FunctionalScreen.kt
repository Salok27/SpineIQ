package noshtek.back_pain_prototype.ui.assessment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import noshtek.back_pain_prototype.core.scoring.model.FunctionalLevel
import noshtek.back_pain_prototype.navigation.Screen
import noshtek.back_pain_prototype.ui.common.*
import noshtek.back_pain_prototype.ui.gamification.JourneyProgressIndicator
import noshtek.back_pain_prototype.ui.gamification.StageCompleteOverlay

@Composable
fun FunctionalScreen(
    navController: NavController,
    viewModel: AssessmentSessionViewModel
) {
    val session by viewModel.session.collectAsStateWithLifecycle()
    val functional = session.functional
    var showStageComplete by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        SpineIQTopBar(
            title = "Functional",
            onBack = { navController.popBackStack() }
        )
        JourneyProgressIndicator(currentStep = 4)

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AssessmentVideoPlaceholder(
                title = "Functional Movement Demonstration",
                description = "This video will demonstrate the movements and activities used in the functional assessment.",
            )

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

            // Live ODI total on a brand-gradient card — the number animates as choices change.
            GradientHeroCard(contentPadding = PaddingValues(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text("Modified ODI Total", style = MaterialTheme.typography.titleSmall, color = Color.White, fontWeight = FontWeight.Bold)
                        Text(
                            "0 = full function · 10 = severe disability",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f),
                        )
                    }
                    Row(verticalAlignment = Alignment.Bottom) {
                        AnimatedCountText(
                            target = functional.odiTotal,
                            style = MaterialTheme.typography.displaySmall,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            durationMillis = MotionTokens.DurationFast,
                        )
                        Text(
                            " / 10",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.padding(bottom = 4.dp),
                        )
                    }
                }
            }

            NextButton(
                label = "Next: Red Flags",
                onClick = { showStageComplete = true }
            )
            Spacer(Modifier.height(8.dp))
        }
    }

    StageCompleteOverlay(
        visible = showStageComplete,
        stepLabel = "Functional check complete!",
        onFinished = {
            showStageComplete = false
            viewModel.persistFunctional()
            navController.navigate(Screen.RedFlag.route)
        },
    )
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
