import java.util.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("== Week 1 problem set ==");

        Check.eq(0, Problems.twoSum(new int[]{2,7,11,15}, 9)[0], "twoSum index0");
        Check.eq(1, Problems.twoSum(new int[]{2,7,11,15}, 9)[1], "twoSum index1");
        Check.eq(1, Problems.twoSumSorted(new int[]{2,7,11,15}, 9)[0], "twoSumSorted 1-indexed lo");
        Check.eq(2, Problems.twoSumSorted(new int[]{2,7,11,15}, 9)[1], "twoSumSorted 1-indexed hi");

        Check.eq(5, Problems.maxProfit(new int[]{7,1,5,3,6,4}), "maxProfit typical case");
        Check.eq(0, Problems.maxProfit(new int[]{7,6,4,3,1}), "maxProfit monotonic decreasing -> 0");

        Check.isTrue(Problems.isAnagram("anagram", "nagaram"), "isAnagram true case");
        Check.isTrue(!Problems.isAnagram("rat", "car"), "isAnagram false case");

        List<List<String>> groups = Problems.groupAnagrams(new String[]{"eat","tea","tan","ate","nat","bat"});
        Check.eq(3, groups.size(), "groupAnagrams produces 3 groups");

        Check.eq(3, Problems.lengthOfLongestSubstring("abcabcbb"), "longestSubstring abcabcbb -> 3");
        Check.eq(1, Problems.lengthOfLongestSubstring("bbbbb"), "longestSubstring bbbbb -> 1");
        Check.eq(3, Problems.lengthOfLongestSubstring("pwwkew"), "longestSubstring pwwkew -> 3");

        System.out.println("\n== LC 146 LRU Cache — fixed implementation ==");
        LRUCacheFixed lru = new LRUCacheFixed(2);
        lru.put(1, 1);
        lru.put(2, 2);
        Check.eq(1, lru.get(1), "fixed: get(1) after put(1,1) put(2,2)");
        lru.put(3, 3); // evicts key 2, the true LRU
        Check.eq(-1, lru.get(2), "fixed: get(2) evicted by capacity, correct eviction target");
        Check.eq(3, lru.get(3), "fixed: get(3) present");

        System.out.println("\n== Errata drill — reproducing the buggy version's failure ==");
        LRUCacheBuggy buggy = new LRUCacheBuggy(2);
        buggy.put(1, 1);
        buggy.put(2, 2);
        buggy.put(1, 10); // update to an EXISTING key while at capacity — should NOT evict anything
        int buggyResult = buggy.get(2);
        Check.eq(-1, buggyResult, "buggy: get(2) incorrectly evicted (this IS the bug, reproduced on purpose)");

        System.out.println("\n== Fixed version does not reproduce the failure on the same sequence ==");
        LRUCacheFixed fixed2 = new LRUCacheFixed(2);
        fixed2.put(1, 1);
        fixed2.put(2, 2);
        fixed2.put(1, 10); // same sequence — update to existing key at capacity
        Check.eq(2, fixed2.get(2), "fixed: get(2) survives the update-at-capacity sequence");
        Check.eq(10, fixed2.get(1), "fixed: get(1) reflects the update");

        Check.summary("Week 1 suite");
        if (Check.fail > 0) System.exit(1);
    }
}
