package noshtek.back_pain_prototype.ui.gamification

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import noshtek.back_pain_prototype.ui.common.MotionTokens
import noshtek.back_pain_prototype.ui.common.softShadow
import noshtek.back_pain_prototype.ui.theme.PillShape
import noshtek.back_pain_prototype.ui.theme.SpineGlowBright
import noshtek.back_pain_prototype.ui.theme.SpineIQTheme

/**
 * Non-blocking toast: a soft pill that slides in under the status bar, holds,
 * and fades out. Touches pass straight through to the screen beneath.
 */
@Composable
fun RewardToast(
    message: String,
    onTimeout: () -> Unit,
) {
    val colors = SpineIQTheme.colors
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
        delay(1_800)
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
            Row(
                Modifier
                    .softShadow(colors.shadowTint, PillShape, elevation = 14.dp, alpha = 0.22f)
                    .clip(PillShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, PillShape)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    Icons.Filled.AutoAwesome,
                    contentDescription = null,
                    tint = SpineGlowBright,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    message,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}
