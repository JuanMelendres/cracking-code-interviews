---
title: "Cheat Sheet: ReentrantLock, ReadWriteLock, and StampedLock"
slug: reentrantlock-readwritelock-and-stampedlock
document_type: cheat-sheet
domain: concurrency
topic_id: T-404
canonical: ../handbook/concurrency/reentrantlock-readwritelock-and-stampedlock.md
last_updated: 2026-09-02
---

# ReentrantLock, ReadWriteLock, and StampedLock

**Canonical chapter:** [`handbook/concurrency/reentrantlock-readwritelock-and-stampedlock.md`](../handbook/concurrency/reentrantlock-readwritelock-and-stampedlock.md)

## Core Mental Model

Every lock in this family answers "who gets in, and under what condition?" differently. `synchronized`: whoever gets there first, unconditionally. `ReentrantLock`: explicit control (timeouts, fairness). `ReadWriteLock`: two different answers by intent. `StampedLock`: skip asking for reads, validate after the fact.

## Essential Definitions

- **`ReentrantLock`** — adds `tryLock()` (non-blocking/timed), `lockInterruptibly()`, fairness policy, multiple `Condition` wait-sets — none of which `synchronized` offers.
- **`ReentrantReadWriteLock`** — multiple readers can hold the read lock simultaneously; the write lock is exclusive against both writers and readers.
- **`StampedLock` optimistic read** — `tryOptimisticRead()` returns a stamp, no lock taken; caller reads fields, then MUST call `validate(stamp)`; `false` means fall back to a real `readLock()`.

## Decision Table

| Question | Answer |
|---|---|
| Need timeout, interruptibility, fairness, or multiple wait-conditions? | `ReentrantLock` |
| Read-heavy workload with `synchronized`/`ReentrantLock` visibly serializing reads? | `ReadWriteLock` |
| Extreme read-to-write ratio, fields safely re-readable (`volatile`)? | `StampedLock` optimistic read — only with disciplined `validate()` |
| Fairness actually required, or is unfair's higher throughput preferable? | Default unfair unless real starvation observed |

## Key Numbers

- Fairness, 40,000 acquisitions, 4 threads: unfair spread 7255/10000, elapsed 6ms; fair spread 1038/10000, elapsed 75ms — real ~12x throughput cost for a much more even distribution.
- `ReadWriteLock` (4 readers, ~150ms each): all overlap, total 163ms. Plain lock: serialized, total 620ms.
- `StampedLock` optimistic read vs read lock, 20,000,000 iterations: 31ms vs 89ms — ~3x faster with no contention.

## Common Pitfalls

- Reaching for `ReentrantLock` when `synchronized` would do, adding API surface without using any extra capability.
- Using a plain exclusive lock to guard a read-heavy value, serializing reads that could run concurrently.
- Forgetting `unlock()` in a `finally` block — unlike `synchronized`, the JVM doesn't auto-release an explicit lock on exception.
- Using `StampedLock`'s optimistic read without calling `validate()` — a real, silent correctness bug, not just a performance miss.

## Interview Answer Skeleton

**30-sec:** `ReentrantLock` adds real capabilities `synchronized` lacks (`tryLock`, `lockInterruptibly`, fairness, multiple `Condition`s). `ReadWriteLock` lets readers hold concurrently while writers stay exclusive. `StampedLock`'s optimistic mode takes no lock at all, validating afterward — real, measurably faster under low contention, but only correct with disciplined `validate()`.

**2-min:** Add the real, measured fairness/barging trade-off (~12x cost), the real overlapping-hold-interval proof for `ReadWriteLock`, and the deterministic `StampedLock` invalidation trace (`CountDownLatch`-forced, not timing-guessed): `validate(stamp)` correctly returns `false` after a real concurrent write commits.

**Whiteboard:** `synchronized` → `ReentrantLock` (explicit control) → `ReadWriteLock` (read/write distinction) → `StampedLock` (optimistic, lock-free reads). Annotate each arrow with the specific limitation being solved.

**Staff-level framing:** This progression encodes more information about *intent* (read vs write, optimistic vs pessimistic) into the synchronization primitive itself — the same reasoning motivates read replicas in databases and MVCC. Introduce complexity only when justified by a real, measured bottleneck.

## Production Warning Signs

- A read-heavy config cache serializes every request behind a plain `synchronized` block, capping throughput far below CPU capacity — thread dumps show dozens of threads `BLOCKED` on the same monitor for a read-only access. Fix: `ReentrantReadWriteLock`.

## Related

- `handbook/concurrency/atomics-cas-and-the-aba-problem.md`
- `handbook/concurrency/executors-and-thread-pool-sizing.md`
