#!/usr/bin/env python3
"""
Checks that an Architecture Decision Record markdown file has the four sections
Michael Nygard's original ADR pattern requires: Status, Context, Decision,
Consequences. Exit 0 = complete, 1 = missing a required section (named explicitly).

Usage: python3 scripts/check_adr_completeness.py <path-to-adr.md> [more paths...]
"""
from __future__ import annotations
import re
import sys

REQUIRED_SECTIONS = ["Status", "Context", "Decision", "Consequences"]


def check(path: str) -> bool:
    try:
        text = open(path, encoding="utf-8").read()
    except OSError as e:
        print(f"  ERROR  {path}: cannot read ({e})")
        return False

    headings = set(re.findall(r"^##\s+(.+?)\s*$", text, flags=re.MULTILINE))
    missing = [s for s in REQUIRED_SECTIONS if s not in headings]

    if missing:
        print(f"  FAIL   {path}: missing {', '.join(missing)}")
        return False

    print(f"  PASS   {path}")
    return True


def main(argv: list[str]) -> int:
    if not argv:
        print("Usage: check_adr_completeness.py <path-to-adr.md> [more paths...]")
        return 2
    results = [check(p) for p in argv]
    return 0 if all(results) else 1


if __name__ == "__main__":
    sys.exit(main(sys.argv[1:]))
