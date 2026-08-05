---
title: "Unconditional Heap Growth and Memory Leak Diagnosis"
document_type: production-cookbook-entry
domain: jvm
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../handbook/jvm/memory-leak-diagnosis-and-heap-dump-analysis.md
source: handbook/jvm/memory-leak-diagnosis-and-heap-dump-analysis.md#production-scenarios
---

# Unconditional Heap Growth and Memory Leak Diagnosis

## Context

A long-running web service holds several structures with application lifetime: session objects, per-request listeners, and a cache keyed by request ID. Any one of these accumulating references it should be releasing produces the same outward symptom — heap that only grows.

## Symptoms

Heap usage climbs steadily over days. GC pause frequency climbs in step with it. No single deploy correlates with the start of the growth — it is not a step-change from one release, but a slow, monotonic trend.

## Impact

Rising GC pause frequency degrades latency for all traffic on the affected instance well before an eventual `OutOfMemoryError` or forced restart, and the lack of a deploy correlation makes the leak easy to miss during routine release review.

## Initial Hypotheses

- A recent deploy introduced the growth — checked and ruled out; the growth trend has no discontinuity aligned with any release boundary.
- Legitimate traffic growth explains rising memory use — checked against traffic metrics, which show no corresponding volume increase.
- An unconditional, low-rate leak in a structure with application lifetime — correct, and the working hypothesis from the shape of the graph alone.

## Evidence

The growth shape — slow, monotonic, days-long, with no deploy correlation — is the signature this chapter identifies directly as a low-rate but unconditional leak: something scoped to every request accumulating in a structure that never gets scoped back down, rather than a one-time regression from a specific change.

## Investigation Timeline

1. **Trend noticed.** GC pause frequency and heap-after-GC baseline both trending upward over a multi-day window, with no matching deploy.
2. **Coarse sampling.** `jmap -histo:live` run a few hours apart to compare live-object counts by class and identify which class's instance count is growing without bound.
3. **Targeted capture.** Once the growing class is identified, a heap dump taken specifically to analyze that class's retained references.
4. **Root-cause chain isolated.** A GC-roots analysis on the dump traces the specific reference chain keeping the growing instances alive — the one path that should have been broken (an `unregister()`/`remove()` call that never fires, or a cache entry with no eviction policy).

## Root Cause

A structure with application lifetime (a listener registry, a session map, or a request-keyed cache) accumulates entries because the code path that should release them either never runs or never existed for a subset of cases (a missing `unregister()`, or a cache with no TTL/eviction policy).

## Immediate Mitigation

A scheduled restart of the affected instance(s) to reset heap occupancy while the specific reference chain is still being isolated — a stopgap, not a fix, since the underlying accumulation resumes immediately after restart.

## Permanent Fix

Break the specific reference chain identified in the GC-roots analysis: add the missing `unregister()`/`remove()` call, add a TTL or size-bounded eviction policy to the offending cache, or — only where genuinely appropriate — switch a strong reference to a `WeakReference` so the collector can reclaim it once nothing else holds it.

## Alternatives Considered

Increasing heap size to buy more runway before the next restart. Rejected as a fix — it lengthens the interval between symptoms without addressing the accumulation, and larger heaps mean longer GC pauses once collection does happen, trading a more frequent small problem for a less frequent larger one.

## Trade-offs

A `WeakReference` fix (where applicable) removes the leak but makes the referenced object's lifetime implicit and collector-dependent rather than explicit — acceptable only when the object is genuinely a cache-style, safely-droppable entry rather than something the application logically owns.

## Prevention

Any structure with application lifetime that is populated per-request or per-session should be reviewed for an explicit removal path or bounded size at the time it is introduced, not discovered retroactively from a growth graph.

## Monitoring and Alerts

- Heap-after-GC baseline trend (not instantaneous heap usage, which is noisy) as the primary leak signal — alert on a sustained upward trend over a multi-day window rather than a single-point threshold, since the defining shape of this failure is slow and monotonic.
- GC pause frequency as a secondary, correlated signal, since a growing live-object set means more work for every collection.
- Per-class live-object counts from periodic `jmap -histo:live` sampling (or an equivalent low-overhead JFR-based mechanism) to shorten the time between "heap is growing" and "this specific class is growing."

## Interview Story

This maps to a direct "walk me through diagnosing a slow memory leak in production" question. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** heap usage and GC pause frequency both trending upward over days, with no deploy to blame.
- **Task:** find the specific leaking structure without being able to reproduce the growth locally in a short session.
- **Action:** rule out a deploy-triggered regression and traffic growth using existing metrics; use coarse `jmap -histo:live` sampling to find the growing class; take a targeted heap dump and trace the retaining reference chain via GC-roots analysis.
- **Result:** identified the specific missing `unregister()`/eviction gap and shipped the fix, converting a recurring restart cycle into a one-time code change.

## Staff-Level Discussion

The specific fix here — one missing `unregister()` call, one missing eviction policy — is small, but the diagnostic sequence (coarse sampling before a targeted heap dump, trend-based alerting instead of threshold-based) is what actually generalizes across the organization. A team that only alerts on absolute heap usage will catch this leak only once it is already causing `OutOfMemoryError`s; a team that alerts on the *trend* catches it while it is still a minor, low-priority ticket. The Staff-level judgment call is recognizing that the fix's cost is trivial once found, so the organization's real leverage point is shortening time-to-detection, not the fix itself — which argues for investing in the trend-based monitoring described above as a standing platform capability rather than re-deriving it per incident.

## Related Handbook Chapters

- [Memory Leak Diagnosis and Heap Dump Analysis](../handbook/jvm/memory-leak-diagnosis-and-heap-dump-analysis.md) — canonical `jmap`/heap-dump/GC-roots methodology used here.
