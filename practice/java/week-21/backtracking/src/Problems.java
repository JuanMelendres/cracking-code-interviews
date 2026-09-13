import java.util.*;

final class Problems {

    // ---- LC 17: Letter Combinations of a Phone Number ----
    private static final String[] DIGIT_LETTERS = {
        "", "", "abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz"
    };

    static List<String> letterCombinations(String digits) {
        List<String> result = new ArrayList<>();
        if (digits.isEmpty()) return result;
        backtrack17(digits, 0, new StringBuilder(), result);
        return result;
    }

    private static void backtrack17(String digits, int idx, StringBuilder cur, List<String> result) {
        if (idx == digits.length()) {
            result.add(cur.toString());
            return;
        }
        String letters = DIGIT_LETTERS[digits.charAt(idx) - '0'];
        for (char c : letters.toCharArray()) {
            cur.append(c);
            backtrack17(digits, idx + 1, cur, result);
            cur.deleteCharAt(cur.length() - 1);
        }
    }

    // ---- LC 79: Word Search ----
    static boolean exist(char[][] board, String word) {
        int rows = board.length, cols = board[0].length;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (dfs79(board, r, c, word, 0)) return true;
            }
        }
        return false;
    }

    private static boolean dfs79(char[][] board, int r, int c, String word, int idx) {
        if (idx == word.length()) return true;
        int rows = board.length, cols = board[0].length;
        if (r < 0 || r >= rows || c < 0 || c >= cols || board[r][c] != word.charAt(idx)) return false;
        char saved = board[r][c];
        board[r][c] = '#';
        boolean found = dfs79(board, r + 1, c, word, idx + 1)
            || dfs79(board, r - 1, c, word, idx + 1)
            || dfs79(board, r, c + 1, word, idx + 1)
            || dfs79(board, r, c - 1, word, idx + 1);
        board[r][c] = saved;
        return found;
    }

    // ---- LC 47: Permutations II (dedup) ----
    static List<List<Integer>> permuteUnique(int[] nums) {
        Arrays.sort(nums);
        List<List<Integer>> result = new ArrayList<>();
        boolean[] used = new boolean[nums.length];
        backtrack47(nums, used, new ArrayList<>(), result);
        return result;
    }

    private static void backtrack47(int[] nums, boolean[] used, List<Integer> cur, List<List<Integer>> result) {
        if (cur.size() == nums.length) {
            result.add(new ArrayList<>(cur));
            return;
        }
        for (int i = 0; i < nums.length; i++) {
            if (used[i]) continue;
            // skip duplicate values at the same recursion depth, but allow reuse across depths
            if (i > 0 && nums[i] == nums[i - 1] && !used[i - 1]) continue;
            used[i] = true;
            cur.add(nums[i]);
            backtrack47(nums, used, cur, result);
            cur.remove(cur.size() - 1);
            used[i] = false;
        }
    }

    // ---- LC 40: Combination Sum II (dedup, each candidate used at most once) ----
    static List<List<Integer>> combinationSum2(int[] candidates, int target) {
        Arrays.sort(candidates);
        List<List<Integer>> result = new ArrayList<>();
        backtrack40(candidates, target, 0, new ArrayList<>(), result);
        return result;
    }

    private static void backtrack40(int[] candidates, int remaining, int start, List<Integer> cur, List<List<Integer>> result) {
        if (remaining == 0) {
            result.add(new ArrayList<>(cur));
            return;
        }
        for (int i = start; i < candidates.length; i++) {
            if (candidates[i] > remaining) break;
            if (i > start && candidates[i] == candidates[i - 1]) continue; // skip duplicate at this depth
            cur.add(candidates[i]);
            backtrack40(candidates, remaining - candidates[i], i + 1, cur, result);
            cur.remove(cur.size() - 1);
        }
    }

    // ---- LC 51: N-Queens ----
    static List<List<String>> solveNQueens(int n) {
        List<List<String>> result = new ArrayList<>();
        int[] queenCol = new int[n]; // queenCol[row] = column of queen in that row
        boolean[] usedCol = new boolean[n];
        boolean[] usedDiag = new boolean[2 * n - 1];   // row - col + (n-1)
        boolean[] usedAntiDiag = new boolean[2 * n - 1]; // row + col
        backtrack51(0, n, queenCol, usedCol, usedDiag, usedAntiDiag, result);
        return result;
    }

    private static void backtrack51(int row, int n, int[] queenCol, boolean[] usedCol,
                                     boolean[] usedDiag, boolean[] usedAntiDiag, List<List<String>> result) {
        if (row == n) {
            result.add(buildBoard(queenCol, n));
            return;
        }
        for (int col = 0; col < n; col++) {
            int diag = row - col + (n - 1);
            int antiDiag = row + col;
            if (usedCol[col] || usedDiag[diag] || usedAntiDiag[antiDiag]) continue;
            queenCol[row] = col;
            usedCol[col] = usedDiag[diag] = usedAntiDiag[antiDiag] = true;
            backtrack51(row + 1, n, queenCol, usedCol, usedDiag, usedAntiDiag, result);
            usedCol[col] = usedDiag[diag] = usedAntiDiag[antiDiag] = false;
        }
    }

    private static List<String> buildBoard(int[] queenCol, int n) {
        List<String> board = new ArrayList<>();
        for (int row = 0; row < n; row++) {
            char[] line = new char[n];
            Arrays.fill(line, '.');
            line[queenCol[row]] = 'Q';
            board.add(new String(line));
        }
        return board;
    }
}
