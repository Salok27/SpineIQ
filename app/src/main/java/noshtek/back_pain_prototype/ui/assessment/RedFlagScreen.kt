package noshtek.back_pain_prototype.ui.assessment

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import noshtek.back_pain_prototype.navigation.Screen
import noshtek.back_pain_prototype.ui.common.*
import noshtek.back_pain_prototype.ui.theme.CardShape

@Composable
fun RedFlagScreen(
    navController: NavController,
    viewModel: AssessmentSessionViewModel
) {
    val session by viewModel.session.collectAsStateWithLifecycle()
    val flags = session.redFlags

    val items = listOf(
        Triple("History of cancer", flags.historyCancer) { v: Boolean -> viewModel.updateRedFlags { copy(historyCancer = v) } },
        Triple("Unexplained weight loss", flags.unexplainedWeightLoss) { v: Boolean -> viewModel.updateRedFlags { copy(unexplainedWeightLoss = v) } },
        Triple("Fever or suspected infection", flags.feverOrInfection) { v: Boolean -> viewModel.updateRedFlags { copy(feverOrInfection = v) } },
        Triple("Recent major trauma", flags.recentMajorTrauma) { v: Boolean -> viewModel.updateRedFlags { copy(recentMajorTrauma = v) } },
        Triple("Bowel / bladder dysfunction", flags.bowelBladderDysfunction) { v: Boolean -> viewModel.updateRedFlags { copy(bowelBladderDysfunction = v) } },
        Triple("Saddle anaesthesia", flags.saddleAnaesthesia) { v: Boolean -> viewModel.updateRedFlags { copy(saddleAnaesthesia = v) } },
        Triple("Progressive neurological deficit", flags.progressiveNeurologicalDeficit) { v: Boolean -> viewModel.updateRedFlags { copy(progressiveNeurologicalDeficit = v) } },
        Triple("Other serious pathology suspicion", flags.otherSeriousPathologySuspicion) { v: Boolean -> viewModel.updateRedFlags { copy(otherSeriousPathologySuspicion = v) } }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        SpineIQTopBar(
            title = "Red Flags  (5 / 6)",
            onBack = { navController.popBackStack() }
        )
        WizardProgressBar(currentStep = 5, totalSteps = 6)

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AssessmentVideoPlaceholder(
                title = "Red Flags Screening Overview",
                description = "This video will explain important warning signs that may require medical attention.",
            )

            Text(
                "Check any red flag signs or symptoms present. A confirmed red flag overrides the SSS score to Severe / Urgent.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(4.dp))

            items.forEach { (label, checked, onToggle) ->
                RedFlagRow(label = label, checked = checked, onToggle = onToggle)
            }

            AnimatedVisibility(
                visible = flags.hasAnyRedFlag,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
            ) {
                AppCard(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    border = false,
                    modifier = Modifier.padding(top = 4.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Icon(
                            Icons.Filled.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            "Red flag confirmed. SSS score will be overridden to 11 (Severe / Urgent). Refer immediately.",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            NextButton(
                label = "Review & Complete",
                onClick = {
                    viewModel.persistRedFlags()
                    navController.navigate(Screen.Review.route)
                }
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun RedFlagRow(label: String, checked: Boolean, onToggle: (Boolean) -> Unit) {
    val bg by animateColorAsState(
        if (checked) MaterialTheme.colorScheme.error.copy(alpha = 0.10f) else MaterialTheme.colorScheme.surface,
        label = "rf-bg",
    )
    val borderColor by animateColorAsState(
        if (checked) MaterialTheme.colorScheme.error.copy(alpha = 0.45f) else MaterialTheme.colorScheme.outlineVariant,
        label = "rf-border",
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(bg)
            .border(1.dp, borderColor, CardShape)
            .clickable { onToggle(!checked) }
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onToggle,
            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.error),
        )
        Spacer(Modifier.width(8.dp))
        Text(
            label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (checked) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.weight(1f),
        )
    }
}
