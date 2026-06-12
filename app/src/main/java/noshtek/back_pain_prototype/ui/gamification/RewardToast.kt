package noshtek.back_pain_prototype.ui.gamification

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import noshtek.back_pain_prototype.ui.common.MotionTokens
import noshtek.back_pain_prototype.ui.common.softShadow
import noshtek.back_pain_prototype.ui.theme.PillShape
import noshtek.back_pain_prototype.ui.theme.SpineIQTheme

/**
 * Non-blocking reward toast: a glass pill that slides in under the status
 * bar, holds, and fades out. Touches pass straight through to the screen.
 */
@Composable
fun RewardToast(
    coins: Int,
    xp: Int,
    onTimeout: () -> Unit,
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
        delay(1_600)
        visible = false
        delay(MotionTokens.DurationFast.toLong())
        onTimeout()
    }
    Box(
        Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(top = 12.dp),
        contentAlignment = Alignment.TopCenter,
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically(tween(MotionTokens.DurationMedium, easing = MotionTokens.Emphasized)) { -it } +
                fadeIn(tween(MotionTokens.DurationMedium)),
            exit = slideOutVertically(tween(MotionTokens.DurationFast)) { -it } +
                fadeOut(tween(MotionTokens.DurationFast)),
        ) {
            RewardChip(
                coins = coins,
                xp = xp,
                emphasized = true,
                modifier = Modifier.softShadow(SpineIQTheme.colors.shadowTint, PillShape, elevation = 12.dp),
            )
        }
    }
}
