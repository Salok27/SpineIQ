package noshtek.back_pain_prototype.ui.avatar

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke

// Landmarks: eyes (44.5/55.5, 23) · wrists (29..36 / 64..71, y ~60) ·
// head top y 9 · torso (36..64, 38..69).

/** Glasses — round frames with a bridge and temples. */
val AccGlasses = AvatarLayer {
    val frame = Color(0xFF1F2937)
    val stroke = Stroke(width = 1.4f, cap = StrokeCap.Round)
    drawCircle(frame, radius = 4.6f, center = Offset(44.5f, 23f), style = stroke)
    drawCircle(frame, radius = 4.6f, center = Offset(55.5f, 23f), style = stroke)
    drawLine(frame, Offset(49.1f, 22.4f), Offset(50.9f, 22.4f), strokeWidth = 1.4f, cap = StrokeCap.Round)
    drawLine(frame, Offset(39.9f, 22.6f), Offset(36f, 21.8f), strokeWidth = 1.4f, cap = StrokeCap.Round)
    drawLine(frame, Offset(60.1f, 22.6f), Offset(64f, 21.8f), strokeWidth = 1.4f, cap = StrokeCap.Round)
}

/** Smart Watch — dark case with a teal screen on the left wrist. */
val AccSmartWatch = AvatarLayer {
    drawRoundRect(Color(0xFF1E293B), Offset(28.6f, 57.5f), Size(7.8f, 3.2f), CornerRadius(1.6f, 1.6f))
    drawRoundRect(Color(0xFF0F172A), Offset(30.3f, 56.4f), Size(4.4f, 5.4f), CornerRadius(1.4f, 1.4f))
    drawRoundRect(Color(0xFF5EEAD4), Offset(31.1f, 57.2f), Size(2.8f, 3.8f), CornerRadius(0.9f, 0.9f))
}

/** Fitness Band — slim violet band on the right wrist. */
val AccFitnessBand = AvatarLayer {
    drawRoundRect(Color(0xFF7C3AED), Offset(63.6f, 58f), Size(7.8f, 2.8f), CornerRadius(1.4f, 1.4f))
    drawRoundRect(Color(0xFFEDE9FE), Offset(66.5f, 58.6f), Size(2f, 1.6f), CornerRadius(0.6f, 0.6f))
}

/** Gold Fitness Band — premium gold band on the right wrist. */
val AccGoldBand = AvatarLayer {
    drawRoundRect(Color(0xFFF59E0B), Offset(63.6f, 58f), Size(7.8f, 3f), CornerRadius(1.5f, 1.5f))
    drawRoundRect(Color(0xFFFCD34D), Offset(66.3f, 58.6f), Size(2.4f, 1.8f), CornerRadius(0.7f, 0.7f))
}

/** Backpack — straps across the chest and a pack peeking out at the left. */
val AccBackpack = AvatarLayer {
    val packColor = Color(0xFF92400E)
    val strapColor = Color(0xFF78350F)
    drawRoundRect(packColor, Offset(25.5f, 44f), Size(7f, 16f), CornerRadius(3f, 3f))
    drawRoundRect(strapColor, Offset(27f, 47f), Size(4f, 2f), CornerRadius(1f, 1f))
    drawLine(strapColor, Offset(40f, 39f), Offset(45f, 64f), strokeWidth = 2.6f, cap = StrokeCap.Round)
    drawLine(strapColor, Offset(60f, 39f), Offset(55f, 64f), strokeWidth = 2.6f, cap = StrokeCap.Round)
}

/** Headphones — band over the head with two ear cups. */
val AccHeadphones = AvatarLayer {
    val shell = Color(0xFF334155)
    drawArc(
        color = shell,
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = false,
        topLeft = Offset(33.5f, 8f),
        size = Size(33f, 33f),
        style = Stroke(width = 2.6f, cap = StrokeCap.Round),
    )
    drawRoundRect(shell, Offset(32.2f, 19.5f), Size(5f, 9f), CornerRadius(2.4f, 2.4f))
    drawRoundRect(shell, Offset(62.8f, 19.5f), Size(5f, 9f), CornerRadius(2.4f, 2.4f))
    drawRoundRect(Color(0xFF0EA5E9), Offset(33.4f, 21.5f), Size(2.6f, 5f), CornerRadius(1.3f, 1.3f))
    drawRoundRect(Color(0xFF0EA5E9), Offset(64f, 21.5f), Size(2.6f, 5f), CornerRadius(1.3f, 1.3f))
}

private val crownPath = Path().apply {
    moveTo(40f, 10f)
    lineTo(40f, 3f)
    lineTo(45f, 6.6f)
    lineTo(50f, 1.6f)
    lineTo(55f, 6.6f)
    lineTo(60f, 3f)
    lineTo(60f, 10f)
    close()
}

/** Wellness Crown — the flagship gold cosmetic, worn above any hair. */
val AccCrown = AvatarLayer {
    drawPath(crownPath, Color(0xFFF59E0B))
    drawRoundRect(Color(0xFFD97706), Offset(40f, 8.4f), Size(20f, 1.8f), CornerRadius(0.9f, 0.9f))
    drawCircle(Color(0xFF14B8A6), radius = 1.4f, center = Offset(50f, 6.2f))
}
