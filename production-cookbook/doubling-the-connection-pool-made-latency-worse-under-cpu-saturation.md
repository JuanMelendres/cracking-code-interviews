---
title: "Doubling the Connection Pool Made Latency Worse Under CPU Saturation"
document_type: production-cookbook-entry
domain: databases
status: draft
last_updated: 2026-09-02
related_handbook:
  - ../syllabus/06-databases/connection-pooling-and-sizing.md
source: handbook/databases/connection-pooling-and-sizing.md#production-scenarios
---

# Doubling the Connection Pool Made Latency Worse Under CPU Saturation

## Context

A reporting service began throwing `SQLTransientConnectionException` under moderate load. The on-call engineer's first response — the register's own named misconception — was to double `maximumPoolSize` from 10 to 20, expecting more headroom. This is presented as a representative scenario grounded directly in the chapter's own measured mechanism.

## Symptoms

Instead of improving, overall query latency got worse after the pool increase, and the timeout errors, while less frequent, were replaced by generally slower responses across the board.

## Impact

A configuration change intended to fix a timeout problem made the system slower overall, without resolving the underlying cause of the original timeouts.

## Initial Hypotheses

- The pool increase hadn't fully propagated — checked as an initial hypothesis before further investigation.
- The database needed more resources too — checked as an initial hypothesis before further investigation.
- The database's CPU capacity, not the pool size, was already the actual bottleneck — correct.

## Evidence

The database's own CPU utilization was already near saturation before the pool change; after doubling the pool, more queries were genuinely executing concurrently on the same fixed CPU capacity, producing real contention (context switching, lock contention on shared buffers) rather than real additional throughput — exactly the mechanism a real, measured pool-sizing benchmark against a CPU-capped database demonstrates directly (pool size 2, matching a 2-CPU cap exactly, was the fastest; pool size 16 was more than twice as slow).

## Investigation Timeline

1. **`SQLTransientConnectionException` reported** under moderate load, prompting an immediate response of doubling `maximumPoolSize` from 10 to 20.
2. **Overall query latency observed to worsen** after the pool increase, with timeout errors less frequent but general response times slower across the board.
3. **Pool-change propagation checked** and confirmed the new configuration had fully taken effect — ruling out a stale or partially-applied config as the explanation for the lack of improvement.
4. **Database resource needs considered**, prompting a direct check of database-side utilization rather than assuming the database could absorb arbitrary additional concurrency.
5. **Database CPU utilization measured directly** and found already near saturation before the pool change — confirming that increasing the pool let more queries compete for the same fixed CPU capacity simultaneously, producing contention rather than additional throughput.

## Root Cause

The pool had never been the actual bottleneck; the database's CPU capacity was, and increasing the pool size just let more queries compete for that same fixed capacity simultaneously, making each one slower — directly matching the pattern where pool size exceeding a database's real concurrent execution capacity measurably degrades throughput rather than merely plateauing.

## Immediate Mitigation

Reverted the pool size to 10.

## Permanent Fix

Profiled the reporting queries themselves — many were genuinely CPU-bound, unindexed table scans — and fixed the underlying query performance, which resolved the original timeout symptom without touching pool size at all.

## Alternatives Considered

Further increasing the pool size, or adding read replicas to spread the same unoptimized query load — not pursued, since the actual bottleneck (inefficient, CPU-bound queries) would have continued to consume disproportionate CPU regardless of how much additional pooled or distributed capacity was made available to run them concurrently.

## Trade-offs

Query optimization work took longer than the one-line pool-size config change, but addressed the actual bottleneck instead of masking it.

## Prevention

Any pool-sizing change now requires checking real database CPU/IO utilization first — "is the database actually idle waiting for more concurrent work, or already saturated" — before touching `maximumPoolSize`.

## Monitoring and Alerts

- Add database CPU/IO utilization as a required, co-displayed metric on the same dashboard as connection-pool `active`/`idle`/`waiting` counts, so an on-call engineer sees both signals together rather than reaching for the pool-size dial based on pool metrics alone.
- Alert specifically on the combination of "connection-pool waiting count elevated" AND "database CPU utilization already high," since this combination is the direct signature of the pool being correctly sized for demand but the database itself being the actual constraint — a materially different signal from "pool waiting count elevated, database CPU has headroom," which would genuinely suggest a pool-size increase.
- Add a standing query-performance audit (using the database's own slow-query log or `pg_stat_statements`-style tooling) so CPU-bound, unindexed queries are identified proactively during regular review, rather than only being discovered reactively after a pool-sizing incident forces a closer look.

## Interview Story

Present it as a representative scenario to adapt, not a claimed personal history.

- **Situation:** a reporting service began throwing connection-pool-exhaustion exceptions under moderate load, and the immediate response of doubling the pool size made overall latency worse instead of better.
- **Task:** figure out why adding more connection capacity — the seemingly obvious fix for a pool-exhaustion error — made the system slower rather than faster.
- **Action:** checked that the pool change had actually propagated, then measured the database's own CPU utilization directly and found it already near saturation, confirming the pool had never been the actual constraint.
- **Result:** reverted the pool size to its original value, then profiled and fixed the underlying CPU-bound, unindexed queries responsible for the original timeouts — resolving the real bottleneck without touching pool size at all.

## Staff-Level Discussion

This incident captures a genuinely common and costly reflex: "increase pool size" is the fastest, lowest-effort response available to an on-call engineer facing a pool-exhaustion exception, and it is very often wrong, because a connection pool can only ever be sized correctly relative to what the database can *execute* concurrently, not relative to how many callers want a connection. Doubling the pool when the database is already CPU-saturated doesn't add capacity — it adds contention, and the fact that the timeout errors became less frequent (because more requests than before now get a connection immediately) while overall latency got worse is a subtle, easy-to-misread signal that could be mistaken for partial success rather than a worsening problem. A Staff engineer's standing heuristic here should be to treat "increase the pool" as a hypothesis to verify against actual database-side utilization, never a default action — and to recognize that pool-exhaustion exceptions are a *symptom* whose root cause is just as likely to be on the query-performance side as the pool-configuration side. This also has an organizational dimension: an incident runbook that lists "increase maximumPoolSize" as a first-response action for pool-exhaustion errors is actively harmful without a preceding step to check whether the database is CPU/IO-saturated, and codifying that check as a mandatory gate (as the chapter's own prevention step does) converts a recurring, costly mistake into a one-time process fix.

## Related Handbook Chapters

- [Connection Pooling and Sizing (HikariCP)](../syllabus/06-databases/connection-pooling-and-sizing.md) — canonical pool-exhaustion mechanics and the measured pool-size-versus-throughput degradation this incident reproduces at production scale.
- [Hibernate Flush Modes and Batch Writes](../syllabus/06-databases/hibernate-flush-modes-and-batch-writes.md) — related database-interaction performance concern where the ORM layer, not the pool, is the actual throughput driver.
