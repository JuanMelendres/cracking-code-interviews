---
title: "Hand-Rolled Leader Election Producing a Split-Brain During a Network Blip"
document_type: production-cookbook-entry
domain: system-design
status: draft
last_updated: 2026-09-12
related_handbook:
  - ../syllabus/10-distributed-systems/consensus-algorithms-raft-and-paxos.md
source: syllabus/10-distributed-systems/consensus-algorithms-raft-and-paxos.md#production-scenarios
---

# Hand-Rolled Leader Election Producing a Split-Brain During a Network Blip

## Context

A distributed job scheduler uses a hand-rolled leader-election scheme — "whichever instance's row in a shared database was updated most recently is the active scheduler" — rather than a real consensus algorithm.

## Symptoms

During a brief network partition between the active scheduler instance and the database, a second instance, unable to see the first instance's recent heartbeat, promotes itself to active and begins scheduling jobs. When the partition heals, both instances briefly believe they are the active scheduler, and a batch of jobs runs twice.

## Impact

Duplicate job execution — for an idempotent job, a wasted resource cost; for a non-idempotent one (e.g., charging a customer, sending a daily digest email), a real, user-visible correctness incident.

## Initial Hypotheses

- A database consistency bug — checked, the database behaved correctly per its own guarantees.
- A bug in the heartbeat-timeout logic — checked, timeouts fired exactly as configured.
- The underlying leader-election scheme has no real majority-quorum mechanism, so nothing structurally prevents two instances from both believing they're active during a partition — correct.

## Evidence

Both instances' logs show them independently concluding "I am now the active scheduler" during the same window — a design that never asked "did I actually get a majority," only "does the other instance look unresponsive to me."

## Investigation Timeline

1. Duplicate job execution reported following a brief network partition.
2. Database-consistency and heartbeat-timeout-bug hypotheses ruled out.
3. Both instances' logs reviewed, showing independent, unquorumed self-promotion during the same partition window.

## Root Cause

The hand-rolled scheme has no equivalent of a majority-quorum requirement — it can produce two simultaneous "leaders" precisely because nothing in its design makes that mathematically impossible.

## Immediate Mitigation

Manually deduplicate the affected job runs and audit for any non-idempotent side effects from the double execution.

## Permanent Fix

Replace the hand-rolled scheme with a real, battle-tested consensus-backed lock (e.g., `etcd`'s lease-based distributed lock, built on Raft) — the majority-quorum requirement makes "two simultaneous leaders" structurally impossible, not merely unlikely.

## Alternatives Considered

Tightening the heartbeat-timeout window to reduce the incident's likelihood — rejected as treating the symptom; a shorter timeout reduces the frequency of the failure mode without addressing that the underlying scheme has no real safety guarantee against it at all.

## Trade-offs

Adopting a real consensus-backed lock adds an operational dependency (running or consuming `etcd`) in exchange for a genuine, structural correctness guarantee the previous scheme never actually had.

## Prevention

Treat "we need exactly one active instance" as a consensus-shaped requirement by default, and reach for a real, existing implementation rather than a heuristic (most-recent-heartbeat, lowest-ID) with no actual majority-quorum mechanism underneath it.

## Monitoring and Alerts

- Alerting on any window where more than one instance reports itself as "active leader," even briefly, as a direct signal of split-brain risk rather than waiting for a duplicate-execution symptom.
- Job-execution idempotency-key tracking, so duplicate runs are caught and safely no-op'd regardless of the leader-election mechanism's own correctness.

## Interview Story

Present as a representative scenario unless personally lived through an equivalent incident.

- **Situation:** a brief network partition caused two scheduler instances to both believe they were the active leader, duplicating a batch of jobs.
- **Task:** find why the leader-election scheme allowed two simultaneous leaders at all.
- **Action:** reviewed both instances' logs, confirmed neither had checked for a majority quorum before self-promoting, and replaced the scheme with a consensus-backed distributed lock.
- **Result:** made split-brain structurally impossible going forward, not merely less frequent.

## Staff-Level Discussion

This is Interview Question 1 in the canonical chapter's own Interview Questions section — "why does a majority-quorum requirement prevent split-brain" — arriving as a real, duplicate-job-execution incident rather than an abstract safety property. The organizational lesson is recognizing "exactly one active instance" as a consensus-shaped requirement from the start, rather than reaching for a plausible-sounding heuristic that has no actual mathematical guarantee behind it — the difference only becomes visible during the exact partition scenario a real consensus algorithm is specifically designed to handle correctly.

## Related Handbook Chapters

- [Consensus Algorithms: Raft and Paxos](../syllabus/10-distributed-systems/consensus-algorithms-raft-and-paxos.md) — the canonical majority-quorum guarantee behind this incident's fix.
