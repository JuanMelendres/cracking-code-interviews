---
title: "Cheat Sheet: Window Functions and CTEs"
slug: window-functions-and-ctes
document_type: cheat-sheet
domain: 06-databases
topic_id: T-2401
canonical: ../syllabus/06-databases/window-functions-and-ctes.md
last_updated: 2026-09-11
---

# Window Functions and CTEs

**Canonical chapter:** [`syllabus/06-databases/window-functions-and-ctes.md`](../syllabus/06-databases/window-functions-and-ctes.md)

## Core Mental Model

`GROUP BY` is a woodchipper — rows go in, one summarized row per group comes out. A window function is a clipboard — every original row survives, each gaining a computed value from a defined neighborhood of related rows. A recursive CTE teaches the database a starting point and a "next step" rule, then repeatedly applies it, like a `while` loop expressed declaratively.

## Essential Definitions

- **`PARTITION BY`** — narrows which rows count as "related" for the window function.
- **`ROW_NUMBER()`** — strict 1,2,3... rank, no ties.
- **`RANK()`** — ties share a rank, with a gap after (1,1,3).
- **`WITH RECURSIVE`** — a CTE that repeatedly applies a "next step" rule to its own growing result until no new rows appear.

## Decision Table

| Requirement | Tool | Real evidence |
|---|---|---|
| Top N rows per group, exactly N | `ROW_NUMBER() OVER (PARTITION BY ... ORDER BY ...)` | 4 rows for "top 2 per department" with a tie |
| Top N tiers per group, ties included | `RANK() OVER (...)` | 6 rows for the identical cutoff with the same tie |
| Running total | `SUM(...) OVER (ORDER BY ... ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW)` | Real 7-day cumulative total, verified |
| Moving average | `AVG(...) OVER (... ROWS BETWEEN N PRECEDING AND CURRENT ROW)` | Real 3-day moving average, verified |
| Hierarchy of unknown depth | `WITH RECURSIVE` | Real 3-level org chart, correctly bounded |

## Common Pitfalls

- Using `ROW_NUMBER()` when ties should share a rank (or vice versa) — they produce genuinely different row counts for "top N."
- Forgetting a recursive CTE needs a base case (anchor) and a genuinely terminating recursive step, or it can run unbounded.
- Confusing a window function's `PARTITION BY` with `GROUP BY` — window functions never collapse rows.

## Interview Answer Skeleton

**30-sec:** Window functions compute a value per row from a related neighborhood without collapsing rows (unlike `GROUP BY`); `ROW_NUMBER()` gives strict ranks, `RANK()` lets ties share one. `WITH RECURSIVE` walks a hierarchy of unknown depth declaratively.

**2-min:** Add: `ROW_NUMBER()` vs. `RANK()` produce genuinely different row counts for "top 2 per group" when there's a tie (4 vs. 6 rows, both real, verified) — a concrete way to demonstrate the distinction isn't cosmetic.

**Staff-level framing:** Recursive CTEs trade a hand-rolled application-side tree walk for one declarative query — know when that trade is worth it (bounded depth, DB-side recursion overhead) versus fetching and walking in application code.

## Related

- syllabus/06-databases/jsonb-and-advanced-index-types.md
- syllabus/06-databases/query-planning-and-explain-analyze.md
