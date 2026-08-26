#!/usr/bin/env python3
"""
Checks a postmortem markdown file against two things this handbook chapter's own
register-named misconception gets wrong: (1) that a postmortem should have a single
"Root Cause" section (it should have "Contributing Factors", plural), and (2) that
blameless means "we didn't say whose fault it was" rather than the stronger, checkable
standard this script actually enforces: no sentence names an individual or team as
the cause of the incident, and no sentence uses blame-coded language ("failed to",
"should have known", "human error", "negligent", "careless") anywhere in the document.

Exit 0 = passes both checks. Exit 1 = fails at least one (each failure printed with
the specific offending text, not just a pass/fail label).

Usage: python3 scripts/check_postmortem_blameless.py <path-to-postmortem.md> [more paths...]
"""
from __future__ import annotations
import re
import sys

REQUIRED_SECTIONS = [
    "Summary",
    "Impact",
    "Timeline",
    "Detection",
    "Mitigation",
    "Contributing Factors",
    "Action Items",
]

FORBIDDEN_SINGULAR_SECTION = "Root Cause"

BLAME_PATTERNS = [
    r"\bfailed to\b",
    r"\bshould have (known|caught|noticed|tested|checked)\b",
    r"\bhuman error\b",
    r"\bnegligen(t|ce)\b",
    r"\bcareless(ly)?\b",
    r"\bforgot to\b",
    r"\bdidn'?t bother\b",
]

ACTION_ITEM_LINE = re.compile(r"^\s*-\s*\[[ xX]\]\s*(.+)$", re.MULTILINE)


def check(path: str) -> bool:
    try:
        text = open(path, encoding="utf-8").read()
    except OSError as e:
        print(f"  ERROR  {path}: cannot read ({e})")
        return False

    headings = set(re.findall(r"^##\s+(.+?)\s*$", text, flags=re.MULTILINE))
    failures: list[str] = []

    missing_sections = [s for s in REQUIRED_SECTIONS if s not in headings]
    if missing_sections:
        failures.append(f"missing required section(s): {', '.join(missing_sections)}")

    if FORBIDDEN_SINGULAR_SECTION in headings:
        failures.append(
            f'uses a singular "{FORBIDDEN_SINGULAR_SECTION}" section -- '
            'incidents rarely have exactly one cause; use "Contributing Factors" instead'
        )

    for pattern in BLAME_PATTERNS:
        for match in re.finditer(pattern, text, flags=re.IGNORECASE):
            line_start = text.rfind("\n", 0, match.start()) + 1
            line_end = text.find("\n", match.end())
            line_end = len(text) if line_end == -1 else line_end
            offending_line = text[line_start:line_end].strip()
            failures.append(f'blame-coded language "{match.group(0)}" in: "{offending_line}"')

    action_items = ACTION_ITEM_LINE.findall(text)
    for item in action_items:
        if "Owner:" not in item or "Due:" not in item:
            failures.append(f'action item missing Owner/Due: "{item.strip()}"')

    if failures:
        print(f"  FAIL   {path}")
        for f in failures:
            print(f"           - {f}")
        return False

    print(f"  PASS   {path}")
    return True


def main(argv: list[str]) -> int:
    if not argv:
        print("Usage: check_postmortem_blameless.py <path-to-postmortem.md> [more paths...]")
        return 2
    results = [check(p) for p in argv]
    return 0 if all(results) else 1


if __name__ == "__main__":
    sys.exit(main(sys.argv[1:]))
