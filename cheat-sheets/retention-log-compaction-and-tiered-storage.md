---
title: "Cheat Sheet: Retention, Log Compaction, and Tiered Storage"
slug: retention-log-compaction-and-tiered-storage
document_type: cheat-sheet
domain: 09-messaging-event-driven
topic_id: T-706
canonical: ../syllabus/09-messaging-event-driven/retention-log-compaction-and-tiered-storage.md
last_updated: 2026-09-11
---

# Retention, Log Compaction, and Tiered Storage

**Canonical chapter:** [`syllabus/09-messaging-event-driven/retention-log-compaction-and-tiered-storage.md`](../syllabus/09-messaging-event-driven/retention-log-compaction-and-tiered-storage.md)

## Core Mental Model

A compacted topic trades "keep everything, for a bounded time" for "keep everything's latest state, indefinitely" — the log cleaner continuously rewrites closed segments to enforce that guarantee, rather than the broker aging segments out purely by clock time.

## Essential Definitions

- **`cleanup.policy=delete`** — the default; discards old records by age (`retention.ms`) or size (`retention.bytes`).
- **`cleanup.policy=compact`** — keeps only the latest value per key, forever.
- **Tombstone** — a null-valued record; the mechanism to delete a specific key from a compacted topic.
- **Tiered storage** — offloads older segments to cheaper remote storage, retaining more history at lower local-disk cost.

## Decision Table

| Need | Config/Mechanism |
|---|---|
| Discard old records by age | `cleanup.policy=delete`, `retention.ms` |
| Discard old records by total size | `cleanup.policy=delete`, `retention.bytes` |
| Keep only the latest value per key, forever | `cleanup.policy=compact` |
| Delete a specific key from a compacted topic | A null-valued record (tombstone) |
| Control how long a tombstone survives before removal | `delete.retention.ms` |
| Control how eagerly the cleaner compacts | `min.cleanable.dirty.ratio` |
| Retain more history at lower local-disk cost | Tiered storage (`remote.storage.enable`) |

## Common Pitfalls

- Assuming a tombstone deletes the key immediately — it survives for `delete.retention.ms` before physical removal, to give lagging consumers time to see it.
- Confusing `cleanup.policy=delete`'s time/size-based retention with `compact`'s per-key latest-value guarantee — they solve genuinely different problems.
- Setting `min.cleanable.dirty.ratio` too low, causing excessive, wasteful compaction passes on a high-write-volume topic.

## Interview Answer Skeleton

**30-sec:** `delete` retention discards by age/size; `compact` keeps only the latest value per key forever — same append-only log, a background cleaner rewrites segments to enforce whichever guarantee is configured.

**2-min:** Add: a real observed compaction pass reduced eight keyed records to five survivors — direct, measured proof of the mechanism, not just an assertion. Tombstones need `delete.retention.ms` to give lagging consumers a chance to see the deletion before physical removal.

**Staff-level framing:** Tiered storage decouples "how much history to keep" from "how much local disk this costs" — a real cost/retention trade-off distinct from the compaction-vs-delete choice itself.

## Related

- syllabus/09-messaging-event-driven/kafka-streams-and-stateful-processing.md
- syllabus/09-messaging-event-driven/kafka-connect-source-and-sink-connectors.md
