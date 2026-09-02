---
title: "Flashcards: Messaging Patterns and Change Data Capture"
slug: messaging-patterns-and-change-data-capture
document_type: flashcard-deck
domain: system-design
topic_id: T-710
canonical: ../handbook/system-design/messaging-patterns-and-change-data-capture.md
last_updated: 2026-09-02
---

# Flashcards: Messaging Patterns and Change Data Capture

**Canonical chapter:** [`handbook/system-design/messaging-patterns-and-change-data-capture.md`](../handbook/system-design/messaging-patterns-and-change-data-capture.md)

## Card: What does CDC actually read from?

**Prompt:**
What does Change Data Capture actually read to produce its change events?

**Answer:**
The database's own transaction log (the WAL in PostgreSQL, the binlog in MySQL) — not application code, not a polled table. This is what lets CDC require zero application-code changes, proven directly in this chapter with ordinary SQL statements fully captured with no CDC-aware code at all.

**Why it matters:**
Distinguishes real understanding from name-dropping "CDC" or "Debezium" without being able to explain the mechanism.

**Common trap:**
Describing CDC as a polling mechanism, confusing it with the outbox pattern's poller.

**Related:**
[handbook/system-design/messaging-patterns-and-change-data-capture.md](../handbook/system-design/messaging-patterns-and-change-data-capture.md)

## Card: CDC's real operational cost

**Prompt:**
What real, measurable risk does an unconsumed CDC consumer create?

**Answer:**
Unbounded transaction-log (WAL) growth — the consumer's replication slot marks log segments still needed, preventing the database from reclaiming them even across a checkpoint. Measured directly in this chapter: 16 MB → 48 MB growth from 200,000 unconsumed rows.

**Why it matters:**
CDC's "zero application-code changes" benefit doesn't mean zero operational responsibility — replication-slot lag needs the same monitoring discipline as any other replication lag.

**Common trap:**
Assuming CDC is operationally free just because it requires no code changes.

**Related:**
[handbook/system-design/messaging-patterns-and-change-data-capture.md](../handbook/system-design/messaging-patterns-and-change-data-capture.md), [handbook/databases/mvcc-vacuum-and-bloat.md](../handbook/databases/mvcc-vacuum-and-bloat.md)

## Card: Point-to-point vs. publish-subscribe, proven

**Prompt:**
What did this chapter's real Kafka demo prove about point-to-point vs. publish-subscribe?

**Answer:**
The identical 10 published messages produced 10 total real deliveries when 3 consumers shared one group (point-to-point), and 30 total real deliveries when the same 3 consumers were split into 3 independent groups (publish-subscribe) — one publish, two structurally different delivery counts, purely from consumer grouping.

**Why it matters:**
Makes the abstract pattern distinction concrete and measurable rather than a definitional recitation.

**Common trap:**
Assuming the delivery pattern is determined by the message or topic itself, rather than by how consumers are grouped.

**Related:**
[handbook/system-design/messaging-patterns-and-change-data-capture.md](../handbook/system-design/messaging-patterns-and-change-data-capture.md)
