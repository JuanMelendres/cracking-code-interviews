import java.util.*;

final class Main {
    public static void main(String[] args) {
        // LC 217
        Check.isTrue(Problems.containsDuplicate(new int[]{1,2,3,1}), "LC217 containsDuplicate([1,2,3,1]) -> true");
        Check.isTrue(!Problems.containsDuplicate(new int[]{1,2,3,4}), "LC217 containsDuplicate([1,2,3,4]) -> false");

        // LC 560
        Check.eq(2, Problems.subarraySum(new int[]{1,1,1}, 2), "LC560 subarraySum([1,1,1], 2) = 2");
        Check.eq(2, Problems.subarraySum(new int[]{1,2,3}, 3), "LC560 subarraySum([1,2,3], 3) = 2");
        Check.eq(3, Problems.subarraySum(new int[]{1,-1,0}, 0), "LC560 subarraySum([1,-1,0], 0) = 3 (negatives handled)");

        // LC 349
        int[] inter1 = Problems.intersection(new int[]{1,2,2,1}, new int[]{2,2});
        Arrays.sort(inter1);
        Check.isTrue(Arrays.equals(new int[]{2}, inter1), "LC349 intersection([1,2,2,1],[2,2]) = [2]");
        int[] inter2 = Problems.intersection(new int[]{4,9,5}, new int[]{9,4,9,8,4});
        Arrays.sort(inter2);
        Check.isTrue(Arrays.equals(new int[]{4,9}, inter2), "LC349 intersection([4,9,5],[9,4,9,8,4]) = [4,9]");

        // LC 202
        Check.isTrue(Problems.isHappy(19), "LC202 isHappy(19) -> true");
        Check.isTrue(!Problems.isHappy(2), "LC202 isHappy(2) -> false (cycles, never reaches 1)");

        // LC 454
        Check.eq(2, Problems.fourSumCount(new int[]{1,2}, new int[]{-2,-1}, new int[]{-1,2}, new int[]{0,2}),
            "LC454 fourSumCount(4 arrays of 2) = 2");
        Check.eq(1, Problems.fourSumCount(new int[]{0}, new int[]{0}, new int[]{0}, new int[]{0}),
            "LC454 fourSumCount(4 zero arrays) = 1");

        Check.summary("Week 22 — Hashing (LC 217, 560, 349, 202, 454)");
    }
}
