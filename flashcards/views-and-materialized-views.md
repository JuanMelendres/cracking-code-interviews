---
title: "Flashcards: Views and Materialized Views"
slug: views-and-materialized-views
document_type: flashcard-deck
domain: 06-databases
topic_id: T-2410
canonical: ../syllabus/06-databases/views-and-materialized-views.md
last_updated: 2026-09-15
---

# Flashcards: Views and Materialized Views

**Canonical chapter:** [`syllabus/06-databases/views-and-materialized-views.md`](../syllabus/06-databases/views-and-materialized-views.md)

## Card: View vs. materialized view, one sentence each

**Prompt:**
In one sentence each, what's the real difference between a view and a materialized view?

**Answer:**
A view is a stored query re-run on every read, with no storage of its own; a materialized view is a stored result set, fast to read, that stays unchanged until an explicit `REFRESH`.

**Why it matters:**
The single most commonly asked framing of this topic — a shallow "one's cached" answer collapses under any follow-up.

**Common trap:**
Calling a plain view "cached" — nothing is stored.

**Related:**
[Views and Materialized Views](../syllabus/06-databases/views-and-materialized-views.md)

## Card: The automatic-updatability rule

**Prompt:**
What exact conditions make a view automatically updatable in PostgreSQL?

**Answer:**
Exactly one base table (or one updatable view) in `FROM`, no `GROUP BY`/`HAVING`/`DISTINCT`/`UNION`, no `LIMIT`/`OFFSET`, no set-returning function in the target list — verified directly: a single-table view accepted a real `UPDATE`, while a `JOIN`+`GROUP BY` view produced a real, specific refusal.

**Why it matters:**
This is a mechanical rule, not a guideline — knowing it precisely is a Senior-level signal.

**Common trap:**
Assuming any view can be written through, or assuming none can.

**Related:**
[Views and Materialized Views](../syllabus/06-databases/views-and-materialized-views.md)

## Card: The real materialized-view speedup

**Prompt:**
This chapter's lab measured a specific live-query-versus-materialized-view speedup. What was it, and what did it cost?

**Answer:**
~457× faster reads (55.735ms live vs. 0.122ms materialized), at the cost of ~50ms per refresh and staleness between refreshes.

**Why it matters:**
Grounds "materialized views are faster" in a real, honest number with a real, honest cost attached.

**Common trap:**
Citing the speedup without mentioning the refresh cost or the staleness trade-off.

**Related:**
[Views and Materialized Views](../syllabus/06-databases/views-and-materialized-views.md)

## Card: Why concurrent refresh needs a unique index

**Prompt:**
Why does `REFRESH MATERIALIZED VIEW CONCURRENTLY` require a unique index on the materialized view?

**Answer:**
It builds the new result separately, then diffs it row-by-row against the old result to apply only the changes — that diff needs a unique way to match old rows to new rows, which a unique index provides. Verified directly: a real, specific error without one.

**Why it matters:**
Explains *why* the requirement exists, rather than just knowing it's required.

**Common trap:**
Treating the requirement as an arbitrary PostgreSQL rule instead of a real mechanical necessity.

**Related:**
[Views and Materialized Views](../syllabus/06-databases/views-and-materialized-views.md)

## Card: PK vs. FK vs. Index vs. View, in one line each

**Prompt:**
State, in one line each, what a primary key, a foreign key, an index, and a view each guarantee.

**Answer:**
Primary key: this row is uniquely identifiable. Foreign key: this reference points at something real. Index: finding matching rows is fast. View: a reusable, possibly access-controlled or precomputed way to read the result of combining the other three.

**Why it matters:**
The four are routinely conflated as "database things that make queries work."

**Common trap:**
Believing a view enforces any of the other three's guarantees on its own — it doesn't.

**Related:**
[Views and Materialized Views](../syllabus/06-databases/views-and-materialized-views.md)
