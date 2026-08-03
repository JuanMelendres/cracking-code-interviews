---
title: "Coding Practice — Stacks / Monotonic Stack (T-1406)"
week: 21
document_type: study-pack-coding-practice
status: draft
last_reviewed: 2026-08-03
---

# Coding Practice — Stacks / Monotonic Stack (T-1406)

**5 problems. All code on this page was compiled and executed — see `MANIFEST.md` for the exact commands and real pass counts.** Brings this pattern's coverage from 3/10 to 8/10. Previous coverage (LC 20 Valid Parentheses, LC 155 Min Stack, LC 739 Daily Temperatures, in `practice/java/week-02/src/StackProblems.java`) already established the monotonic-stack basics; this batch adds the canonical Daily-Temperatures pair, the classic hard monotonic-stack problem, expression evaluation, the stack-based queue design pair to Min Stack, and the circular-array follow-up.

Note: LC 402 (Remove K Digits) and LC 42 (Trapping Rain Water) are monotonic-stack-*shaped* problems but are already solved and categorized elsewhere in this repo (Greedy and Two-pointers respectively, per `study-packs/week-20/02-greedy-coding-practice.md` and week-11's pattern map) — they are not re-added here to avoid double-counting.

---

## Problem 1 — LC 496 Next Greater Element I

**Pattern:** monotonic decreasing stack over `nums2`, cached into a lookup map, then queried for each element of `nums1`.

```java
static int[] nextGreaterElement(int[] nums1, int[] nums2) {
    Map<Integer, Integer> nextGreater = new HashMap<>();
    Deque<Integer> stack = new ArrayDeque<>();
    for (int num : nums2) {
        while (!stack.isEmpty() && stack.peek() < num) {
            nextGreater.put(stack.pop(), num);
        }
        stack.push(num);
    }
    int[] result = new int[nums1.length];
    for (int i = 0; i < nums1.length; i++) result[i] = nextGreater.getOrDefault(nums1[i], -1);
    return result;
}
```

**Retrospective:** the canonical "next greater element" template — a monotonic decreasing stack pops every element smaller than the current one, and each pop is exactly that element's answer, since the current value is by definition the *first* larger value seen so far scanning left to right. This is the same core loop as LC 739 (Daily Temperatures), just applied to values instead of indices-with-day-deltas. **Complexity:** O(n) time — each element pushed and popped at most once.

## Problem 2 — LC 84 Largest Rectangle in Histogram

**Pattern:** monotonic increasing stack of indices — when a bar shorter than the stack top appears, that's exactly when the popped bar's rectangle extent is fully determined.

```java
static int largestRectangleArea(int[] heights) {
    Deque<Integer> stack = new ArrayDeque<>();
    int maxArea = 0;
    int n = heights.length;
    for (int i = 0; i <= n; i++) {
        int h = (i == n) ? 0 : heights[i];
        while (!stack.isEmpty() && heights[stack.peek()] >= h) {
            int height = heights[stack.pop()];
            int width = stack.isEmpty() ? i : i - stack.peek() - 1;
            maxArea = Math.max(maxArea, height * width);
        }
        stack.push(i);
    }
    return maxArea;
}
```

**Retrospective:** for any bar, its rectangle's width is bounded by the nearest shorter bar on each side — the monotonic stack tracks exactly the bars whose "nearest shorter bar on the right" hasn't been found yet, and popping one when a shorter bar arrives is the moment that right boundary becomes known (the new stack top after the pop is automatically the left boundary, since it's the nearest *remaining* shorter bar). Appending a sentinel height of `0` at `i == n` forces every remaining bar on the stack to be resolved at the end. This is the classic "hard" monotonic-stack problem and a strong Staff-level signal — most candidates can do LC 739 but stall here without recognizing it's the same primitive applied to widths instead of day-deltas. **Complexity:** O(n) time — each index pushed and popped once.

## Problem 3 — LC 150 Evaluate Reverse Polish Notation

**Pattern:** a stack used for its most literal purpose — deferring operands until their operator arrives.

```java
static int evalRPN(String[] tokens) {
    Deque<Integer> stack = new ArrayDeque<>();
    for (String token : tokens) {
        if (isOperator(token)) {
            int b = stack.pop(), a = stack.pop();
            stack.push(apply(token, a, b));
        } else {
            stack.push(Integer.parseInt(token));
        }
    }
    return stack.pop();
}
```

**Retrospective:** postfix notation is precisely the representation that makes a stack the right tool — no parentheses or precedence rules are ever needed, because the token order already encodes evaluation order; popping `b` before `a` (not the reverse) matters for non-commutative operators (`-`, `/`), since the second-pushed operand is the right-hand side. **Complexity:** O(n) time, O(n) space worst case.

## Problem 4 — LC 232 Implement Queue using Stacks

**Pattern:** two stacks, one for incoming pushes and one for outgoing pops, with lazy (amortized) transfer — the design-stack pair to LC 155's Min Stack from Week 2.

```java
static class MyQueue {
    private final Deque<Integer> inStack = new ArrayDeque<>();
    private final Deque<Integer> outStack = new ArrayDeque<>();

    void push(int x) { inStack.push(x); }

    int pop() { transferIfNeeded(); return outStack.pop(); }
    int peek() { transferIfNeeded(); return outStack.peek(); }
    boolean empty() { return inStack.isEmpty() && outStack.isEmpty(); }

    private void transferIfNeeded() {
        if (outStack.isEmpty()) while (!inStack.isEmpty()) outStack.push(inStack.pop());
    }
}
```

**Retrospective:** reversing `inStack` into `outStack` exactly once per element (only when `outStack` runs dry, not on every operation) is what gives this O(1) *amortized* time despite each individual transfer costing O(n) — every element is moved from `inStack` to `outStack` exactly once over its entire lifetime in the structure, so the total transfer cost across n operations is O(n), not O(n²). This amortized-cost reasoning is a common follow-up question and worth being able to state precisely, not just wave at. **Complexity:** O(1) amortized per operation, O(n) space.

## Problem 5 — LC 503 Next Greater Element II (circular array)

**Pattern:** LC 496's monotonic stack, extended to a circular array by iterating `2n` times using modular indexing without physically duplicating the array.

```java
static int[] nextGreaterElements(int[] nums) {
    int n = nums.length;
    int[] result = new int[n];
    Arrays.fill(result, -1);
    Deque<Integer> stack = new ArrayDeque<>();
    for (int i = 0; i < 2 * n; i++) {
        int num = nums[i % n];
        while (!stack.isEmpty() && nums[stack.peek()] < num) result[stack.pop()] = num;
        if (i < n) stack.push(i);
    }
    return result;
}
```

**Retrospective:** simulating the wraparound by iterating `i` from `0` to `2n-1` and indexing with `i % n` avoids the common mistake of physically concatenating `nums` with itself (which doubles memory for no benefit); the guard `if (i < n) stack.push(i)` is essential — without it, indices would be pushed twice during the second pass, corrupting the "each index popped once" invariant the whole algorithm depends on. **Complexity:** O(n) time despite the `2n` iteration bound — each index still pushed and popped at most once.

## Verification

```
$ cd practice/java/week-21/stacks/src && javac -d ../out Check.java Problems.java Main.java && java -cp ../out Main
  PASS  LC496 nextGreaterElement([4,1,2],[1,3,4,2]) = [-1,3,-1]
  PASS  LC496 nextGreaterElement([2,4],[1,2,3,4]) = [3,-1]
  PASS  LC84 largestRectangleArea([2,1,5,6,2,3]) = 10
  PASS  LC84 largestRectangleArea([2,4]) = 4
  PASS  LC150 evalRPN([2,1,+,3,*]) = 9
  PASS  LC150 evalRPN([4,13,5,/,+]) = 6
  PASS  LC232 peek() after push(1),push(2) = 1
  PASS  LC232 pop() = 1 (FIFO)
  PASS  LC232 empty() -> false (2 remains)
  PASS  LC232 pop() = 2
  PASS  LC232 empty() -> true after draining
  PASS  LC503 nextGreaterElements([1,2,1]) circular = [2,-1,2]
  PASS  LC503 nextGreaterElements([1,2,3,4,3]) circular = [2,3,4,-1,4] (last 3 wraps past 4 first)
Week 21 — Stacks (LC 496, 84, 150, 232, 503): 13/13 assertions passed
```
