---
title: "Cheat Sheet: SOLID Principles"
slug: solid-principles
document_type: cheat-sheet
domain: 04-software-design
topic_id: T-1701
canonical: ../syllabus/04-software-design/solid-principles.md
last_updated: 2026-09-11
---

# SOLID Principles

**Canonical chapter:** [`syllabus/04-software-design/solid-principles.md`](../syllabus/04-software-design/solid-principles.md)

## Core Mental Model

Each SOLID letter names a specific structural smell and a real, mechanical fix — not a vague "good design" platitude. SRP: one class, multiple unrelated reasons to change. OCP: editing existing code for every new case. LSP: a subtype compiling but breaking a behavioral contract. ISP: a fat interface forcing unused method stubs. DIP: a high-level class directly constructing a low-level detail.

## Essential Definitions

- **SRP violation** — a class doing multiple unrelated jobs *today*.
- **OCP violation** — needing to *edit* a class every time a new case of the *same* job appears over time.
- **LSP violation** — a behavioral contract break, not a type-system one (compiles fine, breaks correctness).

## Decision Table

| Letter | Principle | Smell it names | Real fix mechanism |
|---|---|---|---|
| S | Single Responsibility | One class, multiple unrelated reasons to change | Split along independent axes of change |
| O | Open/Closed | Must edit existing code to support a new case | Depend on an interface; new cases become new classes |
| L | Liskov Substitution | A subtype compiles but breaks the supertype's behavioral contract | Verify behavioral contracts, not just type compatibility; prefer composition when a hierarchy can't honor a shared contract |
| I | Interface Segregation | A fat interface forces unused/throwing method stubs | Split into small, role-scoped interfaces |
| D | Dependency Inversion | A high-level class directly constructs a concrete low-level detail | Constructor-inject an abstraction instead |

## Common Pitfalls

- Confusing an SRP violation (multiple jobs today) with an OCP violation (needing edits over time for new cases of the same job).
- Assuming a compiler catches an LSP violation — `Square extends Rectangle` compiles cleanly while breaking `Rectangle`'s behavioral assumptions.
- Believing DIP is satisfied merely by injecting *an* interface, even a fat one that itself violates ISP.

## Interview Answer Skeleton

**30-sec:** Each SOLID letter names a specific structural smell with a real fix — SRP splits unrelated responsibilities, OCP makes new cases additive not edits, LSP is a behavioral (not type) contract, ISP splits fat interfaces, DIP inverts concrete-dependency construction via injection.

**2-min:** Add: a real demo shows adding a new shape to an OCP-compliant calculator requires zero edits to existing code; a real failing assertion demonstrates an LSP violation is a behavioral break invisible to the compiler.

**Staff-level framing:** A class can satisfy DIP's letter (depends on an abstraction) while still violating ISP (that abstraction is a fat interface) — the five principles are complementary, not a single checklist item.

## Related

- syllabus/04-software-design/ood-interview-problems.md
