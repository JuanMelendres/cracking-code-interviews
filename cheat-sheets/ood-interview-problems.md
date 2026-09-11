---
title: "Cheat Sheet: Object-Oriented Design Interview Problems"
slug: ood-interview-problems
document_type: cheat-sheet
domain: 04-software-design
topic_id: T-1702
canonical: ../syllabus/04-software-design/ood-interview-problems.md
last_updated: 2026-09-11
---

# Object-Oriented Design Interview Problems

**Canonical chapter:** [`syllabus/04-software-design/ood-interview-problems.md`](../syllabus/04-software-design/ood-interview-problems.md)

## Core Mental Model

Work an OOD prompt in a deliberate sequence: find the nouns (entities), check each has exactly one job (responsibilities), identify which entities will plausibly grow new variants (extension points), and check whether legal actions depend on current state (state modeling) — a vending machine can't dispense before payment.

## Essential Definitions

- **Extension point** — an entity the prompt hints will grow new variants (multiple vehicle categories → `Vehicle` as an interface).
- **State-dependent legality** — an entity's legal next actions depend on which state it's currently in, the tell for needing an explicit state representation over boolean flags.

## Decision Table

| Step | Question to ask | Real demo evidence |
|---|---|---|
| 1. Entities | What are the nouns in this prompt? | `Vehicle`, `ParkingSpot`, `Ticket`, `ParkingLot`; `Item`, `VendingMachine` |
| 2. Responsibilities | Does each entity have exactly one job? | `ParkingSpot` knows fit/free status only, not fees |
| 3. Relationships/extension points | Which entities will plausibly grow new variants? | `Vehicle` (new categories), `FeeCalculator` (new pricing) |
| 4. State check | Do this entity's legal actions depend on its current state? | Vending machine's `IDLE`/`HAS_MONEY`/`DISPENSING` |

## Common Pitfalls

- Modeling state as scattered booleans instead of an explicit state enum — a real, reachable invalid combination (e.g., `isOnTrip=true` and `isPendingMatch=true` simultaneously) can exist with no single place preventing it.
- Making every entity an interface "just in case," rather than only the ones the prompt actually hints will vary.
- Skipping the state-dependent-legality check, missing that an entity needs an explicit state machine rather than ad hoc conditionals.

## Interview Answer Skeleton

**30-sec:** Work OOD prompts in sequence: nouns → entities, one job per entity, identify real variation points (interfaces), check whether legal actions depend on current state (explicit state machine).

**2-min:** Add: a real production example shows scattered boolean state flags allowing an invalid, reachable combination that caused a real incident — the concrete argument for an explicit state enum over booleans whenever legality depends on current state.

**Staff-level framing:** Only model an entity as an interface when the prompt genuinely hints at variation (multiple vehicle categories) — modeling everything as an interface "just in case" adds complexity without a corresponding real requirement.

## Related

- syllabus/04-software-design/solid-principles.md
