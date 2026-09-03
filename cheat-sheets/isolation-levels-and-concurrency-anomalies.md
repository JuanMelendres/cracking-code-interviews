---
title: "Cheat Sheet: Isolation Levels and Concurrency Anomalies"
slug: isolation-levels-and-concurrency-anomalies
document_type: cheat-sheet
domain: databases
topic_id: T-611
canonical: ../handbook/databases/isolation-levels-and-concurrency-anomalies.md
last_updated: 2026-08-03
---

# Isolation Levels and Concurrency Anomalies

**Canonical chapter:** [`syllabus/06-databases/isolation-levels-and-concurrency-anomalies.md`](../syllabus/06-databases/isolation-levels-and-concurrency-anomalies.md)

## Core Mental Model

An isolation level is a promise about what one transaction is allowed to see of another's concurrent work — every anomaly is just a specific way of breaking a promise you didn't actually need. READ COMMITTED says "no, mostly not"; REPEATABLE READ says "not for what I read, but my writes can still interact with concurrent ones in cross-row ways I won't be told about"; SERIALIZABLE says "behave as if this ran alone, or tell me so I can retry."

## Essential Definitions

- **Dirty read** — reading another transaction's *uncommitted* write. Never occurs in PostgreSQL at any level, including its `READ UNCOMMITTED` setting.
- **Non-repeatable read** — the same row, re-read within one transaction, returns a different value because another transaction committed in between.
- **Lost update** — a *same-row* conflict: two transactions read the same row, compute new values, second write silently overwrites the first's intent. PostgreSQL prevents this at READ COMMITTED for an atomic `UPDATE` — but not for app-level read-then-conditional-write.
- **Write skew** — a *cross-row* invariant violation: two transactions each read shared multi-row state and each write a *different* row; neither write conflicts individually, but the combination violates a cross-row invariant. Prevented only by SERIALIZABLE.

## Decision Table

| Level | Prevents | Still allows | Mechanism |
|---|---|---|---|
| READ COMMITTED (PG default) | Dirty reads | Non-repeatable reads, write skew, phantoms | Each *statement* sees a fresh snapshot |
| REPEATABLE READ | Non-repeatable reads | Write skew | One snapshot for the *whole transaction* |
| SERIALIZABLE | Write skew + all weaker anomalies | Nothing | REPEATABLE READ's snapshot + SSI runtime dependency tracking |

**Situation → fix:**

| Situation | What to reach for |
|---|---|
| Simple same-row read-modify-write, one statement | Atomic `UPDATE ... SET x = x - ? WHERE ...` |
| Same-row read-then-write, can't collapse | `SELECT ... FOR UPDATE` |
| Cross-row invariant (shared read, different rows written) | SERIALIZABLE, with mandatory retry-on-serialization-failure everywhere |
| Reporting/read-only, no write-path invariant at risk | READ COMMITTED is fine |
| Deciding whether to escalate at all | Ask: same-row (cheap fix) or cross-row (needs SERIALIZABLE)? |

## Key Numbers (real, executed — PostgreSQL 16, two genuinely concurrent `psql` sessions)

**At REPEATABLE READ (write skew occurs):** Alice reads count=2, updates herself off-call, commits (succeeds). Bob reads count=2, updates himself off-call, commits (succeeds). **Final: zero doctors on call — invariant violated.**

**At SERIALIZABLE (identical code, prevented):** Alice's commit **FAILS**: `ERROR: could not serialize access due to read/write dependencies... Reason code: Canceled on identification as a pivot.` Bob's commit succeeds. **Final: one doctor remains on call.**

All variants are O(1) database operations — the distinction is entirely correctness under concurrency, not algorithmic cost.

## Common Pitfalls

- Conflating write skew with a lost update (different scopes: cross-row vs. same-row, different fixes)
- Assuming READ COMMITTED always risks lost updates — PostgreSQL's atomic `UPDATE` prevents this for the common case
- Choosing SERIALIZABLE everywhere without adding retry logic to every code path that touches it
- Believing REPEATABLE READ prevents write skew because it prevents non-repeatable reads/lost updates — it does not

## Interview Answer Skeleton

**30-sec:** Isolation levels control visibility of concurrent transactions' work. Each PostgreSQL level prevents one more anomaly than the last. The thing nearly everyone misses: REPEATABLE READ prevents same-row lost updates but **not** write skew (cross-row invariant violation from two transactions each writing a different row based on a shared read).

**2-min:** Add how each level works mechanically (fresh-statement snapshot / whole-transaction snapshot / SSI dependency tracking) + the trade-off (SERIALIZABLE only real if every code path retries) + the on-call-doctors reproduction as evidence.

**Whiteboard:** Draw the READ COMMITTED → REPEATABLE READ → SERIALIZABLE ladder (each arrow removes one anomaly). Then two columns "Alice"/"Bob," each with a SELECT reading the same shared value, branching to separate UPDATEs on different rows, converging into one "invariant" box marked with an X — narrate that neither individual read-then-write is wrong, only their combination.

**Staff-level signal:** naming `SELECT ... FOR UPDATE` unprompted as the cheaper fix for the common same-row case, before reaching for SERIALIZABLE.

## Production Warning Signs

- Transactions intermittently failing with "could not serialize access" — expected SERIALIZABLE/SSI behavior, not a bug to suppress (but *far more often than expected* signals high contention worth reconsidering scope)
- A long-running transaction under REPEATABLE READ/SERIALIZABLE holds its snapshot open, preventing vacuum from reclaiming dead row versions — table/index bloat beyond what the workload alone explains
- **Real incident:** intermittent double-refunds — two refund requests seconds apart both succeeded, refunding twice, caught only in a monthly audit, never an error log. Root cause: refund-eligibility `SELECT` executed separately from the `UPDATE`, no row lock, at default READ COMMITTED. Fixed with `SELECT ... FOR UPDATE` plus a unique constraint — a same-row fix, not a write-skew case, deliberately chosen to show the two anomaly classes need different fixes.

## Related

- `syllabus/06-databases/index-structures-btree-composite-covering.md`
- `syllabus/06-databases/query-planning-and-explain-analyze.md`
- [Spring Transactional Proxy Mechanics and Propagation](transactional-proxy-mechanics-and-propagation.md)
