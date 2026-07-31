---
title: "Week 14 Flashcards — Collections"
week: 14
document_type: study-pack-flashcards
status: draft
last_reviewed: 2026-07-30
---

# Week 14 Flashcards — Collections

15 cards, three per topic, each naming the misconception it catches.

## Card 1

**Prompt:** When does a HashMap resize?
**Answer:** When `size` exceeds `capacity × loadFactor` (the threshold) — the backing array doubles and every entry is rehashed.
**Why it matters:** The mechanism behind HashMap's amortized O(1) put/get despite growing.
**Common trap:** Not sizing initial capacity when the final entry count is known, causing avoidable resize events.
**Related:** `01-hashmap-internals.md`

## Card 2

**Prompt:** When does a HashMap bucket treeify?
**Answer:** When it holds at least 8 nodes AND the table's overall capacity is at least 64 — otherwise the table resizes instead.
**Why it matters:** Distinguishes a genuine hash-collision problem from a simple sizing problem.
**Common trap:** Stating only the bucket-size threshold without the capacity condition.
**Related:** `01-hashmap-internals.md`

## Card 3

**Prompt:** What happens to HashMap performance with a poor hashCode()?
**Answer:** Every key can land in the same bucket regardless of table size — measured at a ~3,076x lookup slowdown, even with treeification bounding the worst case at O(log n).
**Why it matters:** Resizing the table doesn't fix a distribution problem.
**Common trap:** Assuming a larger table always fixes slow HashMap lookups.
**Related:** `01-hashmap-internals.md`

## Card 4

**Prompt:** What happens to a plain HashMap under concurrent writes from multiple threads?
**Answer:** It can corrupt silently — measured at 68,683 of an expected 160,000 entries surviving, with no exception thrown.
**Why it matters:** The core reason ConcurrentHashMap exists.
**Common trap:** Assuming a plain HashMap "probably won't cause a problem in practice" under light concurrent access.
**Related:** `02-concurrenthashmap-internals.md`

## Card 5

**Prompt:** Is `get()` followed by `put()` atomic on a ConcurrentHashMap?
**Answer:** No — measured at a 26,212-of-160,000 lost-update rate. Each call is individually safe, but the sequence has an unprotected gap between them.
**Why it matters:** The most common ConcurrentHashMap misuse in real codebases.
**Common trap:** Assuming ConcurrentHashMap's thread-safety extends across multiple separate method calls.
**Related:** `02-concurrenthashmap-internals.md`

## Card 6

**Prompt:** What's the correct way to atomically increment a counter in a ConcurrentHashMap?
**Answer:** `map.merge(key, 1, Integer::sum)` — performs the whole read-modify-write as one atomic operation.
**Why it matters:** Eliminates the lost-update race entirely, measured directly.
**Common trap:** Using a manual get()/put() pair instead of the built-in atomic compound operation.
**Related:** `02-concurrenthashmap-internals.md`

## Card 7

**Prompt:** When does `BlockingQueue.put()` block?
**Answer:** When the queue is at capacity (full) — the calling thread genuinely parks until a `take()` elsewhere frees space, measured directly via thread state.
**Why it matters:** The mechanism that provides real backpressure in a producer-consumer pipeline.
**Common trap:** Assuming `put()` on a full queue returns immediately or throws, rather than blocking.
**Related:** `03-blockingqueue-family.md`

## Card 8

**Prompt:** What is SynchronousQueue's internal capacity?
**Answer:** Zero — it's a direct handoff; `put()` only succeeds once a `take()` is already waiting for that exact element.
**Why it matters:** A common misconception is treating it as a very small buffer rather than genuinely capacity-zero.
**Common trap:** Assuming it behaves like a capacity-1 queue.
**Related:** `03-blockingqueue-family.md`

## Card 9

**Prompt:** Why is an unbounded BlockingQueue a risky default in a production pipeline?
**Answer:** It removes backpressure entirely — a slow consumer no longer slows the producer, it just lets the queue grow until memory runs out.
**Why it matters:** Converts a visible, gradual slowdown into an eventual, harder-to-diagnose OutOfMemoryError crash.
**Common trap:** Constructing a `LinkedBlockingQueue` with no capacity argument, silently defaulting to unbounded.
**Related:** `03-blockingqueue-family.md`

## Card 10

**Prompt:** By what factor does ArrayList grow its backing array when full?
**Answer:** Roughly 1.5x (`oldCapacity + oldCapacity/2`), not by doubling — confirmed via reflection.
**Why it matters:** A common assumption is that it doubles, like many other growable structures.
**Common trap:** Assuming ArrayList doubles its capacity like a typical dynamic array implementation.
**Related:** `04-arraylist-and-linkedlist-internals.md`

## Card 11

**Prompt:** What is the time complexity of `get(index)` for ArrayList versus LinkedList?
**Answer:** O(1) for ArrayList (direct array indexing); O(n) for LinkedList (node-by-node traversal) — measured at ~320x slower for a 50,000-element list.
**Why it matters:** The single biggest reason to prefer ArrayList for read-heavy access patterns.
**Common trap:** Using `LinkedList.get()` in a loop, creating an accidental O(n²) traversal.
**Related:** `04-arraylist-and-linkedlist-internals.md`

## Card 12

**Prompt:** Is LinkedList's O(1) insertion guarantee true for any index?
**Answer:** No — only at an already-known position (head, tail, or an existing iterator position). Inserting at an arbitrary index `k` is O(n) overall, since finding position `k` requires a traversal.
**Why it matters:** A common overgeneralization that leads to choosing LinkedList for the wrong reason.
**Common trap:** Assuming `add(k, x)` for an arbitrary `k` is O(1) on a LinkedList.
**Related:** `04-arraylist-and-linkedlist-internals.md`

## Card 13

**Prompt:** What three questions does every collection choice reduce to?
**Answer:** How is it read, how is it written, and is it shared across more than one thread?
**Why it matters:** The single decision process underlying all four of this week's individual topics.
**Common trap:** Naming an interface (List, Map, Queue) as if that alone determines the implementation.
**Related:** `05-collection-selection-decision-matrix.md`

## Card 14

**Prompt:** When does defaulting to ArrayList actually hurt?
**Answer:** When the dominant operation is frequent head/tail insertion — measured ~117x slower than LinkedList for front-insertion.
**Why it matters:** A concrete, measured counter-example to "ArrayList is usually fine."
**Common trap:** Defending ArrayList as a universal default without checking the actual access pattern.
**Related:** `05-collection-selection-decision-matrix.md`

## Card 15

**Prompt:** When does defaulting to HashMap actually hurt?
**Answer:** The moment it's accessed from more than one thread — it can corrupt silently, with no exception, measured directly.
**Why it matters:** A correctness failure, not just a performance one.
**Common trap:** Assuming a HashMap "probably won't be accessed concurrently in practice."
**Related:** `05-collection-selection-decision-matrix.md`
