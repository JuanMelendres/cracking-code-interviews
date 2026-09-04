---
title: "GC Roots, Reachability, and Reference Strength"
slug: gc-roots-reachability-and-reference-strength
document_type: handbook-chapter
domain: 02-java/jvm-internals
status: draft
version: 1.0
last_reviewed: 2026-08-02
topic_id: T-303
mastery_levels_covered: [L1, L2, L3, L4]
difficulty:
  - intermediate
  - advanced
target_levels:
  - senior
  - staff
prerequisites:
  - gc-fundamentals-and-log-analysis.md
related:
  - gc-fundamentals-and-log-analysis.md
  - memory-leak-diagnosis-and-heap-dump-analysis.md
  - ../../../study-packs/week-19/01-gc-roots-reachability-and-reference-strength.md
official_references:
  - https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/ref/package-summary.html
---

# GC Roots, Reachability, and Reference Strength

> **Topic register:** T-303 (GC fundamentals: roots, reachability, generational, IWI 6.9) · Core tier · Very High interview frequency [VH]
> **Scope note:** `gc-fundamentals-and-log-analysis.md` (Week 9) already covers G1's region-based mechanics (young/mixed/full collections, humongous allocations, GC-log reading) in depth — its own topic-register line credits T-303 alongside T-306, but its actual content is G1-implementation-centric, not a treatment of GC roots, the reachability algorithm, or the reference-strength hierarchy as topics in their own right. This chapter owns that specifically missing ground: what "reachable" formally means, how the mark phase actually walks the object graph from roots, the four reference-strength levels and their real, distinct clearing behavior, and the generational hypothesis that justifies generational collection as a strategy — independent of any specific collector's implementation.

## Table of Contents

