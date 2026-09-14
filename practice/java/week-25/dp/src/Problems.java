final class Problems {

    // ---- LC 152: Maximum Product Subarray ----
    static int maxProduct(int[] nums) {
        int maxEndingHere = nums[0], minEndingHere = nums[0], best = nums[0];
        for (int i = 1; i < nums.length; i++) {
            int n = nums[i];
            if (n < 0) {
                int tmp = maxEndingHere;
                maxEndingHere = minEndingHere;
                minEndingHere = tmp;
            }
            maxEndingHere = Math.max(n, maxEndingHere * n);
            minEndingHere = Math.min(n, minEndingHere * n);
            best = Math.max(best, maxEndingHere);
        }
        return best;
    }

    // ---- LC 279: Perfect Squares ----
    static int numSquares(int n) {
        int[] dp = new int[n + 1];
        java.util.Arrays.fill(dp, Integer.MAX_VALUE);
        dp[0] = 0;
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j * j <= i; j++) {
                dp[i] = Math.min(dp[i], dp[i - j * j] + 1);
            }
        }
        return dp[n];
    }

    // ---- LC 174: Dungeon Game (reverse 2D DP, computed from destination backward) ----
    static int calculateMinimumHP(int[][] dungeon) {
        int rows = dungeon.length, cols = dungeon[0].length;
        int[][] dp = new int[rows + 1][cols + 1];
        for (int[] row : dp) java.util.Arrays.fill(row, Integer.MAX_VALUE);
        dp[rows][cols - 1] = 1;
        dp[rows - 1][cols] = 1;
        for (int r = rows - 1; r >= 0; r--) {
            for (int c = cols - 1; c >= 0; c--) {
                int needed = Math.min(dp[r + 1][c], dp[r][c + 1]) - dungeon[r][c];
                dp[r][c] = Math.max(1, needed);
            }
        }
        return dp[0][0];
    }

    // ---- LC 673: Number of Longest Increasing Subsequence ----
    static int findNumberOfLIS(int[] nums) {
        int n = nums.length;
        int[] length = new int[n]; // length of longest increasing subsequence ending at i
        int[] count = new int[n]; // number of such subsequences of that length ending at i
        java.util.Arrays.fill(length, 1);
        java.util.Arrays.fill(count, 1);
        int maxLen = 1;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < i; j++) {
                if (nums[j] < nums[i]) {
                    if (length[j] + 1 > length[i]) {
                        length[i] = length[j] + 1;
                        count[i] = count[j];
                    } else if (length[j] + 1 == length[i]) {
                        count[i] += count[j];
                    }
                }
            }
            maxLen = Math.max(maxLen, length[i]);
        }
        int total = 0;
        for (int i = 0; i < n; i++) {
            if (length[i] == maxLen) total += count[i];
        }
        return total;
    }

    // ---- LC 1220: Count Vowels Permutation (fixed 5-state transition DP) ----
    static int countVowelPermutation(int n) {
        final int MOD = 1_000_000_007;
        // states: 0=a, 1=e, 2=i, 3=o, 4=u
        long[] dp = {1, 1, 1, 1, 1};
        for (int step = 2; step <= n; step++) {
            long[] next = new long[5];
            next[0] = (dp[1] + dp[2] + dp[4]) % MOD;              // a can follow e, i, u
            next[1] = (dp[0] + dp[2]) % MOD;                       // e can follow a, i
            next[2] = (dp[1] + dp[3]) % MOD;                       // i can follow e, o
            next[3] = dp[2];                                       // o can follow only i
            next[4] = (dp[2] + dp[3]) % MOD;                       // u can follow i, o
            dp = next;
        }
        long total = 0;
        for (long v : dp) total = (total + v) % MOD;
        return (int) total;
    }
}
