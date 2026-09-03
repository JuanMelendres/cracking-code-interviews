---
title: "Flashcards: DDD Tactical Design — Aggregates"
slug: ddd-tactical-design-aggregates
document_type: flashcard-deck
domain: architecture
topic_id: T-903
canonical: ../handbook/architecture/ddd-tactical-design-aggregates.md
last_updated: 2026-08-06
---

# Flashcards: DDD Tactical Design — Aggregates

**Canonical chapter:** [`syllabus/17-architecture/ddd-tactical-design-aggregates.md`](../syllabus/17-architecture/ddd-tactical-design-aggregates.md)

## Card: What an aggregate root is

**Prompt:**
What is an aggregate root?

**Answer:**
The only object in an aggregate that external code is allowed to reference directly.

**Why it matters:**
Enforces that all mutations go through methods that can uphold the aggregate's invariants.

**Common trap:**
Giving an internal entity its own repository, bypassing the root.

**Related:**
[Core Concepts](../syllabus/17-architecture/ddd-tactical-design-aggregates.md#core-concepts)

## Card: What decides aggregate boundaries

**Prompt:**
What decides aggregate boundaries?

**Answer:**
The true consistency invariant — not object composition or conceptual relatedness.

**Why it matters:**
The single principled test that prevents both under- and over-sizing.

**Common trap:**
Grouping objects because they seem related, not because an invariant requires it.

**Related:**
[Decision Framework](../syllabus/17-architecture/ddd-tactical-design-aggregates.md#decision-framework)

## Card: Repositories per aggregate

**Prompt:**
How many repositories does an aggregate get?

**Answer:**
One, for the root only.

**Why it matters:**
Prevents code from bypassing the root's invariant-enforcing methods.

**Common trap:**
Creating a repository for every entity class regardless of aggregate membership.

**Related:**
[Core Concepts](../syllabus/17-architecture/ddd-tactical-design-aggregates.md#core-concepts)
