---
title: "Intervals, Merging, and Sweep Line"
slug: intervals-merging-and-sweep-line
document_type: syllabus-topic
domain: 03-data-structures-algorithms
topic_id: T-2111
status: draft
version: 1.0
last_updated: 2026-09-03
mastery_levels_covered: [L1, L2, L3, L4]
prerequisites:
  - ../01-computer-science-foundations/number-representation.md
  - heaps-top-k-and-k-way-merge.md
related:
  - heaps-top-k-and-k-way-merge.md
  - ../01-computer-science-foundations/number-representation.md
practice: ../../practice/java/week-20/intervals/
production_scenarios: []
interview_paths: [interview-emergency-sprint, senior-to-staff]
official_references: []
source_history:
  - study-packs/week-20/03-intervals-coding-practice.md
---

# Intervals, Merging, and Sweep Line

> **Provenance.** The four worked problems and retrospectives in Sections 7 and 15 are elevated from `study-packs/week-20/03-intervals-coding-practice.md` — real, compiled, executed code (`practice/java/week-20/intervals/`), re-verified on OpenJDK 21.0.12 while writing this chapter (9/9 assertions passing).

This is Master Topic Register **T-1412** (IWI 5.0, high frequency). An interval `[start, end]` problem's difficulty is almost always in choosing the correct sort key (by start, or by end) and handling the boundary comparison correctly — [Heaps](heaps-top-k-and-k-way-merge.md) supplies the data structure for the "track currently active intervals" variant.

## 1. Why This Matters

Interval problems model an enormous range of real scheduling and resource-allocation questions — meeting rooms, calendar conflicts, merging overlapping ranges — and the core techniques (sort by the right key, then a single linear scan) are simple once identified but genuinely easy to get subtly wrong: sorting by the wrong field, or getting a boundary comparison's `<` vs. `<=` backwards, produces a plausible-looking but incorrect answer.

## 2. Prerequisites

[Number Representation](../01-computer-science-foundations/number-representation.md) — this chapter's Section 8 and 9 cover a real, documented integer-overflow bug in an interval comparator, a direct, concrete application of that topic's silent-overflow lesson. [Heaps, Top-K, and K-Way Merge](heaps-top-k-and-k-way-merge.md) — Meeting Rooms II (Section 4) uses a heap directly.

## 3. Foundation (L1)

**An interval is a range `[start, end]`, and most interval problems reduce to one core operation: determine whether two intervals overlap, and if so, how.** Two intervals `[a, b]` and `[c, d]` overlap exactly when `a <= d && c <= b` — both must start before the other ends.

**Sorting is almost always the first step**, and *which* field to sort by — start or end — depends on the specific question being asked, not on habit. Sorting by the wrong field is one of the most common interval-problem mistakes (Section 8).

## 4. Core Concepts (L2)

**Merging pre-sorted intervals** (Insert Interval, Section 7 Problem 1) is a single linear scan when the input is already sorted — no need to re-sort the whole collection just to insert one new interval, in contrast to the more general Merge Intervals problem which must sort first.

**Tracking currently-active intervals** (Meeting Rooms II, Section 7 Problem 2) uses a min-heap of end times: the heap always exposes the earliest-ending currently-occupied resource, which is always the best (and only necessary) one to check for reuse when a new interval starts.

**Greedy selection sorted by end, not start** (Minimum Number of Arrows, Section 7 Problem 3) is the correct approach whenever the goal is to select the fewest "covering points" for a set of intervals — sorting by end and always placing the next point at the earliest remaining end guarantees no interval is missed that could have been covered.

**Two-pointer merge across two independently-sorted interval lists** (Interval List Intersections, Section 7 Problem 4) is the interval-specific application of the same two-pointer-merge shape used for merging two sorted arrays.

## 5. How It Works Internally (L3)

**Why Minimum Number of Arrows sorts by end, precisely**: shooting an arrow at the earliest end among all remaining, unpopped balloons guarantees that arrow pops every balloon whose range includes that point — and critically, it pops the balloon that's *about to close its window* first, since any balloon ending even earlier would already have been handled. Shooting anywhere later than the earliest end risks missing that specific balloon entirely, since its window may close before a later shot. Sorting by *start* instead — a common, plausible-looking mistake — produces a wrong, usually too-high arrow count, since it no longer guarantees the greedy choice is covering the most urgently-expiring balloon first.

