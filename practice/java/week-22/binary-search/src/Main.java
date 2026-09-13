import java.util.*;

final class Main {
    public static void main(String[] args) {
        // LC 34
        Check.isTrue(Arrays.equals(new int[]{3,4}, Problems.searchRange(new int[]{5,7,7,8,8,10}, 8)),
            "LC34 searchRange([5,7,7,8,8,10], 8) = [3,4]");
        Check.isTrue(Arrays.equals(new int[]{-1,-1}, Problems.searchRange(new int[]{5,7,7,8,8,10}, 6)),
            "LC34 searchRange([5,7,7,8,8,10], 6) = [-1,-1]");

        // LC 74
        int[][] matrix = {{1,3,5,7},{10,11,16,20},{23,30,34,60}};
        Check.isTrue(Problems.searchMatrix(matrix, 3), "LC74 searchMatrix(target=3) -> true");
        Check.isTrue(!Problems.searchMatrix(matrix, 13), "LC74 searchMatrix(target=13) -> false");

        // LC 153
        Check.eq(1, Problems.findMin(new int[]{3,4,5,1,2}), "LC153 findMin([3,4,5,1,2]) = 1");
        Check.eq(0, Problems.findMin(new int[]{4,5,6,7,0,1,2}), "LC153 findMin([4,5,6,7,0,1,2]) = 0");
        Check.eq(11, Problems.findMin(new int[]{11,13,15,17}), "LC153 findMin([11,13,15,17]) not rotated = 11");

        // LC 1011
        Check.eq(15, Problems.shipWithinDays(new int[]{1,2,3,4,5,6,7,8,9,10}, 5),
            "LC1011 shipWithinDays(1..10, 5 days) = 15");
        Check.eq(6, Problems.shipWithinDays(new int[]{3,2,2,4,1,4}, 3),
            "LC1011 shipWithinDays([3,2,2,4,1,4], 3 days) = 6");

        // LC 4
        Check.eq(2.0, Problems.findMedianSortedArrays(new int[]{1,3}, new int[]{2}),
            "LC4 findMedianSortedArrays([1,3],[2]) = 2.0");
        Check.eq(2.5, Problems.findMedianSortedArrays(new int[]{1,2}, new int[]{3,4}),
            "LC4 findMedianSortedArrays([1,2],[3,4]) = 2.5");
        Check.eq(0.0, Problems.findMedianSortedArrays(new int[]{0,0}, new int[]{0,0,3,5,7}),
            "LC4 findMedianSortedArrays(uneven lengths) = 0.0 (merged [0,0,0,0,3,5,7], middle of 7)");

        Check.summary("Week 22 — Binary Search (LC 34, 74, 153, 1011, 4)");
    }
}
