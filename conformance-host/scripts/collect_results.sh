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
"$ADB" pull "$DEVICE_OUT/android.results.json" "$RESULTS_DEST"

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
