---
title: "Java Coding Practice — Week 13 (Java Core Fluency)"
week: 13
document_type: study-pack-coding
status: draft
last_reviewed: 2026-07-30
---

# Java Coding Practice — Week 13 (Java Core Fluency)

Four small problems, each exercising one of this week's topics end to end rather than in isolation — the point is applying the discipline, not just discussing it.

**Verification note:** all code is real, compiled and executed on OpenJDK 21.0.12. Source: [`practice/java/week-13/mixed-review/src/JavaCoreCodingPractice.java`](../../practice/java/week-13/mixed-review/src/JavaCoreCodingPractice.java).

## Problem 1 — A correctly-designed `Comparable` value class (T-101, T-104)

Implement a `Money` class (currency + cents) with `equals()`, `hashCode()`, and `compareTo()` all derived from the same fields, where `compareTo()` throws `IllegalArgumentException` on a cross-currency comparison rather than silently comparing incompatible values.

**Why it matters:** exercises the full equals/hashCode/compareTo agreement from `02-equals-hashcode-and-comparable-contracts.md` in one class, not three separate toy examples.

## Problem 2 — A PECS-correct generic utility method (T-104)

Write `<T extends Comparable<? super T>> T maxOf(List<? extends T> items)` — a producer-extends method that works for any `Comparable` type, including a custom one like `Money`.

**Why it matters:** exercises PECS from `03-generics-erasure-and-pecs.md` against a real custom `Comparable` type, not just `Integer`/`String`.

## Problem 3 — Grouping and a downstream collector (T-107)

Given a list of `Employee(department, name, salary)` records, produce a `Map<String, List<String>>` of the top earner's name per department, using `Collectors.groupingBy` with a downstream `Collectors.collectingAndThen(Collectors.maxBy(...), ...)`.

**Why it matters:** exercises a real downstream-collector composition from `01-streams-and-collectors.md`, beyond the simpler `groupingBy` + `counting()` shown in the chapter itself.

## Problem 4 — A defensively-immutable `Roster` (T-103)

Implement a `Roster` class wrapping a `List<String>` of members, immune to both post-construction mutation of the source list and mutation attempts on the returned member list.

**Why it matters:** exercises both leak points from `05-immutability-and-defensive-copying.md` in one class.

## Real output

```
PASS: Money: equal value objects are equals()-equal
PASS: Money: equal objects have equal hashCode()
PASS: Money: compareTo orders by cents
PASS: Money: HashSet correctly deduplicates a and b
PASS: Money: cross-currency compareTo throws IllegalArgumentException
PASS: maxOf: finds max of Integers
PASS: maxOf: finds max of Strings
PASS: maxOf: works for a custom Comparable (Money)
PASS: topEarnerPerDepartment: bob is top earner in eng
PASS: topEarnerPerDepartment: carol is top earner in sales
PASS: Roster: unaffected by post-construction mutation of the source list
PASS: Roster: members() returns an immutable, unmodifiable view

12/12 assertions passed.
```

## Reproduce

```bash
cd practice/java/week-13/mixed-review
mkdir -p out && javac -d out src/*.java && java -cp out JavaCoreCodingPractice
```
