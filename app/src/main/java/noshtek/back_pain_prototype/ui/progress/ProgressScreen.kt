package noshtek.back_pain_prototype.ui.progress

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingFlat
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import noshtek.back_pain_prototype.core.scoring.model.RiskTier
import noshtek.back_pain_prototype.navigation.Screen
import noshtek.back_pain_prototype.ui.common.AppCard
import noshtek.back_pain_prototype.ui.common.MotionTokens
import noshtek.back_pain_prototype.ui.common.PressableCard
import noshtek.back_pain_prototype.ui.common.PrimaryButton
import noshtek.back_pain_prototype.ui.common.entrance
import noshtek.back_pain_prototype.ui.theme.RiskHigh
import noshtek.back_pain_prototype.ui.theme.RiskLow
import noshtek.back_pain_prototype.ui.theme.RiskModerate
import noshtek.back_pain_prototype.ui.theme.SpineIQTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.ceil
import kotlin.math.max

private val PillShape = RoundedCornerShape(50)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    navController: NavController,
    viewModel: ProgressViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val fmt = remember { DateTimeFormatter.ofPattern("d MMM yy") }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("My Progress", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(32.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ShowChart,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp),
                    )
                    Text(
                        "Not enough data yet",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        "Complete at least 2 assessments to see your progress trends here.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(8.dp))
                    PrimaryButton(
                        onClick = { navController.navigate(Screen.AssessmentGraph.route) },
                        label = "Start Assessment",
                    )
                }
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Delta callout
            state.latestScoreDelta?.let { delta ->
                item {
                    val improved = delta > 0
                    val worse = delta < 0
                    val accent = when {
                        improved -> SpineIQTheme.colors.success
                        worse -> RiskHigh
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                    val icon = when {
                        improved -> Icons.AutoMirrored.Filled.TrendingDown
                        worse -> Icons.AutoMirrored.Filled.TrendingUp
                        else -> Icons.AutoMirrored.Filled.TrendingFlat
                    }
                    AppCard(
                        containerColor = accent.copy(alpha = 0.10f),
                        border = false,
                        shadowElevation = 6.dp,
                        modifier = Modifier.entrance(0),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(
                                modifier = Modifier.size(40.dp).background(accent.copy(alpha = 0.16f), PillShape),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(22.dp))
                            }
                            Column {
                                val label = when {
                                    improved -> "SSS score improved by $delta point${if (delta != 1) "s" else ""}"
                                    worse -> "SSS score increased by ${-delta} point${if (-delta != 1) "s" else ""}"
                                    else -> "SSS score is unchanged"
                                }
                                Text(label, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                state.previousAssessmentDate?.let { prev ->
                                    Text(
                                        "since your last check-in on ${prev.format(fmt)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // SSS trend chart
            item {
                ChartCard(
                    title = "SSS Score Over Time",
                    icon = Icons.AutoMirrored.Filled.ShowChart,
                    accent = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.entrance(1),
                ) {
                    val scores = state.assessments.map { it.scores.totalSSSScore.toFloat() }
                    val dates = state.assessments.map { LocalDate.ofEpochDay(it.record.assessmentDate).format(fmt) }
                    SimpleLineChart(values = scores, labels = dates, maxY = 11f, color = MaterialTheme.colorScheme.primary)
                }
            }

            // Lifestyle risk trend chart
            item {
                ChartCard(
                    title = "Lifestyle Risk Over Time",
                    icon = Icons.AutoMirrored.Filled.DirectionsWalk,
                    accent = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.entrance(2),
                ) {
                    val tiers = state.assessments.map { summary ->
                        when (summary.scores.lifestyleRiskTier) {
                            RiskTier.LOW -> 1f
                            RiskTier.MODERATE -> 2f
                            else -> 3f
                        }
                    }
                    val dates = state.assessments.map { LocalDate.ofEpochDay(it.record.assessmentDate).format(fmt) }
                    SimpleLineChart(values = tiers, labels = dates, maxY = 3f, color = MaterialTheme.colorScheme.secondary)
                    Spacer(Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        listOf("1=Low", "2=Moderate", "3=High").forEach {
                            Text(it, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            // Habit Trends section header
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.entrance(3)) {
                    Text("Habit Trends", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        "How your four lifestyle risk factors have shifted across check-ins. Lower is better.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            items(
                listOf(
                    "Sitting" to state.assessments.map { it.scores.sittingRisk },
                    "Walking" to state.assessments.map { it.scores.walkingRisk },
                    "Exercise" to state.assessments.map { it.scores.exerciseRisk },
                    "Sleep" to state.assessments.map { it.scores.sleepRisk },
                )
            ) { (label, history) ->
                HabitTrendCard(label = label, history = history, modifier = Modifier.entrance())
            }

            // Assessment history list
            item {
                Row(
                    modifier = Modifier.entrance(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(Icons.Filled.History, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                    Text("Assessment History", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }
            items(state.assessments.reversed()) { summary ->
                AssessmentHistoryRow(
                    summary = summary,
                    fmt = fmt,
                    onClick = { navController.navigate(Screen.FullReport.route(summary.record.id)) },
                    modifier = Modifier.entrance(),
                )
            }
        }
    }
}

@Composable
private fun ChartCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accent: Color,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    AppCard(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(accent.copy(alpha = 0.12f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(18.dp))
            }
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(12.dp))
        content()
    }
}

/**
 * Line chart with labelled axes, grid lines, on-point value labels, a smooth
 * bezier curve and a gradient fill below it. The line traces on with a
 * PathMeasure segment, the fill fades up, and value labels fade in. All text is
 * clamped inside the canvas bounds so the chart never overflows its parent card.
 */
@Composable
private fun SimpleLineChart(
    values: List<Float>,
    labels: List<String>,
    maxY: Float,
    color: Color,
    valueLabel: (Float) -> String = { it.toInt().toString() },
    modifier: Modifier = Modifier,
) {
    if (values.size < 2) return

    val lineColor = color
    val gridColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.10f)
    val surfaceColor = MaterialTheme.colorScheme.surface
    val fillBrush = Brush.verticalGradient(
        listOf(color.copy(alpha = 0.28f), color.copy(alpha = 0f))
    )

    val textMeasurer = rememberTextMeasurer()
    val axisStyle = TextStyle(fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    val valueStyle = TextStyle(fontSize = 10.sp, color = lineColor, fontWeight = FontWeight.Bold)

    val ticks = remember(maxY) { yAxisTicks(maxY) }
    val yLayouts = remember(ticks, axisStyle) { ticks.map { textMeasurer.measure(valueLabel(it), axisStyle) } }
    val xLayouts = remember(labels, axisStyle) { labels.map { textMeasurer.measure(it, axisStyle) } }
    val valueLayouts = remember(values, valueStyle) { values.map { textMeasurer.measure(valueLabel(it), valueStyle) } }

    var started by remember { mutableStateOf(false) }
    LaunchedEffect(values) { started = true }
    val anim by animateFloatAsState(
        targetValue = if (started) 1f else 0f,
        animationSpec = tween(900, easing = MotionTokens.Emphasized),
        label = "chart-draw",
    )
    val pathMeasure = remember { PathMeasure() }

    Canvas(modifier = modifier.fillMaxWidth().height(160.dp)) {
        val w = size.width
        val h = size.height
        val n = values.size

        val leftPad = (yLayouts.maxOfOrNull { it.size.width } ?: 0).toFloat() + 8.dp.toPx()
        val rightPad = 12.dp.toPx()
        val topPad = (valueLayouts.maxOfOrNull { it.size.height } ?: 0).toFloat() + 6.dp.toPx()
        val bottomPad = (xLayouts.maxOfOrNull { it.size.height } ?: 0).toFloat() + 6.dp.toPx()

        val plotW = (w - leftPad - rightPad).coerceAtLeast(1f)
        val plotH = (h - topPad - bottomPad).coerceAtLeast(1f)
        val baseline = topPad + plotH
        val stepX = plotW / (n - 1).toFloat()

        fun px(i: Int) = leftPad + i * stepX
        fun py(v: Float) = baseline - (v / maxY) * plotH

        ticks.forEachIndexed { idx, tick ->
            val gy = py(tick)
            drawLine(
                color = gridColor,
                start = Offset(leftPad, gy),
                end = Offset(leftPad + plotW, gy),
                strokeWidth = 1.dp.toPx(),
            )
            val lay = yLayouts[idx]
            drawText(lay, topLeft = Offset(leftPad - 6.dp.toPx() - lay.size.width, gy - lay.size.height / 2f))
        }

        val points = values.mapIndexed { i, v -> Offset(px(i), py(v)) }

        val linePath = Path().apply {
            moveTo(points[0].x, points[0].y)
            for (i in 1 until points.size) {
                val prev = points[i - 1]
                val cur = points[i]
                val midX = (prev.x + cur.x) / 2f
                cubicTo(midX, prev.y, midX, cur.y, cur.x, cur.y)
            }
        }
        val fillPath = Path().apply {
            addPath(linePath)
            lineTo(points.last().x, baseline)
            lineTo(points.first().x, baseline)
            close()
        }
        drawPath(fillPath, fillBrush, alpha = anim)

        // Trace the line on with a PathMeasure segment.
        pathMeasure.setPath(linePath, false)
        val traced = Path()
        pathMeasure.getSegment(0f, pathMeasure.length * anim, traced, true)
        drawPath(traced, lineColor, style = Stroke(width = 3.dp.toPx()))

        points.forEachIndexed { i, p ->
            val frac = i.toFloat() / (n - 1)
            if (frac <= anim + 0.001f) {
                drawCircle(surfaceColor, radius = 5.dp.toPx(), center = p)
                drawCircle(lineColor, radius = 3.5.dp.toPx(), center = p)
                val lay = valueLayouts[i]
                val tx = (p.x - lay.size.width / 2f).coerceIn(0f, w - lay.size.width)
                val ty = (p.y - lay.size.height - 4.dp.toPx()).coerceAtLeast(0f)
                drawText(lay, topLeft = Offset(tx, ty), alpha = anim)
            }
        }

        val maxLabelW = (xLayouts.maxOfOrNull { it.size.width } ?: 1).toFloat()
        val maxLabels = max(2, (plotW / (maxLabelW + 8.dp.toPx())).toInt())
        val stride = max(1, ceil(n / maxLabels.toFloat()).toInt())
        for (i in 0 until n) {
            if (i % stride != 0 && i != n - 1) continue
            val lay = xLayouts.getOrNull(i) ?: continue
            val tx = (px(i) - lay.size.width / 2f).coerceIn(0f, w - lay.size.width)
            drawText(lay, topLeft = Offset(tx, baseline + 6.dp.toPx()))
        }
    }
}

private fun yAxisTicks(maxY: Float): List<Float> {
    val rawStep = maxY / 4f
    val step = when {
        rawStep <= 1f -> 1f
        rawStep <= 2f -> 2f
        rawStep <= 3f -> 3f
        rawStep <= 5f -> 5f
        else          -> 10f
    }
    val ticks = mutableListOf<Float>()
    var v = 0f
    while (v <= maxY + 0.001f) {
        ticks.add(v); v += step
    }
    if (ticks.last() < maxY) ticks.add(maxY)
    return ticks
}

@Composable
private fun HabitTrendCard(label: String, history: List<RiskTier>, modifier: Modifier = Modifier) {
    val current = history.last()
    val previous = history.getOrNull(history.size - 2)
    AppCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(label, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            previous?.let { TrendPill(trendFor(previous = it, current = current)) }
        }

        Spacer(Modifier.height(12.dp))
        RiskLevelBar(tier = current)
        Spacer(Modifier.height(6.dp))
        Text(
            "Current level: ${riskTierLabel(current)}",
            style = MaterialTheme.typography.labelMedium,
            color = tierColor(current),
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(Modifier.height(12.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            history.forEach { tier ->
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .background(tierColor(tier), RoundedCornerShape(4.dp))
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            "Risk at each of your last ${history.size} check-ins (oldest → newest)",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private enum class HabitTrend(val label: String) {
    IMPROVING("Improving"),
    WORSENING("Worsening"),
    STABLE("Stable"),
}

private fun trendFor(previous: RiskTier, current: RiskTier): HabitTrend {
    val delta = tierOrdinal(current) - tierOrdinal(previous)
    return when {
        delta < 0 -> HabitTrend.IMPROVING
        delta > 0 -> HabitTrend.WORSENING
        else      -> HabitTrend.STABLE
    }
}

@Composable
private fun TrendPill(trend: HabitTrend) {
    val color = when (trend) {
        HabitTrend.IMPROVING -> RiskLow
        HabitTrend.WORSENING -> RiskHigh
        HabitTrend.STABLE    -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    val icon = when (trend) {
        HabitTrend.IMPROVING -> Icons.AutoMirrored.Filled.TrendingDown
        HabitTrend.WORSENING -> Icons.AutoMirrored.Filled.TrendingUp
        HabitTrend.STABLE    -> Icons.AutoMirrored.Filled.TrendingFlat
    }
    Row(
        modifier = Modifier
            .background(color.copy(alpha = 0.12f), PillShape)
            .border(BorderStroke(1.dp, color.copy(alpha = 0.35f)), PillShape)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(14.dp))
        Text(trend.label, color = color, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun RiskLevelBar(tier: RiskTier) {
    val active = tierOrdinal(tier)
    val color = tierColor(tier)
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(3) { i ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(10.dp)
                    .background(
                        if (i <= active) color else color.copy(alpha = 0.12f),
                        RoundedCornerShape(4.dp),
                    )
            )
        }
    }
}

private fun tierColor(tier: RiskTier): Color = when (tier) {
    RiskTier.LOW      -> RiskLow
    RiskTier.MODERATE -> RiskModerate
    RiskTier.HIGH     -> RiskHigh
}

private fun riskTierLabel(tier: RiskTier): String =
    tier.name.lowercase().replaceFirstChar { it.uppercase() }

private fun tierOrdinal(tier: RiskTier) = when (tier) { RiskTier.LOW -> 0; RiskTier.MODERATE -> 1; else -> 2 }

@Composable
private fun AssessmentHistoryRow(
    summary: AssessmentSummary,
    fmt: DateTimeFormatter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val date = LocalDate.ofEpochDay(summary.record.assessmentDate).format(fmt)
    val score = summary.scores.totalSSSScore
    val tier = summary.scores.sssSeverityTier.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() }
    PressableCard(onClick = onClick, modifier = modifier, contentPadding = PaddingValues(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(date, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                Text(tier, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(
                "$score / 11",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}
