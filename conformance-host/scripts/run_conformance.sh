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

# 0. Preflight — fail fast, by name, on the two ways this script has been
#    seen to sit silent for a quarter hour.
#
# (a) Device selection. `adb get-state` fails for "no device" AND for "more
#     than one device/emulator" (adb refuses to pick), and the old message
#     called both "no device". With several emulators up and ANDROID_SERIAL
#     unset, every adb call in the run fails the same way — screen_hash then
#     hashes EMPTY output, two identical empties look like "settled", and
#     the run marches on against nothing. Name the devices and the cure.
attached_devices() { "$ADB" devices 2>/dev/null | awk 'NR > 1 && $2 == "device" {print $1}'; }
if ! "$ADB" get-state >/dev/null 2>&1; then
  devices="$(attached_devices | tr '\n' ' ')"
  count="$(attached_devices | wc -l | tr -d '[:space:]')"
  if [[ -n "${ANDROID_SERIAL:-}" ]]; then
    echo "error: ANDROID_SERIAL=$ANDROID_SERIAL is not an attached device (attached: ${devices:-none})" >&2
  elif [[ "$count" -gt 1 ]]; then
    echo "error: $count devices attached and ANDROID_SERIAL is unset — adb will not pick one. Set ANDROID_SERIAL to one of: $devices" >&2
  else
    echo "error: no device/emulator connected (adb get-state failed)" >&2
  fi
  exit 1
fi
echo "device: ${ANDROID_SERIAL:-$(attached_devices | head -1)} (attached: $(attached_devices | tr '\n' ' '))"

# (b) The `timeout` wrapper. Every probe below is `$(timeout N adb ... | md5)`
#     and relies on coreutils semantics: when the child exits, timeout exits
#     and the pipe closes. A wrapper whose watchdog is a backgrounded
#     `( sleep N; kill )` that INHERITS stdout keeps the pipe open until the
#     sleep ends, so a screencap that answered in 0.3s costs the full N —
#     measured 2026-09-03 on macOS with such a shim: 10s for `echo`, 5s per
#     screen sample, 120s per adbsh read, and a "settle" loop of 20 samples
#     that quietly took 10 minutes. Probe once with a 3s budget on a child
#     that lives 0.3s (NOT `true`: a child that exits before the wrapper's
#     watchdog has forked its sleep leaves no orphan, and the probe passes
#     by a race — measured). Coreutils returns in 0.3s, the wrapper class
#     in 3s, which a whole-second clock may read as 2.
if ! command -v timeout >/dev/null 2>&1; then
  echo "error: 'timeout' not found — install coreutils (macOS: brew install coreutils, then put gnubin on PATH)" >&2
  exit 1
fi
probe_start=$(date +%s)
probe_out="$(timeout 3 sleep 0.3 2>/dev/null | cat)"
probe_elapsed=$(( $(date +%s) - probe_start ))
if [[ $probe_elapsed -ge 2 ]]; then
  echo "error: this 'timeout' ($(command -v timeout)) holds stdout open for its full budget (${probe_elapsed}s for 'timeout 3 sleep 0.3') — every probe in this run would stall for its timeout. Use coreutils timeout, or detach the wrapper's watchdog from stdout (>/dev/null 2>&1 </dev/null)." >&2
  exit 1
fi
: "${probe_out}"
if [[ "${CONFORMANCE_PREFLIGHT_ONLY:-0}" == "1" ]]; then
  echo "preflight OK (CONFORMANCE_PREFLIGHT_ONLY=1, stopping here)"
  exit 0
fi

# 1. Sync fixtures when a suite dir is provided
if [[ -n "${CONFORMANCE_DIR:-}" ]]; then
  (cd "$ROOT_DIR" && timeout 300 ./gradlew -q :conformance-host:syncConformanceFixtures)
elif [[ ! -f "$MODULE_DIR/src/main/assets/conformance/manifest.json" ]]; then
  echo "error: no synced fixtures and CONFORMANCE_DIR unset" >&2
  exit 1
fi

