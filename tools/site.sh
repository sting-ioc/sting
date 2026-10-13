#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
ACTION="${1:-}"
SITE_DIR="${ROOT}/reports/site"

build_site() {
  cd "${ROOT}"
  bazel build //website:site
  local site_output
  site_output="$(bazel info bazel-bin)/website/site"
  rm -rf "${SITE_DIR}"
  mkdir -p "${ROOT}/reports"
  cp -R "${site_output}" "${SITE_DIR}"
  chmod -R u+w "${SITE_DIR}"
}

case "${ACTION}" in
  build)
    build_site
    ;;
  check)
    cd "${ROOT}"
    bazel run //tools/site:tool -- check "${SITE_DIR}"
    ;;
  serve)
    build_site
    exec bazel run //tools/site:tool -- serve "${SITE_DIR}"
    ;;
  deploy)
    if [[ $# -ne 2 ]]; then
      echo 'Usage: tools/site.sh deploy <version>' >&2
      exit 2
    fi
    VERSION="$2"
    if [[ ! "${VERSION}" =~ ^[0-9]+(\.[0-9]+){1,2}$ ]]; then
      echo "Invalid release version: ${VERSION}" >&2
      exit 2
    fi
    if [[ "$(git -C "${ROOT}" describe --tags --exact-match HEAD 2>/dev/null || true)" != "v${VERSION}" ]]; then
      echo "Deploy from the v${VERSION} tag." >&2
      exit 1
    fi
    build_site
    bazel run //tools/site:tool -- check "${SITE_DIR}"
    DEPLOY_DIR="$(mktemp -d)"
    trap 'rm -rf "${DEPLOY_DIR}"' EXIT
    git clone --depth 1 https://github.com/sting-ioc/sting-ioc.github.io.git "${DEPLOY_DIR}"
    find "${DEPLOY_DIR}" -mindepth 1 -maxdepth 1 ! -name .git -exec rm -rf {} +
    cp -R "${SITE_DIR}/." "${DEPLOY_DIR}/"
    git -C "${DEPLOY_DIR}" add -A
    if ! git -C "${DEPLOY_DIR}" diff --cached --quiet; then
      git -C "${DEPLOY_DIR}" commit -m "Publish Sting ${VERSION} website"
      git -C "${DEPLOY_DIR}" push origin HEAD:master
    fi
    ;;
  *)
    echo 'Usage: tools/site.sh <build|check|serve|deploy> [version]' >&2
    exit 2
    ;;
esac
