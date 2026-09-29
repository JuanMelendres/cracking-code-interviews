---
title: "Cheat Sheet: Batch and Scheduled Job Design"
slug: batch-and-scheduled-job-design
document_type: cheat-sheet
domain: 11-system-design
topic_id: T-2443
canonical: ../syllabus/11-system-design/batch-and-scheduled-job-design.md
last_updated: 2026-09-29
---

# Batch and Scheduled Job Design

**Canonical chapter:** [`syllabus/11-system-design/batch-and-scheduled-job-design.md`](../syllabus/11-system-design/batch-and-scheduled-job-design.md)

## Core Mental Model

A long walk carrying a ledger, not a function call. It can be interrupted at any point; whether you continue from where you stopped depends entirely on whether you wrote your position down **durably, as you went**. And nobody is walking with you, so arriving is not the same as having been seen to arrive.

## The Four Decisions

| Decision | Bad default | What to do instead |
|---|---|---|
| **Who runs it** | Every instance | Lock with a lease, or an external scheduler |
| **How often it commits** | One transaction | Chunk sized from acceptable rework |
| **After a crash** | Start over | Resume from a checkpoint |
| **On a bad record** | Crash | Fail-fast *or* skip — deliberately |

A job that has not answered these has answered them anyway, by default.

## 1. Multi-Instance: the measured default

```
Unguarded, 3 instances, 1000 invoices -> 3000 side effects, 3 workers  (EVERY RECORD 3x)
Lock-guarded,  same input             -> 1000 side effects, 1 worker   (correct)
```

No error. No log. All three reported success. **Invisible until the second replica exists.**

```sql
UPDATE job_lock SET held_by = ?, locked_at = CURRENT_TIMESTAMP
 WHERE name = ? AND (held_by IS NULL OR locked_at < ?)   -- lease expiry
```

Returns 1 for exactly one instance, 0 for the rest. Atomic via row-level locking; no check-then-act window. **The lease is not optional** — without it, a dead instance blocks the job forever, which is quieter and worse than the duplication it fixed.

Alternative: move the schedule out (Kubernetes `CronJob`) so only one pod exists.

## 2. Chunk Size = Durability Budget

| chunk (rows/commit) | duration (10k rows) | worst-case rows lost |
|---|---|---|
| 1 | 148 ms | 0 |
| 100 | 30 ms | 99 |
| 10,000 | 16 ms | 9,999 |

**9.25x** across the range. Work at risk in a crash = exactly one chunk, by construction.

Ask "how much rework is acceptable?" (seconds/minutes), then size the chunk. Not "how fast can this go." Second cost: a chunk is a transaction, so the biggest chunk is often not the fastest on a busy database.

## 3. Checkpoint — one rule

```java
markCheckpoint(id);
connection.commit();   // work AND checkpoint commit TOGETHER
```

Separate commits = a dual write. Crash between them leaves the checkpoint **ahead** of the work (records silently skipped) or **behind** it (records reprocessed). A log file, memory, or another service all fail the same way.

```
No checkpoint : crash at 60% -> committed 0.   1599 units of work to commit 1000
With checkpoint: crash at 60% -> committed 500. 1099 units (99 wasted = the open chunk)
```

The single transaction is **not wrong** — it is wasteful and fragile. A 6-hour job failing in hour 5 redoes 5 hours.

**Better still:** a status column on the rows. Progress becomes a property of the data and cannot disagree with it.

```sql
UPDATE invoice SET processed = TRUE
 WHERE id IN (SELECT id FROM invoice WHERE processed = FALSE ORDER BY id LIMIT 100)
```

## 4. Restartable ≠ Idempotent

A checkpoint reduces how often a record is processed twice. Only idempotent processing makes it **safe** when it happens (crash between work and commit, stolen lease, manual rerun).

**The test:** run it twice against the same input — is the result identical to running it once?

## 5. Fail-Fast vs Skip

```
fail-fast : stopped at 500. processed=499 skipped=0 remaining=500  -> loud, work undone
skip      : completed.      processed=999 skipped=1 remaining=0    -> "succeeded"
```

Skip is only safe when **both**: the skip count is an **alerted metric**, and skipped records are **replayable**. Alert on the skip *rate* — the same code reports success whether 1 or 400 were skipped.

## Decision Table

| Situation | Choice |
|---|---|
| Runs < 1 minute | Start over on failure; a checkpoint is over-engineering |
| More than one replica | Lock + lease, or external scheduler |
| Rows can mark themselves | Status column, not a checkpoint table |
| Records interdependent | Fail fast |
| Records independent + alerting exists | Skip |
| One job, one table | Hand-rolled (~50 lines) |
| Many jobs, real ops requirements | Spring Batch (`JobRepository` is the value) |

## Common Pitfalls

- `synchronized` as a multi-instance guard — does nothing across JVMs.
- Lock with no expiry.
- Checkpoint in a separate transaction, a log file, or memory.
- Chunk size chosen for throughput.
- Skip with no alert on the count.
- Alerting only on failure, never on the job **not running**.
- First run at production volume happens in production.

## Interview Answer Skeleton

1. Four decisions: who runs it, commit interval, crash behaviour, bad-record policy.
2. Lead with the surprise: `@Scheduled` runs on **every** instance — 3x, measured, silent.
3. Fix: one conditional `UPDATE` + lease. Or move the schedule out.
4. Chunk size = durability budget, not tuning.
5. Checkpoint commits **with** the work.
6. Restartable ≠ idempotent; give the "run it twice" test.

## Production Warning Signs

- Duplicate downstream effects (emails, charges, ledger entries) after a replica-count change.
- A long job that has failed several nights with a different proximate cause each time.
- A job that logs only errors — indistinguishable from healthy and from never triggered.
- Nightly window creeping as data grows, because the job reprocesses everything.

## Related

- [Idempotency at System Edges](../syllabus/11-system-design/idempotency.md)
- [Hibernate Flush Modes and Batch Writes](../syllabus/06-databases/hibernate-flush-modes-and-batch-writes.md)
- [Zero-Downtime Schema Migration](../syllabus/06-databases/zero-downtime-schema-migration.md)
- [Consumer Lag, Backpressure, and DLQ Strategy](../syllabus/09-messaging-event-driven/consumer-lag-backpressure-and-dlq-strategy.md)
- [`practice/java/system-design/batch-job-design/`](../practice/java/system-design/batch-job-design/README.md)
