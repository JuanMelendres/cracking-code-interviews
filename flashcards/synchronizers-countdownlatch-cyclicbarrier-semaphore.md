---
title: "Flashcards: java.util.concurrent Synchronizers"
slug: synchronizers-countdownlatch-cyclicbarrier-semaphore
document_type: flashcard-deck
domain: 02-java/concurrency
topic_id: T-417
canonical: ../syllabus/02-java/concurrency/synchronizers-countdownlatch-cyclicbarrier-semaphore.md
last_updated: 2026-09-11
---

# Flashcards: java.util.concurrent Synchronizers

**Canonical chapter:** [`syllabus/02-java/concurrency/synchronizers-countdownlatch-cyclicbarrier-semaphore.md`](../syllabus/02-java/concurrency/synchronizers-countdownlatch-cyclicbarrier-semaphore.md)

## Card: CountDownLatch cannot be reset

**Prompt:**
Can a `CountDownLatch` be reused for a second round of coordination once its count reaches zero?

**Answer:**
No — it's one-shot. A real demo shows a second `await()` call on an exhausted latch returns immediately, without waiting at all. A new round needs a brand-new instance.

**Why it matters:**
A common real bug: reusing a latch expecting round-two coordination, silently getting no coordination at all.

**Common trap:**
Confusing `CountDownLatch` with `CyclicBarrier`, which genuinely is reusable across rounds.

**Related:**
[java.util.concurrent Synchronizers](../syllabus/02-java/concurrency/synchronizers-countdownlatch-cyclicbarrier-semaphore.md)

## Card: Who can signal vs. who must wait

**Prompt:**
In `CountDownLatch`, must the same threads that call `countDown()` also call `await()`?

**Answer:**
No — the signaling and waiting roles are fully decoupled; any thread can call `countDown()`, and any thread can `await()`. This is different from `CyclicBarrier`, where the same fixed set of parties must all `await()` each round.

**Why it matters:**
A precise distinction interviewers use to check real understanding versus rote memorization of "they're both counters."

**Common trap:**
Assuming both synchronizers require the same threads to signal and wait.

**Related:**
[java.util.concurrent Synchronizers](../syllabus/02-java/concurrency/synchronizers-countdownlatch-cyclicbarrier-semaphore.md)

## Card: Semaphore enforces count, not identity

**Prompt:**
Does a `Semaphore` require the same thread that called `acquire()` to be the one that calls `release()`?

**Answer:**
No — `Semaphore` enforces a bounded concurrency limit purely by count. Any thread may `release()`, regardless of which thread performed the matching `acquire()`.

**Why it matters:**
A real, common source of over-release bugs if `release()` is called in a path not guaranteed to follow a successful `acquire()`.

**Common trap:**
Assuming `Semaphore` tracks per-thread ownership the way a `ReentrantLock` does.

**Related:**
[java.util.concurrent Synchronizers](../syllabus/02-java/concurrency/synchronizers-countdownlatch-cyclicbarrier-semaphore.md)