# 1b. Wipe the device's previous OUTPUT state. progress.jsonl exists so a
# crashed ATTEMPT resumes inside one invocation — but carrying it (and the
# old screenshots) across INVOCATIONS makes a re-run resume past everything
# and re-serve last run's pixels: a local re-run after a library fix
# measured the OLD library (2026-08-08, SafeAreaView_spacing/cellWidth —
# CI never sees this because its emulators are fresh). A new invocation is
# a new measurement; it starts from zero.
adbsh rm -rf "/sdcard/Android/data/$APP_PKG/files/conformance" || true

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
#
# The broadcasts are fire-and-forget and SystemUI silently ignores them when it
# is not ready yet, so this used to be a wish rather than a guarantee: of four
# CI captures inspected on 2026-08-04 (run 30839045057, both lanes ×2 attempts)
# THREE carried a live clock — 6:10, 6:09, 12:36 — and only one showed the
# frozen 12:00. A live clock moves the status-bar row in every screenshot, which
# is why env=ci android hashes drifted between runs and made ci baselines
# unbakeable. So: issue the commands, then PROVE demo mode took, and retry.
#
# The probe is a positive control, not a guess: a sample taken at one demo clock
# must differ from a sample at a DIFFERENT demo clock, on a screen that is
# otherwise known to be quiet. Both halves are needed — a busy screen changes on
# its own (so a difference proves nothing) and a dead demo channel never changes
# (so equality proves nothing either). Which is why the verification runs AFTER
# the settle block below, not here: measured 2026-08-04 (run 30868405003), this
# probe placed right after install reported quiet=no five times in a row because
# the launcher is still painting itself minutes after the APKs land.
# Never fails: callers treat an empty result as "could not sample" rather than
# dying, because `set -e` + pipefail would otherwise turn one timed-out
# screencap on a busy runner into an aborted suite.
screen_hash() {
  { timeout 30 "$ADB" exec-out screencap -p 2>/dev/null \
      | { md5 2>/dev/null || md5sum 2>/dev/null; } \
      | awk '{print $1}'; } || true
}
# Two identical consecutive samples, or empty if the screen never stops moving.
wait_quiet() {
  local tries="${1:-15}" prev="" now="" stable=0
  for _ in $(seq 1 "$tries"); do
    sleep 1
    now="$(screen_hash)"
    [[ -z "$now" ]] && { echo ""; return; }
    if [[ "$now" == "$prev" ]]; then
      stable=$((stable + 1))
      [[ $stable -ge 1 ]] && { echo "$now"; return; }
    else
      stable=0
    fi
    prev="$now"
  done
  echo ""
}
demo_broadcast() {
  adbsh am broadcast -a com.android.systemui.demo -e command "$@" >/dev/null 2>&1 || true
}
adbsh settings put global sysui_demo_allowed 1
demo_broadcast exit
demo_broadcast enter
demo_broadcast battery -e level 100 -e plugged false
demo_broadcast network -e wifi show -e level 4
demo_broadcast clock -e hhmm 1200

# Predicted-apps row of the tablet taskbar: the launcher reorders it between
# boots (measured 2026-08-04 across two CI runs — [Messages, Phone] became
# [Phone, Messages], and a third capture had the host app itself promoted into
# the row). Within a run it is stable, so the settle loop below was enough for
# same-run comparisons, but env=ci baselines and codegen parity compare across
# emulators, where the reorder lands as a whole-suite hash shift. Predictions
# come from Android System Intelligence; with the provider disabled the row
# falls back to the launcher's fixed default set.
adbsh pm disable-user --user 0 com.google.android.as >/dev/null 2>&1 \
  && echo "app-prediction provider disabled (taskbar row pinned to defaults)" \
  || echo "note: com.google.android.as absent or not disableable — taskbar row may still drift" >&2

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
# after it has stopped moving — plus the prediction-provider pin above, which
# is what keeps the row equal ACROSS runs rather than merely within one.
#
# Waiting on the whole screen rather than the taskbar strip keeps this to
# shell: with the app parked on its idle launch screen, "screen stopped
# changing" and "taskbar settled" are the same event.
echo "Settling the launcher taskbar..."
adbsh am start -n "$APP_PKG/.FixtureHostActivity" >/dev/null 2>&1 || \
  adbsh monkey -p "$APP_PKG" -c android.intent.category.LAUNCHER 1 >/dev/null 2>&1 || true
settle_prev=""
settle_stable=0
settle_start=$(date +%s)
for _ in $(seq 1 20); do
  sleep 1
  settle_now="$(screen_hash)"
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
  # Elapsed is printed because "20 samples" is only ~20s when each sample is
  # instant; a slow sampler (see preflight (b)) or a device another run is
  # driving makes the same line the entry to a much longer silence.
  echo "warning: screen did not settle in 20 samples ($(( $(date +%s) - settle_start ))s) — taskbar may drift between runs; the demo-mode probe below samples up to 5x15 more" >&2
fi

