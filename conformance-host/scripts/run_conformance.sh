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
  (cd "$ROOT_DIR" && ./gradlew -q :conformance-host:syncConformanceFixtures)
elif [[ ! -f "$MODULE_DIR/src/main/assets/conformance/manifest.json" ]]; then
  echo "error: no synced fixtures and CONFORMANCE_DIR unset" >&2
  exit 1
fi

# 2. Build + install both APKs
(cd "$ROOT_DIR" && ./gradlew :conformance-host:installDebug :conformance-host:installDebugAndroidTest)

# 3. Stable test environment
"$ADB" shell settings put global window_animation_scale 0
"$ADB" shell settings put global transition_animation_scale 0
"$ADB" shell settings put global animator_duration_scale 0
# Freeze the status bar (clock/battery/wifi) via SystemUI demo mode so
# full-screen screenshots don't carry live-clock noise between runs.
"$ADB" shell settings put global sysui_demo_allowed 1
"$ADB" shell am broadcast -a com.android.systemui.demo -e command enter >/dev/null
"$ADB" shell am broadcast -a com.android.systemui.demo -e command clock -e hhmm 1200 >/dev/null
"$ADB" shell am broadcast -a com.android.systemui.demo -e command battery -e level 100 -e plugged false >/dev/null
"$ADB" shell am broadcast -a com.android.systemui.demo -e command network -e wifi show -e level 4 >/dev/null

if [[ "$FRESH" == "1" ]]; then
  echo "Wiping on-device conformance output..."
  "$ADB" shell rm -rf "$DEVICE_OUT" || true
fi

# 4. Run instrumentation; restart after process crashes until the final
#    results file exists (progress.jsonl makes reruns resume, and dangling
#    "running" markers turn crashed fixtures into error outcomes).
attempt=1
while true; do
  echo "--- instrumentation attempt $attempt/$MAX_ATTEMPTS (filter=$FILTER) ---"
  "$ADB" shell am instrument -w \
    -e conformanceFilter "$FILTER" \
    -e class "$APP_PKG.ConformanceSuiteTest" \
    "$TEST_PKG/androidx.test.runner.AndroidJUnitRunner" || true

  if "$ADB" shell "[ -f $DEVICE_OUT/android.results.json ]"; then
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
