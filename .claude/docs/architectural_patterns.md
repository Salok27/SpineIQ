# Architectural Patterns

Recurring patterns in SpineIQ. Each is cited with representative files. One-off
implementations are intentionally excluded.

## Single-user local-first architecture

SpineIQ is a **D2C personal health app** — one installation, one user. There is no
patient management, no multi-tenant data model, no role-based access control, and
no clinic or enterprise network layer.

Key constraints:
- **Single `UserProfile` record per install.** The profile is set up once on first
  launch and edited from Settings. There is no "patient list", no patient selection
  screen, and no concept of a "logged-in" role beyond the local device owner.
- **Offline-first, on-device only.** All scoring, classification, and report
  generation runs entirely on-device. There are no API calls, no telemetry, and no
  cloud sync in Phase 1. The app must be fully functional without network access.
- **Encrypted at rest.** The Room DB is encrypted via SQLCipher with a
  Keystore-backed passphrase (`DatabaseKeyProvider`). The user's health data never
  leaves the device in Phase 1.
- **Optional cloud backup is out of scope for Phase 1.** If added later, it must be
  user-initiated and user-controlled (not automatic).
- **No RBAC.** There are no user roles, no admin screens, and no permission tiers.
  The installed app is the user's personal tool.

## Module dependency flow

Strict one-directional graph (`settings.gradle.kts`):

```
:app  ──►  :core:scoring   (pure Kotlin/JVM, no Android deps except via data models)
  │   ──►  :core:data       (Room + SQLCipher + Hilt; depends on :core:scoring for result models)
  │   ──►  :core:pdf         (android.graphics.pdf; depends on :core:data + :core:scoring)
```

- `:core:scoring` is the lowest layer. It must never import Android UI, Room, or Hilt.
- `:core:data` depends on `:core:scoring` only to persist `ScoringResult` (see
  `ScoresRecordEntity.fromScoringResult`, `core/data/.../entity/ScoresRecordEntity.kt:124`).
- `:app` wires everything together via Hilt and Compose navigation.
- Never introduce a reverse dependency (e.g. `:core:scoring` importing `:core:data`).

## Pure scoring engine

The entire scoring domain is **side-effect-free pure functions** — no I/O, no
coroutines, thread-safe, independently unit-testable (satisfies NFR-08).

- Entry point: `ScoringEngine.compute(input)` → `ScoringResult`
  (`core/scoring/.../ScoringEngine.kt:16`).
- Delegates to two stateless `object`s: `SssScorer` (SSS 0–11 components) and
  `LifestyleScorer` (three-tier lifestyle risk + modifiers).
- Inputs are plain data classes in `core/scoring/.../model/ScoringInput.kt`;
  outputs in `ScoringResult.kt`. The engine takes *no* entities — the caller maps
  DB/draft state into `AssessmentInput`.
- Thresholds are isolated in `core/scoring/.../thresholds/AgeThresholds.kt` so a
  threshold change requires no logic or UI change. Add new threshold tables here.

When changing scoring rules, edit only `:core:scoring` and update
`ScoringEngineTest.kt`. See `domain_knowledge.md` for the rule definitions.

## MVVM with Hilt + StateFlow + Compose

Every screen follows the same shape:

- `@HiltViewModel class XxxViewModel @Inject constructor(repos…)` exposes a single
  immutable UI-state data class via `StateFlow` (`MutableStateFlow` + `asStateFlow()`).
- Composables collect with `collectAsStateWithLifecycle()` and obtain the VM via
  `hiltViewModel()`.
- State mutation uses `_state.update { it.copy(...) }`; side effects run in
  `viewModelScope.launch { … }`.

Representative: `ui/home/HomeViewModel.kt`, `ui/results/ResultsViewModel.kt`,
`ui/settings/SettingsViewModel.kt`.

## Bottom-nav hub + full-screen pushes (V2 navigation)

`NavGraph.kt` wraps the single `NavHost` in a `Scaffold` whose bottom bar
(`navigation/SpineIQNavBar.kt`) is shown only when the current route is one of
the four hub tabs (`BottomNavDestination`: Home, Progress, Shop, Awards).
Everything else (wizard, results, settings, profile, onboarding) is a
full-screen push that hides the bar.

