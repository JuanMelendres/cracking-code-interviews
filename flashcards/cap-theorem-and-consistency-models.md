---
title: "Flashcards: CAP Theorem and Consistency Models"
slug: cap-theorem-and-consistency-models
document_type: flashcard-deck
domain: system-design
topic_id: T-807
canonical: ../handbook/system-design/cap-theorem-and-consistency-models.md
last_updated: 2026-08-06
---

# Flashcards: CAP Theorem and Consistency Models

**Canonical chapter:** [`handbook/system-design/cap-theorem-and-consistency-models.md`](../handbook/system-design/cap-theorem-and-consistency-models.md)

## Card: When CAP applies

**Prompt:**
Does CAP apply outside of an actual network partition?

**Answer:**
No — CAP is specifically a statement about partition behavior; both C and A are achievable when no partition is occurring.

**Why it matters:**
Prevents treating CAP as a permanent, always-active trade-off.

**Common trap:**
Discussing CAP as if it constrains every design decision, not just partition behavior.

**Related:**
[Definition and Purpose](../handbook/system-design/cap-theorem-and-consistency-models.md#definition-and-purpose)

## Card: CP system behavior

**Prompt:**
What does a CP system do during a partition?

**Answer:**
Refuses requests it can't guarantee are current, on the partitioned-off side — trading availability for consistency.

**Why it matters:**
Concrete behavior, not just the label "CP."

**Common trap:**
Stating the label without describing the actual refusal behavior.

**Related:**
[Core Concepts](../handbook/system-design/cap-theorem-and-consistency-models.md#core-concepts)

## Card: AP system behavior

**Prompt:**
What does an AP system do during a partition?

**Answer:**
Continues serving requests on both sides, accepting the sides may disagree until reconciliation — trading consistency for availability.

**Why it matters:**
Concrete behavior, not just the label "AP."

**Common trap:**
Stating the label without describing the reconciliation implication.

**Related:**
[Core Concepts](../handbook/system-design/cap-theorem-and-consistency-models.md#core-concepts)

## Card: One model for a whole system?

**Prompt:**
Should one consistency model apply uniformly across a whole system?

**Answer:**
No — different data types warrant different models (e.g., strong for inventory, eventual for a recently-viewed list).

**Why it matters:**
The most sophisticated version of this topic; most candidates stop at "pick CP or AP for the system."

**Common trap:**
Choosing one consistency model for an entire system rather than per data type.

**Related:**
[Staff-Level Discussion](../handbook/system-design/cap-theorem-and-consistency-models.md#interview-answer-framework)
