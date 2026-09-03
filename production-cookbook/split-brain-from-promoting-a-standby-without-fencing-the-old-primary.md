---
title: "Split-Brain from Promoting a Standby Without Fencing the Old Primary"
document_type: production-cookbook-entry
domain: system-design
status: draft
last_updated: 2026-09-02
related_handbook:
  - ../syllabus/10-distributed-systems/multi-region-failover-and-disaster-recovery.md
  - ../syllabus/10-distributed-systems/distributed-systems-failure-modes.md
source: handbook/system-design/multi-region-failover-and-disaster-recovery.md#production-scenarios
---

# Split-Brain from Promoting a Standby Without Fencing the Old Primary

## Context

During a real, genuine network partition (`docker network disconnect`, not a killed process — the old primary stays alive and fully functional), an automated controller sees the primary as unreachable and promotes the standby without first confirming the old primary is actually dead.

## Symptoms

After promotion, the old primary — still alive, merely partitioned — genuinely accepts a write from a client still able to reach it, while the newly-promoted standby independently accepts its own, different write.

## Impact

After the partition heals, the two nodes' ledgers have genuinely, observably diverged — real split-brain, with two independently-accepted, conflicting writes that must be reconciled.

## Initial Hypotheses

None needed — this was a deliberate reproduction of the split-brain failure mode, not a diagnosis of an unexpected symptom.

## Evidence

[`splitbrain-demo.sh`](../../practice/sql/multi-region-failover-and-dr/README.md) reproduced this directly: after promotion, the *old* primary accepted a write (`accepted-by-old-primary-unaware-of-failover`) from a client still able to reach it, while the newly-promoted standby independently accepted its own, different write (`accepted-by-new-primary-after-failover`). After healing the partition, the two nodes' ledgers had genuinely, observably diverged.

## Investigation Timeline

1. A genuine network partition (not a process kill) isolates the primary while it remains alive and fully functional.
2. An automated controller detects the primary as unreachable and promotes the standby, without a fencing step confirming the old primary is actually dead.
3. A client still able to reach the old primary successfully writes to it, unaware a promotion has occurred.
4. The newly-promoted standby independently accepts a different write from clients that can reach it.
5. The partition heals, and both nodes' ledgers are compared, confirming a genuine divergence between the two independently-accepted writes.
6. A second run adds a fencing step (`docker pause` on the old primary, standing in for STONITH) before promotion; the identical write attempt against the fenced old primary is refused by the fencing mechanism itself before ever reaching PostgreSQL, and the standby is promoted with zero risk of a second writer.

## Root Cause

The failover promoted a new writer without first guaranteeing the old one could not also write — an unreachable primary was treated as equivalent to a dead primary, which a genuine network partition (as opposed to a crash) disproves.

## Immediate Mitigation

Once split-brain is detected, there is no automatic fix — a human must decide which divergent history is authoritative and manually reconcile or discard the other side's writes.

## Permanent Fix

Add a fencing/STONITH step before promotion: in the reproduced fix, `docker pause` on the old primary (a real stand-in for fencing) caused the identical write attempt against the old primary to be refused before ever reaching PostgreSQL, and the standby was promoted with zero risk of a second writer.

## Alternatives Considered

Skipping fencing to fail over faster — explicitly identified as exactly what produced the real divergence in the first place, not a viable alternative.

## Trade-offs

Fencing adds a real step — and real latency — to the failover path, directly increasing RTO; the alternative (skip fencing to fail over faster) is exactly what produced the real divergence above.

## Prevention

Never promote based on "the primary looks unreachable" alone; a failover procedure must include a real, verifiable fencing step before promotion, every time, with no fast path that skips it under time pressure.

## Monitoring and Alerts

- Alert distinctly on "primary unreachable" versus "primary confirmed dead" as two different states in the failover controller's own telemetry, so an operator (or the automated system itself) never conflates the two the way the un-fenced promotion in this incident did.
- Track fencing-step success/failure as its own explicit event in the failover pipeline's logs, separate from the promotion event itself, so a promotion that occurred without a preceding successful fence is visible and auditable after the fact, not just inferred from a later reconciliation effort.
- After any real failover (fenced or not), run an automated ledger-divergence check comparing the old and new primary's data once connectivity is restored, catching a split-brain's actual data divergence directly rather than relying on it surfacing through a downstream data-consistency complaint.

## Interview Story

This maps directly to "what can go wrong during an automated failover" backed by a real, reproduced split-brain. Present it as a representative scenario to adapt, not a claimed personal history:

- **Situation:** during a genuine network partition, an automated controller promoted a standby because it could not reach the primary — but the primary was alive and fully functional, merely partitioned.
- **Task:** determine whether "unreachable" was being treated the same as "dead," and what the consequence was.
- **Action:** reproduced the failover under a real partition and observed both the old and new primary independently accept different writes; then added a fencing step (a real stand-in for STONITH) before promotion and confirmed it blocked the old primary's write attempt entirely.
- **Result:** established that fencing must precede promotion unconditionally, accepting the real added latency to failover as the cost of preventing a data-divergence incident that has no automatic fix once it occurs.

## Staff-Level Discussion

Split-brain is one of the failure modes where the cost of getting it wrong is qualitatively different from most latency or availability incidents: once it happens, there is no automated remediation — a human must decide which divergent history is authoritative, which is both slow and inherently a judgment call about which data to discard. That asymmetry is the strongest argument for accepting fencing's real, unconditional RTO cost: a slightly slower failover that is always correct is a better trade than a faster one that is occasionally catastrophic and unrecoverable by automation. The distinction between "primary is unreachable" and "primary is confirmed dead" is a general lesson for any leader-election or automatic-promotion system, not just database failover — a Staff engineer reviewing any such system should ask explicitly what evidence the controller has for "dead" versus merely "not currently reachable from here," since a network partition can produce the second without the first, and any promotion logic that doesn't distinguish them is carrying this exact risk silently until a partition (rather than a crash) actually occurs.

## Related Handbook Chapters

- [Multi-Region Failover and Disaster Recovery](../syllabus/10-distributed-systems/multi-region-failover-and-disaster-recovery.md) — canonical mechanics of fencing/STONITH and the `splitbrain-demo.sh` reproduction this incident is based on.
- [Distributed Systems Failure Modes](../syllabus/10-distributed-systems/distributed-systems-failure-modes.md) — the broader partition-versus-crash distinction this root cause depends on.
