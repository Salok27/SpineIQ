package noshtek.back_pain_prototype.ui.avatar

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope

// Legs landmark: x 41..48.5 / 51.5..59, y 66..92; hip joins torso at y 64..69.

private fun DrawScope.hipBlock(color: Color) {
    drawRoundRect(color, Offset(39.5f, 63f), Size(21f, 9f), CornerRadius(4f, 4f))
}

/** Joggers (default) — slate with cuffs. */
val BottomJoggers = AvatarLayer {
    val body = Color(0xFF475569)
    hipBlock(body)
    drawRoundRect(body, Offset(40.5f, 66f), Size(8.5f, 23f), CornerRadius(4f, 4f))
    drawRoundRect(body, Offset(51f, 66f), Size(8.5f, 23f), CornerRadius(4f, 4f))
    // Cuffs
    drawRoundRect(Color(0xFF334155), Offset(41f, 86.5f), Size(7.5f, 3f), CornerRadius(1.5f, 1.5f))
    drawRoundRect(Color(0xFF334155), Offset(51.5f, 86.5f), Size(7.5f, 3f), CornerRadius(1.5f, 1.5f))
}

/** Athletic Shorts — brand blue, knee length (skin shows below). */
val BottomShorts = AvatarLayer {
    val body = Color(0xFF2563EB)
    hipBlock(body)
    drawRoundRect(body, Offset(40f, 66f), Size(9f, 13f), CornerRadius(3.5f, 3.5f))
    drawRoundRect(body, Offset(51f, 66f), Size(9f, 13f), CornerRadius(3.5f, 3.5f))
    drawRoundRect(Color.White.copy(alpha = 0.8f), Offset(40.8f, 76.5f), Size(7.4f, 1.6f), CornerRadius(0.8f, 0.8f))
    drawRoundRect(Color.White.copy(alpha = 0.8f), Offset(51.8f, 76.5f), Size(7.4f, 1.6f), CornerRadius(0.8f, 0.8f))
}

/** Fitness Pants — deep teal, slim, with a side performance stripe. */
val BottomFitnessPants = AvatarLayer {
    val body = Color(0xFF0F766E)
    hipBlock(body)
    drawRoundRect(body, Offset(41f, 66f), Size(7.8f, 24f), CornerRadius(3.6f, 3.6f))
    drawRoundRect(body, Offset(51.2f, 66f), Size(7.8f, 24f), CornerRadius(3.6f, 3.6f))
    // Side stripes
    drawRoundRect(Color(0xFF5EEAD4), Offset(41.8f, 67f), Size(1.5f, 21f), CornerRadius(0.75f, 0.75f))
    drawRoundRect(Color(0xFF5EEAD4), Offset(56.7f, 67f), Size(1.5f, 21f), CornerRadius(0.75f, 0.75f))
}
