#!/usr/bin/env python3
"""Heuristic scan for the "multiple intelligences" / engagement content
audit: which syllabus chapters lean hardest on pure prose, with little
visual/structural variety (diagrams, tables, code, real-life analogies)?

This is a DIAGNOSTIC ONLY tool -- it does not modify any content. It
scores each chapter and prints a ranked report so a human (or a
follow-up focused pass) can decide which chapters actually deserve
attention, rather than editing based on a raw heuristic score alone.

Metrics per file, all counted directly from the raw Markdown text:
  - word_count: total words
  - mermaid: number of ```mermaid fenced blocks
  - other_code: number of non-mermaid fenced code blocks
  - tables: number of Markdown tables (a run of consecutive `|...|` lines)
  - analogy_hits: occurrences of common real-life-analogy phrasing
    ("think of", "the same way", "like a", "imagine", "analogy")
  - has_l1_foundation: whether a "Level 1 -- Foundation" (or equivalent
    L1/Foundation) heading exists at all

Score: visual/structural elements per 1,000 words (mermaid+tables+
other_code, weighted so a diagram counts more than a generic code
block), plus a flag for zero analogy hits and zero L1 section.

Run: python3 scripts/audit_content_engagement.py
"""
from __future__ import annotations

import os
import re
from dataclasses import dataclass, field

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SYLLABUS = os.path.join(ROOT, "syllabus")

SKIP_NAMES = {"INDEX.md", "changelog.md", "vision.md", "taxonomy.md",
              "mastery-model.md", "topic-specification.md"}
SKIP_DIR_PARTS = {"00-overview", "question-bank"}

MERMAID_RE = re.compile(r"```mermaid\b")
FENCE_RE = re.compile(r"^```", re.MULTILINE)
TABLE_ROW_RE = re.compile(r"^\|.+\|\s*$", re.MULTILINE)
ANALOGY_RE = re.compile(
    r"\b(think of|the same way|like a\b|like an\b|imagine|analogy|"
    r"is exactly the way|the way you'?d|picture a\b|similar to|much like|"
    r"resembles|akin to|just as a\b|just as an\b|in the same way)",
    re.IGNORECASE,
)
L1_HEADING_RE = re.compile(
    r"^##+\s*(Level\s*1|L1)\b.*(Foundation)?", re.IGNORECASE | re.MULTILINE
)


@dataclass
class ChapterScore:
    path: str
    domain: str
    word_count: int = 0
    mermaid: int = 0
    other_code: int = 0
    tables: int = 0
    analogy_hits: int = 0
    has_l1: bool = False
    density: float = 0.0


def count_tables(text: str) -> int:
    """Count distinct Markdown tables -- a table is a maximal run of
    consecutive lines each matching a `|...|` row pattern."""
    lines = text.split("\n")
    tables = 0
    in_table = False
    for line in lines:
        is_row = bool(TABLE_ROW_RE.match(line))
        if is_row and not in_table:
            tables += 1
            in_table = True
        elif not is_row:
            in_table = False
    return tables


def count_fences(text: str) -> tuple[int, int]:
    """Return (mermaid_count, other_code_count). Fences come in pairs;
    count opening fences only, distinguishing mermaid vs. other."""
    mermaid = len(MERMAID_RE.findall(text))
    total_fences = len(FENCE_RE.findall(text)) // 2  # opens == closes, so /2 pairs... but easier: count ``` occurrences / 2
    other = max(total_fences - mermaid, 0)
    return mermaid, other


def score_file(path: str, domain: str) -> ChapterScore:
    text = open(path, encoding="utf-8", errors="replace").read()
    words = len(text.split())
    mermaid, other_code = count_fences(text)
    tables = count_tables(text)
    analogy_hits = len(ANALOGY_RE.findall(text))
    has_l1 = bool(L1_HEADING_RE.search(text))

    # Weighted visual/structural density per 1,000 words: a diagram is
    # worth more than a generic code fence (a diagram is deliberately
    # explanatory; a code fence is often just an example, not a visual aid).
    weighted = (mermaid * 3) + (other_code * 1) + (tables * 2)
    density = (weighted / words * 1000) if words else 0.0

    return ChapterScore(
        path=os.path.relpath(path, ROOT), domain=domain, word_count=words,
        mermaid=mermaid, other_code=other_code, tables=tables,
        analogy_hits=analogy_hits, has_l1=has_l1, density=density,
    )


def collect() -> list[ChapterScore]:
    scores = []
    for dirpath, dirnames, filenames in os.walk(SYLLABUS):
        rel = os.path.relpath(dirpath, SYLLABUS)
        parts = set(rel.split(os.sep))
        if parts & SKIP_DIR_PARTS:
            dirnames[:] = []
            continue
        domain = rel.split(os.sep)[0] if rel != "." else "?"
        for fn in filenames:
            if not fn.endswith(".md") or fn in SKIP_NAMES:
                continue
            scores.append(score_file(os.path.join(dirpath, fn), domain))
    return scores


def main() -> None:
    scores = collect()
    scores.sort(key=lambda s: s.density)

    print(f"Scanned {len(scores)} chapters across syllabus/\n")

    print("=== Bottom 30 by visual/structural density (candidates for review) ===")
    print(f"{'density':>8} {'words':>6} {'mmd':>4} {'code':>5} {'tbl':>4} {'analogy':>8} {'L1?':>4}  path")
    for s in scores[:30]:
        print(f"{s.density:8.2f} {s.word_count:6d} {s.mermaid:4d} {s.other_code:5d} {s.tables:4d} {s.analogy_hits:8d} {'Y' if s.has_l1 else 'N':>4}  {s.path}")

    print("\n=== Chapters with ZERO real-life-analogy phrasing hits ===")
    no_analogy = [s for s in scores if s.analogy_hits == 0]
    print(f"{len(no_analogy)} of {len(scores)} chapters\n")
    for s in sorted(no_analogy, key=lambda s: s.path):
        print(f"  {s.path}")

    print("\n=== Chapters with NO Level 1 / Foundation heading at all ===")
    no_l1 = [s for s in scores if not s.has_l1]
    print(f"{len(no_l1)} of {len(scores)} chapters\n")
    for s in sorted(no_l1, key=lambda s: s.path):
        print(f"  {s.path}")

    print("\n=== Per-domain averages (density, sorted worst-first) ===")
    by_domain: dict[str, list[ChapterScore]] = {}
    for s in scores:
        by_domain.setdefault(s.domain, []).append(s)
    domain_avgs = []
    for d, items in by_domain.items():
        avg_density = sum(i.density for i in items) / len(items)
        zero_analogy = sum(1 for i in items if i.analogy_hits == 0)
        no_l1_count = sum(1 for i in items if not i.has_l1)
        domain_avgs.append((avg_density, d, len(items), zero_analogy, no_l1_count))
    domain_avgs.sort()
    print(f"{'avg_density':>11} {'chapters':>8} {'0-analogy':>9} {'no-L1':>5}  domain")
    for avg_density, d, n, za, nl in domain_avgs:
        print(f"{avg_density:11.2f} {n:8d} {za:9d} {nl:5d}  {d}")


if __name__ == "__main__":
    main()
