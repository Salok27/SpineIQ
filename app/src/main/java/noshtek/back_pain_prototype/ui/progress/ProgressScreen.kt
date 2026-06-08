package noshtek.back_pain_prototype.ui.progress

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import noshtek.back_pain_prototype.core.scoring.model.RiskTier
import noshtek.back_pain_prototype.navigation.Screen
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    navController: NavController,
    viewModel: ProgressViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val fmt = DateTimeFormatter.ofPattern("d MMM yy")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Progress") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        if (!state.hasEnoughData) {
            Box(modifier = Modifier.fillMaxSize().padding(padding).padding(32.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Not enough data yet", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        "Complete at least 2 assessments to see your progress trends here.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = {
                        navController.navigate(Screen.AssessmentGraph.route)
                    }) { Text("Start Assessment") }
                }
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Delta callout
            state.latestScoreDelta?.let { delta ->
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = if (delta >= 0) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            val arrow = if (delta > 0) "↓" else if (delta < 0) "↑" else "→"
                            val label = when {
                                delta > 0 -> "Your SSS score improved by $delta point${if (delta != 1) "s" else ""}"
                                delta < 0 -> "Your SSS score increased by ${-delta} point${if (-delta != 1) "s" else ""}"
                                else -> "Your SSS score is unchanged"
                            }
                            Text("$arrow $label", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            state.previousAssessmentDate?.let { prev ->
                                Text(
                                    "since your last check-in on ${prev.format(fmt)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // SSS trend chart
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("SSS Score Over Time", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        val scores = state.assessments.map { it.scores.totalSSSScore.toFloat() }
                        val dates = state.assessments.map { LocalDate.ofEpochDay(it.record.assessmentDate).format(fmt) }
                        SimpleLineChart(values = scores, labels = dates, maxY = 11f, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            // Lifestyle risk trend chart
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Lifestyle Risk Over Time", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        val tiers = state.assessments.map { summary ->
                            when (summary.scores.lifestyleRiskTier) {
                                RiskTier.LOW -> 1f
                                RiskTier.MODERATE -> 2f
                                else -> 3f
                            }
                        }
                        val dates = state.assessments.map { LocalDate.ofEpochDay(it.record.assessmentDate).format(fmt) }
                        SimpleLineChart(values = tiers, labels = dates, maxY = 3f, color = MaterialTheme.colorScheme.tertiary)
                        Spacer(Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            listOf("1=Low", "2=Moderate", "3=High").forEach {
                                Text(it, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            // Component trend cards
            if (state.assessments.size >= 2) {
                item {
                    val latest = state.assessments.last().scores
                    val prev = state.assessments[state.assessments.size - 2].scores
                    Text("Habit Trends", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(
                            Triple("Sitting", prev.sittingRisk, latest.sittingRisk),
                            Triple("Walking", prev.walkingRisk, latest.walkingRisk),
                            Triple("Exercise", prev.exerciseRisk, latest.exerciseRisk),
                            Triple("Sleep", prev.sleepRisk, latest.sleepRisk)
                        ).forEach { (label, prevTier, currTier) ->
                            ComponentTrendCard(label = label, previous = prevTier, current = currTier, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            // Assessment history list
            item {
                Text("Assessment History", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            }
            items(state.assessments.reversed()) { summary ->
                AssessmentHistoryRow(
                    summary = summary,
                    fmt = fmt,
                    onClick = { navController.navigate(Screen.FullReport.route(summary.record.id)) }
                )
            }
        }
    }
}

@Composable
private fun SimpleLineChart(values: List<Float>, labels: List<String>, maxY: Float, color: Color) {
    if (values.size < 2) return
    val primaryColor = color
    Box(modifier = Modifier.fillMaxWidth().height(120.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val stepX = w / (values.size - 1).toFloat()
            val points = values.mapIndexed { i, v ->
                Offset(i * stepX, h - (v / maxY) * h)
            }
            for (i in 0 until points.size - 1) {
                drawLine(primaryColor, points[i], points[i + 1], strokeWidth = 3f)
            }
            points.forEach { p ->
                drawCircle(primaryColor, radius = 5f, center = p)
            }
        }
    }
}

@Composable
private fun ComponentTrendCard(label: String, previous: RiskTier, current: RiskTier, modifier: Modifier = Modifier) {
    val improved = tierOrdinal(current) < tierOrdinal(previous)
    val worsened = tierOrdinal(current) > tierOrdinal(previous)
    val arrow = if (improved) "↓" else if (worsened) "↑" else "→"
    val containerColor = when {
        improved -> MaterialTheme.colorScheme.primaryContainer
        worsened -> MaterialTheme.colorScheme.errorContainer
        else     -> MaterialTheme.colorScheme.surfaceVariant
    }
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = containerColor)) {
        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            Text(arrow, style = MaterialTheme.typography.titleLarge)
            Text(current.name.lowercase().replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.labelSmall)
        }
    }
}

private fun tierOrdinal(tier: RiskTier) = when (tier) { RiskTier.LOW -> 0; RiskTier.MODERATE -> 1; else -> 2 }

@Composable
private fun AssessmentHistoryRow(summary: AssessmentSummary, fmt: DateTimeFormatter, onClick: () -> Unit) {
    val date = LocalDate.ofEpochDay(summary.record.assessmentDate).format(fmt)
    val score = summary.scores.totalSSSScore
    val tier = summary.scores.sssSeverityTier.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() }
    Card(modifier = Modifier.fillMaxWidth(), onClick = onClick) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(date, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                Text(tier, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text("$score / 11", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
    }
}
