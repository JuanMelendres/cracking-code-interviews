import java.util.*;

final class Problems {

    // ---- LC 11: Container With Most Water ----
    static int maxArea(int[] height) {
        int lo = 0, hi = height.length - 1, best = 0;
        while (lo < hi) {
            int width = hi - lo;
            int area = width * Math.min(height[lo], height[hi]);
            best = Math.max(best, area);
            if (height[lo] < height[hi]) lo++; else hi--;
        }
        return best;
    }

    // ---- LC 239: Sliding Window Maximum ----
    static int[] maxSlidingWindow(int[] nums, int k) {
        int n = nums.length;
        int[] result = new int[n - k + 1];
        Deque<Integer> deque = new ArrayDeque<>(); // indices, decreasing value order
        for (int i = 0; i < n; i++) {
            while (!deque.isEmpty() && deque.peekFirst() <= i - k) deque.pollFirst();
            while (!deque.isEmpty() && nums[deque.peekLast()] < nums[i]) deque.pollLast();
            deque.offerLast(i);
            if (i >= k - 1) result[i - k + 1] = nums[deque.peekFirst()];
        }
        return result;
    }

    // ---- LC 238: Product of Array Except Self ----
    static int[] productExceptSelf(int[] nums) {
        int n = nums.length;
        int[] result = new int[n];
        result[0] = 1;
        for (int i = 1; i < n; i++) {
            result[i] = result[i - 1] * nums[i - 1];
        }
        int suffix = 1;
        for (int i = n - 1; i >= 0; i--) {
            result[i] *= suffix;
            suffix *= nums[i];
        }
        return result;
    }

    // ---- LC 189: Rotate Array (right rotation by k, in place via triple reversal) ----
    static void rotate(int[] nums, int k) {
        int n = nums.length;
        k %= n;
        reverse(nums, 0, n - 1);
        reverse(nums, 0, k - 1);
        reverse(nums, k, n - 1);
    }

    private static void reverse(int[] nums, int lo, int hi) {
        while (lo < hi) {
            int tmp = nums[lo]; nums[lo] = nums[hi]; nums[hi] = tmp;
            lo++; hi--;
        }
    }

    // ---- LC 31: Next Permutation ----
    static void nextPermutation(int[] nums) {
        int n = nums.length;
        int i = n - 2;
        while (i >= 0 && nums[i] >= nums[i + 1]) i--;
        if (i >= 0) {
            int j = n - 1;
            while (nums[j] <= nums[i]) j--;
            int tmp = nums[i]; nums[i] = nums[j]; nums[j] = tmp;
        }
        reverse(nums, i + 1, n - 1);
    }
}
