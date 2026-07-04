#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

cd "${ROOT}"
tools/update_java_deps.sh
if ! git diff --exit-code -- MODULE.bazel MODULE.bazel.lock third_party/java/BUILD.bazel tools/java-format/BUILD.bazel; then
  echo "depgen generated outputs are stale; run tools/update_java_deps.sh" >&2
  exit 1
fi
bazel run //:buildifier_check
tools/java_format.sh check
bazel build //core //processor //server //doc-examples
bazel test //:all_tests
coverage_report="$(bazel info output_path)/_coverage/_coverage_report.dat"
bazel coverage \
  //processor/src/test/java/sting/processor:all_tests \
  //server/src/test/java/sting/server/interceptors:all_tests \
  --combined_report=lcov \
  --instrumentation_filter='^//(processor|server)[:/]'
tools/check_coverage.py "${coverage_report}" 0.94 0.85