**Meeting Rooms II's heap-reuse correctness**: the heap's minimum (earliest end time among all currently-occupied rooms) is checked against the new meeting's start time. If that earliest end time is already ≤ the new start, that specific room is free and can be reused — and it's *always* correct to check only that one room, never any other occupied room, because if the earliest-ending room isn't yet free, no other occupied room (all ending later) could be free either.

## 6. Practical Usage

- **Ask "am I selecting covering points (sort by end) or merging/tracking active ranges (sort by start)" before choosing a sort key** — the single fastest way to avoid the most common interval-problem mistake.
- **Use `Comparator.comparingLong` (or an explicit `long` cast) for any interval-boundary comparator**, never raw subtraction (`a[1] - b[1]`) — Section 8/9 documents a real, reproduced overflow bug from exactly this shortcut.
- **Reach for a min-heap of end times specifically for "how many resources are needed simultaneously" questions** (Meeting Rooms II) — a different question from "can these all fit without overlap at all," which a simple sort-and-scan answers.

## 7. Examples

**Problem 1 — LC 57, Insert Interval.**

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

**Retrospective:** since the input is already sorted, a single linear scan suffices. **Complexity:** O(n) time, O(n) space.

**Problem 2 — LC 253, Meeting Rooms II.**

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

**Retrospective:** see Section 5's heap-reuse argument. **Complexity:** O(n log n).

**Problem 3 — LC 452, Minimum Number of Arrows to Burst Balloons.**

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

**Retrospective:** see Section 5's sort-by-end argument. **Complexity:** O(n log n).

**Problem 4 — LC 986, Interval List Intersections.**

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

**Retrospective:** the interval ending earlier can never intersect anything further along in the other list either, so it's always safe to advance its pointer. **Complexity:** O(n + m).

## 8. Common Mistakes

- **Sorting by start when the problem actually needs sorting by end (or vice versa)** — Minimum Number of Arrows (Section 5) is specifically designed to catch this.
- **Comparing interval endpoints with raw subtraction** (`(a, b) -> a[1] - b[1]`) instead of `Comparator.comparingLong` or an explicit widening cast — a genuine, reproduced bug documented in this chapter's own source material (Section 9).
- **Using `<` instead of `<=` (or vice versa) in an overlap check** — whether touching-but-not-overlapping intervals (`[1,2]` and `[2,3]`) count as overlapping is a real, problem-specific detail that changes which comparison operator is correct.

## 9. Edge Cases

