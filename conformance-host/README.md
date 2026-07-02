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
  `ConformanceStateProvider.kt` is the generic `class: interactive` state
  provider (INTERACTIVE_HOST_CONTRACT.md): initial values come from the
  fixture layout's `data` section via the production
  `DynamicView.applyDataSectionDefaults` path; manifest `state.handlers`
  become `() -> Unit` closures that set one var to a literal through
  `DataBindingContext.updateValue`; two-way write-back is the standard
  `updateData` sink. Zero per-fixture host code.
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
alias fixtures; `--filter interactive` to `class: interactive` fixtures;
everything else is still reported (as `skipped` with a detail) — the
results file always contains one entry per manifest fixture.

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

The vendored copy is a snapshot of upstream
`jsonui-test-runner-android@7c00459` (the former `KJUI-CONFORMANCE PATCH`
set — pluggable `screenshotHandler`, K2 smart-cast fixes, descendant-text
`assertText` — was upstreamed). One local patch is currently carried,
marked `// KJUI-CONFORMANCE PATCH`:

- `AssertionExecutor.assertText` retries until the step timeout instead of
  a single sample (racy against Compose async state updates; upstream bug
  `testrunner-android-asserttext-single-sample-race`).

Screenshot capture: upstream now also offers `TestRunnerConfig.screenshotDir`,
but this harness drives `ActionExecutor` directly (not `JsonUITestRunner`)
and keeps the `screenshotHandler` wiring — it needs the per-fixture artifact
path bookkeeping (`lastScreenshot`) and a hard failure when capture fails,
which the config-dir default (silent best-effort save) does not provide.
