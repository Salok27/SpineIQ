package noshtek.back_pain_prototype.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import noshtek.back_pain_prototype.ui.theme.CardShape
import noshtek.back_pain_prototype.ui.theme.ChipShape
import noshtek.back_pain_prototype.ui.theme.SpineIQTheme

/**
 * Dark faux glass — real backdrop blur needs RenderEffect (API 31+), so at
 * minSdk 26 the glass look is built from a translucent panel fill, a faint
 * diagonal starlight wash and a light hairline border. Over [NebulaBackground]
 * the blooms shine through the translucent fill, which sells the effect.
 */
fun Modifier.glass(
    shape: Shape,
    surface: Color,
    border: Color,
    highlightAlpha: Float = 0.07f,
): Modifier = this
    .clip(shape)
    .background(surface)
    .drawBehind {
        drawRect(
            Brush.linearGradient(
                colors = listOf(Color.White.copy(alpha = highlightAlpha), Color.Transparent),
                start = Offset.Zero,
                end = Offset(size.width, size.height),
            )
        )
    }
    .border(
        width = 1.dp,
        brush = Brush.linearGradient(
            colors = listOf(border, border.copy(alpha = border.alpha * 0.25f)),
        ),
        shape = shape,
    )

/** Glass content card for use on the nebula background. */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = CardShape,
    contentPadding: PaddingValues = PaddingValues(20.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = SpineIQTheme.colors
    Column(
        modifier
            .fillMaxWidth()
            .softShadow(colors.shadowTint, shape, elevation = 14.dp, alpha = 0.20f)
            .glass(
                shape = shape,
                surface = colors.glassSurface,
                border = colors.glassBorder,
            )
            .padding(contentPadding),
        content = content,
    )
}

/** Small glass chip for use ON TOP of gradient hero surfaces (no shadow). */
@Composable
fun GlassOnGradient(
    modifier: Modifier = Modifier,
    shape: Shape = ChipShape,
    contentPadding: PaddingValues = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier
            .clip(shape)
            .background(Color.Black.copy(alpha = 0.22f))
            .border(1.dp, Color.White.copy(alpha = 0.28f), shape)
            .padding(contentPadding),
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}
