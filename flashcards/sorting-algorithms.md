---
title: "Flashcards: Sorting Algorithms"
slug: sorting-algorithms
document_type: flashcard-deck
domain: 03-data-structures-algorithms
topic_id: T-2119
canonical: ../syllabus/03-data-structures-algorithms/sorting-algorithms.md
last_updated: 2026-09-11
---

# Flashcards: Sorting Algorithms

**Canonical chapter:** [`syllabus/03-data-structures-algorithms/sorting-algorithms.md`](../syllabus/03-data-structures-algorithms/sorting-algorithms.md)

## Card: QuickSort's real worst case

**Prompt:**
On a 2000-element already-sorted array, how many comparisons does a naive first-element-pivot QuickSort really make, and why?

**Answer:**
Exactly 1,999,000 — precisely `n(n-1)/2` for n=2000. Every partition step is maximally unbalanced (one side gets everything, the other nothing), degrading recursion from O(log n) depth to O(n) depth — a real, measured O(n²) worst case, not a theoretical footnote.

**Why it matters:**
Concrete, measured proof rather than an abstract "QuickSort can be O(n²)" statement.

**Common trap:**
Assuming QuickSort is "just O(n log n)" without knowing which real inputs trigger its worst case.

**Related:**
[Sorting Algorithms](../syllabus/03-data-structures-algorithms/sorting-algorithms.md)

## Card: Why a random pivot fixes QuickSort

**Prompt:**
Why does switching to a uniformly random pivot fix QuickSort's worst-case behavior on sorted/reverse-sorted input?

**Answer:**
A random pivot makes a pathological, adversarial input structurally no different from random input — real, measured comparisons on the identical sorted/reverse-sorted arrays drop from 1,999,000 to roughly 23,000–24,000, back in line with random input's own ~25,000.

**Why it matters:**
Explains the mechanism, not just "randomization helps."

**Common trap:**
Believing randomization only helps "on average" without realizing it neutralizes the specific adversarial inputs that break a fixed pivot strategy.

**Related:**
[Sorting Algorithms](../syllabus/03-data-structures-algorithms/sorting-algorithms.md)

## Card: When InsertionSort actually wins

**Prompt:**
Is InsertionSort strictly worse than MergeSort?

**Answer:**
No — real, measured evidence shows InsertionSort winning on small (n=12) or nearly-sorted input (14 vs. 21 comparisons), while losing by two orders of magnitude on large random input (n=5000: 6,237,056 vs. 55,277). This is exactly why production hybrid sorts (including TimSort) fall back to insertion sort for small runs.

**Why it matters:**
A common candidate mistake is dismissing InsertionSort as strictly inferior, missing why real-world hybrid sorts still use it.

**Common trap:**
Treating O(n²) as automatically worse than O(n log n) regardless of actual input size or shape.

**Related:**
[Sorting Algorithms](../syllabus/03-data-structures-algorithms/sorting-algorithms.md)
