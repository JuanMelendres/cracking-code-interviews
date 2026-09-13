public class Problems {

    // LC 136 — Single Number. XOR of every element: pairs cancel to 0
    // (x^x=0), and 0^x=x, leaving only the unpaired value. O(n) time, O(1) space.
    static int singleNumber(int[] nums) {
        int result = 0;
        for (int n : nums) result ^= n;
        return result;
    }

    // LC 191 — Number of 1 Bits. Brian Kernighan's trick: n & (n-1) clears
    // the lowest set bit each iteration; loop count = popcount. O(popcount) time.
    static int hammingWeight(int n) {
        int count = 0;
        while (n != 0) {
            n &= (n - 1);
            count++;
        }
        return count;
    }

    // LC 268 — Missing Number. XOR every index AND every value together;
    // every number 0..n except the missing one cancels with its index pair.
    // O(n) time, O(1) space -- avoids the overflow risk of a sum-based approach.
    static int missingNumber(int[] nums) {
        int result = nums.length;
        for (int i = 0; i < nums.length; i++) {
            result ^= i ^ nums[i];
        }
        return result;
    }

    // LC 371 — Sum of Two Integers (no + or -). Simulate binary addition:
    // XOR gives the sum-without-carry; AND-then-shift-left gives the
    // carry; repeat until no carry remains. O(1) time (bounded by 32 bits).
    static int getSum(int a, int b) {
        while (b != 0) {
            int carry = (a & b) << 1;
            a = a ^ b;
            b = carry;
        }
        return a;
    }

    // LC 338 — Counting Bits. Bitmask DP: bits[i] = bits[i >> 1] + (i & 1)
    // -- popcount of i equals popcount of i-with-lowest-bit-removed, plus
    // whether that lowest bit was set. O(n) time, O(n) space.
    static int[] countBits(int n) {
        int[] bits = new int[n + 1];
        for (int i = 1; i <= n; i++) {
            bits[i] = bits[i >> 1] + (i & 1);
        }
        return bits;
    }
}
