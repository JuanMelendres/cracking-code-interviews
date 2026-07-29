---
title: "Java Coding Practice — Week 2"
week: 2
last_reviewed: 2026-07-29
---

# Java Coding Practice — Week 2

**8 problems + the monotonic-stack errata. All code on this page was compiled and executed — see the verification block at the end and `MANIFEST.md` for the exact commands.**

Narrate all six phases from `study-packs/week-01/04-coding-interview-communication.md` on every problem.

## Table of Contents

1. [Day 1 — LC 704, LC 35](#day-1--lc-704-binary-search-lc-35-search-insert-position)
2. [Day 2 — LC 33](#day-2--lc-33-search-in-rotated-sorted-array)
3. [Day 3 — LC 875](#day-3--lc-875-koko-eating-bananas)
4. [Day 4 — LC 20, LC 155](#day-4--lc-20-valid-parentheses-lc-155-min-stack)
5. [Day 5 — LC 739 + the errata](#day-5--lc-739-daily-temperatures--the-errata-drill)
6. [Day 6 — LC 208](#day-6--lc-208-implement-trie)
7. [Verification](#verification--real-not-asserted)

---

## Day 1 — LC 704 Binary Search, LC 35 Search Insert Position

**Pattern:** the binary-search template, with exact boundary handling.

```java
// LC 704
static int search(int[] nums, int target) {
    int lo = 0, hi = nums.length - 1;
    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;
        if (nums[mid] == target) return mid;
        if (nums[mid] < target) lo = mid + 1; else hi = mid - 1;
    }
    return -1;
}

// LC 35 — same template; on a miss, lo IS the correct insertion index
static int searchInsert(int[] nums, int target) {
    int lo = 0, hi = nums.length - 1;
    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;
        if (nums[mid] == target) return mid;
        if (nums[mid] < target) lo = mid + 1; else hi = mid - 1;
    }
    return lo;
}
```

**Retrospective:** identical loop skeleton; the only change is what happens on a miss. `mid = lo + (hi - lo) / 2` rather than `(lo + hi) / 2` avoids integer overflow on large arrays — worth stating unprompted as a follow-up-proofing detail. **Complexity:** O(log n) time, O(1) space, both.

## Day 2 — LC 33 Search in Rotated Sorted Array

```java
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
```

**Invariant, stated before coding:** at every step, at least one of the two halves around `mid` is fully sorted (rotation can only break sortedness at one point). Determine which half is sorted first, then check whether the target falls inside that half's range to decide which way to move. **Complexity:** O(log n) time, O(1) space — the rotation doesn't cost an extra pass because the sorted-half check is O(1) per iteration.

## Day 3 — LC 875 Koko Eating Bananas

```java
// Binary search on the ANSWER SPACE (eating speed), not the input array.
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
```

**Invariant:** "can Koko finish within `h` hours at speed `k`" is monotonic — if speed `k` is feasible, every speed greater than `k` is also feasible. Binary search works on any monotonic predicate over a range, not just over a sorted array of values — this is the generalization worth stating explicitly, since it's the piece candidates most often miss on first exposure to this pattern. **Complexity:** O(n log(max pile)) time, O(1) space.

## Day 4 — LC 20 Valid Parentheses, LC 155 Min Stack

```java
// LC 20
static boolean isValid(String s) {
    Deque<Character> stack = new ArrayDeque<>();
    for (char c : s.toCharArray()) {
        if (c == '(' || c == '[' || c == '{') {
            stack.push(c);
        } else {
            if (stack.isEmpty()) return false;
            char top = stack.pop();
            if ((c == ')' && top != '(') || (c == ']' && top != '[') || (c == '}' && top != '{')) return false;
        }
    }
    return stack.isEmpty();
}

// LC 155 — two parallel stacks: values, and the running minimum at each depth
final class MinStack {
    private final Deque<Integer> values = new ArrayDeque<>();
    private final Deque<Integer> minima = new ArrayDeque<>();
    void push(int val) {
        values.push(val);
        minima.push(minima.isEmpty() ? val : Math.min(val, minima.peek()));
    }
    void pop() { values.pop(); minima.pop(); }
    int top() { return values.peek(); }
    int getMin() { return minima.peek(); }
}
```

**Retrospective:** LC 20's early `stack.isEmpty()` check on a closing bracket is the edge case most commonly forgotten (an unmatched close on an empty stack). LC 155's parallel-minima-stack trick trades O(n) extra space for O(1) `getMin()` — the alternative (scanning for the min on demand) is O(n) per call, which is the natural but wrong first instinct.

## Day 5 — LC 739 Daily Temperatures — the errata drill

The Phase 1 audit found the source Notion guide's monotonic-stack material self-contradictory: **the concept-map diagram describes indices, but the module the audit read pushed values onto the stack** — and this is worth understanding as *why*, not just noting as a defect, because it isn't an arbitrary documentation typo.

**Why a values-only stack cannot solve this problem:** the required output is "how many days until a warmer temperature" — a *distance*, computed as `laterIndex - earlierIndex`. If the stack holds only temperature values, popping it tells you *that* a warmer day was found, but not *which day* the popped value came from, so the distance can't be computed at all. A values-only implementation isn't merely buggy — it's structurally incapable of producing the required output. This is the diagram's claim (indices) is actually the *only* correct approach; the documentation's inconsistency was internal, not a coherent alternative implementation worth reproducing.

**Correct, index-based implementation:**

```java
static int[] dailyTemperatures(int[] temps) {
    int n = temps.length;
    int[] result = new int[n];
    Deque<Integer> indexStack = new ArrayDeque<>(); // holds INDICES, not values
    for (int i = 0; i < n; i++) {
        while (!indexStack.isEmpty() && temps[indexStack.peek()] < temps[i]) {
            int prevIndex = indexStack.pop();
            result[prevIndex] = i - prevIndex;
        }
        indexStack.push(i);
    }
    return result;
}
```

Verified against the canonical LC 739 example `[73,74,75,71,69,72,76,73]` → `[1,1,4,2,1,1,0,0]` (see verification block).

**A second, separate defect the audit found in the same source material:** the method there was named `nextGreaterElements` (LeetCode 503's name, which is the **circular** variant) while implementing the plain, non-circular version. Naming matters here specifically because a candidate who has memorized the name without the distinction will confidently answer the wrong follow-up when asked "does this wrap around?"

## Day 6 — LC 208 Implement Trie

```java
final class Trie {
    private static final class Node {
        Map<Character, Node> children = new HashMap<>();
        boolean isEnd = false;
    }
    private final Node root = new Node();

    void insert(String word) {
        Node cur = root;
        for (char c : word.toCharArray()) cur = cur.children.computeIfAbsent(c, k -> new Node());
        cur.isEnd = true;
    }
    boolean search(String word) { Node n = find(word); return n != null && n.isEnd; }
    boolean startsWith(String prefix) { return find(prefix) != null; }
    private Node find(String s) {
        Node cur = root;
        for (char c : s.toCharArray()) { cur = cur.children.get(c); if (cur == null) return null; }
        return cur;
    }
}
```

**Retrospective:** the `isEnd` flag is what distinguishes "a prefix exists" (`startsWith`) from "a complete word exists" (`search`) — the most common bug on this problem is conflating the two, returning true from `search` for any node reached, complete word or not. **Complexity:** O(k) per operation, k = word/prefix length; O(alphabet size × total characters inserted) space in the worst case, much less in practice given shared prefixes.

## Verification — real, not asserted

```
== Week 2 problem set — binary search family ==
  PASS  LC704 search found
  PASS  LC704 search not found
  PASS  LC35 exact match
  PASS  LC35 insert between
  PASS  LC35 insert at end
  PASS  LC33 target in right half
  PASS  LC33 target absent
  PASS  LC875 koko example 1
  PASS  LC875 koko example 2
  PASS  LC875 koko example 3

== Stack family ==
  PASS  LC20 valid nested
  PASS  LC20 mismatched
  PASS  LC20 unclosed
  PASS  LC155 min after 3 pushes
  PASS  LC155 top after pop
  PASS  LC155 min after pop
  PASS  LC739 daily temperatures, index-based monotonic stack (corrected — see errata)

== Trie ==
  PASS  LC208 search exact word after insert
  PASS  LC208 prefix alone is not a word
  PASS  LC208 startsWith matches prefix
  PASS  LC208 search matches after inserting the prefix as its own word

Week 2 suite: 21/21 assertions passed
```

Full output in `practice/java/week-02/README.md`. Compiled and run with `javac`/`java` on OpenJDK 21.0.12, same hand-rolled harness as Week 1.

## Exit check

- [ ] All 8 problems solved with a written retrospective
- [ ] Can explain, from first principles, why a values-only stack cannot solve LC 739 (not just "the corrected code uses indices")
- [ ] Can state the circular-vs-non-circular naming distinction for `nextGreaterElement`-family problems
