---
title: "Interview Question Bank — 10-distributed-systems"
document_type: interview-question-bank
domain: 20-interview-preparation
status: in progress
version: 1.0
last_updated: 2026-09-13
related:
  - ../../10-distributed-systems/INDEX.md
  - 09-messaging-event-driven.md
  - ../../../00-project/interview-question-bank-plan.md
---

# Interview Question Bank — Distributed Systems

Part of the multi-domain compendium. See [`06-databases.md`](06-databases.md) for the
tier-explanation format and `00-project/interview-question-bank-plan.md` for the full
22-domain plan and sourcing discipline.

**Honest count for this domain:** 7 chapters yielded 15 deep questions + 24 quick-fire
questions = **39 real questions**. No Junior Fundamentals chapter exists in this
domain — distributed systems presupposes backend fundamentals already covered
elsewhere.

**Incidental fix while mining this domain:** `multi-region-failover-and-disaster-recovery.md`'s
Flashcards section used `## Card:` (heading level 2) instead of the file's own
`### Card:` convention (heading level 3) for all three of its cards — the same class
of markdown-hierarchy inconsistency found and fixed in `09-messaging-event-driven`'s
`schema-registry-and-compatibility-evolution.md`, corrected in the same pass.

---

## CAP Theorem and Consistency Models

### Q1 — CAP: what does a system actually give up during a partition? Be specific about your own system.

