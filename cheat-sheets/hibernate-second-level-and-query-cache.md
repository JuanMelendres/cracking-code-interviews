---
title: "Cheat Sheet: Hibernate Second-Level and Query Cache"
slug: hibernate-second-level-and-query-cache
document_type: cheat-sheet
domain: databases
topic_id: T-603
canonical: ../handbook/databases/hibernate-second-level-and-query-cache.md
last_updated: 2026-09-01
---

# Hibernate Second-Level and Query Cache

**Canonical chapter:** [`syllabus/06-databases/hibernate-second-level-and-query-cache.md`](../syllabus/06-databases/hibernate-second-level-and-query-cache.md)

## Core Mental Model

The persistence context (L1) is a diary for one conversation — everything it remembers is forgotten the moment that session ends. The second-level cache is a shared notice board everyone can read from and write to, persisting across many separate sessions — which means its biggest strength (surviving past any one session) is also its biggest risk (nobody posting to that board automatically hears about changes made somewhere else entirely). The query cache is a separate board that only posts "which IDs matched this question last time" — useful only if whoever reads it can still look up the actual current details, which is what the entity cache is for.

## Essential Definitions

- **Second-level cache (L2)** — `SessionFactory`-scoped, shared across every session for the application's lifetime, backed by a pluggable provider (Ehcache via JCache).
- **Query cache** — caches a specific query's matching entity IDs plus a timestamp, not the entity data itself; needs the entity cache alongside it to be useful.
- **Cache concurrency strategies** — `READ_ONLY` (fastest, only valid for genuinely immutable data), `READ_WRITE` (soft locks against dirty reads), `NONSTRICT_READ_WRITE` (accepts a brief staleness window), `TRANSACTIONAL` (needs a JTA-integrated provider).
- **Hibernate defends its own writes, and only those** — DML through Hibernate's own API (including native SQL via `Session`) proactively evicts the cache; a write through any other connection/process does not.

## Decision Table

| Question | If yes, lean toward |
|---|---|
| Is this entity read far more often than written, and is some staleness acceptable? | Enable L2 with `READ_WRITE` or `NONSTRICT_READ_WRITE` |
| Is the entity genuinely immutable once created? | `READ_ONLY` — fastest, safe only because nothing changes |
| Does more than one service/process write to this entity's table? | Design explicit cross-process cache eviction before enabling L2 |
| Is a specific query run frequently with the same parameters, rarely-changing results? | Enable the query cache alongside (not instead of) the entity cache |

**Trade-offs:**

| Cache | Scope | What it stores |
|---|---|---|
| First-level (L1) | Per session/transaction | Managed entity instances |
| Second-level (L2) | `SessionFactory`-wide, cross-session | Entity state |
| Query cache | `SessionFactory`-wide, cross-session | Matching entity IDs + timestamp |

## Key Numbers (real, executed with Hibernate ORM + Ehcache 3)

Cross-session L2 hit:

```
=== First load, session A: real DB hit expected ===
Real L2 cache misses: 1, hits: 0

=== Second load, session B (a DIFFERENT session) ===
Real L2 cache misses: 1 (still), hits: 1 -- served from L2, no SQL for session B
```

Corrected stale-cache reproduction — Hibernate's own native SQL was NOT reproducible this way; a separate JDBC connection was:

```
=== A write through a REAL, completely separate JDBC connection ===
Real row updated -- Hibernate's L2 cache was never told.

=== A NEW Hibernate session loads the same entity ===
Real loaded stock: 100 (real row value is 5; stale L2 value would be 100)
```

## Common Pitfalls

- Enabling the query cache without also enabling the entity cache, then being surprised the real-world benefit is smaller than expected.
- Assuming any write through Hibernate — including native SQL via `Session.createNativeQuery(...)` — bypasses cache invalidation; Hibernate actually defends against exactly that case.
- Choosing `READ_ONLY` for an entity that does get updated somewhere in the codebase, silently serving stale data forever.
- Enabling L2 blanket-wide "for performance" without checking each entity's actual read/write ratio.

## Interview Answer Skeleton

**30-sec:** L2 is shared across sessions (unlike per-session L1), reducing repeated DB hits for rarely-changing entities. The query cache separately caches matching IDs, still needing the entity cache to hydrate data. Hibernate defends its own cache against writes it executes itself, even native SQL — but a write through any other connection leaves it silently stale.

**2-min:** Add the real cross-session hit proof (zero SQL for session B) and the honest, corrected investigation: an initial assumption that native SQL would break the cache failed to reproduce (Hibernate evicts proactively); the genuine gotcha only appeared with a completely separate JDBC connection bypassing the `SessionFactory` entirely.

**Whiteboard:** Draw per-session L1 boxes (torn down when the session ends) all pointing into one shared "L2 — SessionFactory-wide" box. A "Query Cache" box arrows sideways into L2, labeled "still needs this for actual data." Two arrows into the database: one labeled "Hibernate's own writes — defended," a separate one labeled "another connection/process — NOT defended," with no line to the L2 box at all.

**Staff-level framing:** Design explicit cross-process cache-invalidation strategy for a genuinely multi-writer table, and discuss the organizational discipline (a review checklist, an explicit eviction-integration contract) needed to prevent a new writer from silently reintroducing the staleness bug.

## Production Warning Signs

- Customers intermittently seeing "in stock" for items actually out of stock, hours after a nightly batch job runs — a legacy batch job writing directly to a cached table via its own JDBC pool, invisible to the owning service's L2 cache.
- A query cache seemingly "not working" — check whether the entity cache is also enabled; a hit alone still needs somewhere to hydrate entity state from.
- `READ_ONLY` strategy used for an entity that does, in fact, get updated — Hibernate won't detect or prevent this; it serves stale data indefinitely.

## Related

- `syllabus/06-databases/jpa-entity-lifecycle-and-the-n1-problem.md`
- `syllabus/05-spring/spring-cache-abstraction-and-pitfalls.md`
- `syllabus/06-databases/optimistic-vs-pessimistic-locking.md`
