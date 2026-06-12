# PROJECT_PLAN.md
# SpineIQ — Personal Back Pain Self-Assessment & Spine Health Tracker
### Phase 1 · Android Application (Kotlin + Jetpack Compose)

**Version:** 2.1 — D2C Self-Assessment Specification + V2 Gamified Experience  
**Prepared by:** Business Analysis & Product Management  
**Source Documents:** Stakeholder direction (2026-06-08), V2 gamification direction (2026-06-12), Daily Habit Snapshot (Dr. Ayush Sharma / SSS), Spine Severity Assessment System (SSS), Open Questions Resolution Log (OQ-01 through OQ-21)  
**Status:** Confirmed — Product direction updated from Clinic Mode to Direct-to-Consumer; V2 adds an engagement layer (Spine Coins, XP/levels, streaks, achievements, avatar shop) on top of the unchanged clinical assessment (FR-21 – FR-26)

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

**Product Name:** SpineIQ  
**Platform:** Native Android — Kotlin + Jetpack Compose  
**Methodology:** Spine Severity System (SSS) by Dr. Ayush Sharma, combined with Daily Habit Snapshot lifestyle scoring  
**Phase:** 1 — Functional Prototype  
**Distribution:** Public Google Play Store (production keystore required)  
**Primary Mode:** Personal self-assessment — the user assesses themselves

SpineIQ is a direct-to-consumer Android application that empowers individuals to understand their own spinal health risk through a structured, evidence-based self-assessment. Users assess themselves — there is no clinician, no patient roster, and no clinic operator. The app guides the user through a multi-domain questionnaire covering their demographics, occupation, lifestyle, pain, and functional status. It computes a composite Spine Severity Score (SSS, 0–11), a three-tier Lifestyle Risk score, and a Back Pain Risk classification, then produces a personalised in-app report and exportable PDF — without recommending specific treatments.

SpineIQ helps users answer: *"What is driving my back pain, and is it getting better over time?"*

The platform follows a **Measure → Assess → Score → Classify → Educate** pipeline, identifying probable contributors to back pain and tracking changes longitudinally across repeated self-assessments.

---

## 2. Business Goal

| Goal | Description |
|---|---|
| Primary | Empower individuals experiencing back pain to understand their personal risk profile through a reproducible, evidence-based self-assessment they can run themselves at any time. |
| Secondary | Build a longitudinal personal spine health record — enabling users to track changes in their SSS score and lifestyle habits over time and see the impact of behaviour changes. |
| Tertiary | Create a data foundation for AI-assisted personalised insights and lifestyle recommendations in future phases. |

**Problem Being Solved:** Individuals with back pain often lack access to structured, evidence-based assessments outside of clinical visits, and have no way to monitor whether lifestyle changes are improving their spinal health over time. SpineIQ puts a standardised, scored, and reproducible self-assessment directly in the user's hands.

---

## 3. Target Users

### Primary Users

| User Type | Context | Usage Pattern |
|---|---|---|
| Individual experiencing back pain | Wants to understand the severity of their condition and what is contributing to it | Self-assess, review results, share report with their doctor |
| Health-conscious individual | Wants to monitor spinal health proactively before symptoms worsen | Periodic self-assessment every 4–8 weeks |
| Person post-physiotherapy | Recently discharged and wants to monitor independently | Monthly self-check-in to track maintenance progress |

> **Note (OQ-01):** Phase 1 is scoped exclusively to **self-assessment by the individual user**. The user is both the operator and the subject of every assessment. There is no clinician-led or multi-patient mode.

### Secondary Users (Future)

- Healthcare providers who want patients to self-report before appointments and share structured results
- Wellness coaches and personal trainers monitoring client spinal health
- Research participants contributing de-identified longitudinal data

---

## 4. Functional Requirements

### FR-01 — Personal Profile
The app must collect and store the user's own demographic information including name, age, gender, height, weight, and auto-calculated BMI. This is a single personal profile — not a patient management system. The profile is set up once on first launch and is editable from Settings at any time.

### FR-02 — Occupation and Work Pattern Assessment
The app must present an occupation type selector and capture daily sitting, standing, driving hours, and lifting activity level.

### FR-03 — Lifestyle Assessment
The app must capture sleep hours, sleep quality, walking minutes per day, daily steps, exercise frequency, exercise type, and active minutes per day. Sleep quality must be incorporated as a mathematical modifier in the Lifestyle Risk score calculation (not treated as display-only information).

### FR-04 — Health Data Import (Health Connect)
The app must support automatic import of health metrics (steps, activity minutes, sleep, heart rate, weight) via Android Health Connect on API 28+ devices. Manual entry must always be available as a fallback. On devices running API 26–27, Health Connect features must be gracefully disabled/hidden and all relevant fields presented for manual entry.

### FR-05 — Pain Assessment
The app must capture pain location, VAS pain intensity (0–10), pain duration, pain pattern (constant/intermittent), pain triggers, radiation/radiculopathy symptoms, and self-reported functional limitations. Pain location must be captured via a **text-based checklist UI**. Illustrated body diagrams are excluded from Phase 1.

### FR-06 — Functional Assessment
The app must assess sitting tolerance, standing tolerance, walking capacity, stair climbing ability, and lifting ability using the **SSS Modified ODI** scoring variant, implemented exactly as specified in Section 10.1.

### FR-07 — Red Flag Screening
The app must present a mandatory red flag checklist and automatically set the overall score to the maximum severity value if any red flag is confirmed. When a red flag is present, the app must display a clear, prominent consumer-facing message: **"Please seek immediate medical attention from a doctor or emergency department. Do not delay."**

### FR-08 — Scoring Engine
The app must compute the following scores after data collection is complete:
- VAS Pain Score (0–2 points)
- Radiculopathy Score (0–3 points)
- Disability Score / SSS Modified ODI (0–2 points)
- BMI Mechanical Load Score (0–2 points)
- Chronicity of Symptoms Score (0–2 points)
- Red Flag Score (0 or 11)
- Total SSS Score (0–11)
- Lifestyle Score — **three-tier** (Low / Moderate / High Risk), with age-group thresholds interpolated to include a Moderate band
- Sleep Quality Modifier (incorporated mathematically into the Lifestyle Risk calculation)
- Exercise Type Risk Modifier (moderate additive weighting for high-impact / spine-loading types)
- Activity Score, Sleep Score, Mobility Score, Obesity Score sub-components
- Back Pain Risk Score (aggregate classification via SSS × Lifestyle combination matrix)

### FR-09 — Risk Classification
The app must classify each completed assessment into one of four SSS tiers (Low, Mild-Moderate, Moderate-Severe, Severe/High Risk) and one of **three** Lifestyle Risk tiers (Low, Moderate, High).

### FR-10 — Report Generation
The app must generate a structured, personalised, human-readable assessment report after assessment completion. The "Probable Contributors" narrative must be generated by a **static, logic-based rules engine** (if-then text blocks) using only data collected in the assessment. LLM or AI text generation is not integrated in Phase 1.

