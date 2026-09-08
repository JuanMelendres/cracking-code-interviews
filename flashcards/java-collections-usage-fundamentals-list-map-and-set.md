---
title: "Flashcards: Collections Usage Fundamentals: List, Map, and Set"
slug: java-collections-usage-fundamentals-list-map-and-set
document_type: flashcard-deck
domain: 02-java
topic_id: T-2207
canonical: ../syllabus/02-java/collections/java-collections-usage-fundamentals-list-map-and-set.md
last_updated: 2026-09-08
---

# Flashcards: Collections Usage Fundamentals: List, Map, and Set

**Canonical chapter:** [`syllabus/02-java/collections/java-collections-usage-fundamentals-list-map-and-set.md`](../syllabus/02-java/collections/java-collections-usage-fundamentals-list-map-and-set.md)

## Card: List vs. Set core distinction

**Prompt:**
What's the core difference between a `List` and a `Set`?

**Answer:**
A `List` is ordered and allows duplicates. A `Set` has no guaranteed order (for `HashSet`) and never allows duplicates — adding a value already present silently does nothing.

**Why it matters:**
The single most basic Collections Framework distinction, checked in almost every "which collection would you use" question.

**Common trap:**
Describing them only by syntax differences, not by the actual duplicate/order guarantees.

**Related:**
[Collections Usage Fundamentals: List, Map, and Set](../syllabus/02-java/collections/java-collections-usage-fundamentals-list-map-and-set.md)

## Card: Converting a List to a Set

**Prompt:**
How do you get the unique elements of a `List<String> words` in one line?

**Answer:**
`Set<String> unique = new HashSet<>(words);` — this builds a new `HashSet` from the list's elements, automatically discarding duplicates in the process.

**Why it matters:**
A frequently-used, genuinely simple pattern for deduplication.

**Common trap:**
Manually looping and checking `contains()` on a growing `List` to deduplicate, instead of using the one-line `Set` conversion.

**Related:**
[Collections Usage Fundamentals: List, Map, and Set](../syllabus/02-java/collections/java-collections-usage-fundamentals-list-map-and-set.md)

## Card: map.get() on a missing key

**Prompt:**
What does `map.get(key)` return if `key` isn't in the map, and what's the risk?

**Answer:**
`null`. The risk is a `NullPointerException` on the next line if the caller assumes a non-null result (e.g., unboxing it into an `int` context). `getOrDefault(key, fallback)` or a `containsKey` check avoids this.

**Why it matters:**
A very common, real source of NPEs in code that looks correct at a glance.

**Common trap:**
Assuming `get()` returns some default value (like `0` or an empty value) instead of `null`.

**Related:**
[Collections Usage Fundamentals: List, Map, and Set](../syllabus/02-java/collections/java-collections-usage-fundamentals-list-map-and-set.md)

## Card: Why Set/Map lookups beat List scans

**Prompt:**
Why is checking membership with a `HashSet` faster than checking a `List`?

**Answer:**
`HashSet.contains()` is ~O(1) on average; checking whether a value exists in a `List` requires scanning it, which is O(n). For a large enough collection, this difference dominates.

**Why it matters:**
The single highest-leverage performance decision at this level — before any deeper internals matter.

**Common trap:**
Saying "Set is faster" without being able to name the actual complexity classes involved.

**Related:**
[Collections Usage Fundamentals: List, Map, and Set](../syllabus/02-java/collections/java-collections-usage-fundamentals-list-map-and-set.md)

## Card: List<Integer>.remove() ambiguity

**Prompt:**
On a `List<Integer>` containing `{10, 20, 30}`, what does `list.remove(1)` do — remove the value `1`, or remove the element at index `1`?

**Answer:**
It removes the element *at index 1* (the value `20`) — `remove(int index)` is chosen over `remove(Object)` due to autoboxing rules. To remove by value, box the argument explicitly: `list.remove(Integer.valueOf(1))`.

**Why it matters:**
A real, well-known Java gotcha specific to `List<Integer>` that trips up even experienced developers.

**Common trap:**
Assuming `remove(1)` removes the value `1` from the list.

**Related:**
[Collections Usage Fundamentals: List, Map, and Set](../syllabus/02-java/collections/java-collections-usage-fundamentals-list-map-and-set.md)
