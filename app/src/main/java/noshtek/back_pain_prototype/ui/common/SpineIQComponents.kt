package noshtek.back_pain_prototype.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import noshtek.back_pain_prototype.core.scoring.model.BackPainRiskClassification
import noshtek.back_pain_prototype.core.scoring.model.RiskTier
import noshtek.back_pain_prototype.core.scoring.model.SssSeverityTier
import noshtek.back_pain_prototype.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpineIQTopBar(
    title: String,
    onBack: (() -> Unit)? = null
) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        }
    )
}

@Composable
fun SssTierBadge(tier: SssSeverityTier, modifier: Modifier = Modifier) {
    val (label, color) = when (tier) {
        SssSeverityTier.LOW              -> "Low" to RiskLow
        SssSeverityTier.MILD_MODERATE    -> "Mild–Moderate" to RiskModerate
        SssSeverityTier.MODERATE_SEVERE  -> "Moderate–Severe" to RiskModerateSevere
        SssSeverityTier.SEVERE_HIGH_RISK -> "Severe / High Risk" to RiskHigh
    }
    RiskBadge(label = label, color = color, modifier = modifier)
}

@Composable
fun LifestyleTierBadge(tier: RiskTier, modifier: Modifier = Modifier) {
    val (label, color) = when (tier) {
        RiskTier.LOW      -> "Low Risk" to RiskLow
        RiskTier.MODERATE -> "Moderate Risk" to RiskModerate
        RiskTier.HIGH     -> "High Risk" to RiskHigh
    }
    RiskBadge(label = label, color = color, modifier = modifier)
}

@Composable
fun CompositeBadge(classification: BackPainRiskClassification, modifier: Modifier = Modifier) {
    val (label, color) = when (classification) {
        BackPainRiskClassification.LOW            -> "Low" to RiskLow
        BackPainRiskClassification.LOW_MODERATE   -> "Low–Moderate" to RiskLow
        BackPainRiskClassification.MILD_MODERATE  -> "Mild–Moderate" to RiskModerate
        BackPainRiskClassification.MODERATE       -> "Moderate" to RiskModerate
        BackPainRiskClassification.MODERATE_HIGH  -> "Moderate–High" to RiskModerateSevere
        BackPainRiskClassification.HIGH           -> "High" to RiskHigh
        BackPainRiskClassification.SEVERE_URGENT  -> "Severe / Urgent" to RiskSevereUrgent
    }
    RiskBadge(label = label, color = color, modifier = modifier)
}

@Composable
fun RiskBadge(label: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(label, color = color, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun SliderWithLabel(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int = 0,
    unit: String = "",
    modifier: Modifier = Modifier
) {
    val valueText = if (unit.isNotEmpty()) "%.1f %s".format(value, unit) else "%.0f".format(value)
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                label,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.width(8.dp))
            Text(
                valueText,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps,
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "$label: $valueText" }
        )
    }
}

@Composable
fun SectionCard(title: String, modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Card(modifier = modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun NextButton(
    onClick: () -> Unit,
    enabled: Boolean = true,
    label: String = "Next"
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(label)
    }
}

@Composable
fun RequiredFieldError(show: Boolean, message: String = "This field is required") {
    if (show) {
        Text(message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
    }
}

/**
 * Horizontal score bar with a numeric label pinned to the end of the filled
 * portion (or against the track edge when progress is near 0 or 1).
 * Uses a [Box] overlay — no Canvas required.
 */
@Composable
fun LabelledScoreBar(
    label: String,
    score: Int,
    maxScore: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    require(maxScore > 0) { "maxScore must be > 0" }
    val progress = (score.coerceIn(0, maxScore).toFloat() / maxScore)
    val scoreText = "$score / $maxScore"

    Column(modifier = modifier.semantics(mergeDescendants = true) {
        contentDescription = "$label: $scoreText"
    }) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                label,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                scoreText,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = color,
                maxLines = 1
            )
        }
        Spacer(Modifier.height(6.dp))
        // 12 dp bar height keeps it above the 48 dp touch-target floor because
        // the Column as a whole is the interactive unit, not the bar itself.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
        ) {
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxSize(),
                color = color,
                trackColor = color.copy(alpha = 0.15f),
                gapSize = 0.dp,
                drawStopIndicator = {}
            )
        }
    }
}

@Composable
fun RiskTileSmall(label: String, tier: RiskTier) {
    val color = when (tier) {
        RiskTier.LOW -> RiskLow; RiskTier.MODERATE -> RiskModerate; RiskTier.HIGH -> RiskHigh
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall)
        Box(
            modifier = Modifier
                .background(color.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(tier.name.lowercase().replaceFirstChar { it.uppercase() },
                color = color, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        }
    }
}
