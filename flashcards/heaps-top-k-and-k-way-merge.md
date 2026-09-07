---
title: "Flashcards: Heaps, Top-K, and K-Way Merge"
slug: heaps-top-k-and-k-way-merge
document_type: flashcard-deck
domain: 03-data-structures-algorithms
topic_id: "T-2106"
canonical: ../syllabus/03-data-structures-algorithms/heaps-top-k-and-k-way-merge.md
last_updated: 2026-09-07
---

# Flashcards: Heaps, Top-K, and K-Way Merge

**Canonical chapter:** [`syllabus/03-data-structures-algorithms/heaps-top-k-and-k-way-merge.md`](../syllabus/03-data-structures-algorithms/heaps-top-k-and-k-way-merge.md)

## Card: Why top-k largest uses a min-heap

**Prompt:**
Why does keeping the top k largest elements use a min-heap, not a max-heap?

**Answer:**
The heap needs to quickly identify which element to evict when the size cap is exceeded, and the element to evict is always the smallest of the currently-kept "largest k" candidates. A min-heap makes that smallest element instantly accessible via `peek()`/`poll()`; a max-heap would expose the largest, the opposite of what eviction needs.

**Why it matters:**
Genuinely counterintuitive on first encounter — the heap type is chosen by what needs evicting, not by what the problem is nominally "about."

**Common trap:**
Defaulting to a max-heap for any "find the largest" framing without checking which operation (insertion vs. eviction) actually needs to be fast.

**Related:**
[syllabus/03-data-structures-algorithms/heaps-top-k-and-k-way-merge.md](../syllabus/03-data-structures-algorithms/heaps-top-k-and-k-way-merge.md)

## Card: The tie-break inversion bug

**Prompt:**
In Top K Frequent Words (LC 692), why must the comparator's tie-break direction be inverted relative to `String`'s natural ordering?

**Answer:**
The heap evicts its "worst" element first; among tied frequencies, the "worst" element by the problem's own ranking is the lexicographically larger one — the reverse of `String.compareTo`'s natural ordering. Forgetting to flip this produces a heap that silently returns the wrong tie-break winner.

**Why it matters:**
A tie-break bug that only manifests on inputs with identical frequencies — occasionally wrong, not obviously broken.

**Common trap:**
Using the natural comparator direction for tie-breaking instead of deriving it from what the heap needs to evict.

**Related:**
[syllabus/03-data-structures-algorithms/heaps-top-k-and-k-way-merge.md](../syllabus/03-data-structures-algorithms/heaps-top-k-and-k-way-merge.md)

## Card: Heap-based k-way merge seeding

**Prompt:**
In Find K Pairs with Smallest Sums (LC 373), why does seeding the heap with pair `(i, 0)` for every index `i` of the first array guarantee correctness?

**Answer:**
Pairing every element of `nums1` with `nums2`'s smallest element guarantees the single globally smallest sum is already present in the heap. Each pop only ever advances that specific row's second-array index by one — the heap itself decides which row to advance next, rather than the algorithm needing to reason about it directly.

**Why it matters:**
Structurally identical to merging k sorted linked lists — each "row" is an implicit sorted sequence, merged lazily without materializing the full cross-product.

**Common trap:**
Materializing the entire cross-product upfront instead of using the lazy, incremental k-way-merge technique.

**Related:**
[syllabus/03-data-structures-algorithms/heaps-top-k-and-k-way-merge.md](../syllabus/03-data-structures-algorithms/heaps-top-k-and-k-way-merge.md)

## Card: Reorganize String's one-step cooldown

**Prompt:**
How does Reorganize String (LC 767) enforce "no two adjacent characters are identical" using a heap?

**Answer:**
The just-placed character is held out of the heap for exactly one iteration — a one-step cooldown — and re-admitted only after a different character has been placed next. One step is sufficient because the constraint only ever concerns the immediately preceding placement, not any earlier one.

**Why it matters:**
A minimal, precise mechanism — not a general "wait until safe" check — worth being able to justify precisely.

**Common trap:**
Assuming a longer or more general cooldown window is needed than the constraint actually requires.

**Related:**
[syllabus/03-data-structures-algorithms/heaps-top-k-and-k-way-merge.md](../syllabus/03-data-structures-algorithms/heaps-top-k-and-k-way-merge.md)

## Card: Size-bounded heap vs. full sort for top-k

**Prompt:**
What complexity does a size-bounded heap achieve for a "top k" query, and why is that better than sorting the whole input?

**Answer:**
O(n log k), versus O(n log n) for a full sort — a real, worthwhile improvement whenever `k` is small relative to `n`, since the heap never grows past size `k`.

**Why it matters:**
The concrete reason to reach for a size-bounded heap instead of "just sort everything" for any top-k or k-closest requirement.

**Common trap:**
Using a max-heap holding all `n` elements then popping `k` times — correct, but O(n) space with no advantage over a size-bounded min-heap's O(k) space in the streaming case.

**Related:**
[syllabus/03-data-structures-algorithms/heaps-top-k-and-k-way-merge.md](../syllabus/03-data-structures-algorithms/heaps-top-k-and-k-way-merge.md)
