#!/usr/bin/env bash
set -euo pipefail

if [[ $# -ne 1 ]]; then
  echo "Usage: tools/update_goldens.sh <processor-test-target>" >&2
  exit 1
fi

case "$1" in
  model_goldens|factory_goldens|injector_goldens|interceptor_goldens|includes_goldens|injector_graph)
    ;;
  *)
    echo "No generated goldens are owned by processor test target: $1" >&2
    exit 1
    ;;
esac

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
TARGET="$1"
cd "${ROOT}"

JAVA_TOOL_OPTIONS=-Dsting.output_fixture_data=true \
  bazel run "//processor/src/test/java/sting/processor:${TARGET}"

RUNFILES="$(bazel info bazel-bin)/processor/src/test/java/sting/processor/${TARGET}.runfiles/_main/processor/src/test/fixtures"
python3 - "${RUNFILES}" "${ROOT}/processor/src/test/fixtures" <<'PY'
import shutil
import sys
from pathlib import Path

source = Path(sys.argv[1])
destination = Path(sys.argv[2])
for tree in ("expected", "expectedFormatted"):
    root = source / tree
    if root.exists():
        for file in root.rglob("*"):
            if file.is_file():
                target = destination / tree / file.relative_to(root)
                target.parent.mkdir(parents=True, exist_ok=True)
                if not target.exists() or not file.samefile(target):
                    shutil.copyfile(file, target)
PY

bazel test "//processor/src/test/java/sting/processor:${TARGET}" --test_output=errors
git diff -- processor/src/test/fixtures/expected processor/src/test/fixtures/expectedFormatted
