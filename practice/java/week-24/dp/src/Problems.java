import java.util.*;

final class Problems {

    // ---- LC 329: Longest Increasing Path in a Matrix (DFS + memoization) ----
    static int longestIncreasingPath(int[][] matrix) {
        int rows = matrix.length, cols = matrix[0].length;
        int[][] memo = new int[rows][cols];
        int best = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                best = Math.max(best, dfs329(matrix, r, c, memo));
            }
        }
        return best;
    }

    private static final int[][] DIRS_4 = {{0,1},{0,-1},{1,0},{-1,0}};

    private static int dfs329(int[][] matrix, int r, int c, int[][] memo) {
        if (memo[r][c] != 0) return memo[r][c];
        int rows = matrix.length, cols = matrix[0].length;
        int best = 1;
        for (int[] d : DIRS_4) {
            int nr = r + d[0], nc = c + d[1];
            if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && matrix[nr][nc] > matrix[r][c]) {
                best = Math.max(best, 1 + dfs329(matrix, nr, nc, memo));
            }
        }
        memo[r][c] = best;
        return best;
    }

    // ---- LC 312: Burst Balloons (interval DP) ----
    static int maxCoins(int[] nums) {
        int n = nums.length;
        int[] balloons = new int[n + 2];
        balloons[0] = 1;
        balloons[n + 1] = 1;
        for (int i = 0; i < n; i++) balloons[i + 1] = nums[i];

        int[][] dp = new int[n + 2][n + 2]; // dp[left][right] = max coins bursting all strictly between left,right
        for (int len = 2; len <= n + 1; len++) {
            for (int left = 0; left + len <= n + 1; left++) {
                int right = left + len;
                for (int k = left + 1; k < right; k++) {
                    int coins = balloons[left] * balloons[k] * balloons[right] + dp[left][k] + dp[k][right];
                    dp[left][right] = Math.max(dp[left][right], coins);
                }
            }
        }
        return dp[0][n + 1];
    }

    // ---- LC 10: Regular Expression Matching ('.' and '*') ----
    static boolean isMatch(String s, String p) {
        int m = s.length(), n = p.length();
        boolean[][] dp = new boolean[m + 1][n + 1];
        dp[0][0] = true;
        for (int j = 1; j <= n; j++) {
            if (p.charAt(j - 1) == '*') dp[0][j] = dp[0][j - 2];
        }
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                char sc = s.charAt(i - 1), pc = p.charAt(j - 1);
                if (pc == '*') {
                    dp[i][j] = dp[i][j - 2]; // treat preceding element as occurring zero times
                    char preceding = p.charAt(j - 2);
                    if (preceding == '.' || preceding == sc) {
                        dp[i][j] = dp[i][j] || dp[i - 1][j]; // one or more occurrences
                    }
                } else if (pc == '.' || pc == sc) {
                    dp[i][j] = dp[i - 1][j - 1];
                }
            }
        }
        return dp[m][n];
    }

    // ---- LC 44: Wildcard Matching ('?' and '*') ----
    static boolean isMatchWildcard(String s, String p) {
        int m = s.length(), n = p.length();
        boolean[][] dp = new boolean[m + 1][n + 1];
        dp[0][0] = true;
        for (int j = 1; j <= n; j++) {
            if (p.charAt(j - 1) == '*') dp[0][j] = dp[0][j - 1];
        }
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                char pc = p.charAt(j - 1);
                if (pc == '*') {
                    dp[i][j] = dp[i - 1][j] || dp[i][j - 1]; // '*' matches current char, or matches empty
                } else if (pc == '?' || pc == s.charAt(i - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                }
            }
        }
        return dp[m][n];
    }

    // ---- LC 96: Unique Binary Search Trees (Catalan number DP) ----
    static int numTrees(int n) {
        int[] dp = new int[n + 1];
        dp[0] = 1;
        dp[1] = 1;
        for (int nodes = 2; nodes <= n; nodes++) {
            for (int root = 1; root <= nodes; root++) {
                dp[nodes] += dp[root - 1] * dp[nodes - root];
            }
        }
        return dp[n];
    }

    // ---- LC 32: Longest Valid Parentheses (DP) ----
    static int longestValidParentheses(String s) {
        int n = s.length();
        int[] dp = new int[n]; // dp[i] = length of longest valid substring ENDING at i
        int best = 0;
        for (int i = 1; i < n; i++) {
            if (s.charAt(i) == ')') {
                if (s.charAt(i - 1) == '(') {
                    dp[i] = (i >= 2 ? dp[i - 2] : 0) + 2;
                } else {
                    int matchStart = i - dp[i - 1] - 1;
                    if (matchStart >= 0 && s.charAt(matchStart) == '(') {
                        dp[i] = dp[i - 1] + 2 + (matchStart >= 1 ? dp[matchStart - 1] : 0);
                    }
                }
                best = Math.max(best, dp[i]);
            }
        }
        return best;
    }
}