- **An integer-overflow bug in an interval comparator, real and reproduced**: the source study-pack's original comparator used `(a, b) -> a[1] - b[1]`, which silently overflows when endpoint values are near `Integer.MIN_VALUE`/`MAX_VALUE` — `Integer.MIN_VALUE - Integer.MAX_VALUE` evaluates to `1`, not a large negative number (a direct instance of [Number Representation's](../01-computer-science-foundations/number-representation.md#4-core-concepts-l2) silent-overflow lesson). Sorting `{{0, MIN_VALUE}, {0, MAX_VALUE}, {0, 0}}` with the naive comparator produced `[[0, 0], [0, MAX_VALUE], [0, MIN_VALUE]]` — genuinely wrong order. `Comparator.comparingLong(a -> (long) a[1])` widens to `long` before comparing and never overflows, correctly producing `[[0, MIN_VALUE], [0, 0], [0, MAX_VALUE]]`. Both orderings were captured live in the same real test run, side by side.
- **No intervals at all** (Meeting Rooms II and Minimum Number of Arrows both guard against an empty input, correctly returning `0`).
- **Fully non-overlapping intervals** (Meeting Rooms II's verified case, correctly returning `1` room needed).

## 10. Performance Implications

Real, executed verification from `practice/java/week-20/intervals/` (OpenJDK 21.0.12), re-run while writing this chapter:

```
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

The overflow-comparator errata drill is a real, load-bearing part of this chapter's performance and correctness evidence — it's not a hypothetical caution, it's a bug that genuinely existed in this repository's own source material, was caught, and is now reproduced live as its own passing test, contrasting the exact wrong output against the exact correct one side by side.

## 11. Trade-offs

| Choice | Gains | Costs |
|---|---|---|
| Sort by start | Correct for merging overlapping ranges, insertion into sorted intervals | Wrong for "minimum covering points" problems |
| Sort by end | Correct for greedy covering-point selection | Wrong for straightforward overlap-merging |
| Heap of end times | Answers "how many resources needed simultaneously" | O(n log n), more complex than a simple sort-and-scan for problems that don't need this |
| `Comparator.comparingLong` / widening cast | Never overflows regardless of endpoint magnitude | Marginally more verbose than raw subtraction |

## 12. Senior-Level Considerations (L3)

The Senior-level skill is choosing the sort key from the problem's actual question, not from habit or the most recently practiced interval problem — explicitly asking "if I sort by start, does my greedy/scan logic still hold" versus "if I sort by end" before committing, rather than defaulting to sort-by-start (the more common convention) out of familiarity alone.

## 13. Staff/System-Level Considerations (L4)

At Staff scope, the overflow-comparator bug documented in this chapter's own source material (Section 9) is a real, general warning about comparator correctness in production code: any comparator using raw subtraction on values that could plausibly reach the extremes of their type's range is a latent, silent-failure-mode bug waiting for the right (or wrong) input — and this exact bug class (`Comparator` implementations using subtraction) is common enough in real Java codebases that it's worth a standing code-review heuristic, not just a one-off fix. Meeting Rooms II's heap-based resource-tracking technique also transfers directly to real capacity-planning questions: "how many concurrent database connections/worker threads/API rate-limit slots are needed at peak" is structurally the same "track currently-active intervals, minimize simultaneous count" question.

## 14. Production Scenarios

No existing `production-cookbook/` entry documents this exact comparator-overflow bug as a production incident, though it is a real, reproduced bug in this repository's own source material (Section 9), documented as an "Errata" correction rather than a fictionalized incident.

> Planned reference: a future `production-cookbook/` entry generalizing this chapter's comparator-overflow bug into a real production scenario (e.g., a sorting-based scheduling feature that silently misordered entries under specific large-value inputs) would be a natural, non-duplicative addition.

## 15. Interview Questions

### Question 1 — Given a list of meeting intervals, find the minimum number of conference rooms required.

**Why interviewers ask it.** It's the canonical heap-of-end-times test, checking whether a candidate recognizes "track how many are simultaneously active" as a distinct question from "can these all fit without overlap."

**Expected answer.** Sort meetings by start time. Maintain a min-heap of end times for currently-occupied rooms. For each meeting, if the heap's minimum end time is ≤ the meeting's start time, that room is free — pop it and push the new meeting's end time (reuse); otherwise, push the new end time without popping (a new room is needed). The final heap size is the answer.

**Minimum acceptable answer.** Produces a correct solution, even a less efficient one (e.g., an O(n²) approach checking every pair of intervals for overlap).

**Strong Senior answer.** Produces the heap-based O(n log n) solution directly, and can explain why checking only the heap's minimum (not every occupied room) is always sufficient (Section 5).

**Staff-level extension.** Connects this to a real capacity-planning application (Section 13) — the identical technique for determining minimum concurrent resource capacity from a stream of resource-usage intervals.

**Common mistakes.** Sorting by end time instead of start time for this specific problem — unlike Minimum Number of Arrows, Meeting Rooms II needs start-time ordering to process meetings in the order they actually begin.

**Follow-up questions.** "What if you needed to know which specific room each meeting was assigned to, not just the count?" (Requires tracking a room identifier alongside each end time in the heap, a real, slightly more involved extension.)

### Question 2 — Why does Minimum Number of Arrows to Burst Balloons sort by end coordinate rather than start coordinate?

**Why interviewers ask it.** It's a direct test of the sort-key-selection reasoning (Section 5), which is exactly backwards from the more commonly-practiced "sort by start" convention most interval problems use.

**Expected answer.** Greedily shooting at the earliest end coordinate among remaining balloons guarantees that shot pops every balloon whose range includes that point, and specifically pops the balloon that's about to "close its window" first — any balloon ending even earlier would already have been handled by an earlier shot. Sorting by start instead loses this guarantee, since a balloon's urgency (how soon it must be popped) is determined by when its range *ends*, not when it begins.

**Minimum acceptable answer.** States that sorting by end is correct here, even without a full justification for why.

**Strong Senior answer.** Explains the urgency argument precisely, and can contrast it directly against Meeting Rooms II's start-time sort, correctly identifying why the two problems need opposite sort keys despite both being "interval" problems.

**Staff-level extension.** Generalizes the principle: the correct sort key in an interval-scheduling problem is always determined by which endpoint determines "urgency" or "availability" for the specific decision being greedily made — a transferable diagnostic question for any new interval problem, not a memorized rule tied to either problem specifically.

**Common mistakes.** Assuming interval problems always sort by start, since that's the more commonly seen convention, and applying it here without re-deriving whether it's actually correct for this specific question.

**Follow-up questions.** "What if multiple balloons could be popped by the same arrow at the boundary — does `>` vs `>=` matter?" (Yes — whether touching-but-not-overlapping ranges count as poppable by the same arrow changes the boundary comparison, a real, problem-specific detail worth checking against the problem's exact stated constraints.)

## 16. Coding/Practice Exercises

- Run the [existing practice code](../../practice/java/week-20/intervals/) yourself and confirm the same 9/9 assertions pass, including both errata-drill assertions.
- Attempt LC 56 (Merge Intervals) from scratch — the more general version of this chapter's Insert Interval (Section 7, Problem 1), which must sort the entire input first since it isn't given pre-sorted.
- Reproduce the overflow-comparator bug (Section 9) yourself: write both the naive `(a, b) -> a[1] - b[1]` comparator and the `Comparator.comparingLong` version, sort the same adversarial input with both, and confirm the two produce genuinely different results.

## 17. Debugging Exercises

**Symptom:** a scheduling feature's sort-based conflict-resolution logic behaves correctly for almost all real data but occasionally produces a bizarre, clearly-wrong ordering for a small number of specific records.

**Diagnose:** check whether the sort comparator uses raw subtraction on the sorted field, and whether that field's real-world values could plausibly approach the extremes of its integer type's range (a very large or very small timestamp, ID, or duration value) — Section 9's exact, reproduced bug. Confirm by testing the comparator directly against a deliberately constructed adversarial input pair whose difference would overflow, and checking whether the sort order matches what `Comparator.comparingLong` (or an equivalent widening approach) would produce instead.

## 18. Design Exercises

**Design constraint:** design a resource-booking system (meeting rooms, rental equipment, or similar) that must report the minimum number of physical resources needed to satisfy all bookings without conflict, and must also support inserting a new booking into an already-processed, sorted booking list efficiently.

Design the two operations using this chapter's two core techniques directly: minimum-resource-count via the heap-of-end-times technique (Section 4/7, Problem 2), and single-booking insertion via the three-phase linear scan (Section 4/7, Problem 1) rather than re-sorting the entire booking list on every insert. State explicitly why the insertion technique's O(n) cost (versus an O(n log n) full re-sort) matters at scale for a system receiving many individual booking insertions over time, and name the comparator-overflow risk (Section 9/13) as a specific, real code-review item to check for in this design's own sort/comparison logic, given that resource IDs or timestamps in a real system could plausibly reach large values over the system's lifetime.

## 19. Further Reading

- [Heaps, Top-K, and K-Way Merge](heaps-top-k-and-k-way-merge.md) — the heap mechanics Meeting Rooms II is built directly on top of.
- [Number Representation](../01-computer-science-foundations/number-representation.md) — the silent-integer-overflow mechanism behind this chapter's own documented comparator bug (Section 9).

## 20. Mastery Checklist

| Level | You can... | Verify with |
|---|---|---|
| L1 | Explain, in plain language, when two intervals overlap, and why sort order matters for interval problems | [Section 3](#3-foundation-l1) |
| L2 | Choose the correct sort key (start or end) for a new interval problem based on the specific question being asked | [Interview Question 2](#question-2--why-does-minimum-number-of-arrows-to-burst-balloons-sort-by-end-coordinate-rather-than-start-coordinate) |
| L3 | Derive the heap-reuse correctness argument for Meeting Rooms II, and explain precisely why an interval comparator using raw subtraction can silently produce a wrong sort order | [Section 10's real verification](#10-performance-implications), [Section 5](#5-how-it-works-internally-l3) |
| L4 | Diagnose a real production sort-ordering bug as a comparator-overflow issue (Section 17), and design a real resource-booking system using both this chapter's core techniques deliberately (Section 18) | [Debugging Exercise](#17-debugging-exercises), [Section 13](#13-staffsystem-level-considerations-l4) |
