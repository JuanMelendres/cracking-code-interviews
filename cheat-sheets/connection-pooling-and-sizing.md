---
title: "Cheat Sheet: Connection Pooling and Sizing (HikariCP)"
slug: connection-pooling-and-sizing
document_type: cheat-sheet
domain: databases
topic_id: T-607
canonical: ../handbook/databases/connection-pooling-and-sizing.md
last_updated: 2026-09-02
---

# Connection Pooling and Sizing (HikariCP)

**Canonical chapter:** [`handbook/databases/connection-pooling-and-sizing.md`](../handbook/databases/connection-pooling-and-sizing.md)

## Core Mental Model

A connection pool amortizes the real cost of opening a database connection (TCP handshake, auth, session setup) by keeping a set of already-open connections ready to reuse. Pool size is not a "more is safer" dial — it's a finite resource that should match the database's actual concurrent execution capacity, not the application's thread count.

## Essential Definitions

- **Pool exhaustion** — every connection is in use; a new request waits up to `connectionTimeout`, then throws a real, typed `SQLTransientConnectionException` with the pool's exact state embedded.
- **Leak detection** (`leakDetectionThreshold`) — catches a connection borrowed and never returned, with a real stack trace at the acquisition site; HikariCP enforces a real minimum of 2000ms — anything lower silently disables it.
- **Bigger-is-not-faster** — sizing beyond the database's real concurrent execution capacity creates contention for finite backend resources, measurably degrading throughput, not just plateauing.

## Decision Table

| Question | Answer |
|---|---|
| Database's own CPU/IO already near saturation? | Fix query performance, don't grow the pool |
| `active` consistently near `maximumPoolSize` with low `waiting`? | Pool is appropriately sized for current load |
| `waiting` consistently non-zero and database utilization is low? | Pool may genuinely be undersized — growing it can help |
| Active connections climbing steadily with no load increase? | Suspect a leak — check `leakDetectionThreshold` |

## Key Numbers

- Real PostgreSQL container capped at 2 CPUs, 40 queries: pool size 2 → 2902ms (72.6ms/query); size 4 → 3226ms; size 8 → 5587ms; size 16 → 6161ms (154.0ms/query) — pool size 2 was fastest; size 16 was more than 2x slower.
- Real exhaustion: `SQLTransientConnectionException after 509ms — "Connection is not available, request timed out after 505ms (total=2, active=2, idle=0, waiting=0)"`.
- HikariCP's real, enforced `leakDetectionThreshold` minimum: 2000ms.

## Common Pitfalls

- Increasing `maximumPoolSize` as the default fix for timeouts without checking database utilization first — the named misconception this chapter disproves directly.
- Assuming pool exhaustion means the database is down, rather than reading the real, specific exception.
- Setting `leakDetectionThreshold` below the real 2000ms minimum and not noticing it was silently disabled.
- Sizing a pool from application thread count rather than the database's actual concurrent execution capacity.

## Interview Answer Skeleton

**30-sec:** A connection pool reuses a fixed set of open connections. Exhaustion happens when every connection is busy and a new request waits past `connectionTimeout`, throwing a real, typed exception. A bigger pool isn't automatically better — sized beyond real database capacity, it measurably degrades throughput.

**2-min:** Add the real, decisive measurement: a pool sized to exactly match a CPU-capped database's real capacity (size 2) outperformed a pool 8x larger (size 16) by more than 2x on identical, genuinely CPU-bound work — because the oversized pool let more queries compete for the same fixed CPU capacity simultaneously.

**Whiteboard:** A small box labeled "Pool (size N)" with N connection icons, a queue of waiting requests outside it. An arrow from the database showing a fixed, small number of "CPU lanes" it can actually execute concurrently — fewer lanes than N. "Growing the pool doesn't grow the lanes; it just lets more requests fight over the same lanes."

**Staff-level framing:** Connect pool sizing to real database capacity measurement as an ongoing operational discipline, not a one-time config choice — check database-side metrics (CPU, active queries, wait events) before any pool-size change, given how counter-intuitive the degradation-from-oversizing result is.

## Production Warning Signs

- A team doubles `maximumPoolSize` from 10 to 20 in response to timeouts, and overall latency gets worse — the database's CPU was already near saturation, so more concurrent queries competed for the same fixed capacity. Fix: revert the pool size, profile and fix the underlying slow queries.

## Related

- `handbook/databases/mvcc-vacuum-and-bloat.md`
- `handbook/databases/isolation-levels-and-concurrency-anomalies.md`
- `handbook/spring/transactional-proxy-mechanics-and-propagation.md`
