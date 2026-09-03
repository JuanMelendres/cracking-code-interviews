---
title: "Flashcards: ForkJoinPool and Work-Stealing"
slug: forkjoinpool-and-work-stealing
document_type: flashcard-deck
domain: concurrency
topic_id: T-408
canonical: ../handbook/concurrency/forkjoinpool-and-work-stealing.md
last_updated: 2026-09-02
---

# Flashcards: ForkJoinPool and Work-Stealing

**Canonical chapter:** [`syllabus/02-java/concurrency/forkjoinpool-and-work-stealing.md`](../syllabus/02-java/concurrency/forkjoinpool-and-work-stealing.md)

## Card: Own end vs. opposite end

**Prompt:**
Why does an idle worker steal from the opposite end of a peer's deque instead of the same end the peer is using?

**Answer:**
To minimize contention — the peer keeps working its own end uncontended while the thief takes from the far end, and the far end holds the peer's oldest (usually largest-remaining) task.

**Why it matters:**
The actual contention-minimization mechanism, not just "idle workers take work."

**Common trap:**
Describing work-stealing as generic load balancing without the deque-end mechanism.

**Related:**
[Core Concepts](../syllabus/02-java/concurrency/forkjoinpool-and-work-stealing.md#core-concepts)

## Card: The shared common pool (and the one that isn't)

**Prompt:**
Do parallel streams, `CompletableFuture`'s default `*Async` calls, and `StructuredTaskScope` all share the same thread pool?

**Answer:**
Only the first two — parallel streams and `CompletableFuture` genuinely share `ForkJoinPool.commonPool()`. `StructuredTaskScope`'s virtual threads run on a separate, dedicated `ForkJoinPool` instance, verified directly (different carrier thread name, `commonPool()`'s metrics unaffected).

**Why it matters:**
A real, easy-to-miss cross-feature contention risk for the first two — and an easy over-generalization trap for the third.

**Common trap:**
Assuming every JDK concurrency feature "backed by a ForkJoinPool" shares the exact same instance.

**Related:**
[Production Scenarios](../syllabus/02-java/concurrency/forkjoinpool-and-work-stealing.md#production-scenarios)

## Card: Verifying stealing, not assuming it

**Prompt:**
How would you verify work-stealing is actually happening, rather than assuming it from `fork()`/`RecursiveTask` usage?

**Answer:**
Check `ForkJoinPool.getStealCount()` — a real, public JDK metric, positive when stealing genuinely occurred.

**Why it matters:**
Evidence over assumption — the same discipline this whole chapter is built on.

**Common trap:**
Assuming stealing happens just because the API "supports" it, without checking.

**Related:**
[Internal Implementation](../syllabus/02-java/concurrency/forkjoinpool-and-work-stealing.md#internal-implementation)
