# Development Workflows

## Build / test / run

Use the Gradle wrapper (`./gradlew` on POSIX, `.\gradlew.bat` on Windows). JDK 11.

| Task | Command |
|---|---|
| Build debug APK | `./gradlew :app:assembleDebug` |
| Full build | `./gradlew build` |
| Scoring unit tests (fast, pure JVM) | `./gradlew :core:scoring:test` |
| Gamification unit tests (fast, pure JVM) | `./gradlew :core:data:testDebugUnitTest` |
| App unit tests | `./gradlew :app:testDebugUnitTest` |
| Instrumented tests (needs device/emulator) | `./gradlew connectedDebugAndroidTest` |
| Install on device | `./gradlew :app:installDebug` |

The scoring module has no Android dependency, so `:core:scoring:test` runs in
milliseconds and is the primary fast-feedback loop for rule changes
(`core/scoring/.../ScoringEngineTest.kt`). The gamification rules (levels,
streaks, achievements, catalogs) are likewise pure Kotlin and covered by JVM
tests in `core/data/src/test/.../gamification/` — run them after touching
anything in `core/data/.../gamification/`.

## Common tasks

### Add or change a scoring rule
1. Edit the relevant pure function in `:core:scoring` (`SssScorer`,
   `LifestyleScorer`, or a threshold in `thresholds/AgeThresholds.kt`).
2. Update `ScoringResult` / `SssResult` / `LifestyleResult` models if a new output
   field is needed.
3. If persisted, add the column to `ScoresRecordEntity` **and** map it in
   `fromScoringResult()`, then bump the Room schema (see migrations note below).
4. Surface it in `FullReportScreen.kt` and `PdfExporter.kt` if it appears in reports.
5. Add/extend cases in `ScoringEngineTest.kt`.

### Add a new assessment input field
1. Add to the relevant `*Draft` in `ui/common/AssessmentDraftState.kt`.
2. Bind it in the screen composable under `ui/assessment/` and call the matching
   `updateXxx { copy(...) }`.
3. Add the column to the section `*Entity`, map it in the VM's `persistXxx()`
   (`AssessmentSessionViewModel.kt`).
4. If it feeds scoring, add it to the `*Input` model and thread it through
   `computeAndComplete()`.

### Add a new screen
1. Add a route + args to `navigation/Screen.kt`.
2. Register a `composable(...)` in `navigation/NavGraph.kt`. Wizard screens go
   inside the `AssessmentGraph` nested graph and use `hiltViewModel(parentEntry)`.
3. Create `XxxScreen.kt` + `XxxViewModel.kt` following the StateFlow/Hilt pattern
   in `architectural_patterns.md`.

### Add a persisted entity / DAO query
1. Add the `@Entity` under `core/data/.../db/entity/`, register it in
   `SpineIQDatabase.kt`'s `entities = [...]` and bump the `version`.
2. Add `@TypeConverter`s in `db/converters/Converters.kt` for any enum/collection.
3. Add DAO methods; expose them through a repository, not directly to the VM.
4. New user-owned tables should FK → `user_profiles(id)` with CASCADE so
   "delete all my data" keeps working with no extra code.

### Add a shop cosmetic (avatar item)
1. Add an `AvatarCatalogItem` to `AvatarCatalog.ALL`
   (`core/data/.../gamification/AvatarCatalog.kt`) — unique id, category, price.
2. Write a draw function for it in the matching `ui/avatar/*Layers.kt` file
   (0..100 unit space; see existing layers for the body landmarks).
3. Map the id → layer in `ui/avatar/AvatarRegistry.kt`.
4. Done — the Shop grid, preview, purchase flow, and renderer pick it up from
   the catalog. `EconomyCatalogInvariantsTest` guards id uniqueness/pricing.

### Add an achievement
1. Add an `Achievement` to `AchievementCatalog.ALL`
   (`core/data/.../gamification/AchievementCatalog.kt`) — unique id, rewards, a
   monotonic `isUnlocked` predicate over `AchievementContext`, and optionally a
   `progress` hint lambda.
