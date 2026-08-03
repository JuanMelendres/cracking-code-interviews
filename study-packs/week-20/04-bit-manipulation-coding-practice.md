---
title: "Coding Practice — Bit Manipulation (T-1414)"
week: 20
document_type: study-pack-coding-practice
status: draft
last_reviewed: 2026-08-03
---

# Coding Practice — Bit Manipulation (T-1414)

**5 problems. All code on this page was compiled and executed — see `MANIFEST.md` for the exact commands and real pass counts.** Brings this pattern's coverage from 0/6 to 5/6 register problems — this pattern had zero coverage before this week.

---

## Problem 1 — LC 136 Single Number

**Pattern:** XOR-of-everything — every paired value cancels to zero.

```java
static int singleNumber(int[] nums) {
    int result = 0;
    for (int n : nums) result ^= n;
    return result;
}
```

**Retrospective:** XOR is commutative and associative, and `x ^ x = 0`, `0 ^ x = x` — so XOR-ing the entire array in any order cancels every value that appears exactly twice, leaving only the odd one out. **Complexity:** O(n) time, O(1) space — no hash set needed, unlike the naive approach.

## Problem 2 — LC 191 Number of 1 Bits

**Pattern:** Brian Kernighan's bit trick — `n & (n-1)` clears the lowest set bit.

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

**Retrospective:** `n - 1` flips every bit from the lowest set bit down to (and including) that bit itself; ANDing with the original `n` clears exactly that lowest set bit and leaves everything above it untouched — so the loop runs exactly `popcount(n)` times, not 32 times regardless of how many bits are actually set. **Complexity:** O(popcount) time, better than a fixed 32-iteration shift-and-check loop for sparse bit patterns.

## Problem 3 — LC 268 Missing Number

**Pattern:** XOR every index against every value — the missing number is what's left unpaired.

```java
static int missingNumber(int[] nums) {
    int result = nums.length;
    for (int i = 0; i < nums.length; i++) {
        result ^= i ^ nums[i];
    }
    return result;
}
```

**Retrospective:** XOR-ing `nums.length` (standing in for the index that would pair with it) together with every `i ^ nums[i]` means every value 0..n that's actually present cancels with its corresponding index, leaving only the missing value un-cancelled. This avoids the overflow risk of the alternative "expected sum minus actual sum" approach for large inputs. **Complexity:** O(n) time, O(1) space.

## Problem 4 — LC 371 Sum of Two Integers

**Pattern:** simulate binary addition — XOR for sum-without-carry, shifted AND for carry, repeat.

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

**Retrospective:** `a ^ b` computes each bit's sum ignoring carries; `(a & b) << 1` computes exactly the carry bits that XOR dropped, shifted into the next position — repeating until there's no carry left to add. This works correctly for negative operands too, since Java's `int` already uses two's-complement representation, which is exactly what this bit-level addition assumes. **Complexity:** O(1) time — bounded by 32 iterations regardless of operand size.

## Problem 5 — LC 338 Counting Bits

**Pattern:** bitmask DP — `bits[i] = bits[i >> 1] + (i & 1)`.

```java
static int[] countBits(int n) {
    int[] bits = new int[n + 1];
    for (int i = 1; i <= n; i++) {
        bits[i] = bits[i >> 1] + (i & 1);
    }
    return bits;
}
```

**Retrospective:** `i >> 1` is `i` with its lowest bit removed, and its popcount was already computed earlier in the same loop — so `i`'s popcount is just that plus 1 if `i`'s lowest bit is set, a direct O(1) recurrence per value instead of recomputing each value's popcount from scratch with a Kernighan-style inner loop. **Complexity:** O(n) time total (versus O(n · popcount) for a naive per-value bit-counting loop), O(n) space for the result array.

## Verification

```
$ cd practice/java/week-20/bit-manipulation/src && javac -d ../out Check.java Problems.java Main.java && java -cp ../out Main
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
