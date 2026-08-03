import java.util.*;

final class Problems {

    // ---- LC 72: Edit Distance ----
    static int minDistance(String word1, String word2) {
        int m = word1.length(), n = word2.length();
        int[][] dp = new int[m + 1][n + 1];
        for (int i = 0; i <= m; i++) dp[i][0] = i;
        for (int j = 0; j <= n; j++) dp[0][j] = j;
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j - 1], Math.min(dp[i - 1][j], dp[i][j - 1]));
                }
            }
        }
        return dp[m][n];
    }

    // ---- LC 213: House Robber II (circular street) ----
    static int robCircular(int[] nums) {
        int n = nums.length;
        if (n == 1) return nums[0];
        return Math.max(robLinear(nums, 0, n - 2), robLinear(nums, 1, n - 1));
    }

    private static int robLinear(int[] nums, int start, int end) {
        int prev2 = 0, prev1 = 0;
        for (int i = start; i <= end; i++) {
            int cur = Math.max(prev1, prev2 + nums[i]);
            prev2 = prev1;
            prev1 = cur;
        }
        return prev1;
    }

    // ---- LC 518: Coin Change II (unbounded knapsack, count combinations) ----
    static int change(int amount, int[] coins) {
        int[] dp = new int[amount + 1];
        dp[0] = 1;
        for (int coin : coins) {
            for (int a = coin; a <= amount; a++) {
                dp[a] += dp[a - coin];
            }
        }
        return dp[amount];
    }

    // ---- LC 494: Target Sum (0/1 knapsack via sum-partition transform) ----
    static int findTargetSumWays(int[] nums, int target) {
        int total = Arrays.stream(nums).sum();
        // partition into P (positive subset) and N (negative subset): P - N = target, P + N = total
        // => P = (total + target) / 2, must be non-negative integer and <= total
        if (Math.abs(target) > total || (total + target) % 2 != 0) return 0;
        int subsetSum = (total + target) / 2;
        if (subsetSum < 0) return 0;
        int[] dp = new int[subsetSum + 1];
        dp[0] = 1;
        for (int num : nums) {
            for (int s = subsetSum; s >= num; s--) {
                dp[s] += dp[s - num];
            }
        }
        return dp[subsetSum];
    }

    // ---- LC 64: Minimum Path Sum ----
    static int minPathSum(int[][] grid) {
        int rows = grid.length, cols = grid[0].length;
        int[][] dp = new int[rows][cols];
        dp[0][0] = grid[0][0];
        for (int c = 1; c < cols; c++) dp[0][c] = dp[0][c - 1] + grid[0][c];
        for (int r = 1; r < rows; r++) dp[r][0] = dp[r - 1][0] + grid[r][0];
        for (int r = 1; r < rows; r++) {
            for (int c = 1; c < cols; c++) {
                dp[r][c] = grid[r][c] + Math.min(dp[r - 1][c], dp[r][c - 1]);
            }
        }
        return dp[rows - 1][cols - 1];
    }

    // ---- LC 516: Longest Palindromic Subsequence ----
    static int longestPalindromeSubseq(String s) {
        int n = s.length();
        int[][] dp = new int[n][n]; // dp[i][j] = LPS length in s[i..j]
        for (int i = n - 1; i >= 0; i--) {
            dp[i][i] = 1;
            for (int j = i + 1; j < n; j++) {
                if (s.charAt(i) == s.charAt(j)) {
                    dp[i][j] = 2 + (i + 1 <= j - 1 ? dp[i + 1][j - 1] : 0);
                } else {
                    dp[i][j] = Math.max(dp[i + 1][j], dp[i][j - 1]);
                }
            }
        }
        return dp[0][n - 1];
    }

    // ---- LC 309: Best Time to Buy and Sell Stock with Cooldown (state-machine DP) ----
    static int maxProfitCooldown(int[] prices) {
        if (prices.length == 0) return 0;
        int hold = -prices[0];   // holding a stock
        int sold = 0;            // just sold today (must cooldown next day)
        int rest = 0;             // not holding, not just sold (free to buy)
        for (int i = 1; i < prices.length; i++) {
            int prevHold = hold, prevSold = sold, prevRest = rest;
            hold = Math.max(prevHold, prevRest - prices[i]);
            sold = prevHold + prices[i];
            rest = Math.max(prevRest, prevSold);
        }
        return Math.max(sold, rest);
    }
}