### FR-11 — PDF Export
The app must allow the user to export the assessment report as a formatted PDF using **generic SpineIQ branding**. The share sheet must include a **"Share with my doctor"** label as the primary suggested action. Customisable branding is excluded from Phase 1.

### FR-12 — Assessment History
The app must store all completed assessments locally on device and allow the user to view past assessments. Each past assessment must be viewable as a full report.

### FR-13 — Assessment Edit / Review
Before final submission, the app must allow the user to review and edit any section of the assessment.

### FR-14 — Single User Architecture
The app must implement a **single-user personal architecture**. One installation supports one user profile. There is no patient list, patient management screen, or multi-user switcher. This is a personal health app, not a clinical management tool.

### FR-15 — Reassessment Frequency
The app must allow users to complete assessments at any frequency. No minimum interval blockers are enforced. The recommended default reminder interval is 4 weeks (see FR-18).

### FR-16 — Heart Rate Trend Visualisation
Resting and average heart rate data must be processed and displayed as a longitudinal trend showing deltas across assessments. Raw heart rate values must not be tied to diagnostic alerts or threshold-based medical warnings.

### FR-17 — Onboarding Flow
The app must display a first-launch onboarding sequence of 3 screens:
1. **Welcome** — app name, tagline, "Understand your back pain risk" value proposition (not skippable)
2. **How It Works** — brief explanation of the SSS methodology and what the score means (skippable)
3. **Your Privacy** — explicit statement that all data stays on the device, never sent to servers without consent (skippable)

After onboarding completes, the user is taken to Profile Setup (FR-01). Onboarding is shown only on first launch.

### FR-18 — Reassessment Reminders
The app must support configurable push notification reminders to prompt the user to complete a new assessment. Default interval: 4 weeks. User-configurable range: 1–12 weeks. Reminders can be disabled entirely. Tapping a reminder notification must open the Start Assessment flow directly.

### FR-19 — Progress Tracking
After two or more completed assessments exist, the app must display a Progress screen showing:
- Line chart of Total SSS Score over time across all assessments
- Line chart of Lifestyle Risk tier over time
- Individual component trend cards (Sitting, Walking, Exercise, Sleep) showing current vs. previous value
- Improvement delta callout: "Your SSS score improved by X since your last check-in on [date]"

### FR-20 — Lifestyle Improvement Tips
The report must include brief, actionable, non-prescriptive lifestyle tips for each at-risk lifestyle component. Tips must be generated by the static rules engine, must not recommend specific treatments or medications, and must be framed as general wellness information (e.g., "Taking a short walk every 30 minutes can help reduce disc pressure from prolonged sitting").

### FR-21 — Spine Coins (Virtual Currency) *(V2)*
The app must maintain a spendable virtual-currency balance ("Spine Coins") earned through engagement: +10 per completed assessment section (5 sections), +50 per assessment completion, +5 per daily check-in, one-time streak milestone bonuses (3 days +50, 7 days +100, 14 days +150, 30 days +300), and per-achievement rewards. A full assessment therefore pays **100 coins**. The balance must be visible on the dashboard and in the Shop. **Every grant must be idempotent** — re-saving a section via back-navigation, retrying a completion, or repeating any action must never pay twice (enforced by a dedupe-keyed reward ledger). Coins are spent only on avatar cosmetics; **coins must never gate any health or assessment functionality**.

### FR-22 — XP and Levels *(V2)*
Independently of coins, the app must track lifetime XP (never spent, never decreases): +20 per section, +100 per completion, +15 per check-in, plus achievement rewards (full assessment = **200 XP**). XP maps onto 8 named levels — Beginner (0), Explorer (100), Spine Explorer (250), Recovery Champion (500), Wellness Warrior (1000), Mobility Master (2000), Spine Guardian (3500), Back Health Expert (5500). The level is always derived from XP (never stored) so the curve can be retuned without migration. The dashboard must show the current level, level name, and progress toward the next level; level-ups trigger a celebration overlay.

### FR-23 — Avatar and Shop *(V2)*
The app must provide a customisable, gender-neutral avatar rendered entirely as Compose vector graphics (no image assets), with swappable layers per category: Hair, Tops, Bottoms, Accessories. One free default item per clothing category is implicitly owned; all other items are purchased with Spine Coins in a Shop screen (catalog ≈ 21 items including Champion Jacket, Smart Watch, Neon Hoodie, Gold Fitness Band, Wellness Crown). The Shop must show the coin balance, a live try-on preview, owned/equipped states, and a purchase confirmation with balance-after. At most one item per category is equipped. **Cosmetics only** — purchases must never affect scoring, reports, or any health feature.

### FR-24 — Achievement System *(V2)*
The app must track a static, code-defined achievement catalog (first assessment, 5/10 assessments, section-mastery achievements, check-in count, 3/7/30-day streaks, first purchase — each with coin/XP rewards). Unlock state is persisted; definitions are not, so the catalog can grow without schema changes. An Awards screen must show the full gallery in catalog order with locked badges greyed out and partial-progress hints. Unlocks trigger a one-time celebration overlay and pay their reward exactly once.

### FR-25 — Daily Check-In and Streaks *(V2)*
The dashboard must offer a once-per-calendar-day mood check-in ("How's your back feeling today?" — Better / Same / Worse) rewarded per FR-21/FR-22. Mood history is retained for future trend display. A consecutive-day streak counter must be maintained where **only daily check-ins and assessment completions qualify** (per-section saves do not — they are repeatable). Day boundaries use the device-local calendar date; a missed day displays the streak as 0 immediately (lazy reset on read). Streak milestones pay one-time-ever coin bonuses and trigger celebrations. Current and longest streaks are displayed on the dashboard.

### FR-26 — Reward Feedback and Celebrations *(V2)*
Earning events must produce immediate, tasteful feedback: coin/XP toasts (bursts coalesced into one), full-screen celebration overlays with confetti for level-ups, achievement unlocks, and streak milestones, a 6-node "journey" progress indicator in the assessment wizard with per-stage reward previews, a brief skippable stage-complete interstitial after each wizard section, and a one-shot completion celebration on the Results screen (never replayed when re-viewing old results). All animation is Compose-native (no Lottie). Two hard constraints: (1) celebration UI fires only after the underlying grant has committed to the database; (2) **gamification must never block or delay the medical flow** — all gamification calls in the assessment path are failure-isolated.

---

## 5. Non-Functional Requirements

