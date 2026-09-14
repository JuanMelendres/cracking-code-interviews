import java.util.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("== Errata drill: LC 46 with a DUPLICATE-VALUE input [1,1,2] ==");
        int[] dupInput = {1, 1, 2};
        List<List<Integer>> buggyResult = PermuteBuggy.permute(dupInput);
        List<List<Integer>> fixedResult = BacktrackingProblems.permute(dupInput);
        System.out.println("Buggy (value-based temp.contains()):  " + buggyResult.size() + " permutations found: " + buggyResult);
        System.out.println("Fixed (index-based used[] array):     " + fixedResult.size() + " permutations found");
        Check.eq(6, fixedResult.size(), "fixed permute produces all 3! = 6 position-based permutations");
        Check.isTrue(buggyResult.size() < 6, "buggy permute UNDER-COUNTS on duplicate-value input (this IS the bug, reproduced on purpose)");

        System.out.println("\n== LC 46 on a normal, no-duplicates input ==");
        List<List<Integer>> normal = BacktrackingProblems.permute(new int[]{1, 2, 3});
        Check.eq(6, normal.size(), "permute([1,2,3]) produces 6 permutations");

        System.out.println("\n== LC 78: Subsets ==");
        List<List<Integer>> subsets = BacktrackingProblems.subsets(new int[]{1, 2, 3});
        Check.eq(8, subsets.size(), "subsets([1,2,3]) produces 2^3 = 8 subsets, including the empty set");
        Check.isTrue(subsets.contains(new ArrayList<>()), "subsets includes the empty subset");
        Check.isTrue(subsets.contains(Arrays.asList(1, 2, 3)), "subsets includes the full set");

        System.out.println("\n== LC 39: Combination Sum ==");
        List<List<Integer>> combos = BacktrackingProblems.combinationSum(new int[]{2, 3, 6, 7}, 7);
        Check.eq(2, combos.size(), "combinationSum([2,3,6,7], 7) finds exactly 2 combinations");
        Check.isTrue(combos.contains(Arrays.asList(7)), "combinationSum finds [7]");
        Check.isTrue(combos.contains(Arrays.asList(2, 2, 3)), "combinationSum finds [2,2,3] (element reused)");

        System.out.println("\n== LC 22: Generate Parentheses ==");
        List<String> parens = BacktrackingProblems.generateParenthesis(3);
        Check.eq(5, parens.size(), "generateParenthesis(3) produces the Catalan number C(3) = 5 combinations");
        Check.isTrue(parens.contains("((()))"), "generateParenthesis includes ((()))");
        Check.isTrue(parens.contains("()()()"), "generateParenthesis includes ()()()");

        Check.summary("Week 7 backtracking suite");
        if (Check.fail > 0) System.exit(1);
    }
}
