---
title: "Java Syntax Fundamentals: Variables, Control Flow, and Methods"
slug: java-syntax-fundamentals-variables-control-flow-and-methods
document_type: syllabus-topic
domain: 02-java
topic_id: T-2206
status: draft
version: 1.0
last_updated: 2026-09-08
mastery_levels_covered: [L1, L2]
prerequisites: []
related:
  - java-oop-fundamentals-classes-objects-and-interfaces.md
practice: ../../../practice/java/oop-fundamentals/syntax-basics/
production_scenarios: []
interview_paths: [junior-to-mid, interview-emergency-sprint]
official_references:
  - https://docs.oracle.com/javase/tutorial/java/nutsandbolts/index.html
---

# Java Syntax Fundamentals: Variables, Control Flow, and Methods

## Table of Contents

1. [Why This Matters](#1-why-this-matters)
2. [Prerequisites](#2-prerequisites)
3. [Foundation (L1)](#3-foundation-l1)
4. [Core Concepts (L2)](#4-core-concepts-l2)
5. [How It Works Internally (L3)](#5-how-it-works-internally-l3)
6. [Practical Usage](#6-practical-usage)
7. [Examples](#7-examples)
8. [Common Mistakes](#8-common-mistakes)
9. [Edge Cases](#9-edge-cases)
10. [Performance Implications](#10-performance-implications)
11. [Trade-offs](#11-trade-offs)
12. [Senior-Level Considerations (L3)](#12-senior-level-considerations-l3)
13. [Staff/System-Level Considerations (L4)](#13-staffsystem-level-considerations-l4)
14. [Production Scenarios](#14-production-scenarios)
15. [Interview Questions](#15-interview-questions)
16. [Coding/Practice Exercises](#16-codingpractice-exercises)
17. [Debugging Exercises](#17-debugging-exercises)
18. [Design Exercises](#18-design-exercises)
19. [Further Reading](#19-further-reading)
20. [Mastery Checklist](#20-mastery-checklist)

## 1. Why This Matters

This is the true floor beneath every other chapter in this repository — even [Java OOP Fundamentals](java-oop-fundamentals-classes-objects-and-interfaces.md), which starts at "a class is a blueprint," assumes you can already read an `if` statement and a `for` loop inside a method body. This chapter exists for a reader with prior exposure to *some* programming (a bootcamp, a CS course, a different language) who needs the specific Java syntax — not for someone who has never written a line of code in any language, which is genuinely outside this repository's scope (see this chapter's own Section 2). Every interview, at every level, eventually requires writing real, compiling Java on a whiteboard or a shared editor; hesitating over `for` loop syntax under pressure costs time that should go to the actual problem.

## 2. Prerequisites

None from this repository — but this chapter assumes you have written *some* code before, in some language, and are learning Java's specific syntax rather than the concept of programming itself. If you have never programmed at all, a general introductory programming course (outside this repository's scope, which targets backend interview preparation specifically) is the right starting point before this chapter.

## 3. Foundation (L1)

A **variable** is a named storage location with a declared type — `int score = 95;` declares a variable named `score`, of type `int` (a whole number), holding the value `95`. Java is **statically typed**: the type is fixed at the point of declaration and checked by the compiler, not discovered at runtime — assigning a `String` to an `int` variable is a compile error, not something that fails later.

**Operators** combine or compare values: `+`, `-`, `*`, `/` for arithmetic; `==`, `!=`, `<`, `>`, `<=`, `>=` for comparison (producing a `boolean`); `&&` (AND — both sides must be true), `||` (OR — at least one side must be true), and `!` (NOT) for combining boolean conditions; `+=`, `-=`, and similar compound-assignment operators as shorthand for "take the current value, apply an operation, store it back."

**`if`/`else`** branches execution based on a `boolean` condition, checked top to bottom — the first matching branch runs, the rest are skipped. A **`for` loop** repeats a block a known number of times, tracking its own counter (`for (int i = 0; i < n; i++)`). A **`while` loop** repeats as long as a condition holds, used when the number of iterations isn't known in advance.

A **method** is a named, reusable block of code — [`scoreToLetterGrade(int score)`](../../../practice/java/oop-fundamentals/syntax-basics/src/GradeReportDemo.java) takes an `int` parameter and returns a `char`. Calling it runs its body with the supplied argument bound to `score`; the `return` statement both produces the result and immediately exits the method.

An **array** (`int[] scores = {95, 82, 71};`) is a fixed-size, indexed collection of values of one declared type, accessed by a zero-based integer index (`scores[0]` is the first element).

## 4. Core Concepts (L2)

**`switch`** branches on a single value's exact match against several fixed possibilities — a cleaner alternative to a long `if`/`else` chain when every branch is checking the same variable. Section 7's [`describeLetterGrade`](../../../practice/java/oop-fundamentals/syntax-basics/src/GradeReportDemo.java) demonstrates this directly: one `switch` on a `char`, each `case` returning a different description, with `default` catching every value not explicitly listed.

**Integer division truncates.** `7 / 2` in Java produces `3`, not `3.5` — dividing two `int` values always produces an `int` result, discarding any remainder. Producing a real decimal requires at least one operand to be a floating-point type, usually via an explicit **cast**: `(double) sum / count`. Section 7's average calculation demonstrates exactly this — casting `sum` to `double` before dividing, specifically to avoid truncation.

Method parameters are **passed by value** for primitives (`int`, `double`, `boolean`, etc.) — the method receives a copy of the value, and changes to the parameter inside the method never affect the caller's original variable. [Java OOP Fundamentals](java-oop-fundamentals-classes-objects-and-interfaces.md)'s own Section 5 covers what happens for object references instead, where the copied *reference* still points at the same object.

## 5. How It Works Internally (L3)

A primitive variable (`int`, `double`, `boolean`, `char`, and similar) stores its actual value directly in the memory location the variable occupies — a local `int` lives on the stack frame of the method it's declared in, and is reclaimed automatically the moment that method returns, with no garbage collection involved (primitives are never heap-allocated on their own). This is a structurally different storage model from an object reference, which [Java OOP Fundamentals](java-oop-fundamentals-classes-objects-and-interfaces.md)'s Section 5 covers — a reference variable is a pointer-sized value on the stack, but the object it points at lives on the heap.

An array, once created with `new int[7]` (or the shorthand `{95, 82, ...}`), has a fixed length for its entire lifetime — there is no "resize an array" operation in Java; growing a collection dynamically requires a different structure entirely (an `ArrayList`, covered in [Collections Usage Fundamentals](../collections/java-collections-usage-fundamentals-list-map-and-set.md)), because an array's fixed size is baked into how it's laid out in memory at creation time.

## 6. Practical Usage

Default to a `for` loop when the number of iterations is known in advance (iterating over an array's known length); default to a `while` loop when it isn't (searching until a condition is met, as Section 7's first-failing-score search does). Cast explicitly and deliberately when mixing integer and floating-point arithmetic, rather than being surprised by truncation after the fact — Section 8's most common mistake. Extract repeated logic into a method the moment you notice the same few lines appearing more than once.

## 7. Examples

All output below is real, compiled and executed on OpenJDK 21.0.12 — [`practice/java/oop-fundamentals/syntax-basics/`](../../../practice/java/oop-fundamentals/syntax-basics/), 9/9 assertions passing.

[`GradeReportDemo.java`](../../../practice/java/oop-fundamentals/syntax-basics/src/GradeReportDemo.java) turns an array of 7 test scores into a full grade report using every construct in this chapter together: a `for` loop sums the array (`498` total) and computes a correctly-cast `double` average (`71.14`, not truncated); a `while` loop searches for the first failing score (finds `58`, the first value below `60`, and stops immediately via `break`); the `scoreToLetterGrade` method is called once per score, converting each to a letter grade (`95` → `'A'`, `58` → `'F'`); `describeLetterGrade` uses a `switch` to map each letter to a description; and a combined `&&` condition correctly identifies only the single score that is both an `'A'` grade *and* at least `93`, not every `'A'`.

## 8. Common Mistakes

- **Dividing two `int` values and expecting a decimal result** — `7 / 2` is `3`, not `3.5`; at least one operand must be cast to a floating-point type first.
- **Confusing `=` (assignment) with `==` (comparison)** — `if (x = 5)` is a genuine, common typo; Java's compiler catches this specific case for `boolean` contexts (it won't compile if `x` isn't itself a `boolean`), but the confusion is worth naming explicitly since not every language's compiler catches it.
- **Using `&&`/`||` incorrectly when a value should satisfy only one of several conditions, not all** — Section 7's honor-roll check deliberately demonstrates `&&` requiring *both* conditions, a common source of off-by-logic bugs when the intended condition was actually "either."
- **Forgetting `break` in a `switch` statement** written the older way (without arrow syntax) — execution "falls through" into the next `case` unless `break` (or `return`) stops it, a real and historically common Java bug class.

## 9. Edge Cases

- **Array index out of bounds** — `scores[7]` on a 7-element array (valid indices `0`–`6`) throws a real `ArrayIndexOutOfBoundsException` at runtime, not a compile error — array bounds are not checked until the program actually runs.
- **Integer overflow** — arithmetic on `int` that exceeds its range silently wraps around rather than throwing an error; [Number Representation](../../01-computer-science-foundations/number-representation.md) covers this mechanism in full.
- **A `for` loop's counter variable scope** — a variable declared in a `for` loop's own header (`for (int i = 0; ...)`) exists only within that loop; referencing `i` after the loop ends is a compile error, not a runtime surprise.

## 10. Performance Implications

Primitive variables and arrays are among the fastest-access data in Java precisely because of their storage model (Section 5) — no object allocation, no garbage collection pressure, direct memory access by index. This is exactly why performance-sensitive code often prefers a primitive array over a boxed collection type, a trade-off [Collections Usage Fundamentals](../collections/java-collections-usage-fundamentals-list-map-and-set.md) revisits once dynamic sizing is needed.

## 11. Trade-offs

| Concern | `for` loop | `while` loop |
|---|---|---|
| Best for | A known number of iterations | An unknown number, stopping when a condition is met |
| Readability | States the iteration bound directly in the loop header | Requires reading the loop body to find the stopping condition |
| Risk if misused | An off-by-one error in the bound condition | An infinite loop if the condition never becomes false |

## 12. Senior-Level Considerations (L3)

A Senior engineer reviewing code at this level checks for exactly Section 8's class of mistake in a live-coding round — integer-division truncation and `&&`/`||` logic errors are disproportionately common under interview time pressure, and catching one's own mistake out loud ("wait, that's integer division, let me cast this") is itself a positive signal, distinct from silently getting it wrong.

## 13. Staff/System-Level Considerations (L4)

This chapter's mechanics don't carry direct Staff-level system implications on their own — they're the floor every other chapter's Staff-level discussion already builds on. The relevant Staff-level observation is about the syllabus itself: a codebase-wide standard for something as basic as consistent `switch` statement style (arrow syntax vs. fall-through, once a team settles on Java 14+) is exactly the kind of low-stakes-individually, high-cost-in-aggregate consistency question a Staff engineer resolves once via a linter rule rather than repeatedly in code review.

## 14. Production Scenarios

No existing `production-cookbook/` entry has a syntax-fundamentals-specific root cause — this is intentionally the most basic chapter in the repository; genuine production incidents at this level are rare specifically because compile-time type checking catches most of what could go wrong here before code ever ships.

> Planned reference: a future `production-cookbook/` entry covering a real incident caused by unnoticed integer-division truncation in a financial or metrics calculation (Section 8's own warning) would be a natural, non-duplicative addition, if a genuine instance is ever documented.

## 15. Interview Questions

**Q1 (Junior): "What does `7 / 2` evaluate to in Java, and why?"**
Expected answer: `3` — integer division truncates the remainder; producing `3.5` requires casting at least one operand to a floating-point type first.

**Q2 (Junior): "What's the difference between a `for` loop and a `while` loop, and when would you use each?"**
Expected answer: `for` when the number of iterations is known in advance; `while` when it depends on a condition being met, unknown in advance — Section 6's practical rule.

**Q3 (Junior/Mid): "What happens if you access `array[10]` on an 8-element array?"**
Expected answer: a real `ArrayIndexOutOfBoundsException`, thrown at runtime — Java does not check array bounds at compile time.

**Q4 (Mid): "Explain the difference between `&&` and `||`, with an example of getting it backwards."**
Expected answer: `&&` requires both conditions true; `||` requires at least one. A concrete example (like Section 7's honor-roll check) where using `||` instead of the intended `&&` would incorrectly include every `'A'` grade rather than only sufficiently high ones.

**Q5 (Mid/Senior): "Why can't you resize a Java array after creating it?"**
Expected answer: Section 5's storage-model answer — an array's fixed length is part of how it's laid out in memory at creation; growing a collection dynamically requires a different structure (`ArrayList`) built on top of arrays internally, reallocating and copying when it needs to grow.

## 16. Coding/Practice Exercises

1. Extend `GradeReportDemo` with a method `String letterGradeSummary(char[] grades)` that returns a count of each letter grade present (e.g., `"A:1, B:1, C:1, D:0, F:1"`), using a loop and `switch` or `if`/`else`.
2. Add a `while` loop that finds the *highest* score without using any built-in max function, and add an assertion proving it against the known correct answer.
3. Deliberately write `int average = sum / scores.length;` (no cast) instead of the correct `double` version, run it, and confirm the truncated result differs from the correct one — then fix it and confirm the assertion passes.

## 17. Debugging Exercises

Given this code, predict the output before running it:

```java
int total = 0;
for (int i = 0; i <= 5; i++) {
    total += i;
}
System.out.println(total);
```

The answer is `15` (`0+1+2+3+4+5`), not `10` — the loop condition is `i <= 5`, not `i < 5`, so `i` runs through `0, 1, 2, 3, 4, 5` inclusive: six iterations, not five. A candidate predicting `10` is misreading `<=` as `<`, a genuinely common off-by-one mistake worth deliberately practicing against.

## 18. Design Exercises

Write the method signature (not the implementation) for a function that takes an array of temperatures (as `double`) and returns how many are above freezing (`0.0`). State its return type, parameter type, and name, and explain in one sentence why an array (not a single value) is the right parameter type here.

## 19. Further Reading

- [Java OOP Fundamentals: Classes, Objects, and Interfaces](java-oop-fundamentals-classes-objects-and-interfaces.md) — the next step once this chapter's mechanics are solid: organizing this same logic into classes and objects.
- [Collections Usage Fundamentals: List, Map, and Set](../collections/java-collections-usage-fundamentals-list-map-and-set.md) — what to reach for once a fixed-size array's limitation (Section 5) becomes a real constraint.
- [Number Representation](../../01-computer-science-foundations/number-representation.md) — the full mechanism behind Section 9's integer overflow and Section 8's truncation.

## 20. Mastery Checklist

- [ ] Can declare a variable with the correct type for a given value.
- [ ] Can write a correct `for` loop and a correct `while` loop, and explain when to use each.
- [ ] Can explain why `7 / 2` is `3` in Java and how to get a decimal result instead.
- [ ] Can write a method with parameters and a return type from scratch.
- [ ] Can correctly answer the Section 17 debugging exercise (the `<=` vs. `<` off-by-one) before running it.
- [ ] Can explain why a Java array can't be resized after creation.