| ID | Category | Requirement |
|---|---|---|
| NFR-01 | Performance | Assessment completion to report generation must complete within 3 seconds on a mid-range Android device (2 GB RAM, Android 10+). |
| NFR-02 | Usability | Every screen must be completable by an individual user without referencing external documentation. All scoring terms must include plain-language tooltips. |
| NFR-03 | Accessibility | The app must support Android TalkBack. Minimum touch target size 48×48 dp. Minimum contrast ratio WCAG AA. |
| NFR-04 | Privacy | All personal health data must be stored only on-device by default. No data may be transmitted to external servers without explicit user consent. Optional, user-controlled cloud backup is permitted. |
| NFR-05 | Security | Local database must be encrypted at rest (SQLCipher or Android Keystore-backed encryption). |
| NFR-06 | Offline Operation | All assessment, scoring, report-generation, and progress-tracking functions must work fully offline. Health Connect sync requires only device-side data availability. |
| NFR-07 | Compatibility | Minimum Android API Level 26 (Android 8.0). Health Connect features require API Level 28+ and must be **gracefully disabled on API 26–27**. Target API Level must track current Android release. |
| NFR-08 | Maintainability | Scoring logic must be isolated in a dedicated, independently testable module. Changing a scoring threshold must not require UI changes. |
| NFR-09 | Localisation | Architecture must support future localisation. Phase 1 delivers English only. |
| NFR-10 | PDF Quality | Exported PDF must be legible when printed at A4 size. Must embed fonts. |
| NFR-11 | Distribution | Phase 1 build must be configured for **public Google Play Store distribution** using a production keystore and a published privacy policy. Internal sideloading is also supported for testing. |

---

## 6. User Flow

```
First Launch
    │
    └─► Onboarding (3 screens: Welcome → How It Works → Privacy Promise)
              │
              ▼
         Profile Setup (name, DOB, gender, height, weight)
              │
              ▼
    ┌────────────────────────────────────────────────┐
    │           Home / Personal Dashboard             │
    │  (subsequent launches start here directly)      │
    └────────────────────────────────────────────────┘
              │
              ├─► [Start New Assessment]
              │        │
              │        ▼
              │   Occupation & Work Pattern Screen
              │        │
              │        ▼
              │   Lifestyle Assessment Screen
              │        │
              │        ├─[API 28+ AND Health Connect available?]
              │        │   YES → Import + confirm (editable)
              │        │   NO  → Manual entry (all fields)
              │        │
              │        ▼
              │   Pain Assessment Screen
              │        │
              │        ▼
              │   Functional Assessment Screen
              │        │
              │        ▼
              │   Red Flag Screening Screen
              │        │
              │        ▼
              │   Assessment Review Screen (all inputs, editable)
              │        │
              │        ▼
              │   [Confirm & Calculate]
              │        │
              │        ▼
              │   Scoring Engine (background)
              │        │
              │        ▼
              │   Results Dashboard Screen
              │        │
              │        ├─► View Full Report Screen
              │        │        │
              │        │        └─► Export / Share PDF ("Share with my doctor")
              │        │
              │        └─► [Done → Home]
              │
              ├─► [My Progress]  (shown after 2+ completed assessments)
              │        │
              │        ├─► SSS Score trend chart
              │        ├─► Lifestyle Risk trend chart
              │        ├─► Component trend cards
              │        └─► Assessment history list → tap → view past report
              │
              └─► [Profile & Settings]
                       │
                       ├─► Edit personal profile (demographics)
                       ├─► Reminder settings (interval or off)
                       ├─► Health Connect permissions (API 28+ only)
                       ├─► Units: metric / imperial
                       ├─► Export all my data
                       ├─► Delete all my data
                       └─► App version and attribution
```

**V2 engagement loop (parallel to the clinical flow above).** The dashboard,
Progress, Shop, and Awards screens are persistent bottom-nav tabs. Each day the
user can check in their mood (+5 coins / +15 XP) and keep their streak alive;
each wizard stage pays +10 coins / +20 XP with a brief stage-complete
interstitial; completing an assessment pays +50 / +100, advances the streak,
and may unlock achievements or a level-up — each celebrated once. Coins are
spent in the Shop on avatar cosmetics. The clinical flow itself (sections,
scoring, classification, reporting) is unchanged by any of this.

---

## 7. Screens and Features

### Screen 0 — Onboarding (first launch only)
- **Screen 0a — Welcome**: App logo, name "SpineIQ", tagline ("Understand your back pain. Track your progress."), "Get Started" button. Not skippable.
- **Screen 0b — How It Works**: Brief explanation of the Spine Severity System (SSS), what the score (0–11) represents, and how lifestyle factors are assessed. "Skip" and "Next" options.
- **Screen 0c — Your Privacy**: Explicit statement that all data is stored only on this device. Optional cloud backup is user-controlled. No data is shared with anyone without your permission. "Skip" and "Let's Start" options.

### Screen 1 — Home / Personal Dashboard *(V2 hub)*
The dashboard is the central engagement hub, reached via a persistent bottom navigation bar (Home · Progress · Shop · Awards). Full-screen pushes (wizard, results, settings, profile) hide the bar.
- **Header**: "SpineIQ" title, Spine Coins balance pill (tap → Shop), Settings access
- **Avatar hero card** (brand gradient): the user's avatar inside a level-progress ring with "LV n" pill, personalised greeting, level-name chip, XP progress bar, streak flame
- **Daily check-in card** — mood buttons (Better / Same / Worse) with reward preview; after check-in shows confirmation + last-7-days dot row (FR-25)
- **Today's goals card** — daily check-in + complete-an-assessment rows with reward chips and done-state checks
- **Start New Assessment** — primary CTA button, with "+100 coins / +200 XP earned on completion" reward preview beneath
- **Recent achievements strip** — last unlocked badges + the next locked badge with progress ring; "View all" → Awards
- **Last Assessment Summary Card** — shown after first completed assessment: score badge, severity tier, date, "View Report" link
- **Motivational insight card** — rotating static wellness tip
- Privacy assurance row ("Private & encrypted…")

### Screen 2 — Personal Information (Profile Setup / Edit)
- Name (text input)
- Date of Birth (date picker — age auto-calculated and displayed)
- Gender (Male / Female / Other / Prefer not to say)
- Height (cm) — numeric with unit toggle
- Weight (kg) — numeric
- BMI — auto-calculated and displayed inline with category label
- Note displayed: "This information stays on your device and personalises your assessment results."

### Screen 3 — Occupation & Work Pattern
- Occupation type (single-select): Office Worker, Field Worker, Driver, Homemaker, Student, Manual Labor, Other
- Sitting hours per day (slider + numeric: 0–24)
- Standing hours per day (slider + numeric)
- Driving hours per day (slider + numeric)
- Lifting activity level (None / Light / Moderate / Heavy — with descriptor for each)
- Work pattern notes (optional free text)

### Screen 4 — Lifestyle Assessment & Health Data
- **Health Connect import banner** (API 28+ only, if permission available): shows last synced values with option to confirm or override. Labelled "from your wearable".
- On API 26–27: Health Connect banner hidden; all fields for manual entry.
- Sleep hours per night (slider + numeric: 0–12)
- Sleep quality (Poor / Fair / Good / Excellent) — feeds into Lifestyle Risk score as a mathematical modifier
- Walking minutes per day (slider + numeric)
- Daily steps (numeric — auto-filled from Health Connect if available)
- Exercise frequency (days per week: 0–7)
- Exercise type (multi-select: Walking, Running, Cycling, Swimming, Gym/Weights, Yoga/Pilates, Other, None) — high-impact types apply a moderate additive risk modifier
- Active minutes per day (numeric)
- Sedentary time per day (numeric — auto-derived from Health Connect if available)
- Resting heart rate (numeric — optional, from wearable — displayed as longitudinal trend)
- Average daily heart rate (numeric — optional, from wearable — displayed as longitudinal trend)

