package noshtek.back_pain_prototype.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// AURA shape scale — large, soft radii so light cards read as calm, organic
// pebbles rather than sharp panels.
val SpineIQShapes = Shapes(
    extraSmall = RoundedCornerShape(16.dp),
    small      = RoundedCornerShape(20.dp),
    medium     = RoundedCornerShape(28.dp),
    large      = RoundedCornerShape(32.dp),
    extraLarge = RoundedCornerShape(40.dp),
)

// Shared shapes referenced directly by custom components.
val CardShape      = RoundedCornerShape(32.dp)
val ButtonShape    = RoundedCornerShape(28.dp)
val HeroShape      = RoundedCornerShape(40.dp)
val ChipShape      = RoundedCornerShape(50)      // full-pill chips (Aura signature)
val TextFieldShape = RoundedCornerShape(24.dp)
val PillShape      = RoundedCornerShape(50)
val SheetShape     = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)
val BadgeShape     = RoundedCornerShape(28.dp)   // milestone tiles
val DockShape      = RoundedCornerShape(40.dp)   // floating nav dock
