package noshtek.back_pain_prototype.ui.patient

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import noshtek.back_pain_prototype.core.data.db.entity.AssessmentRecordEntity
import noshtek.back_pain_prototype.core.data.db.entity.ScoresRecordEntity
import noshtek.back_pain_prototype.core.data.model.AssessmentStatus
import noshtek.back_pain_prototype.navigation.Screen
import noshtek.back_pain_prototype.ui.common.CompositeBadge
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientHistoryScreen(
    navController: NavController,
    patientId: String,
    viewModel: PatientHistoryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Assessment History")
                        if (state.patientName.isNotEmpty()) {
                            Text(state.patientName, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        val completed = state.assessments.filter { it.status == AssessmentStatus.COMPLETED }

        if (completed.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No completed assessments yet.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(completed, key = { it.id }) { record ->
                    AssessmentRow(
                        record = record,
                        scores = state.scoresMap[record.id],
                        onClick = { navController.navigate(Screen.Results.route(record.id)) }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun AssessmentRow(
    record: AssessmentRecordEntity,
    scores: ScoresRecordEntity?,
    onClick: () -> Unit
) {
    val dateStr = try {
        LocalDate.ofEpochDay(record.assessmentDate)
            .format(DateTimeFormatter.ofPattern("d MMM yyyy"))
    } catch (e: Exception) { "—" }

    ListItem(
        headlineContent = {
            Text(dateStr, fontWeight = FontWeight.Medium)
        },
        supportingContent = scores?.let {
            {
                Text("SSS: ${it.totalSSSScore} · BMI: ${"%.1f".format(it.bmiScore)}")
            }
        },
        trailingContent = scores?.let {
            { CompositeBadge(it.backPainRiskClassification) }
        },
        modifier = Modifier.clickable(onClick = onClick)
    )
}
