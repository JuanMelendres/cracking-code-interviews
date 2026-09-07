---
title: "Flashcards: React useReducer and Custom Hooks"
slug: react-usereducer-and-custom-hooks
document_type: flashcard-deck
domain: frontend
topic_id: F-109/F-110
tier: Intermediate
canonical: ../syllabus/21-frontend-web/react-usereducer-and-custom-hooks.md
last_updated: 2026-09-07
---

# Flashcards: React useReducer and Custom Hooks

**Canonical chapter:** [`syllabus/21-frontend-web/react-usereducer-and-custom-hooks.md`](../syllabus/21-frontend-web/react-usereducer-and-custom-hooks.md)

## Card: useReducer vs. useState for related fields

**Prompt:**
Why can useReducer correctly derive one field from another during the same update, when two separate useState setters can't?

**Answer:**
A reducer receives the FULL previous state as an argument and computes the FULL next state in one function call — no closures involved. Two useState setters each only see their own previous value; neither has visibility into the other's pending update in the same handler.

**Why it matters:**
Verified with a real, deterministic double-update reproduction: useState version showed count=2 but lastAction="incremented to 1" (wrong); useReducer version showed count=2, lastAction="incremented to 2" (correct).

**Common trap:**
Assuming a functional setState updater (`setX(x => ...)`) fixes cross-field staleness — it only fixes same-field staleness.

**Related:**
[React useReducer and Custom Hooks](../syllabus/21-frontend-web/react-usereducer-and-custom-hooks.md)

## Card: Custom hook naming convention

**Prompt:**
What actually makes a function behave as a React hook — is the `use` prefix itself the mechanism?

**Answer:**
No — the prefix is a naming CONVENTION that lets tooling (the Rules-of-Hooks linter) and React recognize the function as one that calls other hooks internally. The actual mechanism is that it calls hooks like useState/useEffect, tying it into React's per-fiber hook-call bookkeeping.

**Why it matters:**
A `useFoo` function that calls no hooks is just a regular function with a misleading name — no Rules-of-Hooks protection applies or is needed.

**Common trap:**
Treating the naming convention as the mechanism itself.

**Related:**
[React useReducer and Custom Hooks](../syllabus/21-frontend-web/react-usereducer-and-custom-hooks.md)
