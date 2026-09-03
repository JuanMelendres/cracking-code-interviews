---
title: "Cheat Sheet: GC Roots, Reachability, and Reference Strength"
slug: gc-roots-reachability-and-reference-strength
document_type: cheat-sheet
domain: jvm
topic_id: T-303
canonical: ../handbook/jvm/gc-roots-reachability-and-reference-strength.md
last_updated: 2026-08-05
---

# GC Roots, Reachability, and Reference Strength

**Canonical chapter:** [`syllabus/02-java/jvm-internals/gc-roots-reachability-and-reference-strength.md`](../syllabus/02-java/jvm-internals/gc-roots-reachability-and-reference-strength.md)

## Core Mental Model

Picture the heap as a directed graph, and GC roots as the *only* legitimate starting points for asking "is this object alive." An object is reachable if and only if there's a path from at least one root, following reference fields, that reaches it — an object with a thousand incoming references is exactly as dead as one with zero, the moment none of those incoming paths trace back to a root. The reference-strength hierarchy adds a second axis: not "is there a path from a root," but "how strongly does the collector respect a path through *this specific kind* of reference."

## Essential Definitions

- **GC roots** — the fixed set of reference locations treated as inherently alive: active thread stacks, static fields of loaded classes, JNI references, and a few JVM-internal roots.
- **Reachability** — a computed property, not a static one: a chain of strong references from at least one GC root, discovered by the mark phase actually traversing the object graph.
- **Tracing collection** (mark-and-sweep/compact) — not reference counting; correctly collects reference cycles as a natural consequence of only counting root-reachable paths, no special cycle-detection needed.
- **Reference strength** (`java.lang.ref`, strongest to weakest) — strong, soft, weak, phantom — four distinct policies for holding a reference without necessarily keeping the object strongly alive.
- **Weak generational hypothesis** — most objects die young; survivors of one collection are disproportionately likely to survive many more. The theoretical basis for every generational collector, independent of any specific implementation (G1 included).

## Decision Table

| Reference type | `get()` behavior | Clearing trigger |
|---|---|---|
| Strong (default) | Always returns the object | Never, while the reference exists |
| Soft | Returns the object until cleared | Collector's discretion; guaranteed cleared before `OutOfMemoryError` |
| Weak | Returns the object until cleared | Immediately upon otherwise-unreachability, no pressure consideration |
| Phantom | Always returns `null` | N/A — enqueued to a `ReferenceQueue` after collection, notification only |

**When to reach for each:** strong = the default, vast majority of code. Weak = tracking without extending lifetime (canonicalizing maps, listener registries owned elsewhere). Soft = memory-sensitive caches wanting "keep if there's room, drop under pressure." Phantom + `ReferenceQueue` (or `Cleaner`) = deterministic post-collection cleanup, never `finalize()`.

## Key Numbers (real, executed — `ReferenceStrengthDemo.java`)

Four distinct behaviors from four reference types wrapping otherwise-identical objects, same `System.gc()` call:

```
Strong:  survives GC unconditionally
Weak:    before nulling strong ref -> Payload#2; after System.gc() -> null
Soft:    after System.gc() with NO real memory pressure -> still Payload#3
Phantom: get() = null (always); queue.remove() returns the enqueued reference itself
```

The weak reference clears immediately once its only strong path is gone; the soft reference *survives the identical operation* under no real memory pressure — the concrete evidence separating soft's discretionary, pressure-aware policy from weak's unconditional clearing.

## Common Pitfalls

- Describing "eligible for GC" as "no longer referenced" without naming actual GC roots or describing reachability as root-traced graph connectivity.
- Using `WeakHashMap` expecting memory-pressure-aware caching — it clears entries immediately on unreachability, with **no** pressure consideration at all.
- Relying on `finalize()` for cleanup timing — no execution guarantee, no timing guarantee, and objects can be "resurrected" during finalization.
- Treating the generational hypothesis as G1-specific rather than the general theoretical justification underlying every generational collector.

## Interview Answer Skeleton

**30-sec:** Reachable = a chain of strong references from a GC root, found by the mark phase's graph traversal — not "being used" vaguely. Reference-strength hierarchy (strong/soft/weak/phantom) lets code hold a reference without keeping the object strongly alive, each with distinct, real clearing behavior.

**2-min:** Add why the hierarchy exists (four distinct "hold without extending lifetime" policies) + how each behaves (soft = pressure-aware, before-OOM guarantee; weak = immediate, no pressure consideration; phantom = never returns the object, notification only) + the measured weak-vs-soft distinction under an identical `System.gc()` call.

**Whiteboard:** Boxes labeled "GC Roots" at top (thread stack, static field, JNI ref) → arrows into an object graph, some tracing back to a root (live), an isolated cluster pointing only at each other with no path to a root (garbage, despite mutual references). Below: same object, four arrow styles labeled strong/soft/weak/phantom, each annotated with its clearing rule.

**Staff-level framing:** the generational hypothesis is the general theoretical basis for generational collection, independent of any specific collector — reason about it on its own terms when evaluating a tuning strategy. Treat `finalize()` as a legacy anti-pattern with nameable hazards (no timing/execution guarantee, resurrection), defaulting to phantom-reference/`Cleaner` without being prompted.

## Production Warning Signs

- A `WeakHashMap`-based cache empties far sooner than expected with plenty of free heap — this is correct, documented behavior, not a bug; switch to `SoftReference`-backed storage or a purpose-built cache if pressure-aware retention was actually wanted.
- A native-resource cleanup mechanism relying on `finalize()` traces a native-memory leak to finalization simply not running promptly under GC pressure — migrate to `PhantomReference` + `ReferenceQueue` (or `Cleaner`) for deterministic, hazard-free notification.
- **Prevention:** when choosing between `WeakReference` and `SoftReference` for a caching/tracking structure, explicitly state which clearing policy is wanted rather than picking whichever "sounds more like caching."

## Related

- `syllabus/02-java/jvm-internals/gc-fundamentals-and-log-analysis.md`
- `syllabus/02-java/jvm-internals/memory-leak-diagnosis-and-heap-dump-analysis.md`
