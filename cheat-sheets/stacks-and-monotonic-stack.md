---
title: "Cheat Sheet: Stacks and the Monotonic Stack"
slug: stacks-and-monotonic-stack
document_type: cheat-sheet
domain: 03-data-structures-algorithms
topic_id: T-2105
canonical: ../syllabus/03-data-structures-algorithms/stacks-and-monotonic-stack.md
last_updated: 2026-09-06
---

# Stacks and the Monotonic Stack

**Canonical chapter:** [`syllabus/03-data-structures-algorithms/stacks-and-monotonic-stack.md`](../syllabus/03-data-structures-algorithms/stacks-and-monotonic-stack.md)

## Core Mental Model

A monotonic stack maintains its elements in strictly increasing or decreasing order by popping (evicting) any element that violates that order before pushing a new one — an evicted element can never again be useful for the question being asked, because a better candidate has already been found. This is the same "evict dominated candidates" reasoning as a sliding window's monotonic deque, applied across the whole array in one pass instead of a bounded window.

## Essential Definitions

- **Stack (LIFO)** — the most recently added element is always the first removed; the natural tool for matching/resolving something in the reverse order it was encountered (parentheses, postfix evaluation).
- **Monotonic stack (next-greater-element template)** — scanning left to right, maintain a decreasing stack of values; the moment a larger value arrives, pop every smaller value — each pop's answer is exactly the current value.
- **Monotonic stack of indices** — needed whenever the answer depends on position (a distance or width), not just the neighboring value.
- **Two-stack design** — the standard technique for building a FIFO-behaved structure (queue) out of LIFO-behaved primitives (two stacks).

## Recognition Signals / When to Use This Pattern

| Signal | Technique |
|---|---|
| "Next/previous greater or smaller element" for every position | Monotonic stack (values) |
| Answer needs a distance or width, not just a neighboring value | Monotonic stack (indices) |
| Implement one ADT (queue, min-tracking stack) using only a more primitive one | Two-stack / augmented-stack design |
| Matching or resolving tokens in reverse order encountered | Plain stack |

**Complexity:** Next Greater Element I O(n); Largest Rectangle in Histogram O(n); Evaluate RPN O(n) time/O(n) space worst case; Queue via two stacks O(1) amortized per op, O(n) space; Next Greater Element II (circular) O(n) despite the `2n` iteration bound.

## Common Pitfalls

- Tracking values on the monotonic stack when the answer actually needs a position — Largest Rectangle in Histogram fails immediately if the stack holds heights instead of indices.
- Forgetting the sentinel value (`h = 0` appended) at the end of a monotonic-stack pass — bars still on the stack when the array ends never get resolved.
- Transferring `inStack` to `outStack` on every operation rather than only when `outStack` is empty — still correct, but destroys the O(1) amortized guarantee.
- Physically duplicating an array to simulate a circular scan instead of using modular indexing.

## Interview Answer Skeleton

**30-sec:** A monotonic stack turns an O(n²) "for each element, scan to find the next/previous greater or smaller value" brute force into a single O(n) pass, by evicting values from the stack the instant a better candidate proves them permanently irrelevant.

**2-min:** The canonical template (Next Greater Element I) maintains a decreasing stack; a larger incoming value pops every smaller stack entry, and each pop is exactly that entry's answer. The amortized-O(n) argument: each element is pushed once and popped at most once across the whole run — even a nested-looking `while` loop (Largest Rectangle in Histogram) sums to at most `2n` total pushes and pops, not `n²`.

**Whiteboard:** Draw the array with a stack growing underneath; show a bar/value arriving that's larger than the stack's top, pop the top, draw the resolved width/answer, repeat until the stack top is no longer smaller.

**Staff-level framing:** The amortized-cost reasoning behind the two-stack queue is exactly the reasoning needed to evaluate whether a system's occasional expensive operation is a real problem — a batch-flush design has the identical amortized-cost shape as `transferIfNeeded()`: excellent average throughput, but no bound on any single operation's worst-case latency. Knowing which of throughput-SLA vs. p99.9-latency-SLA a requirement actually cares about is the judgment call this distinction prepares an engineer to make explicitly.

## Production Warning Signs

- A "next greater element" implementation passes on small, simple test arrays but produces wrong answers on arrays with duplicate values or a strictly decreasing suffix.
- Diagnose: check the stack's eviction comparison operator (`<=` vs. `<`) — a tie-handling mismatch only manifests on inputs with duplicates. Separately, check whether the stack is left with unresolved elements at the end of the pass (missing sentinel) — a strictly decreasing suffix never triggers eviction, so those elements silently keep their default "no answer" value.

## Related

- syllabus/03-data-structures-algorithms/arrays-two-pointers-and-sliding-window.md
- syllabus/03-data-structures-algorithms/hashing-patterns-and-frequency-maps.md
