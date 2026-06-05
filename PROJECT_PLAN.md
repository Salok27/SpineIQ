# PROJECT_PLAN.md
# SpineIQ — Scientific Back Pain Assessment Platform
### Phase 1 · Android Application (Kotlin + Jetpack Compose)

**Version:** 1.1 — Phase 1 Specification (Open Questions Resolved)  
**Prepared by:** Business Analysis & Product Management  
**Source Documents:** Stakeholder Email (Khalid), Daily Habit Snapshot (Dr. Ayush Sharma / SSS), Spine Severity Assessment System (SSS) — OPD Screening & Severity Scoring Tool, Open Questions Resolution Log (OQ-01 through OQ-18)  
**Status:** Confirmed — Open Questions Resolved

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [Business Goal](#2-business-goal)
3. [Target Users](#3-target-users)
4. [Functional Requirements](#4-functional-requirements)
5. [Non-Functional Requirements](#5-non-functional-requirements)
6. [User Flow](#6-user-flow)
7. [Screens and Features](#7-screens-and-features)
8. [Data Fields Collected](#8-data-fields-collected)
9. [Assessment Logic](#9-assessment-logic)
10. [Scoring Rules](#10-scoring-rules)
11. [Risk Classification Rules](#11-risk-classification-rules)
12. [Reporting Requirements](#12-reporting-requirements)
13. [PDF Export Requirements](#13-pdf-export-requirements)
14. [Health Connect Integration Requirements](#14-health-connect-integration-requirements)
15. [Local Data Storage Requirements](#15-local-data-storage-requirements)
16. [Future Enhancements](#16-future-enhancements)
17. [Acceptance Criteria](#17-acceptance-criteria)
18. [Open Questions](#18-open-questions)

---

## 1. Project Overview

**Product Name:** SpineIQ (working title)  
**Platform:** Native Android — Kotlin + Jetpack Compose  
**Methodology:** Spine Severity System (SSS) by Dr. Ayush Sharma, combined with Daily Habit Snapshot lifestyle scoring  
**Phase:** 1 — Functional Prototype  
**Distribution:** Internal (APK sideload or private Play Store track)  
**Primary Mode:** Clinician-led intake (Clinic Mode)

SpineIQ is a scientific, data-driven back pain assessment application for Android. It is designed explicitly for **clinician-led intake** in a **Clinic Mode** architecture, where a single app installation supports multiple distinct patient profiles managed by a physiotherapist, spine specialist, or general practitioner. The app guides the clinician through a structured, multi-domain questionnaire that collects demographic, occupational, lifestyle, pain, and functional data for a given patient. The app computes a composite Spine Severity Score and a Lifestyle Risk Score, classifies the patient into a risk tier, and generates an evidence-based assessment report — without recommending specific treatments.

The platform follows a strict **Measure → Assess → Score → Classify → Recommend** pipeline and is designed to identify probable root causes of back pain rather than default to treatment-first workflows.

---

## 2. Business Goal

| Goal | Description |
|---|---|
| Primary | Replace fear-based, generic clinical screening with a reproducible, evidence-based digital assessment tool for use during OPD consultations. |
| Secondary | Build a data foundation for longitudinal spine health tracking and AI-assisted root-cause analysis in future phases. |
| Tertiary | Enable population-level back pain analytics at clinic scale. |

**Problem Being Solved:** Current OPD back pain workflows rely on subjective intake forms and often skip structured lifestyle and functional assessment, leading to treatment recommendations that do not address root causes. SpineIQ creates a standardised, scored, and reproducible clinician-led intake process.

---

## 3. Target Users

### Primary Users

| User Type | Context | Usage Pattern |
|---|---|---|
| Physiotherapists / Spine Specialists | Guided intake during OPD consultation | Conduct assessment on behalf of the patient; generate and archive report |
| General Practitioners | Quick screening tool | Rapid SSS scoring during consultation |

> **Note (OQ-01):** Phase 1 is scoped exclusively to **clinician-led intake**. Patient-facing self-assessment flows are not implemented in Phase 1. All UX, language, and screen design assumes a clinical operator conducting the assessment on behalf of the patient.

### Secondary Users (Future)

- Patients (self-assessment app — Phase 2)
- Clinic administrators (report management, population analytics)
- Research teams (de-identified dataset export)

---

## 4. Functional Requirements

### FR-01 — Patient Information Capture
The app must collect and store patient demographics including name, age, gender, height, weight, and auto-calculated BMI.

### FR-02 — Occupation and Work Pattern Assessment
The app must present an occupation type selector and capture daily sitting, standing, driving hours, and lifting activity level.

### FR-03 — Lifestyle Assessment
The app must capture sleep hours, sleep quality, walking minutes per day, daily steps, exercise frequency, exercise type, and active minutes per day. Sleep quality must be incorporated as a mathematical modifier in the Lifestyle Risk score calculation (not treated as display-only information).

### FR-04 — Health Data Import (Health Connect)
The app must support automatic import of health metrics (steps, activity minutes, sleep, heart rate, weight) via Android Health Connect on API 28+ devices. Manual entry must always be available as a fallback. On devices running API 26–27, Health Connect features must be gracefully disabled/hidden and all relevant fields presented for manual entry.

### FR-05 — Pain Assessment
The app must capture pain location, VAS pain intensity (0–10), pain duration, pain pattern (constant/intermittent), pain triggers, radiation/radiculopathy symptoms, and self-reported functional limitations. Pain location must be captured via a **text-based checklist UI**. Illustrated body diagrams and interactive tap regions are excluded from Phase 1.

### FR-06 — Functional Assessment
The app must assess sitting tolerance, standing tolerance, walking capacity, stair climbing ability, and lifting ability using the **SSS Modified ODI** scoring variant, implemented exactly as specified in Section 10.1.

### FR-07 — Red Flag Screening
The app must present a mandatory red flag checklist and automatically set the overall score to the maximum severity value if any red flag is confirmed.

### FR-08 — Scoring Engine
The app must compute the following scores after data collection is complete:
- VAS Pain Score (0–2 points)
- Radiculopathy Score (0–3 points)
- Disability Score / SSS Modified ODI (0–2 points)
- BMI Mechanical Load Score (0–2 points)
- Chronicity of Symptoms Score (0–2 points)
- Red Flag Score (0 or 11)
- Total SSS Score (0–11)
- Lifestyle Score — **three-tier** (Low / Moderate / High Risk), with age-group thresholds interpolated to include a Moderate band between the "good" and "high risk" reference points
- Sleep Quality Modifier (incorporated mathematically into the Lifestyle Risk calculation)
- Exercise Type Risk Modifier (additional moderate additive weighting for high-impact / spine-loading exercise types)
- Activity Score
- Sleep Score
- Mobility Score
- Obesity Score
- Back Pain Risk Score (aggregate classification via SSS × Lifestyle combination matrix)

### FR-09 — Risk Classification
The app must classify each completed assessment into one of four SSS tiers (Low, Mild-Moderate, Moderate-Severe, Severe/High Risk) and one of **three** Lifestyle Risk tiers (Low, Moderate, High). A strict two-tier (Low / High) binary lifestyle classification must not be used.

### FR-10 — Report Generation
The app must generate a structured, human-readable assessment report within the app after assessment completion. The "Probable Contributors" narrative section must be generated by a **static, logic-based rules engine** (if-then text blocks). LLM or AI text generation is not integrated in Phase 1.

### FR-11 — PDF Export
The app must allow the user to export the assessment report as a formatted PDF document using **generic SpineIQ branding**. Clinician-customisable PDF headers and clinic logo upload features are excluded from Phase 1.

### FR-12 — Assessment History
The app must store all completed assessments locally on device and allow the user to view past assessments.

### FR-13 — Assessment Edit / Review
Before final submission, the app must allow the user to review and edit any section of the assessment.

### FR-14 — Multi-Patient Support (Clinic Mode)
The app must implement a **Clinic Mode** architecture, allowing a single installation to create and manage multiple distinct patient profiles. This is a single-operator, multi-patient clinical tool, not a single-user personal health app.

### FR-15 — Reassessment Frequency
The app must allow clinicians to log unlimited assessments per patient at any frequency. No minimum reassessment interval timers, blockers, or UI prompts are implemented.

### FR-16 — Heart Rate Trend Visualisation
Resting and average heart rate data collected from wearables must be processed and displayed as a **longitudinal trend** (showing deltas over time across assessments). Raw heart rate values must not be tied to diagnostic alerts or threshold-based medical warnings.

---

## 5. Non-Functional Requirements

| ID | Category | Requirement |
|---|---|---|
| NFR-01 | Performance | Assessment completion to report generation must complete within 3 seconds on a mid-range Android device (2 GB RAM, Android 10+). |
| NFR-02 | Usability | Every screen must be completable by a clinician without referencing external documentation. All clinical terms must include plain-language tooltips. |
| NFR-03 | Accessibility | The app must support Android TalkBack. Minimum touch target size 48×48 dp. Minimum contrast ratio WCAG AA. |
| NFR-04 | Privacy | All personal health data must be stored only on-device by default. Optional, user-controlled cloud backup is permitted. No data may be transmitted to external servers without explicit user consent. |
| NFR-05 | Security | Local database must be encrypted at rest (SQLCipher or Android Keystore-backed encryption). |
| NFR-06 | Offline Operation | All assessment, scoring, and report-generation functions must work fully offline. Health Connect sync requires only device-side data availability. |
| NFR-07 | Compatibility | Minimum Android API Level 26 (Android 8.0). Health Connect features require API Level 28+ and must be **gracefully disabled on API 26–27** (not blocked from running). Target API Level must track current Android release. |
| NFR-08 | Maintainability | Scoring logic must be isolated in a dedicated, independently testable module. Changing a scoring threshold must not require UI changes. |
| NFR-09 | Localisation | Architecture must support future localisation. Phase 1 delivers English only. |
| NFR-10 | PDF Quality | Exported PDF must be legible when printed at A4 size. Must embed fonts (no system-font dependency). |
| NFR-11 | Distribution | Phase 1 build must be configured for **internal distribution** (APK sideloading or private Play Store track). Production keystores and public Play Store listing configurations are excluded from Phase 1. |

---

## 6. User Flow

```
App Launch
    │
    ├─► [New Patient] ──► Patient Information Screen
    │                          │
    │                          ▼
    │                     Occupation & Work Pattern Screen
    │                          │
    │                          ▼
    │                     Lifestyle Assessment Screen
    │                          │
    │                          ├─► [API 28+ AND Health Connect Available?]
    │                          │        YES ──► Import Data Screen (auto-fill + confirm)
    │                          │        NO  ──► Manual Entry (all fields)
    │                          │
    │                          ▼
    │                     Pain Assessment Screen
    │                          │
    │                          ▼
    │                     Functional Assessment Screen
    │                          │
    │                          ▼
    │                     Red Flag Screening Screen
    │                          │
    │                          ▼
    │                     Assessment Review Screen (summary of all inputs, editable)
    │                          │
    │                          ▼
    │                     [Confirm & Calculate]
    │                          │
    │                          ▼
    │                     Scoring Engine (background computation)
    │                          │
    │                          ▼
    │                     Results Dashboard Screen
    │                          │
    │                          ├─► View Full Report Screen
    │                          │
    │                          └─► Export PDF
    │
    └─► [Returning Patient] ──► Patient Selection Screen
                                    │
                                    ├─► Assessment History Screen
                                    │         │
                                    │         └─► View Past Report
                                    │
                                    └─► Start New Assessment ──► (same flow as above)
```

---

## 7. Screens and Features

### Screen 1 — Home / Dashboard
- Welcome message and app branding
- Two primary actions: **New Assessment** | **View Patients**
- Quick stats (total assessments completed — shown if > 0)
- Settings access (language, data management)

### Screen 2 — Patient Selection / Management
- Searchable list of saved patient profiles
- Create new patient profile
- Tap patient to view their assessment history
- Long-press or swipe for delete/archive options

### Screen 3 — Patient Information
- Name (text input)
- Date of Birth (date picker — age auto-calculated)
- Gender (Male / Female / Other / Prefer not to say)
- Height (cm) — numeric input with unit toggle
- Weight (kg) — numeric input
- BMI — auto-calculated and displayed inline
- BMI category label (Underweight / Normal / Overweight / Obese) displayed alongside value
- Phone number (optional — for report sharing)
- Date of assessment (auto-populated, editable)
- OPD Number / Patient ID (optional alphanumeric text field — standalone, no external system validation)

### Screen 4 — Occupation & Work Pattern
- Occupation type (single-select): Office Worker, Field Worker, Driver, Homemaker, Student, Manual Labor, Other
- Sitting hours per day (slider + numeric: 0–24)
- Standing hours per day (slider + numeric)
- Driving hours per day (slider + numeric)
- Lifting activity level (None / Light / Moderate / Heavy — with descriptor for each)
- Work pattern notes (optional free text)

### Screen 5 — Lifestyle Assessment & Health Data
- **Health Connect import banner** (shown only on API 28+ devices where permission is available): shows last synced values with option to confirm or override
- On API 26–27 devices: Health Connect banner is hidden; all fields presented for manual entry
- Sleep hours per night (slider + numeric: 0–12)
- Sleep quality (Poor / Fair / Good / Excellent) — **feeds into Lifestyle Risk score as a mathematical modifier**
- Walking minutes per day (slider + numeric)
- Daily steps (numeric — auto-filled from Health Connect if available on API 28+)
- Exercise frequency (days per week: 0–7)
- Exercise type (multi-select: Walking, Running, Cycling, Swimming, Gym/Weights, Yoga/Pilates, Other, None) — **high-impact and spine-loading types apply a moderate additive risk modifier**
- Active minutes per day (numeric)
- Sedentary time per day (numeric — auto-derived from Health Connect if available on API 28+)
- Resting heart rate (numeric — optional, from wearable — displayed as longitudinal trend across assessments)
- Average daily heart rate (numeric — optional, from wearable — displayed as longitudinal trend across assessments)

### Screen 6 — Pain Assessment
- Pain location — **text-based checklist** (multi-select):
  - Upper Back
  - Mid Back
  - Lower Back / Lumbar
  - Sacral / Coccyx
  - Left Hip / Buttock
  - Right Hip / Buttock
- Pain intensity — VAS scale (0–10 face/emoji slider with severity label)
- Pain duration (< 3 weeks / 3–6 weeks / > 6 weeks)
- Pain pattern (Constant / Intermittent / Activity-related / Position-related)
- Pain triggers (multi-select: Sitting, Standing, Walking, Lifting, Bending, Morning stiffness, Rest/sleep, No clear trigger)
- Radiation symptoms — leg radiculopathy (None / Mild occasional / Moderate affecting activities / Severe constant with walking limitation) — maps to SSS Section 2
- Radiation location (Left leg / Right leg / Both legs — shown only if radiation > None)
- Functional limitations (free-text + optional structured severity: None / Mild / Moderate / Severe)

### Screen 7 — Functional Assessment (SSS Modified ODI)
For each of the following activities, the user selects: Normal (0) / Mild Difficulty (1) / Severe Difficulty (2).  
This implements the **SSS Modified ODI variant exactly as documented** — five activities scored 0–2 each (total 0–10), not the standard 10-activity full ODI.
- Walking
- Sitting
- Standing
- Sleep
- Daily Activities
- ODI Total Score displayed (0–10) with corresponding ODI Points (0–2) shown inline

### Screen 8 — Red Flag Screening
- Introductory explanation: "These questions help identify serious conditions that require urgent evaluation."
- Checklist (each item Yes/No):
  - History of cancer
  - Unexplained weight loss
  - Fever or active infection
  - Recent major trauma / injury
  - Bowel or bladder dysfunction (new onset)
  - Saddle anaesthesia (numbness in groin/inner thighs)
  - Progressive neurological deficit
  - Other serious pathology suspicion (clinician-noted)
- **Alert banner** displayed immediately if any item is checked: "One or more red flags are present. Clinical evaluation is required urgently. The severity score will be set to the maximum level."

### Screen 9 — Assessment Review
- Scrollable summary of all entered data, grouped by section
- Edit button on each section card navigates back to that screen
- "Confirm and Calculate Score" primary button at bottom
- Warning if any required field is empty

### Screen 10 — Results Dashboard
- Patient name and assessment date in header
- **Total SSS Score** displayed prominently (large numeral, 0–11)
- Severity tier badge: Low (green) / Mild-Moderate (amber) / Moderate-Severe (orange) / Severe/High Risk (red)
- Severity description text
- Suggested management tier label (from SSS table)
- **Lifestyle Risk tile**: Low / Moderate / High with composite habit summary
- Sub-score breakdown (expandable): VAS, Radiculopathy, ODI, BMI, Chronicity, Red Flag
- Individual lifestyle component indicators: Sitting, Walking, Exercise, Sleep — each shown as adequate/warning/risk
- Heart rate trend chart (shown if two or more assessments with heart rate data exist for this patient)
- Action buttons: **View Full Report** | **Export PDF** | **Start New Assessment**

### Screen 11 — Full Assessment Report (In-App)
Scrollable, section-by-section report. See Section 12 for content requirements.

### Screen 12 — Settings
- Data management: export all patient data, delete patient, delete all data, optional cloud backup
- Health Connect: manage permissions, view last sync time (visible only on API 28+ devices)
- Units: metric / imperial toggle
- App version and attribution

---

## 8. Data Fields Collected

### 8.1 Patient Demographics

| Field | Type | Required | Notes |
|---|---|---|---|
| Full Name | Text | Yes | |
| Date of Birth | Date | Yes | Age auto-calculated |
| Age (calculated) | Integer | Auto | Drives age-specific risk thresholds |
| Gender | Enum | Yes | Male / Female / Other / Prefer not to say |
| Height | Decimal (cm) | Yes | |
| Weight | Decimal (kg) | Yes | |
| BMI | Decimal | Auto | Weight(kg) / Height(m)² |
| BMI Category | Enum | Auto | < 25 Normal / 25–29.9 Overweight / ≥ 30 Obese |
| Phone | Text | No | |
| Patient ID / OPD No. | Text (alphanumeric) | No | Standalone field only; no external system validation |
| Assessment Date | Date | Auto | |

### 8.2 Occupation & Work Pattern

| Field | Type | Required |
|---|---|---|
| Occupation Type | Enum | Yes |
| Sitting Hours/Day | Decimal | Yes |
| Standing Hours/Day | Decimal | Yes |
| Driving Hours/Day | Decimal | Yes |
| Lifting Activity Level | Enum | Yes |

### 8.3 Lifestyle & Activity

| Field | Source | Required | Scoring Role |
|---|---|---|---|
| Sleep Hours/Night | Manual / Health Connect | Yes | Lifestyle Score component |
| Sleep Quality | Manual Enum | Yes | **Lifestyle Risk mathematical modifier** |
| Walking Minutes/Day | Manual / Health Connect | Yes | Lifestyle Score component |
| Daily Steps | Manual / Health Connect | Yes | Activity Score |
| Exercise Days/Week | Manual | Yes | Lifestyle Score component |
| Exercise Type(s) | Manual Multi-select | Yes | Moderate additive risk modifier for high-impact/spine-loading types |
| Active Minutes/Day | Manual / Health Connect | Yes | Activity Score |
| Sedentary Time/Day | Manual / Health Connect | No | Informational |
| Resting Heart Rate | Manual / Health Connect | No | Longitudinal trend display only |
| Average Heart Rate | Manual / Health Connect | No | Longitudinal trend display only |

### 8.4 Pain Assessment

| Field | Type | Required |
|---|---|---|
| Pain Location(s) | Text checklist (multi-select) | Yes |
| VAS Pain Intensity | Integer 0–10 | Yes |
| Pain Duration | Enum | Yes |
| Pain Pattern | Enum | Yes |
| Pain Triggers | Multi-select | Yes |
| Radiculopathy Severity | Enum (0–3) | Yes |
| Radiation Location | Enum | Conditional |
| Functional Limitations | Text + Enum | Yes |

### 8.5 Functional Assessment (SSS Modified ODI)

| Activity | Scale |
|---|---|
| Walking | Normal(0) / Mild(1) / Severe(2) |
| Sitting | Normal(0) / Mild(1) / Severe(2) |
| Standing | Normal(0) / Mild(1) / Severe(2) |
| Sleep | Normal(0) / Mild(1) / Severe(2) |
| Daily Activities | Normal(0) / Mild(1) / Severe(2) |

> **Note (OQ-04):** The SSS Modified ODI (5 activities × 0–2 scale, total 0–10) is implemented exactly as documented. The standard 10-activity full ODI variant is not used.

### 8.6 Red Flag Screening

Eight binary (Yes/No) items — see Screen 8 for full list.

### 8.7 Health Connect Data Points (API 28+ devices only)

| Data Type | Health Connect Record Type |
|---|---|
| Daily Steps | StepsRecord |
| Walking / Exercise Minutes | ExerciseSessionRecord |
| Active Minutes | ActiveCaloriesBurnedRecord |
| Sedentary Time | SedentaryIntervalRecord |
| Sleep Duration | SleepSessionRecord |
| Sleep Stages | SleepStageRecord |
| Resting Heart Rate | RestingHeartRateRecord |
| Average Heart Rate | HeartRateRecord |
| Weight | WeightRecord |

> On API 26–27 devices all Health Connect data types above are captured via manual entry only.

---

## 9. Assessment Logic

### 9.1 BMI Calculation

```
BMI = Weight (kg) / (Height (m))²
```

BMI Category mapping:
- < 18.5 → Underweight
- 18.5 – 24.9 → Normal
- 25.0 – 29.9 → Overweight
- ≥ 30.0 → Obese

### 9.2 Age Group Classification

Used to apply age-specific thresholds from the Daily Habit Snapshot:

| Age Range | Group Label |
|---|---|
| 20–30 | Young Adult |
| 31–45 | Mid Adult |
| 46–60 | Pre-Senior |
| 61+ | Senior |

### 9.3 Red Flag Override

If **any** red flag checkbox is marked Yes:
- All scoring calculations are still performed for record.
- The **displayed Total SSS Score is overridden to 11** (maximum).
- The severity classification is forced to **Severe / High Risk**.
- The report prominently displays a red-flag warning requiring urgent clinical evaluation.

### 9.4 Health Connect Data Handling

1. On the Lifestyle Assessment screen, the app checks the device API level.
2. On **API 28+** devices: the app requests Health Connect read permissions contextually.
3. If granted, the app queries the most recent 7-day average for each relevant data type.
4. Pre-filled values are shown as editable fields with a "from wearable" label.
5. The user may override any auto-filled value.
6. The source of each value (wearable / manual) is stored alongside the value and shown in the report.
7. On **API 26–27** devices, or if permission is denied on API 28+: all fields default to manual entry with no functional difference. Health Connect UI elements are hidden on unsupported API versions.

### 9.5 Sleep Quality Modifier

Sleep quality (Poor / Fair / Good / Excellent) is incorporated as a mathematical modifier applied to the Lifestyle Risk score calculation. The modifier adjusts the composite Lifestyle Risk tier upward when sleep quality is Poor or Fair, and confirms or improves the tier when Good or Excellent. The exact weighting is defined in Section 10.2.

### 9.6 Exercise Type Risk Modifier

When the clinician selects high-impact or spine-loading exercise types (e.g., heavy weightlifting, high-intensity interval training), the scoring engine applies a **moderate additive risk modifier** to the exercise component of the Lifestyle Risk calculation. This is not an absolute penalty or severe multiplier — it adjusts the exercise risk tier one step upward at most.

### 9.7 Heart Rate Trend Processing

Resting heart rate and average heart rate values are stored per-assessment. When two or more assessments exist for a patient with heart rate data, the app calculates and displays inter-assessment deltas as a longitudinal trend chart. No threshold-based diagnostic alerts or medical warnings are generated from heart rate values.

---

## 10. Scoring Rules

### 10.1 SSS Scoring — Spine Severity System (0–11)

#### Section 1 — VAS Pain Score (0–2 points)

| VAS Value | Severity Label | Points |
|---|---|---|
| 0–3 | Mild | 0 |
| 4–6 | Moderate | 1 |
| 7–10 | Severe | 2 |

#### Section 2 — Leg Radiculopathy / Sciatica Severity (0–3 points)

| Description | Points |
|---|---|
| No pain or symptoms in leg | 0 |
| Mild symptoms, occasional, not affecting activities | 1 |
| Moderate symptoms, affects daily activities / walking | 2 |
| Severe symptoms, constant pain, marked limitation in walking | 3 |

#### Section 3 — Disability Score / SSS Modified ODI (0–2 points)

Sum the five activity scores (Walking, Sitting, Standing, Sleep, Daily Activities), each 0–2.

| ODI Total (0–10) | ODI Points |
|---|---|
| 0–2 | 0 |
| 3–5 | 1 |
| 6–10 | 2 |

> **Note (OQ-04):** This implements the SSS Modified ODI variant exactly. No other ODI variant is used.

#### Section 4 — BMI Mechanical Load Score (0–2 points)

| BMI | Points |
|---|---|
| < 25 | 0 |
| 25–29.9 (Overweight) | 1 |
| ≥ 30 (Obese) | 2 |

#### Section 5 — Chronicity of Symptoms (0–2 points)

| Pain Duration | Points |
|---|---|
| < 3 Weeks (Acute) | 0 |
| 3–6 Weeks (Subacute) | 1 |
| > 6 Weeks (Chronic) | 2 |

#### Section 6 — Red Flag Score

| Condition | Points |
|---|---|
| No red flags present | 0 |
| Any red flag present | 11 (overrides total) |

#### Total SSS Score

```
Total SSS Score = VAS Points + Radiculopathy Points + ODI Points + BMI Points + Chronicity Points + Red Flag Score
```

Range: 0–11 (Red Flag presence sets total to 11 regardless of other scores)

---

### 10.2 Lifestyle / Daily Habit Snapshot Scoring

Each lifestyle parameter is scored independently and then combined into an overall Lifestyle Risk tier. The engine implements a **three-tier system** (Low / Moderate / High) — a strict two-tier binary is not used.

#### Sitting Hours/Day

| Hours | Risk |
|---|---|
| < 6–7 hrs | Low |
| 7–9 hrs | Moderate |
| > 9–10 hrs | High |

#### Walking Minutes/Day

| Minutes | Risk |
|---|---|
| ≥ 45 min | Low |
| 20–44 min | Moderate |
| < 20 min | High |

#### Exercise Days/Week

| Days | Risk |
|---|---|
| ≥ 3 days | Low |
| 1–2 days | Moderate |
| 0 days | High |

#### Sleep Hours/Night

| Hours | Risk |
|---|---|
| 7–8 hrs | Low |
| 5–6 hrs | Moderate |
| < 5 hrs | High |

#### Sleep Quality Modifier

| Sleep Quality | Modifier Effect |
|---|---|
| Excellent | No adjustment (or improves Sleep component one step toward Low if borderline) |
| Good | No adjustment |
| Fair | Adjusts Sleep component one step toward High |
| Poor | Adjusts Sleep component one step toward High (or forces High if already Moderate) |

#### Exercise Type Risk Modifier

| Exercise Type Category | Modifier Effect |
|---|---|
| Low-impact (Walking, Yoga/Pilates, Swimming, Cycling) | No adjustment |
| High-impact / Spine-loading (Heavy Gym/Weights, Running, Other high-impact) | Moderate additive modifier — adjusts exercise risk component one step toward High |

#### Age-Adjusted Thresholds

The scoring engine applies age-group-specific thresholds for walking and sitting per the Daily Habit Snapshot reference table. The "good" and "high risk" reference points from the source document are used as the Low and High tier anchors; the Moderate tier band is **interpolated between them** for each age group. The threshold values above represent the general baseline; the engine selects the appropriate row based on the calculated age group at assessment time.

#### Lifestyle Risk Aggregate

- **Low Risk:** All four components (post-modifier) score Low
- **Moderate Risk:** One or more components score Moderate, none score High
- **High Risk:** One or more components score High

Sub-scores for individual lifestyle components (Sitting Score, Walking Score, Exercise Score, Sleep Score) are stored separately and displayed individually on the Results Dashboard.

---

### 10.3 Composite Back Pain Risk Score

The final Back Pain Risk Score combines the SSS Score and the Lifestyle Risk tier into a single patient-facing risk classification using the following **SSS × Lifestyle combination matrix** (OQ-05):

| SSS Score | Lifestyle Risk | Final Back Pain Risk |
|---|---|---|
| 0–3 | Low | Low |
| 0–3 | Moderate | Low-Moderate |
| 0–3 | High | Moderate |
| 4–6 | Low | Mild-Moderate |
| 4–6 | Moderate | Moderate |
| 4–6 | High | Moderate-High |
| 7–9 | Any | High |
| 10–11 | Any | Severe / Urgent |
| Any | Any (Red Flag) | Severe / Urgent |

---

## 11. Risk Classification Rules

### 11.1 SSS Severity Tiers

| Total SSS Score | Severity Tier | Description | Suggested Management |
|---|---|---|---|
| 0–3 | LOW | Minimal pain and disability. Low impact on daily activities. | Education, posture correction, exercise |
| 4–6 | MILD–MODERATE | Mild to moderate pain/disability. Some functional limitation. | Physiotherapy, lifestyle correction, evaluation |
| 7–9 | MODERATE–SEVERE | Moderate to severe pain/disability. Marked limitation in activities. | Spine specialist consultation and advanced care |
| 10–11 | SEVERE / HIGH RISK | Severe symptoms / high risk. Possible serious underlying condition. Requires urgent attention. | URGENT SPINE SPECIALIST EVALUATION / ADVANCED CARE |

### 11.2 Lifestyle Risk Tiers

The Lifestyle Risk engine implements a **three-tier** system. The Moderate band is interpolated between the "good" and "high risk" age-specific reference points defined in the Daily Habit Snapshot.

| Tier | Criteria | Implication |
|---|---|---|
| Low Risk | All components (post-modifier) score Low | Healthy spine-supportive habits |
| Moderate Risk | One or more components score Moderate, none score High | Lifestyle adjustment recommended |
| High Risk | One or more components score High | Significant lifestyle intervention required |

### 11.3 Red Flag Rule

Any confirmed red flag triggers an immediate classification of **Severe / High Risk** regardless of all other scores. The report must include a prominent clinical urgent-referral notice.

---

## 12. Reporting Requirements

The in-app report and exported PDF must include all sections listed below. The "Probable Contributors" narrative is generated by a **static rules engine** (see FR-10).

### 12.1 Report Header
- App name and SpineIQ generic logo/branding
- Report title: "Back Pain Risk Assessment Report"
- Patient Name, Age, Gender
- Assessment Date
- Patient ID / OPD Number (if provided)

> **Note (OQ-08):** Generic SpineIQ branding is applied. Clinician-customisable headers and clinic logo upload are excluded from Phase 1.

### 12.2 Section 1 — Patient Summary
- Full demographics
- BMI value and category
- Occupation and work pattern summary

### 12.3 Section 2 — BMI Analysis
- BMI value and category
- Mechanical load interpretation:
  - Normal: no mechanical overload
  - Overweight: moderate additional spinal load
  - Obese: significant mechanical overload contributing to disc and joint stress
- Comparison to age-appropriate healthy range

### 12.4 Section 3 — Lifestyle Assessment
- Summary table: Sitting / Walking / Exercise / Sleep — actual value | target range | status (adequate / at risk / high risk)
- Sleep quality value and its effect on the Lifestyle Risk modifier
- Age-specific context (e.g., "For your age group (31–45), the recommended sitting limit is < 7 hours/day")
- Overall Lifestyle Risk tier with explanation
- Each at-risk habit explained with its spine health consequence:
  - Prolonged sitting → increased disc pressure, core muscle weakness, posture collapse
  - Low walking activity → reduced endurance, spinal mobility
  - Lack of exercise → weakened spine support muscles, increased spinal overload
  - Poor sleep → increased inflammation, delayed recovery

### 12.5 Section 4 — Activity Assessment
- Steps, active minutes, exercise frequency (actual values)
- Exercise type(s) recorded, with notation if high-impact/spine-loading modifier was applied
- Activity Score rating
- Comparison to recommended thresholds
- Data source indicated (wearable / manual entry)

### 12.6 Section 5 — Occupational Risk Assessment
- Occupation type
- Daily sitting / standing / driving hours
- Lifting activity level
- Occupational risk interpretation (e.g., prolonged driving + heavy lifting = elevated disc and lumbar risk)

### 12.7 Section 6 — Pain Pattern Analysis
- Pain location(s) listed by body-region label (from text checklist selections)
- VAS score and severity label
- Pain duration and pattern
- Radiculopathy/leg symptoms severity
- Triggers identified
- Chronicity classification

### 12.8 Section 7 — Functional Assessment Summary
- ODI activity scores table (SSS Modified ODI)
- Total ODI score and disability tier
- Functional limitations summary

### 12.9 Section 8 — Red Flag Status
- Red flag items reviewed (listed)
- Confirmed red flags highlighted
- Urgent referral notice if any flag is positive

### 12.10 Section 9 — Key Risk Factors
- Bulleted list of identified risk factors derived from all data domains, ranked by contribution severity:
  - Obesity / BMI
  - Sedentary lifestyle
  - Poor sleep
  - Low physical activity
  - Occupational strain
  - Chronic pain duration
  - Radiculopathy

### 12.11 Section 10 — Back Pain Risk Score (Results)
- Total SSS Score (0–11) with visual indicator
- Severity Tier badge
- Lifestyle Risk Tier
- Composite Back Pain Risk Classification
- Sub-score breakdown table (VAS, Radiculopathy, ODI, BMI, Chronicity, Red Flag)
- Individual lifestyle component scores

### 12.12 Section 11 — Probable Contributors to Pain
- Narrative paragraph generated by the **static, logic-based rules engine** explaining the likely causes of the patient's back pain based on the data collected. This section must:
  - Avoid mentioning specific treatments or products
  - Reference only the data collected in this assessment
  - Use plain, accessible language
  - Prioritise the top 2–3 contributors identified by the scoring engine

### 12.13 Section 12 — Recommended Next Steps
Based on the severity tier, the report displays standardised, non-treatment-specific next steps:

| Tier | Recommended Next Steps |
|---|---|
| Low | Continue healthy habits. Monitor symptoms. Consider postural assessment. |
| Mild-Moderate | Consult a physiotherapist. Begin lifestyle corrections. Reassess in 4–6 weeks. |
| Moderate-Severe | Seek spine specialist consultation. Avoid self-management of symptoms. |
| Severe / Urgent | Seek urgent spine specialist evaluation. Do not delay. |

### 12.14 Report Footer
- **Placeholder clinical disclaimer:** "This report is generated by a screening tool and is not a substitute for clinical judgment. It should be reviewed by a qualified healthcare professional." *(Formal medical-legal sign-off is out of scope for Phase 1 — placeholder language to be replaced prior to any production release.)*
- App version and scoring methodology reference (SSS v1.0)

> **Note (OQ-09):** QR code and URL are omitted from the report footer in Phase 1.  
> **Note (OQ-13):** Disclaimer text is placeholder only. Medical-legal sign-off is not required for this Phase 1 management prototype.

---

## 13. PDF Export Requirements

### 13.1 Format
- Page size: A4 (210 × 297 mm)
- Orientation: Portrait
- Margins: 15 mm all sides
- Font: embedded sans-serif (e.g., Roboto — the Android system default, embedded in PDF)
- Colour: use risk-tier colours for score badges (green, amber, orange, red) — all must print legibly in greyscale

### 13.2 Branding
- Generic SpineIQ branding (app name and logo) in the PDF header
- Clinician-customisable headers and clinic logo upload are **excluded from Phase 1**

### 13.3 Content
- All 12 report sections listed in Section 12 above
- Patient demographic header repeated on all pages (via header)
- Page numbering: "Page X of Y"
- Assessment date in footer of every page

### 13.4 Generation
- PDF is generated entirely on-device (no server round-trip)
- PDF library: to be selected during implementation (candidate: iText for Android / PdfDocument API)
- Generated file is saved to the app's private storage and offered to the user via the Android share sheet or Files picker for saving to device Downloads or cloud storage

### 13.5 Naming Convention
```
SpineIQ_[PatientName]_[YYYYMMDD].pdf
```

### 13.6 Accessibility
- PDF must include semantic tags for headings and tables (tagged PDF) to support screen readers where feasible

---

## 14. Health Connect Integration Requirements

### 14.1 Supported Data Types (Read Only — Phase 1, API 28+ only)

| Data Type | Health Connect Permission | Priority |
|---|---|---|
| Steps | READ_STEPS | Must-have |
| Exercise Sessions | READ_EXERCISE | Must-have |
| Sleep Sessions | READ_SLEEP_SESSION | Must-have |
| Sleep Stages | READ_SLEEP_STAGE | Nice-to-have |
| Active Calories Burned | READ_ACTIVE_CALORIES_BURNED | Must-have |
| Resting Heart Rate | READ_RESTING_HEART_RATE | Nice-to-have |
| Heart Rate | READ_HEART_RATE | Nice-to-have |
| Weight | READ_WEIGHT | Nice-to-have |

### 14.2 API Level Compatibility

| API Level | Health Connect Behaviour |
|---|---|
| API 26–27 (Android 8.0–8.1) | Health Connect features gracefully disabled/hidden. All health data fields presented for manual entry. No Health Connect permission requests made. |
| API 28+ (Android 9.0+) | Full Health Connect integration available. Permission requested contextually on Lifestyle Assessment screen. |

### 14.3 Permission Handling
- The app must request Health Connect permissions using the standard Android Health Connect permission flow (API 28+ only).
- Permissions must be requested contextually on the Lifestyle Assessment screen, not at app launch.
- If the user denies permission, all health data fields fall back to manual entry without degraded functionality.
- A "Why we need this" explanation must be displayed before the permission request dialog.

### 14.4 Data Query Window
- Default: 7-day rolling average ending at midnight of the assessment date
- Display: show the query window to the user ("Based on your last 7 days of data")
- The user may override any auto-populated value

### 14.5 Supported Source Devices
The integration is wearable-agnostic. Any device that writes to Android Health Connect is supported, including but not limited to:
- Google Fit / Pixel Watch
- Fitbit (via Health Connect sync)
- Samsung Galaxy Watch (Samsung Health → Health Connect)
- Garmin Connect (via Health Connect sync)
- Amazfit / Zepp (via Zepp → Health Connect)
- Xiaomi Mi Band (via Mi Fitness → Health Connect)
- OnePlus Watch
- Any third-party app that writes to Health Connect

### 14.6 Offline Behaviour
Health Connect data is read from the device's local Health Connect store. No external network request is made. This works offline as long as wearable data has previously been synced.

### 14.7 Data Source Tracking
The app must record, for each auto-filled field, whether the value was sourced from Health Connect or entered manually. This provenance is displayed in the report.

---

## 15. Local Data Storage Requirements

### 15.1 Database
- Technology: Room (Jetpack) with SQLCipher encryption
- Storage location: App-internal private storage (`/data/data/[package]/databases/`)
- No data is stored in external storage or shared storage without an explicit user export action

### 15.2 Data Entities

#### Patient Profile
```
PatientProfile {
  id: UUID (primary key)
  fullName: String
  dateOfBirth: LocalDate
  gender: Enum
  heightCm: Float
  weightKg: Float
  phoneNumber: String?
  patientIdExternal: String?   // alphanumeric text only; no external system validation
  createdAt: Timestamp
  updatedAt: Timestamp
}
```

#### Assessment Record
```
AssessmentRecord {
  id: UUID (primary key)
  patientId: UUID (foreign key → PatientProfile)
  assessmentDate: LocalDate
  status: Enum (IN_PROGRESS / COMPLETED)
  createdAt: Timestamp
  completedAt: Timestamp?
}
```

#### Assessment Sections (linked to AssessmentRecord)
- OccupationData
- LifestyleData (with data source flags per field, sleep quality modifier value, exercise type modifier flag)
- PainData
- FunctionalData
- RedFlagData

#### Scores Record (computed, stored alongside assessment)
```
ScoresRecord {
  assessmentId: UUID
  vasScore: Int
  vasPoints: Int
  radiculopathyScore: Int
  odiScore: Int
  odiPoints: Int
  bmiScore: Float
  bmiPoints: Int
  chronicityPoints: Int
  redFlagScore: Int
  totalSSSScore: Int
  sssSeverityTier: Enum
  sittingRisk: Enum
  walkingRisk: Enum
  exerciseRisk: Enum
  sleepRisk: Enum
  sleepQualityModifier: Enum       // derived from sleep quality input
  exerciseTypeModifier: Boolean    // true if high-impact/spine-loading type selected
  lifestyleRiskTier: Enum
  backPainRiskClassification: Enum
  computedAt: Timestamp
}
```

### 15.3 Data Retention
- Phase 1: all data is retained indefinitely on-device until explicitly deleted by the user
- Delete patient profile: cascades to delete all associated assessments and scores
- Export before delete: the app must prompt the user to export a PDF summary before deleting a patient profile

### 15.4 Backup
- **Optional, user-controlled cloud backup** is permitted in Phase 1 (OQ-12). A strict air-gapped/no-data-leaves-device constraint is not enforced.
- Users may enable cloud backup via the Settings screen.
- Data is also included in Android Auto Backup if the user has it enabled (default Android behaviour) — note: encrypted database may not restore correctly across devices with Auto Backup; document this limitation in-app.

### 15.5 In-Progress Assessment Persistence
- If a user navigates away mid-assessment, the in-progress state must be persisted to the database with status IN_PROGRESS
- On next app launch, the user is prompted to resume or discard the in-progress assessment

---

## 16. Future Enhancements

The following features are explicitly out of scope for Phase 1 but must be considered in the architectural design to avoid rework.

| ID | Feature | Phase |
|---|---|---|
| FE-01 | Cloud sync and multi-device access (patient app + clinician portal) | Phase 2 |
| FE-02 | AI-assisted root cause analysis using longitudinal data | Phase 2 |
| FE-03 | Clinician web dashboard with population-level analytics | Phase 2 |
| FE-04 | Progress tracking — reassessment comparison and trend charts | Phase 2 |
| FE-05 | Personalised exercise and lifestyle recommendations engine | Phase 3 |
| FE-06 | Outcome monitoring — did patient follow recommendations? | Phase 3 |
| FE-07 | iOS application (SwiftUI) | Phase 2 |
| FE-08 | Apple Health / HealthKit integration (for iOS) | Phase 2 |
| FE-09 | Wearable companion app (Wear OS) for continuous monitoring | Phase 3 |
| FE-10 | DICOM / imaging integration (X-ray, MRI annotation) | Phase 3 |
| FE-11 | Multi-language support (Hindi, Arabic, Urdu priority) | Phase 2 |
| FE-12 | Clinic-level admin portal with patient management | Phase 2 |
| FE-13 | Anonymised research data export (de-identified, consent-gated) | Phase 2 |
| FE-14 | Telemedicine integration — share report with remote specialist | Phase 2 |
| FE-15 | Longitudinal spine health score trending | Phase 2 |
| FE-16 | Patient self-assessment mode (patient-facing UX and onboarding flows) | Phase 2 |
| FE-17 | Clinician-customisable PDF header / clinic branding | Phase 2 |
| FE-18 | HL7 FHIR integration with hospital information systems | Phase 2 |
| FE-19 | LLM / AI-generated "Probable Contributors" narrative | Phase 2 |
| FE-20 | Public Play Store listing and production keystore configuration | Phase 2 |

---

## 17. Acceptance Criteria

### AC-01 — Patient Data Entry
- ✅ All required fields in Patient Information, Occupation, Lifestyle, Pain, Functional, and Red Flag screens must be completable by a clinician without requiring external documentation
- ✅ BMI is auto-calculated and displayed within 100ms of entering height and weight
- ✅ Date of Birth field automatically derives and displays patient age
- ✅ Pain location is captured via text checklist (no illustrated body diagram)
- ✅ Patient ID / OPD Number field accepts free-form alphanumeric text without external validation

### AC-02 — Health Connect Integration
- ✅ App successfully imports steps, sleep, and active minutes from Health Connect on API 28+ devices where at least one supported wearable has synced
- ✅ App functions identically when Health Connect permission is denied (all fields presented for manual entry)
- ✅ On API 26–27 devices, Health Connect UI elements are hidden and all fields are presented for manual entry — no errors or crashes occur
- ✅ Auto-filled values are labelled with their data source in the UI
- ✅ User can override any auto-filled value

### AC-03 — Scoring Engine
- ✅ VAS Points computed correctly for all three input ranges
- ✅ Radiculopathy Score reflects the correct selected severity level
- ✅ ODI sum and ODI points computed correctly for all input combinations (SSS Modified ODI variant)
- ✅ BMI points correctly reflect all three BMI bands
- ✅ Chronicity points reflect all three duration bands
- ✅ Red Flag override sets Total SSS Score to 11 regardless of other inputs
- ✅ Total SSS Score is the correct sum of all component points (except red flag override)
- ✅ Lifestyle Risk correctly classifies all four components (sitting, walking, exercise, sleep) into three tiers
- ✅ Sleep quality modifier correctly adjusts the sleep risk component
- ✅ High-impact/spine-loading exercise types apply the correct moderate additive modifier
- ✅ Age-adjusted thresholds with interpolated Moderate band are applied based on the patient's calculated age group
- ✅ Composite Back Pain Risk classification is correct for all SSS × Lifestyle matrix combinations

### AC-04 — Risk Classification
- ✅ SSS tier badge displays the correct label and colour for all four tiers
- ✅ Red flag presence always displays "Severe / High Risk" tier
- ✅ Suggested management label matches the severity tier
- ✅ Lifestyle Risk displays three tiers (Low / Moderate / High) — two-tier display is not acceptable

### AC-05 — Report
- ✅ All 12 report sections are present in the in-app report view
- ✅ Probable Contributors section is generated by the static rules engine and references only data entered in the assessment (no generic statements; no AI-generated text)
- ✅ Recommended Next Steps match the severity tier
- ✅ Report disclaimer (placeholder) is present
- ✅ No QR code or external URL appears in the report footer
- ✅ Generic SpineIQ branding is applied; no clinician-customisable header fields are present

### AC-06 — PDF Export
- ✅ PDF is generated without errors for a completed assessment
- ✅ PDF renders all 12 report sections correctly
- ✅ PDF filename follows the specified naming convention
- ✅ PDF is legible when printed at A4 size
- ✅ PDF is shareable via Android share sheet
- ✅ PDF uses generic SpineIQ branding only

### AC-07 — Data Persistence
- ✅ Completed assessments are stored and retrievable after app restart
- ✅ In-progress assessments are saved and resumable after app backgrounding
- ✅ Deleting a patient profile deletes all associated assessment records

### AC-08 — Performance
- ✅ Score computation completes within 3 seconds of "Confirm and Calculate"
- ✅ PDF generation completes within 10 seconds for a typical assessment
- ✅ Assessment History screen loads within 2 seconds for up to 50 stored assessments

### AC-09 — Usability
- ✅ A clinician can complete a full assessment end-to-end without referring to documentation
- ✅ All clinical scoring terms include accessible tooltips or inline explanations
- ✅ Red flag warning is visually prominent and unambiguous
- ✅ No minimum reassessment interval prompts or blockers are displayed

### AC-10 — Privacy & Security
- ✅ No patient data is transmitted to any external server without explicit user consent
- ✅ Database is encrypted at rest
- ✅ Health Connect permissions are requested contextually on API 28+ devices with a rationale displayed
- ✅ Optional cloud backup toggle is accessible in Settings

### AC-11 — Distribution Build
- ✅ Build is configured for internal distribution (APK sideload or private Play Store track)
- ✅ No production keystore or public Play Store listing configuration is present in the Phase 1 build

---

## 18. Open Questions

All open questions from v1.0 have been resolved. The table below is retained for traceability and records each decision.

| ID | Question | Decision | Impact Area |
|---|---|---|---|
| OQ-01 | Primary user mode | Clinician-led intake only. Patient self-assessment is out of scope for Phase 1. | UX, Sections 3, 7 |
| OQ-02 | Multi-patient support | Clinic Mode: single installation supports multiple patient profiles. | Sections 4, 7, 15 |
| OQ-03 | Body diagram vs checklist | Text-based checklist only. Body diagrams excluded from Phase 1. | Sections 7 (Screen 6), 8.4 |
| OQ-04 | ODI variant | SSS Modified ODI implemented exactly as documented (5 activities × 0–2). No other ODI variant used. | Sections 7 (Screen 7), 8.5, 10.1 |
| OQ-05 | Composite risk score formula | SSS × Lifestyle combination matrix accepted and implemented as proposed. | Section 10.3 |
| OQ-06 | Age-specific threshold — moderate band | Three-tier engine. Moderate band interpolated between "good" and "high risk" reference points. | Sections 4, 10.2, 11.2 |
| OQ-07 | Probable Contributors generation | Static rules engine only. No LLM / AI text generation in Phase 1. | Sections 4, 12.12 |
| OQ-08 | PDF branding | Generic SpineIQ branding. Clinician-customisable header excluded. | Sections 13.2, 16 (FE-17) |
| OQ-09 | QR code / URL in footer | Omitted from Phase 1. | Section 12.14 |
| OQ-10 | Patient ID integration | Standalone alphanumeric text field. No HL7 FHIR or external system integration in Phase 1. | Sections 8.1, 15.2 |
| OQ-11 | Minimum Android version | API 26 minimum retained. Health Connect gracefully disabled on API 26–27. | Sections 5, 14.2 |
| OQ-12 | Offline-only constraint | Optional user-controlled cloud backup permitted. Strict air-gap not enforced. | Sections 5, 15.4 |
| OQ-13 | Clinical disclaimer sign-off | Placeholder language used. Medical-legal sign-off out of scope for Phase 1. | Section 12.14 |
| OQ-14 | App Store distribution | Internal distribution only (APK sideload / private track). No public Play Store configuration. | Sections 5 (NFR-11), 17 (AC-11) |
| OQ-15 | Reassessment interval | Unlimited assessments at any frequency. No interval enforcement in Phase 1. | Sections 4 (FR-15), 17 (AC-09) |
| OQ-16 | Sleep quality scoring | Sleep quality incorporated as a mathematical modifier in the Lifestyle Risk score. | Sections 4, 9.5, 10.2 |
| OQ-17 | Heart rate data use | Displayed as longitudinal trend (deltas over time). No diagnostic alerts or threshold warnings. | Sections 4, 9.7, 10, 15.2 |
| OQ-18 | Exercise type risk weighting | High-impact / spine-loading types apply a moderate additive risk modifier. Not an absolute penalty. | Sections 4, 9.6, 10.2 |

---

*Document prepared based on: Stakeholder Email (Khalid, undated), Daily Habit Snapshot — Spine Severity System v1.0 (Dr. Ayush Sharma), Spine Severity Assessment System (SSS) OPD Rapid Screening Form v1.0, and Open Questions Resolution Log (OQ-01 through OQ-18).*

*This specification represents Phase 1 scope only. All future enhancement items are recorded in Section 16 and are explicitly excluded from Phase 1 delivery.*
