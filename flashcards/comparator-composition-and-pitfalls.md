---
title: "Flashcards: Comparator: Composition and Pitfalls"
slug: comparator-composition-and-pitfalls
document_type: flashcard-deck
domain: 02-java
topic_id: T-2413
canonical: ../syllabus/02-java/language-core/comparator-composition-and-pitfalls.md
last_updated: 2026-09-17
---

# Flashcards: Comparator: Composition and Pitfalls

**Canonical chapter:** [`syllabus/02-java/language-core/comparator-composition-and-pitfalls.md`](../syllabus/02-java/language-core/comparator-composition-and-pitfalls.md)

## Card: What thenComparing actually does

**Prompt:**
Does `Comparator.comparing(a).thenComparing(b)` sort by `b` independently of `a`?

**Answer:**
No — `thenComparing(b)` only runs for pairs where the first comparator (`a`) already returned zero (a tie). It's a tie-breaker, not a second, independent sort.

**Why it matters:**
A common misunderstanding of comparator chain semantics.

**Common trap:**
Assuming later links in the chain apply to every pair, not just tied ones.

**Related:**
[Comparator: Composition and Pitfalls](../syllabus/02-java/language-core/comparator-composition-and-pitfalls.md)

## Card: The subtraction comparator bug

**Prompt:**
Why is `(a, b) -> a.getX() - b.getX()` a real bug for `int` fields?

**Answer:**
`int` subtraction can overflow — `Integer.MIN_VALUE - 1` wraps to `Integer.MAX_VALUE`, a real positive number — producing a wrong comparison result at extreme values. `Comparator.comparingInt()` (via `Integer.compare()`) never has this problem.

**Why it matters:**
Turns a commonly-dismissed "style nit" into a defensible, measured correctness bug.

**Common trap:**
Treating the subtraction shortcut as merely less idiomatic rather than genuinely incorrect.

**Related:**
[Comparator: Composition and Pitfalls](../syllabus/02-java/language-core/comparator-composition-and-pitfalls.md)

## Card: Fixing a null sort key

**Prompt:**
What's the standard, correct way to sort data where the key can be `null`?

**Answer:**
Wrap the key comparator: `Comparator.comparing(keyExtractor, Comparator.nullsFirst(Comparator.naturalOrder()))` (or `nullsLast`) — a plain `comparing()` throws a real `NullPointerException` on a `null` key.

**Why it matters:**
The standard fix, not a manual null-check inside the lambda.

**Common trap:**
Writing an inline `if (x == null)` check instead of using the standard wrapper.

**Related:**
[Comparator: Composition and Pitfalls](../syllabus/02-java/language-core/comparator-composition-and-pitfalls.md)

## Card: Comparator vs. Comparable

**Prompt:**
When should a class implement `Comparable` versus relying on an external `Comparator`?

**Answer:**
`Comparable` for one genuine, universal natural ordering that should apply almost everywhere the type is sorted; an external `Comparator` for any context-specific ordering, since a class can only ever have one `Comparable` implementation.

**Why it matters:**
Builds directly on the `equals`/`hashCode`/`Comparable` contract chapter's own decision framework.

**Common trap:**
Trying to express two conflicting orderings via `Comparable` alone, or implementing `Comparable` inconsistently with `equals()` just to get a convenient sort.

**Related:**
[equals(), hashCode(), and Comparable Contracts](../syllabus/02-java/language-core/equals-hashcode-and-comparable-contracts.md)
