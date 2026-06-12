package noshtek.back_pain_prototype.ui.gamification

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import noshtek.back_pain_prototype.ui.common.AnimatedCountText
import noshtek.back_pain_prototype.ui.common.MotionTokens
import noshtek.back_pain_prototype.ui.common.neonGlow
import noshtek.back_pain_prototype.ui.theme.CoinGoldDeep
import noshtek.back_pain_prototype.ui.theme.PillShape
import noshtek.back_pain_prototype.ui.theme.SpineIQTheme
import noshtek.back_pain_prototype.ui.theme.coinGradient
import noshtek.back_pain_prototype.ui.theme.rewardGradient
import noshtek.back_pain_prototype.ui.theme.rewardGradientHorizontal

// ── Coin glyph ────────────────────────────────────────────────────────────────

/** Small Canvas-drawn Spine Coin: radial gold sheen + embossed inner ring. */
@Composable
fun CoinGlyph(
    modifier: Modifier = Modifier,
    size: Dp = 18.dp,
) {
    val sheen = coinGradient()
    Canvas(modifier.size(size)) {
        val radius = this.size.minDimension / 2f
        drawCircle(brush = sheen, radius = radius)
        drawCircle(
            color = CoinGoldDeep.copy(alpha = 0.8f),
            radius = radius * 0.94f,
            style = Stroke(width = radius * 0.12f),
        )
        drawCircle(
            color = CoinGoldDeep.copy(alpha = 0.55f),
            radius = radius * 0.58f,
            style = Stroke(width = radius * 0.14f),
        )
    }
}

// ── Coin balance pill ─────────────────────────────────────────────────────────

/**
 * App-wide Spine Coins balance. Counts up on appearance and pulses briefly
 * whenever the balance increases. [onClick] usually navigates to the Shop.
 */
@Composable
fun CoinBalancePill(
    coins: Int,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val colors = SpineIQTheme.colors
    var previous by remember { mutableIntStateOf(coins) }
    val pulse = remember { Animatable(1f) }
    LaunchedEffect(coins) {
        if (coins > previous) {
            pulse.animateTo(1.15f, tween(120, easing = MotionTokens.Emphasized))
            pulse.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        }
        previous = coins
    }
    Row(
        modifier
            .graphicsLayer { scaleX = pulse.value; scaleY = pulse.value }
            .neonGlow(colors.coin, PillShape, elevation = 10.dp, alpha = 0.30f)
            .clip(PillShape)
            .background(colors.coinContainer)
            .border(1.dp, colors.coin.copy(alpha = 0.45f), PillShape)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CoinGlyph()
        Spacer(Modifier.width(6.dp))
        AnimatedCountText(
            target = coins,
            style = MaterialTheme.typography.labelLarge,
            color = colors.coinText,
            fontWeight = FontWeight.ExtraBold,
        )
    }
}

// ── XP / level bar ────────────────────────────────────────────────────────────

/**
 * Level chip + animated XP progress track in the reward gradient.
 * [xpForNextLevel] null means max level (full bar). [onGradient] switches to
 * white-alpha track/text for use on the brand-gradient hero.
 */
@Composable
fun XpLevelBar(
    level: Int,
    levelName: String,
    xpIntoLevel: Int,
    xpForNextLevel: Int?,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
    onGradient: Boolean = false,
) {
    val colors = SpineIQTheme.colors
    val progress =
        if (xpForNextLevel == null || xpForNextLevel <= 0) 1f
        else (xpIntoLevel.toFloat() / xpForNextLevel).coerceIn(0f, 1f)
    val animated by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "xp-progress",
    )
    val trackColor = if (onGradient) Color.White.copy(alpha = 0.25f) else colors.rewardContainer
    val labelColor = if (onGradient) Color.White.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSurfaceVariant

    Column(modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .clip(PillShape)
                    .background(rewardGradient())
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    "LV $level",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                )
            }
            Spacer(Modifier.width(10.dp))
            Box(
                Modifier
                    .weight(1f)
                    .height(10.dp)
                    .clip(PillShape)
                    .background(trackColor)
            ) {
                Box(
                    Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animated)
                        .clip(PillShape)
                        .background(rewardGradientHorizontal())
                )
            }
        }
        if (!compact) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = if (xpForNextLevel == null) {
                    "$levelName · Max level"
                } else {
                    "$levelName · $xpIntoLevel / $xpForNextLevel XP to next level"
                },
                style = MaterialTheme.typography.labelSmall,
                color = labelColor,
            )
        }
    }
}

