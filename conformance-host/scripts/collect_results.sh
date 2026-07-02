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

mkdir -p "$CONFORMANCE_DIR/results" "$CONFORMANCE_DIR/artifacts/android"
"$ADB" pull "$DEVICE_OUT/android.results.json" "$CONFORMANCE_DIR/results/android.results.json"

if "$ADB" shell "[ -d $DEVICE_OUT/artifacts/android ]"; then
  tmp="$(mktemp -d)"
  "$ADB" pull "$DEVICE_OUT/artifacts/android" "$tmp/android" >/dev/null
  # merge (adb pull creates the dir); keep destination flat
  cp -R "$tmp/android/." "$CONFORMANCE_DIR/artifacts/android/"
  rm -rf "$tmp"
fi

count_png=$(ls "$CONFORMANCE_DIR/artifacts/android" 2>/dev/null | wc -l | tr -d ' ')
echo "Collected results -> $CONFORMANCE_DIR/results/android.results.json"
echo "Collected $count_png screenshot artifact(s) -> $CONFORMANCE_DIR/artifacts/android/"
