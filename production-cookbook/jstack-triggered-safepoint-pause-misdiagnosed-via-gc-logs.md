---
title: "jstack-Triggered Safepoint Pause Misdiagnosed via GC Logs"
document_type: production-cookbook-entry
domain: jvm
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../handbook/jvm/safepoints-and-stop-the-world-mechanics.md
source: handbook/jvm/safepoints-and-stop-the-world-mechanics.md#production-scenarios
---

# jstack-Triggered Safepoint Pause Misdiagnosed via GC Logs

## Context

A service exhibits an occasional, unexplained latency spike. A monitoring agent periodically requests thread dumps or class metadata from the process for profiling purposes.

## Symptoms

The latency spike has no corresponding GC log entry at the timestamp it occurs, leading the initial investigation to search GC logs for a cause that isn't there.

## Impact

An investigation stalls, potentially indefinitely, searching the wrong data source for a cause that will never appear there, because GC is assumed to be the only trigger for a stop-the-world pause.

## Initial Hypotheses

- A GC event not properly captured in the logs — investigated first, and repeatedly, given the pause's stop-the-world signature.
- A non-GC safepoint operation, triggered by something other than garbage collection — correct, once the investigation widens beyond GC logs.

## Evidence

Correlating the latency spikes against monitoring-agent activity, rather than GC logs, shows the agent periodically requesting thread dumps or class metadata for profiling purposes — each request is itself a real safepoint operation, requiring every thread to stop so their state can be inspected consistently.

## Investigation Timeline

1. **Unexplained latency spikes noticed**, with no GC log entry at the corresponding timestamps.
2. **GC logs searched repeatedly** as the default first hypothesis for any stop-the-world-shaped pause, coming up empty each time.
3. **Investigation widened** to consider non-GC safepoint triggers, once the GC-only assumption stalled progress.
4. **Correlation run against external tooling activity**, identifying the monitoring agent's periodic thread-dump requests as coinciding exactly with the spikes.

## Root Cause

A thread dump — or any operation requiring a consistent view of every thread's state — is itself a real safepoint operation, requiring every thread to stop, regardless of whether garbage collection is involved. "No GC in the logs at that timestamp" does not mean "no stop-the-world pause occurred," because GC is only one of several real safepoint-operation triggers.

## Immediate Mitigation

Reduce the monitoring agent's thread-dump or class-metadata polling frequency to lessen the pause frequency while a longer-term decision is made about the trade-off.

## Permanent Fix

Explicitly account for non-GC safepoint operations — diagnostic tooling, monitoring agents, JMX operations — when investigating unexplained pauses, and evaluate whether the monitoring agent's polling frequency is proportionate to the latency sensitivity of the service it's profiling, adjusting it if not.

## Alternatives Considered

Disabling the monitoring agent entirely to eliminate the pauses. Rejected as trading away real profiling and diagnostic visibility for a latency gain that could likely be achieved with a less frequent polling interval instead.

## Trade-offs

Reducing monitoring-agent polling frequency trades some profiling granularity for fewer safepoint-inducing pauses. Accepted for a latency-sensitive service where even brief, infrequent pauses have a measurable cost.

## Prevention

Any investigation into an unexplained pause with a stop-the-world signature should check all real safepoint-operation triggers — GC, thread dumps, JMX operations, class redefinition — not default to GC logs alone as the only place such a pause could originate.

## Monitoring and Alerts

- Safepoint operation logging (`-Xlog:safepoint` or equivalent) enabled and reviewed as a standing diagnostic source, not brought in only after GC logs fail to explain a pause — this single change would have redirected the investigation immediately rather than after a stalled search.
- Monitoring-agent request frequency correlated directly against latency-spike timestamps as a standard first-pass check for any unexplained pause investigation, given how common external tooling is as an unaccounted safepoint trigger.

## Interview Story

This maps to a "you have unexplained pauses and nothing shows up in GC logs" question. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** occasional, unexplained latency spikes with no corresponding GC log entry stalled an investigation searching GC logs repeatedly.
- **Task:** find the cause after the most natural hypothesis — GC — is exhausted.
- **Action:** widen the investigation beyond GC to other real safepoint triggers; correlate the spikes against external tooling activity rather than only internal JVM logs; identify the monitoring agent's periodic thread-dump requests as the actual trigger.
- **Result:** reduced the monitoring agent's polling frequency to an interval proportionate to the service's latency sensitivity, and adopted safepoint-operation logging as a standing diagnostic tool for any future unexplained pause.

## Staff-Level Discussion

The specific value of this incident is correcting a common but incomplete mental model: many engineers associate "stop-the-world pause" exclusively with garbage collection, when in fact any operation requiring a consistent snapshot of every thread's state — a thread dump, certain JMX operations, class redefinition — triggers the identical mechanism. An investigation anchored on that incomplete model will search GC logs indefinitely and never find the answer, because the answer was never going to be there. This is a specific instance of a broader diagnostic discipline: when a well-understood mechanism (GC) fails to explain an observed symptom with the expected signature, the correct move is questioning whether the mechanism assumed to be the only cause is actually the only cause, not searching harder within the same, already-exhausted data source. A Staff engineer should recognize "GC logs are silent" as evidence against GC specifically, not evidence that no stop-the-world event occurred.

## Related Handbook Chapters

- [Safepoints and Stop-the-World Mechanics](../handbook/jvm/safepoints-and-stop-the-world-mechanics.md) — canonical safepoint-operation-trigger mechanics used here.
