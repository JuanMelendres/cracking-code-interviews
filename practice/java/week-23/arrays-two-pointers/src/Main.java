import java.util.*;

final class Main {
    public static void main(String[] args) {
        // LC 11
        Check.eq(49, Problems.maxArea(new int[]{1,8,6,2,5,4,8,3,7}), "LC11 maxArea(9 heights) = 49");
        Check.eq(1, Problems.maxArea(new int[]{1,1}), "LC11 maxArea([1,1]) = 1");

        // LC 239
        Check.isTrue(Arrays.equals(new int[]{3,3,5,5,6,7},
            Problems.maxSlidingWindow(new int[]{1,3,-1,-3,5,3,6,7}, 3)),
            "LC239 maxSlidingWindow(k=3) = [3,3,5,5,6,7]");
        Check.isTrue(Arrays.equals(new int[]{1}, Problems.maxSlidingWindow(new int[]{1}, 1)),
            "LC239 maxSlidingWindow single element = [1]");

        // LC 238
        Check.isTrue(Arrays.equals(new int[]{24,12,8,6}, Problems.productExceptSelf(new int[]{1,2,3,4})),
            "LC238 productExceptSelf([1,2,3,4]) = [24,12,8,6]");
        Check.isTrue(Arrays.equals(new int[]{0,0,9,0,0}, Problems.productExceptSelf(new int[]{-1,1,0,-3,3})),
            "LC238 productExceptSelf(with zero) = [0,0,9,0,0]");

        // LC 189
        int[] arr1 = {1,2,3,4,5,6,7};
        Problems.rotate(arr1, 3);
        Check.isTrue(Arrays.equals(new int[]{5,6,7,1,2,3,4}, arr1), "LC189 rotate([1..7], k=3) = [5,6,7,1,2,3,4]");
        int[] arr2 = {-1,-100,3,99};
        Problems.rotate(arr2, 2);
        Check.isTrue(Arrays.equals(new int[]{3,99,-1,-100}, arr2), "LC189 rotate([-1,-100,3,99], k=2) = [3,99,-1,-100]");

        // LC 31
        int[] perm1 = {1,2,3};
        Problems.nextPermutation(perm1);
        Check.isTrue(Arrays.equals(new int[]{1,3,2}, perm1), "LC31 nextPermutation([1,2,3]) = [1,3,2]");
        int[] perm2 = {3,2,1};
        Problems.nextPermutation(perm2);
        Check.isTrue(Arrays.equals(new int[]{1,2,3}, perm2), "LC31 nextPermutation([3,2,1]) wraps to [1,2,3]");
        int[] perm3 = {1,1,5};
        Problems.nextPermutation(perm3);
        Check.isTrue(Arrays.equals(new int[]{1,5,1}, perm3), "LC31 nextPermutation([1,1,5]) = [1,5,1]");

        Check.summary("Week 23 — Arrays/Two-Pointers (LC 11, 239, 238, 189, 31)");
    }
}