# 3b-2. Now that the screen is quiet, PROVE the status bar froze (see the probe
# rationale above). Toggling the demo clock must move pixels; if it does not,
# SystemUI dropped the broadcast and every screenshot this run produces carries
# a live clock.
demo_frozen=0
for attempt in 1 2 3 4 5; do
  quiet_hash="$(wait_quiet 15)"
  if [[ -z "$quiet_hash" ]]; then
    echo "demo-mode probe: screen never quiet (attempt $attempt)" >&2
    demo_broadcast enter
    continue
  fi
  demo_broadcast clock -e hhmm 0930
  sleep 2
  probe_other="$(screen_hash)"
  demo_broadcast clock -e hhmm 1200
  sleep 2
  probe_back="$(screen_hash)"
  if [[ -n "$probe_other" && "$quiet_hash" != "$probe_other" && "$quiet_hash" == "$probe_back" ]]; then
    demo_frozen=1
    echo "SystemUI demo mode verified (clock frozen at 12:00, attempt $attempt)"
    break
  fi
  echo "demo-mode probe inconclusive (attempt $attempt): responsive=$([[ "$quiet_hash" != "$probe_other" ]] && echo yes || echo no) reverts=$([[ "$quiet_hash" == "$probe_back" ]] && echo yes || echo no)" >&2
  demo_broadcast exit
  demo_broadcast enter
  demo_broadcast battery -e level 100 -e plugged false
  demo_broadcast network -e wifi show -e level 4
  demo_broadcast clock -e hhmm 1200
  sleep 3
done
if [[ "$demo_frozen" != "1" ]]; then
  # A live clock silently poisons every screenshot the run produces, so the
  # honest outcome is a failed attempt (the workflow retries on a FRESH
  # emulator) rather than a green run whose baselines cannot be reused.
  # Set CONFORMANCE_ALLOW_LIVE_CHROME=1 to downgrade this to a warning when
  # you only need the assertable classes and do not care about the pixels.
  if [[ "${CONFORMANCE_ALLOW_LIVE_CHROME:-0}" == "1" ]]; then
    echo "warning: SystemUI demo mode never took — screenshots carry a live clock" >&2
  else
    echo "error: SystemUI demo mode never took after 5 attempts — screenshots would carry a live clock and the captures could not be baselined" >&2
    exit 1
  fi
fi

# Chrome fingerprint: the idle-screen hash with the app parked and the status
# bar frozen. Two runs whose captures should be comparable must print the SAME
# value here — it is the cheapest way to tell "the renders diverged" from "the
# system chrome moved" when a later baseline diff looks suspiciously whole-suite.
echo "chrome fingerprint (idle screen): $(screen_hash)"

# 3c. a11y projection probe + boot re-roll. Run 30762153614 (first
# instrumented recurrence of the CI-only all-fixtures render-timeout): the
# app renders, input and lifecycle stay healthy, but
# AccessibilityManagerService never completes window registration ("wait for
# adding window timeout") for the WHOLE boot — UIAutomator is globally blind
# and the a11y-dependent fixture classes grind 8s timeouts for hours. The
# wedge is per-boot and probabilistic, so: probe a fresh app window through
# the same a11y channel UIAutomator uses; on failure REBOOT the device to
# re-roll the race (up to 3 reboots) instead of paying 20-min attempts.
# Visual fixtures don't need this (in-process readiness + SurfaceFlinger
# screenshots) — the probe protects the assertable/interactive classes.
a11y_probe() {
  adbsh "am start -W -n $APP_PKG/.FixtureHostActivity" >/dev/null 2>&1 || return 1
  sleep 2
  adbsh "rm -f /sdcard/a11y_probe.xml; uiautomator dump /sdcard/a11y_probe.xml" >/dev/null 2>&1 || return 1
  adbsh "grep -q \"package=[\\\"']$APP_PKG\" /sdcard/a11y_probe.xml" || return 1
  adbsh "am force-stop $APP_PKG" >/dev/null 2>&1 || true
  return 0
}
for roll in 1 2 3 4; do
  if a11y_probe; then
    echo "a11y projection probe: OK (roll $roll)"
    break
  fi
  if [[ $roll -eq 4 ]]; then
    echo "error: a11y projection stayed wedged through 3 reboots — giving up this emulator" >&2
    exit 1
  fi
  echo "a11y projection probe FAILED (roll $roll) — rebooting to re-roll the boot race..."
  "$ADB" reboot
  for i in $(seq 1 60); do
    booted="$(adbsh getprop sys.boot_completed 2>/dev/null | tr -d '[:space:]' || true)"
    [[ "$booted" == "1" ]] && break
    sleep 5
  done
  sleep 10
done

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

  # Logcat snapshots for CI post-mortems (2026-08-02: the all-fixtures
  # "did not render" signature exists ONLY on hosted CI — no local repro
  # even at identical SHAs/fixtures/fresh-install, so the app-side view is
  # unobtainable except from the run itself). First attempt separately:
  # app startup + first composition is where that signature decides.
  mkdir -p "$CONFORMANCE_DIR/results"
  if [[ $attempt -eq 1 ]]; then
    "$ADB" logcat -d -v time -t 8000 > "$CONFORMANCE_DIR/results/android.logcat.first.txt" 2>/dev/null || true
  fi
  "$ADB" logcat -d -v time -t 8000 > "$CONFORMANCE_DIR/results/android.logcat.txt" 2>/dev/null || true

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