### Screen 5 — Pain Assessment
- Pain location — **text-based checklist** (multi-select):
  - Upper Back
  - Mid Back
  - Lower Back / Lumbar
  - Sacral / Coccyx
  - Left Hip / Buttock
  - Right Hip / Buttock
- Pain intensity — VAS scale (0–10 with face/emoji slider and severity label)
- Pain duration (< 3 weeks / 3–6 weeks / > 6 weeks)
- Pain pattern (Constant / Intermittent / Activity-related / Position-related)
- Pain triggers (multi-select: Sitting, Standing, Walking, Lifting, Bending, Morning stiffness, Rest/sleep, No clear trigger)
- Radiation symptoms / leg symptoms (None / Mild occasional / Moderate affecting activities / Severe constant with walking limitation)
- Radiation location (Left leg / Right leg / Both legs — shown only if radiation > None)
- Functional limitations (free-text + optional structured severity: None / Mild / Moderate / Severe)

### Screen 6 — Functional Assessment (SSS Modified ODI)
For each activity, select: Normal (0) / Mild Difficulty (1) / Severe Difficulty (2).
- Walking
- Sitting
- Standing
- Sleep
- Daily Activities
- ODI Total Score (0–10) and corresponding ODI Points (0–2) displayed inline.

### Screen 7 — Red Flag Screening
- Introductory explanation: "These questions help identify symptoms that need urgent medical attention. Answer honestly — your safety comes first."
- Checklist (each item Yes/No):
  - History of cancer
  - Unexplained weight loss
  - Fever or active infection
  - Recent major trauma / injury
  - Bowel or bladder dysfunction (new onset)
  - Saddle anaesthesia (numbness in groin/inner thighs)
  - Progressive neurological deficit
  - Other serious symptoms you are concerned about
- **Alert banner** if any item is checked: "One or more of these symptoms may indicate a serious condition. **Please seek immediate medical attention from a doctor or emergency department. Do not delay.** Your score will be set to the maximum severity level."

### Screen 8 — Assessment Review
- Scrollable summary of all entered data, grouped by section
- Edit button on each section card navigates back to that screen
- "Confirm and Calculate My Score" primary button at bottom
- Warning if any required field is empty

### Screen 9 — Results Dashboard
- User name and assessment date in header
- **Total SSS Score** displayed prominently (large numeral, 0–11) with "Your Spine Severity Score" label
- Severity tier badge: Low (green) / Mild-Moderate (amber) / Moderate-Severe (orange) / Severe/High Risk (red)
- Plain-language severity description ("What this means for you")
- **Lifestyle Risk tile**: Low / Moderate / High with composite habit summary
- Sub-score breakdown (expandable): VAS, Radiculopathy, ODI, BMI, Chronicity, Red Flag
- Individual lifestyle component indicators: Sitting, Walking, Exercise, Sleep — each shown as adequate/warning/risk
- **Improvement delta** (shown if prior assessment exists): e.g., "↓ 2 points vs your last check-in on [date]"
- Heart rate trend chart (shown if 2+ assessments with heart rate data exist)
- Action buttons: **View Full Report** | **Share with My Doctor (PDF)** | **Start New Assessment**

### Screen 10 — Full Assessment Report (In-App)
Scrollable, section-by-section personalised report. See Section 12 for content requirements. All narrative written in second person ("your pain", "your risk").

### Screen 11 — My Progress
- Available after 2+ completed assessments
- **SSS Score chart**: line chart showing Total SSS Score across all assessments (most recent on right), with date labels
- **Lifestyle Risk chart**: line chart of Lifestyle Risk tier over time
- **Component trend cards**: current vs. previous value for Sitting, Walking, Exercise, Sleep — with direction indicator (improved / worsened / unchanged)
- **Assessment history list**: chronological list of completed assessments with score badge and date; tap to view the full report for that assessment

### Screen 12 — Settings
- **Profile**: edit personal information (name, DOB, gender, height, weight)
- **Reminders**: toggle on/off, set reassessment interval (1–12 weeks; default 4 weeks)
- **Health Connect**: manage permissions, view last sync time (API 28+ only)
- **Units**: metric / imperial toggle
- **Data Management**: export all my data (JSON or PDF bundle), delete all my data (confirmation required)
- **Privacy**: link to in-app privacy statement
- **App version and attribution**: SpineIQ version, SSS methodology credit (Dr. Ayush Sharma)

### Screen 13 — Avatar Shop *(V2)*
- Header with Spine Coins balance pill (purchase feedback target)
- Live avatar try-on preview panel (previewing an unowned item shows "Previewing" + reset)
- Category filter chips: All / Hair / Tops / Bottoms / Accessories
- 2-column item grid: each card shows the mannequin wearing that item, name, and state — gold price pill (dimmed when unaffordable) / "Equip" / "Equipped" (highlighted border)
- Purchase confirmation bottom sheet: item preview, price, balance-after, Buy button (disabled + earn-hint when unaffordable); successful purchase auto-equips
- Cosmetics only — no health functionality is ever gated by coins (FR-23)

### Screen 14 — Awards (Achievements) *(V2)*
- Header with unlocked count ("x of y unlocked") and overall collection progress bar
- 3-column badge gallery in catalog order: unlocked badges in reward styling, locked badges greyed with lock glyph and partial-progress ring
- Badge detail bottom sheet: large badge, description, coin/XP reward, unlock date or "How to earn" hint (FR-24)

### Wizard gamification overlay *(V2 — applies to Screens 3–8)*
- The step progress bar is replaced by a 6-node **journey indicator** ("Stage n of 6") with a "+10 coins / +20 XP per stage" preview chip
- Completing a stage shows a brief, skippable **stage-complete interstitial** (check pop-in, reward chip, small confetti) before auto-advancing (~1.1 s)
- Submitting from Review triggers a one-shot **completion celebration** on the Results screen (confetti + "+100 / +200 XP" chip); re-viewing old results never replays it (FR-26)

---

## 8. Data Fields Collected

### 8.1 User Demographics

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
| Assessment Date | Date | Auto | |

> Note: Phone number and OPD/Patient ID fields are not collected. This is a personal self-assessment app with a single user profile.

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
| Sleep Quality | Manual Enum | Yes | Lifestyle Risk mathematical modifier |
| Walking Minutes/Day | Manual / Health Connect | Yes | Lifestyle Score component |
| Daily Steps | Manual / Health Connect | Yes | Activity Score |
| Exercise Days/Week | Manual | Yes | Lifestyle Score component |
| Exercise Type(s) | Manual Multi-select | Yes | Moderate additive risk modifier for high-impact types |
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

