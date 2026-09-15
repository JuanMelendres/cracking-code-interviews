#!/usr/bin/env python3
"""Generates docs/assets/learning-path-next-index.json: for a syllabus
chapter that is one of a learning path's explicitly named, ordered
topics, what is the REAL next topic that same path names -- never a
computed/inferred ordering.

Why this is safe (the concern that blocked building this earlier): most
learning paths only name 2-4 "priority topics" per domain, not an
exhaustive per-chapter sequence -- computing "next" from a domain's full
INDEX.md would assert an ordering the source material never defines.
This script never does that. It only uses two kinds of ALREADY-REAL,
ALREADY-ORDERED source data:

  1. "Type A" paths (junior-to-mid.md, senior-to-staff.md,
     frontend-junior-to-mid.md, frontend-mid-to-senior.md): a single
     flat table, one row per topic, each row already numbered (#1, #2,
     #3...) with a real Markdown link. The author-written row order IS
     the real sequence -- reading consecutive row numbers is not an
     inference.
  2. "Type B" paths (mid-to-senior.md): one row per DOMAIN, each row's
     "priority topics" cell is a semicolon-separated, author-written
     ORDERED list of topic titles (plain text, resolved against real
     chapter titles). Consecutive topics within one domain's list, and
     the last topic of one domain row into the first topic of the next
     domain row, are both real, explicit order the source already states.

backend-java-specialization.md (subdomain-cluster granularity, not
chapter-level), interview-emergency-sprint.md and
senior-interview-refresh.md (no per-chapter ordered table) are
deliberately excluded -- they don't have this kind of usable, real,
chapter-level ordering to read.

A chapter with no entry in the output JSON is not "missing" a next
suggestion -- it's simply not one of the small set of topics any
learning path calls out by name, exactly like today's absence of
priority-topic status for most chapters.

Run: python3 scripts/generate_learning_path_next_index.py
"""
from __future__ import annotations

import json
import os
import re

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
PATHS_DIR = os.path.join(ROOT, "syllabus", "00-overview", "learning-paths")
SYLLABUS = os.path.join(ROOT, "syllabus")
OUT_PATH = os.path.join(ROOT, "docs", "assets", "learning-path-next-index.json")

PATH_LABELS = {
    "junior-to-mid": "Junior → Mid",
    "mid-to-senior": "Mid → Senior",
    "senior-to-staff": "Senior → Staff",
    "frontend-junior-to-mid": "Frontend Junior → Mid",
    "frontend-mid-to-senior": "Frontend Mid → Senior",
}

TYPE_A_FILES = [
    "junior-to-mid.md",
    "senior-to-staff.md",
    "frontend-junior-to-mid.md",
    "frontend-mid-to-senior.md",
]
TYPE_B_FILES = ["mid-to-senior.md"]

ROW_RE = re.compile(r"^\|\s*(\d+)\s*\|\s*\[([^\]]+)\]\(([^)]+?\.md)\)", re.MULTILINE)
TITLE_FRONTMATTER_RE = re.compile(r'^title:\s*["\']?(.*?)["\']?\s*$', re.MULTILINE)
H1_RE = re.compile(r"^#\s+(.+?)\s*$", re.MULTILINE)
NEXT_SECTION_RE = re.compile(r"^## Next\s*\n\n\[([^\]]+)\]\(([^)]+?\.md)\)", re.MULTILINE)
SEQUENCE_ROW_RE = re.compile(
    r"^\|\s*\d+\s*\|\s*\[[^\]]+\]\([^)]+\)\s*\|\s*(.+?)\s*\|\s*[^|]+\|\s*$",
    re.MULTILINE,
)


def real_title(path: str) -> str | None:
    try:
        text = open(path, encoding="utf-8", errors="replace").read()
    except OSError:
        return None
    fm = TITLE_FRONTMATTER_RE.search(text[:2000])
    if fm:
        return fm.group(1).strip()
    h1 = H1_RE.search(text)
    return h1.group(1).strip() if h1 else None


