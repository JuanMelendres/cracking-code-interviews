---
title: "Flashcards: Locks, Deadlocks, and Lock Escalation"
slug: locks-deadlocks-and-lock-escalation
document_type: flashcard-deck
domain: databases
topic_id: T-613
canonical: ../handbook/databases/locks-deadlocks-and-lock-escalation.md
last_updated: 2026-09-02
---

# Flashcards: Locks, Deadlocks, and Lock Escalation

**Canonical chapter:** [`syllabus/06-databases/locks-deadlocks-and-lock-escalation.md`](../syllabus/06-databases/locks-deadlocks-and-lock-escalation.md)

## Card: Does PostgreSQL escalate row locks?

**Prompt:**
Does PostgreSQL escalate row locks to table locks under high contention, like MySQL/InnoDB or SQL Server can?

**Answer:**
No. Row locks live on the tuple header itself, not as lock-manager entries — there's nothing to escalate.

**Why it matters:**
A precise, checkable claim that separates PostgreSQL-specific reasoning from cross-database assumption; verified directly with an identical lock footprint for 1 row and 20,000 rows.

**Common trap:**
Carrying MySQL/SQL Server intuition into a PostgreSQL answer.

**Related:**
[Core Concepts](../syllabus/06-databases/locks-deadlocks-and-lock-escalation.md#core-concepts)

## Card: How PostgreSQL detects a deadlock

**Prompt:**
What's the actual mechanism PostgreSQL uses to detect a deadlock?

**Answer:**
A wait-for graph: after `deadlock_timeout` (default 1s), it checks whether the blocked transaction's wait chain forms a cycle. If so, it aborts one transaction in the cycle.

**Why it matters:**
"The database detects deadlocks" is Junior-level; naming the actual graph-cycle mechanism, and being able to read the real `pg_locks` evidence for it, is the Staff-level version.

**Common trap:**
Describing deadlock detection vaguely, or confusing ordinary blocking (no cycle) with a real deadlock.

**Related:**
[Internal Implementation](../syllabus/06-databases/locks-deadlocks-and-lock-escalation.md#internal-implementation)

## Card: What actually limits PostgreSQL instead of escalation

**Prompt:**
If not row-lock escalation, what real PostgreSQL failure mode limits a lock-heavy transaction?

**Answer:**
Exhausting the shared lock table (`max_locks_per_transaction × max_connections`) — but only via distinct lockable objects (tables, advisory locks), never via row count.

**Why it matters:**
This chapter demonstrated it directly: 20,000 row locks cost nothing extra; 5,000 advisory locks in one transaction produced a real `out of shared memory` error.

**Common trap:**
Assuming a huge bulk `UPDATE` risks this limit — it doesn't, since row locks don't consume lock-table slots.

**Related:**
[Production Scenarios](../syllabus/06-databases/locks-deadlocks-and-lock-escalation.md#production-scenarios)
