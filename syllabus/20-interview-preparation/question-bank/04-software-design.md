---
title: "Interview Question Bank — 04-software-design"
document_type: interview-question-bank
domain: 20-interview-preparation
status: in progress
version: 1.0
last_updated: 2026-09-13
related:
  - ../../04-software-design/INDEX.md
  - 03-data-structures-algorithms.md
  - ../../../00-project/interview-question-bank-plan.md
---

# Interview Question Bank — Software Design

Part of the multi-domain compendium. See [`06-databases.md`](06-databases.md) for the
tier-explanation format and `00-project/interview-question-bank-plan.md` for the full
22-domain plan and sourcing discipline.

**Honest count for this domain:** 3 chapters (`design-patterns-applied.md`,
`ood-interview-problems.md`, `solid-principles.md`) yielded 6 deep questions + 4
quick-fire questions = **10 real questions** — the smallest domain covered so far,
genuinely reflecting the domain's real size (only 3 canonical chapters exist here).
No Junior Fundamentals chapter exists in this domain; the object-oriented design
reasoning these chapters teach presupposes the OOP fundamentals already covered in
`02-java/language-core`.

---

## SOLID Principles

### Q1 — A class uses `if (shape instanceof Circle) ... else if (shape instanceof Square) ...`. A new requirement asks for triangle support. What principle does this violate, and how would you fix it?

**Canonical treatment:** [§ Interview Questions, Q1](../../04-software-design/solid-principles.md#interview-questions)

**What's expected:**
- **Junior:** Recognizes the `if`/`else if` chain will need to grow again for every new shape, even without naming the principle.
- **Mid:** Names Open/Closed as the violated principle, even if the interface-based fix needs prompting.
- **Senior:** Correctly identifies OCP specifically (not SRP) and proposes the interface-based fix — a `Shape` interface, each shape implementing `area()`, the calculator summing polymorphically over a list.
- **Staff:** Discusses when this fix would be overkill (a genuinely closed, permanently fixed set of shapes) versus warranted, per the chapter's own Decision Framework.

### Q2 — A `Square` class extends `Rectangle`, overriding `setWidth`/`setHeight` to keep both dimensions equal. It compiles cleanly. Why might this still be a design problem?

**Canonical treatment:** [§ Interview Questions, Q2](../../04-software-design/solid-principles.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Concludes "it compiles, so it's fine" — the common mistake this question targets.
- **Senior:** Correctly identifies this as an LSP violation and explains why the compiler can't catch it — `Square` breaks a behavioral contract (independent width/height) code written against `Rectangle` may reasonably rely on.
- **Staff:** Proposes the composition-over-inheritance-style fix (independent implementations of a shared `Shape` interface) rather than a narrower patch within the existing hierarchy.

---

## Design Patterns Applied

### Q1 — When would you use Decorator instead of just adding more optional parameters or subclasses?

**Canonical treatment:** [§ Interview Questions, Q1](../../04-software-design/design-patterns-applied.md#interview-questions)

**What's expected:**
- **Junior/Mid:** States that Decorator avoids "too many subclasses," without the precise combinatorial framing.
- **Senior:** Correctly explains the `N` vs. `2^N` distinction — Decorator needs only `N` classes for `N` independent optional behaviors, combined however the caller wants at runtime.
- **Staff:** Names the real cost of Decorator (a chain that's harder to step through in a debugger) and states when a simpler design is actually the better trade-off for a small, stable number of combinations.

### Q2 — Why is a naive lazy Singleton not thread-safe, and what specifically fixes it?

**Canonical treatment:** [§ Interview Questions, Q2](../../04-software-design/design-patterns-applied.md#interview-questions)

**What's expected:**
- **Junior:** Assumes Singleton is "just safe" because it's a well-known pattern — the common mistake this question targets.
- **Mid:** States that the naive version "has a race condition," without precisely explaining the check-then-act mechanism.
- **Senior:** Correctly explains the check-then-act race (measured directly: 30 racing threads constructing 30 separate objects) and names at least one correct fix.
- **Staff:** Names multiple fixes with their trade-offs (enum, double-checked locking with `volatile`, DI-managed scope) and states why DI-managed scope is generally preferable when a framework is already in use.

---

## Object-Oriented Design Interview Problems

### Q1 — Design a parking lot supporting motorcycles, cars, and buses, with different spot sizes. Walk through your approach before writing code.

**Canonical treatment:** [§ Interview Questions, Q1](../../04-software-design/ood-interview-problems.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Writes a `park(String vehicleType)` method with an `if`/`else` chain on the type string — a live OCP violation baked into the design from the start, the common mistake this question targets.
- **Senior:** Produces the entity list and responsibility assignment before code, and correctly identifies `Vehicle` as the interface-based extension point (`Motorcycle`/`Car`/`Bus` implementations).
- **Staff:** Proactively identifies the fee-calculation logic as a second, independent extension point (via `FeeCalculator`) even before being asked about pricing changes, and explains why coupling it into `ParkingLot` directly would violate DIP.

### Q2 — Design a vending machine. What's the first design decision you'd make, and why?

**Canonical treatment:** [§ Interview Questions, Q2](../../04-software-design/ood-interview-problems.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Starts with the item inventory or pricing model as the "first" decision — a real design concern, but not the one that most determines robustness, per this question's own framing.
- **Senior:** Correctly identifies the prompt as state-shaped and proposes an explicit state representation (an enum with guarded transitions) as the first design decision, rather than independent boolean flags.
- **Staff:** Proactively reasons about concurrent/duplicate requests against the state machine, connecting the design choice to a real production failure mode (double-dispense) rather than treating state modeling as purely structural.

---

## Quick-fire questions (from this domain's Flashcards)

Only `design-patterns-applied.md` has a Flashcards section in this domain; the other
two chapters (`solid-principles.md`, `ood-interview-problems.md`) don't.

| # | Question | Canonical chapter |
|---|---|---|
| 1 | What specific kind of variation does the Strategy pattern isolate? | [Design Patterns Applied](../../04-software-design/design-patterns-applied.md#flashcards) |
| 2 | Why is `if (instance == null) instance = new X();` not thread-safe? | [Design Patterns Applied](../../04-software-design/design-patterns-applied.md#flashcards) |
| 3 | For `N` independent optional behaviors, how many classes does Decorator need, versus subclassing? | [Design Patterns Applied](../../04-software-design/design-patterns-applied.md#flashcards) |
| 4 | Which Singleton implementation is thread-safe with zero hand-written synchronization code? | [Design Patterns Applied](../../04-software-design/design-patterns-applied.md#flashcards) |

---

## Related

- [`03-data-structures-algorithms.md`](03-data-structures-algorithms.md)
- [`06-databases.md`](06-databases.md)
- [`02-java-collections.md`](02-java-collections.md), [`02-java-concurrency.md`](02-java-concurrency.md), [`02-java-jvm-internals.md`](02-java-jvm-internals.md), [`02-java-language-core.md`](02-java-language-core.md)
- [`00-project/interview-question-bank-plan.md`](../../../00-project/interview-question-bank-plan.md)
