import java.util.*;

/**
 * ⛔ ERRATA — reproduces the Phase 1 audit's finding: the source Notion
 * guide's `permute` used `temp.contains(num)` to decide whether a value is
 * already placed. This is VALUE-based, not INDEX-based, so it cannot tell
 * two array slots holding the same value apart -- with any duplicate value
 * in the input, it silently under-counts the true number of permutations.
 */
final class PermuteBuggy {
    static List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(nums, new ArrayList<>(), result);
        return result;
    }

    private static void backtrack(int[] nums, List<Integer> temp, List<List<Integer>> result) {
        if (temp.size() == nums.length) {
            result.add(new ArrayList<>(temp));
            return;
        }
        for (int num : nums) {
            if (temp.contains(num)) continue; // BUG: value-based "used" check
            temp.add(num);
            backtrack(nums, temp, result);
            temp.remove(temp.size() - 1);
        }
    }
}