- The outer Scaffold sets `contentWindowInsets = WindowInsets(0)` — screens keep
  handling their own status-bar insets; only the bottom-bar height flows in.
- Tab navigation uses `popUpTo(Home) { saveState = true }` + `launchSingleTop` +
  `restoreState` so tab state survives switches.
- Hub-to-hub transitions are fast cross-fades; pushes keep the fade+scale
  language. Wizard steps slide horizontally (unchanged).
- `MainActivity` stacks `CelebrationHost()` above the nav graph in a `Box` —
  the global reward/celebration layer (see gamification pattern below).

## Shared-ViewModel assessment wizard

The 6-screen assessment wizard (Occupation → Lifestyle → Pain → Functional →
RedFlag → Review) shares **one** `AssessmentSessionViewModel` scoped to a nested
nav graph, not per-screen instances.

- Defined as a `navigation(route = Screen.AssessmentGraph.route)` block in
  `navigation/NavGraph.kt`.
- Each child screen resolves the parent entry and calls
  `hiltViewModel(parentEntry)` so all screens see the same in-memory draft.
- All routes/args are centralised in `navigation/Screen.kt`.
- V2: each screen shows `JourneyProgressIndicator(currentStep = n)` and routes
  its Next-click through `StageCompleteOverlay`, whose `onFinished` runs the
  existing `persistXxx()` + `navigate()`. `persistXxx()` awards the step reward
  (idempotent) after the section save; `computeAndComplete()` awards the
  completion reward and advances the streak. Review navigates to Results with
  `celebrate = true` for the one-shot completion celebration.

## Draft-state accumulation pattern

In-progress assessment input lives in immutable draft data classes
(`OccupationDraft`, `LifestyleDraft`, `PainDraft`, `FunctionalDraft`,
`RedFlagDraft`) aggregated in `AssessmentSession`
(`ui/common/AssessmentDraftState.kt`).

- The VM exposes typed updaters taking a lambda receiver:
  `updateLifestyle { copy(sleepHoursPerNight = 8f) }`
  (`ui/assessment/AssessmentSessionViewModel.kt:62`).
- Each section has a `persistXxx()` method that maps the draft to its `*Entity`
  and writes via the repository. UI calls `persist…()` on "Next".
- Derived flags live on the draft (e.g. `RedFlagDraft.hasAnyRedFlag`,
  `FunctionalDraft.odiTotal`) so UI and scoring read the same computed value.

## Repository + DAO data access

- Repositories (`AssessmentRepository`, `UserProfileRepository`,
  `GamificationRepository`) are `@Singleton`, constructor-injected with DAOs, and
  expose `suspend` writes + `Flow` reads. Multi-table writes use
  `db.withTransaction { }`.
- One Room DB, `SpineIQDatabase` (`core/data/.../db/SpineIQDatabase.kt`, schema
  v3), five DAOs (`UserProfileDao`, `AssessmentDao`, `ScoresDao`,
  `GamificationDao`, `AvatarDao`) provided by `di/DatabaseModule.kt`.
- Assessment sections are separate tables keyed by `assessment_id`, written with
  `@Insert(onConflict = REPLACE)` upserts. `AssessmentDao.getFullAssessment()`
  (`dao/AssessmentDao.kt:91`) assembles them into `FullAssessmentData` in a
  `@Transaction`.
- Enums are stored via `@TypeConverters` in `db/converters/Converters.kt`; set/list
  enums serialise to delimited strings.
- DB is encrypted with SQLCipher; the passphrase comes from `DatabaseKeyProvider`
  (Keystore-backed). See `di/DatabaseModule.kt:24`.
- All gamification tables FK → `user_profiles(id)` with `ON DELETE CASCADE`, so
  `UserProfileRepository.deleteAllData()` wipes them with no extra code.

## Gamification: ledger-idempotent grants + post-commit events

The V2 engagement layer (coins, XP/levels, streaks, check-ins, achievements,
avatar shop) lives in `core/data` and follows two non-negotiable rules.

