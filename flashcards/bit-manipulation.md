---
title: "Flashcards: Bit Manipulation"
slug: bit-manipulation
document_type: flashcard-deck
domain: 03-data-structures-algorithms
topic_id: T-2113
canonical: ../syllabus/03-data-structures-algorithms/bit-manipulation.md
last_updated: 2026-09-07
---

# Flashcards: Bit Manipulation

**Canonical chapter:** [`syllabus/03-data-structures-algorithms/bit-manipulation.md`](../syllabus/03-data-structures-algorithms/bit-manipulation.md)

## Card: XOR cancellation for Single Number

**Prompt:**
Which two algebraic properties of XOR make "XOR everything together" find the single unpaired value in an array where every other value appears twice?

**Answer:**
`x ^ x = 0` and `0 ^ x = x`. XOR-ing the whole array cancels every value that appears an even number of times, leaving only the value that appears an odd number of times.

**Why it matters:**
It solves the problem in O(1) space, versus O(n) space for an equivalent hash-set approach — the same time complexity but a real space win.

**Common trap:**
Reaching for a `HashSet` reflexively and missing the O(1)-space XOR technique, especially when the problem explicitly asks for O(1) space.

**Related:**
[syllabus/03-data-structures-algorithms/bit-manipulation.md](../syllabus/03-data-structures-algorithms/bit-manipulation.md)

## Card: Kernighan's bit trick

**Prompt:**
Why does `n & (n - 1)` clear exactly the lowest set bit of `n`?

**Answer:**
`n - 1` flips every bit from `n`'s lowest set bit downward (including that bit itself) via borrow propagation. ANDing with the original `n` keeps every higher bit unchanged (identical in both) while zeroing out exactly the lowest set bit.

**Why it matters:**
Repeating this until `n` reaches zero counts exactly as many iterations as `n` has set bits — proportional to popcount, not a fixed 32-iteration loop, which matters for sparse bit patterns.

**Common trap:**
Confusing this with `n & -n`, which isolates the lowest set bit rather than clearing it.

**Related:**
[syllabus/03-data-structures-algorithms/bit-manipulation.md](../syllabus/03-data-structures-algorithms/bit-manipulation.md)

## Card: Missing Number's overflow-safe XOR seed

**Prompt:**
Why does Missing Number seed its running XOR with `nums.length` instead of `0`, and what real bug does this avoid versus the sum-based alternative?

**Answer:**
Seeding with `nums.length` accounts for the one index (`n`) that has no corresponding array slot, since the array holds `n` distinct values from `0` to `n` inclusive with one missing — every present value then cancels with its index, leaving only the missing one. This avoids the "expected sum minus actual sum" approach's real risk: for large arrays, the expected sum can silently overflow `int`.

**Why it matters:**
XOR has no overflow failure mode at any input size, unlike sum-based approaches.

**Common trap:**
Using the sum-based approach on large inputs without considering integer overflow.

**Related:**
[syllabus/03-data-structures-algorithms/bit-manipulation.md](../syllabus/03-data-structures-algorithms/bit-manipulation.md)

## Card: Simulated binary addition via XOR and AND

**Prompt:**
How does `getSum(a, b)` compute integer addition using only bitwise operators?

**Answer:**
XOR computes each bit position's sum while ignoring carries; `(a & b) << 1` computes exactly the carry bits XOR dropped. Repeating `a = a ^ b; b = carry;` until there's no carry left reproduces ordinary addition.

**Why it matters:**
This technique works correctly for negative operands specifically because Java's `int` already uses two's complement representation, with no separate negative-number logic needed.

**Common trap:**
Assuming this trick needs special-case handling for negative operands, when two's complement makes it work identically for them.

**Related:**
[syllabus/03-data-structures-algorithms/bit-manipulation.md](../syllabus/03-data-structures-algorithms/bit-manipulation.md)

## Card: Counting Bits' O(1)-per-value recurrence

**Prompt:**
What recurrence lets Counting Bits compute every value's popcount in O(n) total instead of O(n · popcount)?

**Answer:**
`bits[i] = bits[i >> 1] + (i & 1)` — `i >> 1` is `i` with its lowest bit removed, a strictly smaller value whose popcount was already computed; `i`'s popcount is that value's popcount plus one only if `i`'s own lowest bit is set.

**Why it matters:**
This turns each value's popcount into an O(1) lookup-plus-addition instead of an independent Kernighan-trick inner loop per value.

**Common trap:**
Recomputing each value's popcount independently from scratch instead of reusing an already-computed smaller value.

**Related:**
[syllabus/03-data-structures-algorithms/bit-manipulation.md](../syllabus/03-data-structures-algorithms/bit-manipulation.md)

## Card: Bit manipulation's real value proposition

**Prompt:**
What is bit manipulation's typical performance benefit relative to an equivalent hash-set-based approach?

**Answer:**
Space, not time — Single Number's O(n)-time XOR approach isn't asymptotically faster than an O(n)-time hash-set approach, but it uses O(1) space instead of O(n).

**Why it matters:**
The Senior-level skill is choosing bit manipulation deliberately when the space trade-off actually matters (memory-constrained contexts, embedded systems), not reflexively wherever a simpler hash-set approach would be equally correct.

**Common trap:**
Reaching for bit tricks purely for cleverness where a hash-set solution is simpler, equally correct, and the space difference doesn't matter.

**Related:**
[syllabus/03-data-structures-algorithms/bit-manipulation.md](../syllabus/03-data-structures-algorithms/bit-manipulation.md)
