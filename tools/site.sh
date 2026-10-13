#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
ACTION="${1:-}"
SITE_DIR="${ROOT}/reports/site"

extract_api() {
  mkdir -p "$1"
  python3 - "${ROOT}/bazel-bin/tools/release/sting-api-javadoc.jar" "$1" <<'PY'
import sys
import zipfile
from pathlib import Path
with zipfile.ZipFile(sys.argv[1]) as archive:
    archive.extractall(Path(sys.argv[2]))
PY
}

build_site() {
  cd "${ROOT}"
  if [[ ! -x node_modules/.bin/docusaurus-build ]]; then
    echo 'Install site dependencies with npm ci first.' >&2
    exit 1
  fi
  bazel build //tools/release:site_javadoc //doc-examples
  rm -rf "${ROOT}/reports/doc-generated"
  mkdir -p "${ROOT}/reports/doc-generated"
  python3 - "${ROOT}" <<'PY'
import sys
import zipfile
from pathlib import Path
root = Path(sys.argv[1])
for part in ("include_injector/auto/libauto-gensrc.jar", "inputs/libinputs-gensrc.jar"):
    jar = root / "bazel-bin/doc-examples/src/main/java/sting/doc/examples" / part
    with zipfile.ZipFile(jar) as archive:
        archive.extractall(root / "reports/doc-generated")
PY
  npm run build
  rm -rf "${SITE_DIR}"
  mkdir -p "${ROOT}/reports"
  cp -R "${ROOT}/website/build/sting" "${SITE_DIR}"
  extract_api "${SITE_DIR}/api"
  cp "${ROOT}"/assets/favicons/* "${SITE_DIR}/"
}

case "${ACTION}" in
  build)
    build_site
    ;;
  check)
    python3 "${ROOT}/tools/check_site_links.py" "${SITE_DIR}"
    ;;
  serve)
    cd "${ROOT}"
    bazel build //tools/release:site_javadoc
    rm -rf "${ROOT}/website/static/api"
    extract_api "${ROOT}/website/static/api"
    npm start
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
    python3 "${ROOT}/tools/check_site_links.py" "${SITE_DIR}"
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