**Canonical treatment:** [§ Interview Questions, Q1](../../10-distributed-systems/cap-theorem-and-consistency-models.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Recites "consistency, availability, partition tolerance, pick two" without naming what a real system actually does — the common mistake this question targets.
- **Senior:** Correctly classifies a real or hypothetical system as CP or AP.
- **Staff:** Ties the classification to a specific, user-visible consequence (e.g., "a session update on one side isn't visible on the other until reconciliation").

### Q2 — What is the difference between eventual and strong consistency for the user?

**Canonical treatment:** [§ Interview Questions, Q2](../../10-distributed-systems/cap-theorem-and-consistency-models.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Gives the formal definition without connecting it to what the user actually experiences — the common mistake this question targets.
- **Senior:** Gives the correct formal distinction — strong consistency means no stale reads at a latency/availability cost; eventual consistency means a possible temporary staleness window.
- **Staff:** Produces both example types unprompted (clearly fine vs. clearly not) and connects the "payment didn't appear to go through" case to the idempotency mechanism.

---

## Consensus Algorithms — Raft and Paxos

### Q1 — Why does requiring a strict majority of votes prevent two different nodes from both becoming leader in the same term?

**Canonical treatment:** [§ Interview Questions, Q1](../../10-distributed-systems/consensus-algorithms-raft-and-paxos.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Describes majority-vote as simply "the way Raft happens to work" — the common mistake this question targets.
- **Senior:** Gives the overlap argument correctly — any two subsets each strictly more than half the group must overlap by at least one member.
- **Staff:** Connects this directly to CAP's CP classification — the minority side's inability to reach a majority during a partition *is* the mechanism behind choosing consistency over availability.

### Q2 — A node believes it's the leader, but has been disconnected and a new leader has since been elected. What happens when connectivity is restored?

**Canonical treatment:** [§ Interview Questions, Q2](../../10-distributed-systems/consensus-algorithms-raft-and-paxos.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Describes the resolution vaguely ("eventually it figures out it's not the leader anymore") — the common mistake this question targets.
- **Senior:** Correctly names the term-comparison, immediate-step-down mechanism — the stale leader adopts the higher term and transitions to `FOLLOWER` the moment it observes it.
- **Staff:** Proactively raises the log-replication/commit-acknowledgment implication for a disconnected leader that may have accepted writes it couldn't safely commit.

---

## Data Partitioning and Consistent Hashing

### Q1 — Add a node — how much data moves?

**Canonical treatment:** [§ Interview Questions, Q1](../../10-distributed-systems/data-partitioning-and-consistent-hashing.md#interview-questions)

**What's expected:**
- **Junior/Mid:** States that consistent hashing moves less data than naive hashing, without precise figures.
- **Senior:** Correctly names consistent hashing and states the approximate `1/N` cost (vs. ~92.5% for naive `hash % N`).
- **Staff:** Explains the virtual-node mechanism as the reason the measured number approaches the ideal, and reasons about the load-distribution-vs-memory trade-off.

### Q2 — Your shard key is the timestamp. What breaks?

**Canonical treatment:** [§ Interview Questions, Q2](../../10-distributed-systems/data-partitioning-and-consistent-hashing.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Conflates "consistent hashing solves rebalancing cost" with "consistent hashing solves hot keys" — the common mistake this question targets.
- **Senior:** Identifies the hot-node risk — all recent writes hash to nearby points, creating a hot node/partition regardless of an otherwise well-distributed hash.
- **Staff:** Proposes a compound key (timestamp + a distributing prefix) as the fix, and names the trade-off it reintroduces (range queries now fan out across the prefix's full space).

---

## Distributed Systems Failure Modes

### Q1 — You added retries and made the outage worse. Explain the mechanism precisely.

**Canonical treatment:** [§ Interview Questions, Q1](../../10-distributed-systems/distributed-systems-failure-modes.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Gives a vague "retries added more load" without the specific additive claim — the common mistake this question targets.
- **Senior:** Correctly identifies that retries add to the queue rather than replace still-running original attempts, amplifying load with no offsetting success-rate benefit.
- **Staff:** Cites the specific measured numbers (4/12 succeeded either way, but no-backoff cost 2.3× the load) and explains why backoff reduces total amplification, not just latency.

### Q2 — How do you distinguish "the request failed" from "the request succeeded slowly," and why does it matter?

**Canonical treatment:** [§ Interview Questions, Q2](../../10-distributed-systems/distributed-systems-failure-modes.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Treats all timeouts as equivalent to failures — the common mistake this question targets.
- **Senior:** States the distinction (lost response vs. genuine failure) and its consequence — retrying without idempotency protection can duplicate the effect.
- **Staff:** Names idempotency keys as the structural fix, explaining how they shift the burden of resolving the ambiguity from the client to the server.

### Q3 — Two nodes both believe they are leader. How, and what breaks?

**Canonical treatment:** [§ Interview Questions, Q3](../../10-distributed-systems/distributed-systems-failure-modes.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Describes the scenario but doesn't name the fix (fencing tokens) when asked — the common mistake this question targets.
- **Senior:** Describes the split-brain scenario correctly — a paused node's lease expires without it knowing, a new leader is elected, and the stale node's eventual write corrupts shared state.
- **Staff:** Names fencing tokens, states precisely where the check must live (the storage/resource layer, not the nodes themselves), and can point to the real reject condition (`token < highestTokenSeen`).

---

## Distributed Transactions — Saga and Outbox

### Q1 — You wrote to the DB and published to Kafka. Prove no message is lost.

**Canonical treatment:** [§ Interview Questions, Q1](../../10-distributed-systems/distributed-transactions-saga-and-outbox.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Proposes "just retry the Kafka call" without addressing that a crash before the call means there's nothing to retry — the common mistake this question targets.
- **Senior:** Names the outbox pattern and the poller mechanism — cannot be proven for a plain dual write, the outbox makes the claim provable by writing the event atomically with the business row.
- **Staff:** States the at-least-once guarantee precisely (not exactly-once) and names the idempotent-consumer requirement this creates downstream, unprompted.

### Q2 — Compensate a charged payment.

**Canonical treatment:** [§ Interview Questions, Q2](../../10-distributed-systems/distributed-transactions-saga-and-outbox.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Describes compensation as "undoing" the original operation as if it were a database rollback — the common mistake this question targets.
- **Senior:** Correctly frames compensation as a new, forward-moving business operation (issue a refund), not a rollback.
- **Staff:** Discusses compensations needing their own retry/idempotency treatment — a failed refund is itself a dual-write-shaped problem.

---

## Multi-Region Failover and Disaster Recovery

### Q1 — Your team wants to add a DR region. What's the first question you ask?

**Canonical treatment:** [§ Interview Questions, Q1](../../10-distributed-systems/multi-region-failover-and-disaster-recovery.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Jumps straight to "we'll use active-active" or a specific cloud service — the common mistake this question targets.
- **Senior:** Names RPO and RTO explicitly and explains why each drives a different part of the design.
- **Staff:** Notes that "as fast/safe as possible" is not an answer, pushes back on it, and connects the eventual tier choice back to the real cost implications for whichever numbers come back.

### Q2 — Walk me through exactly what happens if you promote a standby without fencing the old primary during a network partition.

**Canonical treatment:** [§ Interview Questions, Q2](../../10-distributed-systems/multi-region-failover-and-disaster-recovery.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Describes split-brain vaguely ("it gets confused") without naming the divergent-writes mechanism — the common mistake this question targets.
- **Senior:** Correctly describes the divergent-writes mechanism — the old primary isn't actually dead, just unreachable, and both nodes independently accept writes once the standby is promoted.
- **Staff:** Names fencing/STONITH unconditionally, describes a concrete mechanism for it, and states that fencing must be automated, not a manual judgment call under incident pressure.

---

## Vector Clocks and Quorum-Based Replication

### Q1 — Your team lowered a read quorum for latency, and users started seeing their own writes disappear on refresh. What happened, and how do you fix it?

**Canonical treatment:** [§ Interview Questions, Q1](../../10-distributed-systems/vector-clocks-and-quorum-based-replication.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes the fix is unrelated to quorum math (e.g., blaming a cache) — the common mistake this question targets.
- **Senior:** Correctly states `W + R > N` as the guarantee and diagnoses its violation as the root cause.
- **Staff:** Proposes treating `N`/`W`/`R` as one jointly-reviewed configuration with an automated check, and discusses the real latency-versus-freshness trade being made either way.

### Q2 — Two replicas both accepted a write for the same key while partitioned. How does the system know whether to merge them or just pick one?

**Canonical treatment:** [§ Interview Questions, Q2](../../10-distributed-systems/vector-clocks-and-quorum-based-replication.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes a timestamp alone can reliably distinguish concurrent from causally-ordered writes — the common mistake this question targets, unreliable due to clock skew and network delay.
- **Senior:** Correctly names vector clocks and the dominates/concurrent distinction — a `CONCURRENT` comparison means neither can be safely discarded.
- **Staff:** Names a concrete real merge strategy (Dynamo's shopping-cart union) and states plainly that vector clocks detect but never resolve the conflict.

---

## Quick-fire questions (from this domain's Flashcards)

| # | Question | Canonical chapter |
|---|---|---|
| 1 | Does CAP apply outside of an actual network partition? | [CAP Theorem and Consistency Models](../../10-distributed-systems/cap-theorem-and-consistency-models.md#flashcards) |
| 2 | What does a CP system do during a partition? | [CAP Theorem and Consistency Models](../../10-distributed-systems/cap-theorem-and-consistency-models.md#flashcards) |
| 3 | What does an AP system do during a partition? | [CAP Theorem and Consistency Models](../../10-distributed-systems/cap-theorem-and-consistency-models.md#flashcards) |
| 4 | CAP only describes partition trade-offs. What does PACELC add? | [CAP Theorem and Consistency Models](../../10-distributed-systems/cap-theorem-and-consistency-models.md#flashcards) |
| 5 | Should one consistency model apply uniformly across a whole system? | [CAP Theorem and Consistency Models](../../10-distributed-systems/cap-theorem-and-consistency-models.md#flashcards) |
| 6 | Why does requiring a strict majority make two simultaneous leaders mathematically impossible? | [Consensus Algorithms: Raft and Paxos](../../10-distributed-systems/consensus-algorithms-raft-and-paxos.md#flashcards) |
| 7 | Can the minority side of a partition ever elect a leader, no matter how many times it retries? | [Consensus Algorithms: Raft and Paxos](../../10-distributed-systems/consensus-algorithms-raft-and-paxos.md#flashcards) |
| 8 | Does Raft provide a stronger correctness guarantee than a correctly implemented Paxos? | [Consensus Algorithms: Raft and Paxos](../../10-distributed-systems/consensus-algorithms-raft-and-paxos.md#flashcards) |
| 9 | Why does naive `hash(key) % N` remap nearly all keys when N changes? | [Data Partitioning and Consistent Hashing](../../10-distributed-systems/data-partitioning-and-consistent-hashing.md#flashcards) |
| 10 | What fraction of keys should move when removing 1 of N nodes under consistent hashing? | [Data Partitioning and Consistent Hashing](../../10-distributed-systems/data-partitioning-and-consistent-hashing.md#flashcards) |
| 11 | Why use many virtual nodes per physical node instead of one? | [Data Partitioning and Consistent Hashing](../../10-distributed-systems/data-partitioning-and-consistent-hashing.md#flashcards) |
| 12 | Why is a network timeout ambiguous? | [Distributed Systems Failure Modes](../../10-distributed-systems/distributed-systems-failure-modes.md#flashcards) |
| 13 | Precisely how do retries amplify an outage? | [Distributed Systems Failure Modes](../../10-distributed-systems/distributed-systems-failure-modes.md#flashcards) |
| 14 | What structurally fixes the retry-safety problem? | [Distributed Systems Failure Modes](../../10-distributed-systems/distributed-systems-failure-modes.md#flashcards) |
| 15 | What structurally prevents split-brain corruption? | [Distributed Systems Failure Modes](../../10-distributed-systems/distributed-systems-failure-modes.md#flashcards) |
| 16 | What specifically does a dual write fail to guarantee? | [Distributed Transactions: Saga, Outbox, and 2PC](../../10-distributed-systems/distributed-transactions-saga-and-outbox.md#flashcards) |
| 17 | What makes the transactional outbox's atomicity possible? | [Distributed Transactions: Saga, Outbox, and 2PC](../../10-distributed-systems/distributed-transactions-saga-and-outbox.md#flashcards) |
| 18 | Is the transactional outbox exactly-once or at-least-once? | [Distributed Transactions: Saga, Outbox, and 2PC](../../10-distributed-systems/distributed-transactions-saga-and-outbox.md#flashcards) |
| 19 | Why is 2PC avoided in practice despite offering true atomicity? | [Distributed Transactions: Saga, Outbox, and 2PC](../../10-distributed-systems/distributed-transactions-saga-and-outbox.md#flashcards) |
| 20 | What's the difference between RPO and RTO? | [Multi-Region Failover and DR](../../10-distributed-systems/multi-region-failover-and-disaster-recovery.md#flashcards) |
| 21 | What actually causes split-brain? | [Multi-Region Failover and DR](../../10-distributed-systems/multi-region-failover-and-disaster-recovery.md#flashcards) |
| 22 | What does fencing actually guarantee, and why is it non-optional? | [Multi-Region Failover and DR](../../10-distributed-systems/multi-region-failover-and-disaster-recovery.md#flashcards) |
| 23 | What are the three possible outcomes of comparing two vector clocks? | [Vector Clocks and Quorum-Based Replication](../../10-distributed-systems/vector-clocks-and-quorum-based-replication.md#flashcards) |
| 24 | What's the exact condition guaranteeing a read quorum overlaps a write quorum, and is `W + R = N` sufficient? | [Vector Clocks and Quorum-Based Replication](../../10-distributed-systems/vector-clocks-and-quorum-based-replication.md#flashcards) |

---

## Related

- [`09-messaging-event-driven.md`](09-messaging-event-driven.md)
- [`08-testing.md`](08-testing.md)
- [`07-api-design.md`](07-api-design.md)
- [`05-spring.md`](05-spring.md)
- [`04-software-design.md`](04-software-design.md)
- [`03-data-structures-algorithms.md`](03-data-structures-algorithms.md)
- [`06-databases.md`](06-databases.md)
- [`02-java-collections.md`](02-java-collections.md), [`02-java-concurrency.md`](02-java-concurrency.md), [`02-java-jvm-internals.md`](02-java-jvm-internals.md), [`02-java-language-core.md`](02-java-language-core.md)
- [`00-project/interview-question-bank-plan.md`](../../../00-project/interview-question-bank-plan.md)
