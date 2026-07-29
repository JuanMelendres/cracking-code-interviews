import java.util.Random;

public class Main {
    public static void main(String[] args) {
        System.out.println("== LC 70: Climbing Stairs ==");
        Check.eq(1, DpProblems.climbStairs(1), "climbStairs(1) = 1");
        Check.eq(2, DpProblems.climbStairs(2), "climbStairs(2) = 2");
        Check.eq(3, DpProblems.climbStairs(3), "climbStairs(3) = 3 (1+1+1, 1+2, 2+1)");
        Check.eq(8, DpProblems.climbStairs(5), "climbStairs(5) = 8 (Fibonacci shape)");

        System.out.println("\n== LC 198: House Robber ==");
        Check.eq(4, DpProblems.rob(new int[]{1, 2, 3, 1}), "rob([1,2,3,1]) = 4 (rob house 0 and 2)");
        Check.eq(12, DpProblems.rob(new int[]{2, 7, 9, 3, 1}), "rob([2,7,9,3,1]) = 12 (rob houses 0,2,4)");
        Check.eq(0, DpProblems.rob(new int[]{}), "rob([]) = 0");

        System.out.println("\n== LC 322: Coin Change ==");
        Check.eq(3, DpProblems.coinChange(new int[]{1, 2, 5}, 11), "coinChange([1,2,5], 11) = 3 (5+5+1)");
        Check.eq(-1, DpProblems.coinChange(new int[]{2}, 3), "coinChange([2], 3) = -1 (unreachable)");
        Check.eq(0, DpProblems.coinChange(new int[]{1}, 0), "coinChange([1], 0) = 0");

        System.out.println("\n== LC 300: Longest Increasing Subsequence ==");
        Check.eq(4, DpProblems.lengthOfLIS(new int[]{10, 9, 2, 5, 3, 7, 101, 18}),
                "lengthOfLIS([10,9,2,5,3,7,101,18]) = 4 (2,3,7,101 or 2,3,7,18)");
        Check.eq(1, DpProblems.lengthOfLIS(new int[]{7, 7, 7, 7}), "lengthOfLIS([7,7,7,7]) = 1 (strictly increasing)");
        Check.eq(4, DpProblems.lengthOfLIS(new int[]{0, 1, 0, 3, 2, 3}), "lengthOfLIS([0,1,0,3,2,3]) = 4");

        System.out.println("\n== LC 300 cross-check: O(n log n) vs O(n^2) reference on random inputs ==");
        Random rnd = new Random(42);
        boolean allMatch = true;
        for (int trial = 0; trial < 200; trial++) {
            int[] arr = rnd.ints(rnd.nextInt(20) + 1, -20, 20).toArray();
            int fast = DpProblems.lengthOfLIS(arr);
            int slow = DpProblems.lengthOfLisQuadratic(arr);
            if (fast != slow) {
                allMatch = false;
                System.out.println("  MISMATCH on " + java.util.Arrays.toString(arr) + ": fast=" + fast + " slow=" + slow);
            }
        }
        Check.isTrue(allMatch, "O(n log n) LIS matches O(n^2) reference on 200 random trials");

        Check.summary("Week 8 DP suite");
        if (Check.fail > 0) System.exit(1);
    }
}
