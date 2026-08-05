---
title: "Feature-Flag Rollout Triggering a Deoptimization Latency Spike"
document_type: production-cookbook-entry
domain: jvm
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../handbook/jvm/jit-tiered-compilation-and-deoptimization.md
source: handbook/jvm/jit-tiered-compilation-and-deoptimization.md#production-scenarios
---

# Feature-Flag Rollout Triggering a Deoptimization Latency Spike

## Context

A hot call site has a single implementation of an interface for its entire history. A feature flag rollout introduces a second implementation of that interface at the same call site.

## Symptoms

The service's p99 latency spikes briefly, with no GC event and no deploy, correlated with the specific feature flag rollout.

## Impact

A brief but real, measurable latency degradation coinciding with a rollout that, from the application's perspective, looks like a purely logical change with no obvious performance dimension.

## Initial Hypotheses

- A GC event coinciding with the rollout by chance — checked and ruled out; no GC activity recorded at the spike's timestamp.
- An unrelated deploy — checked and ruled out; the timing correlates specifically with the feature flag rollout, not any code deployment.
- A JIT deoptimization triggered by a new type appearing at a previously monomorphic call site — correct.

## Evidence

The hot call site had a single implementation of the interface for its entire history before the rollout; the flag rollout introduces a second implementation at that same call site, exactly matching the deoptimization pattern this chapter measures directly.

## Investigation Timeline

1. **Brief p99 latency spike observed**, with no corresponding GC log entry or deployment event.
2. **GC and deploy hypotheses ruled out** by checking their respective logs directly against the spike's timestamp.
3. **Timing correlated specifically against the feature flag rollout schedule**, rather than any code-level change.
4. **Call site reviewed**, confirming the flag rollout introduced a second implementation type where only one had existed before.

## Root Cause

A call site the JIT had speculatively optimized around a single observed type gets a second type introduced by the flag rollout, forcing a deoptimization-and-recompilation cycle for every affected thread hitting that code path around the same time.

## Immediate Mitigation

None needed — the spike is brief and one-time per affected thread, resolving on its own as the JIT recompiles the call site against the new, polymorphic type mix.

## Permanent Fix

The fix isn't code-level — polymorphism at that call site may be entirely legitimate and intended. It's operationally accepting a brief, one-time recompilation cost during rollout, or, if the cost is unacceptable, warming the JIT against the full expected type mix (both implementations) before the flag reaches production traffic.

## Alternatives Considered

Redesigning the call site to avoid polymorphism entirely. Rejected as unnecessary — the polymorphism itself is a legitimate design choice for the feature; the cost is a one-time JIT recompilation event, not an ongoing architectural problem worth redesigning around.

## Trade-offs

Pre-warming the JIT against the full expected type mix before the flag reaches production traffic adds rollout-process complexity and time. Accepted only where the brief deoptimization spike's cost is genuinely unacceptable for the specific service's latency requirements; otherwise, tolerating the brief, one-time cost is the simpler choice.

## Prevention

Any feature-flag rollout introducing a new implementation type at a previously monomorphic hot call site should be flagged during rollout planning as a known, brief JIT-recompilation cost, so the on-call team isn't surprised by an unexplained latency spike with no GC or deploy correlation.

## Monitoring and Alerts

- JIT deoptimization events tracked and correlated against feature-flag rollout timing specifically, giving on-call a direct, mechanical explanation for latency spikes with no GC or deploy correlation, rather than requiring investigation each time this pattern recurs.
- A rollout-planning checklist item for any flag introducing a new implementation type at a hot call site, flagging the expected brief recompilation cost proactively rather than leaving it to be rediscovered as an unexplained incident.

## Interview Story

This maps to "latency spiked with no GC and no deploy, what happened" — a question testing awareness of JIT deoptimization as a real production mechanism. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** a brief p99 latency spike coincided with a feature flag rollout, with no GC event or deploy to explain it.
- **Task:** find the cause when the two most common latency-spike explanations are both ruled out.
- **Action:** correlate the spike's timing specifically against the flag rollout; review the affected call site, finding the rollout introduced a second implementation type where only one existed before; recognize the deoptimization-and-recompilation mechanism this triggers.
- **Result:** accepted the brief, one-time recompilation cost as the correct trade-off for legitimate polymorphism, with pre-warming reserved for cases where even that brief cost is unacceptable.

## Staff-Level Discussion

This incident broadens the mental model of what can cause a stop-the-world-adjacent performance event beyond garbage collection: JIT deoptimization is triggered by the compiler's own speculative assumptions being invalidated, and a feature flag — a purely logical, application-level change — can trigger it just as effectively as a code deployment, because the JIT doesn't distinguish "new code was deployed" from "a new type started appearing at runtime." This is a specific instance of a broader principle worth internalizing: any change that alters the *runtime shape* of data flowing through a hot path (a new subtype, a new enum value, a newly-exercised code branch) can have JIT-level performance consequences invisible to a purely functional or logical review of the change. A Staff engineer planning any feature-flag rollout that introduces new runtime behavior at a hot, previously stable call site should factor in this class of cost explicitly, rather than treating flag rollouts as performance-neutral by default.

## Related Handbook Chapters

- [JIT Tiered Compilation and Deoptimization](../handbook/jvm/jit-tiered-compilation-and-deoptimization.md) — canonical speculative-optimization and deoptimization mechanics used here.
