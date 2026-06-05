# Domain Knowledge

SpineIQ is a **clinician-led** back pain screening tool. A physiotherapist or
doctor conducts a structured assessment *on behalf of* a patient during an OPD
consultation; the app is multi-patient ("Clinic Mode"), single-operator. It is a
screening/decision-support tool, **not** a diagnostic or treatment tool — it never
recommends specific treatments.

The canonical specification is `PROJECT_PLAN.md` at the repo root. Code comments
cite its section numbers (e.g. "Section 10.3", "OQ-06"). When a rule is ambiguous,
that document is authoritative.

Pipeline: **Measure → Assess → Score → Classify → Recommend**.

## Glossary

| Term | Meaning |
|---|---|
| **SSS** | Spine Severity System — the 0–11 clinical severity score (Dr. Ayush Sharma's methodology). |
| **VAS** | Visual Analogue Scale, 0–10 self-reported pain intensity. |
| **ODI** | Oswestry Disability Index. This app uses the **SSS Modified ODI**: 5 activities (Walking, Sitting, Standing, Sleep, Daily Activities), each 0–2, total 0–10. *Not* the standard 10-activity ODI (OQ-04). |
| **Radiculopathy** | Nerve-root pain radiating into the leg (sciatica). Scored 0–3. |
| **Chronicity** | How long pain has persisted: Acute (<3wk), Subacute (3–6wk), Chronic (>6wk). |
| **Red flag** | A symptom suggesting serious pathology (cancer, infection, cauda equina, etc.). Any positive flag overrides everything. |
| **Lifestyle / Daily Habit risk** | Three-tier (Low/Moderate/High) score from sitting, walking, exercise, sleep — age-adjusted. |
| **Age group** | Young Adult (≤30), Mid Adult (31–45), Pre-Senior (46–60), Senior (61+). Drives thresholds. |
| **Composite Back Pain Risk** | Final classification combining SSS score × Lifestyle tier. |

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

## Red flag override (clinically critical)

If **any** of the 8 red-flag items is Yes (`RedFlagDraft.hasAnyRedFlag`):
component scores are still computed and stored, but the displayed total is forced
to 11, severity to Severe/High Risk, and the report shows an urgent-referral
notice. This must never be silently weakened.

## Report sections

The report has 12 fixed sections (Patient Summary → Recommended Next Steps),
defined in `PROJECT_PLAN.md` §12 and rendered in `PdfExporter.kt`. The "Probable
Contributors" narrative and "Key Risk Factors" are produced by a static rules
engine (no AI) and must reference only data collected in the assessment.

## Phase 1 scope boundaries (do not implement without confirmation)

Explicitly **out of scope** (see `PROJECT_PLAN.md` §16 + Open Questions): patient
self-assessment mode, body-diagram pain input (text checklist only), LLM-generated
narrative, clinic-branded PDF headers, QR codes, cloud sync / portal, iOS,
public Play Store release. Health Connect *is* in scope but currently unimplemented
(see `development_workflows.md`).