// ── Streak flame ──────────────────────────────────────────────────────────────

/** Streak display: pulsing flame when today's activity keeps the streak alive. */
@Composable
fun StreakFlame(
    streakDays: Int,
    activeToday: Boolean,
    modifier: Modifier = Modifier,
    onGradient: Boolean = false,
) {
    val colors = SpineIQTheme.colors
    val active = activeToday && streakDays > 0
    val scale: Float
    val alpha: Float
    if (active) {
        val transition = rememberInfiniteTransition(label = "flame")
        scale = transition.animateFloat(
            initialValue = 1f, targetValue = 1.08f,
            animationSpec = infiniteRepeatable(tween(1100, easing = MotionTokens.Standard), RepeatMode.Reverse),
            label = "flame-scale",
        ).value
        alpha = transition.animateFloat(
            initialValue = 0.85f, targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(1100, easing = MotionTokens.Standard), RepeatMode.Reverse),
            label = "flame-alpha",
        ).value
    } else {
        scale = 1f; alpha = 1f
    }
    val flameTint = when {
        active -> colors.streak
        onGradient -> Color.White.copy(alpha = 0.6f)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    val textColor = when {
        onGradient -> Color.White
        active -> colors.streakText
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = if (active) Icons.Filled.LocalFireDepartment else Icons.Outlined.LocalFireDepartment,
            contentDescription = null,
            tint = flameTint,
            modifier = Modifier
                .size(20.dp)
                .graphicsLayer { scaleX = scale; scaleY = scale; this.alpha = alpha },
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = if (streakDays == 1) "1 day streak" else "$streakDays day streak",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = textColor,
        )
    }
}

// ── Reward chip ───────────────────────────────────────────────────────────────

/** "+10 · +20 XP" pill used to preview or confirm rewards. */
@Composable
fun RewardChip(
    coins: Int = 0,
    xp: Int = 0,
    modifier: Modifier = Modifier,
    emphasized: Boolean = false,
) {
    val colors = SpineIQTheme.colors
    val textStyle =
        if (emphasized) MaterialTheme.typography.titleMedium else MaterialTheme.typography.labelMedium
    Row(
        modifier
            .clip(PillShape)
            .background(colors.rewardContainer)
            .border(1.dp, colors.reward.copy(alpha = 0.25f), PillShape)
            .padding(horizontal = if (emphasized) 16.dp else 10.dp, vertical = if (emphasized) 8.dp else 5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (coins > 0) {
            CoinGlyph(size = if (emphasized) 20.dp else 14.dp)
            Spacer(Modifier.width(4.dp))
            if (emphasized) {
                AnimatedCountText(
                    target = coins,
                    style = textStyle,
                    color = colors.coinText,
                    fontWeight = FontWeight.ExtraBold,
                    prefix = "+",
                )
            } else {
                Text("+$coins", style = textStyle, color = colors.coinText, fontWeight = FontWeight.Bold)
            }
        }
        if (coins > 0 && xp > 0) {
            Text(
                "  ·  ",
                style = textStyle,
                color = colors.onRewardContainer.copy(alpha = 0.5f),
            )
        }
        if (xp > 0) {
            if (emphasized) {
                AnimatedCountText(
                    target = xp,
                    style = textStyle,
                    color = colors.rewardText,
                    fontWeight = FontWeight.ExtraBold,
                    prefix = "+",
                    suffix = " XP",
                )
            } else {
                Text("+$xp XP", style = textStyle, color = colors.rewardText, fontWeight = FontWeight.Bold)
            }
        }
    }
}
