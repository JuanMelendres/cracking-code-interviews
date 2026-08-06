---
title: "Flashcards: Microservice Decomposition and the Monolith Trade-off"
slug: microservice-decomposition-and-monolith-tradeoff
document_type: flashcard-deck
domain: architecture
topic_id: T-907
canonical: ../handbook/architecture/microservice-decomposition-and-monolith-tradeoff.md
last_updated: 2026-08-06
---

# Flashcards: Microservice Decomposition and the Monolith Trade-off

**Canonical chapter:** [`handbook/architecture/microservice-decomposition-and-monolith-tradeoff.md`](../handbook/architecture/microservice-decomposition-and-monolith-tradeoff.md)

## Card: The actual boundary test

**Prompt:**
What's the actual test for where to draw a service boundary?

**Answer:**
Where strong single-transaction consistency is NOT required across the line — the same test as an aggregate boundary.

**Why it matters:**
Prevents boundary decisions driven by code organization or team preference instead of consistency requirements.

**Common trap:**
Splitting by table or file proximity rather than consistency.

**Related:**
[Core Concepts](../handbook/architecture/microservice-decomposition-and-monolith-tradeoff.md#core-concepts)

## Card: Two services, one transaction

**Prompt:**
Two services need one transaction — what does that signal?

**Answer:**
The boundary may be wrong, or the operation needs to become an explicitly eventually-consistent saga, not a distributed transaction.

**Why it matters:**
The default reflex (reach for 2PC) treats the symptom, not the cause.

**Common trap:**
Proposing distributed 2PC as the fix.

**Related:**
[Core Concepts](../handbook/architecture/microservice-decomposition-and-monolith-tradeoff.md#core-concepts)

## Card: Merge-back signal

**Prompt:**
Name a concrete signal that two services should be merged back.

**Answer:**
They are always co-deployed together (detectable via deployment-history correlation).

**Why it matters:**
Makes "should we merge back" an evidence-based question, not a purely qualitative one.

**Common trap:**
Treating decomposition as a one-way door.

**Related:**
[Production Scenarios](../handbook/architecture/microservice-decomposition-and-monolith-tradeoff.md#production-scenarios)

## Card: Small-team microservices question

**Prompt:**
Should a 4-engineer team default to microservices?

**Answer:**
Generally no — the organizational benefit requires multiple independently-scheduled teams, which a team that size very likely isn't.

**Why it matters:**
One of the highest-signal questions in the register; most candidates answer "yes" reflexively.

**Common trap:**
Defending microservices as a technical best practice regardless of team size.

**Related:**
[Interview Questions](../handbook/architecture/microservice-decomposition-and-monolith-tradeoff.md#interview-questions), Question 4
