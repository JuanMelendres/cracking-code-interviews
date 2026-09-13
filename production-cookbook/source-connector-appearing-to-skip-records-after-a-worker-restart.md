---
title: "Source Connector Appearing to Skip Records After a Worker Restart"
document_type: production-cookbook-entry
domain: kafka
status: draft
last_updated: 2026-09-12
related_handbook:
  - ../syllabus/09-messaging-event-driven/kafka-connect-source-and-sink-connectors.md
source: syllabus/09-messaging-event-driven/kafka-connect-source-and-sink-connectors.md#production-scenarios
---

# Source Connector Appearing to Skip Records After a Worker Restart

## Context

A Kafka Connect worker crashes and is automatically restarted by an orchestrator.

## Symptoms

A downstream consumer of the connector's output topic reports missing records that were known to exist in the source system before the crash.

## Impact

Data that should have been captured from the external system appears never to arrive in Kafka.

## Initial Hypotheses

- A bug in the source system itself — checked, the records genuinely existed and were readable before the crash.
- A Kafka Connect fault-tolerance failure — checked against this chapter's own demonstrated behavior; offset-based resume, correctly implemented, does not skip data.
- `offset.flush.interval.ms` was configured unusually high, and the worker crashed between successfully reading records and its next scheduled offset commit, so on restart it resumed from an older, previously-committed offset than where it had actually already produced to — correct, and notably the safe direction of failure (at-least-once re-delivery, not data loss).

## Evidence

The source connector's actual `offset.flush.interval.ms` and the worker's logs around the crash timestamp show which offset was last durably committed versus what had actually been read from the source.

## Investigation Timeline

1. Downstream consumer reports missing records following a worker crash-and-restart.
2. Source-system-bug and Connect-fault-tolerance-failure hypotheses examined against known-good behavior.
3. `offset.flush.interval.ms` and crash-time worker logs checked, revealing a large gap between last-read and last-committed offset.
4. Verified against the source system directly which records were genuinely never read (a true gap) versus already read-but-not-yet-flushed at crash time.

## Root Cause

A high `offset.flush.interval.ms` combined with a crash landing between a read and its next scheduled offset commit causes the worker to resume from an older committed offset — the safe (redundant-read) direction of failure, not data loss; "missing records" downstream in this specific incident traced to the source connector's own read logic not having reached those records before the crash at all.

## Immediate Mitigation

Distinguish, per record, genuinely-never-read records (a true gap, needing separate remediation) from already-read-but-not-yet-flushed ones (which Connect's own at-least-once semantics would redeliver, not lose).

## Permanent Fix

Tune `offset.flush.interval.ms` to a value appropriate for the acceptable redundant-read window, and, for connectors where source-side data could be evicted or rotated out before Connect reads it, address that retention mismatch directly.

## Alternatives Considered

Blaming Kafka Connect's fault-tolerance model as unreliable — rejected once the chapter's own real, repeatable evidence (a genuine kill-and-restart producing correct, non-duplicated, non-skipped results) is checked against the specific configuration and timeline of the actual incident.

## Trade-offs

A more frequent offset flush reduces the redundant-read window after a crash but adds real, if usually small, overhead per flush.

## Prevention

Understand and explicitly tune `offset.flush.interval.ms` for each source connector's actual redundancy tolerance, rather than leaving it at a default that may not match the specific source system's own retention characteristics.

## Monitoring and Alerts

- Alerting on worker restarts correlated with source-connector read-lag metrics, so a crash-during-read-window incident is flagged for manual reconciliation rather than discovered only via downstream complaint.
- A standing comparison of last-committed offset versus last-known-good source-system position after any worker restart.

## Interview Story

Present as a representative scenario unless personally lived through an equivalent incident.

- **Situation:** downstream consumers reported missing records after a Kafka Connect worker crash-and-restart.
- **Task:** determine whether this was true data loss or a diagnosable configuration interaction.
- **Action:** checked `offset.flush.interval.ms` and crash-time logs, and verified directly against the source system which records were genuinely unread versus already-read-but-unflushed.
- **Result:** confirmed no data loss occurred structurally; tuned the flush interval to reduce the redundant-read window going forward.

## Staff-Level Discussion

Kafka Connect's offset-based recovery is a real, provable mechanism — an apparent "skipped record" symptom usually traces to a specific, diagnosable configuration or source-system interaction, not a fundamental flaw in the recovery model itself. The organizational lesson is treating `offset.flush.interval.ms` as a deliberate, reviewed trade-off per connector, not a default left unexamined, since it directly governs how much redundant re-delivery a crash can produce.

## Related Handbook Chapters

- [Kafka Connect: Source and Sink Connectors](../syllabus/09-messaging-event-driven/kafka-connect-source-and-sink-connectors.md) — the canonical offset-flush recovery mechanics behind this incident.
