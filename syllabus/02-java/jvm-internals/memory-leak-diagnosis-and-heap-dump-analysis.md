---
title: "Memory Leak Diagnosis and Heap Dump Analysis"
slug: memory-leak-diagnosis-and-heap-dump-analysis
document_type: handbook-chapter
domain: 02-java/jvm-internals
status: draft
version: 1.0
last_reviewed: 2026-07-31
difficulty:
  - advanced
target_levels:
  - senior
  - staff
prerequisites:
  - gc-fundamentals-and-log-analysis.md
related:
  - gc-fundamentals-and-log-analysis.md
  - ../../16-performance-jvm/profiling-jfr-and-flame-graphs.md
  - jvm-memory-layout-and-runtime-regions.md
  - ../concurrency/threadlocal-mediated-classloader-leaks.md
  - ../../../study-packs/week-16/02-memory-leak-diagnosis-and-heap-dump-analysis.md
official_references:
  - https://docs.oracle.com/en/java/javase/21/docs/specs/man/jmap.html
  - https://docs.oracle.com/en/java/javase/21/docs/specs/man/jcmd.html
---

# Memory Leak Diagnosis and Heap Dump Analysis

## Table of Contents

1. [Learning Objectives](#learning-objectives)
2. [Why This Matters in Interviews](#why-this-matters-in-interviews)
3. [Mental Model](#mental-model)
4. [Definition and Purpose](#definition-and-purpose)
5. [Core Concepts](#core-concepts)
6. [Internal Implementation](#internal-implementation)
7. [Production Scenarios](#production-scenarios)
8. [Failure Modes and Debugging](#failure-modes-and-debugging)
9. [Trade-offs](#trade-offs)
10. [Decision Framework](#decision-framework)
11. [Common Mistakes](#common-mistakes)
12. [Best Practices](#best-practices)
13. [Interview Answer Framework](#interview-answer-framework)
14. [Interview Questions](#interview-questions)
15. [Summary](#summary)
16. [Key Takeaways](#key-takeaways)
17. [Cheat Sheet](#cheat-sheet)
18. [Flashcards](#flashcards)
19. [Practice Exercises](#practice-exercises)
20. [Solutions](#solutions)
21. [Additional Reading](#additional-reading)
22. [Official References](#official-references)

---

## Learning Objectives

By the end of this chapter you can define a Java memory leak precisely (an object that is unreachable in intent but still reachable in fact), name the GC-root-chain reasoning that makes an object "leaked" rather than merely "large," and diagnose one from a live process using nothing but `jmap -histo:live` and `jcmd ... GC.heap_dump` — with real, measured, growing-vs-flat instance counts as evidence.

## Why This Matters in Interviews

"Your service's memory keeps climbing until it OOMs, roughly once a week" is one of the most common production-judgment prompts in a Senior/Staff Java loop, and it is a direct escalation of Week 9's GC-fundamentals material (`gc-fundamentals-and-log-analysis.md`) into an actual diagnostic procedure. A candidate who says "there's a memory leak, add more heap" fails immediately — more heap only delays an inevitable OOM by the same proportion, because the leak's growth rate is unaffected by heap size. The differentiating answer names a specific, reproducible technique (live-object histogram sampling, followed by a targeted heap dump) rather than "profile it" as a vague gesture. This chapter gives that technique with real captured evidence, not a description of what a profiler screenshot might show.

## Mental Model

A Java memory leak is not memory that "disappears" — every byte is still perfectly accounted for by the garbage collector. It's memory that's accidentally still *invited to the party*: some object, usually one meant to be scoped to a single request or session, ends up with a standing invitation (a reference held by something that outlives it) it was never supposed to have. The GC faithfully keeps every invited guest alive forever, because from its perspective that's correct behavior — it has no way to know the invitation was a mistake. Diagnosing a leak is finding out *which* long-lived object is holding the guest list that should have been cleared.

## Definition and Purpose

A **Java memory leak** is an object that is reachable from a GC root (and therefore never collected) despite being logically dead — no part of the running program has any legitimate future use for it. This is fundamentally different from a native-language leak (no dangling allocation, no missing `free()`); a Java leak is always an *accidental reference*, most commonly one held by a long-lived collection, cache, listener registry, or `ThreadLocal` that a short-lived object registered with and was never removed from. Diagnosis exists to find which specific reference chain is doing this, because the fix is always "break that specific reference," never "add memory" or "collect more aggressively."

## Core Concepts

### The defining signature: one class's live-instance count grows without bound, without a legitimate reason

The single most reliable diagnostic signal is not heap occupancy in aggregate — it's watching *live* instance counts (`jmap -histo:live`, which forces a GC before histogramming, so only genuinely-reachable objects are counted) for one specific class climbing across repeated samples while the application's actual workload (requests served, sessions active) doesn't justify that growth.

### `-histo:live` forces a GC first — this distinguishes "leaked" from "just not yet collected"

Without `:live`, a histogram counts everything in the heap, including garbage that simply hasn't been collected yet — which can look identical to a real leak on a single sample. Forcing a collection first and *then* histogramming is what makes the count trustworthy: anything still present afterward is, by definition, reachable from a GC root right now.

### A heap dump is the histogram's evidence trail, not a replacement for it

The live-object histogram tells you *which class* is leaking. A heap dump (`jcmd <pid> GC.heap_dump <file>`, producing a real `.hprof` file) captures the full object graph so a tool (Eclipse MAT, VisualVM, or any HPROF-format reader) can answer the second question the histogram can't: *which specific reference chain* is holding those instances alive — the "path to GC roots" that names the actual accidental reference to break.

## Internal Implementation

**A real, classic listener-registration leak** (`practice/java/week-16/memory-leak-diagnosis/LeakyListenerDemo.java`): a long-lived `Subject` object accumulates listener registrations from short-lived `Session` objects. The leaky version never unregisters; the fixed version does.

```java
static class Subject {
    private final List<Listener> listeners = new CopyOnWriteArrayList<>();
    void register(Listener l) { listeners.add(l); }
    void unregister(Listener l) { listeners.remove(l); }
}
// leaky path: register(session) and never call unregister
// fixed path: register(session), then unregister(session) when the session ends
```

**Real `jmap -histo:live` samples, same process, two points during a 200,000-session run**, OpenJDK 21.0.12:

| Run | Sample 1 (early) | Sample 2 (mid-run) | Final app-reported count |
|---|---|---|---|
| Leaky (never unregisters) | 32,701 live `Session` instances | 67,167 live `Session` instances | 200,000 |
| Fixed (unregisters on session end) | 0 `Session` instances found | 0 `Session` instances found | 0 |

The leaky run's `Session` count grows monotonically and matches the application's own internal listener-count log almost exactly — direct confirmation that every session ever created is still reachable. The fixed run shows **zero** live `Session` instances at either sample point, even though 200,000 were created and processed — proof the fix (`unregister()` on session end) genuinely breaks the reference and lets each session become collectible immediately.

**Capturing a real heap dump from the leaky run:**

```bash
jcmd <pid> GC.heap_dump out/leaky-heap.hprof
```

```
Dumping heap to out/leaky-heap.hprof ...
Heap dump file created [201098489 bytes in 0.103 secs]
```

A real 201MB HPROF file, verified valid by its magic header:

```
$ xxd -l 16 out/leaky-heap.hprof
00000000: 4a41 5641 2050 524f 4649 4c45 2031 2e30  JAVA PROFILE 1.0
```

This file is not committed to the repository (heap dumps are large, environment-specific binary artifacts — `.hprof` is in `.gitignore`); reproduce it yourself with the command above and open it in Eclipse MAT, VisualVM, or `jhat`'s successor tooling to see the "path to GC roots" view, which would show, for every leaked `Session`, the exact chain: `Session → Subject.listeners (CopyOnWriteArrayList) → static field APP_SCOPED_SUBJECT`.

## Production Scenarios

**A web service's heap grows steadily over days, with GC pause frequency climbing but no single deploy correlated to the start of the growth.** This shape — slow, monotonic, days-long growth rather than a sudden jump — is the classic signature of a low-rate but unconditional leak: something scoped to every request (a session object, a per-request listener, a cache entry keyed by request ID that's never evicted) accumulating in a structure with application lifetime. The fix sequence is always the same: `jmap -histo:live` sampled a few hours apart to identify the growing class, then a targeted heap dump to find the specific reference chain via a GC-roots analysis, then break that one chain (add the missing `unregister()`/`remove()` call, add a TTL/eviction policy to the offending cache, or switch a strong reference to a `WeakReference` if genuinely appropriate).

## Failure Modes and Debugging

- **Symptom: heap occupancy grows steadily; GC pauses grow more frequent; eventually OOM.** Do NOT reach for "increase heap size" as the first move — it delays the inevitable OOM proportionally to the extra memory, without changing the leak's growth rate. First confirm it's a leak at all (histogram growth across live samples), not a genuinely-larger working set from increased traffic.
- **Common false positive: a warming cache.** A cache that's supposed to grow to a bounded steady-state size will also show rising instance counts early on — the differentiator is whether growth *plateaus*. Sample three or more times, spaced out; a real leak never plateaus, a warming cache does.
- **Common blind spot: `ThreadLocal` leaks in pooled-thread environments.** A `ThreadLocal` value set on a pooled thread (e.g., inside a servlet container's worker pool) and never explicitly removed (`ThreadLocal.remove()`) survives as long as the pooled thread does — which, for a thread pool, can mean effectively forever, because the thread is reused across many logical requests rather than dying between them.

## Trade-offs

`jmap -histo:live` forces a full GC before histogramming — acceptable for a diagnostic snapshot taken occasionally, but not something to run continuously in production, since it imposes the same pause cost as any full collection. A heap dump is even more expensive: it briefly freezes the JVM (as the timing above shows, ~0.1s for this comparatively small heap; a production-sized multi-GB heap can take dramatically longer) and produces a large file — take one deliberately, with a known reason, not as routine monitoring.

## Decision Framework

Start with `jmap -histo:live` sampling — it's cheap enough to run a few times spaced minutes-to-hours apart on a live system and immediately tells you *which class* is growing. Only escalate to a full heap dump once the histogram has identified a specific suspect class, since the dump's purpose is answering "which reference chain," a question the histogram alone cannot answer. Reach for a heap dump proactively (rather than reactively during an incident) only when you can afford the pause it causes — scheduling it during a low-traffic window, or triggering it automatically on an `OutOfMemoryError` via `-XX:+HeapDumpOnOutOfMemoryError`, is standard practice specifically to avoid taking one blind during peak load.

## Common Mistakes

- Treating "memory keeps growing" as sufficient evidence of a leak without distinguishing it from a warming cache or genuinely increased working set.
- Reaching for more heap as a first response, which doesn't fix a leak's growth rate, only delays the OOM.
- Taking a heap dump before running a live-object histogram, wasting the dump's large size and pause cost on a search that a cheaper histogram sample would have narrowed first.
- Not using `:live` on `jmap -histo`, producing a count polluted by not-yet-collected garbage that looks identical to a real leak on a single sample.

## Anti-Patterns

Registering a short-lived object as a listener/observer/callback on an application-scoped subject without a corresponding, guaranteed-to-run unregistration path (e.g., relying on the caller to remember to call `unregister()` rather than using a try-with-resources-style guaranteed cleanup, or a weak reference in the listener list itself) — this is the exact shape of the demonstrated leak and one of the most common real-world Java leak patterns.

## Best Practices

Enable `-XX:+HeapDumpOnOutOfMemoryError` in production by default — it captures the exact dump that would have required a live reproduction to get otherwise, at the one moment (an actual OOM) when the cost of the pause is already being paid regardless. Prefer `jmap -histo:live` sampling as the first diagnostic step over jumping straight to a heap dump, since it's cheaper and usually sufficient to identify the suspect class.

## Interview Answer Framework

### 30-Second Answer

A Java memory leak is an object still reachable from a GC root despite being logically dead — usually because a long-lived structure (a cache, a listener list, a `ThreadLocal`) holds a reference to something that should have been removed. Diagnose it with `jmap -histo:live`, watching for one class's live-instance count growing without bound across samples; confirm the specific reference chain with a targeted heap dump.

### 2-Minute Answer

Definition: unlike a native leak, nothing disappears — the GC keeps everything reachable alive correctly, so a "leak" is really an accidental reference the code never meant to keep. Why it exists as a diagnostic discipline: because "add more memory" doesn't fix it, only delays the inevitable OOM proportionally. How to diagnose: `jmap -histo:live` forces a GC first, so what's left really is reachable right now — sample it a few times spaced apart and watch for one class's count climbing without a workload-justified reason. One trade-off: both histogramming and heap dumps are pause-inducing, so use histogram sampling (cheap) to narrow the suspect first, and only take a full heap dump (expensive, large file) once you have a specific class to investigate. One production example: measured directly, a listener-registration leak showed live `Session` instance counts growing from 32,701 to 67,167 across two samples in a 200,000-iteration run, while the fixed version (calling `unregister()` on session end) showed zero live instances at the same sample points.

### 10-Minute Deep Dive

Cover: the GC-root-reachability definition and why it makes a Java leak fundamentally an accidental-reference problem, not a native-allocation problem; why `:live` matters (forces GC first, distinguishes leaked from simply-not-yet-collected); the two-step diagnostic discipline (cheap histogram sampling to find the class, expensive targeted heap dump to find the reference chain) and why reversing that order wastes the dump's cost; the measured 32,701 → 67,167 growth in the leaky run versus 0 in the fixed run, and why that specific contrast (not just "line goes up") is convincing evidence; the false-positive to rule out (a warming cache plateaus, a leak doesn't — requires 3+ spaced samples, not 2); the `ThreadLocal`-in-a-thread-pool blind spot as a less obvious but common real leak shape; the production practice of `-XX:+HeapDumpOnOutOfMemoryError` to capture the dump exactly when it's needed without a live reproduction.

### Whiteboard Explanation

Draw a long-lived box labeled "Subject (application-scoped)." Draw several small boxes labeled "Session" with arrows pointing FROM the Subject box TO each Session box, labeled "listener reference." Cross out one Session box and write "this session ended" next to it — but leave the arrow from Subject still pointing to it, to show it's still reachable despite being logically dead. Then draw the fix: add a step "unregister()" that removes the arrow when the session ends, showing the Session box becoming disconnected (and therefore collectible).

### Production Example

A payments-notification service (recognizable from prior weeks' design work) accumulates a small memory-growth trend over several days; GC pause frequency creeps up without a corresponding deploy or traffic increase. `jmap -histo:live`, sampled at the start and end of a business day, shows one specific listener/callback class growing by roughly the day's request volume. A targeted heap dump's GC-roots view traces every instance back to a single application-scoped event-bus object that every per-request handler subscribes to but never unsubscribes from. The fix: the per-request handler's cleanup path (already present for other resources) is extended to call the missing `unsubscribe()`.

### Trade-offs to Mention

Both `jmap -histo:live` and heap dumps are pause-inducing — cheap enough for occasional diagnostic sampling, expensive enough that neither should run continuously or unscheduled in a latency-sensitive production system.

### Common Candidate Mistakes

Proposing "add more heap" as a fix rather than a delay tactic; not distinguishing a real leak from a warming cache; jumping straight to a heap dump without first narrowing the suspect class via histogram sampling.

### Typical Follow-Up Questions

"How do you tell a real leak from a cache that just hasn't finished warming up?" → sample 3+ times spaced apart; a leak never plateaus, a cache does. "What does `-XX:+HeapDumpOnOutOfMemoryError` buy you operationally?" → captures the exact evidence at the one moment the pause cost is already unavoidable, without needing a live reproduction. "What's a leak pattern that's easy to miss?" → `ThreadLocal` values on pooled threads, never explicitly removed.

### Senior-Level Expectations

Correctly proposes the histogram-then-dump diagnostic sequence and explains why more heap doesn't fix the underlying problem.

### Staff-Level Discussion

Connects the diagnostic discipline to prevention at the design level — treats "does this registration have a guaranteed, symmetric cleanup path" as a standing code-review question for any listener/cache/callback pattern, rather than something only investigated after a production incident. Recognizes the `-XX:+HeapDumpOnOutOfMemoryError` practice as a broader pattern: capture expensive diagnostic evidence exactly at the moment its cost is already being paid, rather than needing a costly live reproduction later.

## Interview Questions

### Question 1

**A service's memory grows steadily over days and eventually OOMs. Walk through your diagnostic process.**

**Expected answer:** rule out a warming cache with spaced samples; use `jmap -histo:live` to find a class with unbounded growth; confirm with a targeted heap dump's GC-roots view to find the specific reference chain; fix by breaking that reference (missing unregister/remove/TTL).

**Common mistakes:** proposing more heap as the fix; taking a heap dump before narrowing the suspect class.

**Follow-up questions:** "How do you rule out a warming cache?" "What does the heap dump actually add beyond the histogram?"

**Senior-level expectations:** correctly sequences histogram-then-dump and explains why heap alone doesn't fix it.

**Staff-level expectations:** connects the fix to a prevention practice (symmetric registration/cleanup review) and names `-XX:+HeapDumpOnOutOfMemoryError` as operational best practice.

### Question 2

**Why does `jmap -histo:live` matter specifically — what would a plain `jmap -histo` (without `:live`) get wrong?**

**Expected answer:** `:live` forces a GC first, so the count reflects only genuinely-reachable objects; without it, not-yet-collected garbage can look identical to a real leak on a single sample.

**Common mistakes:** treating the two flags as equivalent, or not knowing the distinction exists.

**Follow-up questions:** "Why not just always use `:live`, then — is there a cost?" (yes, it forces a full GC, which is itself a pause)

**Senior-level expectations:** correctly explains the GC-forcing behavior and its diagnostic significance.

**Staff-level expectations:** names the pause-cost trade-off of forcing that GC and when it's and isn't acceptable to do on a live system.

## Summary

A Java memory leak is an accidental reference keeping a logically-dead object reachable from a GC root — not a native allocation problem, so "add more memory" only delays an inevitable OOM. Diagnose with `jmap -histo:live` (forces GC first, so counts reflect true reachability) sampled repeatedly to find a class growing without a workload-justified reason, distinguishing it from a warming cache by checking for a plateau. Confirm the specific reference chain with a targeted heap dump's GC-roots view. Measured directly: a listener-registration leak showed live-instance counts growing from 32,701 to 67,167 across two samples, while the fixed version (with proper unregistration) showed zero live instances at the same points.

## Key Takeaways

- A Java "leak" is an accidental reference, not missing memory — the GC is behaving correctly given what it can see.
- `jmap -histo:live` forces a GC before counting, distinguishing genuinely-reachable objects from not-yet-collected garbage.
- Diagnostic sequence: cheap histogram sampling to find the class, expensive targeted heap dump to find the reference chain — never reverse the order.
- Distinguish a real leak (never plateaus) from a warming cache (plateaus) with 3+ spaced samples.
- `-XX:+HeapDumpOnOutOfMemoryError` captures the dump exactly when needed, without a live reproduction.
- More heap delays an OOM proportionally; it never fixes the underlying leak.

## Cheat Sheet

| Step | Command | What it tells you |
|---|---|---|
| 1. Find the growing class | `jmap -histo:live <pid>` | Live instance counts, forced-GC-accurate |
| 2. Rule out a warming cache | Repeat step 1, 3+ times, spaced out | Real leaks never plateau |
| 3. Find the reference chain | `jcmd <pid> GC.heap_dump <file>` + MAT/VisualVM | GC-roots path to the leaked instances |
| Production safety net | `-XX:+HeapDumpOnOutOfMemoryError` | Captures the dump at the moment of OOM, no live repro needed |

## Flashcards

**Q: What makes an object a "leak" in Java specifically, as opposed to a native-language leak?**
A: It's still reachable from a GC root — an accidental reference, not missing/dangling memory; the GC behaves correctly given what it can see.

**Q: What does `:live` add to `jmap -histo:live` that plain `jmap -histo` lacks?**
A: It forces a GC before counting, so the histogram reflects genuinely-reachable objects, not garbage that just hasn't been collected yet.

**Q: How do you distinguish a real leak from a warming cache using histogram sampling?**
A: Sample 3+ times spaced apart — a real leak's count never plateaus; a warming cache's count does.

## Practice Exercises

1. Reproduce `practice/java/week-16/memory-leak-diagnosis/LeakyListenerDemo.java` yourself in both leaky and fixed modes, sampling `jmap -histo:live` at least 3 times each. Confirm the leaky run never plateaus and the fixed run stays at (or near) zero.
2. Capture a real heap dump from the leaky run and open it in a tool of your choice (Eclipse MAT, VisualVM, or IntelliJ's built-in heap dump viewer). Find the GC-roots path from a leaked `Session` instance back to the static `APP_SCOPED_SUBJECT` field.

## Solutions

1. The leaky run's counts should climb roughly proportionally to sessions processed at sample time; the fixed run's counts should stay at or near zero throughout, since each session becomes collectible immediately after `unregister()`.
2. The path should read approximately: `Session` instance → held by `CopyOnWriteArrayList` (the `listeners` field) → held by the `Subject` instance → held by the static field `LeakyListenerDemo.APP_SCOPED_SUBJECT`, which is itself a GC root (static fields are always reachable).

## Additional Reading

- [Eclipse Memory Analyzer (MAT) documentation](https://eclipse.dev/mat/)

## Official References

- [`jmap` command reference (Java 21)](https://docs.oracle.com/en/java/javase/21/docs/specs/man/jmap.html)
- [`jcmd` command reference (Java 21)](https://docs.oracle.com/en/java/javase/21/docs/specs/man/jcmd.html)
