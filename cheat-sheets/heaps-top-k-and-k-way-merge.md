---
title: "Cheat Sheet: Heaps, Top-K, and K-Way Merge"
slug: heaps-top-k-and-k-way-merge
document_type: cheat-sheet
domain: 03-data-structures-algorithms
topic_id: T-2106
canonical: ../syllabus/03-data-structures-algorithms/heaps-top-k-and-k-way-merge.md
last_updated: 2026-09-06
---

# Heaps, Top-K, and K-Way Merge

**Canonical chapter:** [`syllabus/03-data-structures-algorithms/heaps-top-k-and-k-way-merge.md`](../syllabus/03-data-structures-algorithms/heaps-top-k-and-k-way-merge.md)

## Core Mental Model

A heap doesn't maintain a total order over every element — it only guarantees the single smallest (or largest) is always immediately accessible, which is exactly why insert/remove costs O(log n) rather than the O(n log n) a full re-sort would cost. Recognizing "this problem repeats an extreme-value query" as the trigger, and correctly choosing max-heap vs. min-heap vs. size-bounded heap, is the core skill.

## Essential Definitions

- **Repeated extreme-value extraction** — whenever a loop is "find the current max/min, use it, put a modified version back, repeat," a heap replaces an O(n log n)-per-step re-sort with O(log n) per step.
- **Size-bounded heap ("keep only the top k")** — maintains a *min*-heap capped at size `k`, evicting the smallest whenever the cap is exceeded — counterintuitively using a min-heap to track the largest k elements, since the element to discard first is always the smallest currently kept.
- **Heap-based k-way merge** — seed the heap with one candidate from each of k sorted sequences; each pop pushes that sequence's next candidate, so the heap always holds exactly one "next candidate" per active sequence.
- **Greedy heap-based construction** — uses a heap to make a sequence of locally-optimal choices (always the most-frequent character, always the largest scarce-resource need), where the heap's O(log n) extreme-value access is what makes each greedy step affordable.

## Recognition Signals / When to Use This Pattern

| Signal | Technique |
|---|---|
| Core loop repeats "find current max/min, modify, repeat" | Heap (max or min per the loop's need) |
| "Top k" or "k closest" requirement | Size-bounded heap, O(n log k) |
| Multiple already-sorted sequences (explicit or implicit) need combined extraction | Heap-based k-way merge |
| Need to know which element to *evict* when over capacity | Choose the heap type by what `peek()`/`poll()` must return for eviction, not by the problem's surface phrasing |

**Complexity:** Last Stone Weight O(n log n) overall; Top K Frequent Words O(n log k); Find K Pairs with Smallest Sums O(k log(min(k,m))); Reorganize String O(n log a), a = alphabet size; Furthest Building O(n log(ladders)).

## Common Pitfalls

- Reaching for a max-heap when the problem actually needs a bounded min-heap (or vice versa) — "keep the top k largest via a min-heap" is genuinely counterintuitive on first encounter.
- Forgetting to flip a tie-break comparator's direction when the natural ordering doesn't match the direction the heap needs to evict in.
- Materializing an entire cross-product or full merge upfront instead of using the lazy, incremental heap-based k-way-merge technique.

## Interview Answer Skeleton

**30-sec:** A heap answers "repeatedly find and remove the current extreme value" in O(log n) per operation, versus O(n log n) to re-sort from scratch every time something changes — reach for it the moment a problem's core loop repeats an extreme-value query.

**2-min:** For "top k largest," use a min-heap capped at size k: add each new candidate, then evict the minimum if the size exceeds k. The heap ends up holding exactly the k largest seen — the element evicted is always the smallest of the currently-kept largest, since it's the weakest one a new, larger candidate should displace.

**Whiteboard:** Draw the heap as a small triangle with the root highlighted, and show a new element arriving, being inserted, and — if over capacity — the root being evicted. Label the root explicitly as "what gets evicted," not "what the problem is about."

**Staff-level framing:** The top-k-via-bounded-heap pattern transfers directly to production: a monitoring system tracking the top-N slowest requests over a continuous stream uses exactly this bounded-min-heap technique to maintain that top-N in O(log N) per new observation, rather than re-sorting the entire observed history on every new data point.

## Production Warning Signs

- A "top k most frequent items" feature works correctly for most inputs but occasionally returns the wrong tie-break winner when two items have identical frequency.
- Diagnose: check the heap comparator's tie-break branch — the tie-break direction inside a bounded min-heap must be the *inverse* of the problem's stated tie-break rule, since the heap evicts its "worst" element first, and among ties, "worst" is the opposite end of whatever the problem's display-order criterion favors.

## Related

- syllabus/03-data-structures-algorithms/stacks-and-monotonic-stack.md
