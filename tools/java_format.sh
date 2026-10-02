#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
MODE="${1:-write}"

case "${MODE}" in
  write | check | watch)
    ;;
  *)
    echo "usage: tools/java_format.sh [write|check|watch]" >&2
    exit 2
    ;;
esac

cd "${ROOT}"

paths=(
  core/src/main/java
  processor/src/main/java
  processor/src/test/java
  processor/src/test/fixtures/bad_input
  processor/src/test/fixtures/input
  server/src/main/java
  server/src/test/java
  integration-tests/src/test/java
  server-integration-tests/src/test/java
  doc-examples/src/main/java
)

format_roots=()
for path in "${paths[@]}"; do
  format_roots+=("--root=${path}")
done

case "${MODE}" in
  check)
    exec bazel build //:java_format_check
    ;;
  write)
    exec bazel run @rules_palantir_java_format//:java_format -- "${format_roots[@]}"
    ;;
  watch)
    exec bazel run @rules_palantir_java_format//:java_format_watch -- "${format_roots[@]}"
    ;;
esac