> The SSS Modified ODI (5 activities × 0–2 scale, total 0–10) is implemented exactly as documented (OQ-04).

### 8.6 Red Flag Screening

Eight binary (Yes/No) items — see Screen 7 for full list.

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

> On API 26–27 devices all Health Connect data types are captured via manual entry only.

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
- The app displays a prominent, consumer-facing urgent medical attention notice — not a clinical referral notice.
- The report includes: "One or more red flag symptoms were identified. Please seek immediate medical attention from a doctor or emergency department."

### 9.4 Health Connect Data Handling

1. On the Lifestyle Assessment screen, the app checks the device API level.
2. On **API 28+** devices: the app requests Health Connect read permissions contextually.
3. If granted, the app queries the most recent 7-day average for each relevant data type.
4. Pre-filled values are shown as editable fields with a "from wearable" label.
5. The user may override any auto-filled value.
6. The source of each value (wearable / manual) is stored and shown in the report.
7. On **API 26–27** devices, or if permission is denied: all fields default to manual entry. Health Connect UI elements are hidden on unsupported API versions.

### 9.5 Sleep Quality Modifier

Sleep quality (Poor / Fair / Good / Excellent) is incorporated as a mathematical modifier applied to the Lifestyle Risk score. It adjusts the composite Lifestyle Risk tier upward when sleep quality is Poor or Fair, and confirms or improves the tier when Good or Excellent. The exact weighting is defined in Section 10.2.

### 9.6 Exercise Type Risk Modifier

When the user selects high-impact or spine-loading exercise types (e.g., heavy weightlifting, running), the scoring engine applies a **moderate additive risk modifier** to the exercise component of the Lifestyle Risk calculation. This adjusts the exercise risk tier one step upward at most — it is not an absolute penalty.

### 9.7 Heart Rate Trend Processing

Resting heart rate and average heart rate values are stored per-assessment. When two or more assessments exist with heart rate data, the app calculates and displays inter-assessment deltas as a longitudinal trend chart. No threshold-based diagnostic alerts or medical warnings are generated from heart rate values.

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

> Implements the SSS Modified ODI variant exactly (OQ-04). No other ODI variant is used.

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

Each lifestyle parameter is scored independently and combined into an overall Lifestyle Risk tier. Three-tier system (Low / Moderate / High) — a strict two-tier binary is not used.

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

The scoring engine applies age-group-specific thresholds for walking and sitting per the Daily Habit Snapshot reference table. The "good" and "high risk" reference points are used as the Low and High tier anchors; the Moderate tier band is **interpolated between them** for each age group (OQ-06).

#### Lifestyle Risk Aggregate

- **Low Risk:** All four components (post-modifier) score Low
- **Moderate Risk:** One or more components score Moderate, none score High
- **High Risk:** One or more components score High

Sub-scores for individual lifestyle components are stored separately and displayed individually on the Results Dashboard and Progress screen.

---

### 10.3 Composite Back Pain Risk Score

The final Back Pain Risk Score combines the SSS Score and the Lifestyle Risk tier using the following **SSS × Lifestyle combination matrix** (OQ-05):

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

| Total SSS Score | Severity Tier | Description | Suggested Next Steps |
|---|---|---|---|
| 0–3 | LOW | Minimal pain and disability. Low impact on daily activities. | Maintain healthy habits. Self-monitor. Consider a postural assessment. |
| 4–6 | MILD–MODERATE | Mild to moderate pain/disability. Some functional limitation. | Consider talking to a physiotherapist. Focus on lifestyle improvements. Reassess in 4–6 weeks. |
| 7–9 | MODERATE–SEVERE | Moderate to severe pain/disability. Marked limitation in activities. | We recommend speaking with a doctor or spine specialist about your results. |
| 10–11 | SEVERE / HIGH RISK | Severe symptoms / high risk. Possible serious underlying condition. | **Please seek urgent medical attention. Share this report with a doctor immediately. Do not delay.** |

### 11.2 Lifestyle Risk Tiers

| Tier | Criteria | Implication |
|---|---|---|
| Low Risk | All components (post-modifier) score Low | Healthy spine-supportive habits |
| Moderate Risk | One or more components score Moderate, none score High | Lifestyle adjustment recommended |
| High Risk | One or more components score High | Significant lifestyle changes are recommended |

### 11.3 Red Flag Rule

Any confirmed red flag triggers an immediate classification of **Severe / High Risk** regardless of all other scores. The report must include a prominent, consumer-facing urgent medical attention notice. The language must never reference clinical referral systems — it must directly encourage the user to see a doctor.

---

## 12. Reporting Requirements

The in-app report and exported PDF must include all sections listed below. All narrative is written in **second person** ("your back pain", "your lifestyle habits"). The "Probable Contributors" narrative is generated by a **static rules engine** (FR-10).

### 12.1 Report Header
- App name and SpineIQ generic logo/branding
- Report title: "Your Back Pain Risk Assessment Report"
- Your Name, Age, Gender
- Assessment Date
- Assessment Number (e.g., "Assessment #3")

### 12.2 Section 1 — Your Summary
- Full demographics
- BMI value and category
- Occupation and work pattern summary

### 12.3 Section 2 — BMI Analysis
- BMI value and category
- Mechanical load interpretation (written in second person):
  - Normal: your weight is not adding extra load to your spine
  - Overweight: moderate additional spinal load
  - Obese: significant mechanical overload contributing to disc and joint stress
- Comparison to age-appropriate healthy range

### 12.4 Section 3 — Lifestyle Assessment
- Summary table: Sitting / Walking / Exercise / Sleep — actual value | target range | status (adequate / at risk / high risk)
- Sleep quality value and its effect on the Lifestyle Risk score
- Age-specific context (e.g., "For your age group (31–45), the recommended sitting limit is under 7 hours/day")
- Overall Lifestyle Risk tier with plain-language explanation
- Each at-risk habit explained with its spine health consequence in accessible language

### 12.5 Section 4 — Activity Assessment
- Steps, active minutes, exercise frequency (actual values)
- Exercise type(s) recorded, with notation if high-impact modifier was applied
- Activity Score rating
- Comparison to recommended thresholds
- Data source indicated (wearable / manual entry)

### 12.6 Section 5 — Occupational Risk Assessment
- Occupation type
- Daily sitting / standing / driving hours
- Lifting activity level
- Occupational risk interpretation in plain language

### 12.7 Section 6 — Pain Pattern Analysis
- Pain location(s) listed
- VAS score and severity label
- Pain duration and pattern
- Leg symptoms severity
- Triggers identified
- Chronicity classification

### 12.8 Section 7 — Functional Assessment Summary
- ODI activity scores table (SSS Modified ODI)
- Total ODI score and disability tier
- Functional limitations summary

### 12.9 Section 8 — Red Flag Status
- Red flag items reviewed (listed)
- Confirmed red flags highlighted
- If any flag is positive: prominent urgent medical attention notice — "Please see a doctor or go to an emergency department immediately."

