#!/usr/bin/env bash
# Sync (vendor) the jsonui-test-runner Android driver sources into this module.
#
# The conformance androidTest suite embeds the driver's Kotlin sources directly
# (models + ActionExecutor/AssertionExecutor + runner) instead of depending on
# a published artifact, so the suite builds without any repository-external
# path in committed files. The vendored copy IS committed; re-run this script
# to refresh it from a local jsonui-test-runner checkout.
#
# NOTE: the vendored copy may carry local patches marked with
#   // KJUI-CONFORMANCE PATCH
# Re-syncing overwrites them — re-apply from git history (or upstream the
# patches to jsonui-test-runner) after a refresh.
#
# Usage:
#   JSONUI_TEST_RUNNER_PATH=/path/to/jsonui-test-runner ./scripts/sync_driver.sh
set -euo pipefail

if [[ -z "${JSONUI_TEST_RUNNER_PATH:-}" ]]; then
  echo "error: JSONUI_TEST_RUNNER_PATH is not set (path to a jsonui-test-runner checkout)" >&2
  exit 1
fi

SRC="$JSONUI_TEST_RUNNER_PATH/drivers/android/jsonuitestrunner/src/main/kotlin/com/jsonui/testrunner"
if [[ ! -d "$SRC" ]]; then
  echo "error: driver sources not found at: $SRC" >&2
  exit 1
fi

MODULE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
DEST="$MODULE_DIR/src/androidTest/kotlin/com/jsonui/testrunner"

rm -rf "$DEST"
mkdir -p "$DEST"
cp -R "$SRC/." "$DEST/"

echo "Vendored jsonui-test-runner android driver -> ${DEST#"$MODULE_DIR/"}"
find "$DEST" -name '*.kt' | sed "s|$MODULE_DIR/||" | sort
