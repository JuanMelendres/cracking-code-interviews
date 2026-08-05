---
title: "Cheat Sheet: Collection Selection Decision Matrix"
slug: collection-selection-decision-matrix
document_type: cheat-sheet
domain: collections
topic_id: T-209
canonical: ../handbook/collections/collection-selection-decision-matrix.md
last_updated: 2026-08-05
---

# Collection Selection Decision Matrix

**Canonical chapter:** [`handbook/collections/collection-selection-decision-matrix.md`](../handbook/collections/collection-selection-decision-matrix.md)

## Core Mental Model

Every collection choice reduces to the same three questions: how is it read, how is it written, and does more than one thread touch it? `List` vs. `Map` vs. `Queue` is usually obvious from the problem shape; the harder, interview-relevant decision is which *implementation* fits the actual read/write pattern — and that decision should always trace back to a specific, nameable operation's complexity, not a general reputation ("ArrayList is usually fine").

## Essential Definitions

- **Dominant access pattern** — the actual operation a collection performs most, stated explicitly in one sentence before naming any implementation.
- **List selection** — indexed reads/iteration → `ArrayList`; frequent head/tail insertion at an already-known position → `LinkedList`/`ArrayDeque`.
- **Map selection** — single-threaded/externally-synchronized → `HashMap`; genuine multi-threaded access → `ConcurrentHashMap` with `merge()`/`compute()` for any read-modify-write.
- **Queue selection** — buffering with real backpressure → bounded `BlockingQueue`; direct handoff, no buffering → `SynchronousQueue`.

## Decision Table

| Interface | Default | Alternative | Switch when |
|---|---|---|---|
| `List` | `ArrayList` | `LinkedList` / `ArrayDeque` | Dominant op is head/tail insertion at a known position, not indexed reads |
| `Map` | `HashMap` | `ConcurrentHashMap` | Accessed from more than one thread — use `merge()`/`compute()`, never `get()`+`put()` |
| `Queue` | Bounded `BlockingQueue` | `SynchronousQueue` | Intent is direct producer-consumer handoff with zero buffering |

**Trade-offs:** no collection implementation is free — each optimizes one operation at the expense of another. Concurrent access rules out more implementations than single-threaded access.

## Key Numbers (real, measured — this week's other chapters, synthesized here)

```
ArrayList random-access get():         ~320x faster than LinkedList (50,000-element list)
LinkedList front-insertion (addFirst): ~117x faster than ArrayList
ConcurrentHashMap get()+put() (naive): measurably loses updates under concurrency
HashMap poor key hashCode() distribution: ~3,076x slowdown vs. good distribution
BlockingQueue put() on full queue:     genuinely blocks (WAITING thread state), real backpressure
```

## Common Pitfalls

- Naming an interface ("it's a List") as if that alone determines the implementation, without stating the access pattern.
- Choosing a collection implementation by habit or reputation rather than the code's actual dominant operation.
- Treating HashMap, ConcurrentHashMap, BlockingQueue, and ArrayList/LinkedList as unrelated facts instead of instances of one decision process.

## Interview Answer Skeleton

**30-sec:** Collection selection reduces to three questions: how is it read, how is it written, and is it shared across threads? `ArrayList` for indexed reads, `LinkedList`/`ArrayDeque` for head/tail insertion, `ConcurrentHashMap` with `merge()`/`compute()` for concurrent maps, a bounded `BlockingQueue` for producer-consumer buffering with real backpressure.

**2-min:** Add why (the JDK deliberately offers multiple implementations per interface with different measured trade-offs; defaulting out of habit forfeits real performance) + the synthesis evidence (each of the four sub-decisions grounded in a separate chapter's own measured numbers) + a real production example (a code review catching three separate collection mismatches in one pull request by consistently applying "what's the access pattern").

**Whiteboard:** Interface needed → List/Map/Queue branch → each branching again on the specific access-pattern question, ending at a concrete implementation. Walk through it live for whatever scenario the interviewer proposes, narrating the access-pattern question at each branch.

**Staff-level framing:** the real Staff-level signal isn't knowing all four individual facts — it's applying them as one coherent decision process, unprompted, to a genuinely new scenario. Treat "what collection should I use here" as a design question requiring an explicit access-pattern statement before any implementation name is offered.

## Production Warning Signs

- A pull request introduces an unbounded `LinkedBlockingQueue`, a `HashMap` shared with a new background thread, and a `LinkedList` used purely for `get(index)` in a hot loop — three separate known-bad patterns matching three other chapters' own production incidents, catchable by one standing review question: "what's the access pattern for this collection?"
- A collection choice that was correct when written no longer matches the code's current actual usage after a feature evolved — revisit the choice when the access pattern changes materially, don't assume the original decision still holds.
- **Prevention:** make "state the dominant access pattern before naming an implementation" a standing code-review checklist item for any new collection field, not an occasional afterthought.

## Related

- `handbook/collections/hashmap-internals.md`
- `handbook/collections/concurrenthashmap-internals.md`
- `handbook/collections/blockingqueue-family.md`
- `handbook/collections/arraylist-and-linkedlist-internals.md`
- `handbook/system-design/storage-selection-tradeoffs.md`
