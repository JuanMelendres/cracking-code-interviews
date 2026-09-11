---
title: "Cheat Sheet: java.util.concurrent Synchronizers"
slug: synchronizers-countdownlatch-cyclicbarrier-semaphore
document_type: cheat-sheet
domain: 02-java/concurrency
topic_id: T-417
canonical: ../syllabus/02-java/concurrency/synchronizers-countdownlatch-cyclicbarrier-semaphore.md
last_updated: 2026-09-11
---

# java.util.concurrent Synchronizers: CountDownLatch, CyclicBarrier, Semaphore

**Canonical chapter:** [`syllabus/02-java/concurrency/synchronizers-countdownlatch-cyclicbarrier-semaphore.md`](../syllabus/02-java/concurrency/synchronizers-countdownlatch-cyclicbarrier-semaphore.md)

## Core Mental Model

One question per synchronizer. `CountDownLatch`: "has this one-time count reached zero?" — opens once, stays open forever. `CyclicBarrier`: "have all N parties arrived at this round's checkpoint?" — closes and reopens automatically, round after round. `Semaphore`: "are there fewer than N holders right now?" — a continuously-enforced capacity limit, checked on every `acquire()`.

## Essential Definitions

- **`CountDownLatch`** — one-shot gate; `countDown()` and `await()` roles are decoupled (any thread can signal).
- **`CyclicBarrier`** — reusable gate; the same fixed set of parties must all `await()` each round.
- **`Semaphore`** — bounded concurrency by count, not thread identity; any thread may `release()`.

## Decision Table

| Symptom | Likely cause | Fix |
|---|---|---|
| A second "round" silently doesn't wait | Reusing an exhausted `CountDownLatch` | New `CountDownLatch` per round, or use `CyclicBarrier` |
| A `CyclicBarrier` round never completes | One party never calls `await()` | Use `await(timeout, unit)`, handle `TimeoutException`/`BrokenBarrierException` |
| More concurrent holders than the configured limit | `release()` without a matching successful `acquire()` | `acquire()` before `try`, `release()` only in a `finally` after success |
| Coordination logic getting complicated with manual counters/`wait()`/`notify()` | Missing purpose-built synchronizer | Match the coordination shape to the right synchronizer |

## Common Pitfalls

- Reusing a `CountDownLatch` expecting a second round to wait — it's exhausted after reaching zero once.
- Not handling `BrokenBarrierException`/`TimeoutException` on `CyclicBarrier.await()` — one failed party breaks the barrier for everyone.
- Calling `release()` without a guaranteed prior successful `acquire()` — a real over-release bug that breaks the capacity guarantee.

## Interview Answer Skeleton

**30-sec:** `CountDownLatch` is one-shot; `CyclicBarrier` is reusable across rounds for a fixed party set; `Semaphore` enforces a continuous capacity limit by count, not identity.

**2-min:** Add: a real demo proves `CountDownLatch` cannot be reset (second `await()` returns immediately), `CyclicBarrier` reused correctly across two independent rounds, and `Semaphore`'s max-concurrent-holder count measured directly under genuine contention.

**Staff-level framing:** Match the synchronizer to the actual coordination shape rather than hand-rolling equivalent logic with a raw counter and `wait()`/`notify()` — each of the three encodes a genuinely different real-world coordination pattern.

## Related

- syllabus/02-java/language-core/java-time-api.md
