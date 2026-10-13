#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

cd "${ROOT}"
bazel run //:buildifier_check
bazel run //tools/ahab:hermeticity.check
tools/java_format.sh check
bazel build //core //processor //server //doc-examples //tools/release:maven_artifacts
python3 tools/release/check_artifacts.py
bazel test //:all_tests
coverage_report="$(bazel info output_path)/_coverage/_coverage_report.dat"
bazel coverage \
  //processor/src/test/java/sting/processor:all_tests \
  //server/src/test/java/sting/server/interceptors:all_tests \
  --combined_report=lcov \
  --instrumentation_filter='^//(processor|server)[:/]'
tools/check_coverage.py "${coverage_report}" 0.94 0.85
tools/site.sh build
tools/site.sh check
