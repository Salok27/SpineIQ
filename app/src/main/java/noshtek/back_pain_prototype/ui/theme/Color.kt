package noshtek.back_pain_prototype.ui.theme

import androidx.compose.ui.graphics.Color

// ── Brand palette ────────────────────────────────────────────────────────────
// Light scheme
val Blue600       = Color(0xFF2563EB)
val Blue50        = Color(0xFFDBEAFE)
val Blue900       = Color(0xFF1E3A8A)
val Teal600       = Color(0xFF0D9488)
val Teal50        = Color(0xFFCCFBF1)
val Teal900       = Color(0xFF003731)
val Green600      = Color(0xFF16A34A)
val Green50       = Color(0xFFDCFCE7)
val Green900      = Color(0xFF052E16)
val Slate50       = Color(0xFFF8FAFC)
val Slate100      = Color(0xFFF1F5F9)
val Slate200      = Color(0xFFE2E8F0)
val Slate300      = Color(0xFFCBD5E1)
val Slate500      = Color(0xFF64748B)
val Slate900      = Color(0xFF1E293B)

// Dark scheme
val Blue300       = Color(0xFF93C5FD)
val Blue800       = Color(0xFF1D4ED8)
val BlueOnDark    = Color(0xFF1D3461)
val Teal300       = Color(0xFF5EEAD4)
val Teal700       = Color(0xFF0F766E)
val Green300      = Color(0xFF86EFAC)
val Green700      = Color(0xFF15803D)
val SlateD50      = Color(0xFF162032)
val SlateD100     = Color(0xFF1E293B)
val SlateD200     = Color(0xFF263548)
val SlateD300     = Color(0xFF1E3A5F)
val SlateD500     = Color(0xFF475569)
val SlateD600     = Color(0xFF94A3B8)
val SlateD700     = Color(0xFF334155)
val SlateD900     = Color(0xFF0F172A)

// ── Risk tier colours (semantic, used in SpineIQComponents) ──────────────────
// Refined from original palette for WCAG AA compliance on white backgrounds
val RiskLow              = Color(0xFF16A34A)   // was #4CAF50
val RiskModerate         = Color(0xFFD97706)   // was #FF9800 (amber, better contrast)
val RiskModerateSevere   = Color(0xFFEA580C)   // was #FF5722
val RiskHigh             = Color(0xFFDC2626)   // was #F44336
val RiskSevereUrgent     = Color(0xFF991B1B)   // was #B71C1C

val RiskLowContainer             = Color(0xFFDCFCE7)
val RiskModerateContainer        = Color(0xFFFEF3C7)
val RiskModerateSevereContainer  = Color(0xFFFED7AA)
val RiskHighContainer            = Color(0xFFFEE2E2)
val RiskSevereUrgentContainer    = Color(0xFFFECACA)

// ── Signature accent: indigo → violet (Design System 2.0) ────────────────────
// Blue stays the trustworthy base; indigo/violet add the modern "premium" pop,
// used for the brand gradient, key CTAs, focus states and data highlights.
// Light
val Indigo600     = Color(0xFF4F46E5)
val Indigo50      = Color(0xFFE0E7FF)
val Indigo900     = Color(0xFF312E81)
val Violet600     = Color(0xFF7C3AED)
val Violet50      = Color(0xFFEDE9FE)
val Violet900     = Color(0xFF4C1D95)
// Mid stops used for gradients on dark surfaces (more luminous)
val Blue500       = Color(0xFF3B82F6)
val Indigo500     = Color(0xFF6366F1)
val Violet500     = Color(0xFF8B5CF6)
// Dark scheme accent
val Indigo300     = Color(0xFFA5B4FC)
val Indigo700     = Color(0xFF4338CA)
val Violet300     = Color(0xFFC4B5FD)
val Violet700     = Color(0xFF6D28D9)

// ── Semantic success / warning (explicit, reuse risk hues) ───────────────────
val Success           = Color(0xFF16A34A)
val SuccessContainer  = Color(0xFFDCFCE7)
val Warning           = Color(0xFFD97706)
val WarningContainer  = Color(0xFFFEF3C7)
