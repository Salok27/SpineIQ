package noshtek.back_pain_prototype.ui.theme

import androidx.compose.ui.graphics.Color

// ── AURA palette — Aura prototype (light-only) ──────────────────────────────
// Warm-paper surfaces under soft sage / sky / coral light: a calm, premium
// wellness identity (vs the V1 "Neon Aurora" dark neon). Token NAMES are kept
// identical to DS 3.0 so the whole UI re-skins by value alone — only the hues
// change. Clinical risk hues keep their green→amber→orange→red ordering and
// stay vivid/saturated so they never blend into the muted wellness accents.

// ── Warm-paper neutrals ──────────────────────────────────────────────────────
val DeepSpace    = Color(0xFFFBF8F3)   // app background (warm sand canvas)
val DeepSpaceLow = Color(0xFFF4EFE7)   // lowest container / splash window
val Panel        = Color(0xFFFFFFFF)   // surface (cards, sheets)
val PanelHigh    = Color(0xFFFBF7F1)   // raised surface / surfaceVariant
val PanelHigher  = Color(0xFFF1EBE1)   // chips, input fills, high containers
val Ink          = Color(0xFF2C2A26)   // overlay scrims (kept dark — scrim consumers)

// Warm near-black text ramp
val Starlight = Color(0xFF2A2724)      // primary text
val StarDim   = Color(0xFF6E665C)      // secondary text
val StarFaint = Color(0xFFA89F92)      // muted text / disabled

// Hairlines (warm-ink alpha now that surfaces are light)
val HairlineBright = Color(0x1F2A2724) // ~12% warm ink — outline
val Hairline       = Color(0x0F2A2724) // ~6% warm ink  — outlineVariant

// ── Wellness accents (sage / sky / coral — fill the cyan/indigo/magenta slots) ─
val Cyan      = Color(0xFF3E7A57)      // primary — deep sage (AA with white text)
val CyanDim   = Color(0xFF5E9C77)      // lighter sage — gradient / accent
val CyanText  = Color(0xFF2F6147)      // AA-safe sage text on light panels
val OnCyan    = Color(0xFFFFFFFF)      // text/icons on sage fills
val CyanContainer   = Color(0xFFE4EFE6)
val OnCyanContainer = Color(0xFF1E3A2C)

val IndigoGlow      = Color(0xFF5E92A8) // secondary — sky
val OnIndigo        = Color(0xFFFFFFFF)
val IndigoContainer = Color(0xFFE2EEF3)
val OnIndigoContainer = Color(0xFF22414C)

val Magenta         = Color(0xFFD87E63) // tertiary — coral
val OnMagenta       = Color(0xFFFFFFFF)
val MagentaContainer = Color(0xFFFBE7DF)
val OnMagentaContainer = Color(0xFF5A3024)

// ── Semantic colours (light-tuned) ───────────────────────────────────────────
val Success          = Color(0xFF3F9D6B)
val SuccessFill      = Color(0xFF4CAF7A)
val SuccessContainer = Color(0xFFE2F2E9)
val Warning          = Color(0xFFC98A2E)
val WarningFill      = Color(0xFFE5B04A)
val WarningContainer = Color(0xFFFBF0D8)
val Error            = Color(0xFFD2645A)
val ErrorFill        = Color(0xFFC9554B)
val ErrorContainer   = Color(0xFFFBE3E0)
val OnErrorContainer = Color(0xFF5A211C)

// ── Clinical risk tier colours (semantic — vivid + saturated on light) ───────
// Hue ordering green → amber → orange → red → deep red is the clinical source
// of truth. Kept MORE saturated than the muted wellness accents so a sage
// surface never reads as "low risk". NEVER mix with the reward palette.
val RiskLow              = Color(0xFF2E9E5B)
val RiskModerate         = Color(0xFFE0A93C)
val RiskModerateSevere   = Color(0xFFE8853D)
val RiskHigh             = Color(0xFFD85C4E)
val RiskSevereUrgent     = Color(0xFFC03A2E)

val RiskLowContainer             = Color(0xFFE1F2E7)
val RiskModerateContainer        = Color(0xFFFBF0D7)
val RiskModerateSevereContainer  = Color(0xFFFCE7D5)
val RiskHighContainer            = Color(0xFFFBE2DD)
val RiskSevereUrgentContainer    = Color(0xFFF8DAD4)

// ── Reward / progression palette (Violet family — engagement only) ──────────
// Aura's engagement accent (vitality milestones, streaks, celebrations): a
// COOL violet family, disjoint from the warm clinical risk ramp. NEVER used on
// risk tiers, SSS badges, or clinical chart lines.
val RewardViolet          = Color(0xFF7C5CD6)  // reward primary (white text)
val RewardVioletBright    = Color(0xFF5E42B0)  // AA-safe reward text on panels
val RewardVioletDeep      = Color(0xFFFFFFFF)  // on-reward (text on violet fills)
val RewardVioletContainer = Color(0xFFECE6FB)
val OnRewardVioletContainer = Color(0xFF2E2057)
val PurpleGlow            = Color(0xFF9D7CF0)  // reward gradient end

// ── Coin / streak accents (engagement only) ─────────────────────────────────
val CoinGold      = Color(0xFFE0A93C)  // amber-gold fill
val CoinGoldLight = Color(0xFFF5D98A)  // sheen highlight
val CoinGoldDeep  = Color(0xFFB97D1B)  // edge / embossing
val CoinText      = Color(0xFF9A6A12)  // AA-safe gold text on light panels
val CoinContainer = Color(0xFFFBF1D6)  // coin pill background
val StreakOrange  = Color(0xFFE8703A)  // flame fill (deep terracotta — distinct from risk orange)
val StreakEmber   = Color(0xFFC9531C)  // flame core
val StreakGlow    = Color(0xFFA8431A)  // streak text on light

// ── Spine vitality glow (Living Spine — cool, non-clinical) ─────────────────
// The Living Spine "heals" by brightening along a COOL slate→teal→aqua→radiant
// ramp. Deliberately NOT the clinical green→red ramp, so the glow never reads
// as a medical risk signal. Consumed directly by ui/common/LivingSpine.kt.
val SpineGlowDim     = Color(0xFF8FA6BC)  // dim cool slate (low vitality)
val SpineGlowMid     = Color(0xFF6FB6C2)  // teal
val SpineGlowBright  = Color(0xFF74D3B8)  // bright aqua
val SpineGlowRadiant = Color(0xFFCBEFDD)  // radiant mint-white (peak vitality)