2. Map an icon for the id in `achievementIcon()`
   (`ui/gamification/AchievementBadge.kt`).
3. No schema change needed — only unlock state is persisted. Extend
   `AchievementContext` + `GamificationRepository.buildAchievementContext()` if
   the predicate needs a new count.
4. Add a predicate case to `AchievementCatalogTest`.

### Change reward amounts / level curve
- All coin/XP amounts and streak-milestone bonuses live in `Economy`
  (`core/data/.../gamification/Economy.kt`); the level table in `LevelTable.kt`.
- Levels are derived from XP at read time, so retuning the table requires **no
  migration** — but keep thresholds ascending and update `LevelTableTest`.
- Never grant coins/XP directly: all grants must go through
  `GamificationRepository.tryGrant()` with a `DedupeKeys` key, or rewards can
  be farmed by repeating the action.

## Database schema & migrations

- Room schemas are exported to `core/data/schemas/`. The current version is
  `3.json` (v3 added the five gamification tables).
- The DB currently uses `.fallbackToDestructiveMigration()`
  (`di/DatabaseModule.kt`) — **schema changes wipe local data**. This is
  acceptable only for the Phase 1 prototype. Before production, bump the DB
  version and write real `Migration`s.

## Health Connect status (important)

Health Connect is a **declared dependency but not yet implemented**. The
`androidx.health.connect:connect-client` lib is in `core/data/build.gradle.kts`
and the spec (FR-04, §14) requires it, but there is no `HealthConnectClient`
usage in source. All lifestyle/activity data is currently **manual entry only**
(`DataSource.MANUAL` is hard-coded in `AssessmentSessionViewModel.persistLifestyle()`).
Implementing the API 28+ import flow and API 26–27 graceful-disable is open work.

## Debugging approaches

- **Scoring discrepancies**: reproduce in `ScoringEngineTest.kt` with the exact
  inputs — the engine is deterministic and pure, so a failing case is fully
  isolatable without the app.
- **Report mismatch (UI vs PDF)**: the two renderers read the same
  `ScoresRecordEntity`; check both `FullReportScreen.kt` and `PdfExporter.kt`.
- **Encrypted DB**: the SQLCipher DB cannot be opened with plain `sqlite3`; inspect
  via in-app reads or temporarily log query results.
- **Wizard state loss**: confirm the screen resolves the *parent* nav entry
  (`getBackStackEntry(AssessmentGraph.route)`) — a fresh `hiltViewModel()` would
  create a new, empty session.
- **Reward not granted**: almost always the ledger dedupe working as intended —
  the `dedupe_key` already exists (e.g. re-saving a section, same-day check-in,
  one-time streak milestone). Inspect `reward_ledger` rows / the
  `tryGrant()` return value before suspecting the economy.
- **Celebration didn't show**: events emit only *after* the DB transaction
  commits and have no replay — if `CelebrationHost`'s collector wasn't attached
  (e.g. process death mid-grant) the animation is lost but currency is safe.
  Toasts inside the wizard are intentionally suppressed by
  `RewardToastSuppressor` because the stage interstitial shows the same reward.
- **Streak looks wrong**: display uses `StreakLogic.effectiveStreak` (lazy
  reset — shows 0 after a missed day even though the stored value is stale);
  the stored row only updates on the next qualifying event. Only check-ins and
  completions qualify, not per-step saves.

## Distribution

Phase 1 targets the **public Google Play Store** (see `PROJECT_PLAN.md` §1).

Pre-release checklist:
- **Production keystore**: a signing config must be created and stored securely
  before a Play Store submission. Do not commit the keystore or its credentials
  to version control.
- **`isMinifyEnabled`**: currently `false` in the `release` build type. Enable and
  configure ProGuard/R8 rules before the production release build.
- **Play Store listing**: app description, screenshots, privacy policy URL, and
  content rating questionnaire are required by Google Play before publishing.
- **Privacy policy**: mandatory for any health-data app on the Play Store. Must
  clearly state that all data is stored locally on-device and is never transmitted
  to any server in Phase 1.

For development and internal testing, use `assembleDebug` / `installDebug` or
distribute via an internal Play testing track.
