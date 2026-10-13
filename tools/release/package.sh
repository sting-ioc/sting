#!/usr/bin/env bash
set -euo pipefail

if [[ $# -lt 1 ]]; then
  echo "Usage: tools/release/package.sh <version> [--unsigned]" >&2
  exit 1
fi

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
VERSION="$1"
shift

cd "${ROOT}"
bazel build //tools/release:maven_artifacts --//tools/release:version="${VERSION}"
python3 tools/release/package.py "${VERSION}" "$@"
