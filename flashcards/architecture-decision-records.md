---
title: "Flashcards: Architecture Decision Records"
slug: architecture-decision-records
document_type: flashcard-deck
domain: architecture
topic_id: T-916
canonical: ../handbook/architecture/architecture-decision-records.md
last_updated: 2026-09-02
---

# Flashcards: Architecture Decision Records

**Canonical chapter:** [`handbook/architecture/architecture-decision-records.md`](../handbook/architecture/architecture-decision-records.md)

## Card: The four required ADR sections

**Prompt:**
What are the four sections Michael Nygard's original ADR pattern requires?

**Answer:**
Status, Context, Decision, Consequences.

**Why it matters:**
The minimal, durable core of the pattern — deliberately lightweight so teams actually keep doing it.

**Common trap:**
Assuming a longer template (MADR-style, with Decision Drivers and Considered Options) replaces these four rather than extending them.

**Related:**
[Core Concepts](../handbook/architecture/architecture-decision-records.md#core-concepts)

## Card: Editing vs. superseding

**Prompt:**
When a past ADR's decision no longer holds, do you edit it or write a new one?

**Answer:**
Write a new ADR and mark the old one Superseded, linked to the new one. Never edit an accepted ADR's content in place.

**Why it matters:**
Preserves the historical record of what was known and believed at the time — the exact thing that makes the pattern useful for evaluating whether circumstances changed or the original reasoning was simply wrong.

**Common trap:**
Treating ADRs as living documents to keep current, like a wiki page.

**Related:**
[Interview Questions, Question 2](../handbook/architecture/architecture-decision-records.md#interview-questions)

## Card: The real Consequences test

**Prompt:**
What's the real test for whether an ADR's Consequences section is any good?

**Answer:**
It includes at least one real, honestly-stated negative outcome — not just benefits.

**Why it matters:**
An ADR listing only positive consequences signals the decision wasn't honestly interrogated before being written up.

**Common trap:**
Writing Consequences as a justification restatement of the chosen option's pros, already covered in Considered Options.

**Related:**
[Common Mistakes](../handbook/architecture/architecture-decision-records.md#common-mistakes)
