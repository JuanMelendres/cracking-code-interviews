#!/usr/bin/env python3
"""Measure every layered interview answer against how long it takes to SAY.

A "30-Second Answer" is a delivery instruction, not a label. This script checks
whether the repository's own answers can actually be delivered in the time their
heading claims, at a realistic speaking rate for technical explanation under
interview pressure.

Rate: 140 words per minute. That is the middle of the commonly cited 130-150 wpm
band for prepared technical speech, and deliberately not the 160+ some sources
quote for casual conversation -- an interview answer is slower, because the
speaker is also thinking. At 140 wpm:

    30 seconds  ->  70 words
    2 minutes   -> 280 words
    10 minutes  -> 1400 words

Usage:
    python3 scripts/audit_answer_delivery_length.py [--verbose] [path ...]
"""
import re
import sys
from pathlib import Path

WPM = 140
BUDGETS = {
    "30-Second Answer": 30,
    "2-Minute Answer": 120,
    "10-Minute Deep Dive": 600,
}
HEADING = re.compile(r"^#{3,4}\s+(.+?)\s*$", re.M)


def sections(text):
    """Yield (heading, body) for every H3/H4 in the document."""
    marks = [(m.start(), m.end(), m.group(1)) for m in HEADING.finditer(text)]
    for i, (_, end, title) in enumerate(marks):
        stop = marks[i + 1][0] if i + 1 < len(marks) else len(text)
        yield title, text[end:stop]


def spoken_words(body):
    """Count words a candidate would actually say.

    Fenced code blocks are drawn or shown, not spoken, so they are excluded;
    counting them would make a code-heavy answer look unspeakably long when it
    is not. Inline markdown is stripped so formatting does not inflate counts.
    """
    body = re.sub(r"```.*?```", " ", body, flags=re.S)
    body = re.sub(r"\[([^\]]*)\]\([^)]*\)", r"\1", body)
    body = re.sub(r"[`*_>#|]", " ", body)
    return len(body.split())


def main(argv):
    verbose = "--verbose" in argv
    paths = [a for a in argv if not a.startswith("--")] or ["syllabus"]

    rows = []
    for root in paths:
        root = Path(root)
        files = [root] if root.is_file() else sorted(root.rglob("*.md"))
        for p in files:
            try:
                text = p.read_text(encoding="utf-8")
            except (OSError, UnicodeDecodeError):
                continue
            for title, body in sections(text):
                if title not in BUDGETS:
                    continue
                words = spoken_words(body)
                budget = BUDGETS[title]
                seconds = words / WPM * 60
                rows.append((title, p, words, budget, seconds))

    print(f"Speaking rate assumed: {WPM} wpm "
          f"(30s = {int(WPM/2)} words, 2min = {WPM*2} words, 10min = {WPM*10} words)\n")

    for label, budget in BUDGETS.items():
        group = [r for r in rows if r[0] == label]
        if not group:
            continue
        over = [r for r in group if r[4] > budget]
        worst = sorted(group, key=lambda r: -r[4])[:10]
        pct = 100 * len(over) / len(group)
        print(f"== {label} ==  ({budget}s budget = {int(budget/60*WPM)} words)")
        print(f"   sections: {len(group)}   over budget: {len(over)} ({pct:.0f}%)")
        if group:
            med = sorted(r[4] for r in group)[len(group) // 2]
            print(f"   median actual delivery time: {med:.0f}s "
                  f"({med/budget:.1f}x the budget)")
        if verbose or label == "30-Second Answer":
            print(f"   longest {min(10, len(worst))}:")
            for _, p, words, b, secs in worst:
                print(f"     {secs:6.0f}s  {words:5} words  "
                      f"{secs/b:4.1f}x  {str(p).replace('syllabus/', '')}")
        print()

    return 0


if __name__ == "__main__":
    sys.exit(main(sys.argv[1:]))
