package noshtek.back_pain_prototype.ui.theme

import androidx.compose.ui.graphics.Color

// ── NEON AURORA palette — Design System 3.0 (dark-only) ─────────────────────
// Deep-space ink surfaces lit by an aurora sweeping cyan → indigo → magenta.
// Rewards keep their violet/gold identity; clinical risk hues keep their
// semantic green→amber→orange→red ordering, re-tuned to stay luminous and
// AA-legible on dark panels.

// ── Deep-space neutrals ──────────────────────────────────────────────────────
val DeepSpace    = Color(0xFF060B18)   // app background
val DeepSpaceLow = Color(0xFF04070F)   // lowest container / splash window
val Panel        = Color(0xFF0C1426)   // surface (cards, sheets)
val PanelHigh    = Color(0xFF131C33)   // raised surface / surfaceVariant
val PanelHigher  = Color(0xFF1A2542)   // chips, input fills, high containers
val Ink          = Color(0xFF02040A)   // overlay scrims (kept name — overlay consumers)

// Starlight text ramp
val Starlight = Color(0xFFEAF2FF)      // primary text
val StarDim   = Color(0xFF93A5C8)      // secondary text
val StarFaint = Color(0xFF5A6B8C)      // muted text / disabled

// Hairlines (gradient borders are built from these in Gradients.kt)
val HairlineBright = Color(0x29FFFFFF) // 16% white — outline
val Hairline       = Color(0x14FFFFFF) // 8% white  — outlineVariant

// ── Aurora accents ───────────────────────────────────────────────────────────
val Cyan      = Color(0xFF38E1FF)      // primary — electric cyan
val CyanDim   = Color(0xFF22D3EE)      // aurora gradient start / accent
val CyanText  = Color(0xFF7DE9FF)      // AA-safe cyan for small text on panels
val OnCyan    = Color(0xFF052433)      // text/icons on cyan fills
val CyanContainer   = Color(0xFF0B3A4F)
val OnCyanContainer = Color(0xFFC8F4FF)

val IndigoGlow      = Color(0xFF818CF8) // aurora mid stop / secondary
val OnIndigo        = Color(0xFF101542)
val IndigoContainer = Color(0xFF1E2660)
val OnIndigoContainer = Color(0xFFDDE1FF)

val Magenta         = Color(0xFFE879F9) // aurora end stop / tertiary
val OnMagenta       = Color(0xFF3A0E44)
val MagentaContainer = Color(0xFF471A54)
val OnMagentaContainer = Color(0xFFFAD7FF)

// ── Semantic colours (dark-tuned: luminous hue = text/fills on dark panels) ──
val Success          = Color(0xFF34D399)
val SuccessFill      = Color(0xFF34D399)
val SuccessContainer = Color(0xFF0C3A2A)
val Warning          = Color(0xFFFBBF24)
val WarningFill      = Color(0xFFFBBF24)
val WarningContainer = Color(0xFF2E2208)
val Error            = Color(0xFFF87171)
val ErrorFill        = Color(0xFFEF4444)
val ErrorContainer   = Color(0xFF3A1320)
val OnErrorContainer = Color(0xFFFECACA)

// ── Clinical risk tier colours (semantic, used in SpineIQComponents) ─────────
// Hue ordering green → amber → orange → red → deep red is the clinical source
// of truth. Values are the luminous dark-surface variants; containers are
// solid dark tints of the same hue. NEVER mix with the reward palette.
val RiskLow              = Color(0xFF4ADE80)
val RiskModerate         = Color(0xFFFBBF24)
val RiskModerateSevere   = Color(0xFFFB923C)
val RiskHigh             = Color(0xFFF87171)
val RiskSevereUrgent     = Color(0xFFEF4444)

val RiskLowContainer             = Color(0xFF0D2B1D)
val RiskModerateContainer        = Color(0xFF2E2208)
val RiskModerateSevereContainer  = Color(0xFF331B07)
val RiskHighContainer            = Color(0xFF331114)
val RiskSevereUrgentContainer    = Color(0xFF3A0D11)

// ── Reward / progression palette (Violet family — gamification only) ────────
// Strictly for XP, levels, achievements, shop and celebrations. NEVER used on
// clinical risk tiers, SSS badges, or chart clinical lines.
val RewardViolet          = Color(0xFFA78BFA)  // reward primary on dark
val RewardVioletBright    = Color(0xFFC4B5FD)  // AA-safe reward text on panels
val RewardVioletDeep      = Color(0xFF221148)  // on-reward (text on violet fills)
val RewardVioletContainer = Color(0xFF2A1D4E)
val OnRewardVioletContainer = Color(0xFFEDE9FE)
val PurpleGlow            = Color(0xFFC084FC)  // reward gradient end

// ── Coin / streak accents (gamification only) ───────────────────────────────
val CoinGold      = Color(0xFFFBBF24)  // coin fill
val CoinGoldLight = Color(0xFFFDE68A)  // coin sheen highlight
val CoinGoldDeep  = Color(0xFFD97706)  // coin edge / embossing
val CoinText      = Color(0xFFFCD34D)  // AA-safe gold text on dark panels
val CoinContainer = Color(0xFF2E2108)  // coin pill background
val StreakOrange  = Color(0xFFF97316)  // flame fill
val StreakEmber   = Color(0xFFEA580C)  // flame core
val StreakGlow    = Color(0xFFFDBA74)  // streak text on dark
