---
title: "Flashcards: Java Syntax Fundamentals: Variables, Control Flow, and Methods"
slug: java-syntax-fundamentals-variables-control-flow-and-methods
document_type: flashcard-deck
domain: 02-java
topic_id: T-2206
canonical: ../syllabus/02-java/language-core/java-syntax-fundamentals-variables-control-flow-and-methods.md
last_updated: 2026-09-08
---

# Flashcards: Java Syntax Fundamentals: Variables, Control Flow, and Methods

**Canonical chapter:** [`syllabus/02-java/language-core/java-syntax-fundamentals-variables-control-flow-and-methods.md`](../syllabus/02-java/language-core/java-syntax-fundamentals-variables-control-flow-and-methods.md)

## Card: Integer division truncation

**Prompt:**
What does `7 / 2` evaluate to in Java, and why?

**Answer:**
`3` — dividing two `int` values always produces an `int` result, discarding the remainder. Getting `3.5` requires casting at least one operand to a floating-point type first, e.g. `(double) 7 / 2`.

**Why it matters:**
A genuinely common source of subtle bugs, especially in average/percentage calculations.

**Common trap:**
Expecting a decimal result from dividing two `int` variables without an explicit cast.

**Related:**
[Java Syntax Fundamentals: Variables, Control Flow, and Methods](../syllabus/02-java/language-core/java-syntax-fundamentals-variables-control-flow-and-methods.md)

## Card: for vs. while

**Prompt:**
When should you use a `for` loop instead of a `while` loop?

**Answer:**
`for` when the number of iterations is known in advance (e.g., iterating an array by index); `while` when it depends on a condition being met and isn't known ahead of time (e.g., searching until a value is found).

**Why it matters:**
A basic but real design choice — using the wrong one usually still "works," but signals shakier fundamentals.

**Common trap:**
Defaulting to `for` even when a `while` loop's condition-driven logic (like "keep going until found") would read more clearly.

**Related:**
[Java Syntax Fundamentals: Variables, Control Flow, and Methods](../syllabus/02-java/language-core/java-syntax-fundamentals-variables-control-flow-and-methods.md)

## Card: && requires both, || requires either

**Prompt:**
If you want a score to count as "honors" only when it's both an A grade AND at least 93, which operator do you need?

**Answer:**
`&&` — it requires both conditions to be true. Using `||` instead would count a score as honors if *either* condition were true, incorrectly including every A grade regardless of the exact number.

**Why it matters:**
Mixing up `&&`/`||` is a genuinely common source of "off by logic" bugs, distinct from off-by-one.

**Common trap:**
Reaching for `||` when the intended condition actually requires all parts to be true.

**Related:**
[Java Syntax Fundamentals: Variables, Control Flow, and Methods](../syllabus/02-java/language-core/java-syntax-fundamentals-variables-control-flow-and-methods.md)

## Card: Array bounds are checked at runtime, not compile time

**Prompt:**
What happens if you access `array[10]` on an 8-element array?

**Answer:**
A real `ArrayIndexOutOfBoundsException`, thrown at runtime. Java does not check array bounds when the code compiles — only when that specific line actually executes.

**Why it matters:**
Explains why a program can compile cleanly and still crash the first time it runs against unexpected input size.

**Common trap:**
Assuming an out-of-bounds array access would be caught at compile time.

**Related:**
[Java Syntax Fundamentals: Variables, Control Flow, and Methods](../syllabus/02-java/language-core/java-syntax-fundamentals-variables-control-flow-and-methods.md)

## Card: The <= vs < off-by-one

**Prompt:**
`for (int i = 0; i <= 5; i++) { total += i; }` — how many times does the loop body run, and what's the final `total`?

**Answer:**
6 times (`i` takes values `0, 1, 2, 3, 4, 5`), and `total` is `15`, not `10` — `<=` includes `5` as a final iteration, unlike `<` which would stop at `4`.

**Why it matters:**
A genuinely common off-by-one mistake, worth deliberately practicing until it's automatic.

**Common trap:**
Misreading `<=` as `<` and predicting one fewer iteration than actually occurs.

**Related:**
[Java Syntax Fundamentals: Variables, Control Flow, and Methods](../syllabus/02-java/language-core/java-syntax-fundamentals-variables-control-flow-and-methods.md)
