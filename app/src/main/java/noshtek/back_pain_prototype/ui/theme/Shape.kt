package noshtek.back_pain_prototype.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Design System 2.0 shape scale — larger radii read more premium and modern.
val SpineIQShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small      = RoundedCornerShape(12.dp),
    medium     = RoundedCornerShape(16.dp),
    large      = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

// Shared shapes referenced directly by custom components.
val CardShape      = RoundedCornerShape(20.dp)
val ButtonShape    = RoundedCornerShape(16.dp)   // spec: 16dp button corners
val HeroShape      = RoundedCornerShape(28.dp)
val ChipShape      = RoundedCornerShape(14.dp)
val TextFieldShape = RoundedCornerShape(14.dp)   // centralised text-field radius
val PillShape      = RoundedCornerShape(50)
val SheetShape     = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)  // bottom sheets
val BadgeShape     = RoundedCornerShape(18.dp)   // achievement tiles