def build_title_map() -> dict[str, str]:
    titles: dict[str, str] = {}
    for dirpath, _, filenames in os.walk(SYLLABUS):
        for fn in filenames:
            if fn.endswith(".md"):
                p = os.path.join(dirpath, fn)
                t = real_title(p)
                if t:
                    titles[t] = os.path.normpath(p)
    return titles


def chapter_key(abs_path: str) -> str:
    rel = os.path.relpath(abs_path, ROOT)
    return rel.replace(os.sep, "/")


def parse_type_a(path_file: str) -> list[tuple[str, str]]:
    """Returns an ordered list of (title, abs_path) from a flat numbered table."""
    text = open(path_file, encoding="utf-8").read()
    base_dir = os.path.dirname(path_file)
    rows: list[tuple[int, str, str]] = []
    for m in ROW_RE.finditer(text):
        num, title, rel_target = int(m.group(1)), m.group(2), m.group(3)
        abs_target = os.path.normpath(os.path.join(base_dir, rel_target))
        if os.path.isfile(abs_target):
            rows.append((num, title, abs_target))
    rows.sort(key=lambda r: r[0])
    return [(title, abs_path) for _, title, abs_path in rows]


def parse_type_b(path_file: str, title_map: dict[str, str]) -> list[tuple[str, str]]:
    """Returns an ordered list of (title, abs_path) from a per-domain
    row's semicolon-separated priority-topics cell, flattened across
    domain rows in their own written order."""
    text = open(path_file, encoding="utf-8").read()
    ordered: list[tuple[str, str]] = []
    unresolved: list[str] = []
    for m in SEQUENCE_ROW_RE.finditer(text):
        cell = m.group(1)
        for raw_name in cell.split(";"):
            name = raw_name.strip()
            if not name:
                continue
            abs_path = title_map.get(name)
            if abs_path:
                ordered.append((name, abs_path))
            else:
                unresolved.append(name)
    if unresolved:
        print(f"  WARN {os.path.basename(path_file)}: could not resolve {len(unresolved)} topic name(s) to a real chapter title: {unresolved}")
    return ordered


def find_next_path_link(path_file: str) -> str | None:
    text = open(path_file, encoding="utf-8").read()
    m = NEXT_SECTION_RE.search(text)
    if not m:
        return None
    return m.group(1)  # display text of the "## Next" link, e.g. "Mid -> Senior"


def main() -> None:
    title_map = build_title_map()
    result: dict[str, list[dict]] = {}

    for fn in TYPE_A_FILES + TYPE_B_FILES:
        path_file = os.path.join(PATHS_DIR, fn)
        if not os.path.isfile(path_file):
            print(f"SKIP (not found): {fn}")
            continue
        slug = fn[:-3]
        label = PATH_LABELS.get(slug, slug)

        if fn in TYPE_A_FILES:
            ordered = parse_type_a(path_file)
        else:
            ordered = parse_type_b(path_file, title_map)

        if not ordered:
            print(f"WARN {fn}: resolved zero ordered topics")
            continue

        next_path_label = find_next_path_link(path_file)

        for i, (title, abs_path) in enumerate(ordered):
            key = chapter_key(abs_path)
            if i + 1 < len(ordered):
                next_title, next_abs = ordered[i + 1]
                entry = {
                    "path": label,
                    "next_title": next_title,
                    "next_key": chapter_key(next_abs),
                    "path_complete": False,
                }
            else:
                entry = {
                    "path": label,
                    "next_title": next_path_label,
                    "next_key": None,
                    "path_complete": True,
                }
            result.setdefault(key, []).append(entry)

        print(f"OK   {fn}: {len(ordered)} ordered topics")

    os.makedirs(os.path.dirname(OUT_PATH), exist_ok=True)
    with open(OUT_PATH, "w", encoding="utf-8") as f:
        json.dump(result, f, indent=0, sort_keys=True)

    total_refs = sum(len(v) for v in result.values())
    print(f"\nWrote {OUT_PATH}: {len(result)} chapters, {total_refs} path-sequence references")


if __name__ == "__main__":
    main()
