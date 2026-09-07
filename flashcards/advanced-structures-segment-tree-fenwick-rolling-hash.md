---
title: "Flashcards: Advanced Structures: Segment Tree, Fenwick Tree, and Rolling Hash"
slug: advanced-structures-segment-tree-fenwick-rolling-hash
document_type: flashcard-deck
domain: 03-data-structures-algorithms
topic_id: T-2117
canonical: ../syllabus/03-data-structures-algorithms/advanced-structures-segment-tree-fenwick-rolling-hash.md
last_updated: 2026-09-07
---

# Flashcards: Advanced Structures: Segment Tree, Fenwick Tree, and Rolling Hash

**Canonical chapter:** [`syllabus/03-data-structures-algorithms/advanced-structures-segment-tree-fenwick-rolling-hash.md`](../syllabus/03-data-structures-algorithms/advanced-structures-segment-tree-fenwick-rolling-hash.md)

## Card: Fenwick tree's two O(log n) operations

**Prompt:**
What two operations does a Fenwick tree (Binary Indexed Tree) support in O(log n), and what's its main advantage over a segment tree?

**Answer:**
Point update (add a value at an index) and prefix-sum query (sum of everything up to an index) — both O(log n), dramatically faster than an O(n) prefix re-scan. Its advantage over a segment tree is a much smaller constant factor, at the cost of only supporting prefix-sum-style aggregates, not arbitrary combinable operations.

**Why it matters:**
Choosing the narrower, cheaper structure specifically because a problem never needs a segment tree's extra generality is a real Staff-level trade-off judgment, not just knowing the structure exists.

**Common trap:**
Reaching for a full segment tree when only prefix sums and point updates are needed.

**Related:**
[syllabus/03-data-structures-algorithms/advanced-structures-segment-tree-fenwick-rolling-hash.md](../syllabus/03-data-structures-algorithms/advanced-structures-segment-tree-fenwick-rolling-hash.md)

## Card: Coordinate compression for Fenwick trees

**Prompt:**
Why is coordinate compression necessary before using a Fenwick tree on typical problem inputs?

**Answer:**
A Fenwick tree indexes into a dense, contiguous `1..m` range, but real inputs are arbitrary integers, possibly sparse and widely spread out. Mapping each distinct input value to its rank among all distinct values (via a sorted, deduplicated array and binary search) converts the arbitrary domain into the dense index space a Fenwick tree requires.

**Why it matters:**
It's the technique underlying all of this chapter's Fenwick-tree problems (Count of Smaller Numbers After Self, Reverse Pairs, Count of Range Sum).

**Common trap:**
Attempting to coordinate-compress a domain that's too large or arrives online (not known upfront) — a dynamic segment tree is needed instead in that case.

**Related:**
[syllabus/03-data-structures-algorithms/advanced-structures-segment-tree-fenwick-rolling-hash.md](../syllabus/03-data-structures-algorithms/advanced-structures-segment-tree-fenwick-rolling-hash.md)

## Card: Reverse Pairs' real documented directional bug

**Prompt:**
What was the real, documented bug in an early Reverse Pairs implementation, and how was it caught?

**Answer:**
The first implementation queried "already-inserted values `v` with `2*v < nums[i]`" — a plausible-looking but backwards mirror of the required condition (`nums[i] > 2 * nums[j]` for `i < j`). It passed 2 of 4 test cases, including misleadingly the first one checked, and was only caught by the full test suite, not by inspection.

**Why it matters:**
It's a genuine, reproduced example of how a subtly wrong direction in a coordinate-compressed query can pass some tests while silently producing wrong counts on others.

**Common trap:**
Trusting that a solution is correct because it passes the first test case checked.

**Related:**
[syllabus/03-data-structures-algorithms/advanced-structures-segment-tree-fenwick-rolling-hash.md](../syllabus/03-data-structures-algorithms/advanced-structures-segment-tree-fenwick-rolling-hash.md)

## Card: Exact bit-packing vs. lossy rolling hash

**Prompt:**
Why does Repeated DNA Sequences need no collision guard while Longest Duplicate Substring genuinely does?

**Answer:**
DNA's 4-symbol alphabet lets a fixed-length window pack losslessly into a small number of bits (2 bits per base) — an exact, reversible encoding where two different windows can never produce the same value. A 26-letter alphabet with variable-length windows can't be packed losslessly, so its polynomial rolling hash is genuinely lossy and can collide, requiring an explicit character-by-character comparison before trusting any hash match.

**Why it matters:**
Trusting an unguarded hash match as proof of equality when the hash isn't provably lossless is a genuinely worse failure mode (silently wrong) than a slower, always-correct approach.

**Common trap:**
Treating all "hash-based" substring techniques as uniformly needing (or not needing) a collision guard, rather than deriving the answer from whether the encoding is lossless.

**Related:**
[syllabus/03-data-structures-algorithms/advanced-structures-segment-tree-fenwick-rolling-hash.md](../syllabus/03-data-structures-algorithms/advanced-structures-segment-tree-fenwick-rolling-hash.md)

## Card: Dynamic segment tree's lazy node creation

**Prompt:**
How does My Calendar III's segment tree handle a domain as large as `[0, 1e9]` without materializing a flat array?

**Answer:**
It creates child nodes lazily, only along the O(log(range)) path a given booking's range actually touches, keeping the tree sparse regardless of how enormous the coordinate domain is.

**Why it matters:**
This is the technique to reach for specifically when the domain is too large or too dynamic (bookings arriving online, not known upfront) to coordinate-compress.

**Common trap:**
Trying to coordinate-compress an online, unbounded domain instead of recognizing a dynamic segment tree is the right structure.

**Related:**
[syllabus/03-data-structures-algorithms/advanced-structures-segment-tree-fenwick-rolling-hash.md](../syllabus/03-data-structures-algorithms/advanced-structures-segment-tree-fenwick-rolling-hash.md)
