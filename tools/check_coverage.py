#!/usr/bin/env python3
import sys
from pathlib import Path


def main() -> int:
    if len(sys.argv) != 4:
        print("usage: tools/check_coverage.py LCOV_FILE MIN_LINE_RATIO MIN_BRANCH_RATIO", file=sys.stderr)
        return 2

    lcov_file = Path(sys.argv[1])
    min_line_ratio = float(sys.argv[2])
    min_branch_ratio = float(sys.argv[3])

    line_found = 0
    line_hit = 0
    branch_found = 0
    branch_hit = 0

    for line in lcov_file.read_text(encoding="utf-8").splitlines():
        if line.startswith("LF:"):
            line_found += int(line[3:])
        elif line.startswith("LH:"):
            line_hit += int(line[3:])
        elif line.startswith("BRF:"):
            branch_found += int(line[4:])
        elif line.startswith("BRH:"):
            branch_hit += int(line[4:])

    line_ratio = line_hit / line_found if line_found else 1.0
    branch_ratio = branch_hit / branch_found if branch_found else 1.0

    print(f"Line coverage: {line_ratio:.2%} ({line_hit}/{line_found})")
    print(f"Branch coverage: {branch_ratio:.2%} ({branch_hit}/{branch_found})")

    failed = False
    if line_ratio < min_line_ratio:
        print(f"Line coverage below required {min_line_ratio:.2%}", file=sys.stderr)
        failed = True
    if branch_ratio < min_branch_ratio:
        print(f"Branch coverage below required {min_branch_ratio:.2%}", file=sys.stderr)
        failed = True
    return 1 if failed else 0


if __name__ == "__main__":
    raise SystemExit(main())
