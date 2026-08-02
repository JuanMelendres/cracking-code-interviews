---
title: "Week 19 Mock — JVM Internals Technical Round (45 min)"
week: 19
document_type: study-pack-mock
status: draft
last_reviewed: 2026-08-02
---

# Week 19 Mock — JVM Internals Technical Round (45 min)

**Target role:** Senior/Staff Backend Engineer · **Duration:** 45 minutes · **Format:** self-recorded or with a partner, candidate/evaluator sections hard-separated below.

## Candidate Section

Answer each question aloud, unprompted, before checking the evaluator section. Record yourself — the goal is fluent, structured delivery, not just a correct answer typed out.

1. **(6 min)** A `WeakHashMap`-based cache empties much faster than expected, with plenty of heap free. What's going on?
2. **(6 min)** A service migrates from G1 to ZGC and initially sees worse p99 under peak load. What would you check?
3. **(6 min)** A service shows an unexplained 2ms latency spike with no corresponding GC log entry. Walk through your investigation.
4. **(6 min)** A team's memory estimate for a linked data structure (declared-field-size sum) significantly undershoots real production usage. What's the likely gap?
5. **(6 min)** A container with memory limit set equal to `-Xmx` gets OOMKilled despite heap usage never approaching the max. Diagnose it.
6. **(6 min, whiteboard)** Sketch how escape analysis and scalar replacement let the JIT eliminate an allocation entirely, and explain what disables it.
7. **(9 min)** Free-form: pick any two of this week's six topics and explain how they interact in a single real production system (e.g., why a service migrating to ZGC for latency reasons should also re-check its container memory-limit headroom, since both topics touch what lives outside `-Xmx`'s accounting).

## Evaluator Section

*(Do not read before completing the candidate section.)*

### Question 1 — WeakHashMap cache emptying fast

**Ideal answer outline:** `WeakHashMap` clears entries immediately upon otherwise-unreachability, with no memory-pressure consideration — not a pressure-aware caching mechanism. Recommend `SoftReference`-backed storage instead if pressure-aware retention was actually wanted.
**Common weak answers:** assuming `WeakHashMap` behaves like a memory-aware LRU cache.
**Pass signal:** correctly identifies the immediate-clearing behavior as the (correct, not buggy) cause.
**Borderline signal:** senses something's off but can't name the specific weak-vs-soft distinction.
**Fail signal:** treats this as a bug in `WeakHashMap` itself.

### Question 2 — ZGC migration, worse p99

**Ideal answer outline:** check for allocation-stall events (`-Xlog:gc` or `-Xlog:safepoint`), not just pause duration — likely insufficient heap headroom re-provisioned for the concurrent collector's continuous-reclamation model.
**Common weak answers:** assuming ZGC "doesn't work" for this workload.
**Pass signal:** correctly identifies allocation stalls as the mechanism to check.
**Borderline signal:** suspects "something about the new collector" without naming the specific mechanism.
**Fail signal:** proposes reverting to G1 with no diagnosis.

### Question 3 — Unexplained pause, no GC log entry

**Ideal answer outline:** check `-Xlog:safepoint`, not just `-Xlog:gc` — the pause may be a real, non-GC safepoint operation (thread dump, deoptimization, class redefinition).
**Common weak answers:** assuming the cause is entirely unrelated to the JVM without checking the safepoint log.
**Pass signal:** correctly proposes checking the safepoint log specifically.
**Borderline signal:** eventually gets there after ruling out several unrelated causes first.
**Fail signal:** never considers a non-GC JVM-internal cause.

### Question 4 — Memory estimate undershoots

**Ideal answer outline:** the estimate likely omitted object header overhead (12-16 bytes/object) and reference-field cost — for small objects, this overhead can be a majority of real footprint.
**Common weak answers:** assuming a leak rather than an estimation-methodology error.
**Pass signal:** correctly identifies header and reference-field overhead as the likely gap.
**Borderline signal:** senses "there's more overhead than I accounted for" without naming the specific components.
**Fail signal:** no explanation beyond "memory is complicated."

### Question 5 — Container OOMKilled despite heap headroom

**Ideal answer outline:** `-Xmx` doesn't bound total process memory — check thread stacks, metaspace, code cache, and direct-buffer usage via Native Memory Tracking.
**Common weak answers:** assuming the heap configuration alone should determine the container limit.
**Pass signal:** correctly identifies non-heap regions and proposes NMT as the diagnostic tool.
**Borderline signal:** knows "there's more to it than heap" without naming NMT or specific regions.
**Fail signal:** insists the container limit should equal `-Xmx` with no further investigation.

### Question 6 — Whiteboard: escape analysis

**Ideal answer outline:** draws a method boundary with a non-escaping object fully contained inside (scalar-replaced) versus one crossing the boundary (returned/stored, real allocation required); explains that returning the object, storing it, or passing it to an un-inlined call disables the optimization.
**Pass signal:** correctly draws and narrates both cases and names at least one specific disabling change.
**Borderline signal:** draws the concept but can't name a concrete disabling change.
**Fail signal:** can't explain why some objects are eliminated and others aren't.

### Question 7 — Free-form cross-topic synthesis

**Pass signal:** picks a genuine interaction (e.g., ZGC migration and off-heap/container sizing both touching what `-Xmx` doesn't cover; or compressed oops' ~32GB ceiling interacting with a large-heap ZGC deployment's own sizing decisions) and reasons through it precisely.
**Fail signal:** describes two topics separately with no real connective insight.

## Scoring Rubric

Same 1–5 scale and pass threshold (average ≥ 3.5, no score below 2) as Weeks 13–18's mocks — see `study-packs/week-13/08-week-13-mock-interview.md` for the full rubric description.
