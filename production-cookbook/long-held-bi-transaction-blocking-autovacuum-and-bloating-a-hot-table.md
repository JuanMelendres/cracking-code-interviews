---
title: "Table Bloat from a BI Tool's Long-Held Transaction Blocking Autovacuum"
document_type: production-cookbook-entry
domain: databases
status: draft
last_updated: 2026-09-02
related_handbook:
  - ../syllabus/06-databases/mvcc-vacuum-and-bloat.md
  - ../syllabus/06-databases/isolation-levels-and-concurrency-anomalies.md
source: handbook/databases/mvcc-vacuum-and-bloat.md#production-scenarios
---

# Table Bloat from a BI Tool's Long-Held Transaction Blocking Autovacuum

## Context

An `orders` table receives normal write traffic while a business-intelligence dashboard tool queries the database, opening a single `REPEATABLE READ` transaction per analyst dashboard session to guarantee a consistent view across multiple queries within that session.

## Symptoms

The `orders` table's on-disk size grows from 2GB to 4.3GB over seven days despite no meaningful change in row count, and query latency on that table degrades proportionally as more of each query's I/O goes to scanning dead tuples.

## Impact

A hot table more than doubled in physical size within a week with no corresponding growth in logical data, and read performance against it degraded as a direct consequence — a capacity and latency problem that would recur indefinitely if left unaddressed.

## Initial Hypotheses

An index had become inefficient, or statistics were stale.

## Evidence

`pg_stat_user_tables` showed `n_dead_tup` at over 40% of `n_live_tup` and climbing continuously despite autovacuum being enabled and apparently running (its last-run timestamp was recent). Cross-referencing `pg_stat_activity` for long-running transactions found the BI dashboard tool holding a single `REPEATABLE READ` transaction open for the entire duration each analyst's dashboard session was active — sometimes six or more hours.

## Investigation Timeline

1. Table size growth (2GB to 4.3GB over seven days) and degrading query latency on `orders` observed, with no meaningful change in row count.
2. Stale-statistics and inefficient-index hypotheses raised initially.
3. `pg_stat_user_tables` checked, showing `n_dead_tup` climbing continuously past 40% of `n_live_tup` despite autovacuum appearing to run recently.
4. `pg_stat_activity` cross-referenced for long-running transactions, surfacing the BI tool's `REPEATABLE READ` sessions held open for up to six or more hours.
5. Confirmed the mechanism directly against `long-transaction-blocks-vacuum-demo.sh` — a long-held open snapshot prevents autovacuum from reclaiming any dead tuple created during that snapshot's lifetime, even on tables the long transaction never touches.

## Root Cause

The dashboard's long-held `REPEATABLE READ` snapshot prevented autovacuum from reclaiming any dead tuple created by the `orders` table's normal write traffic for the entire session duration, even though the dashboard tool never touched `orders` directly.

## Immediate Mitigation

Identified and terminated the longest-idle dashboard sessions, allowing a backlog of accumulated dead tuples to finally be reclaimed by the next autovacuum run.

## Permanent Fix

Configured the BI tool to use a short-lived transaction per query instead of one long transaction per session, eliminating the open-snapshot problem entirely, and added a monitoring alert on transaction age (`now() - xact_start`) exceeding a threshold.

## Alternatives Considered

None recorded beyond reconfiguring the BI tool's transaction model — the scenario treats eliminating the long-held snapshot as the direct fix rather than working around autovacuum's blocking behavior another way.

## Trade-offs

The BI tool's cross-query consistency guarantee was weakened slightly — queries within one dashboard session could now, in principle, see different snapshots — judged acceptable for analytics use cases where perfect point-in-time consistency across dashboard widgets was not actually a hard requirement.

## Prevention

Transaction-age monitoring is now a standing alert, not something discovered reactively during a capacity investigation.

## Monitoring and Alerts

- Alert on `now() - xact_start` exceeding a defined threshold (the Permanent Fix's own monitoring addition) for any session, paging before a long-held snapshot has had time to accumulate meaningful bloat, rather than discovering it via table-size growth days later.
- Track `n_dead_tup / n_live_tup` per table as a standing metric (not just autovacuum's last-run timestamp, which can look healthy while still failing to reclaim anything meaningful) — a climbing ratio despite autovacuum "running" is the specific signal this incident's evidence relied on.
- Periodically audit `pg_stat_activity` for the longest-held transactions across all connected tools, not just the application's own connections — the responsible session in this incident belonged to a BI tool that never touched the bloating table directly, which is exactly the kind of connection an application-only monitoring view would miss.

## Interview Story

This maps directly to a "diagnose unexplained table bloat" question. Present it as a representative scenario to adapt, not a claimed personal history:

- **Situation:** a hot `orders` table's on-disk size more than doubled over a week with no corresponding row-count growth, and query latency degraded as a result.
- **Task:** find the cause even though autovacuum appeared to be enabled and running normally.
- **Action:** checked `pg_stat_user_tables` and found dead-tuple ratio climbing despite recent autovacuum runs; cross-referenced `pg_stat_activity` for long-running transactions and found a BI tool holding hours-long `REPEATABLE READ` sessions that never touched the bloating table directly.
- **Result:** terminated the long-idle sessions to let the backlog reclaim, then reconfigured the BI tool to use short-lived per-query transactions and added transaction-age alerting.

## Staff-Level Discussion

The counterintuitive core of this incident — a tool that never wrote to, or even read from, the bloating table was still the actual cause — is exactly the kind of cross-system coupling that a per-service or per-table monitoring view will not surface on its own. MVCC's snapshot mechanism ties every table's dead-tuple reclamation to the oldest open transaction anywhere on the cluster, which means a connection pattern in one tool (a BI dashboard's long session) can silently degrade a completely unrelated table's operational health. A Staff engineer reviewing this after the fact should push the organization toward transaction-age monitoring as infrastructure-wide policy, not a fix scoped to the one BI tool that happened to be caught this time, because any other long-lived connection — a forgotten debugging session, a poorly configured connection pool, a batch job holding a transaction open across an external call — reproduces the identical mechanism against a different table next time.

## Related Handbook Chapters

- [MVCC, VACUUM, and Bloat](../syllabus/06-databases/mvcc-vacuum-and-bloat.md) — canonical mechanics of how a long-held snapshot blocks vacuum's ability to reclaim dead tuples.
- [Isolation Levels and Concurrency Anomalies](../syllabus/06-databases/isolation-levels-and-concurrency-anomalies.md) — the `REPEATABLE READ` snapshot semantics this incident's root cause depends on.
