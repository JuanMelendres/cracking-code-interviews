import java.util.*;

final class Main {
    public static void main(String[] args) {
        // LC 307: Range Sum Query - Mutable
        Problems.NumArray numArray = new Problems.NumArray(new int[]{1, 3, 5});
        Check.eq(9, numArray.sumRange(0, 2), "LC307 sumRange(0,2) on [1,3,5] = 9");
        numArray.update(1, 2);
        Check.eq(8, numArray.sumRange(0, 2), "LC307 sumRange(0,2) after update(1,2) -> [1,2,5] = 8");
        Check.eq(7, numArray.sumRange(1, 2), "LC307 sumRange(1,2) on [1,2,5] = 7");
        numArray.update(0, 10);
        Check.eq(17, numArray.sumRange(0, 2), "LC307 sumRange(0,2) after update(0,10) -> [10,2,5] = 17");

        // LC 732: My Calendar III
        Problems.MyCalendarThree cal = new Problems.MyCalendarThree();
        Check.eq(1, cal.book(10, 20), "LC732 book(10,20) -> 1");
        Check.eq(1, cal.book(50, 60), "LC732 book(50,60) -> 1 (disjoint)");
        Check.eq(2, cal.book(10, 40), "LC732 book(10,40) -> 2 (overlaps first)");
        Check.eq(3, cal.book(5, 15), "LC732 book(5,15) -> 3 (triple overlap at [10,15))");
        Check.eq(3, cal.book(5, 10), "LC732 book(5,10) -> 3 (max k-booking unchanged, [5,10) only double)");
        Check.eq(3, cal.book(25, 55), "LC732 book(25,55) -> 3 (max k-booking still 3)");

        // LC 218: The Skyline Problem
        int[][] buildings1 = {{2, 9, 10}, {3, 7, 15}, {5, 12, 12}, {15, 20, 10}, {19, 24, 8}};
        List<List<Integer>> expected1 = List.of(
            List.of(2, 10), List.of(3, 15), List.of(7, 12), List.of(12, 0),
            List.of(15, 10), List.of(20, 8), List.of(24, 0)
        );
        Check.eq(expected1, Problems.getSkyline(buildings1), "LC218 getSkyline (5-building classic example)");

        int[][] buildings2 = {{0, 2, 3}, {2, 5, 3}};
        List<List<Integer>> expected2 = List.of(List.of(0, 3), List.of(5, 0));
        Check.eq(expected2, Problems.getSkyline(buildings2), "LC218 getSkyline (2 adjacent same-height buildings merge into one segment)");

        int[][] buildings3 = {{0, 5, 7}, {1, 6, 4}};
        List<List<Integer>> expected3 = List.of(List.of(0, 7), List.of(5, 4), List.of(6, 0));
        Check.eq(expected3, Problems.getSkyline(buildings3), "LC218 getSkyline (taller building masks the shorter one only where they overlap)");

        Check.summary("Advanced Structures — Segment Tree (LC 307, 732, 218)");
    }
}
