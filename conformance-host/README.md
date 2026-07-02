# conformance-host

Android execution environment for the JsonUI renderer conformance suite
(renderer-SSoT plan 04). Renders generated fixtures through **KotlinJsonUI
Compose dynamic mode** (`library-dynamic` / `DynamicView`) and executes each
fixture's screen test with the embedded jsonui-test-runner Android driver
(UIAutomator; elements are located by resource-id = Compose `testTag`).

## Layout

- `src/main` — `FixtureHostActivity`: single Activity that renders one fixture
  at a time. Fixture selection via the `fixtureId` intent extra, or (from the
  instrumentation suite, which shares the app process) via `FixtureHost.show()`.
  Bundles the `conformance_sample` drawable required by image fixtures.
- `src/main/assets/conformance/` — fixtures + manifest, **synced, gitignored**.
- `src/androidTest/kotlin/com/kotlinjsonui/conformance/` — the suite:
  manifest-driven iteration, RESULTS_SCHEMA output, crash-resume via
  `progress.jsonl`.
- `src/androidTest/kotlin/com/jsonui/testrunner/` — **vendored** snapshot of
  the jsonui-test-runner Android driver (see below).
- `scripts/` — sync / run / collect helpers.

## Runtime parameters (never hardcoded)

| Env var | Meaning |
|---|---|
| `CONFORMANCE_DIR` | conformance suite dir containing `manifest.json`, `fixtures/`, `results/`, `artifacts/` |
| `JSONUI_TEST_RUNNER_PATH` | local jsonui-test-runner checkout (only needed to re-vendor the driver) |

## Local run (reproducible procedure)

```bash
# 0. Device: boot a headless emulator (CI-compatible)
$ANDROID_HOME/emulator/emulator -avd Pixel_Tablet -no-window -no-audio -no-boot-anim &
$ANDROID_HOME/platform-tools/adb wait-for-device

# 1..4. Sync fixtures, install, instrument (crash-resume), all in one:
CONFORMANCE_DIR=/path/to/conformance ./conformance-host/scripts/run_conformance.sh --fresh

# 5. Pull results + screenshots into the suite dir
CONFORMANCE_DIR=/path/to/conformance ./conformance-host/scripts/collect_results.sh
```

`run_conformance.sh --filter assertable` limits execution to assertable +
alias fixtures; everything else is still reported (as `skipped` with a
detail) — the results file always contains one entry per manifest fixture.

Equivalent Gradle-only invocation (no crash-resume loop):

```bash
CONFORMANCE_DIR=... ./gradlew :conformance-host:syncConformanceFixtures
./gradlew :conformance-host:connectedDebugAndroidTest
CONFORMANCE_DIR=... ./conformance-host/scripts/collect_results.sh
```

## Outputs (on device, pulled by collect_results.sh)

`/sdcard/Android/data/com.kotlinjsonui.conformance/files/conformance/`

- `android.results.json` — RESULTS_SCHEMA-compliant (`platform`,
  `manifestHash` = sha256 of the synced manifest bytes, `runner`, one result
  per manifest fixture: `pass` / `fail` / `error` / `skipped` + detail).
- `artifacts/android/<Section>_<attr>__<case>.png` — visual fixture
  screenshots.
- `progress.jsonl` — incremental log; lets a rerun resume after an app-process
  crash (the crashing fixture is recorded as `error` instead of being retried
  forever).

## Skip policy

- fixture `platforms` without `android` → `skipped` (`not applicable to android`)
- fixture `mode` present without `compose` (e.g. `uikit`, `swiftui`) →
  `skipped` (`mode: ... (host runs compose dynamic)`) — this host only
  exercises the Compose dynamic path
- `--filter assertable` → non-assertable, non-alias fixtures `skipped`
  (`filtered out (assertable-only run)`)

## Vendored driver

The driver sources are vendored (committed) rather than referenced by path or
artifact so this module builds standalone with zero machine-specific
configuration. Re-sync from a checkout with:

```bash
JSONUI_TEST_RUNNER_PATH=/path/to/jsonui-test-runner ./conformance-host/scripts/sync_driver.sh
```

Local patches are marked `// KJUI-CONFORMANCE PATCH` (currently: pluggable
`screenshotHandler` — upstream's `screenshot` action is a no-op placeholder).
Re-syncing overwrites them; re-apply from git history or upstream them first.
