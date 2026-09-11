---
title: "Flashcards: Consensus Algorithms — Raft and Paxos"
slug: consensus-algorithms-raft-and-paxos
document_type: flashcard-deck
domain: 10-distributed-systems
topic_id: T-2403
canonical: ../syllabus/10-distributed-systems/consensus-algorithms-raft-and-paxos.md
last_updated: 2026-09-11
---

# Flashcards: Consensus Algorithms — Raft and Paxos

**Canonical chapter:** [`syllabus/10-distributed-systems/consensus-algorithms-raft-and-paxos.md`](../syllabus/10-distributed-systems/consensus-algorithms-raft-and-paxos.md)

## Card: Why two leaders can't coexist

**Prompt:**
Why can't two nodes both believe they're the leader for the same term?

**Answer:**
Becoming leader requires a majority of votes. Two disjoint groups of nodes can't both be a majority of the same fixed total — it's arithmetic, not a rule the algorithm has to separately enforce.

**Why it matters:**
The single fact every other consensus safety guarantee reduces to.

**Common trap:**
Explaining leader uniqueness as a special rule rather than a direct consequence of majority arithmetic.

**Related:**
[Consensus Algorithms: Raft and Paxos](../syllabus/10-distributed-systems/consensus-algorithms-raft-and-paxos.md)

## Card: The partitioned minority side

**Prompt:**
Can a network partition's minority side ever elect a leader by retrying enough times?

**Answer:**
No — a real, deterministic simulation proves it caps out short of quorum on every attempt, for as long as the partition lasts. This is deliberate, correct unavailability, not a bug.

**Why it matters:**
Directly explains why Raft/Paxos-based systems favor consistency over availability during a partition (the CP side of CAP).

**Common trap:**
Assuming enough retries eventually let the minority side succeed.

**Related:**
[Consensus Algorithms: Raft and Paxos](../syllabus/10-distributed-systems/consensus-algorithms-raft-and-paxos.md)

## Card: Raft vs. Paxos

**Prompt:**
Does Raft provide a stronger consistency guarantee than Paxos?

**Answer:**
No — the two provide the identical real guarantee. Raft was designed specifically to be more understandable and easier to implement correctly, not more correct.

**Why it matters:**
A common misconception — picking Raft is an engineering/understandability decision, not a safety upgrade.

**Common trap:**
Claiming Raft is "better" than Paxos in terms of consistency guarantees.

**Related:**
[Consensus Algorithms: Raft and Paxos](../syllabus/10-distributed-systems/consensus-algorithms-raft-and-paxos.md)
