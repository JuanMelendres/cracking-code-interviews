---
title: "Cheat Sheet: Hashing Patterns and Frequency Maps"
slug: hashing-patterns-and-frequency-maps
document_type: cheat-sheet
domain: 03-data-structures-algorithms
topic_id: T-2102
canonical: ../syllabus/03-data-structures-algorithms/hashing-patterns-and-frequency-maps.md
last_updated: 2026-09-06
---

# Hashing Patterns and Frequency Maps

**Canonical chapter:** [`syllabus/03-data-structures-algorithms/hashing-patterns-and-frequency-maps.md`](../syllabus/03-data-structures-algorithms/hashing-patterns-and-frequency-maps.md)

## Core Mental Model

A hash-based structure answers "have I seen this?" or "how many times have I seen this?" in roughly constant time — the general pattern shape is: process a sequence once, left to right, maintaining a hash-based structure recording something about what's already been seen, and use it to answer a question about the current element in O(1) instead of re-scanning.

## Essential Definitions

- **`HashSet` membership check** — answers "have I seen this exact value?"; `Set.add()`'s own return value (`false` if already present) *is* the duplicate check.
- **`HashMap` frequency count** — answers "have I seen this value, and how many times/in what context?"
- **Prefix-sum + hash map** — the standard technique for counting/finding subarrays matching a sum condition when the array can contain negative numbers, since negatives break a sliding window's monotonicity assumption.
- **Split-and-hash** — turns an O(n⁴) quadruple-nested loop into two independent O(n²) passes joined by a hash-map lookup, valid only when the brute force's choices are genuinely independent (4Sum II).

## Recognition Signals / When to Use This Pattern

| Signal | Technique |
|---|---|
| Core question is "has this exact value appeared before" | `HashSet` |
| Anagram, character-count, or "group by a computed key" | `HashMap` frequency/grouping |
| Subarray-sum condition, negative numbers allowed | Prefix-sum + hash map (not sliding window) |
| Brute force's choices are genuinely independent (e.g., 4 arrays, split 2+2) | Split-and-hash |
| Sequence must eventually repeat by pigeonhole (bounded value space) | Hash-set cycle detection |

**Complexity:** Contains Duplicate O(n)/O(n); Subarray Sum Equals K O(n)/O(n); Intersection of Two Arrays O(n+m)/O(n); Happy Number ~O(1) amortized per call (bounded cycle length); 4Sum II O(n²)/O(n²).

## Common Pitfalls

- Reaching for a sliding window on a subarray-sum problem without checking whether negative numbers are allowed — the window-shrink logic silently produces wrong answers, with no exception to signal it.
- Manually implementing a "contains, then add" two-step check when a single `Set.add()` call already returns the needed boolean.
- Forgetting to seed a prefix-sum map with `{0: 1}` before the main loop — silently undercounts every subarray that starts at index 0.

## Interview Answer Skeleton

**30-sec:** A huge share of coding problems reduce to "have I seen this before" or "how many times has this occurred" — a hash-based structure answers both in O(1) average time, turning an O(n) or O(n²) approach into a single linear pass.

**2-min:** Contains Duplicate is the purest example — iterate once, `Set.add()`'s return value is the duplicate check. For subarray-sum problems, the prefix-sum-plus-hash-map technique rests on one identity: `nums[i+1..j]` sums to `k` exactly when `prefixSum[i] == prefixSum[j] - k`, so at each position the count of valid subarrays ending there is a direct hash-map lookup.

**Staff-level framing:** A service that needs "have we already processed this event ID" (idempotency) or "how many times in the last N minutes" (rate limiting) is solving exactly this chapter's core hashing question, just backed by a distributed hash structure instead of an in-memory `HashSet` — and a poorly distributed key space degrades the same O(1)-average assumption these techniques quietly depend on.

## Production Warning Signs

- A service's idempotency check (a `HashSet`/`HashMap`-backed cache of already-processed request IDs) is supposed to prevent duplicate processing, but duplicates are occasionally processed anyway under high load.
- Diagnose by distinguishing two causes: (a) a genuine race condition from concurrent check-then-insert on a non-thread-safe map, versus (b) a poor `hashCode()` distribution causing degraded worst-case lookup behavior severe enough that checks time out or get skipped under load. Confirm by checking whether the ID type's `hashCode()` varies well across real values, and whether failures cluster under concurrency (pointing at (a)) or under sheer volume regardless of concurrency (pointing at (b)).

## Related

- syllabus/02-java/collections/hashmap-internals.md
- syllabus/03-data-structures-algorithms/arrays-two-pointers-and-sliding-window.md
