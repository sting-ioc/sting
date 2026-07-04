#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
MODE="${1:-write}"

case "${MODE}" in
  write | check)
    ;;
  *)
    echo "usage: tools/java_format.sh [write|check]" >&2
    exit 2
    ;;
esac

cd "${ROOT}"

args_file="$(mktemp)"
trap 'rm -f "${args_file}"' EXIT

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

for path in "${paths[@]}"; do
  if [[ -d "${path}" ]]; then
    find "${path}" -type f -name '*.java'
  fi
done | sort | while IFS= read -r source_file; do
  printf '%s/%s\n' "${ROOT}" "${source_file}" >> "${args_file}"
done

if [[ ! -s "${args_file}" ]]; then
  exit 0
fi

if [[ "${MODE}" == "check" ]]; then
  bazel run //tools/java-format:palantir_java_format -- \
    --palantir \
    --dry-run \
    --set-exit-if-changed \
    "@${args_file}"
else
  bazel run //tools/java-format:palantir_java_format -- \
    --palantir \
    --replace \
    "@${args_file}"
fi
