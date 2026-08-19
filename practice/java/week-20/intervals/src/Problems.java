import java.util.*;

public class Problems {

    // LC 57 — Insert Interval. Intervals arrive pre-sorted; a single linear
    // scan splits into "before" (ends before new starts), "merge" (overlaps
    // the new interval, expanding its bounds), and "after." O(n) time.
    static int[][] insert(int[][] intervals, int[] newInterval) {
        List<int[]> result = new ArrayList<>();
        int i = 0, n = intervals.length;
        while (i < n && intervals[i][1] < newInterval[0]) {
            result.add(intervals[i]);
            i++;
        }
        int mergedStart = newInterval[0], mergedEnd = newInterval[1];
        while (i < n && intervals[i][0] <= mergedEnd) {
            mergedStart = Math.min(mergedStart, intervals[i][0]);
            mergedEnd = Math.max(mergedEnd, intervals[i][1]);
            i++;
        }
        result.add(new int[]{mergedStart, mergedEnd});
        while (i < n) {
            result.add(intervals[i]);
            i++;
        }
        return result.toArray(new int[0][]);
    }

    // LC 253 — Meeting Rooms II. Min-heap of active end times: for each
    // meeting (sorted by start), reuse the earliest-ending room if it's
    // already free, else allocate a new one. O(n log n) time.
    static int minMeetingRooms(int[][] intervals) {
        if (intervals.length == 0) return 0;
        Arrays.sort(intervals, Comparator.comparingInt(a -> a[0]));
        PriorityQueue<Integer> endTimes = new PriorityQueue<>();
        for (int[] iv : intervals) {
            if (!endTimes.isEmpty() && endTimes.peek() <= iv[0]) {
                endTimes.poll();
            }
            endTimes.offer(iv[1]);
        }
        return endTimes.size();
    }

    // LC 452 — Minimum Number of Arrows to Burst Balloons. Greedy on
    // interval END points: sort by end, shoot an arrow at the first
    // balloon's end; any balloon whose start is <= that arrow's position
    // is popped for free. O(n log n) time.
    //
    // ERRATA (Phase 1 audit, item #4): the source material's comparator used
    // `(a, b) -> a[1] - b[1]` -- a classic overflow bug. With end values near
    // Integer.MIN_VALUE/MAX_VALUE, that raw subtraction wraps around (e.g.
    // Integer.MIN_VALUE - Integer.MAX_VALUE overflows to 1, not a large
    // negative number), producing a genuinely wrong sort order. Fixed here
    // with `Comparator.comparingLong(a -> (long) a[1])`, which widens to long
    // before comparing, never overflows, and is reproduced with a real,
    // executed counter-example in Main.java's own errata drill.
    static int findMinArrowShots(int[][] points) {
        if (points.length == 0) return 0;
        Arrays.sort(points, Comparator.comparingLong(a -> (long) a[1]));
        int arrows = 1;
        long arrowPos = points[0][1];
        for (int[] p : points) {
            if (p[0] > arrowPos) {
                arrows++;
                arrowPos = p[1];
            }
        }
        return arrows;
    }

    // LC 986 — Interval List Intersections. Two-pointer merge: compute the
    // overlap of the current pair (max of starts, min of ends); if valid,
    // record it; advance whichever list's current interval ends first.
    // O(n + m) time.
    static int[][] intervalIntersection(int[][] first, int[][] second) {
        List<int[]> result = new ArrayList<>();
        int i = 0, j = 0;
        while (i < first.length && j < second.length) {
            int start = Math.max(first[i][0], second[j][0]);
            int end = Math.min(first[i][1], second[j][1]);
            if (start <= end) result.add(new int[]{start, end});
            if (first[i][1] < second[j][1]) i++; else j++;
        }
        return result.toArray(new int[0][]);
    }
}
