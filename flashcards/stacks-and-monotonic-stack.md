---
title: "Flashcards: Stacks and the Monotonic Stack"
slug: stacks-and-monotonic-stack
document_type: flashcard-deck
domain: 03-data-structures-algorithms
topic_id: "T-2105"
canonical: ../syllabus/03-data-structures-algorithms/stacks-and-monotonic-stack.md
last_updated: 2026-09-07
---

# Flashcards: Stacks and the Monotonic Stack

**Canonical chapter:** [`syllabus/03-data-structures-algorithms/stacks-and-monotonic-stack.md`](../syllabus/03-data-structures-algorithms/stacks-and-monotonic-stack.md)

## Card: The "evict dominated candidates" insight

**Prompt:**
What is the core insight behind a monotonic stack's eviction rule?

**Answer:**
A monotonic stack pops (evicts) any element that violates strict order before pushing a new one, because an evicted element can never again be useful for the question being asked — a better (larger or smaller) candidate has already been found.

**Why it matters:**
The same reasoning underlies the monotonic deque used for sliding windows — recognizing the shared primitive across superficially different problems is the actual transferable skill.

**Common trap:**
Treating each new monotonic-stack problem as needing an entirely new idea instead of recognizing this recurring eviction logic.

**Related:**
[syllabus/03-data-structures-algorithms/arrays-two-pointers-and-sliding-window.md](../syllabus/03-data-structures-algorithms/arrays-two-pointers-and-sliding-window.md)

## Card: Why Largest Rectangle in Histogram needs indices, not values

**Prompt:**
In Largest Rectangle in Histogram (LC 84), why must the monotonic stack hold indices instead of bar heights?

**Answer:**
The answer needs a width, which requires a position to subtract. Tracking heights instead of indices makes the width calculation impossible — the stack must hold indices whenever the answer depends on distance or position, not just the neighboring value itself.

**Why it matters:**
This is exactly why candidates who handle the simpler "next greater element" template often stall on the histogram variant.

**Common trap:**
Tracking values on the monotonic stack when the answer actually needs a position.

**Related:**
[syllabus/03-data-structures-algorithms/stacks-and-monotonic-stack.md](../syllabus/03-data-structures-algorithms/stacks-and-monotonic-stack.md)

## Card: Why a monotonic-stack pass is O(n), not O(n²)

**Prompt:**
A monotonic-stack pass has an inner `while` loop that looks like it could run up to `n` times per outer iteration. Why is the total complexity O(n)?

**Answer:**
Each element is pushed onto the stack exactly once and popped at most once across the entire run — summed across the whole pass, total pushes and pops are bounded by `2n`, not `n²`.

**Why it matters:**
The identical amortized-cost accounting used for `ArrayList.add()` and for the sliding-window monotonic deque.

**Common trap:**
Assuming a nested-looking `while` loop must be worse than O(n) without doing the summed-cost accounting.

**Related:**
[syllabus/03-data-structures-algorithms/arrays-two-pointers-and-sliding-window.md](../syllabus/03-data-structures-algorithms/arrays-two-pointers-and-sliding-window.md)

## Card: Why the sentinel height matters

**Prompt:**
Why does Largest Rectangle in Histogram append a sentinel height of `0` after the real array before finishing the pass?

**Answer:**
Without a sentinel, bars still on the stack when the real array ends never get their rectangle resolved at all — appending the sentinel forces every remaining bar to be evicted and its area computed.

**Why it matters:**
A concrete, easy-to-miss finalization bug — forgetting the sentinel (or an equivalent explicit cleanup pass) silently drops valid answers.

**Common trap:**
Forgetting the sentinel value at the end of a monotonic-stack pass.

**Related:**
[syllabus/03-data-structures-algorithms/stacks-and-monotonic-stack.md](../syllabus/03-data-structures-algorithms/stacks-and-monotonic-stack.md)

## Card: Two-stack queue's amortized O(1)

**Prompt:**
The two-stack queue's `pop()` can trigger an O(n) transfer from `inStack` to `outStack`. Why is the amortized cost per operation still O(1)?

**Answer:**
Each individual element moves from `inStack` to `outStack` at most once over its entire lifetime in the structure. Summed across `n` total operations, total transfer work is bounded by O(n), so dividing by `n` operations gives amortized O(1) per operation, even though a single call can occasionally cost more.

**Why it matters:**
Amortized O(1) is the right guarantee for throughput but says nothing about any single operation's worst-case latency — a batch-flush design with this exact cost shape can violate a strict p99.9 latency SLA despite excellent average throughput.

**Common trap:**
Conflating "amortized O(1)" with "every call is fast."

**Related:**
[syllabus/03-data-structures-algorithms/stacks-and-monotonic-stack.md](../syllabus/03-data-structures-algorithms/stacks-and-monotonic-stack.md)
