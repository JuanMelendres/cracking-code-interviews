final class BinarySearchProblems {

    // LC 704 — Binary Search. Classic template, exact boundary conditions.
    static int search(int[] nums, int target) {
        int lo = 0, hi = nums.length - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (nums[mid] == target) return mid;
            if (nums[mid] < target) lo = mid + 1; else hi = mid - 1;
        }
        return -1;
    }

    // LC 35 — Search Insert Position. Same template; on miss, lo is the insertion point.
    static int searchInsert(int[] nums, int target) {
        int lo = 0, hi = nums.length - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (nums[mid] == target) return mid;
            if (nums[mid] < target) lo = mid + 1; else hi = mid - 1;
        }
        return lo;
    }

    // LC 33 — Search in Rotated Sorted Array. At each step, one half is guaranteed sorted;
    // check which half is sorted, then check if target lies within that half's range.
    static int searchRotated(int[] nums, int target) {
        int lo = 0, hi = nums.length - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (nums[mid] == target) return mid;
            if (nums[lo] <= nums[mid]) { // left half sorted
                if (nums[lo] <= target && target < nums[mid]) hi = mid - 1;
                else lo = mid + 1;
            } else { // right half sorted
                if (nums[mid] < target && target <= nums[hi]) lo = mid + 1;
                else hi = mid - 1;
            }
        }
        return -1;
    }

    // LC 875 — Koko Eating Bananas. Binary search on the ANSWER SPACE (eating speed),
    // not on the input array. Feasibility check: can Koko finish at speed k within h hours?
    static int minEatingSpeed(int[] piles, int h) {
        int lo = 1, hi = 0;
        for (int p : piles) hi = Math.max(hi, p);
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (feasible(piles, h, mid)) hi = mid; else lo = mid + 1;
        }
        return lo;
    }

    private static boolean feasible(int[] piles, int h, int speed) {
        long hours = 0;
        for (int p : piles) hours += (p + speed - 1) / speed; // ceil division
        return hours <= h;
    }
}
