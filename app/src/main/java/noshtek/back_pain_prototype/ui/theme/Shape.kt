package noshtek.back_pain_prototype.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// NEON AURORA shape scale — generous radii so dark panels read as smooth,
// machined holo-glass rather than paper cards.
val SpineIQShapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small      = RoundedCornerShape(14.dp),
    medium     = RoundedCornerShape(18.dp),
    large      = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp),
)

// Shared shapes referenced directly by custom components.
val CardShape      = RoundedCornerShape(24.dp)
val ButtonShape    = RoundedCornerShape(18.dp)
val HeroShape      = RoundedCornerShape(32.dp)
val ChipShape      = RoundedCornerShape(50)      // full-pill chips (DS 3.0 signature)
val TextFieldShape = RoundedCornerShape(16.dp)
val PillShape      = RoundedCornerShape(50)
val SheetShape     = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
val BadgeShape     = RoundedCornerShape(20.dp)   // achievement tiles
val DockShape      = RoundedCornerShape(32.dp)   // floating nav dock
