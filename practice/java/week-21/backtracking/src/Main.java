import java.util.*;

final class Main {
    public static void main(String[] args) {
        // LC 17
        List<String> combos = Problems.letterCombinations("23");
        Check.eq(9, combos.size(), "LC17 letterCombinations(23) count = 9");
        Check.isTrue(new HashSet<>(combos).containsAll(
            List.of("ad","ae","af","bd","be","bf","cd","ce","cf")), "LC17 letterCombinations(23) exact set");
        Check.eq(0, Problems.letterCombinations("").size(), "LC17 letterCombinations(empty) = []");

        // LC 79
        char[][] board1 = {
            {'A','B','C','E'},
            {'S','F','C','S'},
            {'A','D','E','E'}
        };
        Check.isTrue(Problems.exist(board1, "ABCCED"), "LC79 exist(ABCCED) -> true");
        Check.isTrue(Problems.exist(board1, "SEE"), "LC79 exist(SEE) -> true");
        Check.isTrue(!Problems.exist(board1, "ABCB"), "LC79 exist(ABCB) reuse-cell -> false");

        // LC 47
        List<List<Integer>> perms = Problems.permuteUnique(new int[]{1,1,2});
        Check.eq(3, perms.size(), "LC47 permuteUnique([1,1,2]) unique count = 3");
        Set<List<Integer>> permSet = new HashSet<>(perms);
        Check.isTrue(permSet.contains(List.of(1,1,2)) && permSet.contains(List.of(1,2,1)) && permSet.contains(List.of(2,1,1)),
            "LC47 permuteUnique([1,1,2]) exact set");

        // LC 40
        List<List<Integer>> combSum2 = Problems.combinationSum2(new int[]{10,1,2,7,6,1,5}, 8);
        Check.eq(4, combSum2.size(), "LC40 combinationSum2 target 8 -> 4 combinations");
        Set<List<Integer>> combSum2Set = new HashSet<>(combSum2);
        Check.isTrue(combSum2Set.contains(List.of(1,1,6)) && combSum2Set.contains(List.of(1,2,5))
            && combSum2Set.contains(List.of(1,7)) && combSum2Set.contains(List.of(2,6)),
            "LC40 combinationSum2 exact set, no duplicate [1,7] twice");

        // LC 51
        List<List<String>> solutions4 = Problems.solveNQueens(4);
        Check.eq(2, solutions4.size(), "LC51 solveNQueens(4) solution count = 2");
        List<List<String>> solutions8 = Problems.solveNQueens(8);
        Check.eq(92, solutions8.size(), "LC51 solveNQueens(8) solution count = 92 (well-known result)");

        Check.summary("Week 21 — Backtracking (LC 17, 79, 47, 40, 51)");
    }
}