### 12.10 Section 9 — Key Risk Factors
- Bulleted list of your identified risk factors ranked by contribution severity

### 12.11 Section 10 — Your Back Pain Risk Score (Results)
- Total SSS Score (0–11) with visual indicator
- Severity Tier badge
- Lifestyle Risk Tier
- Composite Back Pain Risk Classification
- Sub-score breakdown table
- Individual lifestyle component scores

### 12.12 Section 11 — Probable Contributors to Your Pain
- Narrative paragraph generated by the static rules engine explaining the likely contributors to your back pain based on the data you entered. Must:
  - Avoid mentioning specific treatments or products
  - Reference only the data collected in this assessment
  - Use plain, accessible language written in second person
  - Prioritise the top 2–3 contributors identified

### 12.13 Section 12 — Lifestyle Improvement Tips
- Per at-risk lifestyle component, 1–2 brief, actionable, non-prescriptive tips
- Generated by the static rules engine
- Framed as general wellness information, not medical advice
- Examples: "Taking a short walk every 30 minutes can help relieve disc pressure from prolonged sitting"

### 12.14 Section 13 — Recommended Next Steps
Based on severity tier:

| Tier | Recommended Next Steps |
|---|---|
| Low | Maintain healthy habits. Self-monitor. Consider a postural assessment. |
| Mild-Moderate | Consider talking to a physiotherapist. Focus on lifestyle improvements. Reassess in 4–6 weeks. |
| Moderate-Severe | We recommend speaking with a doctor or spine specialist about your results. |
| Severe / Urgent | Please seek urgent medical attention. Share this report with a doctor immediately. Do not delay. |

### 12.15 Report Footer
- **Consumer disclaimer:** "This report is generated by a self-assessment screening tool and does not constitute medical advice. It is not a substitute for consultation with a qualified healthcare professional. If you are concerned about your symptoms, please consult a doctor."
- App version and scoring methodology reference (SSS v1.0, Dr. Ayush Sharma)

> Note: QR code and external URL are omitted from the report footer in Phase 1 (OQ-09).

---

## 13. PDF Export Requirements

### 13.1 Format
- Page size: A4 (210 × 297 mm)
- Orientation: Portrait
- Margins: 15 mm all sides
- Font: embedded sans-serif (Roboto)
- Colour: risk-tier colours for score badges — must print legibly in greyscale

### 13.2 Branding
- Generic SpineIQ branding (app name and logo) in the PDF header
- No customisable headers or clinic branding

### 13.3 Content
- All 13 report sections listed in Section 12 above
- User name and assessment date in header on all pages
- Page numbering: "Page X of Y"
- Assessment date in footer of every page

### 13.4 Generation
- PDF is generated entirely on-device (no server round-trip)
- PDF library: `android.graphics.pdf.PdfDocument` (on-device)
- Generated file is saved to app's private storage and offered via the Android share sheet

### 13.5 Naming Convention
```
SpineIQ_Assessment_[YYYYMMDD].pdf
```

### 13.6 Share Sheet
- Default suggested action label: **"Share with my doctor"**
- Standard Android share sheet used — supports any app (email, WhatsApp, Drive, etc.)

### 13.7 Accessibility
- PDF must include semantic tags for headings and tables where feasible

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
| API 26–27 (Android 8.0–8.1) | Gracefully disabled/hidden. All fields presented for manual entry. |
| API 28+ (Android 9.0+) | Full Health Connect integration available. Permission requested contextually on Lifestyle screen. |

### 14.3 Permission Handling
- Permissions requested contextually on the Lifestyle Assessment screen (not at launch)
- If denied, all fields fall back to manual entry without degraded functionality
- "Why we need this" explanation displayed before the permission dialog

### 14.4 Data Query Window
- Default: 7-day rolling average ending at midnight of the assessment date
- Display: "Based on your last 7 days of data"
- User may override any auto-populated value

### 14.5 Supported Source Devices
Wearable-agnostic. Any device writing to Android Health Connect is supported, including Google Fit / Pixel Watch, Fitbit, Samsung Galaxy Watch, Garmin, Amazfit, Xiaomi Mi Band, and any third-party app writing to Health Connect.

### 14.6 Offline Behaviour
Health Connect data is read from the device's local store. No external network request is made.

### 14.7 Data Source Tracking
The app must record, for each auto-filled field, whether the value was sourced from Health Connect or entered manually. This provenance is shown in the report.

---

## 15. Local Data Storage Requirements

### 15.1 Database
- Technology: Room (Jetpack) with SQLCipher encryption
- Storage location: App-internal private storage
- No data stored in external or shared storage without an explicit user export action

### 15.2 Data Entities

#### User Profile (single record per installation)
```
UserProfile {
  id: UUID (primary key)
  fullName: String
  dateOfBirth: LocalDate
  gender: Enum
  heightCm: Float
  weightKg: Float
  createdAt: Timestamp
  updatedAt: Timestamp
}
```

> One `UserProfile` record exists per installation. There is no list of profiles and no multi-user support. No external patient or OPD ID field is defined.

#### Assessment Record
```
AssessmentRecord {
  id: UUID (primary key)
  userId: UUID (foreign key → UserProfile)
  assessmentDate: LocalDate
  assessmentNumber: Int            // sequential counter per user
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

#### Scores Record
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
  sleepQualityModifier: Enum
  exerciseTypeModifier: Boolean
  lifestyleRiskTier: Enum
  backPainRiskClassification: Enum
  computedAt: Timestamp
}
```

#### Gamification Entities *(V2 — DB schema v3; all cascade-delete from UserProfile)*

```
GamificationState {              // single row: wallet + streak state
  userId: UUID (PK, FK → UserProfile)
  coins: Int                     // spendable, never negative
  xp: Int                        // lifetime, monotonic — level is DERIVED, never stored
  currentStreakDays: Int
  longestStreakDays: Int
  lastActivityDay: EpochDay?     // last streak-qualifying event
  updatedAt: Timestamp
}

RewardLedger {                   // append-only; THE idempotency mechanism
  dedupeKey: String (PK)         // e.g. step:{assessmentId}:{STEP}, complete:{id},
                                 //      checkin:{epochDay}, streak_milestone:{n},
                                 //      achievement:{id}, purchase:{itemId}
  userId: UUID (FK)
  rewardType: Enum
  coinsDelta: Int                // negative for purchases
  xpDelta: Int
  createdAt: Timestamp
}

AchievementUnlock {              // definitions live in code (AchievementCatalog)
  achievementId: String (PK)
  userId: UUID (FK)
  unlockedAt: Timestamp
}

DailyCheckIn {                   // PK enforces once per calendar day; rows kept for mood history
  checkInDay: EpochDay (PK)
  userId: UUID (FK)
  mood: Enum (BETTER / SAME / WORSE)
  createdAt: Timestamp
}

AvatarItem {                     // owned/equipped state only; catalog lives in code (AvatarCatalog)
  itemId: String (PK)
  userId: UUID (FK)
  category: Enum (HAIR / TOPS / BOTTOMS / ACCESSORIES)
  equipped: Boolean              // at most one per category
  purchasedAt: Timestamp
}
```

