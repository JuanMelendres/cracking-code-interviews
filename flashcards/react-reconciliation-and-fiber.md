---
title: "Flashcards: React Reconciliation and the Fiber Architecture"
slug: react-reconciliation-and-fiber
document_type: flashcard-deck
domain: frontend
topic_id: F-112
tier: Advanced
canonical: ../syllabus/21-frontend-web/react-reconciliation-and-fiber.md
last_updated: 2026-09-07
---

# Flashcards: React Reconciliation and the Fiber Architecture

**Canonical chapter:** [`syllabus/21-frontend-web/react-reconciliation-and-fiber.md`](../syllabus/21-frontend-web/react-reconciliation-and-fiber.md)

## Card: Type change vs. prop change

**Prompt:**
What's the difference in outcome between changing an element's TYPE vs. changing a PROP on the same type, at the same JSX position?

**Answer:**
Type change: React destroys the entire old subtree and builds a new one — all state and DOM nodes lost. Prop change on the same type: React patches the existing subtree in place — state and DOM node identity preserved.

**Why it matters:**
Verified directly: a counter reset to 0 on a type change, but survived an unrelated prop change on the same type.

**Common trap:**
Conditionally rendering different component types for what should be "the same thing in a different state," accidentally losing state on the switch.

**Related:**
[React Reconciliation and the Fiber Architecture](../syllabus/21-frontend-web/react-reconciliation-and-fiber.md)

## Card: Batching guarantee, precisely

**Prompt:**
What does React's batching guarantee actually promise?

**Answer:**
Multiple state updates within the same synchronous execution context (e.g., one event handler) are grouped into a single render + commit, not one per update. It does NOT promise that later code in the same handler synchronously sees the updated value.

**Why it matters:**
Verified with a real commit counter: 3 simultaneous updates cost the identical single commit as 1 update.

**Common trap:**
Confusing "batched into one commit" with "immediately readable after the setState call" — those are separate concerns (the latter is the stale-closure topic from the useReducer chapter).

**Related:**
[React Reconciliation and the Fiber Architecture](../syllabus/21-frontend-web/react-reconciliation-and-fiber.md)
