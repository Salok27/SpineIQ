package noshtek.back_pain_prototype.ui.avatar

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope

// Torso landmark: (36..64, 38..69); arms x 29..36 / 64..71 from y 42.

private fun DrawScope.shirtBase(color: Color, sleeveLength: Float) {
    // Sleeves first so the torso panel overlaps the join.
    drawRoundRect(color, Offset(28.5f, 41f), Size(8f, sleeveLength), CornerRadius(4f, 4f))
    drawRoundRect(color, Offset(63.5f, 41f), Size(8f, sleeveLength), CornerRadius(4f, 4f))
    drawRoundRect(color, Offset(35f, 37.5f), Size(30f, 28f), CornerRadius(9f, 9f))
}

private fun DrawScope.collar(color: Color) {
    drawArc(color, 0f, 180f, true, Offset(44.5f, 35f), Size(11f, 7f))
}

/** T-Shirt (default) — brand blue, short sleeves. */
val TopTShirt = AvatarLayer {
    shirtBase(Color(0xFF3B82F6), sleeveLength = 13f)
    collar(Color(0xFF2563EB))
}

/** Athletic Shirt — teal with a white performance stripe. */
val TopAthletic = AvatarLayer {
    shirtBase(Color(0xFF14B8A6), sleeveLength = 12f)
    collar(Color(0xFF0F766E))
    drawRoundRect(Color.White.copy(alpha = 0.85f), Offset(38f, 47f), Size(24f, 2.4f), CornerRadius(1.2f, 1.2f))
    drawRoundRect(Color.White.copy(alpha = 0.5f), Offset(38f, 51f), Size(24f, 1.6f), CornerRadius(0.8f, 0.8f))
}

/** Hoodie — slate with kangaroo pocket, drawstrings and a hood roll. */
val TopHoodie = AvatarLayer {
    val body = Color(0xFF64748B)
    val shade = Color(0xFF475569)
    shirtBase(body, sleeveLength = 23f)
    // Hood roll around the neck
    drawRoundRect(shade, Offset(40f, 34.5f), Size(20f, 6.5f), CornerRadius(3.2f, 3.2f))
    // Kangaroo pocket
    drawRoundRect(shade, Offset(42f, 54f), Size(16f, 9f), CornerRadius(3.5f, 3.5f))
    // Drawstrings
    drawLine(Color.White, Offset(47f, 41f), Offset(46.4f, 47.5f), strokeWidth = 1.1f, cap = StrokeCap.Round)
    drawLine(Color.White, Offset(53f, 41f), Offset(53.6f, 47.5f), strokeWidth = 1.1f, cap = StrokeCap.Round)
}

/** Wellness Jacket — sky blue, zip and stand collar. */
val TopWellnessJacket = AvatarLayer {
    val body = Color(0xFF0EA5E9)
    shirtBase(body, sleeveLength = 23f)
    drawRoundRect(Color(0xFF0284C7), Offset(43f, 34.5f), Size(14f, 5f), CornerRadius(2.4f, 2.4f))
    drawLine(Color.White, Offset(50f, 39f), Offset(50f, 64f), strokeWidth = 1.4f, cap = StrokeCap.Round)
    // Zip pull
    drawCircle(Color.White, radius = 1.3f, center = Offset(50f, 44f))
}

/** Neon Hoodie — high-vis lime with a deep pocket. */
val TopNeonHoodie = AvatarLayer {
    val body = Color(0xFFA3E635)
    val shade = Color(0xFF65A30D)
    shirtBase(body, sleeveLength = 23f)
    drawRoundRect(shade, Offset(40f, 34.5f), Size(20f, 6.5f), CornerRadius(3.2f, 3.2f))
    drawRoundRect(shade, Offset(42f, 54f), Size(16f, 9f), CornerRadius(3.5f, 3.5f))
    drawLine(Color(0xFF365314), Offset(47f, 41f), Offset(46.4f, 47.5f), strokeWidth = 1.1f, cap = StrokeCap.Round)
    drawLine(Color(0xFF365314), Offset(53f, 41f), Offset(53.6f, 47.5f), strokeWidth = 1.1f, cap = StrokeCap.Round)
}

/** Champion Jacket — reward violet with gold trim. */
val TopChampionJacket = AvatarLayer {
    val body = Color(0xFF7C3AED)
    val gold = Color(0xFFF59E0B)
    shirtBase(body, sleeveLength = 23f)
    drawRoundRect(Color(0xFF6D28D9), Offset(43f, 34.5f), Size(14f, 5f), CornerRadius(2.4f, 2.4f))
    drawLine(gold, Offset(50f, 39f), Offset(50f, 64f), strokeWidth = 1.5f, cap = StrokeCap.Round)
    // Gold cuffs
    drawRoundRect(gold, Offset(28.5f, 60f), Size(8f, 3f), CornerRadius(1.5f, 1.5f))
    drawRoundRect(gold, Offset(63.5f, 60f), Size(8f, 3f), CornerRadius(1.5f, 1.5f))
    // Champion star
    drawCircle(gold, radius = 2.6f, center = Offset(43f, 45f))
}

/** Recovery Master Outfit — indigo with a white chevron crest. */
val TopRecoveryOutfit = AvatarLayer {
    val body = Color(0xFF6366F1)
    shirtBase(body, sleeveLength = 23f)
    collar(Color(0xFF4338CA))
    // Chevron crest
    drawLine(Color.White, Offset(44f, 52f), Offset(50f, 46.5f), strokeWidth = 2f, cap = StrokeCap.Round)
    drawLine(Color.White, Offset(50f, 46.5f), Offset(56f, 52f), strokeWidth = 2f, cap = StrokeCap.Round)
    drawLine(Color.White.copy(alpha = 0.6f), Offset(44f, 57f), Offset(50f, 51.5f), strokeWidth = 2f, cap = StrokeCap.Round)
    drawLine(Color.White.copy(alpha = 0.6f), Offset(50f, 51.5f), Offset(56f, 57f), strokeWidth = 2f, cap = StrokeCap.Round)
}
