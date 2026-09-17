---
title: "Flashcards: Coding Interview Pattern-Recognition Methodology"
slug: coding-interview-pattern-recognition-methodology
document_type: flashcard-deck
domain: 03-data-structures-algorithms
topic_id: T-2120
canonical: ../syllabus/03-data-structures-algorithms/coding-interview-pattern-recognition-methodology.md
last_updated: 2026-09-17
---

# Flashcards: Coding Interview Pattern-Recognition Methodology

**Canonical chapter:** [`syllabus/03-data-structures-algorithms/coding-interview-pattern-recognition-methodology.md`](../syllabus/03-data-structures-algorithms/coding-interview-pattern-recognition-methodology.md)

## Card: The five-step method

**Prompt:**
What are the five steps of the problem-solving method this chapter teaches, in order?

**Answer:**
1. Understand the problem (restate it, note the constraint on `n`). 2. Look for signals (match keywords/shape to a pattern). 3. Get a brute-force solution first, even a bad one. 4. Optimize using the matched pattern. 5. Trace a small example, then state the real final complexity.

**Why it matters:**
Steps 1, 3, and 5 are identical for every problem; steps 2 and 4 are what pattern recognition actually is.

**Common trap:**
Skipping straight to step 4 (optimizing) before finishing steps 1–2 — the most common real interview failure mode.

**Related:**
[Coding Interview Pattern-Recognition Methodology](../syllabus/03-data-structures-algorithms/coding-interview-pattern-recognition-methodology.md)

## Card: "Shortest path" vs. "cheapest path"

**Prompt:**
A problem says "find the shortest path" and another says "find the cheapest path." Why does this wording change which algorithm you need?

**Answer:**
"Shortest path" (unweighted) is a plain BFS problem. "Cheapest path" implies weighted edges — BFS's shortest-path guarantee specifically requires unweighted edges, so a weighted graph needs Dijkstra instead. BFS doesn't error on a weighted graph, it just silently gives a wrong answer.

**Why it matters:**
A single word in the problem statement is the signal that rules out an otherwise-plausible algorithm.

**Common trap:**
Reaching for BFS on any "shortest/cheapest path" problem without checking whether edges are weighted.

**Related:**
[Coding Interview Pattern-Recognition Methodology](../syllabus/03-data-structures-algorithms/coding-interview-pattern-recognition-methodology.md)

## Card: Reading `n`'s bound as a complexity budget

**Prompt:**
A problem states `n ≤ 20`. Roughly what complexity classes does that constraint allow, and what does it rule out spending time on?

**Answer:**
`n ≤ ~20` allows O(2ⁿ) or O(n!) — full brute-force subsets/permutations or unpruned backtracking are fine. It rules out spending interview time hunting for an O(n) trick the problem doesn't need.

**Why it matters:**
The stated input bound tells you, before writing any code, roughly which complexity class is even required — a genuinely reliable first filter.

**Common trap:**
Treating every problem as needing the fastest possible algorithm regardless of its actual constraints.

**Related:**
[Coding Interview Pattern-Recognition Methodology](../syllabus/03-data-structures-algorithms/coding-interview-pattern-recognition-methodology.md)

## Card: Two Sum's two valid patterns

**Prompt:**
"Find two numbers in an array that add up to a target" can be solved with two different patterns. What are they, and what decides which one applies?

**Answer:**
Opposite-direction two pointers (if the array is sorted, or sorting it doesn't lose needed information like original indices) — O(n log n) for the sort plus O(n) scan; or a hashing/frequency-map approach (if the array isn't sorted and original indices must be preserved) — O(n) time and space, no sort needed.

**Why it matters:**
A real example of a problem matching two rows of the signal-to-pattern table at once — the deciding factor (sortable? need original indices?) matters more than picking either pattern reflexively.

**Common trap:**
Assuming there's always exactly one "correct" pattern per problem.

**Related:**
[Coding Interview Pattern-Recognition Methodology](../syllabus/03-data-structures-algorithms/coding-interview-pattern-recognition-methodology.md)
