---
title: "Hashing Patterns and Frequency Maps"
slug: hashing-patterns-and-frequency-maps
document_type: syllabus-topic
domain: 03-data-structures-algorithms
topic_id: T-2102
status: draft
version: 1.0
last_updated: 2026-09-03
mastery_levels_covered: [L1, L2, L3, L4]
prerequisites:
  - ../01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md
related:
  - ../02-java/collections/hashmap-internals.md
  - arrays-two-pointers-and-sliding-window.md
practice: ../../practice/java/week-22/hashing/
production_scenarios:
  - ../../production-cookbook/hashmap-bucket-overload-from-a-poor-hashcode-distribution.md
interview_paths: [interview-emergency-sprint, senior-to-staff]
official_references:
  - https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/HashMap.html
source_history:
  - study-packs/week-22/01-hashing-coding-practice.md
---

# Hashing Patterns and Frequency Maps

> **Provenance.** The five worked problems and retrospectives in Sections 7 and 15 are elevated from `study-packs/week-22/01-hashing-coding-practice.md` — real, compiled, executed code (`practice/java/week-22/hashing/`), re-verified on OpenJDK 21.0.12 while writing this chapter (11/11 assertions passing).

This is Master Topic Register **T-1403** (IWI 6.0, near-certain frequency). [HashMap Internals](../02-java/collections/hashmap-internals.md) explains how `HashMap` and `HashSet` work *underneath*; this chapter is about *using* them as an interview technique — turning an O(n) or O(n²) lookup or counting problem into O(1) average-case lookups.

## 1. Why This Matters

A huge share of coding-interview problems reduce to some version of "have I seen this before" or "how many times has this occurred" — and a hash-based structure answers both in O(1) average time. Recognizing when a problem is secretly a hashing problem, even when it isn't phrased as one (Happy Number, Section 7, doesn't mention hashing at all in its description), is as much the skill being tested as knowing `HashMap`'s API.

## 2. Prerequisites

[Algorithmic Complexity and Big-O](../01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md) — specifically the O(1)-average-case-vs-O(n)-worst-case distinction for hash-based lookups, which this chapter uses without re-deriving.

## 3. Foundation (L1)

**A hash-based structure (`HashSet`, `HashMap`) answers "have I seen this value?" or "how many times have I seen this value?" in roughly constant time**, regardless of how many values it already holds — a dramatic improvement over scanning a list, which gets slower as the list grows. This single property — near-free membership and counting — is why hashing shows up as the fix for so many otherwise-slow brute-force approaches.

**The general shape of a hashing-pattern problem: as you process a sequence once, left to right, maintain a hash-based structure recording something about what you've already seen — and use it to answer a question about the current element in O(1), instead of re-scanning everything seen so far.**

## 4. Core Concepts (L2)

**A `HashSet` answers "have I seen this exact value?"** — Contains Duplicate (Section 7, Problem 1) is the purest example: iterate once, and `Set.add()`'s own return value (`false` if the element was already present) *is* the duplicate check, with no separate `contains()` call needed.

**A `HashMap` answers "have I seen this value, and how many times / in what context?"** — a running frequency count (character counts for an anagram check), or, in the prefix-sum pattern (Section 7, Problem 2), a count of how many times each running prefix sum has occurred so far.

**The prefix-sum-plus-hash-map technique is the standard approach whenever a problem asks to count or find subarrays matching a sum condition, and the array can contain negative numbers** — negative numbers break a sliding-window approach's core assumption (that growing the window only increases the sum, so shrinking is always the right response to overshooting), but the prefix-sum technique never depended on that monotonicity in the first place.

