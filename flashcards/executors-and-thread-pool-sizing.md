---
title: "Flashcards: Executors and Thread Pool Sizing"
slug: executors-and-thread-pool-sizing
document_type: flashcard-deck
domain: concurrency
topic_id: T-406
canonical: ../handbook/concurrency/executors-and-thread-pool-sizing.md
last_updated: 2026-08-06
---

# Flashcards: Executors and Thread Pool Sizing

**Canonical chapter:** [`handbook/concurrency/executors-and-thread-pool-sizing.md`](../handbook/concurrency/executors-and-thread-pool-sizing.md)

## Card: Default queue's consequence

**Prompt:**
What queue does `Executors.newFixedThreadPool()` use by default, and what's the consequence?

**Answer:**
An unbounded `LinkedBlockingQueue` — tasks are never rejected, so memory grows without limit under sustained overload.

**Why it matters:**
The hidden default most candidates have used without examining.

**Common trap:**
Assuming "never rejects" is a purely positive property.

**Related:**
[Internal Implementation](../handbook/concurrency/executors-and-thread-pool-sizing.md#internal-implementation)

## Card: Getting real backpressure

**Prompt:**
How do you get real backpressure from a thread pool?

**Answer:**
Build a `ThreadPoolExecutor` with a bounded queue and an explicit `RejectedExecutionHandler` (e.g., `AbortPolicy`).

**Why it matters:**
Converts a silent, catastrophic failure mode into a loud, actionable one.

**Common trap:**
Assuming pool size alone controls memory usage under load.

**Related:**
[Internal Implementation](../handbook/concurrency/executors-and-thread-pool-sizing.md#internal-implementation)

## Card: CPU-bound vs IO-bound sizing

**Prompt:**
How should CPU-bound vs IO-bound pool sizing differ?

**Answer:**
CPU-bound scales near `N_cores`; IO-bound scales with the wait/compute ratio (Little's Law), since threads spend most time blocked, not computing.

**Why it matters:**
A single pool-sizing heuristic is wrong for at least one of the two profiles.

**Common trap:**
Applying the same sizing rule to both workload types.

**Related:**
[Core Concepts](../handbook/concurrency/executors-and-thread-pool-sizing.md#core-concepts)
