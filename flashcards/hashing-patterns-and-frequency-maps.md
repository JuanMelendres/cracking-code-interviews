---
title: "Flashcards: Hashing Patterns and Frequency Maps"
slug: hashing-patterns-and-frequency-maps
document_type: flashcard-deck
domain: 03-data-structures-algorithms
topic_id: "T-2102"
canonical: ../syllabus/03-data-structures-algorithms/hashing-patterns-and-frequency-maps.md
last_updated: 2026-09-07
---

# Flashcards: Hashing Patterns and Frequency Maps

**Canonical chapter:** [`syllabus/03-data-structures-algorithms/hashing-patterns-and-frequency-maps.md`](../syllabus/03-data-structures-algorithms/hashing-patterns-and-frequency-maps.md)

## Card: `Set.add()` doubles as the duplicate check

**Prompt:**
In Contains Duplicate (LC 217), why does a single `Set.add()` call suffice instead of a separate `contains()` check followed by `add()`?

**Answer:**
`Set.add()` already returns `false` when the element was already present, so its return value itself is the duplicate check — no separate `contains()` call is needed.

**Why it matters:**
A small but real interview signal of actual collection API fluency, not just knowing the class exists.

**Common trap:**
Manually implementing a "contains, then add" two-step check when one call already does both.

**Related:**
[syllabus/03-data-structures-algorithms/hashing-patterns-and-frequency-maps.md](../syllabus/03-data-structures-algorithms/hashing-patterns-and-frequency-maps.md)

## Card: Prefix-sum identity and the `{0: 1}` seed

**Prompt:**
What algebraic identity does the prefix-sum-plus-hash-map technique (Subarray Sum Equals K, LC 560) rely on, and why must the map be seeded with `{0: 1}`?

**Answer:**
A subarray `nums[i+1..j]` sums to exactly `k` precisely when `prefixSum[j] - prefixSum[i] == k`, i.e. `prefixSum[i] == prefixSum[j] - k` — so at each position `j`, counting earlier prefix sums equal to `prefixSum[j] - k` (a hash-map lookup) counts valid subarrays ending at `j`. Seeding with `{0: 1}` before the loop correctly counts subarrays that start at index 0, representing the "empty prefix" whose sum is trivially 0.

**Why it matters:**
Missing the seed is a genuinely easy-to-miss initialization bug with no compiler warning — it silently undercounts every subarray starting at index 0.

**Common trap:**
Forgetting to seed the prefix-sum map with `{0: 1}`.

**Related:**
[syllabus/03-data-structures-algorithms/hashing-patterns-and-frequency-maps.md](../syllabus/03-data-structures-algorithms/hashing-patterns-and-frequency-maps.md)

## Card: Why negative numbers rule out a sliding window here

**Prompt:**
Why can't a sliding window solve Subarray Sum Equals K the way it solves Minimum Size Subarray Sum?

**Answer:**
A sliding window's shrink logic relies on the window sum changing monotonically as it grows or shrinks — true only when every element is non-negative. Subarray Sum Equals K allows negative numbers, so growing the window doesn't monotonically increase the sum, and there's no well-defined "shrink from the left until valid" rule. Prefix-sum-plus-hash-map doesn't depend on monotonicity at all.

**Why it matters:**
Tests whether "sliding window" and "hash map" are understood as tools with different preconditions, not interchangeable labels for "efficient subarray technique."

**Common trap:**
Trying to patch a sliding window with ad-hoc special-casing for negative numbers instead of recognizing it's the wrong tool.

**Related:**
[syllabus/03-data-structures-algorithms/arrays-two-pointers-and-sliding-window.md](../syllabus/03-data-structures-algorithms/arrays-two-pointers-and-sliding-window.md)

## Card: Happy Number's pigeonhole cycle argument

**Prompt:**
Happy Number (LC 202) never mentions hashing or cycles in its description. Why does a `HashSet`-based cycle check correctly determine whether a number is happy?

**Answer:**
The sum-of-squared-digits sequence for any starting number is bounded (pigeonhole principle: only finitely many possible values), so a sequence that never reaches 1 must eventually repeat a value — there's nowhere else for it to go. `seen.add(n)` returning `false` doubles as both the cycle check and the insertion.

**Why it matters:**
Recognizing a hashing-shaped problem that isn't announced as one is the actual skill being tested, not the mechanical `HashSet` usage once that insight lands.

**Common trap:**
Checking only against the immediately previous value instead of the full set of previously seen values.

**Related:**
[syllabus/03-data-structures-algorithms/hashing-patterns-and-frequency-maps.md](../syllabus/03-data-structures-algorithms/hashing-patterns-and-frequency-maps.md)

## Card: 4Sum II's split-and-hash restructuring

**Prompt:**
How does 4Sum II (LC 454) turn an O(n⁴) quadruple-nested loop into O(n²) using hashing?

**Answer:**
It restructures `a + b + c + d == 0` as `a + b == -(c + d)`, decomposing the problem into two independent O(n²) passes: hash every `a+b` sum's count, then for every `c+d` pair look up `-(c+d)` in that map.

**Why it matters:**
Applies whenever a brute force's choices are genuinely independent — explicitly *not* the right technique when choices are drawn from one shared array (3Sum needs two pointers instead, since its three indices aren't independent).

**Common trap:**
Applying split-and-hash to a problem like 3Sum where the indices aren't independent.

**Related:**
[syllabus/03-data-structures-algorithms/arrays-two-pointers-and-sliding-window.md](../syllabus/03-data-structures-algorithms/arrays-two-pointers-and-sliding-window.md)
