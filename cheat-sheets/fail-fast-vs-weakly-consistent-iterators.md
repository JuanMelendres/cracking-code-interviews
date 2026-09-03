---
title: "Cheat Sheet: Fail-Fast vs. Weakly-Consistent Iterators"
slug: fail-fast-vs-weakly-consistent-iterators
document_type: cheat-sheet
domain: collections
topic_id: T-208
canonical: ../handbook/collections/fail-fast-vs-weakly-consistent-iterators.md
last_updated: 2026-09-02
---

# Fail-Fast vs. Weakly-Consistent Iterators

**Canonical chapter:** [`syllabus/02-java/collections/fail-fast-vs-weakly-consistent-iterators.md`](../syllabus/02-java/collections/fail-fast-vs-weakly-consistent-iterators.md)

## Core Mental Model

A fail-fast iterator isn't watching the collection for changes — it's comparing two counters, only at moments it happens to check. This is a cheap, best-effort tripwire, not a lock.

## Essential Definitions

- **`modCount`/`expectedModCount`** — every structural modification (add/remove) bumps `modCount`; an iterator captures `expectedModCount` at creation; every `next()` compares them and throws on mismatch.
- **Structural vs. non-structural** — `add`/`remove` bump `modCount`; `set`/`get` never do.
- **Weakly consistent** — never throws `ConcurrentModificationException`; covers two genuinely different real contracts: `CopyOnWriteArrayList`'s fixed snapshot, and `ConcurrentHashMap`'s live traversal that may reflect concurrent writes.

## Decision Table

| Question | Answer |
|---|---|
| Collection ever mutated by more than one thread? | Plain fail-fast detection is not a safety mechanism — choose a genuinely concurrent-safe collection |
| Read-heavy, rare writes, need a stable fixed iteration view? | `CopyOnWriteArrayList` |
| Write-heavy, "may or may not see a concurrent write" acceptable? | `ConcurrentHashMap`/`ConcurrentLinkedQueue` |
| Only need to remove during single-threaded iteration? | `Iterator.remove()` / `Collection.removeIf()` |

## Common Pitfalls

- Treating absence of `ConcurrentModificationException` as proof a loop was safe — it isn't; removing the second-to-last element of an `ArrayList` produces zero exception despite a real structural modification.
- Calling `list.remove(value)`/`.add()` directly inside a for-each loop instead of `Iterator.remove()`.
- Assuming `CopyOnWriteArrayList` and `ConcurrentHashMap` provide the identical guarantee — one is a fixed snapshot, the other may reflect concurrent writes.
- Using `Collections.synchronizedList()` and forgetting iteration still needs an explicit external `synchronized` block per its own contract.

## Interview Answer Skeleton

**30-sec:** Fail-fast iterators compare a `modCount` counter on `next()` and throw on mismatch — documented best-effort, not guaranteed; removing the second-to-last element slips through with zero exception. Weakly-consistent iterators never throw and cover two different contracts: a fixed snapshot (COW) vs may-reflect-concurrent-writes (`ConcurrentHashMap`).

**2-min:** Add the real reflective proof (`set`/`get` never bump `modCount`; `add`/`remove` do) and the real, reproduced second-to-last-element quirk: `hasNext()`'s `cursor != size` check returns false before `next()`'s comparison ever runs — a real gap in the tripwire, not hypothetical.

**Whiteboard:** Iterator creation captures `expectedModCount`; every `next()` compares it to live `modCount`. Draw the quirk branch separately: `hasNext()` can end the loop before `next()`'s comparison runs. Circle it: "this is why 'no exception' isn't proof of safety."

**Staff-level framing:** Cheap, opportunistic bug-detection mechanisms (a version counter checked only at specific points) are valuable but never a correctness guarantee — the same distinction applies to optimistic-locking version checks and periodic health checks. "No exception thrown" is weak evidence, not strong evidence.

## Production Warning Signs

- Intermittent `ConcurrentModificationException` under production load PLUS a rarer, unnoticed missing-entry bug (no exception at all) from the identical plain, unsynchronized `ArrayList` shared across threads — fix: `CopyOnWriteArrayList` for a genuinely safe, structural guarantee.

## Related

- `syllabus/02-java/collections/concurrenthashmap-internals.md`
- `syllabus/02-java/collections/collection-selection-decision-matrix.md`
- `syllabus/02-java/collections/copyonwritearraylist-and-copy-on-write-tradeoffs.md`
