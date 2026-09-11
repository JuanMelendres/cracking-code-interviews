---
title: "Flashcards: Object-Oriented Design Interview Problems"
slug: ood-interview-problems
document_type: flashcard-deck
domain: 04-software-design
topic_id: T-1702
canonical: ../syllabus/04-software-design/ood-interview-problems.md
last_updated: 2026-09-11
---

# Flashcards: Object-Oriented Design Interview Problems

**Canonical chapter:** [`syllabus/04-software-design/ood-interview-problems.md`](../syllabus/04-software-design/ood-interview-problems.md)

## Card: The tell for needing explicit state

**Prompt:**
What's the specific, recognizable tell that a prompt needs an explicit state representation rather than boolean flags?

**Answer:**
The entity's legal next actions depend on which state it's currently in — e.g., a vending machine can't dispense before payment, can't accept a second `select()` while already dispensing.

**Why it matters:**
A concrete, checkable signal rather than a vague "use a state machine when it feels complex" heuristic.

**Common trap:**
Reaching for scattered boolean flags by default, only discovering the need for explicit state after an invalid combination causes a bug.

**Related:**
[Object-Oriented Design Interview Problems](../syllabus/04-software-design/ood-interview-problems.md)

## Card: When to model an entity as an interface

**Prompt:**
In the parking lot design, why is `Vehicle` modeled as an interface but `ParkingLot` is not?

**Answer:**
The prompt names multiple vehicle categories (motorcycle, car, bus) — a real, hinted variation point — while there's only ever one parking lot instance in this design, with no corresponding need to vary its own type.

**Why it matters:**
The general rule: model as an interface only where the prompt genuinely hints at variation, not by default.

**Common trap:**
Making every entity an interface "for flexibility," adding unnecessary complexity where the prompt gives no variation signal.

**Related:**
[Object-Oriented Design Interview Problems](../syllabus/04-software-design/ood-interview-problems.md)

## Card: The real risk of scattered boolean state

**Prompt:**
What real failure mode does modeling state as scattered booleans risk, versus an explicit state enum?

**Answer:**
An invalid combination of flags (e.g., `isOnTrip=true` and `isPendingMatch=true` simultaneously) can exist as a reachable program state with no single place preventing it — a real production example (ride-sharing driver status) shows this causing a real incident.

**Why it matters:**
A concrete, real-incident-backed argument for explicit state modeling, not just a style preference.

**Common trap:**
Believing careful boolean-flag discipline is enough to prevent invalid combinations without an explicit state machine.

**Related:**
[Object-Oriented Design Interview Problems](../syllabus/04-software-design/ood-interview-problems.md)
