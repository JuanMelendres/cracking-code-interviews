import java.util.Arrays;
import java.util.Comparator;

public class Main {
    static String show(int[][] a) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < a.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(Arrays.toString(a[i]));
        }
        return sb.append("]").toString();
    }

    public static void main(String[] args) {
        // LC 57
        int[][] r1 = Problems.insert(new int[][]{{1,3},{6,9}}, new int[]{2,5});
        Check.eq("[[1, 5], [6, 9]]", show(r1), "LC57 insert([[1,3],[6,9]], [2,5])");

        int[][] r2 = Problems.insert(new int[][]{{1,2},{3,5},{6,7},{8,10},{12,16}}, new int[]{4,8});
        Check.eq("[[1, 2], [3, 10], [12, 16]]", show(r2), "LC57 insert merging three overlapping intervals");

        // LC 253
        Check.eq(2, Problems.minMeetingRooms(new int[][]{{0,30},{5,10},{15,20}}), "LC253 minMeetingRooms 3 meetings -> 2 rooms");
        Check.eq(1, Problems.minMeetingRooms(new int[][]{{7,10},{2,4}}), "LC253 minMeetingRooms non-overlapping -> 1 room");

        // LC 452
        Check.eq(2, Problems.findMinArrowShots(new int[][]{{10,16},{2,8},{1,6},{7,12}}), "LC452 findMinArrowShots(4 balloons) = 2 arrows");
        Check.eq(4, Problems.findMinArrowShots(new int[][]{{1,2},{3,4},{5,6},{7,8}}), "LC452 findMinArrowShots(no overlaps) = 4 arrows");

        // Errata drill (Phase 1 audit, item #4): a[1]-b[1] comparator overflow, reproduced directly.
        int[][] extremes = {{0, Integer.MIN_VALUE}, {0, Integer.MAX_VALUE}, {0, 0}};
        int[][] naiveSorted = extremes.clone();
        Arrays.sort(naiveSorted, (a, b) -> a[1] - b[1]);
        int[][] safeSorted = extremes.clone();
        Arrays.sort(safeSorted, Comparator.comparingLong(a -> (long) a[1]));
        System.out.println("Errata drill -- naive (a[1]-b[1]) sort: " + show(naiveSorted));
        System.out.println("Errata drill -- safe (comparingLong) sort: " + show(safeSorted));
        Check.eq("[[0, -2147483648], [0, 0], [0, 2147483647]]", show(safeSorted),
                "comparingLong correctly sorts MIN_VALUE < 0 < MAX_VALUE");
        Check.isTrue(!show(naiveSorted).equals(show(safeSorted)),
                "a[1]-b[1] overflow genuinely produces a DIFFERENT (wrong) order than comparingLong, reproduced live");

        // LC 986
        int[][] r3 = Problems.intervalIntersection(
                new int[][]{{0,2},{5,10},{13,23},{24,25}},
                new int[][]{{1,5},{8,12},{15,24},{25,26}});
        Check.eq("[[1, 2], [5, 5], [8, 10], [15, 23], [24, 24], [25, 25]]", show(r3),
                "LC986 intervalIntersection(4 vs 4 intervals)");

        Check.summary("Week 20 — Intervals (LC 57, 253, 452, 986)");
    }
}
