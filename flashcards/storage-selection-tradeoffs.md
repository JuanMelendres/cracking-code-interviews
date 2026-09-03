---
title: "Flashcards: Storage Selection Trade-offs"
slug: storage-selection-tradeoffs
document_type: flashcard-deck
domain: system-design
topic_id: T-811
canonical: ../handbook/system-design/storage-selection-tradeoffs.md
last_updated: 2026-08-06
---

# Flashcards: Storage Selection Trade-offs

**Canonical chapter:** [`syllabus/11-system-design/storage-selection-tradeoffs.md`](../syllabus/11-system-design/storage-selection-tradeoffs.md)

## Card: The first question in storage selection

**Prompt:**
What's the first question in storage selection?

**Answer:**
What are the actual read/write access patterns — not "which technology is trendy."

**Why it matters:**
Anchors every subsequent decision in evidence rather than reputation.

**Common trap:**
Naming a technology before articulating the access pattern.

**Related:**
[Core Concepts](../syllabus/11-system-design/storage-selection-tradeoffs.md#core-concepts)

## Card: The four storage categories

**Prompt:**
Name the four storage categories in this chapter.

**Answer:**
Relational, document, key-value, wide-column.

**Why it matters:**
Prevents treating "NoSQL" as a single, undifferentiated alternative to relational.

**Common trap:**
Assuming all non-relational stores share the same trade-offs.

**Related:**
[Trade-offs](../syllabus/11-system-design/storage-selection-tradeoffs.md#trade-offs)

## Card: The hidden cost of polyglot persistence

**Prompt:**
What's the hidden cost of polyglot persistence?

**Answer:**
Ongoing operational burden — backup, monitoring, on-call expertise — for every additional storage technology.

**Why it matters:**
The reason polyglot persistence is a cost/benefit call, not a default good practice.

**Common trap:**
Adopting a second storage technology without weighing this cost explicitly.

**Related:**
[Core Concepts](../syllabus/11-system-design/storage-selection-tradeoffs.md#core-concepts)
