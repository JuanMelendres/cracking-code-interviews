#!/usr/bin/env python3
"""
Checks that every action item in a retrospective's "## Action Items" section
names an explicit Owner and Deadline -- the real, cheap, mechanical floor
against retro theater (action items proposed, never assigned, never revisited).
Exit 0 = every item has both fields, 1 = at least one item is missing one
(named explicitly, with which field).

Usage: python3 scripts/check_retro_action_items.py <path-to-retro.md> [more paths...]
"""
from __future__ import annotations
import re
import sys


def extract_action_items(text: str) -> list[str]:
    match = re.search(r"^##\s+Action Items\s*$(.*?)(?=^##\s+|\Z)", text, flags=re.MULTILINE | re.DOTALL)
    if not match:
        return []
    section = match.group(1)
    return [line.strip() for line in section.splitlines() if line.strip().startswith("- [")]


def check(path: str) -> bool:
    try:
        text = open(path, encoding="utf-8").read()
    except OSError as e:
        print(f"  ERROR  {path}: cannot read ({e})")
        return False

    items = extract_action_items(text)
    if not items:
        print(f"  FAIL   {path}: no \"## Action Items\" section, or it's empty")
        return False

    all_complete = True
    for item in items:
        missing = []
        if "Owner:" not in item:
            missing.append("Owner")
        if "Deadline:" not in item:
            missing.append("Deadline")
        if missing:
            all_complete = False
            print(f"  FAIL   {path}: item missing {', '.join(missing)}: \"{item}\"")

    if all_complete:
        print(f"  PASS   {path}: {len(items)} action item(s), all with an Owner and a Deadline")
    return all_complete


def main(argv: list[str]) -> int:
    if not argv:
        print("Usage: check_retro_action_items.py <path-to-retro.md> [more paths...]")
        return 2
    results = [check(p) for p in argv]
    return 0 if all(results) else 1


if __name__ == "__main__":
    sys.exit(main(sys.argv[1:]))
