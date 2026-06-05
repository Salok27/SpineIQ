# Development Workflows

## Build / test / run

Use the Gradle wrapper (`./gradlew` on POSIX, `.\gradlew.bat` on Windows). JDK 11.

| Task | Command |
|---|---|
| Build debug APK | `./gradlew :app:assembleDebug` |
| Full build | `./gradlew build` |
| Scoring unit tests (fast, pure JVM) | `./gradlew :core:scoring:test` |
| App unit tests | `./gradlew :app:testDebugUnitTest` |
| Instrumented tests (needs device/emulator) | `./gradlew connectedDebugAndroidTest` |
| Install on device | `./gradlew :app:installDebug` |

The scoring module has no Android dependency, so `:core:scoring:test` runs in
milliseconds and is the primary fast-feedback loop for rule changes
(`core/scoring/.../ScoringEngineTest.kt`).

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
   `SpineIQDatabase.kt`'s `entities = [...]`.
2. Add `@TypeConverter`s in `db/converters/Converters.kt` for any enum/collection.
3. Add DAO methods; expose them through a repository, not directly to the VM.

## Database schema & migrations

- Room schemas are exported to `core/data/schemas/`. The current version is `1.json`.
- The DB currently uses `.fallbackToDestructiveMigration()`
  (`di/DatabaseModule.kt:36`) — **schema changes wipe local data**. This is
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

## Distribution

Phase 1 is internal-only (NFR-11 / OQ-14): `release` build type has
`isMinifyEnabled = false` and **no production keystore**. Do not add a public
Play Store / signing config without confirming it is in scope.
