# Domain Knowledge

SpineIQ is a **direct-to-consumer** back pain self-assessment and spine health
tracking app. The user assesses themselves — there is no clinician, no patient
roster, and no clinic operator. The app is single-user ("Personal Health App"):
one installation, one user profile. It is a screening/decision-support tool,
**not** a diagnostic or treatment tool — it never recommends specific treatments.

The canonical specification is `PROJECT_PLAN.md` at the repo root. Code comments
cite its section numbers (e.g. "Section 10.3", "OQ-06"). When a rule is ambiguous,
that document is authoritative.

Pipeline: **Measure → Assess → Score → Classify → Educate**.

## Glossary

| Term | Meaning |
|---|---|
| **SSS** | Spine Severity System — the 0–11 personal severity score (Dr. Ayush Sharma's methodology). |
| **VAS** | Visual Analogue Scale, 0–10 self-reported pain intensity. |
| **ODI** | Oswestry Disability Index. This app uses the **SSS Modified ODI**: 5 activities (Walking, Sitting, Standing, Sleep, Daily Activities), each 0–2, total 0–10. *Not* the standard 10-activity ODI (OQ-04). |
| **Radiculopathy** | Nerve-root pain radiating into the leg (sciatica). Scored 0–3. |
| **Chronicity** | How long pain has persisted: Acute (<3wk), Subacute (3–6wk), Chronic (>6wk). |
| **Red flag** | A symptom suggesting serious pathology (cancer, infection, cauda equina, etc.). Any positive flag overrides everything and prompts the user to seek immediate medical attention. |
| **Lifestyle / Daily Habit risk** | Three-tier (Low/Moderate/High) score from sitting, walking, exercise, sleep — age-adjusted. |
| **Age group** | Young Adult (≤30), Mid Adult (31–45), Pre-Senior (46–60), Senior (61+). Drives thresholds. |
| **Composite Back Pain Risk** | Final classification combining SSS score × Lifestyle tier. |
| **UserProfile** | The single user profile record (one per installation). Renamed from `PatientProfile` in v1.1 — there is no patient management in this app. |
| **Spine Coins** | V2 spendable virtual currency earned through engagement; spent only on avatar cosmetics. Never gates health functionality. |
| **XP / Level** | V2 lifetime experience points (monotonic, never spent) mapped to 8 named levels (Beginner → Back Health Expert). Level is derived from XP, never stored. |
| **Streak** | Consecutive days with a qualifying engagement (daily check-in or assessment completion — *not* per-step saves). Milestones at 3/7/14/30 days pay one-time coin bonuses. |
| **Daily check-in** | Once-per-calendar-day mood log (Better/Same/Worse) that earns a small reward and keeps the streak alive. |
| **Achievement** | Code-defined collectible badge with coin/XP reward; only unlock state is persisted. |
| **Reward ledger** | Append-only grant/spend log whose dedupe-key primary key makes every coin/XP grant idempotent. |

## SSS scoring (total 0–11) — `SssScorer.kt`

| Component | Rule | Points |
|---|---|---|
| VAS pain | 0–3 / 4–6 / 7–10 | 0 / 1 / 2 |
| Radiculopathy | none / mild / moderate / severe | 0 / 1 / 2 / 3 |
| ODI (modified) | total 0–2 / 3–5 / 6–10 | 0 / 1 / 2 |
| BMI | <25 / 25–29.9 / ≥30 | 0 / 1 / 2 |
| Chronicity | acute / subacute / chronic | 0 / 1 / 2 |
| **Red flag** | any flag present | **sets total = 11** |

SSS severity tiers: 0–3 Low, 4–6 Mild-Moderate, 7–9 Moderate-Severe, 10–11
Severe/High Risk. `rawSSSScore` (sum before override) is kept for audit alongside
the displayed `totalSSSScore`.

## Lifestyle scoring (3-tier) — `LifestyleScorer.kt`

Four components each scored Low/Moderate/High, then aggregated:
- **High** if *any* component is High; else **Moderate** if any is Moderate; else **Low**.

Component rules:
- **Sitting / Walking** use age-adjusted thresholds in `AgeThresholds.kt`. Low and
  High anchors come from the source "good"/"high risk" columns; the Moderate band
  is interpolated between them (OQ-06).
- **Exercise frequency**: ≥3 days Low, 1–2 Moderate, 0 High.
- **Sleep hours**: ≥7 Low, 5–6 Moderate, <5 High.

Two modifiers (each shifts a tier by one step, never an absolute penalty):
- **Sleep quality** (Poor/Fair/Good/Excellent): Fair/Poor → step toward High;
  Excellent → step toward Low; Good → no change. Applied to the sleep component.
- **Exercise type**: a high-impact / spine-loading type (e.g. Running, heavy
  Gym/Weights) steps the exercise component one toward High (OQ-18). See
  `ExerciseType.isHighImpact`.

## Composite Back Pain Risk — `ScoringEngine.classifyBackPainRisk()`

The SSS score × Lifestyle tier matrix (Section 10.3). Key rules:
- Red flag **or** SSS ≥ 10 → `SEVERE_URGENT`.
- SSS 7–9 → `HIGH` (regardless of lifestyle).
- SSS 4–6 → Mild-Moderate / Moderate / Moderate-High by lifestyle tier.
- SSS 0–3 → Low / Low-Moderate / Moderate by lifestyle tier.

## Red flag override (safety-critical)

If **any** of the 8 red-flag items is Yes (`RedFlagDraft.hasAnyRedFlag`):
component scores are still computed and stored, but the displayed total is forced
to 11, severity to Severe/High Risk. The report and Results screen must display a
prominent, consumer-facing message: **"Please seek immediate medical attention
from a doctor or emergency department. Do not delay."**

This language must never reference "clinical evaluation", "urgent referral", or
assume a clinician is present — the user is reading this themselves. This must
never be silently weakened.

## Gamification economy (V2) — `Economy.kt`, FR-21 – FR-26

| Action | Coins | XP |
|---|---|---|
| Complete a wizard section (×5 per assessment) | +10 | +20 |
| Complete an assessment | +50 | +100 |
| **Full assessment total** | **+100** | **+200** |
| Daily check-in (once per calendar day) | +5 | +15 |
| Streak milestone 3 / 7 / 14 / 30 days (one-time ever) | +50 / +100 / +150 / +300 | — |
| Achievements | per catalog | per catalog |

Levels (derived from lifetime XP, never persisted): Beginner 0 · Explorer 100 ·
Spine Explorer 250 · Recovery Champion 500 · Wellness Warrior 1000 ·
Mobility Master 2000 · Spine Guardian 3500 · Back Health Expert 5500.

Invariants that must never be weakened:
- **Idempotency** — every grant goes through the reward ledger
  (`GamificationRepository.tryGrant` + `DedupeKeys`); repeating an action never
  pays twice. Re-saving a wizard section via back-navigation earns 0 extra.
- **Cosmetics only** — coins buy avatar items and nothing else; no coin price,
  level, or achievement may gate any assessment, score, or report feature.
- **Never blocks the medical flow** — gamification calls in the assessment path
  are failure-isolated (`runCatching`); a grant error must not stop a save.
- **Clinical color separation** — the violet/gold reward palette never appears
  on risk tiers, SSS badges, or clinical chart lines.
- **Streak honesty** — only check-ins and completions qualify; missed days show
  0 immediately (lazy reset); milestones are one-time-ever.

Note: the original V2 requirement brief stated the check-in reward
inconsistently ("+15 coins" vs "+5 coins / +15 XP"); **+5 coins / +15 XP is
canonical**, keeping assessments the dominant coin source.

## Report sections

The report has 13 fixed sections (Your Summary → Recommended Next Steps →
Lifestyle Improvement Tips), defined in `PROJECT_PLAN.md` §12 and rendered in
`PdfExporter.kt`. All report narrative is written in **second person** ("your
pain", "your risk"). The "Probable Contributors" and "Lifestyle Improvement Tips"
sections are produced by a static rules engine (no AI) and must reference only
data collected in the assessment.

## Phase 1 scope boundaries (do not implement without confirmation)

In scope for Phase 1: onboarding flow (3 screens), push notification reminders,
Progress / Trends screen (SSS and lifestyle trend charts), Lifestyle Improvement
Tips in the report, single-user profile, public Play Store distribution, and —
since V2 (2026-06-12) — the gamification layer (Spine Coins, XP/levels, streaks,
daily check-ins, achievements, avatar + shop, celebrations, bottom navigation;
FR-21 – FR-26).

Explicitly **out of scope** (see `PROJECT_PLAN.md` §16): body-diagram pain input
(text checklist only), LLM-generated narrative, cloud sync / portal, iOS, multi-
user or clinic-mode, doctor-sharing portal, social sharing, user-defined goal
setting (V2 ships fixed daily goals only).

Health Connect is in scope but currently unimplemented (see `development_workflows.md`).