> Free default items (one per clothing category) have no `AvatarItem` row — they are implicitly owned, and equipping a default simply clears the category. Inserts into `RewardLedger`, `AchievementUnlock`, and `DailyCheckIn` use insert-or-ignore on the primary key, which is what guarantees every reward is granted exactly once (FR-21).

### 15.3 Data Retention
- All data retained indefinitely on-device until explicitly deleted by the user
- "Delete all my data" cascades to delete the user profile and all associated assessments, scores, and gamification data (coins, XP, streaks, check-ins, achievements, owned avatar items)
- Export prompt shown before deletion

### 15.4 Backup
- Optional, user-controlled cloud backup is permitted (OQ-12)
- Users may enable cloud backup via Settings
- Data included in Android Auto Backup if enabled — encrypted database may not restore correctly across devices; document this limitation in-app

### 15.5 In-Progress Assessment Persistence
- Navigating away mid-assessment persists state to the database with status IN_PROGRESS
- On next app launch, the user is prompted to resume or discard the in-progress assessment

---

## 16. Future Enhancements

The following features are explicitly out of scope for Phase 1 but should be considered in architectural design.

| ID | Feature | Phase |
|---|---|---|
| FE-01 | Cloud sync and multi-device access | Phase 2 |
| FE-02 | AI-assisted personalised insights using longitudinal data | Phase 2 |
| FE-03 | AI-generated "Probable Contributors" narrative (replacing static rules engine) | Phase 2 |
| FE-04 | iOS application (SwiftUI + Apple HealthKit) | Phase 2 |
| FE-05 | Personalised exercise and lifestyle recommendations engine | Phase 3 |
| FE-06 | Outcome tracking — did lifestyle changes improve your score? | Phase 3 |
| FE-07 | Wearable companion app (Wear OS) for continuous monitoring | Phase 3 |
| FE-08 | DICOM / imaging integration (X-ray, MRI annotation) | Phase 3 |
| FE-09 | Multi-language support (Hindi, Arabic, Urdu priority) | Phase 2 |
| FE-10 | Body diagram pain input (interactive tap regions replacing text checklist) | Phase 2 |
| FE-11 | Social sharing — share score card to social/messaging apps (opt-in) | Phase 2 |
| FE-12 | Doctor-sharing portal — structured results shared directly with a healthcare provider account | Phase 2 |
| FE-13 | Anonymised research data export (de-identified, consent-gated) | Phase 2 |
| FE-14 | Goal setting — user sets *custom* lifestyle improvement targets and tracks progress toward them (V2 ships fixed daily goals only) | Phase 2 |
| ~~FE-15~~ | ~~Achievement system — milestone badges for improvement streaks~~ — **delivered early in Phase 1 V2** (FR-24), expanded into the full gamification layer (FR-21 – FR-26) | ✅ Done |

> Removed from future scope: Clinician web dashboard, Clinic admin portal, Clinician-customisable PDF branding, HL7 FHIR integration. These are clinic-mode features that do not apply to a D2C product.
>
> V2 gamification follow-ups for later phases: avatar catalog expansion + seasonal items, multi-slot accessories, mood-trend visualisation from check-in history, coin sinks beyond cosmetics (always non-health), timezone-robust streak day boundaries.

---

## 17. Acceptance Criteria

### AC-01 — Profile and Data Entry
- ✅ All required fields in Personal Information, Occupation, Lifestyle, Pain, Functional, and Red Flag screens are completable by an individual user without external documentation
- ✅ BMI is auto-calculated and displayed within 100ms of entering height and weight
- ✅ Date of Birth field automatically derives and displays user age
- ✅ Pain location is captured via text checklist (no body diagram)
- ✅ No OPD Number / Patient ID field exists anywhere in the app

### AC-02 — Health Connect Integration
- ✅ App successfully imports steps, sleep, and active minutes from Health Connect on API 28+ devices
- ✅ App functions identically when Health Connect permission is denied
- ✅ On API 26–27 devices, Health Connect UI is hidden and all fields presented for manual entry
- ✅ Auto-filled values are labelled with their data source
- ✅ User can override any auto-filled value

### AC-03 — Scoring Engine
- ✅ VAS Points computed correctly for all three input ranges
- ✅ Radiculopathy Score reflects the correct selected severity level
- ✅ ODI sum and ODI points computed correctly (SSS Modified ODI variant)
- ✅ BMI points correctly reflect all three BMI bands
- ✅ Chronicity points reflect all three duration bands
- ✅ Red Flag override sets Total SSS Score to 11 regardless of other inputs
- ✅ Total SSS Score is the correct sum of all component points (except red flag override)
- ✅ Lifestyle Risk correctly classifies all four components into three tiers
- ✅ Sleep quality modifier correctly adjusts the sleep risk component
- ✅ High-impact/spine-loading exercise types apply the correct moderate additive modifier
- ✅ Age-adjusted thresholds with interpolated Moderate band applied based on user's age group
- ✅ Composite Back Pain Risk classification correct for all SSS × Lifestyle matrix combinations

### AC-04 — Risk Classification
- ✅ SSS tier badge displays correct label and colour for all four tiers
- ✅ Red flag presence always displays "Severe / High Risk" tier
- ✅ Next Steps recommendation matches the severity tier
- ✅ Lifestyle Risk displays three tiers (Low / Moderate / High)

### AC-05 — Report
- ✅ All 13 report sections are present in the in-app report view
- ✅ Probable Contributors section references only data from this assessment (no generic statements; no AI text)
- ✅ Recommended Next Steps match the severity tier
- ✅ Consumer disclaimer (not clinical disclaimer) is present
- ✅ Report is written entirely in second person ("your pain", "your risk")
- ✅ No OPD number, patient ID, or clinician name fields appear anywhere in the report

### AC-06 — PDF Export
- ✅ PDF generated without errors for a completed assessment
- ✅ PDF renders all 13 report sections correctly
- ✅ PDF filename follows `SpineIQ_Assessment_[YYYYMMDD].pdf`
- ✅ PDF is legible when printed at A4 size
- ✅ Share sheet includes "Share with my doctor" as the primary suggested action
- ✅ PDF uses generic SpineIQ branding only

### AC-07 — Data Persistence
- ✅ Completed assessments are stored and retrievable after app restart
- ✅ In-progress assessments are saved and resumable
- ✅ "Delete all my data" deletes the user profile and all associated assessments

### AC-08 — Performance
- ✅ Score computation completes within 3 seconds of "Confirm and Calculate"
- ✅ PDF generation completes within 10 seconds
- ✅ Progress screen loads within 2 seconds for up to 50 stored assessments

