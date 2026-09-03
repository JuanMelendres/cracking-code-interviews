---
title: "Legacy Stack Synchronization Overhead in a Single-Threaded Undo Buffer"
document_type: production-cookbook-entry
domain: collections
status: draft
last_updated: 2026-09-02
related_handbook:
  - ../handbook/collections/arraydeque-internals-and-the-legacy-stack-problem.md
source: handbook/collections/arraydeque-internals-and-the-legacy-stack-problem.md#production-scenarios
---

# Legacy Stack Synchronization Overhead in a Single-Threaded Undo Buffer

## Context

A hot code path uses `java.util.Stack` for a purely single-threaded undo/redo buffer.

## Symptoms

Under sustained load, profiling shows a measurable, real amount of time spent in `Stack`'s synchronized `push()`/`pop()` methods — lock acquisition/release overhead — despite the stack never being accessed by more than one thread.

## Impact

Measurable wasted CPU time on lock operations that serve no purpose in this genuinely single-threaded context.

## Initial Hypotheses

- A JIT deoptimization issue — checked, and ruled out: the methods are correctly inlined and optimized, the cost is the lock itself.
- Contention from an unrelated thread — checked, and ruled out: thread dumps confirm no other thread ever touches this stack.
- The legacy `Stack`'s built-in synchronization is simply unnecessary overhead here — correct.

## Evidence

The measured slowdown matches the known cost profile of `Stack` versus `ArrayDeque` under an equivalent push/pop workload — roughly a 2x overhead attributable specifically to unconditional lock acquisition on every call.

## Investigation Timeline

1. **Profiling flags time spent inside `Stack`'s `push()`/`pop()` methods** on a hot, purely single-threaded undo/redo code path.
2. **JIT behavior checked** and confirmed the methods are correctly inlined and optimized — the cost is not a missed optimization opportunity, it's the lock acquisition itself.
3. **Thread dumps reviewed** across the affected process and confirmed no thread other than the one owning the undo/redo buffer ever touches this stack.
4. **Cost attributed directly to `Stack.push()`/`pop()`'s `synchronized` modifier**, inherited from `Vector`, which acquires and releases a monitor lock on every single call regardless of actual contention.
5. **Comparison against `ArrayDeque`'s equivalent `push()`/`pop()` via the `Deque` interface** confirms the measured overhead is attributable specifically to unnecessary lock acquisition, not any other difference in the two implementations.

## Root Cause

`Stack` extends `Vector`, whose methods (`push`, `pop`, `peek`, ...) are all `synchronized` — real lock acquisition on every single call, a cost paid even when the stack is never touched by more than one thread.

## Immediate Mitigation

None needed beyond the fix itself — swapping the type carries no semantic risk for this genuinely single-threaded usage.

## Permanent Fix

Replace `Stack<T>` with `ArrayDeque<T>`, using `push()`/`pop()`/`peek()` via the `Deque` interface — a drop-in behavioral replacement for stack usage with none of the unnecessary synchronization cost.

## Alternatives Considered

None seriously — this is a case where the JDK's own documentation already recommends the fix explicitly; there's no real trade-off to weigh here for single-threaded usage.

## Trade-offs

None meaningful for the single-threaded case — if genuine multi-threaded access is ever needed, that requires a real concurrent-safe structure, not a return to `Stack`, which offers no useful concurrency guarantee beyond avoiding data races on individual method calls (it provides no compound-operation atomicity either).

## Prevention

Flag any new `java.util.Stack`/`Vector` usage in code review by default — the JDK's own documentation has recommended against them for years.

## Monitoring and Alerts

- Add a static-analysis rule (a straightforward one for most linters) flagging any new import of `java.util.Stack` or `java.util.Vector`, since the JDK's own Javadoc already recommends `ArrayDeque`/`ArrayList` as the modern replacement in essentially every case.
- When profiling a hot path already known to use `Stack`, specifically check for time attributed to `synchronized` method entry/exit rather than assuming lock cost is negligible — this class of overhead is easy to overlook because the code "looks" single-threaded and correct.
- Track a periodic dependency/API-usage audit (a simple `grep` or bytecode scan across the codebase) for legacy synchronized collection types (`Stack`, `Vector`, `Hashtable`) so latent instances are found proactively rather than only when a specific hot path happens to get profiled.

## Interview Story

Present it as a representative scenario to adapt, not a claimed personal history.

- **Situation:** a purely single-threaded undo/redo buffer built on `java.util.Stack` showed measurable CPU time spent inside lock-acquisition code during profiling of a hot path.
- **Task:** determine why a stack that was never accessed by more than one thread was still paying a synchronization cost.
- **Action:** ruled out a JIT deoptimization issue and cross-thread contention via thread dumps, then attributed the cost directly to `Stack`'s inherited `synchronized` methods from `Vector`.
- **Result:** replaced `Stack` with `ArrayDeque` via the `Deque` interface, eliminating the unnecessary lock overhead with no semantic change, and added a static-analysis rule to catch future legacy-collection usage in code review.

## Staff-Level Discussion

This incident is a good illustration of "legacy but still standard library" traps: `java.util.Stack` is not deprecated, appears in every introductory Java tutorial as *the* stack type, and compiles and runs correctly for single-threaded use — nothing about using it signals a mistake to someone unfamiliar with its history as a `Vector` subclass predating the Collections Framework's non-synchronized designs. The actual cost here is small in absolute terms per call, but the pattern generalizes: any legacy synchronized type (`Stack`, `Vector`, `Hashtable`) pays a lock-acquisition tax on every operation regardless of real contention, and a Staff engineer reviewing a codebase's collection choices should treat "why is this type synchronized when nothing here is multi-threaded" as a standing, low-cost audit question rather than something surfaced only when a specific hot path happens to get profiled. The broader organizational lesson is that "which collection type" decisions, individually trivial, compound across a large codebase — a lint rule that costs nothing to run continuously is a better investment than relying on profiling to catch each instance one at a time.

## Related Handbook Chapters

- [ArrayDeque Internals and the Legacy Stack/Vector Problem](../handbook/collections/arraydeque-internals-and-the-legacy-stack-problem.md) — canonical circular-buffer mechanics and the measured ~2.26x `Stack`-versus-`ArrayDeque` cost this incident reproduces.
- [CopyOnWriteArrayList and Copy-on-Write Trade-offs](../handbook/collections/copyonwritearraylist-and-copy-on-write-tradeoffs.md) — related consideration of when synchronization cost on a collection is or isn't justified by the actual access pattern.
