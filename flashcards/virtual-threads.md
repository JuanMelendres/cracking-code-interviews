---
title: "Flashcards: Virtual Threads (Project Loom)"
slug: virtual-threads
document_type: flashcard-deck
domain: concurrency
topic_id: T-410
canonical: ../handbook/concurrency/virtual-threads.md
last_updated: 2026-08-06
---

# Flashcards: Virtual Threads (Project Loom)

**Canonical chapter:** [`handbook/concurrency/virtual-threads.md`](../handbook/concurrency/virtual-threads.md)

## Card: What a carrier does on virtual thread block

**Prompt:**
What does a virtual thread's carrier do when the virtual thread blocks on supported IO?

**Answer:**
Unmounts the virtual thread, freeing the carrier to run a different virtual thread — the blocking call doesn't tie up a platform thread.

**Why it matters:**
The core mechanism that makes virtual threads' concurrency benefit possible.

**Common trap:**
Assuming all blocking operations behave this way, including inside `synchronized`.

**Related:**
[Internal Implementation](../handbook/concurrency/virtual-threads.md#internal-implementation)

## Card: What causes pinning

**Prompt:**
What causes a virtual thread to pin its carrier?

**Answer:**
Blocking inside a `synchronized` block (or a few other cases, e.g. native calls) — the carrier can't run anything else until the call returns.

**Why it matters:**
The single most important migration hazard, with no compiler warning.

**Common trap:**
Assuming `synchronized` "just works" under virtual threads with no downside.

**Related:**
[Internal Implementation](../handbook/concurrency/virtual-threads.md#internal-implementation)

## Card: Why pooling virtual threads is wrong

**Prompt:**
Why is pooling virtual threads considered an anti-pattern?

**Answer:**
They're designed to be cheap and disposable, created per-task; pooling reimposes platform-thread-style resource-limiting thinking that virtual threads exist to eliminate.

**Why it matters:**
A common instinct carried over from platform-thread practice that no longer applies.

**Common trap:**
Building a pool of virtual threads "to be safe," adding complexity for no benefit.

**Related:**
[Java Examples](../handbook/concurrency/virtual-threads.md#java-examples)