### AC-09 — Usability
- ✅ A user can complete a full assessment end-to-end without referring to documentation
- ✅ All scoring terms include accessible plain-language tooltips
- ✅ Red flag warning is visually prominent and uses consumer-appropriate "see a doctor" language — no clinical referral language
- ✅ No minimum reassessment interval prompts or blockers are displayed

### AC-10 — Privacy & Security
- ✅ No data is transmitted to any external server without explicit user consent
- ✅ Database is encrypted at rest
- ✅ Health Connect permissions are requested contextually with a rationale displayed
- ✅ Optional cloud backup toggle is accessible in Settings

### AC-11 — Distribution Build
- ✅ Build is configured for public Google Play Store distribution
- ✅ Production keystore is present and used for release builds
- ✅ Privacy policy is accessible from within the app

### AC-12 — Onboarding
- ✅ Onboarding sequence displays on first launch only
- ✅ Screen 0a (Welcome) is not skippable; screens 0b and 0c have a "Skip" option
- ✅ Completing or skipping onboarding leads to Profile Setup on first launch
- ✅ Subsequent app launches go directly to the Home Dashboard

### AC-13 — Progress Screen
- ✅ Progress screen is not shown until 2+ completed assessments exist
- ✅ SSS score trend chart renders correctly with correct dates on the x-axis
- ✅ Lifestyle Risk trend chart renders correctly
- ✅ Component trend cards show current vs. previous value with direction indicator
- ✅ Improvement delta callout shows correct delta vs. the immediately prior assessment

### AC-14 — Reminders
- ✅ Push notification fires at the configured interval after the most recent completed assessment
- ✅ Tapping the notification opens the Start Assessment flow directly
- ✅ Reminders can be disabled from Settings
- ✅ Default interval is 4 weeks

### AC-15 — Single User Architecture
- ✅ The app stores exactly one user profile
- ✅ There is no patient list, patient management screen, or profile switcher anywhere in the app

### AC-16 — Gamification (V2)
- ✅ A full assessment earns exactly 100 coins and 200 XP; back-navigating and re-saving any section earns 0 extra (ledger dedupe)
- ✅ Daily check-in can be completed once per calendar day; a second attempt the same day grants nothing
- ✅ Streak advances only on check-ins and assessment completions; a missed day displays the streak as 0; milestone bonuses (3/7/14/30) pay exactly once ever
- ✅ Level is derived from XP against the 8-level table; level-up, achievement, and streak-milestone events each show a celebration overlay exactly once
- ✅ Shop purchases atomically deduct coins, mark the item owned, and equip it; purchases with insufficient balance are rejected with no state change
- ✅ No coin price, level, or achievement ever gates an assessment, score, report, or any other health feature
- ✅ The reward palette (violet/gold) never appears on clinical risk tiers, SSS badges, or clinical chart lines
- ✅ A gamification failure (e.g. grant error) never blocks or delays assessment save/completion
- ✅ "Delete all my data" wipes all gamification state via cascade
- ✅ Re-opening a past result from history never replays the completion celebration

---

## 18. Open Questions

All open questions are resolved. The table below is retained for traceability.

| ID | Question | Decision | Impact Area |
|---|---|---|---|
| OQ-01 | Primary user mode | **Self-assessment only.** The user is both operator and subject. No clinician-led or multi-patient mode. | Sections 3, 7, entire UX |
| OQ-02 | Multi-patient support | **Single-user personal app.** One profile per installation. No patient management. | Sections 4 (FR-14), 7, 15 |
| OQ-03 | Body diagram vs checklist | Text-based checklist only. Body diagrams excluded from Phase 1 (FE-10). | Section 7 (Screen 5), 8.4 |
| OQ-04 | ODI variant | SSS Modified ODI implemented exactly as documented (5 activities × 0–2). | Sections 7, 8.5, 10.1 |
| OQ-05 | Composite risk score formula | SSS × Lifestyle combination matrix accepted and implemented. | Section 10.3 |
| OQ-06 | Age-specific threshold — moderate band | Three-tier engine. Moderate band interpolated between reference points. | Sections 4, 10.2, 11.2 |
| OQ-07 | Probable Contributors generation | Static rules engine only. No LLM in Phase 1. | Sections 4, 12.12 |
| OQ-08 | PDF branding | Generic SpineIQ branding. No customisable headers. | Section 13.2 |
| OQ-09 | QR code / URL in footer | Omitted from Phase 1. | Section 12.15 |
| OQ-10 | Patient ID field | **Removed entirely.** OPD Number / Patient ID has no place in a self-assessment consumer app. | Sections 8.1, 15.2 |
| OQ-11 | Minimum Android version | API 26 minimum retained. Health Connect gracefully disabled on API 26–27. | Sections 5, 14.2 |
| OQ-12 | Offline-only constraint | Optional user-controlled cloud backup permitted. | Sections 5, 15.4 |
| OQ-13 | Disclaimer sign-off | Consumer-facing disclaimer used (not clinical referral language). Medical-legal sign-off out of scope for Phase 1. | Section 12.15 |
| OQ-14 | App Store distribution | **Public Google Play Store** distribution for Phase 1. Production keystore required. Privacy policy required. | Section 5 (NFR-11), 17 (AC-11) |
| OQ-15 | Reassessment interval | Unlimited assessments at any frequency. No interval enforcement. Default reminder: 4 weeks. | Sections 4 (FR-15, FR-18), 17 (AC-09, AC-14) |
| OQ-16 | Sleep quality scoring | Sleep quality incorporated as a mathematical modifier in the Lifestyle Risk score. | Sections 4, 9.5, 10.2 |
| OQ-17 | Heart rate data use | Displayed as longitudinal trend (deltas over time). No diagnostic alerts. | Sections 4, 9.7, 15.2 |
| OQ-18 | Exercise type risk weighting | High-impact / spine-loading types apply a moderate additive risk modifier. Not an absolute penalty. | Sections 4, 9.6, 10.2 |
| OQ-19 | Play Store distribution | Public Play Store for Phase 1. Privacy policy required. | Section 5 (NFR-11) |
| OQ-20 | Reassessment reminder default interval | 4 weeks (monthly check-in). User-configurable 1–12 weeks. | Section 4 (FR-18) |
| OQ-21 | Onboarding skip behaviour | Screen 0a (Welcome) not skippable. Screens 0b–0c skippable. Shown on first launch only. | Section 4 (FR-17), 7 (Screen 0) |

---

*Document prepared based on: Stakeholder direction (2026-06-08), Daily Habit Snapshot — Spine Severity System v1.0 (Dr. Ayush Sharma), Spine Severity Assessment System (SSS) Rapid Screening Form v1.0, and Open Questions Resolution Log (OQ-01 through OQ-21).*

*This is version 2.0 of the specification. Version 1.1 defined a clinician-led Clinic Mode architecture; v2.0 redefines the product as a direct-to-consumer self-assessment application. All scoring mathematics are unchanged from v1.1.*

*This specification represents Phase 1 scope only. All future enhancement items are recorded in Section 16 and are explicitly excluded from Phase 1 delivery.*
