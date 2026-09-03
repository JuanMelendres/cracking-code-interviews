---
title: "Gradual Latency Degradation From an Unbounded Cache and Growing Old Generation"
document_type: production-cookbook-entry
domain: jvm
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../syllabus/02-java/jvm-internals/gc-fundamentals-and-log-analysis.md
source: handbook/jvm/gc-fundamentals-and-log-analysis.md#production-scenarios
---

# Gradual Latency Degradation From an Unbounded Cache and Growing Old Generation

## Context

A service holds an in-memory cache with no eviction policy — no TTL, no size bound — as one of its long-lived, application-scoped structures.

## Symptoms

The service's p99 request latency, stable for months, begins climbing gradually over several days with no deployment or traffic-pattern change. GC logs show young-collection pause duration is unchanged, but young collections are becoming more frequent, and mixed collections — rare historically — are now occurring every few minutes with rising duration each time.

## Impact

User-facing latency degrades continuously, eventually crossing the service's SLO, without any single triggering event to investigate.

## Initial Hypotheses

- A recent code change — checked and ruled out; no deploy in the affected window.
- Increased traffic — checked and ruled out; request rate is flat.
- A slow memory leak in application code causing the old generation to fill and mixed collections to work harder each time — correct, confirmed via heap dump comparison over time.

## Evidence

Two heap dumps taken 48 hours apart show the same object type — a cache entry class — growing in retained count with no corresponding eviction. The GC log's post-mixed-collection occupancy trend is monotonically increasing across the observation window.

## Investigation Timeline

1. **Gradual p99 climb noticed**, with no matching deploy or traffic change.
2. **Deploy and traffic hypotheses ruled out** against deployment history and request-rate metrics, both flat.
3. **GC log pattern examined**: young-collection pause duration unchanged, but frequency rising, and previously rare mixed collections now occurring every few minutes with growing duration each time.
4. **Heap dumps compared 48 hours apart**, isolating the specific growing object type — a cache entry class with no corresponding eviction.

## Root Cause

An unbounded, or incorrectly keyed, in-memory cache is retaining entries indefinitely. As the retained set grows, more of it survives each young collection, more gets promoted, old-generation occupancy climbs, and each mixed collection has to scan and compact more live data — directly explaining both the rising mixed-collection frequency and their individually climbing duration.

## Immediate Mitigation

Restart the affected instances to reclaim the accumulated retained objects and buy time.

## Permanent Fix

Fix the cache's eviction policy — a missing TTL or size bound on the specific cache identified in the heap dump — not GC tuning; no heap size or pause-time-goal change addresses a genuine leak.

## Alternatives Considered

Increasing heap size. Rejected as treating the symptom — a genuinely unbounded cache eventually exhausts any heap size, just more slowly.

## Trade-offs

None — this is a straightforward application-level bug, not a GC tuning trade-off; the GC log was the diagnostic tool, not the thing being tuned.

## Prevention

Any long-lived cache or collection should have an explicit bound — size or TTL — reviewed at code-review time. A GC log dashboard alerting on a sustained rise in post-mixed-collection occupancy would have caught this days earlier than p99 latency did.

## Monitoring and Alerts

- Post-mixed-collection occupancy trend as a standing GC log dashboard metric, alerted on a sustained rise rather than only noticed once p99 latency itself degrades — this signal moves earlier and more directly than the eventual user-facing symptom.
- Mixed-collection frequency as its own tracked metric; a previously rare event becoming regular is itself an anomaly worth alerting on, independent of whether latency has yet crossed an SLO.

## Interview Story

This maps to a "p99 latency creeping up over days with no deploy, walk through diagnosing it" question. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** a stable service's p99 latency began climbing gradually, with GC logs showing rising mixed-collection frequency and duration.
- **Task:** find the cause without a deploy or traffic change to anchor the investigation.
- **Action:** rule out deploy and traffic hypotheses using existing history; read the GC log pattern directly to distinguish a leak signature from a tuning problem; confirm via two heap dumps taken 48 hours apart which specific object type is growing.
- **Result:** fixed the missing eviction policy on the identified cache, and added a GC-occupancy-trend alert to catch the same failure mode days earlier next time.

## Staff-Level Discussion

The most valuable move in this incident is refusing the GC-tuning framing: an unbounded cache is an application-level correctness bug, and no amount of heap sizing or pause-time-goal adjustment fixes a structure that never releases what it holds — tuning would only change how many days it takes to reach the same symptom. The GC log here functions purely as a diagnostic instrument, not the thing under repair, which is a distinction worth stating explicitly in an interview: recognizing when "the JVM is doing something concerning" is actually "the application is asking too much of the JVM" prevents wasted effort on the wrong layer of the stack. The monitoring fix (alerting on occupancy trend, not just latency) generalizes past this one cache — it catches the entire class of "something in this process is unboundedly growing" days before it becomes user-visible.

## Related Handbook Chapters

- [GC Fundamentals and Log Analysis](../syllabus/02-java/jvm-internals/gc-fundamentals-and-log-analysis.md) — canonical GC log reading and diagnostic-checklist methodology used here.
- [Memory Leak Diagnosis and Heap Dump Analysis](../syllabus/02-java/jvm-internals/memory-leak-diagnosis-and-heap-dump-analysis.md) — the heap dump comparison technique used to isolate the growing object type.
