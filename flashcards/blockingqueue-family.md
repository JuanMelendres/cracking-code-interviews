---
title: "Flashcards: BlockingQueue Family and Producer-Consumer"
slug: blockingqueue-family
document_type: flashcard-deck
domain: collections
topic_id: T-207
canonical: ../handbook/collections/blockingqueue-family.md
last_updated: 2026-08-06
---

# Flashcards: BlockingQueue Family and Producer-Consumer

**Canonical chapter:** [`handbook/collections/blockingqueue-family.md`](../handbook/collections/blockingqueue-family.md)

## Card: When put() blocks

**Prompt:**
When does `BlockingQueue.put()` block?

**Answer:**
When the queue is at capacity (full) — the calling thread genuinely parks until a `take()` elsewhere frees space, measured directly via thread state.

**Why it matters:**
The mechanism that provides real backpressure in a producer-consumer pipeline.

**Common trap:**
Assuming `put()` on a full queue returns immediately or throws, rather than blocking.

**Related:**
[Internal Implementation](../handbook/collections/blockingqueue-family.md#internal-implementation)

## Card: SynchronousQueue's actual capacity

**Prompt:**
What is `SynchronousQueue`'s internal capacity?

**Answer:**
Zero — it's a direct handoff; `put()` only succeeds once a `take()` is already waiting for that exact element.

**Why it matters:**
A common misconception is treating it as a very small buffer rather than genuinely capacity-zero.

**Common trap:**
Assuming it behaves like a capacity-1 queue.

**Related:**
[Internal Implementation](../handbook/collections/blockingqueue-family.md#internal-implementation)

## Card: Why unbounded queues are dangerous

**Prompt:**
Why is an unbounded `BlockingQueue` a risky default in a production pipeline?

**Answer:**
It removes backpressure entirely — a slow consumer no longer slows the producer, it just lets the queue grow until memory runs out.

**Why it matters:**
Converts a visible, gradual slowdown into an eventual, harder-to-diagnose OutOfMemoryError crash.

**Common trap:**
Constructing a `LinkedBlockingQueue` with no capacity argument, silently defaulting to unbounded.

**Related:**
[Production Scenarios](../handbook/collections/blockingqueue-family.md#production-scenarios)
