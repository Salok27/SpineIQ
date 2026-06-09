package noshtek.back_pain_prototype.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import noshtek.back_pain_prototype.core.scoring.model.BackPainRiskClassification
import noshtek.back_pain_prototype.core.scoring.model.RiskTier
import noshtek.back_pain_prototype.core.scoring.model.SssSeverityTier
import noshtek.back_pain_prototype.ui.theme.Blue600
import noshtek.back_pain_prototype.ui.theme.Blue900
import noshtek.back_pain_prototype.ui.theme.RiskHigh
import noshtek.back_pain_prototype.ui.theme.RiskLow
import noshtek.back_pain_prototype.ui.theme.RiskModerate
import noshtek.back_pain_prototype.ui.theme.RiskModerateSevere
import noshtek.back_pain_prototype.ui.theme.RiskSevereUrgent

private val PillShape = RoundedCornerShape(50)
private val CardShape = RoundedCornerShape(12.dp)
private val ButtonShape = RoundedCornerShape(16.dp)

// ── Top Bar ───────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpineIQTopBar(
    title: String,
    onBack: (() -> Unit)? = null
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
    )
}

// ── Risk Badges ───────────────────────────────────────────────────────────────

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
        BackPainRiskClassification.LOW           -> "Low" to RiskLow
        BackPainRiskClassification.LOW_MODERATE  -> "Low–Moderate" to RiskLow
        BackPainRiskClassification.MILD_MODERATE -> "Mild–Moderate" to RiskModerate
        BackPainRiskClassification.MODERATE      -> "Moderate" to RiskModerate
        BackPainRiskClassification.MODERATE_HIGH -> "Moderate–High" to RiskModerateSevere
        BackPainRiskClassification.HIGH          -> "High" to RiskHigh
        BackPainRiskClassification.SEVERE_URGENT -> "Severe / Urgent" to RiskSevereUrgent
    }
    RiskBadge(label = label, color = color, modifier = modifier)
}

@Composable
fun RiskBadge(label: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(color.copy(alpha = 0.12f), PillShape)
            .border(BorderStroke(1.dp, color.copy(alpha = 0.35f)), PillShape)
            .padding(horizontal = 12.dp, vertical = 5.dp)
    ) {
        Text(
            text = label,
            color = color,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
        )
    }
}

// ── Form Inputs ───────────────────────────────────────────────────────────────

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
                text = label,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.weight(1f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(8.dp),
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = valueText,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                )
            }
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.secondary,
                activeTrackColor = MaterialTheme.colorScheme.secondary,
                inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "$label: $valueText" },
        )
    }
}

// ── Layout Containers ─────────────────────────────────────────────────────────

@Composable
fun SectionCard(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = CardShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant,
        ),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(14.dp))
            content()
        }
    }
}

// ── Action Buttons ────────────────────────────────────────────────────────────

@Composable
fun NextButton(
    onClick: () -> Unit,
    enabled: Boolean = true,
    label: String = "Next",
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = ButtonShape,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun RequiredFieldError(show: Boolean, message: String = "This field is required") {
    if (show) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

// ── Data Display ──────────────────────────────────────────────────────────────

@Composable
fun LabelledScoreBar(
    label: String,
    score: Int,
    maxScore: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    require(maxScore > 0) { "maxScore must be > 0" }
    val progress = score.coerceIn(0, maxScore).toFloat() / maxScore
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
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = scoreText,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = color,
                maxLines = 1,
            )
        }
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(PillShape),
            color = color,
            trackColor = color.copy(alpha = 0.15f),
            strokeCap = StrokeCap.Round,
            gapSize = 0.dp,
            drawStopIndicator = {},
        )
    }
}

@Composable
fun RiskTileSmall(label: String, tier: RiskTier) {
    val (color, tierLabel) = when (tier) {
        RiskTier.LOW      -> RiskLow to "Low"
        RiskTier.MODERATE -> RiskModerate to "Moderate"
        RiskTier.HIGH     -> RiskHigh to "High"
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Box(
            modifier = Modifier
                .background(color.copy(alpha = 0.12f), PillShape)
                .border(BorderStroke(1.dp, color.copy(alpha = 0.35f)), PillShape)
                .padding(horizontal = 10.dp, vertical = 3.dp)
        ) {
            Text(
                text = tierLabel,
                color = color,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

// ── New: Wizard Progress Bar ──────────────────────────────────────────────────

@Composable
fun WizardProgressBar(
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier,
) {
    val progressFraction = currentStep.toFloat() / totalSteps
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Step $currentStep of $totalSteps",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "${(progressFraction * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Spacer(Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { progressFraction },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(PillShape),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.primaryContainer,
            strokeCap = StrokeCap.Round,
            gapSize = 0.dp,
            drawStopIndicator = {},
        )
    }
}

// ── New: Score Hero Card ──────────────────────────────────────────────────────

@Composable
fun ScoreHeroCard(
    scoreLabel: String,
    scoreValue: String,
    classification: BackPainRiskClassification,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.horizontalGradient(listOf(Blue600, Blue900)))
            .padding(horizontal = 24.dp, vertical = 32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = scoreLabel,
                style = MaterialTheme.typography.titleSmall,
                color = Color.White.copy(alpha = 0.75f),
            )
            Text(
                text = scoreValue,
                style = MaterialTheme.typography.displayMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(4.dp))
            CompositeBadge(classification = classification)
        }
    }
}
