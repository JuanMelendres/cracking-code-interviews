import java.util.*;

final class Main {
    public static void main(String[] args) {
        // LC 315: Count of Smaller Numbers After Self
        Check.eq(List.of(2, 1, 1, 0), Problems.countSmaller(new int[]{5, 2, 6, 1}),
            "LC315 countSmaller([5,2,6,1]) = [2,1,1,0]");
        Check.eq(List.of(0), Problems.countSmaller(new int[]{-1}),
            "LC315 countSmaller([-1]) = [0]");
        Check.eq(List.of(0, 0), Problems.countSmaller(new int[]{-1, -1}),
            "LC315 countSmaller([-1,-1]) = [0,0]");
        Check.eq(List.of(4, 3, 2, 1, 0), Problems.countSmaller(new int[]{5, 4, 3, 2, 1}),
            "LC315 countSmaller(strictly descending [5,4,3,2,1]) = [4,3,2,1,0]");

        // LC 493: Reverse Pairs
        Check.eq(2, Problems.reversePairs(new int[]{1, 3, 2, 3, 1}),
            "LC493 reversePairs([1,3,2,3,1]) = 2");
        Check.eq(3, Problems.reversePairs(new int[]{2, 4, 3, 5, 1}),
            "LC493 reversePairs([2,4,3,5,1]) = 3");
        Check.eq(0, Problems.reversePairs(new int[]{1, 2, 3, 4}),
            "LC493 reversePairs(sorted ascending [1,2,3,4]) = 0");
        Check.eq(3, Problems.reversePairs(new int[]{2147483647, -2147483648, -1, 0}),
            "LC493 reversePairs at int extremes -> 3 (verifies long-math overflow guard)");

        // LC 327: Count of Range Sum
        Check.eq(3, Problems.countRangeSum(new int[]{-2, 5, -1}, -2, 2),
            "LC327 countRangeSum([-2,5,-1], -2, 2) = 3");
        Check.eq(1, Problems.countRangeSum(new int[]{0}, 0, 0),
            "LC327 countRangeSum([0], 0, 0) = 1");
        Check.eq(0, Problems.countRangeSum(new int[]{0}, 1, 1),
            "LC327 countRangeSum([0], 1, 1) = 0 (no range sum in [1,1])");
        Check.eq(6, Problems.countRangeSum(new int[]{1, 1, 1}, 1, 3),
            "LC327 countRangeSum([1,1,1], 1, 3) = 6 (every one of the 6 contiguous ranges qualifies)");

        Check.summary("Advanced Structures — Fenwick/BIT (LC 315, 493, 327)");
    }
}
