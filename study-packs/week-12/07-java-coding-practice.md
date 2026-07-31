---
title: "Java Coding Practice — Week 12 (Final Loop Set)"
week: 12
last_reviewed: 2026-07-31
---

# Java Coding Practice — Week 12 (Final Loop Set)

**8 problems, 2 per loop, spanning sliding window, graphs/topological sort, intervals, DP, hashing, heaps, greedy, and BFS — no problem number repeated from Weeks 1–11. All code compiled and executed — see the verification block and `MANIFEST.md`.**

## Table of Contents

1. [LC 3 — Longest Substring Without Repeating Characters](#lc-3--longest-substring-without-repeating-characters)
2. [LC 207 — Course Schedule](#lc-207--course-schedule)
3. [LC 56 — Merge Intervals](#lc-56--merge-intervals)
4. [LC 139 — Word Break](#lc-139--word-break)
5. [LC 128 — Longest Consecutive Sequence](#lc-128--longest-consecutive-sequence)
6. [LC 973 — K Closest Points to Origin](#lc-973--k-closest-points-to-origin)
7. [LC 55 — Jump Game](#lc-55--jump-game)
8. [LC 127 — Word Ladder](#lc-127--word-ladder)
9. [Verification](#verification--real-not-asserted)

---

## LC 3 — Longest Substring Without Repeating Characters

```java
static int lengthOfLongestSubstring(String s) {
    Map<Character, Integer> lastSeen = new HashMap<>();
    int left = 0, best = 0;
    for (int right = 0; right < s.length(); right++) {
        char c = s.charAt(right);
        if (lastSeen.containsKey(c) && lastSeen.get(c) >= left) {
            left = lastSeen.get(c) + 1;
        }
        lastSeen.put(c, right);
        best = Math.max(best, right - left + 1);
    }
    return best;
}
```

**Invariant:** the `>= left` check prevents a stale last-seen index (from before the current window started) from incorrectly shrinking the window — without it, a repeated character whose earlier occurrence already fell out of the window would wrongly jump `left` backward. **Complexity:** O(n) time, O(min(n, charset)) space.

## LC 207 — Course Schedule

```java
static boolean canFinish(int numCourses, int[][] prerequisites) {
    // build adjacency + indegree, then Kahn's BFS
    Deque<Integer> queue = new ArrayDeque<>();
    for (int i = 0; i < numCourses; i++) if (indegree[i] == 0) queue.add(i);
    int visited = 0;
    while (!queue.isEmpty()) {
        int course = queue.poll();
        visited++;
        for (int next : adj.get(course)) if (--indegree[next] == 0) queue.add(next);
    }
    return visited == numCourses;
}
```

**Invariant:** if a cycle exists, every course in it keeps a nonzero indegree forever (nothing outside the cycle can complete to unblock it), so `visited` ends up strictly less than `numCourses` — the cycle is detected by ABSENCE (of full traversal), not explicit cycle-tracking. **Complexity:** O(V+E) time and space.

## LC 56 — Merge Intervals

```java
static int[][] mergeIntervals(int[][] intervals) {
    Arrays.sort(intervals, Comparator.comparingInt(a -> a[0]));
    List<int[]> merged = new ArrayList<>();
    for (int[] interval : intervals) {
        if (merged.isEmpty() || merged.get(merged.size() - 1)[1] < interval[0]) {
            merged.add(interval);
        } else {
            merged.get(merged.size() - 1)[1] = Math.max(merged.get(merged.size() - 1)[1], interval[1]);
        }
    }
    return merged.toArray(new int[0][]);
}
```

**Invariant:** sorting by start first is what makes a single linear pass sufficient — once sorted, an interval can only overlap the MOST RECENTLY merged interval, never an earlier one, so only the last element of `merged` ever needs checking. **Complexity:** O(n log n) time (sort-dominated), O(n) space.

## LC 139 — Word Break

```java
static boolean wordBreak(String s, List<String> wordDict) {
    Set<String> dict = new HashSet<>(wordDict);
    boolean[] dp = new boolean[s.length() + 1];
    dp[0] = true;
    for (int i = 1; i <= s.length(); i++) {
        for (int j = 0; j < i; j++) {
            if (dp[j] && dict.contains(s.substring(j, i))) { dp[i] = true; break; }
        }
    }
    return dp[s.length()];
}
```

**Invariant:** `dp[i]` = can `s[0..i)` be segmented; the inner loop tries every possible LAST word ending at position `i`, succeeding only if both the prefix up to the candidate word's start is ALREADY known-breakable (`dp[j]`) and the candidate substring is in the dictionary. **Complexity:** O(n²) time (n² substrings, each a substring-extraction + set lookup), O(n) space for the DP array.

## LC 128 — Longest Consecutive Sequence

```java
static int longestConsecutive(int[] nums) {
    Set<Integer> set = new HashSet<>(...);
    int best = 0;
    for (int n : set) {
        if (set.contains(n - 1)) continue; // not a run's start -- skip
        int length = 1;
        while (set.contains(n + length)) length++;
        best = Math.max(best, length);
    }
    return best;
}
```

**Invariant:** the `set.contains(n-1)` skip keeps this O(n) instead of O(n²) — without it, every element of every run would restart the counting walk, redoing work; with it, each run's elements are visited exactly twice total (once to skip, once inside the true start's inner while loop). **Complexity:** O(n) time, O(n) space.

## LC 973 — K Closest Points to Origin

```java
static int[][] kClosest(int[][] points, int k) {
    PriorityQueue<int[]> maxHeap = new PriorityQueue<>(
            (a, b) -> (b[0]*b[0]+b[1]*b[1]) - (a[0]*a[0]+a[1]*a[1]));
    for (int[] p : points) {
        maxHeap.offer(p);
        if (maxHeap.size() > k) maxHeap.poll();
    }
    return maxHeap.toArray(new int[0][]);
}
```

**Invariant:** the same "heap capped at size k, evict the extreme" shape as Week 10's LC 215/347 — here a MAX-heap (evict the FARTHEST point when over capacity) rather than a min-heap, since we want to keep the k SMALLEST distances. **Complexity:** O(n log k) time, O(k) space.

## LC 55 — Jump Game

```java
static boolean canJump(int[] nums) {
    int furthest = 0;
    for (int i = 0; i < nums.length; i++) {
        if (i > furthest) return false; // this index is unreachable
        furthest = Math.max(furthest, i + nums[i]);
    }
    return true;
}
```

**Invariant:** `furthest` is a monotonically non-decreasing high-water mark of the farthest index reachable using any jump seen so far; if the loop ever reaches an index `i` beyond that mark, no sequence of jumps could have reached `i` at all. **Complexity:** O(n) time, O(1) space.

## LC 127 — Word Ladder

```java
static int ladderLength(String beginWord, String endWord, List<String> wordList) {
    // BFS: each level = one more letter change; try all 26 letters at every position
    Deque<String> queue = new ArrayDeque<>();
    queue.add(beginWord);
    int steps = 1;
    while (!queue.isEmpty()) {
        int size = queue.size();
        for (int i = 0; i < size; i++) {
            String word = queue.poll();
            if (word.equals(endWord)) return steps;
            // try changing each position to each of 26 letters
        }
        steps++;
    }
    return 0;
}
```

**Invariant:** BFS (not DFS) guarantees the FIRST time `endWord` is dequeued, `steps` holds the SHORTEST transformation length — DFS would find A path but not necessarily the shortest, the same BFS-for-shortest-path principle as Week 11's LC 102 level-order traversal, applied to an implicit graph (words as nodes, one-letter-difference as edges) instead of an explicit tree. **Complexity:** O(n × 26 × L) where n = word count, L = word length.

## Verification — real, not asserted

```
== Loop 1 -- LC 3: Longest Substring Without Repeating Characters ==
  PASS  "abcabcbb" -> 3 ("abc")
  PASS  "bbbbb" -> 1
  PASS  "pwwkew" -> 3 ("wke")

== Loop 1 -- LC 207: Course Schedule ==
  PASS  2 courses, 1->0: finishable
  PASS  2 courses, cycle: NOT finishable

== Loop 2 -- LC 56: Merge Intervals ==
  PASS  merge([[1,3],[2,6],[8,10],[15,18]]) = [[1,6],[8,10],[15,18]]

== Loop 2 -- LC 139: Word Break ==
  PASS  "leetcode" breakable with [leet,code]
  PASS  "catsandog" NOT breakable

== Loop 3 -- LC 128: Longest Consecutive Sequence ==
  PASS  longestConsecutive = 4 ([1,2,3,4])

== Loop 3 -- LC 973: K Closest Points to Origin ==
  PASS  kClosest returns exactly k=1 point
  PASS  kClosest([[1,3],[-2,2]], k=1) = [[-2,2]] (closer to origin)

== Loop 4 -- LC 55: Jump Game ==
  PASS  [2,3,1,1,4] -> true, reaches the end
  PASS  [3,2,1,0,4] -> false, stuck at the 0

== Loop 4 -- LC 127: Word Ladder ==
  PASS  ladderLength(hit->cog) = 5 (hit->hot->dot->dog->cog)
  PASS  ladderLength = 0 when endWord not in the dictionary
Week 12 final-loop coding suite (8 problems): 15/15 assertions passed
```

Full source: `practice/java/week-12/final-loop-coding/src/`. Reproduce: `cd practice/java/week-12/final-loop-coding && javac -d out src/*.java && java -cp out Main`.

**One real bug this pack's own review caught**: the first draft of the LC 56 test asserted the wrong expected output (`[1,3],[6,10]` instead of the correct `[1,6],[8,10]`) — the ALGORITHM was right, the TEST's hand-computed expectation was wrong. Caught by running it, not by re-reading the code — exactly the discipline `study-packs/week-11/01-test-strategy-and-test-doubles.md` argues for.

## Exit check

- [ ] All 8 problems solved cold, timed, matching each loop's actual round time budget
- [ ] Can explain why LC 128's `set.contains(n-1)` check is what makes it O(n) instead of O(n²), unprompted
- [ ] Can explain why LC 127 needs BFS specifically (not DFS) for a correctness reason, not just a style preference
