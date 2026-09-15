#!/usr/bin/env python3
"""Fixes same-page anchor links whose #slug doesn't match any real heading
id on that page.

Root cause (confirmed against real built HTML, not guessed): headings like
"## 6. Production example (template -- fill from your own system)" or
"### Question 1 -- What's the Big O of this code, and why?" contain an
em-dash. MkDocs' real slugifier strips the em-dash to nothing and collapses
the surrounding spaces to ONE hyphen (real id:
`6-production-example-template-fill-from-your-own-system`), but every
hand-typed Table-of-Contents/cross-reference anchor across this repo was
written assuming the em-dash becomes a hyphen too, producing a double
hyphen (`#6-production-example-template--fill-from-your-own-system`) that
never matches anything. Confirmed identical across syllabus/ (208 files)
and study-packs/ (52 files) via `mkdocs build`'s own
"no such anchor on this page" INFO lines -- one systemic bug, not many.

Fix strategy: for each affected file, get the REAL heading ids from the
already-built site/ HTML (ground truth -- no need to reimplement MkDocs'
slugify algorithm), then for every same-page `](#anchor)` link in the
markdown source that doesn't match a real id, compare it against every
real id on that page with ALL hyphens stripped from both sides. If exactly
one real id matches after stripping hyphens, that's almost certainly the
intended target (the hyphen COUNT is exactly what's wrong; the underlying
character content is not) -- rewrite the link to the real id. If zero or
more than one real id matches, the link is left untouched and reported for
manual review rather than guessed at.

Must be run AFTER `mkdocs build` (reads site/ as ground truth) and BEFORE
trusting site/ as current (re-run mkdocs build afterward to confirm the
INFO count drops).
"""
from __future__ import annotations

import re
import sys
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parent.parent
SITE_DIR = REPO_ROOT / "site"

ANCHOR_LINK_RE = re.compile(r"\]\(#([^)\s]+)\)")
HEADING_ID_RE = re.compile(r'<h[1-6]\s+id="([^"]+)"')


def strip_hyphens(s: str) -> str:
    return s.replace("-", "")


def html_path_for(md_path: Path) -> Path:
    """docs-source md path (e.g. syllabus/foo/bar.md) -> its built HTML
    (site/syllabus/foo/bar/index.html), mirroring MkDocs' own convention.
    md_path may be absolute; Path.__truediv__ silently discards SITE_DIR
    if joined with an absolute operand, so relativize first. README.md is
    MkDocs' own directory-index convention -- it renders straight to
    <dir>/index.html, not <dir>/README/index.html."""
    if md_path.is_absolute():
        md_path = md_path.relative_to(REPO_ROOT)
    if md_path.name == "README.md":
        return SITE_DIR / md_path.parent / "index.html"
    rel = md_path.with_suffix("")
    return SITE_DIR / rel / "index.html"


def real_ids_for(md_path: Path) -> set[str] | None:
    html_path = html_path_for(md_path)
    if not html_path.is_file():
        return None
    html = html_path.read_text(encoding="utf-8", errors="replace")
    return set(HEADING_ID_RE.findall(html))


def fix_file(md_path: Path, real_ids: set[str]) -> tuple[int, list[str]]:
    text = md_path.read_text(encoding="utf-8")
    unresolved: list[str] = []
    fixed_count = 0

    stripped_index: dict[str, list[str]] = {}
    for real_id in real_ids:
        stripped_index.setdefault(strip_hyphens(real_id), []).append(real_id)

    def replace(match: re.Match) -> str:
        nonlocal fixed_count
        anchor = match.group(1)
        if anchor in real_ids:
            return match.group(0)
        candidates = stripped_index.get(strip_hyphens(anchor), [])
        if len(candidates) == 1:
            fixed_count += 1
            return f"](#{candidates[0]})"
        unresolved.append(anchor)
        return match.group(0)

    new_text = ANCHOR_LINK_RE.sub(replace, text)
    if fixed_count:
        md_path.write_text(new_text, encoding="utf-8")
    return fixed_count, unresolved


def main() -> int:
    files_arg = REPO_ROOT / "scripts" / ".broken_anchor_files.txt"
    if not files_arg.is_file():
        print(f"Expected {files_arg} (one repo-relative .md path per line) -- generate it from a real `mkdocs build` run's own 'no such anchor' INFO lines first.", file=sys.stderr)
        return 1

    total_fixed = 0
    total_unresolved = 0
    files_with_unresolved: list[str] = []

    for line in files_arg.read_text(encoding="utf-8").splitlines():
        line = line.strip()
        if not line:
            continue
        md_path = REPO_ROOT / line
        if not md_path.is_file():
            print(f"SKIP (not found): {line}")
            continue
        real_ids = real_ids_for(md_path)
        if real_ids is None:
            print(f"SKIP (no built HTML found): {line}")
            continue
        fixed, unresolved = fix_file(md_path, real_ids)
        total_fixed += fixed
        if unresolved:
            total_unresolved += len(unresolved)
            files_with_unresolved.append(f"{line}: {unresolved}")

    print(f"\nFixed {total_fixed} broken anchor link(s).")
    if total_unresolved:
        print(f"{total_unresolved} anchor(s) could not be resolved unambiguously (left untouched):")
        for entry in files_with_unresolved:
            print(f"  {entry}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
