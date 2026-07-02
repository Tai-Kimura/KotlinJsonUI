#!/bin/bash
# Sync the generated typed-attribute extraction sources
# (com.kotlinjsonui.dynamic.generated) from a jsonui-cli checkout.
#
# Usage:
#   JSONUI_CLI_PATH=/path/to/jsonui-cli ./library-dynamic/scripts/sync_generated_attrs.sh
#
# The generator is `jui generate attr-bindings --lang kotlin` whose single
# source of truth is shared/core/attribute_definitions.json. Every synced
# file carries the @generated header — never edit them by hand; change the
# definitions and re-run this script instead. The synced files are
# committed so the library builds standalone.
set -euo pipefail

CLI="${JSONUI_CLI_PATH:?set JSONUI_CLI_PATH to a jsonui-cli checkout}"
DEST="$(cd "$(dirname "$0")/.." && pwd)/src/main/kotlin/com/kotlinjsonui/dynamic/generated"

(cd "$CLI" && PYTHONPATH=jui_tools python3 -c "from jui_cli.cli import main; main(['generate','attr-bindings','--lang','kotlin'])")

mkdir -p "$DEST"
rsync -a --delete --include='*.kt' --exclude='*' "$CLI/build/attr_codegen/kotlin/" "$DEST/"
echo "Synced $(ls "$DEST" | wc -l | tr -d ' ') file(s) -> $DEST"
