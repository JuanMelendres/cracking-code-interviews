---
title: "Flashcards: Vector Clocks and Quorum-Based Replication"
slug: vector-clocks-and-quorum-based-replication
document_type: flashcard-deck
domain: 10-distributed-systems
topic_id: T-2407
canonical: ../syllabus/10-distributed-systems/vector-clocks-and-quorum-based-replication.md
last_updated: 2026-09-11
---

# Flashcards: Vector Clocks and Quorum-Based Replication

**Canonical chapter:** [`syllabus/10-distributed-systems/vector-clocks-and-quorum-based-replication.md`](../syllabus/10-distributed-systems/vector-clocks-and-quorum-based-replication.md)

## Card: The W+R=N misconception

**Prompt:**
Is `W + R = N` (write quorum plus read quorum equal to replica count) safe enough to guarantee a read sees the latest write?

**Answer:**
No — a real, exhaustive enumeration for a 5-replica set finds a named counterexample at `W+R=N` with zero shared replicas between a specific write quorum and read quorum. Only `W + R > N` guarantees overlap in every case.

**Why it matters:**
Directly corrects a very common "close enough" misconception with rigorous, exhaustive proof rather than a hand-wave.

**Common trap:**
Treating `W+R=N` as equivalent to `W+R>N`.

**Related:**
[Vector Clocks and Quorum-Based Replication](../syllabus/10-distributed-systems/vector-clocks-and-quorum-based-replication.md)

## Card: What a vector clock detects vs. resolves

**Prompt:**
Does a vector clock automatically resolve a detected `CONCURRENT` conflict?

**Answer:**
No — it only detects that two versions are genuinely concurrent (neither causally supersedes the other). Resolving the conflict requires an application-specific merge strategy (e.g., set union), not an automatic mechanism.

**Why it matters:**
A common conceptual gap — knowing a conflict exists is different from knowing how to resolve it.

**Common trap:**
Assuming vector clocks are a complete conflict-resolution system rather than a detection mechanism.

**Related:**
[Vector Clocks and Quorum-Based Replication](../syllabus/10-distributed-systems/vector-clocks-and-quorum-based-replication.md)

## Card: DOMINATES vs. CONCURRENT

**Prompt:**
What's the real difference between a vector-clock comparison result of `DOMINATES` versus `CONCURRENT`?

**Answer:**
`DOMINATES` means one version's clock causally supersedes the other's — a genuine, safe overwrite (e.g., a read-then-write). `CONCURRENT` means neither dominates — two independent, unaware-of-each-other writes, a real, detected conflict needing resolution.

**Why it matters:**
The core distinction a vector clock is built to make, unlike a plain timestamp.

**Common trap:**
Assuming any two differing versions are automatically in conflict, without checking whether one causally supersedes the other.

**Related:**
[Vector Clocks and Quorum-Based Replication](../syllabus/10-distributed-systems/vector-clocks-and-quorum-based-replication.md)
