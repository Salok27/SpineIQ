package noshtek.back_pain_prototype.ui.avatar

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke

// Shared mascot palette — fixed colours (layers are not composable, and
// cosmetics keep their identity in light and dark theme).
internal val SkinTone = Color(0xFFE8B486)
internal val SkinShade = Color(0xFFD49A6A)
internal val FaceLine = Color(0xFF1F2937)
internal val ShoeDark = Color(0xFF1E293B)
internal val ShoeSole = Color(0xFFE2E8F0)

private val smile = Path().apply {
    moveTo(45.5f, 28.5f)
    quadraticTo(50f, 32.5f, 54.5f, 28.5f)
}

/**
 * Base gender-neutral mascot: slightly oversized round head, dot eyes, soft
 * smile, capsule limbs. Drawn first; cosmetic layers stack on top in the
 * order bottoms → tops → hair → accessories.
 *
 * Unit-space landmarks (0..100): head centre (50, 24) r15 · neck (46.5..53.5,
 * 33..40) · torso (36..64, 38..69) · arms x 29..36 / 64..71, y 42..65 ·
 * legs x 41..48.5 / 51.5..59, y 66..92 · shoes y 90..96.
 */
val BodyLayer = AvatarLayer {
    // Legs (skin shows below shorts)
    drawRoundRect(SkinTone, Offset(41f, 66f), Size(7.5f, 26f), CornerRadius(3.75f, 3.75f))
    drawRoundRect(SkinTone, Offset(51.5f, 66f), Size(7.5f, 26f), CornerRadius(3.75f, 3.75f))
    // Shoes
    drawRoundRect(ShoeDark, Offset(38.5f, 90f), Size(11f, 6f), CornerRadius(3f, 3f))
    drawRoundRect(ShoeDark, Offset(50.5f, 90f), Size(11f, 6f), CornerRadius(3f, 3f))
    drawRoundRect(ShoeSole, Offset(38.5f, 94f), Size(11f, 2f), CornerRadius(1f, 1f))
    drawRoundRect(ShoeSole, Offset(50.5f, 94f), Size(11f, 2f), CornerRadius(1f, 1f))
    // Arms
    drawRoundRect(SkinTone, Offset(29f, 42f), Size(7f, 23f), CornerRadius(3.5f, 3.5f))
    drawRoundRect(SkinTone, Offset(64f, 42f), Size(7f, 23f), CornerRadius(3.5f, 3.5f))
    // Torso
    drawRoundRect(SkinTone, Offset(36f, 38f), Size(28f, 31f), CornerRadius(10f, 10f))
    // Neck
    drawRoundRect(SkinShade, Offset(46.5f, 33f), Size(7f, 8f), CornerRadius(2f, 2f))
    // Head
    drawCircle(SkinTone, radius = 15f, center = Offset(50f, 24f))
    // Ears
    drawCircle(SkinShade, radius = 2.2f, center = Offset(35.5f, 24.5f))
    drawCircle(SkinShade, radius = 2.2f, center = Offset(64.5f, 24.5f))
    // Eyes
    drawCircle(FaceLine, radius = 1.7f, center = Offset(44.5f, 23f))
    drawCircle(FaceLine, radius = 1.7f, center = Offset(55.5f, 23f))
    // Smile
    drawPath(smile, FaceLine, style = Stroke(width = 1.3f, cap = StrokeCap.Round))
}
