---
title: "Cheat Sheet: ConcurrentHashMap Internals"
slug: concurrenthashmap-internals
document_type: cheat-sheet
domain: collections
topic_id: T-205
canonical: ../handbook/collections/concurrenthashmap-internals.md
last_updated: 2026-08-04
---

# ConcurrentHashMap Internals

**Canonical chapter:** [`syllabus/02-java/collections/concurrenthashmap-internals.md`](../syllabus/02-java/collections/concurrenthashmap-internals.md)

## Core Mental Model

`ConcurrentHashMap` makes every individual method call safe — it says nothing about the safety of a *sequence* of calls. `get()` is safe. `put()` is safe. But `get()` followed by a separate `put()` is two independent safe operations with an unprotected gap between them, during which another thread can interleave and cause a lost update. The fix isn't "use a thread-safe map harder" — it's using the single-method atomic operations (`merge`, `compute`, `computeIfAbsent`) that perform the entire read-modify-write as one indivisible operation.

## Essential Definitions

- **`ConcurrentHashMap`** — a thread-safe hash map allowing safe concurrent reads and writes without external synchronization, via fine-grained internal locking (historically per-segment; since JDK 8, per-bucket via CAS operations and synchronized blocks on individual bin heads) rather than a single map-wide lock.
- **Why it exists** — a plain `HashMap` is not thread-safe: concurrent structural modifications (resizes, bucket list changes) can corrupt its internal structure, losing entries with no exception thrown at all. `ConcurrentHashMap` also provides atomic compound operations (`merge`, `compute`, `computeIfAbsent`) for the common case where a read-modify-write must happen as one atomic step.

## Decision Table

| Choice | Benefit | Cost |
|---|---|---|
| Plain `HashMap` under concurrency | Fastest single-threaded performance | Corrupts silently under any concurrent structural modification |
| `ConcurrentHashMap` with `get()`+`put()` | Looks correct, compiles, "seems" thread-safe | Loses updates under real concurrent access — a subtle, load-dependent bug |
| `ConcurrentHashMap` with `merge()`/`compute()` | Genuinely atomic per-key read-modify-write | Slightly less flexible than manual get/put for complex multi-step logic |
| External lock around the whole map | Simple to reason about | Serializes all access, discarding per-bucket concurrency entirely |

| Situation | Correct approach |
|---|---|
| A map accessed from multiple threads | `ConcurrentHashMap`, never a plain `HashMap` |
| Incrementing a counter or aggregating a value per key | `merge(key, delta, combiner)`, never `get()` then `put()` |
| Computing a value only if absent, atomically | `computeIfAbsent(key, function)` |
| A complex read-modify-write per key | `compute(key, biFunction)` |

## Key Numbers (real, executed — `ConcurrentHashMapDemo.java`, OpenJDK 21.0.12, 8 threads × 20,000 `put()` calls)

```
Plain HashMap, concurrent writes:       expected 160000, actual  68683  (CORRUPTED)
ConcurrentHashMap, concurrent writes:   expected 160000, actual 160000 (correct)

get()-then-put() lost-update test (8 threads x 20,000 increments each):
  expected 160000, actual  26212  (LOST UPDATES)
merge() fix:
  expected 160000, actual 160000  (correct)
```

## Common Pitfalls

- Using a plain `HashMap` under any concurrent access, assuming "it probably won't cause a problem in practice"
- Using `get()` followed by a separate `put()` on a `ConcurrentHashMap` for a read-modify-write, assuming the map's thread-safety extends across the two calls
- Wrapping `ConcurrentHashMap` access in external synchronization "to be extra safe," discarding its per-bucket concurrency for no correctness benefit

## Interview Answer Skeleton

**30-sec:** `ConcurrentHashMap` guarantees each individual method call is thread-safe, not a sequence of calls. Measured directly: a `get()`-then-`put()` increment pattern under 8 threads × 20,000 increments produced 26,212 instead of the expected 160,000; switching to `merge()` produced exactly 160,000.

**2-min:** Add why it exists (HashMap corrupts silently under concurrency) + how it works (per-bucket locking since JDK 8) + the get-then-put trade-off + the measured 26,212-vs-160,000 production example.

**Whiteboard:** Draw Thread 1 and Thread 2 both `get('hits')` returning 100, both computing 101, both `put()`-ing 101 — annotate "should be 102, one increment lost." Below it, draw a single `merge()` call as one atomic block with no interleaving possible.

**Staff-level framing:** the gap between "this class is thread-safe" and "this sequence of calls on this class is thread-safe" is one of the most common sources of real production concurrency bugs. Treat every multi-step operation on a shared, thread-safe structure as requiring an explicit atomicity check — the same category of reasoning as checking whether a database transaction actually wraps every statement that needs to be atomic, just applied to an in-memory structure.

## Production Warning Signs

- **Real incident pattern:** a service tracks per-endpoint request counts in a `ConcurrentHashMap<String, Integer>` via `get()`-then-`put()`. Under low traffic, dashboard counts match load-balancer logs closely; under peak traffic, the dashboard consistently undercounts by a margin that scales with concurrent request rate — silently underreporting exactly when the metric (used for capacity planning/alerting) matters most.
- Fix: `map.merge(key, 1, Integer::sum)` — no real trade-off, the get-then-put pattern was strictly worse. Flag any `get()`-then-`put()` sequence in code review.

## Related

- [HashMap Internals](hashmap-internals.md)
- [Executors and Thread Pool Sizing](executors-and-thread-pool-sizing.md)
