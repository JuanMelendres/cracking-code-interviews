---
title: "Cheat Sheet: JSONB and Advanced Index Types"
slug: jsonb-and-advanced-index-types
document_type: cheat-sheet
domain: 06-databases
topic_id: T-2402
canonical: ../syllabus/06-databases/jsonb-and-advanced-index-types.md
last_updated: 2026-09-11
---

# JSONB and Advanced Index Types

**Canonical chapter:** [`syllabus/06-databases/jsonb-and-advanced-index-types.md`](../syllabus/06-databases/jsonb-and-advanced-index-types.md)

## Core Mental Model

GIN answers "which rows contain this specific component" (an inverted index, like a book's index). GiST answers "which rows overlap or are near this value" (nested bounding regions, like a map). BRIN answers "which storage blocks might contain this value" (a tiny per-block summary, like a library shelf label) — not an index of rows at all.

## Essential Definitions

- **GIN** — inverted index; best for JSONB containment (`@>`) and full-text search.
- **GiST** — bounding-region tree; best for overlap/exclusion constraints (ranges, geometry).
- **BRIN** — block range index; tiny, best for huge tables physically ordered by a correlated column.

## Decision Table

| Need | Index type | Real evidence |
|---|---|---|
| JSONB containment, high selectivity | GIN | Wins dramatically (full-text case: ~3,780×) |
| JSONB containment, low selectivity | GIN | Real regression measured (11.988ms → 13.896ms) — verify before assuming a win |
| Full-text search | GIN on `to_tsvector(...)` | Real ~3,780× win (721.775ms → 0.191ms) |
| No-overlap integrity constraint | GiST `EXCLUDE` | Real, atomic rejection of an overlapping booking |
| Very large, naturally-ordered table | BRIN | Real ~1,834× smaller; verify the planner actually uses it |

## Common Pitfalls

- Assuming any GIN index is automatically a win — a real, measured case shows a low-selectivity GIN lookup can regress versus a sequential scan.
- Using BRIN on a table not physically ordered by the indexed column — it degrades to nearly useless without that correlation.
- Reaching for an application-level uniqueness check instead of a GiST `EXCLUDE` constraint for overlap prevention — loses atomicity under concurrent writes.

## Interview Answer Skeleton

**30-sec:** GIN for containment/full-text (inverted index), GiST for overlap/exclusion (bounding regions), BRIN for huge, naturally-ordered tables (tiny block-range summary) — pick by the question the query actually asks, not by habit.

**2-min:** Add: GIN isn't a universal win — a real, measured low-selectivity case regresses versus no index at all; always verify selectivity before assuming a GIN win. BRIN's ~1,834× size advantage only pays off if the planner actually chooses it — verify with `EXPLAIN`.

**Staff-level framing:** Index choice is a query-shape decision, not a one-size-fits-all default — the same JSONB column can need GIN for one query pattern and be actively wrong for another.

## Related

- syllabus/06-databases/window-functions-and-ctes.md
- syllabus/06-databases/index-structures-btree-composite-covering.md
