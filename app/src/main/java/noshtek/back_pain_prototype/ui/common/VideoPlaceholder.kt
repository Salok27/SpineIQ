package noshtek.back_pain_prototype.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import noshtek.back_pain_prototype.ui.theme.DeepSpaceLow
import noshtek.back_pain_prototype.ui.theme.PillShape
import noshtek.back_pain_prototype.ui.theme.Spacing
import noshtek.back_pain_prototype.ui.theme.SpineIQTheme
import noshtek.back_pain_prototype.ui.theme.brandGradient

/**
 * Holo-frame placeholder that reserves space for a future instructional/demo
 * video shown before an assessment section. Pure UI — there is no playback yet
 * (no ExoPlayer / video deps). Styled as a dormant holo-projector: deep-space
 * frame, faint aurora wash, scanlines and a glowing play orb.
 *
 * Future swap: when real assets arrive, replace only [VideoFrameSurface] with an
 * ExoPlayer `AndroidView`. This public signature and every call site stay the same.
 */
@Composable
fun AssessmentVideoPlaceholder(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
) {
    // contentPadding = 0 so the 16:9 frame bleeds to the card's rounded edges;
    // the AppCard's own clip rounds the frame's top corners. Text/callout get
    // their own inset below.
    AppCard(modifier = modifier, contentPadding = PaddingValues(0.dp)) {
        VideoFrameSurface()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.xl, vertical = Spacing.lg),
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(Spacing.xs))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(Spacing.md))
            ComingSoonPill()
        }
    }
}

/**
 * The 16:9 "projector" area: near-black base, low-alpha aurora wash, subtle
 * scanlines, and a glowing cyan play orb. This is the single composable to
 * replace with a real video surface later.
 */
@Composable
private fun VideoFrameSurface() {
    val aurora = brandGradient()
    val glow = SpineIQTheme.colors.glow
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .background(DeepSpaceLow)
            .drawBehind {
                // Dormant aurora projection.
                drawRect(brush = aurora, alpha = 0.16f)
                drawCircle(
                    color = Color.White.copy(alpha = 0.05f),
                    radius = size.maxDimension * 0.42f,
                    center = Offset(size.width * 0.86f, size.height * 0.06f),
                )
                // Scanlines every 6dp — the holo-screen texture.
                val step = 6.dp.toPx()
                var y = 0f
                while (y < size.height) {
                    drawLine(
                        color = Color.White.copy(alpha = 0.030f),
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1f,
                    )
                    y += step
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        // Glowing play orb.
        Box(
            modifier = Modifier
                .size(64.dp)
                .neonGlow(glow, CircleShape, elevation = 16.dp, alpha = 0.50f)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.10f))
                .border(1.5.dp, SpineIQTheme.colors.accent.copy(alpha = 0.70f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Filled.PlayArrow,
                contentDescription = "Demo video — coming soon",
                tint = Color.White,
                modifier = Modifier.size(34.dp),
            )
        }
    }
}

/** Static, non-interactive status pill — mirrors the [RiskBadge] idiom. */
@Composable
private fun ComingSoonPill() {
    val accent = SpineIQTheme.colors.accentText
    Row(
        modifier = Modifier
            .clip(PillShape)
            .background(SpineIQTheme.colors.accent.copy(alpha = 0.12f))
            .border(1.dp, SpineIQTheme.colors.accent.copy(alpha = 0.35f), PillShape)
            .padding(horizontal = Spacing.md, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            Icons.Filled.Schedule,
            contentDescription = null,
            tint = accent,
            modifier = Modifier.size(14.dp),
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = "Video Coming Soon",
            style = MaterialTheme.typography.labelMedium,
            color = accent,
        )
    }
}
