---
title: "Flashcards: Greedy and the Exchange Argument"
slug: greedy-and-the-exchange-argument
document_type: flashcard-deck
domain: 03-data-structures-algorithms
topic_id: T-2112
canonical: ../syllabus/03-data-structures-algorithms/greedy-and-the-exchange-argument.md
last_updated: 2026-09-07
---

# Flashcards: Greedy and the Exchange Argument

**Canonical chapter:** [`syllabus/03-data-structures-algorithms/greedy-and-the-exchange-argument.md`](../syllabus/03-data-structures-algorithms/greedy-and-the-exchange-argument.md)

## Card: What an exchange argument proves

**Prompt:**
What is an exchange argument, and what does it prove about a greedy algorithm?

**Answer:**
Assume some optimal solution makes a different choice than the greedy one at some step, then show that swapping in the greedy choice instead produces an equally good or strictly better solution — proving the greedy choice is never worse than any alternative.

**Why it matters:**
Unlike DP, where "did I memoize correctly" is a mechanical check, greedy correctness requires an actual proof — and being able to produce it, not just the code, separates a strong answer from a lucky one.

**Common trap:**
Treating "it worked on the examples I tried" as sufficient evidence a greedy approach is correct.

**Related:**
[syllabus/03-data-structures-algorithms/greedy-and-the-exchange-argument.md](../syllabus/03-data-structures-algorithms/greedy-and-the-exchange-argument.md)

## Card: Gas Station's skip-ahead justification

**Prompt:**
If starting at station `i` and the tank goes negative at station `j`, why can every station strictly between `i` and `j` be skipped as a candidate start, rather than tested individually?

**Answer:**
For any station `k` between `i` and `j`, the cumulative sum from `i` to `k` must be non-negative (otherwise the failure would have occurred at `k`, not `j`) — meaning the sum from `k` to `j` is even more negative than the sum from `i` to `j`. So `k` would fail even sooner than `i` did, and can never be a valid start.

**Why it matters:**
This is the exchange argument made fully explicit for a specific, well-known problem, and it licenses jumping the candidate start straight to `j+1` instead of an O(n²) test-every-index approach.

**Common trap:**
Testing every starting index independently in O(n²) without recognizing the skip-ahead optimization is available and provably correct.

**Related:**
[syllabus/03-data-structures-algorithms/greedy-and-the-exchange-argument.md](../syllabus/03-data-structures-algorithms/greedy-and-the-exchange-argument.md)

## Card: Task Scheduler's closed-form formula and its guard

**Prompt:**
What is Task Scheduler's closed-form formula, and why must it be wrapped in `Math.max(formula, tasks.length)`?

**Answer:**
`(maxFreq - 1) * (n + 1) + maxCount`, where `maxFreq` is the highest task frequency and `maxCount` is how many tasks tie at that frequency. The formula alone assumes idle slots are needed; once there are enough distinct tasks to fill every slot, no idle time is required at all, so the true answer is simply the total task count.

**Why it matters:**
Missing the `Math.max` guard is a real, specifically-named common mistake — the formula degenerates incorrectly whenever task diversity is high enough.

**Common trap:**
Returning the formula's value directly without checking whether `tasks.length` is actually larger.

**Related:**
[syllabus/03-data-structures-algorithms/greedy-and-the-exchange-argument.md](../syllabus/03-data-structures-algorithms/greedy-and-the-exchange-argument.md)

## Card: Remove K Digits' monotonic-stack greedy rule

**Prompt:**
Why does removing a larger digit immediately followed by a smaller one always produce a smaller-or-equal result in Remove K Digits?

**Answer:**
The removed digit occupied a more significant position, so replacing it with a smaller digit at that position strictly decreases the resulting number — no alternative removal choice at that point could produce a smaller result. A monotonic-increasing stack enforces this automatically by popping the top whenever an incoming digit is smaller.

**Why it matters:**
It's a direct combination of this chapter's greedy-correctness principle with the monotonic-stack eviction mechanism.

**Common trap:**
Forgetting to strip leading zeros from the final numeric-string result, since the greedy removal logic alone doesn't prevent them.

**Related:**
[syllabus/03-data-structures-algorithms/greedy-and-the-exchange-argument.md](../syllabus/03-data-structures-algorithms/greedy-and-the-exchange-argument.md)

## Card: Greedy vs. DP — the defining contrast

**Prompt:**
What is the fundamental difference between when greedy suffices and when DP is required?

**Answer:**
Greedy suffices when a choice, once made, is provably never worth reconsidering (an exchange argument holds). DP is needed when a choice must genuinely be reconsidered based on later information — DP considers multiple options and remembers results, while greedy commits permanently and never backtracks.

**Why it matters:**
This is the direct contrast that clarifies which technique applies to a new problem.

**Common trap:**
Assuming a locally-best-looking choice is safe without checking whether the problem actually has the exchange-argument property.

**Related:**
[syllabus/03-data-structures-algorithms/greedy-and-the-exchange-argument.md](../syllabus/03-data-structures-algorithms/greedy-and-the-exchange-argument.md)
