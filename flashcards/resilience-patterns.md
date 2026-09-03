---
title: "Flashcards: Resilience Patterns: Circuit Breaker, Retry Jitter, Timeouts, and Bulkheads"
slug: resilience-patterns
document_type: flashcard-deck
domain: system-design
topic_id: T-515
canonical: ../handbook/system-design/resilience-patterns.md
last_updated: 2026-08-06
---

# Flashcards: Resilience Patterns: Circuit Breaker, Retry Jitter, Timeouts, and Bulkheads

**Canonical chapter:** [`syllabus/11-system-design/resilience-patterns.md`](../syllabus/11-system-design/resilience-patterns.md)

## Card: What a circuit breaker's OPEN state saves

**Prompt:**
What does a circuit breaker's OPEN state actually save, measured?

**Answer:**
Converts a call that would cost the full downstream timeout (e.g., 200ms) into one that fails in ~0ms — real, quantified latency savings during an outage.

**Why it matters:**
The concrete, measurable benefit behind an otherwise abstract pattern name.

**Common trap:**
Describing circuit breakers only qualitatively ("stops calling a down service") without the quantified latency benefit.

**Related:**
[Internal Implementation](../syllabus/11-system-design/resilience-patterns.md#internal-implementation)

## Card: What jitter fixes

**Prompt:**
What does jitter fix about retry backoff, precisely?

**Answer:**
Without it, every client retries at the exact same instant on every attempt (measured, not theoretical) — a retry storm risk. Jitter spreads retry instants across the backoff window.

**Why it matters:**
Distinguishes the synchronization problem (jitter's job) from the load-amplification problem (retry budgets' job).

**Common trap:**
Assuming exponential backoff alone (without jitter) prevents retry storms.

**Related:**
[Internal Implementation](../syllabus/11-system-design/resilience-patterns.md#internal-implementation)

## Card: What a bulkhead prevents

**Prompt:**
What is a bulkhead, and what specific failure mode does it prevent?

**Answer:**
A per-dependency resource pool (threads/connections); prevents one slow/failing dependency from exhausting a shared pool and starving callers of unrelated, healthy dependencies.

**Why it matters:**
Connects directly to the executor unbounded-queue failure mode, one layer down.

**Common trap:**
Sharing one resource pool across dependencies with very different latency/failure profiles.

**Related:**
[Definition and Purpose](../syllabus/11-system-design/resilience-patterns.md#definition-and-purpose)
