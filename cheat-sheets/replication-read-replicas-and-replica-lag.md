---
title: "Cheat Sheet: Replication, Read Replicas, and Replica Lag"
slug: replication-read-replicas-and-replica-lag
document_type: cheat-sheet
domain: databases
topic_id: T-615
canonical: ../handbook/databases/replication-read-replicas-and-replica-lag.md
last_updated: 2026-09-02
---

# Replication, Read Replicas, and Replica Lag

**Canonical chapter:** [`syllabus/06-databases/replication-read-replicas-and-replica-lag.md`](../syllabus/06-databases/replication-read-replicas-and-replica-lag.md)

## Core Mental Model

A read replica is not a live mirror — it's a follower re-executing a real, ordered log of changes (the WAL) some real, nonzero amount of time behind the leader, measured in real time, not "how many queries." Every replication gotcha traces back to this: a replica's data is a genuine snapshot of the primary's recent past, not its present. Replica lag is a real, physical quantity, never zero, even when small enough to usually not matter.

## Essential Definitions

- **Streaming replication** — PostgreSQL's standard mechanism: the primary ships its WAL to each connected replica, which replays it to stay in sync; a real, ongoing process, not a one-time copy.
- **Replica lag** — the real, measurable time delay between a change committing on the primary and becoming visible on a replica; genuinely nonzero under PostgreSQL's default asynchronous replication.
- **Read-your-own-writes risk** — because replication is asynchronous by default, a client writing to the primary and immediately reading from a replica can observe a version of the data before its own write.
- **Promotion (`pg_ctl promote`)** — a real, one-way transition ending standby mode; `pg_is_in_recovery()` flips `true` to `false`, after which the replica accepts writes as an independent primary.

## Decision Table

| Question | If yes, lean toward |
|---|---|
| Read must reflect the client's own immediately-preceding write | Route to the primary (or a synchronous replica) |
| Read can tolerate real, bounded staleness (public listing, activity feed) | Route to a replica |
| Write volume high enough that lag could grow under load | Monitor `pg_stat_replication`'s real lag columns continuously, not just once |
| Failover must preserve gap-free sequential IDs (invoice numbers) | Plan explicitly for sequence discontinuity — don't assume `SERIAL` survives failover gap-free |

**Trade-offs:**

| Choice | Benefit | Cost |
|---|---|---|
| Asynchronous replication (default) | No write-latency cost from replica round-trips | Real, load-dependent lag; read-your-own-writes risk |
| Synchronous replication | Eliminates staleness window for that replica | Added write latency on every commit; reduced availability |
| Reading from a replica | Real read-scaling benefit | Real risk of stale reads |
| Reading from the primary | No staleness risk | No read-scaling benefit; adds load back to primary |

## Key Numbers (real, executed on PostgreSQL 16 via Docker)

- `pg_stat_replication`: `write_lag=00:00:00.000063 flush_lag=00:00:00.000221 replay_lag=00:00:00.0003` — genuinely sub-millisecond on a local network.
- Naive application-level polling (fresh connection per check): ~174ms until a new row was observed — dominated by connection/query overhead, not WAL streaming.
- Replica rejects direct writes: `ERROR: cannot execute INSERT in a read-only transaction`.
- Real promotion: an inserted row's `id` jumped from an expected next value to `35` (observed jumping from `3`) — sequence caching/reservation state not guaranteed to survive promotion gap-free.

## Common Pitfalls

- Assuming a read replica is always "close enough to instant" to be safe for any read, without distinguishing read-your-own-writes-sensitive paths from tolerant ones.
- Conflating naive application-level polling latency with actual WAL-streaming replication lag — they can differ by orders of magnitude.
- Assuming `SERIAL`/sequence values remain perfectly gap-free across a promotion.
- Reaching for synchronous replication globally "to be safe," paying real write-latency cost everywhere instead of scoping the fix.

## Interview Answer Skeleton

**30-sec:** Streaming replication ships the primary's WAL to replicas, which replay it — asynchronous by default, so real, measurable replica lag exists. This creates a genuine read-your-own-writes risk: a client can write to the primary and immediately read stale data from a replica. Promotion turns a replica into an independent writable primary, but sequence values aren't guaranteed to survive it gap-free.

**2-min:** Add the dual lag measurement (sub-millisecond WAL-level vs. ~174ms naive polling) and the production example: users not seeing their own just-created order on a replica-served confirmation page, fixed by routing that specific read to the primary.

**Whiteboard:** Draw the sequence diagram — client writes to primary, WAL record generated, streamed to replica with a real nonzero delay, client reads from replica during that window (real risk of stale result), then WAL applied. Annotate: "this delay is real and measured, not theoretical."

**Staff-level framing:** Generalize read-your-own-writes to any eventually-consistent read-scaling layer (caches, CDC-fed search indexes) — the fix is always the same shape: classify which reads need strong consistency and route only those to the authoritative source. Plan explicitly for secondary consequences of failover (sequence discontinuity) that only surface by actually exercising it.

## Production Warning Signs

- A user doesn't see their own just-created data on the next page load — read-your-own-writes gap from asynchronous replica lag; route that specific read to the primary.
- Replica lag measured differently by different tools/methods — naive polling overhead conflated with real WAL-streaming lag; use `pg_stat_replication`'s own columns for the authoritative figure.
- An auto-incrementing ID has unexpected gaps after a failover — sequence caching/reservation state not surviving promotion gap-free; design gap-sensitive ID systems around this explicitly.

## Related

- `syllabus/10-distributed-systems/cap-theorem-and-consistency-models.md`
- `syllabus/17-architecture/cqrs-read-write-separation.md`
- `syllabus/10-distributed-systems/multi-region-failover-and-disaster-recovery.md`
- `syllabus/09-messaging-event-driven/messaging-patterns-and-change-data-capture.md`
