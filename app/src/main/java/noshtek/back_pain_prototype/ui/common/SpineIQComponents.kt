package noshtek.back_pain_prototype.ui.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import noshtek.back_pain_prototype.core.scoring.model.BackPainRiskClassification
import noshtek.back_pain_prototype.core.scoring.model.RiskTier
import noshtek.back_pain_prototype.core.scoring.model.SssSeverityTier
import noshtek.back_pain_prototype.ui.theme.HeroShape
import noshtek.back_pain_prototype.ui.theme.PillShape
import noshtek.back_pain_prototype.ui.theme.RiskHigh
import noshtek.back_pain_prototype.ui.theme.RiskLow
import noshtek.back_pain_prototype.ui.theme.RiskModerate
import noshtek.back_pain_prototype.ui.theme.RiskModerateSevere
import noshtek.back_pain_prototype.ui.theme.RiskSevereUrgent
import noshtek.back_pain_prototype.ui.theme.SpineIQTheme
import noshtek.back_pain_prototype.ui.theme.brandGradient

// ── Top Bar ───────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpineIQTopBar(
    title: String,
    onBack: (() -> Unit)? = null,
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
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
            // Transparent so the nebula background runs uninterrupted behind it.
            containerColor = Color.Transparent,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
    )
}

// ── Risk Badges ───────────────────────────────────────────────────────────────
// Clinical semantics: hue ordering is the source of truth and never borrows
// from the reward palette. On dark panels the badge is a tinted pill with a
// neon hairline in the risk colour.

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
            .background(color.copy(alpha = 0.16f), PillShape)
            .border(BorderStroke(1.dp, color.copy(alpha = 0.45f)), PillShape)
            .padding(horizontal = 12.dp, vertical = 5.dp),
    ) {
        Text(
            text = label,
            // Deepen the hue for AA-legible text on the pale tint (amber/orange
            // would be unreadable at full brightness on a light pill).
            color = lerp(color, Color.Black, 0.40f),
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
    modifier: Modifier = Modifier,
) {
    val colors = SpineIQTheme.colors
    val valueText = if (unit.isNotEmpty()) "%.1f %s".format(value, unit) else "%.0f".format(value)
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.weight(1f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.width(8.dp))
            // Holo value readout: dark chip + cyan numeral + hairline.
            Box(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainerHighest,
                        shape = RoundedCornerShape(10.dp),
                    )
                    .border(
                        1.dp,
                        colors.accent.copy(alpha = 0.35f),
                        RoundedCornerShape(10.dp),
                    )
                    .padding(horizontal = 12.dp, vertical = 5.dp),
            ) {
                Text(
                    text = valueText,
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.accentText,
                    fontWeight = FontWeight.Bold,
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
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                activeTickColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.4f),
                inactiveTickColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
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
    icon: ImageVector? = null,
    accent: Color = MaterialTheme.colorScheme.primary,
    content: @Composable ColumnScope.() -> Unit,
) {
    AppCard(modifier = modifier, shadowElevation = 10.dp) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(accent.copy(alpha = 0.14f))
                        .border(1.dp, accent.copy(alpha = 0.30f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(12.dp))
            }
            Text(text = title, style = MaterialTheme.typography.titleMedium)
        }
        Spacer(Modifier.height(14.dp))
        content()
    }
}

// ── Action Buttons ────────────────────────────────────────────────────────────

@Composable
fun NextButton(
    onClick: () -> Unit,
    enabled: Boolean = true,
    label: String = "Next",
    loading: Boolean = false,
) {
    PrimaryButton(
        onClick = onClick,
        label = label,
        enabled = enabled,
        loading = loading,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
fun RequiredFieldError(show: Boolean, message: String = "This field is required") {
    AnimatedVisibility(
        visible = show,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
    ) {
        // Clinical-red alert pill — deliberately plain, never playful.
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(PillShape)
                .background(MaterialTheme.colorScheme.error.copy(alpha = 0.12f))
                .border(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.45f), PillShape)
                .padding(horizontal = 12.dp, vertical = 6.dp),
        ) {
            Icon(
                Icons.Filled.ErrorOutline,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(16.dp),
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

// ── Data Display ──────────────────────────────────────────────────────────────

@Composable
fun LabelledScoreBar(
    label: String,
    score: Int,
    maxScore: Int,
    color: Color,
    modifier: Modifier = Modifier,
) {
    require(maxScore > 0) { "maxScore must be > 0" }
    val target = score.coerceIn(0, maxScore).toFloat() / maxScore
    val scoreText = "$score / $maxScore"

    var started by remember { mutableStateOf(false) }
    LaunchedEffect(target) { started = true }
    val progress by animateFloatAsState(
        targetValue = if (started) target else 0f,
        animationSpec = tween(MotionTokens.DurationScore, easing = MotionTokens.Emphasized),
        label = "score-bar",
    )

    Column(modifier = modifier.semantics(mergeDescendants = true) {
        contentDescription = "$label: $scoreText"
    }) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
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
                .height(10.dp)
                .clip(PillShape),
            color = color,
            trackColor = color.copy(alpha = 0.18f),
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
                .background(color.copy(alpha = 0.14f), PillShape)
                .border(BorderStroke(1.dp, color.copy(alpha = 0.50f)), PillShape)
                .padding(horizontal = 10.dp, vertical = 3.dp),
        ) {
            Text(
                text = tierLabel,
                color = lerp(color, Color.Black, 0.40f),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

// ── Score Hero Card (animated gauge) ───────────────────────────────────────────

@Composable
fun ScoreHeroCard(
    scoreLabel: String,
    score: Int,
    maxScore: Int,
    classification: BackPainRiskClassification,
    modifier: Modifier = Modifier,
) {
    val fraction = if (maxScore > 0) score.coerceIn(0, maxScore).toFloat() / maxScore else 0f
    GlowCard(
        modifier = modifier,
        shape = HeroShape,
        contentPadding = PaddingValues(24.dp),
        borderAlpha = 0.45f,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                ScoreGauge(
                    progress = fraction,
                    diameter = 132.dp,
                    strokeWidth = 14.dp,
                    trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    brush = brandGradient(),
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        AnimatedCountText(
                            target = score,
                            style = MaterialTheme.typography.displayMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                        )
                        MicroLabel("of $maxScore")
                    }
                }
                Spacer(Modifier.height(10.dp))
                MicroLabel(scoreLabel)
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                MicroLabel("Overall Risk")
                CompositeBadge(classification = classification)
                Text(
                    "Based on your spine severity, lifestyle and functional scores.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
