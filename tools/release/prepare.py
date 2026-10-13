#!/usr/bin/env python3
"""Move Unreleased notes into a release and update the documented Maven version."""

import argparse
import re
from datetime import date
from pathlib import Path


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("version")
    args = parser.parse_args()
    version = args.version
    if not re.fullmatch(r"[0-9]+(?:\.[0-9]+){1,2}", version):
        parser.error("expected a numeric release version such as 0.41")

    root = Path(__file__).resolve().parents[2]
    changelog = root / "CHANGELOG.md"
    content = changelog.read_text()
    match = re.search(r"^### \[v([^]]+)\]", content, re.MULTILINE)
    if not match or "### Unreleased\n\n" not in content:
        raise SystemExit("Cannot locate Unreleased notes and previous release")
    previous = match.group(1)
    if previous == version or f"### [v{version}]" in content:
        raise SystemExit(f"Version {version} is already in the changelog")
    header = (
        f"### [v{version}](https://github.com/sting-ioc/sting/tree/v{version}) "
        f"({date.today().isoformat()}) · "
        f"[Full Changelog](https://github.com/sting-ioc/sting/compare/v{previous}...v{version})"
    )
    content = content.replace("### Unreleased\n\n", f"### Unreleased\n\n{header}\n\n", 1)
    changelog.write_text(content)

    setup = root / "docs" / "project_setup.md"
    current = setup.read_text()
    updated, count = re.subn(r"<version>[^<]+</version>", f"<version>{version}</version>", current)
    if count == 0:
        raise SystemExit("No Maven version found in docs/project_setup.md")
    setup.write_text(updated)
    print(f"Prepared v{version} from v{previous}; review CHANGELOG.md and docs/project_setup.md")


if __name__ == "__main__":
    main()
