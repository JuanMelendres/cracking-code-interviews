---
title: "WeakHashMap Cache Evicting Far Earlier Than Expected"
document_type: production-cookbook-entry
domain: jvm
status: draft
last_updated: 2026-09-12
related_handbook:
  - ../syllabus/02-java/jvm-internals/gc-roots-reachability-and-reference-strength.md
source: syllabus/02-java/jvm-internals/gc-roots-reachability-and-reference-strength.md#production-scenarios
---

# WeakHashMap Cache Evicting Far Earlier Than Expected

## Context

A cache is implemented with `WeakHashMap`, expected to hold entries "as long as memory allows."

## Symptoms

The cache empties out far sooner than expected under normal operation, with plenty of free heap available.

## Impact

Cache-dependent code paths repeatedly miss and recompute or re-fetch data that was expected to remain cached, under no actual memory pressure.

## Initial Hypotheses

- A memory-pressure-driven eviction — checked, heap usage stays well below any pressure threshold when entries disappear.
- A cache-sizing bug — checked, no explicit size limit was configured.
- `WeakHashMap`'s actual eviction trigger (key reachability, not memory pressure) is being misunderstood — correct.

## Evidence

Entries disappear from the cache in direct correlation with their keys becoming otherwise unreachable, not with any heap-occupancy threshold.

## Investigation Timeline

1. Unexpectedly high cache-miss rate observed with ample free heap.
2. Memory-pressure and sizing-bug hypotheses ruled out via heap metrics and configuration review.
3. Entry-eviction timing cross-checked against key-reachability lifetime rather than heap occupancy, confirming the actual trigger.

## Root Cause

`WeakHashMap` clears entries as soon as the key becomes otherwise unreachable, with no memory-pressure consideration at all — it's not a memory-sensitive cache mechanism, and using it as one produces exactly this symptom.

## Immediate Mitigation

None operationally urgent — no correctness violation, only an unintended cache-hit-rate regression; treat as a design defect to fix rather than an active incident to contain.

## Permanent Fix

Switch to a genuinely memory-pressure-aware cache built on `SoftReference` (or, more commonly in practice, a purpose-built caching library with its own eviction policy).

## Alternatives Considered

Tuning heap size or GC settings to reduce eviction frequency — rejected, since the clearing behavior is a documented property of `WeakReference`'s reachability semantics, not a GC-tunable behavior at all.

## Trade-offs

A purpose-built caching library adds a dependency and configuration surface `WeakHashMap` doesn't have — accepted, since it's the only way to get memory-pressure-aware eviction with a documented, tunable policy.

## Prevention

Reserve `WeakHashMap` for its actual intended use (associating metadata with a key's lifecycle, e.g., canonicalizing mappings) and never as a stand-in for a memory-sensitive cache.

## Monitoring and Alerts

- Cache hit-rate dashboards, since this failure mode surfaces as a silent hit-rate regression rather than an error or crash.
- A design-review checklist flag on any new `WeakHashMap` usage, requiring explicit justification that key-reachability eviction (not memory pressure) is the actually-desired behavior.

## Interview Story

Present as a representative scenario unless personally lived through an equivalent misconfiguration.

- **Situation:** a cache built on `WeakHashMap` was evicting far more aggressively than expected.
- **Task:** determine whether this was a memory-pressure problem or a design mismatch.
- **Action:** correlated eviction timing against key-reachability lifetime rather than heap occupancy, confirming `WeakHashMap`'s actual eviction trigger.
- **Result:** replaced it with a `SoftReference`-based (or purpose-built) cache with genuine memory-pressure-aware eviction.

## Staff-Level Discussion

This is a common misunderstanding of weak references, and the organizational fix is a review-checklist item, not a one-off code change: any use of `WeakHashMap` as a cache should be treated as a design-review flag until it's confirmed the intended eviction trigger really is key-reachability, not memory pressure — the two are easy to conflate and produce very different operational behavior.

## Related Handbook Chapters

- [GC Roots, Reachability, and Reference Strength](../syllabus/02-java/jvm-internals/gc-roots-reachability-and-reference-strength.md) — the canonical reference-strength semantics behind this incident.
