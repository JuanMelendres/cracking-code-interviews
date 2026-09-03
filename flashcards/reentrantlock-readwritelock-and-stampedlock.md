---
title: "Flashcards: ReentrantLock, ReadWriteLock, and StampedLock"
slug: reentrantlock-readwritelock-and-stampedlock
document_type: flashcard-deck
domain: concurrency
topic_id: T-404
canonical: ../handbook/concurrency/reentrantlock-readwritelock-and-stampedlock.md
last_updated: 2026-09-02
---

# Flashcards: ReentrantLock, ReadWriteLock, and StampedLock

**Canonical chapter:** [`syllabus/02-java/concurrency/reentrantlock-readwritelock-and-stampedlock.md`](../syllabus/02-java/concurrency/reentrantlock-readwritelock-and-stampedlock.md)

## Card: ReentrantLock's real additions

**Prompt:**
Name three real capabilities `ReentrantLock` has that `synchronized` lacks.

**Answer:**
`tryLock()` (non-blocking/timed), `lockInterruptibly()`, fairness policy, and multiple `Condition` wait-sets per lock.

**Why it matters:**
The concrete reasons to reach for `ReentrantLock` over `synchronized` at all.

**Common trap:**
Using `ReentrantLock` without needing any of these, adding complexity for no real benefit.

**Related:**
[Core Concepts](../syllabus/02-java/concurrency/reentrantlock-readwritelock-and-stampedlock.md#core-concepts)

## Card: ReadWriteLock's guarantee

**Prompt:**
What can happen concurrently under a `ReentrantReadWriteLock`?

**Answer:**
Multiple readers may hold the read lock simultaneously; the write lock is exclusive against both other writers and all readers.

**Why it matters:**
Measured directly: readers' hold intervals genuinely overlapped in this chapter's real trace.

**Common trap:**
Using it for a write-heavy workload, where it offers no real benefit over a plain lock.

**Related:**
[Internal Implementation](../syllabus/02-java/concurrency/reentrantlock-readwritelock-and-stampedlock.md#internal-implementation)

## Card: StampedLock's mandatory step

**Prompt:**
What must you always do after a `StampedLock.tryOptimisticRead()`?

**Answer:**
Call `validate(stamp)` before trusting the read; if it returns `false`, fall back to a real `readLock()` and re-read.

**Why it matters:**
Skipping this is a silent, real correctness bug — proven by this chapter's deterministic invalidation trace.

**Common trap:**
Treating `StampedLock` as a drop-in faster lock without implementing the validate-and-fallback protocol.

**Related:**
[Internal Implementation](../syllabus/02-java/concurrency/reentrantlock-readwritelock-and-stampedlock.md#internal-implementation)
