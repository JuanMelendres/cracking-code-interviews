---
title: "Cheat Sheet: Coupling, Cohesion, and Code Smells"
slug: coupling-cohesion-and-code-smells
document_type: cheat-sheet
domain: 04-software-design
topic_id: T-1703
canonical: ../syllabus/04-software-design/coupling-cohesion-and-code-smells.md
last_updated: 2026-09-21
---

# Coupling, Cohesion, and Code Smells

**Canonical chapter:** [`syllabus/04-software-design/coupling-cohesion-and-code-smells.md`](../syllabus/04-software-design/coupling-cohesion-and-code-smells.md)

## Core Mental Model

Coupling measures how much a class depends on *other* classes' internal details; cohesion measures whether a class's *own* responsibilities belong together. SOLID's five principles are mechanisms for achieving both without ever naming either directly. The Law of Demeter is the concrete coupling discipline: talk only to immediate collaborators, never reach through them.

## Essential Definitions

- **Coupling** — how much a class depends on another's internal implementation details, not just its interface.
- **Cohesion** — whether a class's own responsibilities are driven by one shared purpose.
- **Law of Demeter** — a method should only call methods on objects it directly owns or receives, never on objects those objects return (no "train wrecks").
- **Feature Envy** — a method using another class's data more than its own host class's.

## Decision Table

| Code smell | Axis | Real fix mechanism |
|---|---|---|
| God Class | Cohesion | Decompose along independent responsibilities |
| Feature Envy | Cohesion | Move the method to the class whose data it uses |
| Shotgun Surgery | Coupling | Consolidate duplicated logic/chains behind one method |
| Data Clumps | Cohesion | Extract the repeated field group into its own type |
| Primitive Obsession | Both | Introduce a small, dedicated type in place of a raw primitive |
| Law of Demeter violation | Coupling | A delegating method on the immediately-owned object |

## Common Pitfalls

- Reciting "low coupling, high cohesion" without being able to define either term independently.
- Treating any use of an interface as automatically low-coupling — a chain reaching through an interface-typed object is still a Law of Demeter violation.
- Fixing a God Class by splitting it into arbitrary pieces without checking each new piece is itself cohesive.
- Reflexively delegating every call chain "to avoid a Demeter violation," even into genuinely stable collaborators that were never going to change.

## Interview Answer Skeleton

**30-sec:** Coupling and cohesion are two separate axes of design quality; low coupling and high cohesion are the target. The Law of Demeter is the checkable coupling discipline — no call chains past an immediate collaborator. Code smells are named, recognizable symptoms of one or both axes, not bugs.

**2-min:** Add: a real, reflection-measured God Class decomposition dropped per-class coupling 5:1, with the honest caveat that the coordinating orchestrator's own coupling count rose. A real Law of Demeter measurement: an identical internal-structure change to a `Wallet` class broke 3 of 6 client files with real compiler errors — exactly the 3 that violated Demeter.

**Staff-level framing:** Minimizing coupling/maximizing cohesion everywhere has a real indirection cost — the principles pay off at genuine, demonstrated points of variation, the same standard SOLID's own Decision Framework applies to DIP.

## Production Warning Signs

- An unrelated bug fix in one part of a class breaks a seemingly unconnected feature living in the same class — check cohesion first (God Class).
- A single conceptual change requires editing the same scattered handful of files every time — Shotgun Surgery, a coupling smell.
- A change to one class's internals breaks compilation or behavior across many unrelated-looking files — check for Law of Demeter violations (a chain of more than one method call in a row).

## Real Measured Numbers

- Reflection-measured coupling: `GodOrderProcessor` (before) = 5 distinct collaborator types; each decomposed class (after) = 1 — a real 5:1 reduction, with the orchestrator itself rising to 6.
- Feature Envy fix verified behavior-identical: 3 real orders, identical totals before/after moving the method (`Double.compare(...) == 0` for all 3).
- Law of Demeter: an identical real `Wallet` internal-structure change produced 3 real compiler errors (`cannot find symbol: method getCard()`) in the 3 train-wreck client files, and 0 in the 3 Demeter-compliant ones.

## Related

- syllabus/04-software-design/solid-principles.md
- syllabus/18-engineering-practices/refactoring-discipline.md
