package noshtek.back_pain_prototype.ui.avatar

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color

// Head landmark: centre (50, 24), radius 15 → top cap arc box (35, 9) 30×30.

private val BrownHair = Color(0xFF6B4F3A)
private val DarkHair = Color(0xFF4E342E)
private val DeepHair = Color(0xFF3E2C23)
private val BandTeal = Color(0xFF14B8A6)

/** Short Hair (default) — clean top cap with short sideburns. */
val HairShort = AvatarLayer {
    drawArc(BrownHair, 180f, 180f, true, Offset(35f, 9f), Size(30f, 30f))
    drawRoundRect(BrownHair, Offset(35.2f, 21f), Size(3.4f, 6f), CornerRadius(1.6f, 1.6f))
    drawRoundRect(BrownHair, Offset(61.4f, 21f), Size(3.4f, 6f), CornerRadius(1.6f, 1.6f))
}

/** Long Hair — top cap with curtains falling to the shoulders. */
val HairLong = AvatarLayer {
    drawArc(DarkHair, 180f, 180f, true, Offset(34.5f, 8.5f), Size(31f, 31f))
    drawRoundRect(DarkHair, Offset(33.5f, 20f), Size(6.5f, 21f), CornerRadius(3.2f, 3.2f))
    drawRoundRect(DarkHair, Offset(60f, 20f), Size(6.5f, 21f), CornerRadius(3.2f, 3.2f))
}

/** Curly Hair — a cloud of overlapping curls around the crown. */
val HairCurly = AvatarLayer {
    drawCircle(DeepHair, radius = 5.4f, center = Offset(38.5f, 16f))
    drawCircle(DeepHair, radius = 6.0f, center = Offset(45f, 11.5f))
    drawCircle(DeepHair, radius = 6.2f, center = Offset(52.5f, 10.5f))
    drawCircle(DeepHair, radius = 5.8f, center = Offset(59.5f, 13.5f))
    drawCircle(DeepHair, radius = 5.0f, center = Offset(63f, 19.5f))
    drawCircle(DeepHair, radius = 5.0f, center = Offset(36f, 21f))
    drawCircle(DeepHair, radius = 5.6f, center = Offset(49f, 14f))
}

/** Athletic Hair — tight buzz cap with a teal sweatband. */
val HairAthletic = AvatarLayer {
    drawArc(DarkHair, 180f, 180f, true, Offset(36f, 10f), Size(28f, 28f))
    drawRoundRect(BandTeal, Offset(36.5f, 15.5f), Size(27f, 4f), CornerRadius(2f, 2f))
}
