---
title: "Silently Unbatched Inserts from IDENTITY-Generated Keys"
document_type: production-cookbook-entry
domain: databases
status: draft
last_updated: 2026-09-02
related_handbook:
  - ../handbook/databases/hibernate-flush-modes-and-batch-writes.md
source: handbook/databases/hibernate-flush-modes-and-batch-writes.md#production-scenarios
---

# Silently Unbatched Inserts from IDENTITY-Generated Keys

## Context

A nightly batch job loads 500,000 rows via Hibernate, with `hibernate.jdbc.batch_size=50` set in the application's configuration.

## Symptoms

The job takes over an hour, despite the batch-size configuration being present.

## Impact

A bulk-load job runs far longer than expected, consuming resources and delaying whatever downstream process depends on its completion, with the configured optimization apparently having no effect.

## Initial Hypotheses

- The database itself is the bottleneck.
- The batch job's business logic between inserts is slow.
- The batch-size setting isn't being picked up at all due to a configuration-loading bug.

## Evidence

Enabling Hibernate's `Statistics` and re-running a small sample showed "0 JDBC batches" and one `executeUpdate()`-shaped round trip per row, despite the setting being confirmed present and correctly loaded.

## Investigation Timeline

1. **Nightly batch job's runtime flagged** as far exceeding expectations despite `hibernate.jdbc.batch_size=50` being configured.
2. **Configuration loading verified** — the setting is confirmed present and correctly loaded by the application at startup, ruling out a configuration-loading bug.
3. **Business logic between inserts reviewed** and found to be lightweight, not accounting for the scale of the runtime overage on its own.
4. **Hibernate `Statistics` enabled** and the job re-run against a small sample, revealing "0 JDBC batches" logged for the insert workload.
5. **JDBC call pattern confirmed directly** — one `executeUpdate()`-shaped round trip occurs per row rather than any grouped `executeBatch()` calls, despite the batch-size setting being active and correctly configured.
6. **Identifier generation strategy inspected**, revealing the entity being bulk-inserted uses `GenerationType.IDENTITY` for its primary key.

## Root Cause

The entity being bulk-inserted used `GenerationType.IDENTITY` for its primary key — a strategy structurally incompatible with statement batching, since Hibernate cannot batch an insert whose generated identifier it needs back immediately (the database assigns the key at insert time, so Hibernate can only learn it by executing that specific insert and reading the result back, before any grouping into a batch could occur). The batch-size setting was valid and loaded correctly; it simply never applied to this entity's identifier strategy.

## Immediate Mitigation

None available without a schema-level change — the incompatibility is structural, not configuration.

## Permanent Fix

Migrated the entity's identifier generation from `IDENTITY` to a database sequence (`GenerationType.SEQUENCE`), re-verified via the same `Statistics` output that real batching now occurred, and measured a substantial reduction in total round trips for the same 500,000-row load.

## Alternatives Considered

Not documented as a distinct alternative beyond the sequence migration itself — the incompatibility between `IDENTITY` and batching is structural, so any fix necessarily involves changing the identifier-generation strategy to one (`SEQUENCE` or `TABLE`) that can pre-allocate identifier values before an insert executes.

## Trade-offs

A sequence-based strategy requires the underlying database to support sequences (PostgreSQL and most enterprise databases do; some MySQL configurations historically did not, though modern MySQL 8+ does) and changes the specific SQL Hibernate generates for identifier retrieval — a schema migration, not a configuration-only fix.

## Prevention

Treat identifier-generation strategy as a decision made with bulk-insert performance in mind from the start for any entity expected to be written in large volumes, rather than defaulting to `IDENTITY` for its simplicity and discovering the batching incompatibility only under real load.

## Monitoring and Alerts

- Enable Hibernate `Statistics` (or an equivalent JDBC-call-counting mechanism) as a standing, always-on instrumentation for any bulk-write batch job, so "0 JDBC batches" is visible on a dashboard rather than requiring a one-off diagnostic run to discover during an incident.
- Add a pre-deployment check (or a design-review checklist item) for any newly-introduced entity expected to be bulk-inserted, explicitly confirming its identifier-generation strategy supports batching before the entity goes into production use at volume.
- Track batch-job runtime against row-count as a standing trend metric; a runtime that scales linearly with row count at a per-row cost matching individual `executeUpdate()` round trips (rather than the expected reduced per-batch cost) is a direct, early signature of this exact unbatched-insert pattern.

## Interview Story

Present it as a representative scenario to adapt, not a claimed personal history.

- **Situation:** a nightly batch job loading 500,000 rows via Hibernate took over an hour despite `hibernate.jdbc.batch_size` being explicitly configured.
- **Task:** determine why a documented, correctly-loaded performance setting appeared to have no effect at all.
- **Action:** ruled out a configuration-loading bug and slow business logic, then enabled Hibernate's `Statistics` and found zero JDBC batches were ever executed, tracing the cause to the entity's `IDENTITY` key-generation strategy being structurally incompatible with batching.
- **Result:** migrated the entity to `GenerationType.SEQUENCE`, re-verified real batching was occurring via the same `Statistics` output, and measured a substantial reduction in total round trips for the identical bulk load.

## Staff-Level Discussion

"We set `batch_size` and saw zero improvement" is a diagnosable, specific story precisely because the actual cause is a structural incompatibility rather than a misconfiguration — no amount of re-checking or re-applying the batch-size setting would ever have fixed it, because the identifier-generation strategy decides, before any insert runs, whether batching is even possible. This is a useful case for illustrating a broader principle to less experienced engineers: a configuration setting having "no effect" is meaningfully different from a configuration setting being *wrong*, and the correct next step when a documented optimization appears inert is to verify what's actually happening at the mechanism level (here, via Hibernate's own `Statistics`) rather than re-checking the configuration's own correctness repeatedly. At the design-review level, a Staff engineer should treat identifier-generation strategy as a decision with real, load-bearing performance consequences that deserves the same scrutiny as index design — `IDENTITY`'s simplicity (no separate sequence object, trivial to reason about) is a real advantage for low-volume entities, but it is actively the wrong default for any entity a team already knows will be bulk-inserted at scale, and that knowledge is available at design time, not only after a production batch job's runtime forces the question.

## Related Handbook Chapters

- [Hibernate Flush Modes and Batch Writes](../handbook/databases/hibernate-flush-modes-and-batch-writes.md) — canonical flush-mode and JDBC-batching mechanics, including the measured `IDENTITY`-versus-`SEQUENCE` batching evidence this incident traces back to.
- [Connection Pooling and Sizing (HikariCP)](../handbook/databases/connection-pooling-and-sizing.md) — related database-throughput concern relevant to a bulk-write workload's actual round-trip and connection-hold cost.
