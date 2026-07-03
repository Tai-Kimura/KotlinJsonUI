#!/usr/bin/env bash
# Run the Android conformance suite end to end:
#   (optional) fixture sync -> install APKs -> instrument (with crash-resume)
#
# Requirements:
# - A booted device/emulator visible to adb (headless works:
#   emulator -avd <name> -no-window -no-audio -no-boot-anim)
# - CONFORMANCE_DIR pointing at the conformance suite directory if you want
#   fixtures re-synced before the run (skipped when unset and assets exist).
#
# Options:
#   --fresh                 wipe on-device progress/results before running
#   --filter assertable     run only assertable + alias fixtures
#   --max-attempts N        instrumentation restarts allowed after crashes (default 8)
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
MODULE_DIR="$(dirname "$SCRIPT_DIR")"
ROOT_DIR="$(dirname "$MODULE_DIR")"

SDK="${ANDROID_HOME:-${ANDROID_SDK_ROOT:-$HOME/Library/Android/sdk}}"
ADB="$SDK/platform-tools/adb"
[[ -x "$ADB" ]] || ADB="$(command -v adb)" || { echo "error: adb not found" >&2; exit 1; }

# Every adb-shell call runs under a timeout. A wedged emulator (intermittent on
# hosted CI runners — the "[EmulatorConsole]: Failed to start" symptom) otherwise
# blocks adb forever, and the crash-resume loop below never gets to retry because
# the first call never returns. On timeout the call exits 124; the script fails
# fast so the workflow can retry with a FRESH emulator (a frozen emulator cannot
# self-recover).
ADB_SH_TIMEOUT="${ADB_SH_TIMEOUT:-120}"
adbsh() { timeout "$ADB_SH_TIMEOUT" "$ADB" shell "$@"; }
# Emulator liveness probe; non-zero (incl. 124 = timeout) means wedged.
adb_alive() { timeout 60 "$ADB" shell true >/dev/null 2>&1; }

APP_PKG="com.kotlinjsonui.conformance"
TEST_PKG="$APP_PKG.test"
DEVICE_OUT="/sdcard/Android/data/$APP_PKG/files/conformance"

FRESH=0
FILTER="all"
MAX_ATTEMPTS=8
while [[ $# -gt 0 ]]; do
  case "$1" in
    --fresh) FRESH=1; shift ;;
    --filter) FILTER="$2"; shift 2 ;;
    --max-attempts) MAX_ATTEMPTS="$2"; shift 2 ;;
    *) echo "unknown option: $1" >&2; exit 1 ;;
  esac
done

if ! "$ADB" get-state >/dev/null 2>&1; then
  echo "error: no device/emulator connected (adb get-state failed)" >&2
  exit 1
fi

# 1. Sync fixtures when a suite dir is provided
if [[ -n "${CONFORMANCE_DIR:-}" ]]; then
  (cd "$ROOT_DIR" && timeout 300 ./gradlew -q :conformance-host:syncConformanceFixtures)
elif [[ ! -f "$MODULE_DIR/src/main/assets/conformance/manifest.json" ]]; then
  echo "error: no synced fixtures and CONFORMANCE_DIR unset" >&2
  exit 1
fi

# 2. Build + install both APKs. Timeout-bounded: a healthy install is ~3-4 min,
# so 8 min fast-fails a gradle/emulator wedge in this phase (the boot succeeded
# but the device wedged) instead of riding the step timeout.
(cd "$ROOT_DIR" && timeout 480 ./gradlew :conformance-host:installDebug :conformance-host:installDebugAndroidTest)

# 2b. Emulator health gate — fail fast (don't hang) if it wedged after install.
if ! adb_alive; then
  echo "error: emulator unresponsive after install (adb probe timed out) — wedged" >&2
  exit 1
fi

# 3. Stable test environment
adbsh settings put global window_animation_scale 0
adbsh settings put global transition_animation_scale 0
adbsh settings put global animator_duration_scale 0
# Freeze the status bar (clock/battery/wifi) via SystemUI demo mode so
# full-screen screenshots don't carry live-clock noise between runs.
adbsh settings put global sysui_demo_allowed 1
adbsh am broadcast -a com.android.systemui.demo -e command enter >/dev/null
adbsh am broadcast -a com.android.systemui.demo -e command clock -e hhmm 1200 >/dev/null
adbsh am broadcast -a com.android.systemui.demo -e command battery -e level 100 -e plugged false >/dev/null
adbsh am broadcast -a com.android.systemui.demo -e command network -e wifi show -e level 4 >/dev/null

if [[ "$FRESH" == "1" ]]; then
  echo "Wiping on-device conformance output..."
  adbsh rm -rf "$DEVICE_OUT" || true
fi

# 4. Run instrumentation; restart after process crashes until the final
#    results file exists (progress.jsonl makes reruns resume, and dangling
#    "running" markers turn crashed fixtures into error outcomes).
attempt=1
while true; do
  # Distinguish a wedged emulator from a crashed test process: a crash leaves
  # the emulator responsive (crash-resume retries below), but a wedge cannot be
  # recovered in-place — bail fast so the workflow retries with a fresh emulator
  # instead of the whole job hanging.
  if ! adb_alive; then
    echo "error: emulator unresponsive before attempt $attempt — wedged" >&2
    exit 1
  fi
  echo "--- instrumentation attempt $attempt/$MAX_ATTEMPTS (filter=$FILTER) ---"
  # ~11-min green suite; a 15-min timeout catches a mid-run wedge / hung fixture
  # without truncating a healthy-but-slow run. On timeout (124) the loop re-probes.
  timeout 900 "$ADB" shell am instrument -w \
    -e conformanceFilter "$FILTER" \
    -e class "$APP_PKG.ConformanceSuiteTest" \
    "$TEST_PKG/androidx.test.runner.AndroidJUnitRunner" || true

  if adbsh "[ -f $DEVICE_OUT/android.results.json ]"; then
    echo "android.results.json produced on device."
    break
  fi
  attempt=$((attempt + 1))
  if [[ $attempt -gt $MAX_ATTEMPTS ]]; then
    echo "error: results file not produced after $MAX_ATTEMPTS attempts" >&2
    exit 1
  fi
done

echo "Done. Collect with: CONFORMANCE_DIR=... $SCRIPT_DIR/collect_results.sh"
