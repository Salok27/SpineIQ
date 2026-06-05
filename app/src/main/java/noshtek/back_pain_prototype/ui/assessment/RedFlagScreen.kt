package noshtek.back_pain_prototype.ui.assessment

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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import noshtek.back_pain_prototype.navigation.Screen
import noshtek.back_pain_prototype.ui.common.*

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

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Check any red flag signs or symptoms present. A confirmed red flag overrides the SSS score to Severe / Urgent.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(4.dp))

            items.forEach { (label, checked, onToggle) ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = checked,
                            onCheckedChange = onToggle
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(label, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }

            if (flags.hasAnyRedFlag) {
                Spacer(Modifier.height(4.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
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
