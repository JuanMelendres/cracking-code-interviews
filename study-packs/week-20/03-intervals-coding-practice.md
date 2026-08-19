---
title: "Coding Practice — Intervals (T-1412)"
week: 20
document_type: study-pack-coding-practice
status: draft
last_reviewed: 2026-08-03
---

# Coding Practice — Intervals (T-1412)

**4 problems. All code on this page was compiled and executed — see `MANIFEST.md` for the exact commands and real pass counts.** Brings this pattern's coverage from 1/8 to 5/8 register problems.

---

## Problem 1 — LC 57 Insert Interval

**Pattern:** three-phase linear scan over pre-sorted intervals — before, merge, after.

```java
static int[][] insert(int[][] intervals, int[] newInterval) {
    List<int[]> result = new ArrayList<>();
    int i = 0, n = intervals.length;
    while (i < n && intervals[i][1] < newInterval[0]) { result.add(intervals[i]); i++; }
    int mergedStart = newInterval[0], mergedEnd = newInterval[1];
    while (i < n && intervals[i][0] <= mergedEnd) {
        mergedStart = Math.min(mergedStart, intervals[i][0]);
        mergedEnd = Math.max(mergedEnd, intervals[i][1]);
        i++;
    }
    result.add(new int[]{mergedStart, mergedEnd});
    while (i < n) { result.add(intervals[i]); i++; }
    return result.toArray(new int[0][]);
}
```

**Retrospective:** because the input is already sorted (unlike LC 56 Merge Intervals, which must sort first), a single linear scan suffices — no need to re-sort the whole array just to insert one interval. **Complexity:** O(n) time, O(n) space for the result.

## Problem 2 — LC 253 Meeting Rooms II

**Pattern:** min-heap of active end times — reuse a room the instant it frees up, else allocate a new one.

```java
static int minMeetingRooms(int[][] intervals) {
    if (intervals.length == 0) return 0;
    Arrays.sort(intervals, Comparator.comparingInt(a -> a[0]));
    PriorityQueue<Integer> endTimes = new PriorityQueue<>();
    for (int[] iv : intervals) {
        if (!endTimes.isEmpty() && endTimes.peek() <= iv[0]) endTimes.poll();
        endTimes.offer(iv[1]);
    }
    return endTimes.size();
}
```

**Retrospective:** the heap always exposes the *earliest*-ending currently-occupied room — if that room has already ended by the time the next meeting starts, it's always the best (and only necessary) one to check for reuse; no need to compare against every other occupied room. **Complexity:** O(n log n) time (sort + heap operations), O(n) space.

## Problem 3 — LC 452 Minimum Number of Arrows to Burst Balloons

**Pattern:** greedy on interval *end* points, not start points.

```java
static int findMinArrowShots(int[][] points) {
    if (points.length == 0) return 0;
    Arrays.sort(points, Comparator.comparingLong(a -> (long) a[1]));
    int arrows = 1;
    long arrowPos = points[0][1];
    for (int[] p : points) {
        if (p[0] > arrowPos) { arrows++; arrowPos = p[1]; }
    }
    return arrows;
}
```

**Retrospective:** sorting by end (not start) and always shooting at the earliest end guarantees the arrow pops every balloon it possibly can before moving on — shooting anywhere later than the earliest end risks missing a balloon that's about to close its window. A common bug: sorting by start instead of end produces a wrong, usually-too-high arrow count. **Complexity:** O(n log n) time, O(1) extra space beyond the sort.

**Errata (Phase 1 audit, item #4):** the source material's comparator used `(a, b) -> a[1] - b[1]` — a classic overflow bug. With end values near `Integer.MIN_VALUE`/`MAX_VALUE`, that raw subtraction wraps around silently. Real, executed proof: `Integer.MIN_VALUE - Integer.MAX_VALUE` evaluates to `1`, not a large negative number, and sorting `{{0, MIN_VALUE}, {0, MAX_VALUE}, {0, 0}}` with the naive comparator produces `[[0, 0], [0, MAX_VALUE], [0, MIN_VALUE]]` — MAX_VALUE sorted before MIN_VALUE, genuinely wrong. `Comparator.comparingLong(a -> (long) a[1])` (used above) widens to `long` before comparing and never overflows, correctly producing `[[0, MIN_VALUE], [0, 0], [0, MAX_VALUE]]`. Both orderings captured live in the real test run below.

## Problem 4 — LC 986 Interval List Intersections

**Pattern:** two-pointer merge across two independently-sorted interval lists.

```java
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
```

**Retrospective:** the interval ending earlier can never intersect anything further along in the *other* list either (both lists are sorted), so it's always safe to advance its pointer — this is the same two-pointer-merge shape as merging two sorted arrays, applied to interval overlap instead of value comparison. **Complexity:** O(n + m) time, no allocation beyond the result list.

## Verification

```
$ cd practice/java/week-20/intervals/src && javac -d ../out Check.java Problems.java Main.java && java -cp ../out Main
  PASS  LC57 insert([[1,3],[6,9]], [2,5])
  PASS  LC57 insert merging three overlapping intervals
  PASS  LC253 minMeetingRooms 3 meetings -> 2 rooms
  PASS  LC253 minMeetingRooms non-overlapping -> 1 room
  PASS  LC452 findMinArrowShots(4 balloons) = 2 arrows
  PASS  LC452 findMinArrowShots(no overlaps) = 4 arrows
Errata drill -- naive (a[1]-b[1]) sort: [[0, 0], [0, 2147483647], [0, -2147483648]]
Errata drill -- safe (comparingLong) sort: [[0, -2147483648], [0, 0], [0, 2147483647]]
  PASS  comparingLong correctly sorts MIN_VALUE < 0 < MAX_VALUE
  PASS  a[1]-b[1] overflow genuinely produces a DIFFERENT (wrong) order than comparingLong, reproduced live
  PASS  LC986 intervalIntersection(4 vs 4 intervals)
Week 20 — Intervals (LC 57, 253, 452, 986): 9/9 assertions passed
```
