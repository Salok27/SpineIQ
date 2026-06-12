package noshtek.back_pain_prototype.ui.avatar

import androidx.compose.ui.graphics.drawscope.DrawScope
import noshtek.back_pain_prototype.core.data.gamification.AvatarCategory

/**
 * What the avatar is wearing: equipped catalog item id per category.
 * Categories absent from the map render the free default (or nothing, for
 * accessories). Built from the avatar_items DB rows by the ViewModels.
 */
data class AvatarSpec(
    val equipped: Map<AvatarCategory, String> = emptyMap(),
) {
    companion object {
        val Default = AvatarSpec()
    }
}

/**
 * One drawable cosmetic layer. Implementations draw in a fixed 0..100 unit
 * space (the renderer applies the scale transform), so every item works at
 * any avatar size with no per-size code.
 */
fun interface AvatarLayer {
    fun DrawScope.draw()
}
