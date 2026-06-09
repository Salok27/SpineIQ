package noshtek.back_pain_prototype.ui.results

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import noshtek.back_pain_prototype.core.data.db.dao.FullAssessmentData
import noshtek.back_pain_prototype.core.data.db.entity.ScoresRecordEntity
import noshtek.back_pain_prototype.ui.common.CompositeBadge
import noshtek.back_pain_prototype.ui.common.SectionCard
import noshtek.back_pain_prototype.ui.common.SssTierBadge
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullReportScreen(
    navController: NavController,
    viewModel: ResultsViewModel = hiltViewModel(),
    pdfViewModel: PdfExportViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val pdfState by pdfViewModel.state.collectAsStateWithLifecycle()

    val shareLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        pdfViewModel.clearShareUri()
    }

    LaunchedEffect(pdfState.shareUri) {
        pdfState.shareUri?.let { uri ->
            val intent = pdfViewModel.buildShareIntent(uri)
            shareLauncher.launch(Intent.createChooser(intent, "Share PDF"))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Full Report", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
        }
    ) { padding ->
        when {
            state.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            state.scores == null || state.fullData == null -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Report unavailable.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            else -> {
                ReportContent(
                    modifier = Modifier.padding(padding),
                    scores = state.scores!!,
                    fullData = state.fullData!!,
                    isExporting = pdfState.isGenerating,
                    exportError = pdfState.error,
                    onExport = pdfViewModel::generateAndShare,
                )
            }
        }
    }
}

