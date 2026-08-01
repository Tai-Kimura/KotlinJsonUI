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
# `adb shell true` alone is NOT enough: a GL-wedged emulator (frozen emugl /
# "Failed to find ColorBuffer") keeps answering trivial shell commands while
# rendering is dead — observed on hosted CI as every instrumentation attempt
# burning its full timeout with zero fixture progress, sailing straight past
# the old probe. screencap exercises the GPU readback path and hangs (-> 124)
# when emugl is wedged, so the script can bail for a fresh-emulator retry.
adb_alive() {
  timeout 60 "$ADB" shell true >/dev/null 2>&1 || return 1
  timeout 90 "$ADB" shell screencap -p /dev/null >/dev/null 2>&1
}

APP_PKG="com.kotlinjsonui.conformance"
TEST_PKG="$APP_PKG.test"
DEVICE_OUT="/sdcard/Android/data/$APP_PKG/files/conformance"

FRESH=0
FILTER="all"
# A chopped fixture now costs up to 2 attempts (first dangle re-runs, second
# dangles to error), and a slow runner legitimately needs several 20-min
# attempts to cover the whole suite via resume — size the ceiling for both.
MAX_ATTEMPTS=12
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

# 3b. Let the launcher taskbar settle before anything is captured.
#
# Full-screen captures include the tablet taskbar, whose recent/predicted app
# row reorders itself for a few seconds around the first launch of the app
# under test. Measured: the taskbar row is byte-identical across all 468
# screenshots WITHIN a run — it settles once and stays — but differed BETWEEN
# runs, putting 9 fixtures over the dHash threshold. So the fix is not to hide
# it (SafeAreaView fixtures need real insets) but to start capturing only
# after it has stopped moving.
#
# Waiting on the whole screen rather than the taskbar strip keeps this to
# shell: with the app parked on its idle launch screen, "screen stopped
# changing" and "taskbar settled" are the same event.
echo "Settling the launcher taskbar..."
adbsh am start -n "$APP_PKG/.FixtureHostActivity" >/dev/null 2>&1 || \
  adbsh monkey -p "$APP_PKG" -c android.intent.category.LAUNCHER 1 >/dev/null 2>&1 || true
settle_prev=""
settle_stable=0
for _ in $(seq 1 20); do
  sleep 1
  settle_now="$(timeout 30 "$ADB" exec-out screencap -p 2>/dev/null | md5 2>/dev/null \
                || timeout 30 "$ADB" exec-out screencap -p 2>/dev/null | md5sum 2>/dev/null \
                || echo "")"
  [[ -z "$settle_now" ]] && break
  if [[ "$settle_now" == "$settle_prev" ]]; then
    settle_stable=$((settle_stable + 1))
    # Two identical consecutive samples: one could catch a mid-animation
    # pause, three would cost 3s on every run for no extra certainty.
    [[ $settle_stable -ge 2 ]] && break
  else
    settle_stable=0
  fi
  settle_prev="$settle_now"
done
if [[ $settle_stable -ge 2 ]]; then
  echo "screen settled"
else
  echo "warning: screen did not settle within 20s — taskbar may drift between runs" >&2
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
  # Suite duration (full 643-fixture set incl. interactive + screenshots):
  # ~4 min on a local arm64 emulator, but 5-7x slower on hosted-CI swiftshader
  # (~25-30 min observed). The per-attempt timeout is NOT the suite budget —
  # progress.jsonl makes each attempt RESUME where the last one stopped, so the
  # loop accumulates progress across attempts. 20 min per attempt still catches
  # a hung fixture / mid-run wedge without starving the resume loop.
  # On timeout (124) the loop re-probes liveness and resumes.
  timeout 1200 "$ADB" shell am instrument -w \
    -e conformanceFilter "$FILTER" \
    -e conformanceHostMode "${HOST_MODE:-dynamic}" \
    -e class "$APP_PKG.ConformanceSuiteTest" \
    "$TEST_PKG/androidx.test.runner.AndroidJUnitRunner" || true

  # Fixture-progress marker: lets a post-mortem tell "slow but advancing"
  # (count grows across attempts — budget problem) from "wedged" (count
  # frozen — emulator problem) without pulling device logs.
  progress_count=$(adbsh "wc -l < $DEVICE_OUT/progress.jsonl" 2>/dev/null | tr -d '[:space:]' || true)
  echo "progress.jsonl outcomes after attempt $attempt: ${progress_count:-0}"

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
