---
title: "Flashcards: Chaos Engineering"
slug: chaos-engineering-fault-injection-and-resilience-verification
document_type: flashcard-deck
domain: 13-observability
topic_id: T-2423
canonical: ../syllabus/13-observability/chaos-engineering-fault-injection-and-resilience-verification.md
last_updated: 2026-09-21
---

# Flashcards: Chaos Engineering

**Canonical chapter:** [`syllabus/13-observability/chaos-engineering-fault-injection-and-resilience-verification.md`](../syllabus/13-observability/chaos-engineering-fault-injection-and-resilience-verification.md)

## Card: Chaos engineering vs. a circuit breaker

**Prompt:**
What's the difference between a chaos experiment and a circuit breaker?

**Answer:**
A circuit breaker is the resilience implementation that reacts to a real failure at runtime. A chaos experiment is the deliberate, controlled practice of triggering a real failure on purpose to verify the circuit breaker (and its alerting) actually works as claimed. A circuit breaker can have an undiscovered bug that never trips, invisible until either a real incident or a chaos experiment exercises it.

**Why it matters:**
The most common conflation interviewers probe for in this topic.

**Common trap:**
Describing them as the same thing rather than verification-vs-implementation.

**Related:**
[Level 2 — Working Knowledge](../syllabus/13-observability/chaos-engineering-fault-injection-and-resilience-verification.md#level-2-working-knowledge)

## Card: Why blast-radius minimization matters

**Prompt:**
What separates a legitimate chaos experiment from recklessly breaking production?

**Answer:**
Scope and reversibility: a real experiment targets a small, well-defined cohort of real traffic (a canary population, a percentage of requests) and can be aborted immediately. A real demo scoped its injected fault to exactly 25% of traffic (not 100%) as a working example of this principle.

**Why it matters:**
Without this, "chaos engineering" is indistinguishable from an unplanned outage.

**Common trap:**
Injecting a fault at 100% of production traffic with no abort mechanism on a first attempt.

**Related:**
[Core Concepts](../syllabus/13-observability/chaos-engineering-fault-injection-and-resilience-verification.md#core-concepts)

## Card: Real measured evidence — detection and recovery

**Prompt:**
What did a real demo measure when a fault was injected against a 25% canary cohort?

**Answer:**
Real multi-window burn-rate monitoring (the same technique documented for T-2409) detected the injected fault 1144ms after injection began, after 12 real requests. After rollback, the alert cleared 310ms later, confirming real recovery. Re-run twice with identical results (only clock jitter differed).

**Why it matters:**
Turns an abstract practice into concrete, reproducible, measured evidence — the demo verifies both detection AND recovery, not just that failure occurred.

**Common trap:**
Describing only the failure-injection half of an experiment, omitting the steady-state hypothesis or recovery verification.

**Related:**
[Internal Implementation](../syllabus/13-observability/chaos-engineering-fault-injection-and-resilience-verification.md#internal-implementation)
