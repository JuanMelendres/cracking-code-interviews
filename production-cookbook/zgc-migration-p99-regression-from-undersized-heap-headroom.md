---
title: "ZGC Migration p99 Regression From Undersized Heap Headroom"
document_type: production-cookbook-entry
domain: jvm
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../syllabus/02-java/jvm-internals/zgc-and-shenandoah-concurrent-collection.md
source: handbook/jvm/zgc-and-shenandoah-concurrent-collection.md#production-scenarios
---

# ZGC Migration p99 Regression From Undersized Heap Headroom

## Context

A latency-sensitive service migrates from G1 to ZGC, expecting uniformly better tail latency. Its heap was sized for G1's evacuation-pause model, with headroom calculated against G1's behavior.

## Symptoms

Under peak load after the migration, the service initially sees worse p99 behavior than it had under G1, contrary to expectations.

## Impact

A collector migration undertaken specifically to improve tail latency instead produces a regression under exactly the conditions — peak load — it was meant to help with.

## Initial Hypotheses

- ZGC simply doesn't suit this workload — a natural first reaction, but treated as a hypothesis to verify rather than an accepted conclusion.
- A heap-sizing mismatch between the old and new collector's models — investigated via allocation-rate monitoring and safepoint logging.

## Evidence

Investigation using `-Xlog:safepoint` and allocation-rate monitoring finds real allocation-stall events occurring specifically during peak traffic — the service's heap was sized for G1's evacuation-pause model, not against a concurrent collector's need for continuous reclamation headroom.

## Investigation Timeline

1. **Worse p99 observed under peak load** immediately following the G1-to-ZGC migration.
2. **"ZGC doesn't work for us" considered as a hypothesis**, rather than accepted outright.
3. **Safepoint logs and allocation-rate metrics inspected**, revealing allocation-stall events correlated specifically with peak-traffic windows.
4. **Heap headroom compared against ZGC's requirements**, finding it was calculated against G1's evacuation-pause model rather than a concurrent collector's continuous-reclamation needs.

## Root Cause

The heap was provisioned with headroom sized for G1's behavior, not for ZGC's need for continuous reclamation headroom during concurrent collection. Under peak allocation rate, the undersized margin produced real allocation stalls — a capacity problem, not evidence that ZGC is unsuitable for the workload.

## Immediate Mitigation

None beyond confirming the mechanism — the allocation stalls resolve only once heap margin is corrected; there is no safe interim workaround that doesn't involve adjusting capacity.

## Permanent Fix

Provision additional heap margin specifically for ZGC's concurrent-collection needs, rather than reverting the collector choice.

## Alternatives Considered

Reverting to G1 based on the initial p99 regression. Rejected once the allocation-stall root cause was confirmed via the safepoint log — the actual remediation is a straightforward capacity adjustment, not a collector reversal that would have abandoned a genuinely better fit for the workload based on a misdiagnosed cause.

## Trade-offs

Additional heap margin costs more memory per instance. Accepted, since it's the actual requirement for ZGC to deliver its intended benefit for this workload, not a wasted cost.

## Prevention

Any collector migration should be accompanied by an explicit review of heap-sizing assumptions inherited from the previous collector — headroom calculated for one collector's pause model doesn't automatically transfer to a different collector with a different reclamation model.

## Monitoring and Alerts

- Allocation-stall events tracked directly via `-Xlog:safepoint` or equivalent, alerted on correlation with peak-traffic windows — this is the precise, mechanical signal that redirected the investigation away from "ZGC doesn't work" and toward the real, fixable cause.
- Heap headroom margin tracked as its own metric relative to peak allocation rate, reviewed explicitly as part of any future collector migration rather than assumed to carry over unchanged.

## Interview Story

This maps to a "we migrated collectors and latency got worse, why" question. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** a G1-to-ZGC migration intended to improve tail latency instead produced worse p99 under peak load.
- **Task:** determine whether ZGC was genuinely unsuited to the workload or something else was wrong.
- **Action:** treat "ZGC doesn't work for us" as a hypothesis to verify, not a conclusion; use safepoint logs and allocation-rate monitoring to find the actual mechanism; trace it to heap headroom sized for the previous collector's model.
- **Result:** provisioned additional heap margin for ZGC's concurrent-collection needs, resolving the regression without abandoning a genuinely better-fitting collector.

## Staff-Level Discussion

The most valuable move in this incident is resisting the tempting, premature conclusion — "we tried ZGC and it made things worse" — in favor of verifying the actual mechanism first. A collector migration changes multiple things at once (pause model, concurrent-work overhead, memory-headroom requirements), and attributing a regression to "the wrong collector choice" without isolating which specific change caused it risks reverting a genuinely correct architectural decision because of an unrelated, fixable capacity oversight. This generalizes to any infrastructure migration: a regression immediately following a change is evidence the change is implicated, not evidence about which specific aspect of the change is at fault, and a Staff engineer's role is insisting on that distinction before a costly reversal decision is made.

## Related Handbook Chapters

- [ZGC and Shenandoah Concurrent Collection](../syllabus/02-java/jvm-internals/zgc-and-shenandoah-concurrent-collection.md) — canonical concurrent-collector headroom and allocation-stall mechanics used here.
