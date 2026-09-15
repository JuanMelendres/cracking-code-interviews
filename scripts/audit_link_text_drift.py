#!/usr/bin/env python3
"""Heuristic scan for the recurring "link-text drift" bug: a Markdown link's
visible text was written to match its target chapter's title at the time,
but the target was later retitled/renamed and the link text never got
updated -- e.g. text reads "Distributed Transactions, Saga, and Outbox"
but the target's real current title is "Distributed Transactions: Saga,
Outbox, and 2PC".

DIAGNOSTIC ONLY -- does not modify any file. Found piecemeal across 6+
domains during the 2026-09 content-quality audit; this is the first
full repo-wide pass to confirm no further instances remain.

Method: build a map of every syllabus/ .md file's real title (front
matter `title:`, falling back to the first `# ` heading), then scan
every internal `[text](relative/path.md)` link and compare the link
text to the resolved target's real title via word-set Jaccard
similarity. A genuine drift case has HIGH-BUT-NOT-EXACT overlap (most
words shared, a few changed) -- unrelated-but-intentionally-different
link text (which this repo's own Cross-Reference Standards explicitly
allow) has LOW overlap. Flag only the middle band; exact matches are
fine, and low-overlap links are almost always legitimate contextual
phrasing, not drift.

Run: python3 scripts/audit_link_text_drift.py
"""
from __future__ import annotations

import os
import re

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SYLLABUS = os.path.join(ROOT, "syllabus")

TITLE_FRONTMATTER_RE = re.compile(r'^title:\s*["\']?(.*?)["\']?\s*$', re.MULTILINE)
H1_RE = re.compile(r'^#\s+(.+?)\s*$', re.MULTILINE)
LINK_RE = re.compile(r'\[([^\]]+)\]\(([^)]+?\.md)(#[^)]*)?\)')
WORD_RE = re.compile(r"[a-z0-9]+")

STOPWORDS = {
    "a", "an", "the", "and", "or", "of", "in", "on", "for", "to", "with",
    "vs", "vs.", "your", "you", "is", "are", "what", "how", "when",
}


def normalize_words(text: str) -> set[str]:
    words = WORD_RE.findall(text.lower())
    return {w for w in words if w not in STOPWORDS and len(w) > 1}


def real_title(path: str) -> str | None:
    try:
        text = open(path, encoding="utf-8", errors="replace").read()
    except OSError:
        return None
    fm = TITLE_FRONTMATTER_RE.search(text[:2000])
    if fm:
        return fm.group(1).strip()
    h1 = H1_RE.search(text)
    if h1:
        return h1.group(1).strip()
    return None


def build_title_map() -> dict[str, str]:
    titles: dict[str, str] = {}
    for dirpath, _, filenames in os.walk(SYLLABUS):
        for fn in filenames:
            if fn.endswith(".md"):
                p = os.path.join(dirpath, fn)
                t = real_title(p)
                if t:
                    titles[os.path.normpath(p)] = t
    return titles


def jaccard(a: set[str], b: set[str]) -> float:
    if not a or not b:
        return 0.0
    inter = len(a & b)
    union = len(a | b)
    return inter / union if union else 0.0


def main() -> None:
    titles = build_title_map()
    print(f"Indexed {len(titles)} chapter titles under syllabus/\n")

    candidates = []
    total_links = 0

    for dirpath, _, filenames in os.walk(SYLLABUS):
        for fn in filenames:
            if not fn.endswith(".md"):
                continue
            src_path = os.path.join(dirpath, fn)
            try:
                text = open(src_path, encoding="utf-8", errors="replace").read()
            except OSError:
                continue
            for m in LINK_RE.finditer(text):
                link_text, target_rel = m.group(1), m.group(2)
                if target_rel.startswith(("http://", "https://")):
                    continue
                total_links += 1
                target_abs = os.path.normpath(os.path.join(dirpath, target_rel))
                if target_abs not in titles:
                    continue  # not a resolvable internal syllabus link (could be external/practice/study-pack -- fine, skip)
                target_title = titles[target_abs]
                if link_text.strip() == target_title.strip():
                    continue
                stripped = link_text.strip()
                if stripped.startswith("`") and stripped.endswith("`"):
                    continue  # code-formatted filename/path citation, not a prose title reference
                score = jaccard(normalize_words(link_text), normalize_words(target_title))
                if 0.55 <= score < 0.95:
                    line_no = text[:m.start()].count("\n") + 1
                    candidates.append((
                        score,
                        os.path.relpath(src_path, ROOT),
                        line_no,
                        link_text,
                        target_title,
                        os.path.relpath(target_abs, ROOT),
                    ))

    print(f"Scanned {total_links} internal .md links.\n")
    print(f"=== {len(candidates)} candidate(s) with high-but-not-exact title overlap (possible drift) ===\n")
    candidates.sort(key=lambda c: -c[0])
    for score, src, line_no, link_text, target_title, target_rel in candidates:
        print(f"[{score:.2f}] {src}:{line_no}")
        print(f"    link text : {link_text!r}")
        print(f"    real title: {target_title!r}  ({target_rel})")
        print()


if __name__ == "__main__":
    main()