@Composable
private fun ReportContent(
    modifier: Modifier,
    scores: ScoresRecordEntity,
    fullData: FullAssessmentData,
    isExporting: Boolean,
    exportError: String?,
    onExport: () -> Unit,
) {
    val dateStr = try {
        LocalDate.ofEpochDay(fullData.record.assessmentDate)
            .format(DateTimeFormatter.ofPattern("d MMMM yyyy"))
    } catch (e: Exception) { "—" }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Header card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "SpineIQ Assessment Report",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                Text(
                    dateStr,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f),
                )
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SssTierBadge(scores.sssSeverityTier)
                    CompositeBadge(scores.backPainRiskClassification)
                }
            }
        }

        // Scores summary
        SectionCard(title = "Scores Summary") {
            ReportRow("SSS Total", "${scores.totalSSSScore}", bold = true)
            ReportRow("BMI", "${"%.1f".format(scores.bmiScore)} (${scores.bmiCategory.name})")
            ReportRow("VAS (input)", "${scores.vasScore}/10")
            ReportRow("ODI Raw", "${scores.odiScore}/10")
            ReportRow("Radiculopathy", "${scores.radiculopathyScore} pts")
            ReportRow("Red Flag Override", if (scores.redFlagScore > 0) "Yes" else "No")
            ReportRow("Lifestyle Risk", scores.lifestyleRiskTier.name.lowercase().replaceFirstChar { it.uppercase() })
            ReportRow(
                "Composite Classification",
                scores.backPainRiskClassification.name.replace('_', ' ')
                    .lowercase().replaceFirstChar { it.uppercase() },
                bold = true,
                isLast = true,
            )
        }

        // Occupation
        fullData.occupation?.let { occ ->
            SectionCard(title = "Occupation") {
                ReportRow("Type", occ.occupationType.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() })
                ReportRow("Sitting", "${"%.0f".format(occ.sittingHoursPerDay)} hrs/day")
                ReportRow("Standing", "${"%.0f".format(occ.standingHoursPerDay)} hrs/day")
                ReportRow("Driving", "${"%.0f".format(occ.drivingHoursPerDay)} hrs/day")
                ReportRow("Lifting", occ.liftingLevel.name.lowercase().replaceFirstChar { it.uppercase() }, isLast = occ.workPatternNotes == null)
                occ.workPatternNotes?.let { ReportRow("Notes", it, isLast = true) }
            }
        }

        // Lifestyle
        fullData.lifestyle?.let { ls ->
            SectionCard(title = "Lifestyle") {
                ReportRow("Sleep Duration", "${"%.0f".format(ls.sleepHoursPerNight)} hrs")
                ReportRow("Sleep Quality", ls.sleepQuality.name.lowercase().replaceFirstChar { it.uppercase() })
                ReportRow("Walking", "${"%.0f".format(ls.walkingMinutesPerDay)} min/day")
                ReportRow("Exercise Days", "${ls.exerciseDaysPerWeek} days/wk")
                ReportRow("Exercise Types", ls.exerciseTypes.joinToString { it.name.replace('_', ' ').lowercase() }, isLast = true)
            }
        }

        // Pain
        fullData.pain?.let { pain ->
            SectionCard(title = "Pain") {
                ReportRow("Locations", pain.painLocations.joinToString { it.name.replace('_', ' ').lowercase() })
                ReportRow("VAS", "${pain.vasScore}/10")
                ReportRow("Duration", pain.painDuration.name.lowercase().replaceFirstChar { it.uppercase() })
                ReportRow("Pattern", pain.painPattern.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() })
                ReportRow("Triggers", pain.painTriggers.joinToString { it.name.replace('_', ' ').lowercase() }.ifBlank { "—" })
                ReportRow("Radiculopathy", pain.radiculopathySeverity.name.lowercase().replaceFirstChar { it.uppercase() })
                pain.radiationLocation?.let { ReportRow("Radiation Side", it.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() }) }
                pain.functionalLimitationSeverity?.let { sev ->
                    ReportRow("Functional Limitation Severity", sev.name.lowercase().replaceFirstChar { it.uppercase() })
                }
                pain.functionalLimitationsText?.let { if (it.isNotBlank()) ReportRow("Notes", it, isLast = true) }
            }
        }

        // Functional
        fullData.functional?.let { func ->
            SectionCard(title = "Functional (Modified ODI)") {
                ReportRow("Walking", func.walking.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() })
                ReportRow("Sitting", func.sitting.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() })
                ReportRow("Standing", func.standing.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() })
                ReportRow("Sleep", func.sleep.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() })
                ReportRow("Daily Activities", func.dailyActivities.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() }, isLast = true)
            }
        }

        // Red flags
        fullData.redFlags?.let { rf ->
            SectionCard(title = "Red Flags") {
                if (!rf.hasAnyRedFlag) {
                    Text(
                        "No red flags confirmed.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    listOf(
                        "History of cancer" to rf.historyCancer,
                        "Unexplained weight loss" to rf.unexplainedWeightLoss,
                        "Fever / infection" to rf.feverOrInfection,
                        "Recent major trauma" to rf.recentMajorTrauma,
                        "Bowel / bladder dysfunction" to rf.bowelBladderDysfunction,
                        "Saddle anaesthesia" to rf.saddleAnaesthesia,
                        "Progressive neurological deficit" to rf.progressiveNeurologicalDeficit,
                        "Other pathology suspicion" to rf.otherSeriousPathologySuspicion,
                    ).filter { (_, v) -> v }.forEach { (label, _) ->
                        Text(
                            "• $label",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            }
        }

        if (exportError != null) {
            Text(
                "PDF error: $exportError",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }

        Button(
            onClick = onExport,
            enabled = !isExporting,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            if (isExporting) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                Spacer(Modifier.width(8.dp))
            }
            Text(
                if (isExporting) "Generating PDF…" else "Export PDF",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
            )
        }

        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun ReportRow(
    label: String,
    value: String,
    bold: Boolean = false,
    isLast: Boolean = false,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (bold) FontWeight.SemiBold else FontWeight.Normal,
                modifier = Modifier.weight(1.2f),
                textAlign = TextAlign.End,
            )
        }
        if (!isLast) {
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        }
    }
}
