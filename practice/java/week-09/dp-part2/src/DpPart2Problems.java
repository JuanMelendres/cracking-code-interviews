import java.util.Arrays;

/** T-1411 DP part 2 -- 2D DP, 0/1 knapsack, interval DP. LC 62, 1143, 416, 5. */
public class DpPart2Problems {

    /** LC 62: Unique Paths. 2D grid DP: dp[r][c] = dp[r-1][c] + dp[r][c-1]. */
    static int uniquePaths(int rows, int cols) {
        int[] dp = new int[cols];
        Arrays.fill(dp, 1); // first row: only one way to reach any cell (go right)
        for (int r = 1; r < rows; r++) {
            for (int c = 1; c < cols; c++) {
                dp[c] += dp[c - 1]; // dp[c] currently holds dp[r-1][c]; add dp[r][c-1]
            }
        }
        return dp[cols - 1];
    }

    /** LC 1143: Longest Common Subsequence. Classic 2D DP. */
    static int longestCommonSubsequence(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];
        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }
        return dp[a.length()][b.length()];
    }

    /** LC 416: Partition Equal Subset Sum. 0/1 knapsack: can some subset sum to target? */
    static boolean canPartition(int[] nums) {
        int sum = 0;
        for (int n : nums) sum += n;
        if (sum % 2 != 0) return false;
        int target = sum / 2;
        boolean[] dp = new boolean[target + 1];
        dp[0] = true;
        for (int n : nums) {
            // iterate target DOWNWARD -- each num used at most once (0/1 knapsack,
            // the mirror of Coin Change's upward iteration for unbounded reuse)
            for (int t = target; t >= n; t--) {
                dp[t] = dp[t] || dp[t - n];
            }
        }
        return dp[target];
    }

    /** LC 5: Longest Palindromic Substring. Interval DP: dp[i][j] = is s[i..j] a palindrome. */
    static String longestPalindrome(String s) {
        int n = s.length();
        if (n == 0) return "";
        boolean[][] dp = new boolean[n][n];
        int start = 0, maxLen = 1;
        for (int i = 0; i < n; i++) dp[i][i] = true;
        // fill by increasing SUBSTRING LENGTH -- dp[i][j] depends on dp[i+1][j-1],
        // a shorter interval, so intervals must be solved shortest-first
        for (int len = 2; len <= n; len++) {
            for (int i = 0; i + len - 1 < n; i++) {
                int j = i + len - 1;
                if (s.charAt(i) != s.charAt(j)) continue;
                dp[i][j] = (len == 2) || dp[i + 1][j - 1];
                if (dp[i][j] && len > maxLen) {
                    start = i;
                    maxLen = len;
                }
            }
        }
        return s.substring(start, start + maxLen);
    }
}
