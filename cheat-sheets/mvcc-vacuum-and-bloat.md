---
title: "Cheat Sheet: MVCC, Vacuum, and Bloat"
slug: mvcc-vacuum-and-bloat
document_type: cheat-sheet
domain: databases
topic_id: T-612
canonical: ../handbook/databases/mvcc-vacuum-and-bloat.md
last_updated: 2026-09-02
---

# MVCC, Vacuum, and Bloat

**Canonical chapter:** [`syllabus/06-databases/mvcc-vacuum-and-bloat.md`](../syllabus/06-databases/mvcc-vacuum-and-bloat.md)

## Core Mental Model

PostgreSQL never overwrites a row in place. Every UPDATE (and DELETE) creates a new physical tuple version and marks the old one dead — invisible to transactions whose snapshot started after the change, but still physically present until VACUUM removes it. Bloat is what happens when tuples die faster than VACUUM removes them; VACUUM's job is reclaiming dead tuples for reuse within the existing file, not shrinking it; and a long-running transaction blocks reclamation because VACUUM can never remove a version some still-open snapshot might still need to see.

## Essential Definitions

- **MVCC** — PostgreSQL keeps multiple physical row versions so readers and writers never block each other; each transaction sees a consistent snapshot as of a point in time.
- **VACUUM** — reclaims dead tuple versions, marking their space reusable by future INSERTs/UPDATEs; does not shrink the file.
- **VACUUM FULL** — rewrites the table into a new, compact file; does shrink it, at the cost of a full `ACCESS EXCLUSIVE` lock for the entire duration.
- **Bloat** — dead tuples (and their disk space) accumulating faster than VACUUM reclaims them.
- **Open snapshot as a global constraint** — any transaction's still-open snapshot can block VACUUM from reclaiming dead tuples database-wide, regardless of which tables that transaction actually queries.

## Decision Table

| Question | If yes, lean toward |
|---|---|
| Write pattern is steady, autovacuum keeps up | Plain (auto)VACUUM is sufficient |
| Table already bloated before the write pattern was fixed | VACUUM FULL or `pg_repack`, planned during low-traffic hours |
| Long-running transactions (BI tools, batch jobs, ORM sessions) held open | Audit and bound their duration — they block vacuum database-wide |
| `n_dead_tup` climbing despite autovacuum appearing to run | Check `pg_stat_activity` for a long-held transaction first |

**Operation comparison:**

| Operation | Reclaims dead tuples? | Shrinks file? | Locking cost |
|---|---|---|---|
| Plain `VACUUM` | Yes | No | Minimal, non-blocking |
| `VACUUM FULL` | Yes | Yes | Full `ACCESS EXCLUSIVE` lock |
| `pg_repack` | Yes | Yes | Much lower — brief final lock |
| Doing nothing | No | No | None, bloat compounds |

## Key Numbers (real, executed on PostgreSQL 16)

- Table size before updates: 1776 kB. After 250,000 real UPDATEs: 10 MB (5.6x growth). After plain VACUUM: still 10 MB (unchanged — reused, not shrunk). After VACUUM FULL: back to 1776 kB.
- With a long `REPEATABLE READ` transaction held open: `0 removed, 150000 remain, 100000 are dead but not yet removable`. Identical VACUUM after that transaction commits: `100000 removed, 50000 remain, 0 are dead but not yet removable`.
- Real `heap_page_items` evidence: a dead tuple slot shows `t_xmin=735, t_xmax=736`; the live version shows `t_xmin=736, t_xmax=0`.

## Common Pitfalls

- Believing UPDATE modifies a row in place — disproved directly by real `ctid`/`xmin`/`xmax` evidence.
- Running plain VACUUM and expecting a bloated table's file size to shrink — it won't.
- Not connecting a long-running transaction to a completely unrelated table's bloat, because that transaction never appears in that table's own query logs.
- Reaching for VACUUM FULL as routine maintenance rather than a rare, planned, exclusive-lock-accepting remediation.
- Disabling autovacuum entirely "for performance" with no replacement strategy.

## Interview Answer Skeleton

**30-sec:** MVCC never updates a row in place — every UPDATE creates a new tuple version and marks the old dead. VACUUM reclaims dead tuples for reuse but doesn't shrink the file; VACUUM FULL does, at the cost of an exclusive lock. A long-running transaction can block vacuum from reclaiming dead tuples anywhere in the database, even on tables it never touches.

**2-min:** Add the measured before/after sizes (1776 kB → 10 MB → still 10 MB after VACUUM → 1776 kB after VACUUM FULL) and the real long-transaction-blocks-vacuum evidence (100,000 dead tuples "not yet removable" while open, all removed the instant it commits).

**Whiteboard:** Draw a row, then a second box labeled "v2" after an UPDATE arrow with the first shaded gray as "dead (xmax=736)." Add a third box for a second UPDATE. Draw a "VACUUM" eraser wiping the gray boxes — then, separately, a dashed line labeled "Transaction L's open snapshot" spanning underneath all boxes, blocking the eraser until the line ends.

**Staff-level framing:** Connect this mechanism to isolation-level choice as a resource-retention decision, not just a correctness one. Treat transaction-age monitoring as a standing operational practice, and reason about the organizational discipline needed to keep long-running tools (BI dashboards, batch jobs) from silently becoming a database-wide vacuum blocker.

## Production Warning Signs

- A table's on-disk size grows steadily with no meaningful row-count change, and query latency degrades proportionally — check `pg_stat_user_tables` for `n_dead_tup` climbing despite autovacuum appearing to run.
- Cross-reference `pg_stat_activity` for a long-held transaction (`xact_start` far in the past) — a BI tool or dashboard session holding one long `REPEATABLE READ` transaction is a realistic, real cause even if it never queries the bloating table.
- Transaction ID wraparound risk if autovacuum falls permanently behind, sustained by exactly this long-transaction mechanism — PostgreSQL can eventually refuse writes entirely to protect against corruption.

## Related

- `syllabus/06-databases/isolation-levels-and-concurrency-anomalies.md`
- `syllabus/06-databases/locks-deadlocks-and-lock-escalation.md`
- `syllabus/06-databases/optimistic-vs-pessimistic-locking.md`
- `syllabus/09-messaging-event-driven/messaging-patterns-and-change-data-capture.md`
