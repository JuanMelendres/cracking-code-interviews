---
title: "Read-Your-Own-Writes Breaking After a Read-Quorum Tuning Change"
document_type: production-cookbook-entry
domain: system-design
status: draft
last_updated: 2026-09-12
related_handbook:
  - ../syllabus/10-distributed-systems/vector-clocks-and-quorum-based-replication.md
source: syllabus/10-distributed-systems/vector-clocks-and-quorum-based-replication.md#production-scenarios
---

# Read-Your-Own-Writes Breaking After a Read-Quorum Tuning Change

## Context

An operator reduces the read quorum `R` on a Dynamo-style distributed cache, to lower read latency.

## Symptoms

Some users occasionally see their own just-written value revert to an older one on a subsequent read.

## Impact

Users perceive data loss — a write appears to have been silently undone.

## Initial Hypotheses

- A real bug in the write path — checked, writes are correctly acknowledged and durable on `W` replicas.
- A caching layer serving stale data — checked, the issue reproduces even bypassing any additional cache.
- The new `R`, combined with the existing `W` and `N`, no longer satisfies `W + R > N`, so some reads land on a quorum that doesn't include any replica holding the latest write — correct.

## Evidence

Recomputing `W + R` against `N` directly confirms the current configuration no longer structurally guarantees overlap between the write quorum and any given read quorum.

## Investigation Timeline

1. Users report their own recent writes reverting to older values, intermittently.
2. Write-path-bug and stale-cache hypotheses ruled out.
3. `W + R > N` recomputed against the current, just-changed configuration, confirming the inequality no longer holds.

## Root Cause

Reducing `R` without re-checking it against the existing `W` and `N` broke the `W + R > N` overlap guarantee that "read your own writes" depends on.

## Immediate Mitigation

Revert `R` to a value that restores `W + R > N`.

## Permanent Fix

Treat `N`, `W`, and `R` as a single, jointly-reviewed configuration, with an explicit, automated check that `W + R > N` holds before any change to any one of the three ships.

## Alternatives Considered

Adding client-side "read-your-writes" session stickiness (always reading from the replica a client's own write went to) — a real, valid complementary technique for this specific symptom, but not a substitute for understanding why the underlying quorum math changed.

## Trade-offs

Restoring `W + R > N` by raising `R` back up trades the read-latency improvement the operator wanted back away — a real, necessary trade, not a free fix.

## Prevention

Gate any quorum-parameter change behind an automated check that `W + R > N` still holds, rather than allowing `N`, `W`, and `R` to be tuned as three independent knobs.

## Monitoring and Alerts

- An automated pre-deployment check computing `W + R > N` for any proposed quorum configuration change, blocking the change if it fails.
- A "read-your-own-writes" synthetic check running continuously in production, catching a quorum-overlap regression directly rather than waiting for user reports.

## Interview Story

Present as a representative scenario unless personally lived through an equivalent misconfiguration.

- **Situation:** users started seeing their own writes revert after an operator tuned a distributed cache's read quorum for latency.
- **Task:** find why a seemingly isolated latency optimization broke a consistency guarantee.
- **Action:** recomputed `W + R > N` against the new configuration and confirmed the inequality no longer held.
- **Result:** reverted the quorum change and added an automated gate requiring the inequality to hold before any future quorum-parameter change ships.

## Staff-Level Discussion

`W + R > N` is a real, load-bearing arithmetic guarantee — treating `N`, `W`, and `R` as independently tunable "performance knobs" without re-checking the inequality is exactly how a system silently loses its own consistency guarantee. The organizational lesson is that any single-parameter tuning change to a distributed system's replication configuration needs to be evaluated against the *system's* full guarantee, not just the one metric (latency, in this case) the change was intended to improve.

## Related Handbook Chapters

- [Vector Clocks and Quorum-Based Replication](../syllabus/10-distributed-systems/vector-clocks-and-quorum-based-replication.md) — the canonical `W + R > N` overlap guarantee behind this incident.