1. [Learning Objectives](#learning-objectives)
2. [Why This Matters in Interviews](#why-this-matters-in-interviews)
3. [Level 1 — Foundation](#level-1--foundation)
4. [Level 2 — Working Knowledge](#level-2--working-knowledge)
5. [Mental Model](#mental-model)
6. [Definition and Purpose](#definition-and-purpose)
7. [Core Concepts](#core-concepts)
8. [Internal Implementation](#internal-implementation)
9. [Production Scenarios](#production-scenarios)
10. [Failure Modes and Debugging](#failure-modes-and-debugging)
11. [Trade-offs](#trade-offs)
12. [Decision Framework](#decision-framework)
13. [Common Mistakes](#common-mistakes)
14. [Anti-Patterns](#anti-patterns)
15. [Best Practices](#best-practices)
16. [Interview Answer Framework](#interview-answer-framework)
17. [Interview Questions](#interview-questions)
18. [Summary](#summary)
19. [Key Takeaways](#key-takeaways)
20. [Cheat Sheet](#cheat-sheet)
21. [Flashcards](#flashcards)
22. [Practice Exercises](#practice-exercises)
23. [Solutions](#solutions)
24. [Additional Reading](#additional-reading)
25. [Official References](#official-references)

---

## Learning Objectives

By the end of this chapter you can define reachability precisely in terms of GC roots and graph traversal, explain what distinguishes each of Java's four reference-strength levels (strong, soft, weak, phantom), and cite a real, executed demonstration of all four's actual clearing behavior — including a phantom reference's real enqueueing via a `ReferenceQueue`, the mechanism behind `DirectByteBuffer` cleanup.

## Why This Matters in Interviews

"What makes an object eligible for garbage collection" is one of the most commonly asked JVM questions, and it separates candidates who say "when it's no longer referenced" (imprecise — referenced by *what*, exactly?) from those who can name the actual GC roots (thread stacks, static fields, JNI references, and a few others) and describe reachability as a formal graph-traversal property from those roots, not a vague notion of "not being used." The reference-strength hierarchy (`WeakReference`, `SoftReference`, `PhantomReference`) is a related, frequently under-prepared area — many candidates know `WeakHashMap` exists without being able to explain precisely when and why its entries actually disappear.

## Level 1 — Foundation

**An object becomes eligible for garbage collection when nothing your program is actively using can trace a path back to it anymore** — not simply "when it's no longer used," but specifically when no chain of references connects it back to something the program is genuinely holding onto right now (a local variable in a running method, a static field). Think of it like a family tree: if a branch has no living connection back to the root, that whole branch is gone, no matter how many objects within that branch still point to each other.

This is why two objects that only reference each other, but nothing else references either of them, are both still collected correctly — neither one has a path back to anything the program is actually using, even though they "reference" each other.

## Level 2 — Working Knowledge

The everyday practical touchpoint for this topic is `WeakHashMap` and `WeakReference`: a normal `Map` holds a strong reference to its keys, meaning an entry never disappears on its own even if nothing else in the program still cares about that key — a common, subtle cause of a slow memory leak in a long-lived cache. A `WeakHashMap` holds its keys weakly, so an entry can be automatically removed once nothing else references that key anymore, which is the practical, everyday tool for building a cache that doesn't accidentally keep every entry alive forever.

**The working rule**: use a plain `HashMap` for normal lookups; reach for `WeakHashMap` specifically when you want entries to disappear automatically once their key is no longer used elsewhere in the program — for instance, caching metadata about objects that are otherwise expected to become eligible for collection.

## Mental Model

Picture the heap as a directed graph, and GC roots as the *only* legitimate starting points for asking "is this object alive." An object is reachable if and only if there's a path from at least one root, following reference fields, that reaches it — an object with a thousand incoming references is exactly as dead as one with zero, the moment none of those incoming paths trace back to a root. The reference-strength hierarchy adds a second axis on top of plain reachability: not "is there a path from a root," but "how strongly does the collector respect a path that goes through *this specific kind* of reference" — a strong reference is followed unconditionally; a weak reference is followed for reachability purposes only until the collector decides to clear it; a phantom reference isn't followed for producing a usable object reference at all, only for post-collection notification.

## Definition and Purpose

**GC roots** are the fixed set of reference locations the collector treats as inherently alive without needing further justification — including active thread stacks (local variables and method parameters currently in scope), static fields of loaded classes, JNI references held by native code, and a few JVM-internal roots (e.g., objects under active synchronization). **Reachability** is a computed property, not a static one: an object is reachable if a chain of strong references exists from at least one GC root to that object, discovered by the collector's mark phase actually traversing the object graph starting from the roots. **Reference strength** (`java.lang.ref`: strong, soft, weak, phantom, listed from strongest to weakest) determines how a reference participates in that reachability determination — most references in ordinary code are implicitly strong; the other three are explicit wrapper types used specifically to hold a reference to an object *without* keeping it strongly reachable, each with a different guarantee about when the collector clears it.

## Core Concepts

### The mark phase is a real graph traversal, not a reference-counting scheme

Java's collectors use tracing garbage collection (mark-and-sweep/compact family), not reference counting — the mark phase starts at each GC root and follows every reachable reference transitively, marking every object it visits as live; anything left unmarked after the traversal completes is garbage, regardless of how many other objects point to it, as long as none of those pointing objects are themselves reachable from a root. This is precisely why a large cycle of objects referencing only each other, with no path back to any root, is correctly collected — a reference-counting scheme would need special cycle-detection logic to handle this case, but tracing collection handles it as a natural consequence of only counting root-reachable paths.

### The reference-strength hierarchy, strongest to weakest

**Strong** references (ordinary Java references) are followed unconditionally — an object reachable only via strong references is never collected while that path exists. **Soft** references are cleared at the collector's discretion, but the JVM is specifically encouraged (not strictly required, but this is the documented intent and the behavior every mainstream implementation follows) to hold onto soft-referenced objects as long as there's no real memory pressure, and to clear all soft references before throwing `OutOfMemoryError` — making `SoftReference` suitable for memory-sensitive caches. **Weak** references are cleared as soon as the collector determines the referent is otherwise unreachable, with no memory-pressure consideration at all — `WeakHashMap`'s keys use exactly this, so entries disappear promptly once nothing else references the key, not only under memory pressure. **Phantom** references never allow `get()` to return the referent at all (it always returns `null`) — they exist purely so a `ReferenceQueue` can notify code *after* an object has been finalized and is about to be reclaimed, which is the mechanism `DirectByteBuffer`'s native-memory cleanup and `java.lang.ref.Cleaner` are built on.

### The (weak) generational hypothesis is the theoretical justification for generational collection

The empirical observation — most objects die young, and objects that survive one collection are disproportionately likely to survive many more — is the "weak generational hypothesis," and it's the entire theoretical basis for splitting collection into a young generation (collected frequently, cheaply, since most of its objects are already garbage) and an old generation (collected rarely, since surviving objects tend to keep surviving). This isn't a G1-specific idea — it predates G1 by decades and underlies every generational collector's design, including G1's region-based implementation of the same underlying strategy (per [GC Fundamentals and Log Analysis](gc-fundamentals-and-log-analysis.md)).

## Internal Implementation

**Real demonstration of all four reference-strength behaviors** (`practice/java/week-19/gc-roots-reachability/src/ReferenceStrengthDemo.java`):

```
=== Strong reference: survives GC ===
strong reference still points to: Payload#1

=== Weak reference: cleared once no strong reference remains ===
before nulling strong ref, weakRef.get() = Payload#2
after System.gc(), weakRef.get() = null

=== Soft reference: JVM prefers to keep it under normal memory pressure ===
after System.gc() with no real memory pressure, softRef.get() = Payload#3

=== Phantom reference: get() ALWAYS returns null; enqueued only after collection ===
phantomRef.get() = null
queue.remove() returned: the phantom reference itself, now enqueued
```

Four distinct, real behaviors from four reference types wrapping otherwise-identical objects: the strong reference survives unconditionally; the weak reference is cleared the instant `System.gc()` runs after its only strong path is removed; the soft reference *survives* the identical operation, under no real memory pressure — the concrete evidence of soft references' discretionary, pressure-aware clearing policy versus weak references' immediate clearing; the phantom reference never returns a usable object at all (even before collection), and is instead delivered through the `ReferenceQueue` after the collector processes it — real, direct evidence of the cleanup-notification mechanism distinct from every other reference type's "give me the object back" model.

## Production Scenarios

**A cache implemented with `WeakHashMap`, expected to hold entries "as long as memory allows," empties out far sooner than expected under normal operation, with plenty of free heap available.** This is a common misunderstanding of weak references: `WeakHashMap` clears entries as soon as the key becomes otherwise unreachable, with no memory-pressure consideration at all — it's not a memory-sensitive cache mechanism, and using it as one produces exactly this symptom. The fix is switching to a genuinely memory-pressure-aware cache built on `SoftReference` (or, more commonly in practice, a purpose-built caching library with its own eviction policy), not tuning heap size or GC settings, since the clearing behavior is a documented property of `WeakReference`, not a bug or a tuning target.

**A native-resource cleanup mechanism (a custom off-heap buffer wrapper) relies on `finalize()` to release native memory, and a production incident traces a native-memory leak to finalization simply not running promptly enough under GC pressure.** `finalize()` (deprecated since JDK 9, removed as a routine recommendation) has real, well-documented problems — no guaranteed timing, no guaranteed execution at all in some shutdown scenarios, and objects can be "resurrected" during finalization, complicating reachability further. The modern fix is exactly the phantom-reference-plus-`ReferenceQueue` pattern this chapter demonstrates directly (or the higher-level `java.lang.ref.Cleaner` API built on it) — deterministic notification after the collector has determined the object unreachable, without `finalize()`'s timing and resurrection hazards.

## Failure Modes and Debugging

- **Symptom: a `WeakHashMap`-based structure empties faster than expected, with plenty of heap free.** This is `WeakHashMap`'s documented, correct behavior — weak references are cleared immediately upon otherwise-unreachability, independent of memory pressure — not a bug; if pressure-aware retention is actually needed, use `SoftReference`-backed storage instead.
- **Symptom: an object with apparently no remaining strong references isn't being collected.** Check for an unexpected GC root path — a static field accidentally retaining it, a still-running thread's local variable, or a listener/callback registration holding a reference the code no longer intends to use (see [Memory Leak Diagnosis and Heap Dump Analysis](memory-leak-diagnosis-and-heap-dump-analysis.md) for the full diagnostic workflow via `jmap -histo:live` and heap-dump GC-roots tracing).
- **Anti-pattern to rule out first when relying on `finalize()` for cleanup timing:** confirm whether the code depends on finalization happening promptly, or at all — `finalize()` provides neither guarantee, and a phantom-reference/`Cleaner`-based approach should be the default for any resource-cleanup mechanism going forward.

## Trade-offs

Soft references provide pressure-aware caching for free from the JVM's own memory-management logic, but their exact clearing policy is implementation-defined beyond the "clear before throwing OOM" guarantee — different JVM versions and vendors can and do tune the specific heuristic, making soft-reference-based cache behavior somewhat less predictable than an explicit, application-managed eviction policy. Weak references provide precise, immediate clearing on unreachability, which is exactly right for `WeakHashMap`-style "don't keep this alive just because I'm tracking it" use cases, but wrong for anything wanting memory-pressure-aware retention.

## Decision Framework

Use strong references (the default) for anything that should genuinely keep an object alive as long as the reference exists — the vast majority of code. Reach for `WeakReference` specifically when tracking an object without wanting that tracking itself to extend its lifetime (canonicalizing maps, listener registries where the listener's real lifecycle is owned elsewhere). Reach for `SoftReference` specifically for memory-sensitive caches where "keep it if there's room, drop it under pressure" is the actual desired policy — but be aware its exact heuristic isn't precisely specified, so don't depend on fine-grained timing control. Reach for `PhantomReference` plus a `ReferenceQueue` (or the higher-level `Cleaner` API) for any deterministic post-collection cleanup need, never `finalize()`.

## Common Mistakes

- Describing "eligible for garbage collection" as "no longer referenced" without being able to name GC roots or describe reachability as root-traced graph connectivity.
- Using `WeakHashMap` expecting memory-pressure-aware caching behavior, when it actually clears immediately on unreachability regardless of memory pressure.
- Relying on `finalize()` for resource cleanup timing, unaware of its lack of execution and timing guarantees, and the resurrection hazard it introduces.
- Treating the generational hypothesis as a G1-specific implementation detail rather than the general theoretical justification underlying every generational collector, including non-region-based ones.

## Anti-Patterns

Overriding `finalize()` as the primary mechanism for releasing a native or off-heap resource — this pattern predates the modern phantom-reference/`Cleaner` alternatives, carries real, well-documented risks (no timing guarantee, no execution guarantee in some JVM shutdown paths, object resurrection during finalization complicating reachability reasoning), and has been formally deprecated since JDK 9 specifically because of these issues.

## Best Practices

Use `PhantomReference` with a `ReferenceQueue` (or the simpler `java.lang.ref.Cleaner` API wrapping the same mechanism) for any resource-cleanup need tied to garbage collection, never `finalize()`. When choosing between `WeakReference` and `SoftReference` for a caching or tracking structure, explicitly state which clearing policy is actually wanted (immediate-on-unreachability versus pressure-aware) rather than picking whichever one "sounds more like caching" without checking the actual guarantee each provides.

## Interview Answer Framework

### 30-Second Answer

An object is reachable if a chain of strong references traces from at least one GC root (thread stacks, static fields, JNI references) to it, determined by the collector's mark phase actually traversing the object graph — not a vague notion of "being used." Java's reference-strength hierarchy (strong, soft, weak, phantom) lets code hold a reference without necessarily keeping an object strongly reachable, each level with a distinct, real clearing behavior: weak clears immediately on unreachability; soft clears only under real memory pressure; phantom never returns the object at all, existing purely for post-collection cleanup notification.

### 2-Minute Answer

Definition: GC roots are the fixed starting points for reachability; reachability is a graph-traversal property computed by the mark phase, not a static count. Why the reference-strength hierarchy exists: sometimes code needs to hold onto an object without that holding itself keeping the object alive — four distinct strength levels provide four distinct such policies. How it works: strong is unconditional; soft is pressure-aware (cleared before OOM, otherwise JVM-discretionary); weak clears immediately on otherwise-unreachability, no pressure consideration; phantom never returns a usable reference, only enables post-collection notification via a `ReferenceQueue`. One trade-off: soft references' exact clearing heuristic is implementation-defined beyond the "before OOM" guarantee, making fine-grained behavior less predictable than an explicit eviction policy. One production example: measured directly, an identical operation (`System.gc()` after removing the only strong reference) clears a weak reference immediately but leaves a soft reference intact under normal memory conditions — the concrete evidence distinguishing the two policies.

### 10-Minute Deep Dive

Cover: the precise definition of GC roots and why tracing (not reference counting) collection correctly handles reference cycles as a natural consequence of root-based reachability; the real, measured four-way reference-strength demonstration, walking through why each behaves the way it does; the `WeakHashMap`-as-a-cache misconception and why it's a real, recurring production surprise; the `finalize()`-to-phantom-reference migration story and the specific hazards (timing, execution guarantees, resurrection) that motivated it; the generational hypothesis as the theoretical justification for generational collection generally, distinct from and prerequisite to understanding any specific collector's (G1's) implementation of that strategy.

### Whiteboard Explanation

Draw a small set of boxes at the top labeled "GC Roots" (thread stack, static field, JNI ref). Draw arrows from each root into a graph of object boxes, some connected back to a root (shaded "live"), some forming an isolated cluster with arrows only pointing among themselves and no path back to any root (shaded "garbage," even though they still reference each other). Below, draw the same object referenced by four different arrow styles labeled strong/soft/weak/phantom, each arrow annotated with its clearing rule.

### Production Example

A caching layer built early in a service's life uses `WeakHashMap` as its underlying store, on the (incorrect) assumption it behaves like a memory-pressure-aware LRU cache. Under light load, the cache appears to work — entries persist as long as some other part of the code happens to hold a reference to the key. Under refactoring, a code path that incidentally kept keys strongly reachable elsewhere is removed, and the cache's hit rate collapses immediately in production, since `WeakHashMap` entries were only ever surviving due to that incidental external strong reference, not any caching logic of `WeakHashMap`'s own. The fix replaces it with a proper caching library with an explicit, understood eviction policy, and the postmortem specifically calls out the `WeakReference`-vs-`SoftReference` distinction as the root conceptual error.

### Trade-offs to Mention

Soft references' clearing heuristic beyond "before OOM" is implementation-defined, trading precise control for automatic, zero-configuration memory-pressure awareness; weak references provide precise, predictable immediate clearing, which is exactly wrong for pressure-aware caching but exactly right for non-lifecycle-affecting tracking.

### Common Candidate Mistakes

Describing reachability as "reference count reaches zero" (a C++/reference-counting mental model, not how Java's tracing collectors work); using `WeakHashMap` and `SoftReference`-based structures interchangeably without knowing their distinct clearing guarantees.

### Typical Follow-Up Questions

"Why does Java use tracing collection instead of reference counting?" → tracing correctly handles reference cycles as a natural consequence of root-based reachability, without needing separate cycle-detection logic reference counting requires. "What's the actual mechanism `Cleaner`/phantom references provide that `finalize()` doesn't?" → deterministic post-collection notification via a `ReferenceQueue`, without `finalize()`'s lack of timing/execution guarantees or its object-resurrection hazard.

### Senior-Level Expectations

Correctly names GC roots and describes reachability as root-traced graph connectivity, and correctly distinguishes weak from soft reference clearing behavior.

### Staff-Level Discussion

Recognizes the generational hypothesis as the general theoretical basis for generational collection, independent of any specific collector's implementation, and can reason about it when evaluating a collector or tuning strategy on its own terms. Treats `finalize()` as a legacy anti-pattern with specific, nameable hazards, defaulting new resource-cleanup designs to the phantom-reference/`Cleaner` pattern without needing to be prompted.

## Interview Questions

### Question 1

**A `WeakHashMap`-based cache is emptying much faster than expected, even though the heap has plenty of free memory. What's going on?**

**Expected answer:** `WeakHashMap` clears entries as soon as the key becomes otherwise unreachable, with no memory-pressure consideration at all — it's not a pressure-aware caching mechanism, and the observed behavior is its correct, documented behavior, not a bug. If pressure-aware retention was actually wanted, `SoftReference`-backed storage (or a purpose-built cache library) is the correct tool.

**Common mistakes:** assuming `WeakHashMap` behaves like a memory-aware LRU cache.

**Follow-up questions:** "What would change if `SoftReference` were used instead?" (entries would be retained under normal memory conditions and only cleared under real pressure, with the JVM guaranteeing all soft references are cleared before an `OutOfMemoryError` is thrown.)

**Senior-level expectations:** correctly identifies `WeakHashMap`'s immediate-on-unreachability clearing as the (correct, not buggy) cause.

**Staff-level expectations:** proposes the specific `SoftReference`-based alternative and names its distinct guarantee.

### Question 2

**Why is `finalize()` considered a legacy anti-pattern for resource cleanup, and what replaced it?**

**Expected answer:** `finalize()` provides no guarantee on *when* it runs (or that it runs at all, in some shutdown scenarios), and an object can be "resurrected" (a new strong reference created) during finalization, complicating reachability reasoning. The modern replacement is a `PhantomReference` registered with a `ReferenceQueue` (or the higher-level `java.lang.ref.Cleaner` API built on the same mechanism), which provides deterministic post-collection notification without either hazard.

**Common mistakes:** describing `finalize()`'s problems only vaguely ("it's slow" or "it's deprecated") without naming the specific timing/execution-guarantee and resurrection hazards.

**Follow-up questions:** "Why can't a phantom reference's `get()` return the object, unlike weak or soft references?" (this is a deliberate design choice preventing resurrection through the cleanup mechanism itself — phantom references exist purely for notification, not for providing renewed access to the object.)

**Senior-level expectations:** correctly names at least the timing/execution-guarantee problem and the phantom-reference-based replacement.

**Staff-level expectations:** explains the resurrection hazard specifically and why phantom references' `get()`-always-null design deliberately avoids reintroducing it.

## Summary

GC roots are the fixed starting points for reachability, computed by the collector's mark phase as an actual graph traversal, not a reference count — this is why reference cycles with no path back to a root are correctly collected without special cycle-detection logic. Java's reference-strength hierarchy (strong, soft, weak, phantom) provides four distinct policies for holding a reference without necessarily keeping an object strongly alive, each with real, measurably distinct clearing behavior, demonstrated directly: an identical post-unreachability `System.gc()` clears a weak reference immediately but leaves a soft reference intact under normal memory pressure, while a phantom reference never returns a usable object at all, instead delivering post-collection notification through a `ReferenceQueue` — the modern, hazard-free replacement for `finalize()`.

## Key Takeaways

- Reachability is a graph-traversal property computed from GC roots by the mark phase, not a reference count — this is why Java's tracing collectors correctly handle reference cycles without special-case logic.
- The reference-strength hierarchy (strong/soft/weak/phantom) provides four distinct clearing policies, verified directly: weak clears immediately on unreachability; soft clears only under real memory pressure.
- `WeakHashMap` is not a memory-pressure-aware cache — it clears entries immediately on unreachability, a common, real source of production surprise.
- `PhantomReference` plus `ReferenceQueue` (or `Cleaner`) is the modern, hazard-free replacement for `finalize()`-based resource cleanup.
- The generational hypothesis (most objects die young; survivors tend to keep surviving) is the general theoretical basis for generational collection, independent of any specific collector's implementation.

## Cheat Sheet

| Reference type | `get()` behavior | Clearing trigger |
|---|---|---|
| Strong (default) | Always returns the object | Never, while the reference exists |
| Soft | Returns the object until cleared | Collector's discretion; guaranteed cleared before `OutOfMemoryError` |
| Weak | Returns the object until cleared | Immediately upon otherwise-unreachability, no pressure consideration |
| Phantom | Always returns `null` | N/A — enqueued to a `ReferenceQueue` after collection, for notification only |

## Flashcards

**Q: What are GC roots, concretely?**
A: The fixed set of reference locations treated as inherently alive — active thread stacks, static fields, JNI references, and a few JVM-internal roots.

**Q: What's the key behavioral difference between `WeakReference` and `SoftReference`?**
A: Weak clears immediately upon otherwise-unreachability, with no memory-pressure consideration; soft is retained under normal conditions and only cleared under real memory pressure (guaranteed before `OutOfMemoryError`).

**Q: Why can't `PhantomReference.get()` ever return the referent?**
A: A deliberate design choice preventing object resurrection through the cleanup mechanism itself — phantom references exist purely for post-collection notification via a `ReferenceQueue`, not for renewed access.

## Practice Exercises

1. Reproduce `ReferenceStrengthDemo.java` and add a fifth scenario: allocate a large array to create real memory pressure before checking the soft reference again, and observe whether it's cleared under that pressure (unlike the no-pressure case in this chapter).
2. Implement a minimal resource-cleanup class using `PhantomReference` and `ReferenceQueue` (not `Cleaner`) that prints a message when a "resource" object is collected, and verify the message only appears after `System.gc()` and a `queue.remove()` call, never before.

## Solutions

1. Under genuine memory pressure (e.g., allocating most of a small, fixed heap before checking), the soft reference should be cleared, unlike this chapter's no-pressure demonstration — directly confirming the pressure-aware policy distinguishing `SoftReference` from `WeakReference`, whose clearing is unconditional on unreachability alone.
2. The message should never appear before `queue.remove()` returns the enqueued reference, since `PhantomReference.get()` never provides direct access and the queue is the only real notification channel — confirming the cleanup pattern's deterministic, notification-only design.

## Additional Reading

- [`java.lang.ref` package documentation](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/ref/package-summary.html)

## Official References

- [`java.lang.ref` package documentation](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/ref/package-summary.html)