**1. Every grant is idempotent via the reward ledger.**
`reward_ledger.dedupe_key` is the primary key; grants insert with
`OnConflictStrategy.IGNORE` and a returned row id of `-1` means "already
granted". Key formats are owned by `DedupeKeys`
(`core/data/.../gamification/GamificationModels.kt`) — e.g.
`step:{assessmentId}:{STEP}`, `complete:{assessmentId}`, `checkin:{epochDay}`,
`streak_milestone:{n}`, `achievement:{id}`, `purchase:{itemId}`. Achievement
counting reuses the same formats via SQL `LIKE`, so never change a key format in
one place. **Never grant coins/XP outside
`GamificationRepository.tryGrant()`** (`core/data/.../repository/GamificationRepository.kt`),
which runs ledger-insert → dedupe-check → state upsert in one `withTransaction`.

**2. Celebration events fire only after the transaction commits.**
`GamificationManager` (`core/data/.../gamification/GamificationManager.kt`) is
the single entry point for the app module. It exposes
`events: SharedFlow<GamificationEvent>` (no replay — a missed event loses an
animation, never currency) and `snapshot: Flow<GamificationSnapshot>` (combined
Room flows + derived level/effective-streak). The activity-scoped
`CelebrationViewModel` (`ui/gamification/`) collects events into a FIFO queue;
`CelebrationHost` (mounted above the NavHost in `MainActivity`) renders toasts
and overlays — and renders *nothing* while idle so it can never intercept taps.
`RewardToastSuppressor` arms a short window around wizard grants because the
stage-complete interstitial already shows the reward inline (prevents double
feedback); overlay celebrations are never suppressed.

Other invariants:
- Rule logic is pure Kotlin with zero Android/Room imports (`Economy`,
  `LevelTable`, `StreakLogic`, `AchievementCatalog`, `AvatarCatalog` in
  `core/data/.../gamification/`) — JVM-tested in `core/data/src/test/`.
- Levels are **derived from XP, never persisted** (`LevelTable.levelFor`), so the
  curve can be retuned without a migration.
- Streaks qualify only on daily check-ins and assessment *completions* (per-step
  saves are repeatable, hence abusable). Breaks are lazy:
  `StreakLogic.effectiveStreak` shows 0 on read; the stored value resets on the
  next qualifying event.
- Calls from the medical flow (`AssessmentSessionViewModel.persistXxx()` /
  `computeAndComplete()`) are `runCatching`-wrapped — **gamification must never
  block an assessment**.
- Cosmetics only: no coin price, level, or achievement may gate any health
  feature; the violet/gold reward palette never appears on clinical risk colors.

## Avatar catalog → draw-layer registry

The avatar is pure Compose Canvas — no image assets. `AvatarCatalog`
(core/data, data only) defines items; `AvatarRegistry` (`ui/avatar/`) maps each
catalog id to an `AvatarLayer` draw function. All layers draw in a fixed
**0..100 unit space** and the renderer (`ui/avatar/AvatarRenderer.kt`) applies
one uniform scale, so the same item code serves 48 dp thumbnails and the 160 dp
dashboard hero. Paint order is fixed: body → bottoms → tops → hair →
accessories. Unknown ids are skipped silently, and missing categories fall back
to `AvatarCatalog.DEFAULTS` (free defaults have no DB row; equipping a default
just clears the category). Adding a cosmetic = one catalog entry + one draw
function + one registry map entry.

## Static rules-engine report generation

The "Probable Contributors" narrative and "Key Risk Factors" are generated by
**static if-then rules over the `ScoresRecordEntity`** — no LLM (FR-10 / OQ-07).
See `buildNarrative()` and `buildRiskFactors()` in `core/pdf/.../PdfExporter.kt:299`.

The in-app report (`ui/results/FullReportScreen.kt`) and the PDF
(`PdfExporter.kt`) render the *same* score data through two separate renderers.
Keep them consistent: a new report section must be added to both.

## Error / null handling

- Reads return nullable types or `Flow<T?>`; screens branch on
  `isLoading / null / content` (see `FullReportScreen.kt:61`).
- Defensive `try/catch` wraps non-critical formatting (e.g. date parsing in
  `PdfExporter.kt:36`, falling back to a placeholder string).
- There is no global error bus; transient errors are surfaced as nullable
  `error` fields on the per-screen state object.
