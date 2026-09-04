---
title: "Stacks and the Monotonic Stack"
slug: stacks-and-monotonic-stack
document_type: syllabus-topic
domain: 03-data-structures-algorithms
topic_id: T-2105
status: draft
version: 1.0
last_updated: 2026-09-03
mastery_levels_covered: [L1, L2, L3, L4]
prerequisites:
  - ../01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md
related:
  - arrays-two-pointers-and-sliding-window.md
  - hashing-patterns-and-frequency-maps.md
practice: ../../practice/java/week-21/stacks/
production_scenarios: []
interview_paths: [interview-emergency-sprint, senior-to-staff]
official_references: []
source_history:
  - study-packs/week-21/03-stacks-coding-practice.md
---

# Stacks and the Monotonic Stack

> **Provenance.** The five worked problems and retrospectives in Sections 7 and 15 are elevated from `study-packs/week-21/03-stacks-coding-practice.md` — real, compiled, executed code (`practice/java/week-21/stacks/`), re-verified on OpenJDK 21.0.12 while writing this chapter (13/13 assertions passing).

This is Master Topic Register **T-1406** (IWI 5.2, high frequency). [Arrays, Two Pointers, and Sliding Window](arrays-two-pointers-and-sliding-window.md#4-core-concepts-l2) already introduces the monotonic-deque idea for a *sliding window*; this chapter covers the same "evict dominated candidates" idea applied to an entire array in one left-to-right pass, using a stack instead of a deque.

## 1. Why This Matters

A monotonic stack turns an O(n²) "for each element, scan forward or backward to find the next/previous greater or smaller value" brute force into a single O(n) pass — and this exact "next greater element" primitive recurs, in disguise, across a surprising range of problems (histogram areas, temperature-wait-time problems, expression evaluation) that don't superficially look related to each other at all. Recognizing the shared primitive underneath different-looking problem statements is the actual interview skill.

## 2. Prerequisites

[Algorithmic Complexity and Big-O](../01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md#4-core-concepts-l2) — specifically the amortized-analysis reasoning already introduced there for `ArrayList.add()`, reused directly in this chapter's monotonic-stack complexity arguments (Section 5).

## 3. Foundation (L1)

**A stack is a last-in-first-out (LIFO) structure: the most recently added element is always the first one removed.** This single property makes it the natural tool for any problem involving matching or resolving something in the *reverse* order it was encountered — matching parentheses (the most recently opened bracket must be the next one closed), or evaluating a postfix expression (the most recently pushed operands are the ones the next operator applies to).

**A monotonic stack maintains its elements in strictly increasing or strictly decreasing order at all times**, by popping (evicting) any element that violates that order before pushing a new one. The core insight: an evicted element can never again be useful for the specific question being asked, because a better (larger, or smaller) candidate has already been found — the same "evict dominated candidates" reasoning [Arrays, Two Pointers, and Sliding Window](arrays-two-pointers-and-sliding-window.md#4-core-concepts-l2) uses for its monotonic deque, here applied across the whole array rather than a bounded window.

## 4. Core Concepts (L2)

**The "next greater element" template** (Section 7, Problem 1) is the monotonic stack's canonical use: scanning left to right, maintain a decreasing stack of values (or indices) whose "next greater element" hasn't been found yet; the moment a larger value arrives, pop every smaller value the stack is holding — each pop's answer is exactly the current value, since it's the *first* larger value encountered scanning forward from that popped element's position.

**A monotonic stack of indices, not values,** is needed whenever the answer depends on *position* (a distance, a width) rather than just the neighboring value itself — Largest Rectangle in Histogram (Section 7, Problem 2) needs the index specifically to compute a width once a bar's rectangle extent is fully determined.

**A stack's most literal use — deferring processing until a later token resolves it —** shows up directly in expression evaluation (Section 7, Problem 3): postfix notation is precisely the representation that makes a stack the natural fit, since operand order already encodes evaluation order with no precedence rules or parentheses ever needed.

**A "two-stack" design** (Section 7, Problem 4) is the standard technique for building a FIFO-behaved structure (a queue) out of LIFO-behaved primitives (two stacks) — the same design-pattern family as [Min Stack](../../practice/java/week-02/src/StackProblems.java) (an O(1)-per-operation min-tracking stack), both examples of augmenting or combining a stack's simple primitive to deliver a richer, still-O(1)-amortized interface.

## 5. How It Works Internally (L3)

**The amortized-O(n) argument for every monotonic-stack problem in this chapter rests on one fact: each element is pushed onto the stack exactly once and popped at most once, across the entire run of the algorithm** — exactly the same style of accounting [Algorithmic Complexity's amortized-analysis section](../01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md#4-core-concepts-l2) introduces for `ArrayList.add()`, and the same accounting [Arrays, Two Pointers, and Sliding Window's Sliding Window Maximum](arrays-two-pointers-and-sliding-window.md#5-how-it-works-internally-l3) uses for its monotonic deque. Even though the inner `while` loop in Largest Rectangle in Histogram (Section 7, Problem 2) looks like it could run up to `n` times per outer iteration, summed across the entire pass, total pushes and pops are bounded by `2n`, not `n²`.

**Largest Rectangle in Histogram's correctness argument, precisely**: for any bar, the widest rectangle with that bar as its limiting height extends exactly from the nearest strictly-shorter bar on its left to the nearest strictly-shorter bar on its right (exclusive of both). A monotonic increasing stack of indices tracks exactly the set of bars whose right boundary hasn't been found yet; the moment a shorter bar arrives, every taller bar still on the stack has just had its right boundary determined (the current index), and its left boundary is automatically the new stack top after popping (the nearest *remaining* shorter bar). Appending a sentinel height of `0` after the real array forces every bar still on the stack at the end to be resolved, rather than needing separate cleanup logic.

**The two-stack queue's amortized cost, precisely**: `transferIfNeeded()` only actually reverses `inStack` into `outStack` when `outStack` is empty, not on every `pop()`/`peek()` call — so while any single transfer can cost O(n) in the worst case, each individual element is moved from `inStack` to `outStack` exactly once over its entire lifetime in the structure. Summed across `n` total operations, total transfer work is bounded by O(n), making the *amortized* per-operation cost O(1) even though a specific, individual call can occasionally cost more.

## 6. Practical Usage

- **Reach for a monotonic stack the moment a problem asks for "the next/previous greater or smaller element"** for every position in an array — the single most reliable trigger phrase for this pattern.
- **Track indices instead of values on the stack whenever the answer needs a distance or width**, not just the neighboring value itself.
- **Reach for the two-stack design pattern whenever a problem asks you to implement one ADT (queue, min-tracking stack) using only the operations of a different, more primitive one** — a genuinely common "design" interview-question shape.

## 7. Examples

**Problem 1 — LC 496, Next Greater Element I.**

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

**Retrospective:** the canonical template — a monotonic decreasing stack pops every element smaller than the current one, and each pop is exactly that element's answer. **Complexity:** O(n) — each element pushed and popped at most once.

**Problem 2 — LC 84, Largest Rectangle in Histogram.**

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

**Retrospective:** see Section 5's boundary-determination argument. The classic "hard" monotonic-stack problem and a strong Staff-level signal — most candidates handle the simpler "next greater element" template but stall here without recognizing it's the same primitive applied to widths. **Complexity:** O(n).

**Problem 3 — LC 150, Evaluate Reverse Polish Notation.**

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

**Retrospective:** postfix notation is precisely the representation that makes a stack the right tool; popping `b` before `a` matters for non-commutative operators, since the second-pushed operand is the right-hand side. **Complexity:** O(n) time, O(n) space worst case.

**Problem 4 — LC 232, Implement Queue using Stacks.**

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

**Retrospective:** see Section 5's amortized-transfer argument. **Complexity:** O(1) amortized per operation, O(n) space.

**Problem 5 — LC 503, Next Greater Element II (circular array).**

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

**Retrospective:** simulating the wraparound by iterating `i` from `0` to `2n-1` with `i % n` indexing avoids physically concatenating the array; the `if (i < n)` guard is essential — without it, indices would be pushed twice, corrupting the "each index popped once" invariant. **Complexity:** O(n) despite the `2n` iteration bound.

## 8. Common Mistakes

- **Tracking values on the monotonic stack when the answer actually needs a position (distance or width)** — Largest Rectangle in Histogram fails immediately if the stack holds heights instead of indices, since the width calculation needs a position to subtract.
- **Forgetting the sentinel value at the end of a monotonic-stack pass** (Section 5's `h = 0` at `i == n`) — without it, bars still on the stack when the real array ends never get their rectangle resolved at all.
- **Transferring `inStack` to `outStack` on every single operation** rather than only when `outStack` is empty — this still produces correct results but destroys the O(1) amortized guarantee, degrading to O(n) per operation.
- **Physically duplicating an array to simulate a circular scan** (Section 7, Problem 5) rather than using modular indexing — not incorrect, but a real, avoidable O(n) memory cost, and a missed opportunity to demonstrate the cleaner technique.

## 9. Edge Cases

- **A strictly increasing or strictly decreasing input array** — Largest Rectangle in Histogram's own verified `[2,4]` case (strictly increasing) confirms the algorithm handles an array with no interior "shorter bar" trigger until the sentinel.
- **Division in Reverse Polish Notation** (`evalRPN([4,13,5,/,+]) = 6`, i.e., `13/5 = 2` using integer division, then `4+2=6`) — a real, verified test case confirming integer-division truncation (a direct instance of [Number Representation's](../01-computer-science-foundations/number-representation.md) narrowing/truncation behavior, here from division rather than casting) is handled as the problem expects, not accidentally producing a different rounding behavior.
- **A circular array where the answer wraps all the way around** (`nextGreaterElements([1,2,3,4,3])`'s verified result, where the last `3` finds its answer by wrapping past the array's own maximum) — confirms the `2n`-iteration technique genuinely handles wraparound, not just same-pass neighbors.

## 10. Performance Implications

Real, executed verification from `practice/java/week-21/stacks/` (OpenJDK 21.0.12), re-run while writing this chapter:

```
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

Every monotonic-stack solution here is O(n), turning what looks like it could be O(n²) (a naive "for each element, scan the rest of the array" approach to "next greater element") into a single linear pass — the same order-of-magnitude gap [Algorithmic Complexity's own measurements](../01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md#10-performance-implications) quantify for O(n) vs. O(n²) directly.

## 11. Trade-offs

| Choice | Gains | Costs |
|---|---|---|
| Monotonic stack (values) | O(n), simple to implement | Only answers "what's the next greater/smaller value," not distance-based questions |
| Monotonic stack (indices) | O(n), also answers distance/width questions | Slightly more indirection when reading the code (an extra array lookup per stack access) |
| Two-stack amortized design | O(1) amortized per operation, simple to reason about | A single operation can occasionally cost O(n) — unsuitable if a hard per-call latency bound is required, not just a good average |
| Physically duplicating an array for circular simulation | Simpler code, no modular arithmetic | O(n) extra memory for no genuine algorithmic benefit over modular indexing |

## 12. Senior-Level Considerations (L3)

The Senior-level skill is recognizing the *same* monotonic-stack primitive underneath superficially different problem statements — Largest Rectangle in Histogram doesn't mention "next greater element" anywhere in its description, yet its core mechanism (Section 5) is the identical eviction logic as the much simpler Next Greater Element I. An interviewer escalating from the simple template to the histogram problem is directly testing whether that transfer happens, or whether the candidate treats each new problem as requiring an entirely new approach from scratch.

## 13. Staff/System-Level Considerations (L4)

At Staff scope, the amortized-cost reasoning underlying the two-stack queue design (Section 5) is exactly the reasoning needed to evaluate whether a real system's occasional expensive operation is actually a problem: a batch-flush design (accumulate writes, periodically flush all of them at once) has the identical amortized-cost shape as `transferIfNeeded()` — most operations are cheap, one periodic operation is expensive, and the *average* cost across all operations is what matters for throughput, while the *worst-case single-operation* cost is what matters for tail latency. Correctly identifying which of these two costs a given system requirement actually cares about (throughput SLA vs. p99.9 latency SLA) is precisely the judgment call this chapter's amortized-vs-worst-case distinction (Section 11) prepares an engineer to make explicitly, rather than conflating "amortized O(1)" with "every single call is fast."

## 14. Production Scenarios

No existing `production-cookbook/` entry has a monotonic-stack-specific algorithmic root cause.

> Planned reference: a future `production-cookbook/` entry covering a real batch-flush or lazy-transfer design's tail-latency surprise (an occasional expensive flush violating a p99.9 SLA despite excellent average throughput) would be a natural, non-duplicative addition connecting this chapter's amortized-cost lesson (Section 13) to a genuine production incident.

## 15. Interview Questions

### Question 1 — Given an array, find the next greater element for every position, in O(n) time.

**Why interviewers ask it.** It's the canonical monotonic-stack warm-up, checking whether "monotonic stack" is a technique the candidate reaches for directly, versus defaulting to an O(n²) "for each element, scan forward" brute force.

**Expected answer.** Maintain a decreasing stack of values (or indices) scanning left to right; whenever the current value exceeds the stack's top, pop it — that pop is exactly the popped element's answer, since the current value is the first larger value found scanning forward from its position. Push the current value/index after all smaller ones are popped. O(n) time, since each element is pushed and popped at most once.

**Minimum acceptable answer.** Produces a correct O(n²) brute force, then successfully improves to O(n) with the monotonic-stack hint.

**Strong Senior answer.** Produces the O(n) monotonic-stack solution directly, and can state the amortized-cost argument (Section 5) precisely when asked to justify the complexity, given the loop's nested-looking structure.

**Staff-level extension.** Recognizes and names the transfer to Largest Rectangle in Histogram (Section 7, Problem 2) unprompted, or when given that problem as a follow-up — identifying it as the same primitive applied to widths rather than values, rather than treating it as an unrelated new problem.

**Common mistakes.** Defaulting to an O(n²) nested-loop solution and stopping there without attempting the monotonic-stack improvement, or implementing the stack logic with the eviction direction reversed (an increasing stack instead of decreasing, silently producing wrong answers rather than an obvious crash).

**Follow-up questions.** "How would this change if you needed the next greater element circularly (wrapping around the end)?" (Section 7, Problem 5's `2n`-iteration-with-modular-indexing technique.)

### Question 2 — Why is `transferIfNeeded()`'s two-stack queue design O(1) amortized, when a single `pop()` call can trigger an O(n) transfer?

**Why interviewers ask it.** It's a direct, concrete test of amortized-analysis reasoning — whether a candidate can defend a big-O claim that looks contradictory on its surface (a single operation costs O(n), yet the amortized claim is O(1)).

**Expected answer.** Each individual element is transferred from `inStack` to `outStack` at most once over its entire lifetime in the structure — once transferred, it never moves back. So while any single `transferIfNeeded()` call can cost up to O(n) (if `outStack` was empty and `inStack` held n elements), the *total* transfer work summed across all `n` operations performed on the structure is bounded by O(n), not O(n²). Dividing that total O(n) transfer cost across n operations gives an amortized O(1) cost per operation, even though individual operations vary widely in actual cost.

**Minimum acceptable answer.** States the answer is O(1) amortized and gestures at "each element only moves once," even without the precise total-cost-divided-by-operation-count framing.

**Strong Senior answer.** Explicitly connects this to `ArrayList.add()`'s own amortized-O(1) resize argument as a structurally identical style of reasoning, despite the completely different mechanism.

**Staff-level extension.** Names the real system-design implication (Section 13): amortized O(1) is the right guarantee for throughput-oriented requirements but does *not* bound any single operation's worst-case latency — a batch-flush or lazy-transfer design with this exact cost shape can silently violate a strict per-request latency SLA even while its average throughput looks excellent.

**Common mistakes.** Conflating "amortized O(1)" with "every call is fast" — missing that amortized guarantees say nothing about any single operation's worst-case cost.

**Follow-up questions.** "Could an adversarial sequence of operations force every single call to hit the expensive O(n) path?" (No — once elements are transferred to `outStack`, they stay there until popped; a new expensive transfer only happens again once `outStack` is fully drained and new elements have accumulated in `inStack`, which itself takes real operations to occur.)

## 16. Coding/Practice Exercises

- Run the [existing practice code](../../practice/java/week-21/stacks/) yourself and confirm the same 13/13 assertions pass.
- This pattern has additional real, already-solved problems: LC 20 (Valid Parentheses), LC 155 (Min Stack), and LC 739 (Daily Temperatures) in [`practice/java/week-02/src/StackProblems.java`](../../practice/java/week-02/src/StackProblems.java) — study Daily Temperatures specifically as the closest sibling to this chapter's Next Greater Element I, applying the identical template to day-index deltas instead of values.
- Attempt LC 402 (Remove K Digits) from scratch — it's monotonic-stack-*shaped* but is already solved and categorized under Greedy elsewhere in this repository (`study-packs/week-20/02-greedy-coding-practice.md`); working through it here is a good check of whether the monotonic-stack primitive transfers to a problem framed in entirely different (greedy digit-removal) terms.

## 17. Debugging Exercises

**Symptom:** a "next greater element" implementation passes on small, simple test arrays but produces wrong answers on arrays with duplicate values or a strictly decreasing suffix.

**Diagnose:** check the stack's eviction comparison operator — using `<=` instead of `<` (or vice versa, depending on whether the problem wants "strictly greater" or "greater or equal") changes which elements get evicted when a tie occurs, a subtle, easy-to-miss condition that only manifests on inputs containing duplicates. Separately, check whether the stack is ever left with unresolved elements at the end of the pass (Section 8's sentinel-value mistake) — a strictly decreasing suffix means no later element will ever trigger those elements' eviction, so without a sentinel or explicit final cleanup pass, they silently keep their default "no answer" value even when a correct answer might exist depending on the exact problem variant.

## 18. Design Exercises

**Design constraint:** design a real-time system that must report, for a continuously arriving stream of stock prices, the number of consecutive prior days (including today) where the price has been less than or equal to today's price — recomputed after every new price arrives, without re-scanning the entire price history on each new arrival.

Design this using the monotonic-stack template from Section 4/7 directly (this is, underneath, the "Stock Span Problem," a well-known real variant of Next Greater Element): maintain a monotonic stack of (price, span) pairs; when a new price arrives, pop every stack entry with a price less than or equal to the new price, accumulating their spans into the new entry's own span before pushing it. State explicitly why this achieves amortized O(1) per new price despite the popping loop, using the identical "each entry pushed and popped at most once" argument from Section 5 — and name the real production framing this represents: an incrementally-updated, O(1)-amortized-per-event calculation, as opposed to a naive re-scan-the-whole-history-on-every-event design that would degrade as the price history grows, the same *shape* of failure [Algorithmic Complexity's own Staff-level section](../01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md#13-staffsystem-level-considerations-l4) warns about generally.

## 19. Further Reading

- [Arrays, Two Pointers, and Sliding Window](arrays-two-pointers-and-sliding-window.md) — the monotonic-*deque* variant of this same "evict dominated candidates" idea, bounded to a sliding window rather than the whole array.
- [Algorithmic Complexity and Big-O](../01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md#4-core-concepts-l2) — the amortized-analysis reasoning this chapter's complexity arguments reuse directly.

## 20. Mastery Checklist

| Level | You can... | Verify with |
|---|---|---|
| L1 | Explain, in plain language, what a stack's LIFO property means and what "monotonic" means for a stack | [Section 3](#3-foundation-l1) |
| L2 | Apply the "next greater element" monotonic-stack template to a new, unfamiliar array problem, choosing values or indices correctly | [Interview Question 1](#question-1--given-an-array-find-the-next-greater-element-for-every-position-in-on-time) |
| L3 | Derive the amortized-O(n) argument for a monotonic-stack pass, and the boundary-determination argument for Largest Rectangle in Histogram specifically | [Section 10's real verification](#10-performance-implications), [Section 5](#5-how-it-works-internally-l3) |
| L4 | Distinguish amortized-cost guarantees from worst-case-per-operation guarantees in a real system-design context, and design a real incremental, O(1)-amortized-per-event system using this chapter's monotonic-stack technique (Section 18) | [Debugging Exercise](#17-debugging-exercises), [Section 13](#13-staffsystem-level-considerations-l4) |
