---
title: "Cheat Sheet: Java Syntax Fundamentals: Variables, Control Flow, and Methods"
slug: java-syntax-fundamentals-variables-control-flow-and-methods
document_type: cheat-sheet
domain: 02-java
topic_id: T-2206
canonical: ../syllabus/02-java/language-core/java-syntax-fundamentals-variables-control-flow-and-methods.md
last_updated: 2026-09-08
---

# Java Syntax Fundamentals: Variables, Control Flow, and Methods

**Canonical chapter:** [`syllabus/02-java/language-core/java-syntax-fundamentals-variables-control-flow-and-methods.md`](../syllabus/02-java/language-core/java-syntax-fundamentals-variables-control-flow-and-methods.md)

## Core Mental Model

Java is statically typed: a variable's type is fixed at declaration and checked at compile time. `for` repeats a known number of times; `while` repeats until a condition changes; a method is a named, reusable block with parameters and a return value.

## Essential Definitions

- **Variable** — a named, typed storage location (`int score = 95;`).
- **`if`/`else`** — branches on a `boolean` condition, first match wins.
- **`switch`** — branches on one value's exact match against several fixed cases.
- **Method** — takes parameters, runs a body, `return`s a value and exits immediately.
- **Array** — a fixed-size, indexed collection of one declared type; cannot be resized after creation.

## Decision Table

| Situation | Use |
|---|---|
| Number of iterations known in advance | `for` loop |
| Number of iterations depends on a condition | `while` loop |
| One variable checked against several fixed values | `switch` |
| Mixing int and floating-point arithmetic | Cast explicitly (`(double) sum / count`) before dividing |

## Common Pitfalls

- Integer division truncates: `7 / 2` is `3`, not `3.5` — cast to `double` first.
- Confusing `=` (assignment) with `==` (comparison).
- Using `&&` where `||` was intended (or vice versa) — `&&` requires both conditions true.
- Forgetting `break` in a classic `switch` — execution falls through to the next `case`.
- Off-by-one in a loop bound: `i <= 5` runs 6 times (`0`–`5`), not 5.

## Interview Answer Skeleton

**30-sec:** Variables are statically typed and fixed at declaration. `for` loops fit known iteration counts; `while` loops fit condition-based repetition. Integer division truncates — cast to get a decimal result. A method takes parameters and returns a value; an array is fixed-size and indexed.

**2-min:** Add a concrete truncation example (`7/2` → `3`, needs a cast for `3.5`) and the `<=` vs `<` off-by-one distinction in a loop bound — both genuinely common under interview time pressure, and catching your own mistake out loud is itself a positive signal.

**Whiteboard:** Draw a `for` loop's three parts (init; condition; increment) as three boxes in a row, then trace one iteration by hand showing exactly when the condition is checked and when it becomes false.

**Staff-level framing:** These mechanics have no direct Staff-level implications on their own — they're the floor every other chapter's Staff-level discussion builds on; the relevant observation is that even basic style consistency (e.g., `switch` arrow syntax vs. fall-through) is worth a linter rule rather than repeated code-review commentary.

## Related

- syllabus/02-java/language-core/java-oop-fundamentals-classes-objects-and-interfaces.md
- syllabus/02-java/collections/java-collections-usage-fundamentals-list-map-and-set.md
- syllabus/01-computer-science-foundations/number-representation.md
