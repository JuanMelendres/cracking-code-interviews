#!/usr/bin/env python3
"""Export flashcards/*.md decks to Anki-importable plain text.

Anki's "Import File" (Notes in Plain Text, tab-separated) expects one note
per line: Front<TAB>Back<TAB>Tags, with "Allow HTML in fields" enabled so a
multi-paragraph Back can use <br> instead of a real newline (a real newline
would break the one-note-per-line format).

Source of truth stays flashcards/*.md; output is a regenerable build
artifact (see dist/ in .gitignore), not committed.
"""
import re
import sys
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parent.parent
FLASHCARDS_DIR = REPO_ROOT / "flashcards"
OUTPUT_DIR = REPO_ROOT / "dist" / "anki"

FRONT_MATTER_RE = re.compile(r"^---\n(.*?)\n---\n", re.DOTALL)
CARD_RE = re.compile(
    r"^## Card: (?P<title>.+?)\n\n"
    r"\*\*Prompt:\*\*\n(?P<prompt>.+?)\n\n"
    r"\*\*Answer:\*\*\n(?P<answer>.+?)\n\n"
    r"\*\*Why it matters:\*\*\n(?P<why>.+?)\n\n"
    r"\*\*Common trap:\*\*\n(?P<trap>.+?)\n\n"
    r"\*\*Related:\*\*\n(?P<related>.+?)(?=\n\n## Card: |\n*$)",
    re.DOTALL | re.MULTILINE,
)


def parse_front_matter(text: str) -> dict:
    match = FRONT_MATTER_RE.match(text)
    if not match:
        raise ValueError("missing YAML front matter")
    fields = {}
    for line in match.group(1).splitlines():
        if ":" not in line:
            continue
        key, _, value = line.partition(":")
        fields[key.strip()] = value.strip().strip('"')
    return fields


def to_html_field(text: str) -> str:
    text = text.strip()
    text = text.replace("\t", " ")
    text = re.sub(r"\n+", "<br>", text)
    return text


def tag_token(value: str) -> str:
    return re.sub(r"[^A-Za-z0-9_-]+", "-", value).strip("-")


def parse_deck(path: Path) -> tuple[dict, list[dict]]:
    text = path.read_text(encoding="utf-8")
    front_matter = parse_front_matter(text)
    cards = []
    for match in CARD_RE.finditer(text):
        cards.append(
            {
                "title": match.group("title").strip(),
                "prompt": match.group("prompt"),
                "answer": match.group("answer"),
                "why": match.group("why"),
                "trap": match.group("trap"),
            }
        )
    return front_matter, cards


def build_rows(front_matter: dict, cards: list[dict]) -> tuple[list[str], str, str]:
    domain = front_matter.get("domain", "unknown")
    topic_id = front_matter.get("topic_id", "")
    slug = front_matter.get("slug", "")
    title = front_matter.get("title", slug).removeprefix("Flashcards: ").strip()
    tags = " ".join(t for t in (tag_token(domain), tag_token(topic_id)) if t)
    deck_name = f"Cracking Code Interviews::{domain}::{title}"

    rows = []
    for card in cards:
        front = to_html_field(card["prompt"])
        back = "<br><br>".join(
            [
                to_html_field(card["answer"]),
                f"<i>Why it matters:</i> {to_html_field(card['why'])}",
                f"<i>Common trap:</i> {to_html_field(card['trap'])}",
            ]
        )
        rows.append((front, back, tags))
    return rows, slug, deck_name


def export_deck(path: Path) -> tuple[list[tuple[str, str, str]], str]:
    front_matter, cards = parse_deck(path)
    if not cards:
        raise ValueError(f"no cards parsed from {path.name}")

    rows, slug, deck_name = build_rows(front_matter, cards)

    lines = ["\t".join(row) for row in rows]
    out_path = OUTPUT_DIR / f"{slug}.txt"
    header = "#separator:tab\n#html:true\n#tags column:3\n"
    out_path.write_text(header + "\n".join(lines) + "\n", encoding="utf-8")
    return rows, deck_name


def main() -> int:
    deck_paths = sorted(
        p for p in FLASHCARDS_DIR.glob("*.md") if p.name != "README.md"
    )
    if not deck_paths:
        print("no flashcard decks found", file=sys.stderr)
        return 1

    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)
    total_cards = 0
    failures = []
    combined_lines = []
    for path in deck_paths:
        try:
            rows, deck_name = export_deck(path)
        except ValueError as exc:
            failures.append(f"{path.name}: {exc}")
            continue
        total_cards += len(rows)
        for front, back, tags in rows:
            combined_lines.append("\t".join([front, back, tags, deck_name]))

    combined_header = (
        "#separator:tab\n#html:true\n#tags column:3\n#deck column:4\n"
    )
    (OUTPUT_DIR / "all-decks.txt").write_text(
        combined_header + "\n".join(combined_lines) + "\n", encoding="utf-8"
    )

    print(f"decks exported: {len(deck_paths) - len(failures)}/{len(deck_paths)}")
    print(f"cards exported: {total_cards}")
    print(f"output: {OUTPUT_DIR.relative_to(REPO_ROOT)}/ ({len(deck_paths) - len(failures)} per-deck files + all-decks.txt)")

    if failures:
        print("\nfailures:", file=sys.stderr)
        for failure in failures:
            print(f"  {failure}", file=sys.stderr)
        return 1
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
