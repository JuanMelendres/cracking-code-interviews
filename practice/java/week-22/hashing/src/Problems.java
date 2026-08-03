import java.util.*;

final class Problems {

    // ---- LC 217: Contains Duplicate ----
    static boolean containsDuplicate(int[] nums) {
        Set<Integer> seen = new HashSet<>();
        for (int n : nums) {
            if (!seen.add(n)) return true;
        }
        return false;
    }

    // ---- LC 560: Subarray Sum Equals K ----
    static int subarraySum(int[] nums, int k) {
        Map<Integer, Integer> prefixCount = new HashMap<>();
        prefixCount.put(0, 1);
        int prefixSum = 0, count = 0;
        for (int num : nums) {
            prefixSum += num;
            count += prefixCount.getOrDefault(prefixSum - k, 0);
            prefixCount.merge(prefixSum, 1, Integer::sum);
        }
        return count;
    }

    // ---- LC 349: Intersection of Two Arrays ----
    static int[] intersection(int[] nums1, int[] nums2) {
        Set<Integer> set1 = new HashSet<>();
        for (int n : nums1) set1.add(n);
        Set<Integer> result = new HashSet<>();
        for (int n : nums2) {
            if (set1.contains(n)) result.add(n);
        }
        int[] arr = new int[result.size()];
        int i = 0;
        for (int n : result) arr[i++] = n;
        return arr;
    }

    // ---- LC 202: Happy Number ----
    static boolean isHappy(int n) {
        Set<Integer> seen = new HashSet<>();
        while (n != 1 && seen.add(n)) {
            n = sumOfSquaredDigits(n);
        }
        return n == 1;
    }

    private static int sumOfSquaredDigits(int n) {
        int sum = 0;
        while (n > 0) {
            int digit = n % 10;
            sum += digit * digit;
            n /= 10;
        }
        return sum;
    }

    // ---- LC 454: 4Sum II ----
    static int fourSumCount(int[] nums1, int[] nums2, int[] nums3, int[] nums4) {
        Map<Integer, Integer> sumCounts = new HashMap<>();
        for (int a : nums1) {
            for (int b : nums2) {
                sumCounts.merge(a + b, 1, Integer::sum);
            }
        }
        int count = 0;
        for (int c : nums3) {
            for (int d : nums4) {
                count += sumCounts.getOrDefault(-(c + d), 0);
            }
        }
        return count;
    }
}
