---
title: "Flashcards: SOLID Principles"
slug: solid-principles
document_type: flashcard-deck
domain: 04-software-design
topic_id: T-1701
canonical: ../syllabus/04-software-design/solid-principles.md
last_updated: 2026-09-11
---

# Flashcards: SOLID Principles

**Canonical chapter:** [`syllabus/04-software-design/solid-principles.md`](../syllabus/04-software-design/solid-principles.md)

## Card: SRP vs. OCP

**Prompt:**
What's the difference between an OCP violation and an SRP violation?

**Answer:**
SRP is about a class doing multiple unrelated jobs *today*; OCP is about needing to *edit* a class every time a new case of the *same* job appears over time.

**Why it matters:**
The two are commonly conflated but describe genuinely different structural problems.

**Common trap:**
Treating "the class does too much" and "I have to keep editing this class" as the same complaint.

**Related:**
[SOLID Principles](../syllabus/04-software-design/solid-principles.md)

## Card: Why the compiler can't catch LSP

**Prompt:**
Why can't the compiler catch a Liskov Substitution violation?

**Answer:**
LSP is a behavioral contract, not a type-system one — `Square extends Rectangle` is structurally valid and compiles cleanly even though it breaks `Rectangle`'s own behavioral assumptions (e.g., setting width independently of height).

**Why it matters:**
Explains why LSP violations surface as real, subtle runtime bugs rather than compile errors.

**Common trap:**
Assuming type-correct inheritance guarantees behavioral correctness.

**Related:**
[SOLID Principles](../syllabus/04-software-design/solid-principles.md)

## Card: DIP satisfied, ISP still violated

**Prompt:**
Can a class satisfy Dependency Inversion's letter while still violating Interface Segregation?

**Answer:**
Yes — injecting a fat interface as a dependency technically satisfies "depend on an abstraction," but the fat interface itself still forces implementers to support methods they don't need, violating ISP.

**Why it matters:**
Shows the five principles are complementary, not redundant — satisfying one doesn't guarantee the others.

**Common trap:**
Treating "I injected an interface" as sufficient evidence of good SOLID design without checking the interface's own shape.

**Related:**
[SOLID Principles](../syllabus/04-software-design/solid-principles.md)
