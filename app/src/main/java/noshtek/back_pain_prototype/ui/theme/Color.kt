package noshtek.back_pain_prototype.ui.theme

import androidx.compose.ui.graphics.Color

// ── Brand palette: Blue (primary) + Teal (accent) — Design System 2.1 ────────
// Blue is the trustworthy clinical base; teal is the modern, calming accent.
// The signature gradient sweeps blue → sky → teal (no indigo/violet).

// Light scheme
val Blue600   = Color(0xFF2563EB)   // primary
val Blue50    = Color(0xFFDBEAFE)   // primary container
val Blue900   = Color(0xFF1E3A8A)   // on-primary-container / PDF title navy
val Blue500   = Color(0xFF3B82F6)   // gradient mid stop (dark surfaces)

val Teal600   = Color(0xFF14B8A6)   // accent (spec) — fills, containers, gradient end
val Teal50    = Color(0xFFCCFBF1)   // accent container
val Teal700   = Color(0xFF0F766E)   // teal text/icons on white (WCAG AA-safe)
val Teal900   = Color(0xFF042F2A)   // on-accent-container

val Sky500    = Color(0xFF0EA5E9)   // gradient mid stop + tertiary accent (2nd chart line)
val Sky100    = Color(0xFFE0F2FE)   // tertiary container
val Sky900    = Color(0xFF0C4A6E)   // on-tertiary-container

// Neutral foundation (Slate)
val Ink        = Color(0xFF0F172A)  // primary text (spec)
val SlateText  = Color(0xFF475569)  // secondary text (spec) — onSurfaceVariant
val SlateMuted = Color(0xFF64748B)  // muted text (spec)
val Slate50    = Color(0xFFF8FAFC)  // background
val Slate100   = Color(0xFFF1F5F9)  // surface elevated
val Slate200   = Color(0xFFE2E8F0)  // outline variant
val Slate300   = Color(0xFFCBD5E1)  // outline (spec)

// Dark scheme neutrals
val SlateD50   = Color(0xFF162032)
val SlateD100  = Color(0xFF1E293B)
val SlateD200  = Color(0xFF263548)
val SlateD500  = Color(0xFF475569)
val SlateD600  = Color(0xFF94A3B8)
val SlateD700  = Color(0xFF334155)
val SlateD900  = Color(0xFF0F172A)
// Dark scheme accents (more luminous for dark surfaces)
val Blue300    = Color(0xFF93C5FD)
val Blue800    = Color(0xFF1D4ED8)
val BlueOnDark = Color(0xFF1D3461)
val Teal300    = Color(0xFF5EEAD4)
val Teal800    = Color(0xFF115E59)
val Sky400     = Color(0xFF38BDF8)

// ── Semantic colours (AA-tuned: bright hue = fills, darker hue = text/icons) ─
// Spec hues (#10B981 / #F59E0B / #EF4444) are kept for large fills & indicators,
// but text/icon variants are darkened so they pass WCAG AA on white surfaces.
val Success          = Color(0xFF059669)  // text/icon on white (AA)
val SuccessFill      = Color(0xFF10B981)  // spec bright — fills/indicators
val SuccessContainer = Color(0xFFD1FAE5)
val Warning          = Color(0xFFB45309)  // text/icon on white (AA)
val WarningFill      = Color(0xFFF59E0B)  // spec bright — fills
val WarningContainer = Color(0xFFFEF3C7)
val Error            = Color(0xFFDC2626)  // text/border on white (AA)
val ErrorFill        = Color(0xFFEF4444)  // spec bright — fills
val ErrorContainer   = Color(0xFFFEE2E2)

// ── Clinical risk tier colours (semantic, used in SpineIQComponents) ─────────
// Already WCAG AA-tuned for white backgrounds. UNCHANGED — clinical source of truth.
val RiskLow              = Color(0xFF16A34A)
val RiskModerate         = Color(0xFFD97706)
val RiskModerateSevere   = Color(0xFFEA580C)
val RiskHigh             = Color(0xFFDC2626)
val RiskSevereUrgent     = Color(0xFF991B1B)

val RiskLowContainer             = Color(0xFFDCFCE7)
val RiskModerateContainer        = Color(0xFFFEF3C7)
val RiskModerateSevereContainer  = Color(0xFFFED7AA)
val RiskHighContainer            = Color(0xFFFEE2E2)
val RiskSevereUrgentContainer    = Color(0xFFFECACA)
