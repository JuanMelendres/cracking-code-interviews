import java.util.*;

/** T-1411 DP part 1 -- LC 70, 198, 322, 300. */
public class DpProblems {

    /** LC 70: Climbing Stairs. Bottom-up, O(n) time O(1) space. */
    static int climbStairs(int n) {
        if (n <= 2) return n;
        int prev2 = 1, prev1 = 2;
        for (int i = 3; i <= n; i++) {
            int cur = prev1 + prev2;
            prev2 = prev1;
            prev1 = cur;
        }
        return prev1;
    }

    /** LC 198: House Robber. dp[i] = max(dp[i-1], dp[i-2] + nums[i]). */
    static int rob(int[] nums) {
        int prev2 = 0, prev1 = 0;
        for (int n : nums) {
            int cur = Math.max(prev1, prev2 + n);
            prev2 = prev1;
            prev1 = cur;
        }
        return prev1;
    }

    /** LC 322: Coin Change. Bottom-up unbounded knapsack, min coins. */
    static int coinChange(int[] coins, int amount) {
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, amount + 1);
        dp[0] = 0;
        for (int a = 1; a <= amount; a++) {
            for (int c : coins) {
                if (c <= a) dp[a] = Math.min(dp[a], dp[a - c] + 1);
            }
        }
        return dp[amount] > amount ? -1 : dp[amount];
    }

    /** LC 300: Longest Increasing Subsequence. O(n log n) patience sorting. */
    static int lengthOfLIS(int[] nums) {
        int[] tails = new int[nums.length];
        int len = 0;
        for (int n : nums) {
            int lo = 0, hi = len;
            while (lo < hi) {
                int mid = (lo + hi) / 2;
                if (tails[mid] < n) lo = mid + 1; else hi = mid;
            }
            tails[lo] = n;
            if (lo == len) len++;
        }
        return len;
    }

    /** LC 300, O(n^2) reference implementation -- used only to cross-check the O(n log n) version. */
    static int lengthOfLisQuadratic(int[] nums) {
        int[] dp = new int[nums.length];
        Arrays.fill(dp, 1);
        int best = nums.length == 0 ? 0 : 1;
        for (int i = 1; i < nums.length; i++) {
            for (int j = 0; j < i; j++) {
                if (nums[j] < nums[i]) dp[i] = Math.max(dp[i], dp[j] + 1);
            }
            best = Math.max(best, dp[i]);
        }
        return best;
    }
}
