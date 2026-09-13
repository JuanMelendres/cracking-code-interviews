import java.util.HashMap;
import java.util.Map;

public class Problems {

    // LC 45 — Jump Game II. Greedy BFS-layer expansion: track the farthest
    // reachable index within the current "jump layer," incrementing jumps
    // only when the current layer is exhausted. O(n) time, O(1) space.
    static int jump(int[] nums) {
        int jumps = 0, currentEnd = 0, farthest = 0;
        for (int i = 0; i < nums.length - 1; i++) {
            farthest = Math.max(farthest, i + nums[i]);
            if (i == currentEnd) {
                jumps++;
                currentEnd = farthest;
            }
        }
        return jumps;
    }

    // LC 134 — Gas Station. Greedy: if total gas >= total cost, a solution
    // exists; the start station is the index right after the point where
    // the running tank total drops most negative. O(n) time, O(1) space.
    static int canCompleteCircuit(int[] gas, int[] cost) {
        int totalTank = 0, currentTank = 0, startIndex = 0;
        for (int i = 0; i < gas.length; i++) {
            int diff = gas[i] - cost[i];
            totalTank += diff;
            currentTank += diff;
            if (currentTank < 0) {
                startIndex = i + 1;
                currentTank = 0;
            }
        }
        return totalTank >= 0 ? startIndex : -1;
    }

    // LC 621 — Task Scheduler. Greedy formula: the answer is bounded below
    // by (maxFreq - 1) * (n + 1) + (count of tasks tied at maxFreq), and
    // above by tasks.length (idle slots only help when the most-frequent
    // task can't fill every cooldown window). O(26 + n) time.
    static int leastInterval(char[] tasks, int n) {
        int[] freq = new int[26];
        for (char t : tasks) freq[t - 'A']++;
        int maxFreq = 0;
        for (int f : freq) maxFreq = Math.max(maxFreq, f);
        int maxCount = 0;
        for (int f : freq) if (f == maxFreq) maxCount++;
        int formula = (maxFreq - 1) * (n + 1) + maxCount;
        return Math.max(formula, tasks.length);
    }

    // LC 763 — Partition Labels. Greedy: for each character track its last
    // occurrence index; extend the current partition's end to cover every
    // character's last occurrence seen so far, cut when i reaches that end.
    // O(n) time, O(1) space (26-letter alphabet).
    static java.util.List<Integer> partitionLabels(String s) {
        int[] lastIndex = new int[26];
        for (int i = 0; i < s.length(); i++) lastIndex[s.charAt(i) - 'a'] = i;
        java.util.List<Integer> result = new java.util.ArrayList<>();
        int start = 0, end = 0;
        for (int i = 0; i < s.length(); i++) {
            end = Math.max(end, lastIndex[s.charAt(i) - 'a']);
            if (i == end) {
                result.add(end - start + 1);
                start = i + 1;
            }
        }
        return result;
    }

    // LC 402 — Remove K Digits. Greedy monotonic stack: pop any digit larger
    // than the current one (while removals remain) to keep the result's
    // digits as small as possible from left to right. O(n) time, O(n) space.
    static String removeKdigits(String num, int k) {
        StringBuilder stack = new StringBuilder();
        for (char c : num.toCharArray()) {
            while (k > 0 && stack.length() > 0 && stack.charAt(stack.length() - 1) > c) {
                stack.deleteCharAt(stack.length() - 1);
                k--;
            }
            stack.append(c);
        }
        while (k > 0 && stack.length() > 0) {
            stack.deleteCharAt(stack.length() - 1);
            k--;
        }
        int i = 0;
        while (i < stack.length() - 1 && stack.charAt(i) == '0') i++;
        String result = stack.substring(i);
        return result.isEmpty() ? "0" : result;
    }
}
