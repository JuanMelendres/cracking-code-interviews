---
title: "Bit Manipulation"
slug: bit-manipulation
document_type: syllabus-topic
domain: 03-data-structures-algorithms
topic_id: T-2113
status: draft
version: 1.0
last_updated: 2026-09-03
mastery_levels_covered: [L1, L2, L3, L4]
prerequisites:
  - ../01-computer-science-foundations/number-representation.md
related:
  - ../01-computer-science-foundations/number-representation.md
  - hashing-patterns-and-frequency-maps.md
practice: ../../practice/java/week-20/bit-manipulation/
production_scenarios: []
interview_paths: [interview-emergency-sprint, senior-to-staff]
official_references: []
source_history:
  - study-packs/week-20/04-bit-manipulation-coding-practice.md
---

# Bit Manipulation

> **Provenance.** The five worked problems and retrospectives in Sections 7 and 15 are elevated from `study-packs/week-20/04-bit-manipulation-coding-practice.md` — real, compiled, executed code (`practice/java/week-20/bit-manipulation/`), re-verified on OpenJDK 21.0.12 while writing this chapter (10/10 assertions passing).

This is Master Topic Register **T-1414** (IWI 4.4, moderate frequency). [Number Representation](../01-computer-science-foundations/number-representation.md) establishes two's complement as the bit-level encoding every technique in this chapter operates on directly.

## 1. Why This Matters

