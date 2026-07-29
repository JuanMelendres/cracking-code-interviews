import java.util.*;

final class Problems {

    // LC 1 — Two Sum. Hash map of value -> index, single pass.
    static int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> seen = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int need = target - nums[i];
            if (seen.containsKey(need)) return new int[]{seen.get(need), i};
            seen.put(nums[i], i);
        }
        throw new IllegalArgumentException("no solution");
    }

    // LC 167 — Two Sum II, sorted input. Two pointers, O(1) space.
    static int[] twoSumSorted(int[] numbers, int target) {
        int lo = 0, hi = numbers.length - 1;
        while (lo < hi) {
            int sum = numbers[lo] + numbers[hi];
            if (sum == target) return new int[]{lo + 1, hi + 1}; // 1-indexed per LC spec
            if (sum < target) lo++; else hi--;
        }
        throw new IllegalArgumentException("no solution");
    }

    // LC 121 — Best Time to Buy and Sell Stock. Track running min, best profit.
    static int maxProfit(int[] prices) {
        int minSoFar = Integer.MAX_VALUE, best = 0;
        for (int p : prices) {
            minSoFar = Math.min(minSoFar, p);
            best = Math.max(best, p - minSoFar);
        }
        return best;
    }

    // LC 242 — Valid Anagram. Fixed 26-slot counter, no allocation per char.
    static boolean isAnagram(String s, String t) {
        if (s.length() != t.length()) return false;
        int[] counts = new int[26];
        for (int i = 0; i < s.length(); i++) {
            counts[s.charAt(i) - 'a']++;
            counts[t.charAt(i) - 'a']--;
        }
        for (int c : counts) if (c != 0) return false;
        return true;
    }

    // LC 49 — Group Anagrams. Sorted-string key groups words sharing a letter multiset.
    static List<List<String>> groupAnagrams(String[] strs) {
        Map<String, List<String>> groups = new HashMap<>();
        for (String s : strs) {
            char[] chars = s.toCharArray();
            Arrays.sort(chars);
            groups.computeIfAbsent(new String(chars), k -> new ArrayList<>()).add(s);
        }
        return new ArrayList<>(groups.values());
    }

    // LC 3 — Longest Substring Without Repeating Characters. Sliding window + last-seen index map.
    static int lengthOfLongestSubstring(String s) {
        Map<Character, Integer> lastSeen = new HashMap<>();
        int start = 0, best = 0;
        for (int end = 0; end < s.length(); end++) {
            char c = s.charAt(end);
            if (lastSeen.containsKey(c) && lastSeen.get(c) >= start) {
                start = lastSeen.get(c) + 1;
            }
            lastSeen.put(c, end);
            best = Math.max(best, end - start + 1);
        }
        return best;
    }
}
