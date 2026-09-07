---
title: "Flashcards: Arrays, Two Pointers, and Sliding Window"
slug: arrays-two-pointers-and-sliding-window
document_type: flashcard-deck
domain: 03-data-structures-algorithms
topic_id: "T-2101"
canonical: ../syllabus/03-data-structures-algorithms/arrays-two-pointers-and-sliding-window.md
last_updated: 2026-09-07
---

# Flashcards: Arrays, Two Pointers, and Sliding Window

**Canonical chapter:** [`syllabus/03-data-structures-algorithms/arrays-two-pointers-and-sliding-window.md`](../syllabus/03-data-structures-algorithms/arrays-two-pointers-and-sliding-window.md)

## Card: Why moving the shorter pointer is always safe

**Prompt:**
In LC 11, Container With Most Water, why is it always safe to advance the pointer at the *shorter* line rather than the taller one?

**Answer:**
The current area is already capped by the shorter line's height — keeping that same shorter line and moving the other pointer inward can only produce an equal-or-smaller width with, at best, an equal limiting height, so it can never beat the current area. The only way to find a larger area is to move past the shorter line and hope for something taller.

**Why it matters:**
This proof-by-contradiction-style argument is what separates producing correct code from being able to defend it under a Senior-level follow-up on a variant not seen before.

**Common trap:**
Treating "move the shorter pointer" as memorized dogma rather than being able to derive why it's correct.

**Related:**
[syllabus/03-data-structures-algorithms/arrays-two-pointers-and-sliding-window.md](../syllabus/03-data-structures-algorithms/arrays-two-pointers-and-sliding-window.md)

## Card: Sliding Window Maximum's amortized complexity

**Prompt:**
Sliding Window Maximum (LC 239) has inner `while` loops that look like they could run up to `k` times per outer iteration. Why is the total complexity O(n), not O(n·k)?

**Answer:**
Each index is pushed onto the deque exactly once and popped at most once across the entire run. Summed over all `n` outer iterations, total push and pop operations are bounded by `2n`, not `n·k`.

**Why it matters:**
The same amortized-analysis reasoning used for `ArrayList.add()`'s resize cost — interviewers use this to test whether a nested-looking loop reflexively triggers overestimating complexity.

**Common trap:**
Assuming any nested-looking `while` loop inside a `for` loop is automatically worse than O(n) without doing the summed-cost accounting.

**Related:**
[syllabus/03-data-structures-algorithms/arrays-two-pointers-and-sliding-window.md](../syllabus/03-data-structures-algorithms/arrays-two-pointers-and-sliding-window.md)

## Card: Rotate Array's `k %= n` guard

**Prompt:**
In LC 189, Rotate Array (in-place via three reversals), why is `k %= n` required for correctness rather than just a defensive nicety?

**Answer:**
The problem's own constraints allow `k` to legally exceed `n`. Without the modulo, `reverse(nums, 0, k - 1)` throws `ArrayIndexOutOfBoundsException` instead of silently misbehaving.

**Why it matters:**
A real, verified production-shaped bug pattern — an unguarded rotation-offset or window-size parameter exceeding the length of the collection it indexes into.

**Common trap:**
Assuming `k` is always within `[0, n)` because it "looks like" a rotation amount.

**Related:**
[syllabus/03-data-structures-algorithms/arrays-two-pointers-and-sliding-window.md](../syllabus/03-data-structures-algorithms/arrays-two-pointers-and-sliding-window.md)

## Card: Why Product of Array Except Self avoids division

**Prompt:**
Why does the two-pass prefix/suffix-product technique avoid using division to solve Product of Array Except Self (LC 238)?

**Answer:**
Computing the total product and then dividing by each element breaks the moment any element is zero, and also violates the problem's no-division constraint. Separate prefix and suffix passes handle a zero at position `i` correctly through the suffix product for `j < i` and the prefix product for `j > i`.

**Why it matters:**
Demonstrates recognizing when an apparently smart shortcut is unsound the moment the input contains zero.

**Common trap:**
Reaching for total-product-then-divide as the first idea without checking the zero case.

**Related:**
[syllabus/03-data-structures-algorithms/arrays-two-pointers-and-sliding-window.md](../syllabus/03-data-structures-algorithms/arrays-two-pointers-and-sliding-window.md)

## Card: Opposite-direction two pointers vs. sliding window

**Prompt:**
What's the trigger for reaching for opposite-direction two pointers versus a sliding window on a new array problem?

**Answer:**
Reach for opposite-direction two pointers when the array is sorted, or a comparison at the two ends reveals something monotonic about which side to move (Container With Most Water, Two Sum on a sorted array). Reach for a sliding window when the problem asks about a contiguous subarray or substring satisfying some condition — a fixed length, a sum target, "contains all of X."

**Why it matters:**
The single most transferable pattern-matching skill in this chapter — recognizing the problem shape before writing any code.

**Common trap:**
Reaching for a nested loop out of habit before checking whether a monotonic property makes either technique applicable.

**Related:**
[syllabus/03-data-structures-algorithms/arrays-two-pointers-and-sliding-window.md](../syllabus/03-data-structures-algorithms/arrays-two-pointers-and-sliding-window.md)
