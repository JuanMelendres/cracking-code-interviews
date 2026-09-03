---
title: "Flashcards: Clean and Hexagonal Architecture"
slug: clean-hexagonal-architecture
document_type: flashcard-deck
domain: architecture
topic_id: T-901
canonical: ../handbook/architecture/clean-hexagonal-architecture.md
last_updated: 2026-08-06
---

# Flashcards: Clean and Hexagonal Architecture

**Canonical chapter:** [`syllabus/17-architecture/clean-hexagonal-architecture.md`](../syllabus/17-architecture/clean-hexagonal-architecture.md)

## Card: What a port is

**Prompt:**
What is a port?

**Answer:**
An interface owned by the domain, stating what it needs or offers.

**Why it matters:**
The core mechanism that lets the domain avoid depending on infrastructure directly.

**Common trap:**
Placing the interface in the infrastructure package "for convenience."

**Related:**
[Core Concepts](../syllabus/17-architecture/clean-hexagonal-architecture.md#core-concepts)

## Card: What an adapter is

**Prompt:**
What is an adapter?

**Answer:**
A concrete implementation of a port, living in infrastructure.

**Why it matters:**
The thing that changes on an infrastructure swap, while the domain doesn't.

**Common trap:**
Calling a concrete class a "port."

**Related:**
[Core Concepts](../syllabus/17-architecture/clean-hexagonal-architecture.md#core-concepts)

## Card: When NOT to use hexagonal architecture

**Prompt:**
When should you NOT use hexagonal architecture?

**Answer:**
A thin CRUD service with no real business rules to protect.

**Why it matters:**
The single most differentiating thing a candidate can volunteer on this topic.

**Common trap:**
Answering "use it everywhere" unconditionally.

**Related:**
[Trade-offs](../syllabus/17-architecture/clean-hexagonal-architecture.md#trade-offs)
