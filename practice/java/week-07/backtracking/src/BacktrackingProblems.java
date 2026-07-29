import java.util.*;

final class BacktrackingProblems {

    // LC 46 -- Permutations, FIXED. Index-based `used[]` array distinguishes
    // two array slots holding the same value, unlike the buggy value-based
    // `temp.contains()` check.
    static List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(nums, new ArrayList<>(), new boolean[nums.length], result);
        return result;
    }

    private static void backtrack(int[] nums, List<Integer> temp, boolean[] used, List<List<Integer>> result) {
        if (temp.size() == nums.length) {
            result.add(new ArrayList<>(temp));
            return;
        }
        for (int i = 0; i < nums.length; i++) {
            if (used[i]) continue; // INDEX-based -- correct even with duplicate values
            used[i] = true;
            temp.add(nums[i]);
            backtrack(nums, temp, used, result);
            temp.remove(temp.size() - 1);
            used[i] = false;
        }
    }

    // LC 78 -- Subsets. At each index, branch into "exclude" and "include".
    static List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        subsetsBacktrack(nums, 0, new ArrayList<>(), result);
        return result;
    }

    private static void subsetsBacktrack(int[] nums, int start, List<Integer> temp, List<List<Integer>> result) {
        result.add(new ArrayList<>(temp)); // every partial state IS a valid subset
        for (int i = start; i < nums.length; i++) {
            temp.add(nums[i]);
            subsetsBacktrack(nums, i + 1, temp, result);
            temp.remove(temp.size() - 1);
        }
    }

    // LC 39 -- Combination Sum. Same element can be reused, so the next
    // recursive call starts at `i`, not `i + 1`.
    static List<List<Integer>> combinationSum(int[] candidates, int target) {
        List<List<Integer>> result = new ArrayList<>();
        combSumBacktrack(candidates, target, 0, new ArrayList<>(), result);
        return result;
    }

    private static void combSumBacktrack(int[] candidates, int remaining, int start,
                                          List<Integer> temp, List<List<Integer>> result) {
        if (remaining == 0) {
            result.add(new ArrayList<>(temp));
            return;
        }
        if (remaining < 0) return;
        for (int i = start; i < candidates.length; i++) {
            temp.add(candidates[i]);
            combSumBacktrack(candidates, remaining - candidates[i], i, temp, result); // i, not i+1: reuse allowed
            temp.remove(temp.size() - 1);
        }
    }

    // LC 22 -- Generate Parentheses. Track open/close counts directly instead
    // of validating a completed string -- prunes invalid branches immediately.
    static List<String> generateParenthesis(int n) {
        List<String> result = new ArrayList<>();
        genParenBacktrack(n, 0, 0, new StringBuilder(), result);
        return result;
    }

    private static void genParenBacktrack(int n, int open, int close, StringBuilder sb, List<String> result) {
        if (sb.length() == 2 * n) {
            result.add(sb.toString());
            return;
        }
        if (open < n) {
            sb.append('(');
            genParenBacktrack(n, open + 1, close, sb, result);
            sb.deleteCharAt(sb.length() - 1);
        }
        if (close < open) {
            sb.append(')');
            genParenBacktrack(n, open, close + 1, sb, result);
            sb.deleteCharAt(sb.length() - 1);
        }
    }
}
