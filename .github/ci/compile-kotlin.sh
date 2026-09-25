#!/usr/bin/env bash
# The CI compile: `compile-kotlin.sh <log> <task>…`. Every Kotlin warning is an
# error (kotlin-warnings-as-errors.gradle), `--continue` reports every failing
# task, and the output is kept in <log>.
#
# pipefail is set HERE, not by the step's `shell:`. A step without `shell:`
# runs `bash -e {0}` — no pipefail — so `./gradlew … | tee` took tee's exit
# code: a failed compile passed the step (measured 2026-09-25: a compile error
# in sample-app, BUILD FAILED in the log, every build-test step green, since
# Unit tests does not compile sample-app). The positive controls run this same
# file, so they test the step's own pipeline.
set -euo pipefail
log="${1:?usage: compile-kotlin.sh <log> <task>...}"
shift
./gradlew -I .github/ci/kotlin-warnings-as-errors.gradle --continue --warning-mode all "$@" 2>&1 | tee "$log"