**The "split into two independent halves, hash one half, probe with the other" technique** (4Sum II, Section 7, Problem 5) applies whenever a brute force's choices are genuinely *independent* — turning an O(n⁴) quadruple-nested loop into two O(n²) passes joined by a hash map lookup. This is explicitly *not* the right technique when the choices aren't independent (3Sum's three indices are drawn from one single array, which is why it needs the two-pointer technique from [Arrays, Two Pointers, and Sliding Window](arrays-two-pointers-and-sliding-window.md) instead).

## 5. How It Works Internally (L3)

**Hash-set-based cycle detection (Happy Number, Section 7, Problem 4) works because of the pigeonhole principle**: the sum-of-squared-digits sequence for any starting number is bounded (it can never exceed a small constant number of digits' worth of squares), so there are only finitely many possible values the sequence can visit. A sequence with finitely many possible values that never terminates must eventually repeat a value — there's nowhere else for it to go. The loop condition `seen.add(n)` exploits this directly: it doubles as both "have I been here before" (returns `false` on a repeat, meaning a cycle was just detected) and the insertion itself, so the loop naturally terminates the moment either `n` reaches 1 (happy) or a cycle is detected (unhappy) — no separate cycle-detection pass needed. This is the same underlying idea as Floyd's tortoise-and-hare cycle detection for linked lists ([Linked Lists and In-Place Manipulation](linked-lists-and-in-place-manipulation.md)), just using O(n) extra memory (the hash set) to detect the cycle directly, rather than O(1) memory with two pointers at different speeds — a genuine, explicit space-for-simplicity trade-off worth naming in an interview.

**The prefix-sum-plus-hash-map technique's correctness rests on one algebraic identity**: a subarray `nums[i+1..j]` sums to exactly `k` precisely when `prefixSum[j] - prefixSum[i] == k`, which rearranges to `prefixSum[i] == prefixSum[j] - k`. So at each position `j`, the number of valid subarrays *ending* at `j` equals the number of earlier positions `i` whose prefix sum equals `prefixSum[j] - k` — a direct hash-map lookup, accumulated in one single left-to-right pass. Seeding the map with `{0: 1}` before the loop starts is what correctly counts subarrays that start at index 0 (representing the "empty prefix," whose sum is trivially 0).

## 6. Practical Usage

- **Reach for a `HashSet` the moment a problem's core question is "has this exact value appeared before"** — before reaching for a sorted-array-plus-two-pointers approach, since a `HashSet` doesn't require sorting the input at all.
- **Reach for a `HashMap`-based frequency count for anagram, character-count, or "group by some computed key" problems** — grouping anagrams by their sorted-character-string key is the canonical example (referenced in Section 16).
- **Reach for prefix-sum-plus-hash-map specifically once negative numbers rule out a sliding window** — this is a real, concrete decision point, not a stylistic preference: check the problem's constraints for whether negative values are allowed before choosing between the two techniques.

## 7. Examples

**Problem 1 — LC 217, Contains Duplicate.**

```java
static boolean containsDuplicate(int[] nums) {
    Set<Integer> seen = new HashSet<>();
    for (int n : nums) {
        if (!seen.add(n)) return true;
    }
    return false;
}
```

**Retrospective:** `Set.add()` already returns `false` when the element was already present, removing the need for a separate `contains()` check — a small but real interview signal of knowing the collection's actual API contract, not just its existence. **Complexity:** O(n) time, O(n) space.

**Problem 2 — LC 560, Subarray Sum Equals K.**

```java
static int subarraySum(int[] nums, int k) {
    Map<Integer, Integer> prefixCount = new HashMap<>();
    prefixCount.put(0, 1);
    int prefixSum = 0, count = 0;
    for (int num : nums) {
        prefixSum += num;
        count += prefixCount.getOrDefault(prefixSum - k, 0);
        prefixCount.merge(prefixSum, 1, Integer::sum);
    }
    return count;
}
```

**Retrospective:** Section 5 covers the full algebraic derivation. Negative numbers are exactly why this technique, not a sliding window, is required. **Complexity:** O(n) time, O(n) space.

**Problem 3 — LC 349, Intersection of Two Arrays.**

```java
static int[] intersection(int[] nums1, int[] nums2) {
    Set<Integer> set1 = new HashSet<>();
    for (int n : nums1) set1.add(n);
    Set<Integer> result = new HashSet<>();
    for (int n : nums2) {
        if (set1.contains(n)) result.add(n);
    }
    // convert to array...
}
```

**Retrospective:** the problem's own constraint — each result element must be unique — is exactly why the result also needs to be a `Set`, not a `List`; a naive scan-and-append would duplicate entries whenever a value repeats in `nums2`. **Complexity:** O(n + m) time, O(n) space.

**Problem 4 — LC 202, Happy Number.**

```java
static boolean isHappy(int n) {
    Set<Integer> seen = new HashSet<>();
    while (n != 1 && seen.add(n)) {
        n = sumOfSquaredDigits(n);
    }
    return n == 1;
}
```

**Retrospective:** see Section 5's pigeonhole-principle derivation. **Complexity:** effectively O(1) amortized per call in practice — the cycle length is bounded by a small constant.

**Problem 5 — LC 454, 4Sum II.**

```java
static int fourSumCount(int[] nums1, int[] nums2, int[] nums3, int[] nums4) {
    Map<Integer, Integer> sumCounts = new HashMap<>();
    for (int a : nums1) for (int b : nums2) sumCounts.merge(a + b, 1, Integer::sum);
    int count = 0;
    for (int c : nums3) for (int d : nums4) count += sumCounts.getOrDefault(-(c + d), 0);
    return count;
}
```

**Retrospective:** the key restructuring is recognizing `a+b+c+d == 0` is equivalent to `a+b == -(c+d)` — decomposing an O(n⁴) quadruple-nested loop into two independent O(n²) passes joined by a hash-map lookup. **Complexity:** O(n²) time, O(n²) space.

## 8. Common Mistakes

- **Reaching for a sliding window on a subarray-sum problem without checking whether negative numbers are allowed.** Section 4/5 covers exactly why this silently produces wrong answers rather than an obvious crash — the window-shrink logic simply stops being valid, with no exception to signal it.
- **Manually implementing a "contains, then add" two-step check when a single `Set.add()` call already returns the needed boolean** — not incorrect, just a missed opportunity to demonstrate real API fluency (Section 7, Problem 1).
- **Forgetting to seed a prefix-sum map with `{0: 1}`** before the main loop, silently undercounting every subarray that starts at index 0 — a genuinely easy-to-miss initialization bug with no compiler warning.

## 9. Edge Cases

- **An empty input, or an input with all identical elements** — Contains Duplicate and Intersection both have real, verified test cases for repeated values (Section 10).
- **A target sum of exactly 0 with negative numbers present** (Subarray Sum Equals K's own verified `[1,-1,0], k=0` case, producing `3`) — a case easy to get wrong if the prefix-sum seeding (Section 8) is missing or incorrect.
- **A number whose "happy" cycle passes through a value already seen much earlier**, rather than immediately re-visiting the previous value — the hash-set approach handles this correctly with no special-casing, unlike some naive "check only the immediately previous value" attempts.

## 10. Performance Implications

Real, executed verification from `practice/java/week-22/hashing/` (OpenJDK 21.0.12), re-run while writing this chapter:

```
  PASS  LC217 containsDuplicate([1,2,3,1]) -> true
  PASS  LC217 containsDuplicate([1,2,3,4]) -> false
  PASS  LC560 subarraySum([1,1,1], 2) = 2
  PASS  LC560 subarraySum([1,2,3], 3) = 2
  PASS  LC560 subarraySum([1,-1,0], 0) = 3 (negatives handled)
  PASS  LC349 intersection([1,2,2,1],[2,2]) = [2]
  PASS  LC349 intersection([4,9,5],[9,4,9,8,4]) = [4,9]
  PASS  LC202 isHappy(19) -> true
  PASS  LC202 isHappy(2) -> false (cycles, never reaches 1)
  PASS  LC454 fourSumCount(4 arrays of 2) = 2
  PASS  LC454 fourSumCount(4 zero arrays) = 1
Week 22 — Hashing (LC 217, 560, 349, 202, 454): 11/11 assertions passed
```

Every solution here trades O(n) (or O(n²), for 4Sum II) extra memory for a reduction from a worse time complexity — the standard hashing trade-off, and exactly why [HashMap Internals](../02-java/collections/hashmap-internals.md) matters as a prerequisite: these techniques only deliver their promised O(1)-average lookups if the underlying `hashCode()` distribution is actually reasonable, per that chapter's own coverage of the worst-case degradation.

## 11. Trade-offs

| Choice | Gains | Costs |
|---|---|---|
| `HashSet`/`HashMap`-based approach | O(1) average lookup/count; no sorting required | O(n) extra memory; O(n) worst case if hash distribution is poor |
| Sorting-based approach (for problems where either would work) | O(1) extra space possible (in-place sort) | O(n log n) time, strictly worse than O(n) hashing when memory isn't the constraint |
| Prefix-sum + hash map | Handles negative numbers correctly, unlike sliding window | O(n) extra memory the sliding-window alternative wouldn't need (when applicable) |
| Split-and-hash (4Sum II style) | Turns O(n⁴) into O(n²) | Only applies when the brute force's choices are genuinely independent |

## 12. Senior-Level Considerations (L3)

The Senior-level skill is recognizing a hashing-shaped problem *without* it being announced — Happy Number (Section 7) never mentions hashing, sets, or cycles in its problem statement; recognizing "this sequence must eventually repeat by pigeonhole, so I need cycle detection" is the actual insight being tested, not the mechanical `HashSet` usage once that insight lands. The same applies to knowing when hashing is the *wrong* tool: a problem whose true bottleneck is memory, not time, may need the O(1)-space two-pointer alternative even at the cost of requiring the input to be sorted first.

## 13. Staff/System-Level Considerations (L4)

At Staff scope, the transfer from these interview patterns is directly to real system design: a service that needs to answer "have we already processed this event ID" (idempotency) or "how many times has this happened in the last N minutes" (rate limiting, anomaly detection) is solving exactly Section 3's core hashing question, just backed by a distributed hash structure (a cache, a database unique index) instead of an in-memory `HashSet`. The same worst-case-distribution risk named in [HashMap Internals](../02-java/collections/hashmap-internals.md) and demonstrated in [HashMap Bucket Overload from a Poor `hashCode()` Distribution](../../production-cookbook/hashmap-bucket-overload-from-a-poor-hashcode-distribution.md) applies at this scale too, often with worse consequences: a poorly distributed key space in a production idempotency cache degrades the same O(1)-average assumption this entire chapter's techniques quietly depend on.

## 14. Production Scenarios

- **[HashMap Bucket Overload from a Poor `hashCode()` Distribution](../../production-cookbook/hashmap-bucket-overload-from-a-poor-hashcode-distribution.md)** — the real-world instance of every technique in this chapter's O(1)-average assumption breaking down, when the underlying hash distribution violates that assumption at scale.

## 15. Interview Questions

### Question 1 — How would you check if a string has all unique characters?

**Why interviewers ask it.** It's the simplest possible hashing-pattern warm-up — checking whether a candidate reaches for the O(n)-time, O(1)-or-O(n)-space `HashSet` approach over an O(n²) nested-loop character comparison.

**Expected answer.** Iterate the string once, adding each character to a `HashSet`; if `add()` ever returns `false` (or a `contains()` check finds it first), a duplicate exists. O(n) time, O(min(n, alphabet size)) space — a real, worth-naming nuance: if the character set is bounded (ASCII, say), the space is actually O(1), bounded by the alphabet, not the string length.

**Minimum acceptable answer.** Produces a correct `HashSet`-based O(n) solution.

**Strong Senior answer.** Names the bounded-alphabet space nuance above unprompted, and can produce the O(1)-extra-space bit-vector alternative for exactly the ASCII case (each of 128 possible characters gets one bit in a single `long` or `int` bitmask) as a genuinely tighter, if less general, solution.

**Staff-level extension.** Connects this to a real trade-off: a bit-vector approach is faster and more memory-efficient but far less flexible if the character set later needs to expand (Unicode) — a real API-design decision point about coupling an implementation to today's known constraints.

**Common mistakes.** Defaulting to an O(n²) nested comparison without considering `HashSet` at all.

**Follow-up questions.** "What if the input is guaranteed to be lowercase ASCII only?" (An even tighter 26-bit bitmask becomes possible, an explicit space-for-generality trade-off.)

### Question 2 — Why does Subarray Sum Equals K need a hash map instead of a sliding window, when Minimum Size Subarray Sum (a similar-sounding problem) uses a sliding window successfully?

**Why interviewers ask it.** It's a direct test of whether "negative numbers break sliding-window monotonicity" (Section 4/5) is genuinely understood, or whether "sliding window" and "hash map" are just two memorized, interchangeable-seeming labels for "efficient subarray technique."

**Expected answer.** A sliding window's shrink logic relies on the window's sum changing monotonically as the window grows or shrinks — true only when every element is non-negative (as in Minimum Size Subarray Sum). Subarray Sum Equals K allows negative numbers, so growing the window doesn't monotonically increase the sum, and there's no longer a well-defined "shrink from the left until valid" rule. The prefix-sum-plus-hash-map technique (Section 5) doesn't depend on monotonicity at all — it directly counts, via a hash-map lookup, how many earlier prefix sums would make the current subarray sum to exactly `k`.

**Minimum acceptable answer.** States that negative numbers are the reason, even without the precise monotonicity mechanism.

**Strong Senior answer.** Derives the algebraic identity from Section 5 (`prefixSum[i] == prefixSum[j] - k`) as the actual mechanism, not just "negative numbers break things."

**Staff-level extension.** Generalizes: whenever a candidate technique's correctness secretly depends on an assumption (monotonicity, sortedness, non-negativity) not explicitly stated in the technique's name, checking the problem's actual constraints before committing to that technique is a real, transferable discipline — the same discipline that catches a production bug when an assumption baked into earlier code silently stops holding as real data changes (a direct echo of [Algorithmic Complexity's own Staff-level section](../01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md#13-staffsystem-level-considerations-l4)).

**Common mistakes.** Trying to "fix" a sliding-window approach for negative numbers with ad-hoc special-casing rather than recognizing the technique itself is the wrong fit.

**Follow-up questions.** "Could you solve this with two pointers instead of a hash map?" (Not in general, for arbitrary negative numbers, without first sorting — which would lose the original subarray indices/order needed for a *contiguous* subarray count.)

## 16. Coding/Practice Exercises

- Run the [existing practice code](../../practice/java/week-22/hashing/) yourself and confirm the same 11/11 assertions pass.
- This pattern has additional real, already-solved problems: LC 1 (Two Sum), LC 3 (Longest Substring Without Repeating Characters), LC 49 (Group Anagrams), and LC 242 (Valid Anagram) are covered in `study-packs/week-01/07-java-coding-practice.md`'s underlying practice code — study Group Anagrams specifically as the canonical "group by computed key" hash-map pattern this chapter's Section 6 references.
- Attempt LC 128 (Longest Consecutive Sequence) from scratch — it's hashing-shaped (a `HashSet` of all values, checking each value's "is this the start of a sequence" condition in O(1)) but is deliberately not duplicated here, since it's already solved elsewhere in this repository's practice code.

## 17. Debugging Exercises

**Symptom:** a service's idempotency check (a `HashSet` or `HashMap`-backed cache of already-processed request IDs) is supposed to prevent duplicate processing, but duplicates are occasionally processed anyway under high load.

**Diagnose:** distinguish two structurally different causes this chapter's vocabulary separates cleanly — (a) a genuine race condition, where two threads check-then-insert into the same non-thread-safe `HashMap` concurrently (an entirely different topic — concurrent collections — not this chapter's concern), versus (b) a poor `hashCode()` distribution on the request-ID type causing degraded worst-case lookup behavior severe enough that some checks time out or get skipped under load pressure, exactly [HashMap Bucket Overload's](../../production-cookbook/hashmap-bucket-overload-from-a-poor-hashcode-distribution.md) failure mode. Confirm which by checking whether the ID type's `hashCode()` implementation actually varies well across real ID values, and whether the failures cluster under concurrent access patterns (pointing at (a)) or under sheer volume regardless of concurrency (pointing at (b)).

## 18. Design Exercises

**Design constraint:** design a rate limiter that must answer "has this API key made more than N requests in the last minute" for millions of distinct API keys, with each check needing to complete in roughly constant time regardless of how many keys are being tracked.

Design the core data structure directly from this chapter's Section 3 principle: a hash-map keyed by API key, mapping to a count (or a small sliding-window structure) — state explicitly why this must be O(1) average per check regardless of total tracked-key count (the same requirement [Algorithmic Complexity's own design exercise](../01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md#18-design-exercises) poses for a similar rate-limiter scenario), and name the real production risk this chapter's Section 13 and Section 14 both point at: if API keys are generated in a way that produces a poor hash distribution (e.g., sequential IDs with a weak `hashCode()`), this design's O(1) assumption silently degrades exactly the way `HashMap Bucket Overload` describes.

## 19. Further Reading

- [`java.util.HashMap`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/HashMap.html) — official documentation for the class this entire chapter's techniques are built on.
- [HashMap Internals](../02-java/collections/hashmap-internals.md) — the canonical deep dive into *how* `HashMap` achieves its O(1)-average behavior, and what breaks that assumption — this chapter deliberately assumes that internals knowledge rather than re-teaching it.
- [Arrays, Two Pointers, and Sliding Window](arrays-two-pointers-and-sliding-window.md) — the alternative technique family for subarray problems, and exactly when it applies instead of hashing (Section 4/5's negative-number decision point).

## 20. Mastery Checklist

| Level | You can... | Verify with |
|---|---|---|
| L1 | Explain, in plain language, why a `HashSet`/`HashMap` answers "have I seen this" in roughly constant time | [Section 3](#3-foundation-l1) |
| L2 | Recognize a hashing-shaped problem (frequency counting, membership checks, grouping by a computed key) and choose between `HashSet` and `HashMap` correctly | [Interview Question 1](#question-1--how-would-you-check-if-a-string-has-all-unique-characters) |
| L3 | Derive the prefix-sum-plus-hash-map algebraic identity, and explain the pigeonhole-principle argument behind hash-set cycle detection | [Section 10's real verification](#10-performance-implications), [Section 5](#5-how-it-works-internally-l3) |
| L4 | Diagnose a real production idempotency/rate-limiting failure (Section 17) by correctly distinguishing a concurrency bug from a hash-distribution degradation, and design a system component that accounts for the latter risk up front (Section 18) | [Debugging Exercise](#17-debugging-exercises), [Section 13](#13-staffsystem-level-considerations-l4) |
