---
title: "Flashcards: Deadlock, Race Conditions, and Thread Diagnostics"
slug: deadlock-race-conditions-and-thread-diagnostics
document_type: flashcard-deck
domain: concurrency
topic_id: T-409
canonical: ../handbook/concurrency/deadlock-race-conditions-and-thread-diagnostics.md
last_updated: 2026-08-06
---

# Flashcards: Deadlock, Race Conditions, and Thread Diagnostics

**Canonical chapter:** [`handbook/concurrency/deadlock-race-conditions-and-thread-diagnostics.md`](../handbook/concurrency/deadlock-race-conditions-and-thread-diagnostics.md)

## Card: The real Thread.State values

**Prompt:**
What are the six real `Thread.State` values?

**Answer:**
NEW, RUNNABLE, BLOCKED, WAITING, TIMED_WAITING, TERMINATED — no separate "Running" state, and TIMED_WAITING is real (e.g., inside `Thread.sleep()`).

**Why it matters:**
Corrects a previously actively-wrong diagram in this project's own source material.

**Common trap:**
Inventing a "Running" state distinct from `RUNNABLE`, or forgetting `TIMED_WAITING`.

**Related:**
[Internal Implementation](../handbook/concurrency/deadlock-race-conditions-and-thread-diagnostics.md#internal-implementation)

## Card: Detecting a deadlock in a live JVM

**Prompt:**
How do you detect a deadlock in a live JVM?

**Answer:**
`ThreadMXBean.findDeadlockedThreads()` (what `jstack` uses under the hood) — walks the lock-ownership graph for a real cycle.

**Why it matters:**
The actual production diagnostic technique, not guessing from a thread dump.

**Common trap:**
Describing deadlock only abstractly without naming a concrete tool.

**Related:**
[Java Examples](../handbook/concurrency/deadlock-race-conditions-and-thread-diagnostics.md#java-examples)

## Card: How much data unsynchronized count++ loses

**Prompt:**
How much data can an unsynchronized `count++` lose under real concurrent load?

**Answer:**
Measured: 83.8% of updates lost with 10 threads × 100,000 increments each — not a rare edge case.

**Why it matters:**
Race conditions under concurrency are a near-certainty, not a theoretical risk.

**Common trap:**
Assuming this kind of bug is rare or unlikely to matter in practice.

**Related:**
[Internal Implementation](../handbook/concurrency/deadlock-race-conditions-and-thread-diagnostics.md#internal-implementation)
