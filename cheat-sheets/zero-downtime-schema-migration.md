---
title: "Cheat Sheet: Zero-Downtime Schema Migration"
slug: zero-downtime-schema-migration
document_type: cheat-sheet
domain: databases
topic_id: T-616
canonical: ../handbook/databases/zero-downtime-schema-migration.md
last_updated: 2026-08-04
---

# Zero-Downtime Schema Migration

**Canonical chapter:** [`syllabus/06-databases/zero-downtime-schema-migration.md`](../syllabus/06-databases/zero-downtime-schema-migration.md)

## Core Mental Model

Every zero-downtime migration technique answers one question: what needs to keep working while the migration is in flight, and for how long? A `CREATE INDEX` needs writes to keep working during a build that might take minutes to hours — `CONCURRENTLY` answers that. A column rename needs *both* old and new application code to keep working during a rolling deploy — expand-contract answers that. Neither problem is really about the schema change being slow; it's about what else is happening concurrently while it runs.

## Essential Definitions

- **Zero-downtime migration** — changing a live database's schema without blocking normal reads/writes for the duration, because a maintenance window is rarely an option.
- **Lock duration hazard** — a plain `CREATE INDEX` holds a `SHARE` lock on the table for its entire build; correct, but it blocks writes for however long the build takes, scaling with table size.
- **Expand-contract** — three phases: **Expand** (add the new column/structure alongside the old — both old and new code work unmodified); **Migrate** (backfill from the old column, dual-write to both during the transition — exactly the dual-write pattern from [Saga and Outbox](distributed-transactions-saga-and-outbox.md), applied to a single database); **Contract** (once all instances run the new code and both columns verified in sync, drop the old column).
- **Rename ≠ safe** — `ALTER TABLE ... RENAME COLUMN` is instant at the catalog level regardless of table size, but breaks any still-running old code referencing the old name immediately. "Fast" and "safe" are different properties.

## Decision Table

| Approach | Benefit | Cost |
|---|---|---|
| Plain `CREATE INDEX` | Simpler, single-pass, generally faster to complete | Blocks writes for the full duration (measured 1943ms here) |
| `CREATE INDEX CONCURRENTLY` | Doesn't block writes (measured 84ms here) | Slower overall, can fail and leave an invalid index, can't run inside a transaction block |
| Direct rename/retype | Simple, one statement | Breaks any code still running the old schema assumption mid-rollout |
| Expand-contract | Old and new code both work throughout | More steps, more total migration time; the migrate phase inherits its own atomicity hazard |

| Change | Safe approach |
|---|---|
| Add an index to a large, live table | `CREATE INDEX CONCURRENTLY` |
| Rename or retype a column | Expand → migrate (dual-write + backfill) → contract |
| Add a column with a default | Generally safe directly in modern Postgres (metadata-only default since PG 11) — verify for the type/version |
| Drop a column | Only after confirming zero code references it — the contract step, not a standalone operation |

## Key Numbers (real, measured — PostgreSQL 16, 2,000,000-row table)

```
Plain CREATE INDEX:            concurrent INSERT took 1943ms (blocked for the ~2s build)
CREATE INDEX CONCURRENTLY:     concurrent INSERT took 84ms
-> roughly 23x
```

## Common Pitfalls

- Assuming a maintenance window is available for schema changes
- Using plain `CREATE INDEX` on a large, actively-written table without considering `CONCURRENTLY`
- Performing a direct column rename/retype against a system with a rolling deploy, breaking whichever application version doesn't match the new schema during the transition window

## Interview Answer Skeleton

**30-sec:** `CREATE INDEX CONCURRENTLY` avoids blocking writes during index builds — measured 84ms vs. 1943ms of blocked `INSERT` for a plain build. Renames/retypes need expand-contract, not a direct rename, because a rolling deploy runs old and new application code concurrently.

**2-min:** Add why it exists + the 23x measured gap + the migrate phase's dual-write atomicity hazard (same as the outbox pattern, one database instead of two systems).

**Whiteboard:** Draw the expand → migrate → contract sequence; annotate the migrate phase: "this is where the crash-safety question lives."

**Staff-level framing:** the 23x measured gap between blocking and `CONCURRENTLY` index creation is a small, safe demonstration of a class of entirely self-inflicted production incident. A Staff engineer treats every schema change against a production-scale table as requiring an explicit answer to "what does this lock, and for how long, under real concurrent load" — and treats expand-contract as the default for any rename/retype against a rolling-deployed system, not a special case for unusually large tables.

## Production Warning Signs

- **Real incident pattern:** a direct `ALTER TABLE ... RENAME COLUMN` mid-rollout (roughly half the fleet on the old version) causes old-version instances to fail with column-does-not-exist errors — a rolling deploy that should have been zero-downtime causes a partial outage proportional to the fraction of the fleet still on old code, worse the slower the rollout.
- The schema changes atomically for all sessions instantly, but the application rollout is never atomic — that mismatch is the root cause, not a database bug.
- Prevention: a lint rule or migration-review checklist flagging direct `RENAME COLUMN`/type-changing `ALTER TABLE` against tables serving live traffic.

## Related

- [Distributed Transactions: Saga and Outbox](distributed-transactions-saga-and-outbox.md)
- [Table Partitioning and Sharding Strategies](table-partitioning-and-sharding-strategies.md)
- [Database Index Structures](index-structures-btree-composite-covering.md)
