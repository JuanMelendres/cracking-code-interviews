import java.util.*;

final class Problems {

    // ---- LC 187: Repeated DNA Sequences ----
    // A fixed-window (10-char) rolling hash over the 4-letter DNA alphabet: each
    // base packs into 2 bits, so the whole window fits in a 20-bit integer.
    // Sliding the window one character right is O(1) -- shift left 2, OR in the
    // new base, mask to 20 bits -- no per-window rehash of all 10 characters.
    static List<String> findRepeatedDnaSequences(String s) {
        List<String> result = new ArrayList<>();
        int n = s.length();
        if (n < 10) return result;

        int[] code = new int[26];
        code['A' - 'A'] = 0;
        code['C' - 'A'] = 1;
        code['G' - 'A'] = 2;
        code['T' - 'A'] = 3;

        int mask = (1 << 20) - 1; // 10 bases * 2 bits
        int hash = 0;
        Map<Integer, Integer> seen = new HashMap<>();
        for (int i = 0; i < n; i++) {
            hash = ((hash << 2) | code[s.charAt(i) - 'A']) & mask;
            if (i >= 9) {
                int count = seen.merge(hash, 1, Integer::sum);
                if (count == 2) result.add(s.substring(i - 9, i + 1)); // add exactly once, the moment it becomes a duplicate
            }
        }
        return result;
    }

    // ---- LC 1044: Longest Duplicate Substring ----
    // Binary search on the answer's length L (monotonic: if some length-L
    // substring repeats, every shorter length also has a repeat). For a fixed
    // L, a polynomial rolling hash (Rabin-Karp) slides across the string in
    // O(n), recomputing each window's hash in O(1) from the previous one;
    // candidates with matching hashes are confirmed with a real character
    // comparison to guard against hash collisions rather than trusting the hash
    // alone.
    static String longestDupSubstring(String s) {
        int n = s.length();
        int[] nums = new int[n];
        for (int i = 0; i < n; i++) nums[i] = s.charAt(i) - 'a';

        int lo = 1, hi = n - 1, bestStart = -1, bestLen = 0;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            int start = search(nums, mid);
            if (start != -1) {
                bestStart = start;
                bestLen = mid;
                lo = mid + 1;
            } else {
                hi = mid - 1;
            }
        }
        return bestStart == -1 ? "" : s.substring(bestStart, bestStart + bestLen);
    }

    private static final long MOD = 1_000_000_007L;
    private static final long BASE = 26L;

    // Returns the start index of a length-L substring that occurs more than
    // once, or -1 if none does.
    private static int search(int[] nums, int len) {
        int n = nums.length;
        if (len == 0) return 0;

        long h = 0;
        for (int i = 0; i < len; i++) h = (h * BASE + nums[i]) % MOD;

        long highOrder = 1;
        for (int i = 0; i < len - 1; i++) highOrder = (highOrder * BASE) % MOD;

        Map<Long, List<Integer>> seen = new HashMap<>();
        seen.computeIfAbsent(h, k -> new ArrayList<>()).add(0);

        for (int start = 1; start + len <= n; start++) {
            h = ((h - nums[start - 1] * highOrder % MOD + MOD) * BASE + nums[start + len - 1]) % MOD;
            List<Integer> candidates = seen.get(h);
            if (candidates != null) {
                for (int idx : candidates) {
                    if (matches(nums, idx, start, len)) return start;
                }
            }
            seen.computeIfAbsent(h, k -> new ArrayList<>()).add(start);
        }
        return -1;
    }

    private static boolean matches(int[] nums, int i, int j, int len) {
        for (int k = 0; k < len; k++) if (nums[i + k] != nums[j + k]) return false;
        return true;
    }
}
