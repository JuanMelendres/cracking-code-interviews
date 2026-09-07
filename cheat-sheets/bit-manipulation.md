---
title: "Cheat Sheet: Bit Manipulation"
slug: bit-manipulation
document_type: cheat-sheet
domain: 03-data-structures-algorithms
topic_id: T-2113
canonical: ../syllabus/03-data-structures-algorithms/bit-manipulation.md
last_updated: 2026-09-06
---

# Bit Manipulation

**Canonical chapter:** [`syllabus/03-data-structures-algorithms/bit-manipulation.md`](../syllabus/03-data-structures-algorithms/bit-manipulation.md)

## Core Mental Model

Bit manipulation problems trade a small number of well-known bit-level tricks for dramatic *space* improvements over an equivalent hash-set or array-based approach — the value proposition is almost always space, not time. A bounded set of specific, memorable techniques (XOR cancellation, Kernighan's bit trick, bit-level addition) recur across a bounded set of problems; recognizing which trick applies is usually the entire difficulty, not deriving a trick from scratch.

## Essential Definitions

- **XOR cancellation** — `x ^ x = 0` and `0 ^ x = x`: XOR-ing a collection cancels every value appearing an even number of times, leaving only what appears an odd number of times.
- **Kernighan's bit trick** — `n & (n - 1)` clears exactly the lowest set bit of `n`, because `n - 1` flips every bit from the lowest set bit downward.
- **Simulated binary addition** — XOR computes each bit's sum ignoring carries; `(a & b) << 1` computes exactly the carry XOR dropped; repeat until no carry remains.
- **Bitmask DP** — reuse an already-computed smaller value's popcount to derive a larger value's popcount in O(1) (`bits[i] = bits[i >> 1] + (i & 1)`).

## Recognition Signals / When to Use This Pattern

| Signal in the problem | Technique | Complexity |
|---|---|---|
| "Every value appears twice except one" | XOR-of-everything | O(n) time, O(1) space |
| Count set bits, especially for sparse bit patterns | Kernighan's bit trick (`n &= n - 1`) | O(popcount) |
| Array holds `n` distinct values from `0..n` with one missing | XOR index against value (seeded with `n`) | O(n) time, O(1) space, no overflow risk |
| "Implement addition/subtraction without `+`/`-`" | XOR (sum) + shifted AND (carry), repeat until no carry | O(1), bounded by 32 iterations |
| Need popcount for every value from `1..n` | Bitmask DP recurrence | O(n) total |

## Common Mistakes

- Reaching for a `HashSet`-based approach for "find the value that appears once" problems when XOR achieves the same result in O(1) space instead of O(n) — not incorrect, but a missed opportunity.
- Using the sum-based ("expected sum minus actual sum") approach for Missing Number on large inputs without considering integer overflow — silently produces a wrong answer for sufficiently large arrays. XOR has no analogous overflow failure mode.
- Assuming a fixed 32-iteration loop is required to count set bits, rather than recognizing Kernighan's trick runs proportional to the actual popcount.

## Complexity Reference

- Single Number (XOR): O(n) time, O(1) space — no hash set needed.
- Number of 1 Bits (Kernighan): O(popcount) time.
- Missing Number (XOR): O(n) time, O(1) space.
- Sum of Two Integers: O(1) time, bounded by 32 iterations.
- Counting Bits (bitmask DP): O(n) time total, O(n) space.

## Interview Answer Skeleton

**30-sec:** Bit manipulation trades a handful of specific bitwise tricks (XOR cancellation, Kernighan's bit trick, bit-level addition) for O(1) space where a hash-set-based approach would cost O(n) — the win is almost always space, not asymptotic time.

**2-min:** For "every value appears twice except one," XOR the entire array together — pairs cancel via `x ^ x = 0`, leaving the unpaired value. For "count set bits," repeatedly apply `n &= (n - 1)`, which clears the lowest set bit each iteration, taking exactly as many iterations as there are set bits. For Missing Number specifically, prefer XOR-index-against-value over "expected sum minus actual sum," since the sum-based approach can silently overflow `int` on large inputs while XOR never can.

**Whiteboard:** Write out a small array of 4-bit binary numbers stacked vertically, one per row, and show the column-wise XOR canceling every paired value bit by bit, leaving only the unpaired value's bit pattern. For Kernighan's trick, write `n` and `n - 1` in binary side by side and highlight how the borrow propagates through the trailing zeros to flip the lowest set bit, then show the AND zeroing exactly that bit.

**Staff-level framing:** Bit manipulation techniques transfer directly to real systems where memory density matters at scale — a bitset-based Bloom filter or a packed permission-flags representation (dozens of boolean flags in a single `long` rather than a `Set<Enum>`) applies this chapter's space-over-simplicity trade-off in a real production context. XOR-cancellation's real-world analog appears in checksum and parity-check algorithms.

## Production Warning Signs

- **Symptom:** a "find the missing value" feature implemented with the sum-based approach (`expectedSum - actualSum`) works correctly during development but produces an obviously wrong result once deployed against a much larger real dataset.
- **Diagnose:** the sum of a large range of integers can exceed `Integer.MAX_VALUE` well before the dataset itself becomes unreasonably large, silently wrapping around. Compute the theoretical maximum possible sum for the real dataset's size and check whether it exceeds `Integer.MAX_VALUE`; fix by widening the sum to `long`, or switching to the XOR-based technique, which has no analogous overflow failure mode at any input size.

## Related

- syllabus/01-computer-science-foundations/number-representation.md
- syllabus/03-data-structures-algorithms/hashing-patterns-and-frequency-maps.md
