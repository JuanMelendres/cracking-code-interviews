---
title: "Flashcards: Retention, Log Compaction, and Tiered Storage"
slug: retention-log-compaction-and-tiered-storage
document_type: flashcard-deck
domain: 09-messaging-event-driven
topic_id: T-706
canonical: ../syllabus/09-messaging-event-driven/retention-log-compaction-and-tiered-storage.md
last_updated: 2026-09-11
---

# Flashcards: Retention, Log Compaction, and Tiered Storage

**Canonical chapter:** [`syllabus/09-messaging-event-driven/retention-log-compaction-and-tiered-storage.md`](../syllabus/09-messaging-event-driven/retention-log-compaction-and-tiered-storage.md)

## Card: delete vs. compact

**Prompt:**
What's the difference between `cleanup.policy=delete` and `cleanup.policy=compact`?

**Answer:**
`delete` discards old records by age or size, regardless of key. `compact` keeps only the latest value per key, forever, discarding older values for the same key as the log cleaner rewrites segments.

**Why it matters:**
Two genuinely different retention guarantees for two different use cases — event history vs. latest-state-per-key.

**Common trap:**
Assuming `compact` is just a stricter version of `delete`, rather than a different guarantee entirely.

**Related:**
[Retention, Log Compaction, and Tiered Storage](../syllabus/09-messaging-event-driven/retention-log-compaction-and-tiered-storage.md)

## Card: How to delete a key from a compacted topic

**Prompt:**
How do you delete a specific key entirely from a compacted topic?

**Answer:**
Write a tombstone — a record with that key and a null value. It survives for `delete.retention.ms` (to give lagging consumers a chance to see the deletion) before being physically removed by the cleaner.

**Why it matters:**
The real mechanism, not "the key just disappears."

**Common trap:**
Assuming a tombstone removes the key immediately, or forgetting `delete.retention.ms` exists.

**Related:**
[Retention, Log Compaction, and Tiered Storage](../syllabus/09-messaging-event-driven/retention-log-compaction-and-tiered-storage.md)

## Card: Compaction observed directly

**Prompt:**
What does a real compaction pass actually do to a topic with repeated keys?

**Answer:**
A real, observed compaction pass reduced eight keyed records down to five survivors — one per distinct key, keeping only each key's latest value.

**Why it matters:**
Concrete, measured evidence rather than an abstract description of the mechanism.

**Common trap:**
Describing compaction only in the abstract without being able to name what actually happens to the record count.

**Related:**
[Retention, Log Compaction, and Tiered Storage](../syllabus/09-messaging-event-driven/retention-log-compaction-and-tiered-storage.md)
