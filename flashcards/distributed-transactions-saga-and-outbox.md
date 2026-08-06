---
title: "Flashcards: Distributed Transactions: Saga, Outbox, and 2PC"
slug: distributed-transactions-saga-and-outbox
document_type: flashcard-deck
domain: system-design
topic_id: T-618
canonical: ../handbook/system-design/distributed-transactions-saga-and-outbox.md
last_updated: 2026-08-06
---

# Flashcards: Distributed Transactions: Saga, Outbox, and 2PC

**Canonical chapter:** [`handbook/system-design/distributed-transactions-saga-and-outbox.md`](../handbook/system-design/distributed-transactions-saga-and-outbox.md)

## Card: What a dual write fails to guarantee

**Prompt:**
What specifically does a dual write (DB write + separate message publish) fail to guarantee?

**Answer:**
That the message gets published if the DB write succeeds and a crash follows before the publish call — there's no shared transaction, so nothing records that a publish is owed.

**Why it matters:**
The precise mechanism, not just "dual writes are risky."

**Common trap:**
Assuming a retry loop around the publish call fixes this.

**Related:**
[Internal Implementation](../handbook/system-design/distributed-transactions-saga-and-outbox.md#internal-implementation)

## Card: What makes the outbox atomic

**Prompt:**
What makes the transactional outbox's atomicity possible?

**Answer:**
Writing the business row and the outbox row in the SAME database transaction — the database's own transaction guarantee, not application coordination.

**Why it matters:**
The core mechanism the entire pattern depends on.

**Common trap:**
Believing application-level coordination (two separate writes with manual retry logic) achieves the same atomicity.

**Related:**
[Core Concepts](../handbook/system-design/distributed-transactions-saga-and-outbox.md#core-concepts)

## Card: Outbox's actual guarantee

**Prompt:**
Is the transactional outbox exactly-once or at-least-once?

**Answer:**
At-least-once — a crash between "the broker confirms the send" and "the DB marks the row published" causes a real, measured duplicate; the downstream consumer must be idempotent.

**Why it matters:**
Prevents overclaiming exactly-once from a pattern that structurally can't provide it.

**Common trap:**
Assuming the outbox pattern is exactly-once because it eliminates loss.

**Related:**
[Internal Implementation](../handbook/system-design/distributed-transactions-saga-and-outbox.md#internal-implementation)

## Card: Why 2PC is avoided

**Prompt:**
Why is 2PC avoided in practice despite offering true atomicity?

**Answer:**
Locks are held across the coordinator's round-trip; a coordinator crash leaves "prepared" participants stuck indefinitely (in-doubt transactions) — poor availability under partial failure.

**Why it matters:**
The precise, structural reason, not just "2PC is old/slow."

**Common trap:**
Attributing 2PC's unpopularity to performance rather than availability.

**Related:**
[Core Concepts](../handbook/system-design/distributed-transactions-saga-and-outbox.md#core-concepts)
