---
title: "Cheat Sheet: Java OOP Fundamentals: Classes, Objects, and Interfaces"
slug: java-oop-fundamentals-classes-objects-and-interfaces
document_type: cheat-sheet
domain: 02-java
topic_id: T-2201
canonical: ../syllabus/02-java/language-core/java-oop-fundamentals-classes-objects-and-interfaces.md
last_updated: 2026-09-07
---

# Java OOP Fundamentals: Classes, Objects, and Interfaces

**Canonical chapter:** [`syllabus/02-java/language-core/java-oop-fundamentals-classes-objects-and-interfaces.md`](../syllabus/02-java/language-core/java-oop-fundamentals-classes-objects-and-interfaces.md)

## Core Mental Model

A class is a blueprint; an object is one specific thing built from it via `new`, with its own copy of the class's fields. Assigning an object to a second variable copies the *reference*, not the object — both variables point at the same thing in memory.

## Essential Definitions

- **Encapsulation** — hide data behind methods so the class can guarantee its own invariants (private fields + validated setters).
- **Abstraction** — expose *what* something does without exposing *how* (an interface or abstract method).
- **Inheritance** — one class reuses and specializes another's fields/methods (`extends`, single inheritance only).
- **Polymorphism** — the same method call runs different code depending on the actual object — see [Polymorphism and Dynamic Dispatch Mechanics](../syllabus/02-java/language-core/polymorphism-and-dynamic-dispatch.md) for the JVM mechanism.
- **Composition** — a class holds a reference to another as a field ("has-a") instead of extending it ("is-a").

## Decision Table

| Situation | Reach for |
|---|---|
| Real shared implementation (fields, constructor, working method bodies) across a genuinely related family | Abstract class |
| Just a capability/contract, possibly across unrelated classes, no shared state | Interface |
| A second independent axis of variation would otherwise force a multiplying number of subclasses | Composition |
| A single, permanent, stable "is-a" relationship | Inheritance |

## Common Pitfalls

- Exposing every field with a public getter/setter — functionally equivalent to a public field.
- Sharing a mutable object between two places that each assume private ownership (reference-copy, not object-copy).
- Reaching for inheritance because an "is-a" name sounds right, without checking for a second independent axis of variation (the exact class-explosion problem: 2 axes x 2 options = 4 subclasses needed under inheritance, 4 small classes under composition).
- Believing an interface can hold instance state — it cannot; only `static final` constants.

## Interview Answer Skeleton

**30-sec:** A class is a blueprint, an object is an instance built from it. The four pillars answer four different problems: encapsulation guards invariants, abstraction exposes a contract without the implementation, inheritance reuses shared implementation, polymorphism lets one call run different code per object.

**2-min:** Add the interface-vs-abstract-class decision rule (shared implementation vs. pure contract, single vs. multiple inheritance) and a concrete composition-over-inheritance example: two independent axes of variation (e.g., engine type + transmission type) need 4 subclasses under inheritance but just 4 small composable classes otherwise — the combination count is free with composition, costly with inheritance.

**Whiteboard:** Draw a fork — "abstract class" branch (shared fields/constructor/methods, single inheritance) vs. "interface" branch (pure contract, multiple implementation) — then draw the composition alternative as two independent small boxes assembled inside a third, showing how a third axis adds one box, not a multiplied set of subclasses.

**Staff-level framing:** A codebase where every class defaults to public fields has no enforced invariants anywhere — the same discipline (encapsulation inside one class) is what [Clean and Hexagonal Architecture](../syllabus/17-architecture/clean-hexagonal-architecture.md) applies across module boundaries at a larger scale.

## Related

- syllabus/02-java/language-core/polymorphism-and-dynamic-dispatch.md
- syllabus/02-java/language-core/equals-hashcode-and-comparable-contracts.md
- syllabus/04-software-design/design-patterns-applied.md
