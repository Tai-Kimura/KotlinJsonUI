#!/usr/bin/env bash
# Pull conformance outputs off the device into the conformance suite dir:
#   $CONFORMANCE_DIR/results/android.results.json
#   $CONFORMANCE_DIR/artifacts/android/*.png
#
# Usage:
#   CONFORMANCE_DIR=/path/to/conformance ./scripts/collect_results.sh
set -euo pipefail

if [[ -z "${CONFORMANCE_DIR:-}" ]]; then
  echo "error: CONFORMANCE_DIR is not set" >&2
  exit 1
fi

SDK="${ANDROID_HOME:-${ANDROID_SDK_ROOT:-$HOME/Library/Android/sdk}}"
ADB="$SDK/platform-tools/adb"
[[ -x "$ADB" ]] || ADB="$(command -v adb)" || { echo "error: adb not found" >&2; exit 1; }

APP_PKG="com.kotlinjsonui.conformance"
DEVICE_OUT="/sdcard/Android/data/$APP_PKG/files/conformance"

if ! "$ADB" shell "[ -f $DEVICE_OUT/android.results.json ]"; then
  echo "error: $DEVICE_OUT/android.results.json not found on device (run run_conformance.sh first)" >&2
  exit 1
fi

# HOST_MODE=codegen: results/artifacts land in codegen-suffixed locations so
# a parity run never clobbers the dynamic truth (results/ + artifacts/android).
if [ "${HOST_MODE:-dynamic}" = "codegen" ]; then
  RESULTS_DEST="$CONFORMANCE_DIR/codegen/android.results.json"
  ARTIFACTS_DEST="$CONFORMANCE_DIR/artifacts/android-codegen"
else
  RESULTS_DEST="$CONFORMANCE_DIR/results/android.results.json"
  ARTIFACTS_DEST="$CONFORMANCE_DIR/artifacts/android"
fi
mkdir -p "$(dirname "$RESULTS_DEST")" "$ARTIFACTS_DEST"

# 🔴 THE TWO OUTPUTS ARE NOT HANDLED THE SAME WAY, AND THAT ASYMMETRY COSTS A
# DAY'S RESULTS. The PNGs below go through `cp -R` INTO the existing directory,
# so a narrow run leaves the wide run's pictures in place. The results file is
# pulled straight over the destination, so a narrow run REPLACES the wide run's
# verdicts. Measured 2026-09-15: a `--filter Web` run (2 fixtures) landed on top
# of that day's full android results and 1997 outcomes were gone. Re-rendering
# recovered them — the second run was identical — so only time was lost.
#
# ⚠️ AND THE ASYMMETRY IS WHAT MAKES IT QUIET. `ls artifacts/android | wc -l`
# keeps returning the full number, so one instrument goes on saying "complete"
# while the one the gate reads has shrunk.
#
# The refusal needs no handshake with run_conformance.sh: both files carry
# their own result count, so "the incoming set is smaller than the one already
# here" is derivable from the two files alone. A legitimate shrink (fixtures
# removed) is exactly the case that deserves a person looking, and says so with
# --allow-narrowing.
INCOMING_TMP="$(mktemp)"
"$ADB" pull "$DEVICE_OUT/android.results.json" "$INCOMING_TMP" >/dev/null
count_results() {  # prints the number of entries, or nothing if unreadable
  python3 - "$1" <<'PYEOF' 2>/dev/null
import json, sys
try:
    data = json.load(open(sys.argv[1]))
except Exception:
    raise SystemExit(1)
results = data.get("results")
if not isinstance(results, list):
    raise SystemExit(1)
print(len(results))
PYEOF
}
INCOMING_N="$(count_results "$INCOMING_TMP" || true)"
EXISTING_N=""
[ -f "$RESULTS_DEST" ] && EXISTING_N="$(count_results "$RESULTS_DEST" || true)"
echo "Incoming results: ${INCOMING_N:-unreadable}   already at destination: ${EXISTING_N:-none}"

# 🔻 UNREADABLE IS NOT SMALLER, AND IT IS NOT LARGER EITHER. If either side
# cannot be counted the comparison has no answer, so it refuses rather than
# guessing in the direction that happens to let the write through.
if [ "${ALLOW_NARROWING:-0}" != "1" ] && [ -f "$RESULTS_DEST" ]; then
  if [ -z "$INCOMING_N" ] || [ -z "$EXISTING_N" ]; then
    echo "error: cannot compare result counts (incoming='${INCOMING_N:-unreadable}' existing='${EXISTING_N:-unreadable}')." >&2
    echo "       Refusing rather than overwriting on an unanswered comparison." >&2
    echo "       Set ALLOW_NARROWING=1 to overwrite deliberately." >&2
    rm -f "$INCOMING_TMP"; exit 1
  fi
  if [ "$INCOMING_N" -lt "$EXISTING_N" ]; then
    echo "error: this run carries $INCOMING_N result(s) and $RESULTS_DEST already holds $EXISTING_N." >&2
    echo "       Overwriting would DELETE $((EXISTING_N - INCOMING_N)) verdict(s) — the PNGs would survive" >&2
    echo "       (they are merged, not replaced), so nothing else would show the loss." >&2
    echo "       This is what a --filter run does to a full run." >&2
    echo "       Set ALLOW_NARROWING=1 if the shrink is intended (fixtures removed)." >&2
    rm -f "$INCOMING_TMP"; exit 1
  fi
fi
mv "$INCOMING_TMP" "$RESULTS_DEST"

if "$ADB" shell "[ -d $DEVICE_OUT/artifacts/android ]"; then
  tmp="$(mktemp -d)"
  "$ADB" pull "$DEVICE_OUT/artifacts/android" "$tmp/android" >/dev/null
  # merge (adb pull creates the dir); keep destination flat
  cp -R "$tmp/android/." "$ARTIFACTS_DEST/"
  rm -rf "$tmp"
fi

count_png=$(ls "$ARTIFACTS_DEST" 2>/dev/null | wc -l | tr -d ' ')
echo "Collected results -> $RESULTS_DEST"
echo "Collected $count_png screenshot artifact(s) -> $ARTIFACTS_DEST/"

# Hosted CI only: kill the emulator ourselves so the emulator-runner action's
# teardown finds it already gone. Observed 2026-08-02 (run 30741336803 retry):
# suite + collect finished at 74.5 min, then the action's own teardown hung
# ~25 min into the step ceiling and voided a successful run. Never do this
# locally — the conf_ci AVD belongs to the developer, not this script.
if [[ "${CI:-}" == "true" ]]; then
  "$ADB" emu kill >/dev/null 2>&1 || true
fi
