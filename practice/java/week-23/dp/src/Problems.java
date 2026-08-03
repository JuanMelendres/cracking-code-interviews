final class Problems {

    // ---- LC 91: Decode Ways ----
    static int numDecodings(String s) {
        int n = s.length();
        if (n == 0 || s.charAt(0) == '0') return 0;
        int prev2 = 1; // dp[i-2], empty-prefix base case
        int prev1 = 1; // dp[i-1] (dp[1])
        for (int i = 2; i <= n; i++) {
            int cur = 0;
            char oneDigit = s.charAt(i - 1);
            if (oneDigit != '0') cur += prev1;
            int twoDigit = Integer.parseInt(s.substring(i - 2, i));
            if (twoDigit >= 10 && twoDigit <= 26) cur += prev2;
            prev2 = prev1;
            prev1 = cur;
        }
        return prev1;
    }

    // ---- LC 63: Unique Paths II (with obstacles) ----
    static int uniquePathsWithObstacles(int[][] grid) {
        int rows = grid.length, cols = grid[0].length;
        int[] dp = new int[cols];
        dp[0] = (grid[0][0] == 0) ? 1 : 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == 1) {
                    dp[c] = 0;
                } else if (c > 0) {
                    dp[c] += dp[c - 1];
                }
            }
        }
        return dp[cols - 1];
    }

    // ---- LC 337: House Robber III (tree DP) ----
    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
        TreeNode(int val, TreeNode left, TreeNode right) { this.val = val; this.left = left; this.right = right; }
    }

    static int robTree(TreeNode root) {
        int[] result = robHelper(root);
        return Math.max(result[0], result[1]);
    }

    // returns {maxIfRobbed, maxIfNotRobbed}
    private static int[] robHelper(TreeNode node) {
        if (node == null) return new int[]{0, 0};
        int[] left = robHelper(node.left);
        int[] right = robHelper(node.right);
        int robbed = node.val + left[1] + right[1];
        int notRobbed = Math.max(left[0], left[1]) + Math.max(right[0], right[1]);
        return new int[]{robbed, notRobbed};
    }

    // ---- LC 188: Best Time to Buy and Sell Stock IV (at most k transactions) ----
    static int maxProfitK(int k, int[] prices) {
        int n = prices.length;
        if (n == 0 || k == 0) return 0;
        // if k >= n/2, unlimited transactions is equivalent to bounding by k
        if (k >= n / 2) {
            int profit = 0;
            for (int i = 1; i < n; i++) {
                if (prices[i] > prices[i - 1]) profit += prices[i] - prices[i - 1];
            }
            return profit;
        }
        int[] hold = new int[k + 1];
        int[] cash = new int[k + 1];
        java.util.Arrays.fill(hold, Integer.MIN_VALUE / 2);
        for (int price : prices) {
            for (int t = 1; t <= k; t++) {
                hold[t] = Math.max(hold[t], cash[t - 1] - price);
                cash[t] = Math.max(cash[t], hold[t] + price);
            }
        }
        return cash[k];
    }

    // ---- LC 132: Palindrome Partitioning II (minimum cuts) ----
    static int minCut(String s) {
        int n = s.length();
        boolean[][] isPalindrome = new boolean[n][n];
        for (int i = n - 1; i >= 0; i--) {
            for (int j = i; j < n; j++) {
                if (s.charAt(i) == s.charAt(j) && (j - i <= 2 || isPalindrome[i + 1][j - 1])) {
                    isPalindrome[i][j] = true;
                }
            }
        }
        int[] dp = new int[n]; // dp[i] = min cuts for s[0..i]
        for (int i = 0; i < n; i++) {
            if (isPalindrome[0][i]) {
                dp[i] = 0;
                continue;
            }
            dp[i] = Integer.MAX_VALUE;
            for (int j = 1; j <= i; j++) {
                if (isPalindrome[j][i]) {
                    dp[i] = Math.min(dp[i], dp[j - 1] + 1);
                }
            }
        }
        return dp[n - 1];
    }
}