Bit manipulation problems trade a small number of well-known bit-level tricks for dramatic space or time improvements over an equivalent hash-set or array-based approach — but their real interview value is narrower than most other patterns: a handful of specific, memorable techniques (XOR cancellation, Kernighan's bit trick, bit-level addition) recur across a bounded set of problems, and recognizing which trick applies is usually the entire difficulty, not deriving a trick from scratch.

## 2. Prerequisites

[Number Representation](../01-computer-science-foundations/number-representation.md) — specifically, two's complement encoding (this chapter's `getSum` technique, Section 4, works correctly for negative numbers *because* Java's `int` already uses this encoding) and the bit-pattern vocabulary that chapter establishes.

## 3. Foundation (L1)

**Every technique in this chapter operates on the raw bit pattern of an integer**, using three primitive bitwise operators: `&` (AND, both bits must be 1), `|` (OR, either bit is 1), and `^` (XOR, exactly one bit is 1, not both) — plus bit shifts (`<<`, `>>`) that move bits left or right.

**XOR's defining algebraic properties — `x ^ x = 0` and `0 ^ x = x` — are the foundation of this chapter's most common technique**: XOR-ing a collection of values together cancels out every value that appears an even number of times, leaving only whatever appears an odd number of times.

## 4. Core Concepts (L2)

**XOR-of-everything** (Single Number, Section 7 Problem 1) exploits XOR's cancellation property directly: XOR-ing an entire array together cancels every value appearing exactly twice, since `x ^ x = 0`, leaving only the single unpaired value.

**Brian Kernighan's bit trick** (Number of 1 Bits, Section 7 Problem 2): `n & (n - 1)` clears exactly the lowest set bit of `n`, because `n - 1` flips every bit from the lowest set bit downward (including that bit itself), and ANDing with the original `n` clears precisely that bit while leaving everything above it untouched. Repeating this until `n` reaches zero counts exactly as many iterations as there are set bits — not a fixed 32 iterations regardless of how sparse the bit pattern is.

**XOR-index-against-value** (Missing Number, Section 7 Problem 3) extends the cancellation idea: XOR-ing every index together with every array value (plus the array's own length, standing in for the one index that has no corresponding array position) cancels every value that's actually present with its corresponding index, leaving only the missing value.

**Simulated binary addition** (Sum of Two Integers, Section 7 Problem 4): XOR computes each bit position's sum while ignoring carries; AND (shifted left by one) computes exactly the carry bits XOR dropped. Repeating until there's no carry left reproduces ordinary addition using only bitwise operators.

**Bitmask DP** (Counting Bits, Section 7 Problem 5) reuses an already-computed smaller value's popcount to derive a larger value's popcount in O(1), rather than recomputing each value's popcount independently from scratch.

## 5. How It Works Internally (L3)

**Missing Number's XOR-cancellation derivation, precisely**: seeding the running XOR with `nums.length` (rather than starting from `0`) accounts for the one index (`n`, the array's length) that has no corresponding array slot to pair with, since the array holds `n` distinct values drawn from the range `0` to `n` inclusive, with exactly one missing. XOR-ing every `i ^ nums[i]` together with that seed means every value actually present in the array cancels with its own corresponding index — leaving only the one missing value un-cancelled at the end. This sidesteps a real, separate concern with the alternative "expected sum minus actual sum" approach: for large arrays, the expected sum itself could silently overflow `int` (a direct, concrete instance of [Number Representation's](../01-computer-science-foundations/number-representation.md#4-core-concepts-l2) overflow lesson), whereas XOR never has an analogous overflow failure mode.

**Sum of Two Integers' correctness for negative operands, precisely**: this bit-level addition technique works identically for negative numbers *specifically because* Java's `int` already uses two's complement representation — the same bit-level addition-with-carry mechanism that makes two's complement negation and addition work correctly (per [Number Representation's](../01-computer-science-foundations/number-representation.md#5-how-it-works-internally-l3) own derivation) is exactly what this technique relies on and exploits directly, rather than needing separate logic for negative operands.

**Counting Bits' recurrence derivation, precisely**: `i >> 1` is `i` with its lowest bit removed — a value strictly smaller than `i`, whose popcount was already computed earlier in the same loop (since the loop proceeds from `1` upward). `i`'s own popcount is exactly that already-known smaller value's popcount, plus one more *only if* `i`'s own lowest bit (`i & 1`) happens to be set. This turns computing each value's popcount into an O(1) lookup-plus-addition, rather than an O(popcount) Kernighan-style inner loop repeated independently for every value from `1` to `n`.

## 6. Practical Usage

- **Reach for XOR-of-everything the moment a problem describes "every value appears twice except one"** — a strong, distinctive trigger phrase for this specific technique.
- **Reach for Kernighan's bit trick whenever a problem needs to count set bits, and the input is expected to be sparse** (few set bits relative to the integer's total width) — it runs proportional to the popcount, not the integer's fixed bit width.
- **Recognize "implement addition/subtraction without using `+`/`-`" as a direct trigger for the XOR-plus-shifted-AND simulated-addition technique** (Section 4/5).

## 7. Examples

**Problem 1 — LC 136, Single Number.**

```java
static int singleNumber(int[] nums) {
    int result = 0;
    for (int n : nums) result ^= n;
    return result;
}
```

**Retrospective:** XOR is commutative and associative, and `x ^ x = 0`, `0 ^ x = x`. **Complexity:** O(n) time, O(1) space — no hash set needed.

**Problem 2 — LC 191, Number of 1 Bits.**

```java
static int hammingWeight(int n) {
    int count = 0;
    while (n != 0) {
        n &= (n - 1);
        count++;
    }
    return count;
}
```

**Retrospective:** see Section 4's bit-clearing argument. **Complexity:** O(popcount) time.

**Problem 3 — LC 268, Missing Number.**

```java
static int missingNumber(int[] nums) {
    int result = nums.length;
    for (int i = 0; i < nums.length; i++) {
        result ^= i ^ nums[i];
    }
    return result;
}
```

**Retrospective:** see Section 5's cancellation derivation, including why this avoids the sum-based approach's overflow risk. **Complexity:** O(n) time, O(1) space.

**Problem 4 — LC 371, Sum of Two Integers.**

```java
static int getSum(int a, int b) {
    while (b != 0) {
        int carry = (a & b) << 1;
        a = a ^ b;
        b = carry;
    }
    return a;
}
```

**Retrospective:** see Section 5's two's-complement-correctness argument. **Complexity:** O(1) time — bounded by 32 iterations.

**Problem 5 — LC 338, Counting Bits.**

```java
static int[] countBits(int n) {
    int[] bits = new int[n + 1];
    for (int i = 1; i <= n; i++) {
        bits[i] = bits[i >> 1] + (i & 1);
    }
    return bits;
}
```

**Retrospective:** see Section 5's recurrence derivation. **Complexity:** O(n) time total, O(n) space.

## 8. Common Mistakes

- **Reaching for a `HashSet`-based approach for "find the value that appears once" problems** when XOR achieves the same result in O(1) space instead of O(n) — not incorrect, but a missed opportunity to demonstrate the more elegant, expected technique.
- **Using the sum-based approach ("expected sum minus actual sum") for Missing Number on large inputs** without considering integer overflow (Section 5) — silently produces a wrong answer for sufficiently large arrays, exactly the kind of bug [Number Representation](../01-computer-science-foundations/number-representation.md#8-common-mistakes) warns about generally.
- **Assuming a fixed 32-iteration loop is required to count set bits** rather than recognizing Kernighan's trick runs proportional to the actual popcount, which matters for problems emphasizing sparse bit patterns.

## 9. Edge Cases

- **`n = 0` in Number of 1 Bits** — the `while (n != 0)` loop correctly terminates immediately, returning `0`, with no special-casing needed.
- **Negative operands in Sum of Two Integers** (the verified `getSum(-5, 5) = 0` case) — the bit-level technique handles this correctly with no separate negative-number logic, precisely because of two's complement (Section 5).
- **An array with a very large length, for Missing Number** — the XOR-based technique has no overflow failure mode, unlike the sum-based alternative (Section 5).

## 10. Performance Implications

Real, executed verification from `practice/java/week-20/bit-manipulation/` (OpenJDK 21.0.12), re-run while writing this chapter:

```
  PASS  LC136 singleNumber([2,2,1]) = 1
  PASS  LC136 singleNumber([4,1,2,1,2]) = 4
  PASS  LC191 hammingWeight(0b1011) = 3
  PASS  LC191 hammingWeight(almost all 1s) = 31
  PASS  LC268 missingNumber([3,0,1]) = 2
  PASS  LC268 missingNumber(9 elements) = 8
  PASS  LC371 getSum(1, 2) = 3, no + operator used
  PASS  LC371 getSum(-5, 5) = 0, negative operands via two's complement
  PASS  LC338 countBits(2) = [0,1,1]
  PASS  LC338 countBits(5) = [0,1,1,2,1,2]
Week 20 — Bit Manipulation (LC 136, 191, 268, 371, 338): 10/10 assertions passed
```

Every technique here achieves O(1) space where a more obvious hash-set-based approach would cost O(n) — the practical performance lesson specific to bit manipulation is that its value proposition is almost always space, not time: Single Number's O(n)-time XOR approach isn't asymptotically faster than an O(n)-time hash-set approach, but it uses O(1) space instead of O(n), a real, meaningful difference at scale.

## 11. Trade-offs

| Choice | Gains | Costs |
|---|---|---|
| XOR-based technique | O(1) space, often equally fast | Requires the problem to have the right structural shape (paired values, index/value correspondence) |
| Hash-set-based equivalent | More generally applicable, easier to read | O(n) extra space |
| Kernighan's bit trick | Runs proportional to popcount, not fixed bit width | Marginal benefit over a fixed-width loop for dense bit patterns |
| Bitmask DP (Counting Bits) | O(n) total instead of O(n · popcount) | Requires recognizing the specific recurrence; less obvious than a per-value Kernighan loop |

## 12. Senior-Level Considerations (L3)

The Senior-level skill is recognizing bit manipulation's narrow but real value proposition — space optimization, not typically a different complexity class — and choosing it deliberately when that trade-off matters (a memory-constrained context, an embedded system, an extremely hot loop where allocation itself is a measurable cost) rather than reaching for it reflexively wherever a hash-set approach would be simpler and equally correct.

## 13. Staff/System-Level Considerations (L4)

At Staff scope, bit manipulation techniques transfer directly to real systems where memory density genuinely matters at scale: a bitset-based Bloom filter or a compact permission-flags representation (packing dozens of boolean flags into a single `long` rather than dozens of separate boolean fields or a `Set<Enum>`) applies exactly this chapter's space-over-simplicity trade-off in a real production context. The XOR-cancellation technique's real-world analog appears in checksum and parity-check algorithms, where the same "cancel out paired/even-occurring values" property underlies simple error-detection schemes.

## 14. Production Scenarios

No existing `production-cookbook/` entry has a bit-manipulation-specific algorithmic root cause.

> Planned reference: a future `production-cookbook/` entry covering a real memory-density optimization (e.g., replacing a `Set<Enum>` permission representation with a packed bitmask at scale) would be a natural, non-duplicative addition connecting this chapter's Section 13 transfer to a genuine production system.

## 15. Interview Questions

### Question 1 — Given an array where every element appears twice except for one, find that single element, using O(1) extra space.

**Why interviewers ask it.** It's the canonical XOR-cancellation test, checking whether a candidate reaches for the O(1)-space bitwise technique rather than defaulting to an O(n)-space hash-set approach when the problem's phrasing ("O(1) space") is a direct hint toward it.

**Expected answer.** XOR every element in the array together. Since `x ^ x = 0` and `0 ^ x = x`, every value appearing exactly twice cancels out, leaving only the single unpaired value as the final XOR result.

**Minimum acceptable answer.** Produces a correct O(n)-space hash-set solution, even without the O(1)-space XOR technique.

**Strong Senior answer.** Produces the XOR solution directly, and can state XOR's specific algebraic properties (`x ^ x = 0`, commutative, associative) that make it work regardless of the array's element order.

**Staff-level extension.** Connects this to a real space-optimization scenario (Section 13) where this exact space-vs-simplicity trade-off matters in production.

**Common mistakes.** Overlooking the "O(1) space" hint entirely and defaulting straight to a hash-set solution without considering whether a more space-efficient technique exists.

**Follow-up questions.** "What if every element appeared three times except one?" (A genuinely harder variant requiring bit-counting per position modulo 3, not simple XOR — a good check for whether a candidate over-generalizes the XOR trick beyond where it actually applies.)

### Question 2 — Why does `n & (n - 1)` clear the lowest set bit of `n`, and how does that help count set bits efficiently?

**Why interviewers ask it.** It's a precise derivation check for Kernighan's bit trick, testing whether the mechanism is genuinely understood or the formula is recited without being able to explain why it works.

**Expected answer.** `n - 1` flips every bit from `n`'s lowest set bit downward, including that bit itself (borrowing propagates through all the trailing zero bits below it, then flips the lowest set bit to 0). ANDing this with the original `n` keeps every bit above the lowest set bit unchanged (since those bits are identical in both `n` and `n-1`) while zeroing out the lowest set bit specifically (since it's `1` in `n` but `0` in `n-1`). Repeating this operation until `n` reaches zero takes exactly as many iterations as `n` has set bits, since each iteration clears exactly one.

**Minimum acceptable answer.** Knows the formula and that it clears the lowest set bit, even without a precise bit-level derivation of why.

**Strong Senior answer.** Derives the borrow-propagation mechanism precisely, and can state why this is more efficient than a fixed 32-iteration shift-and-check loop specifically for sparse bit patterns.

**Staff-level extension.** Connects this to a broader principle about algorithm cost being proportional to actual problem size (popcount here) rather than a fixed, worst-case-sized loop — the same underlying idea as choosing an algorithm whose cost scales with actual data characteristics rather than a conservative upper bound.

**Common mistakes.** Confusing this with a different bit trick (e.g., isolating the lowest set bit via `n & -n`, a related but distinct operation) or being unable to explain the borrow-propagation mechanism when asked to justify the formula.

**Follow-up questions.** "How would you find just the lowest set bit itself, not clear it?" (`n & -n`, isolating rather than clearing — a related, worth-knowing sibling technique.)

## 16. Coding/Practice Exercises

- Run the [existing practice code](../../practice/java/week-20/bit-manipulation/) yourself and confirm the same 10/10 assertions pass.
- Implement Missing Number (Section 7, Problem 3) using the "expected sum minus actual sum" approach instead of XOR, then construct a test input large enough to trigger integer overflow in that approach — confirm it produces a wrong answer where the XOR-based version doesn't.
- Extend Single Number (Section 7, Problem 1) to the "every element appears three times except one" variant referenced in Interview Question 1's follow-up, deriving the modulo-3 bit-counting technique from scratch.

## 17. Debugging Exercises

**Symptom:** a "find the missing value" feature, implemented using the sum-based approach (`expectedSum - actualSum`), works correctly on all test data during development but produces an obviously wrong result once deployed against a much larger real dataset.

**Diagnose:** this is precisely [Number Representation's](../01-computer-science-foundations/number-representation.md#8-common-mistakes) silent-overflow lesson made concrete — the sum of a large range of integers can exceed `Integer.MAX_VALUE` well before the dataset itself becomes unreasonably large, silently wrapping around to a nonsensical value (Section 5). Confirm by computing the theoretical maximum possible sum for the real dataset's size and checking whether it exceeds `Integer.MAX_VALUE`; the fix is either widening the sum to `long`, or switching to this chapter's XOR-based technique (Section 7, Problem 3), which has no analogous overflow failure mode at any input size.

## 18. Design Exercises

**Design constraint:** design a compact, memory-efficient representation for a user-permissions system that must track up to 64 independent boolean permission flags per user, across millions of users, where memory footprint per user genuinely matters at that scale.

Design this using a single packed `long` per user (one bit per permission flag) rather than a `Set<Enum>` or an array of 64 booleans, directly applying this chapter's space-optimization principle (Section 11/13). State the specific bitwise operations needed: checking a flag (`(permissions >> flagIndex) & 1`), setting a flag (`permissions |= (1L << flagIndex)`), and clearing a flag (`permissions &= ~(1L << flagIndex)`) — and name the real trade-off versus the more readable `Set<Enum>` alternative: a packed `long` uses roughly 64x less memory per user for this specific data, at the cost of needing these explicit bitwise operations instead of a self-documenting collection API.

## 19. Further Reading

- [Number Representation](../01-computer-science-foundations/number-representation.md) — the two's complement encoding this entire chapter's techniques operate on directly.
- [Hashing Patterns and Frequency Maps](hashing-patterns-and-frequency-maps.md) — the more general, more widely-applicable O(n)-space alternative to several of this chapter's O(1)-space bit-manipulation techniques.

## 20. Mastery Checklist

| Level | You can... | Verify with |
|---|---|---|
| L1 | Explain, in plain language, what AND/OR/XOR do bit-by-bit, and why `x ^ x = 0` | [Section 3](#3-foundation-l1) |
| L2 | Recognize when a problem's phrasing ("appears twice except one," "O(1) space") signals a specific bit-manipulation technique | [Interview Question 1](#question-1--given-an-array-where-every-element-appears-twice-except-for-one-find-that-single-element-using-o1-extra-space) |
| L3 | Derive Kernighan's bit trick's borrow-propagation mechanism, and explain why Sum of Two Integers works correctly for negative operands | [Section 10's real verification](#10-performance-implications), [Section 5](#5-how-it-works-internally-l3) |
| L4 | Diagnose a real integer-overflow bug in a sum-based algorithm as a case for a bit-manipulation alternative (Section 17), and design a real memory-efficient bitmask representation deliberately (Section 18) | [Debugging Exercise](#17-debugging-exercises), [Section 13](#13-staffsystem-level-considerations-l4) |
