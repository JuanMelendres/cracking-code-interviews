---
title: "Flashcards: Number Representation"
slug: number-representation
document_type: flashcard-deck
domain: 01-computer-science-foundations
topic_id: "T-2003"
canonical: ../syllabus/01-computer-science-foundations/number-representation.md
last_updated: 2026-09-07
---

# Flashcards: Number Representation

**Canonical chapter:** [`syllabus/01-computer-science-foundations/number-representation.md`](../syllabus/01-computer-science-foundations/number-representation.md)

## Card: Two's complement lets one circuit do addition and subtraction

**Prompt:**
Why does every Java integer type (`byte`, `short`, `int`, `long`) use two's complement rather than a simpler sign-and-magnitude encoding?

**Answer:**
In two's complement, `a - b` is computed as `a + (-b)`, where `-b` is just "invert every bit of `b` and add 1" — so the CPU's adder circuit handles subtraction using the exact same hardware as addition, with no separate subtract circuit needed. It's an inheritance from how CPU arithmetic units are built, not a language design choice.

**Why it matters:**
Explains a hardware-level fact that shows up directly in Java semantics — e.g., why `-1` is "all bits set" and why `Integer.MIN_VALUE` has no positive counterpart.

**Common trap:**
Assuming a negative number is stored as a sign bit plus the positive value's bits (sign-and-magnitude) — an older, less common, and different scheme.

**Related:**
[syllabus/01-computer-science-foundations/number-representation.md](../syllabus/01-computer-science-foundations/number-representation.md)

## Card: Integer overflow wraps silently

**Prompt:**
What does `Integer.MAX_VALUE + 1` evaluate to in Java, and does it throw an exception?

**Answer:**
It silently wraps around to `Integer.MIN_VALUE` (`-2,147,483,648`). Java's `+`, `-`, and `*` operators never check for overflow on primitive integers — there is no automatic widening and no exception unless you explicitly use a checked method like `Math.addExact`, which throws `ArithmeticException` instead of wrapping.

**Why it matters:**
It's one of the most common "what does this print" interview questions in this domain, and a real correctness hazard anywhere silent wraparound isn't intentional.

**Common trap:**
Assuming Java automatically promotes `int` arithmetic to `long` on overflow, or assuming an exception is thrown by default.

**Related:**
[syllabus/01-computer-science-foundations/number-representation.md](../syllabus/01-computer-science-foundations/number-representation.md)

## Card: A narrowing cast truncates — it does not round or clamp

**Prompt:**
What does `(byte) 200` evaluate to in Java, and why?

**Answer:**
`-56`. A narrowing conversion is defined by the Java Language Specification as bit-truncation: it keeps `200`'s low 8 bits and discards the rest, which happens to land on a negative number because the discarded high bit changes what the remaining bits mean under two's complement. It doesn't fail, clamp to `127` (the max a `byte` holds), or round.

**Why it matters:**
Code that relies on a cast to validate a range (`(byte) userInput`) is not doing validation at all — it's a real, common source of silent data corruption.

**Common trap:**
Assuming a narrowing cast will throw, clamp to the type's max/min, or round to the nearest representable value.

**Related:**
[syllabus/01-computer-science-foundations/number-representation.md](../syllabus/01-computer-science-foundations/number-representation.md)

## Card: Measured accumulated error — `float` vs `double`

**Prompt:**
In the chapter's real, executed measurement, summing `0.1` ten million times (expected total `1,000,000.0`), what did `float` accumulate to, and how did `double` compare?

**Answer:**
`float` accumulated to `1,087,937.0` — about 8.79% off. `double` accumulated to `999,999.9998389754` — only about 0.000016% off, roughly six orders of magnitude more accurate.

**Why it matters:**
It's the concrete, quantified argument for why `double` is Java's default floating-point type, and why choosing `float` needs an actual reason (memory footprint, matching an external format) rather than being a default.

**Common trap:**
Treating floating-point imprecision as a fixed, one-time rounding error rather than something that compounds with every additional operation.

**Related:**
[syllabus/01-computer-science-foundations/number-representation.md](../syllabus/01-computer-science-foundations/number-representation.md)

## Card: `BigDecimal(double)` vs `BigDecimal(String)`

**Prompt:**
`new BigDecimal(0.1)` and `new BigDecimal("0.1")` produce different results. Which one gives an exact `0.1`, and which reveals the `double`'s ugly true stored value?

**Answer:**
`new BigDecimal("0.1")` — constructed from the string, parsed digit-by-digit — gives the exact decimal value `0.1` with no error. `new BigDecimal(0.1)` — constructed from the `double` — reveals the actual imprecise binary value the `double` already held: `0.1000000000000000055511151231257827021181583404541015625`.

**Why it matters:**
Constructing `BigDecimal` from a `double` doesn't "fix" the double's imprecision — it faithfully preserves whatever error was already there. For money or any exact-decimal requirement, always construct from a `String` (or from the source before it ever became a `double`).

**Common trap:**
Believing `BigDecimal` erases floating-point imprecision regardless of which constructor is used to build it.

**Related:**
[syllabus/01-computer-science-foundations/number-representation.md](../syllabus/01-computer-science-foundations/number-representation.md)

## Card: Two real historical failures from numeric-representation bugs

**Prompt:**
Name the two historical engineering failures the chapter cites, and which numeric-representation failure mode each one demonstrates.

**Answer:**
Ariane 5 Flight 501 (1996) — a 64-bit float velocity value narrowing-converted into a 16-bit signed integer overflowed, triggering an unhandled exception that cascaded into a self-destruct 37 seconds after launch (a $370 million payload). The Patriot missile failure at Dhahran (1991) — time tracked in tenths of a second in a 24-bit fixed-point register accumulated rounding error (since `0.1` has no exact binary representation) to about a third of a second after ~100 hours, causing a missed intercept.

**Why it matters:**
Makes the stakes of Sections 4/8's abstract failure modes (narrowing overflow, inexact fractional representation) concrete rather than theoretical — a representation decision validated for one range or duration can silently stop being safe in a new one.

**Common trap:**
Treating numeric-representation bugs as low-stakes or purely academic, rather than as a documented cause of real, catastrophic system failures.

**Related:**
[syllabus/01-computer-science-foundations/number-representation.md](../syllabus/01-computer-science-foundations/number-representation.md)
