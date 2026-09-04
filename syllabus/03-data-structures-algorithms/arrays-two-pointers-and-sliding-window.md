---
title: "Arrays, Two Pointers, and Sliding Window"
slug: arrays-two-pointers-and-sliding-window
document_type: syllabus-topic
domain: 03-data-structures-algorithms
topic_id: T-2101
status: draft
version: 1.0
last_updated: 2026-09-03
mastery_levels_covered: [L1, L2, L3, L4]
prerequisites:
  - ../01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md
related:
  - ../01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md
practice: ../../practice/java/week-23/arrays-two-pointers/
production_scenarios:
  - ../../production-cookbook/boundary-condition-bug-behind-a-95-percent-coverage-figure.md
interview_paths: [interview-emergency-sprint, senior-to-staff]
official_references:
  - https://en.wikipedia.org/wiki/Two-pointer_technique
source_history:
  - study-packs/week-23/02-arrays-two-pointers-coding-practice.md
---

# Arrays, Two Pointers, and Sliding Window

> **Provenance.** The five worked problems and their retrospectives in Sections 7 and 15 are elevated, not rewritten, from `study-packs/week-23/02-arrays-two-pointers-coding-practice.md` — real, compiled, executed code (`practice/java/week-23/arrays-two-pointers/`), verified again on OpenJDK 21.0.12 while writing this chapter (11/11 assertions passing). This is the pattern the [syllabus transformation plan](../../00-project/syllabus-transformation-plan.md) itself named: canonical prose is new, but the underlying practice code is real and reusable as-is.

This is the first canonical chapter in `03-data-structures-algorithms`, corresponding to Master Topic Register **T-1402** (Arrays, two pointers, sliding window) — the highest-weighted pattern in the entire coding-interview register (IWI 6.3, near-certain interview frequency). [Algorithmic Complexity and Big-O](../01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md) is the one hard prerequisite: every technique below is a specific, learnable way to turn an O(n²) brute-force scan into O(n).

## 1. Why This Matters

Two pointers and sliding window are the single most frequently tested pattern in coding interviews, and for good reason: an enormous fraction of "process an array" problems have a brute-force O(n²) solution (check every pair, recompute every window from scratch) and a linear-time solution that becomes visible only once you recognize this specific pattern. An engineer who has internalized it stops re-deriving it from scratch every time and instead pattern-matches the problem shape to the technique almost immediately — freeing interview time to spend on communication and edge cases rather than algorithm discovery itself.

## 2. Prerequisites

[Algorithmic Complexity and Big-O](../01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md) — specifically, being able to recognize an O(n²) nested-loop shape and understand why eliminating the inner loop's re-scanning is the entire point of every technique in this chapter.

## 3. Foundation (L1)

**A two-pointer technique uses two index variables that move through an array (or string) according to some rule, instead of a nested loop that re-scans from the beginning for every outer step.** The canonical shape: one pointer starts at the beginning, one at the end, and they move toward each other based on a comparison — each step provably eliminates possibilities that could never be part of a better answer, so the pointers never need to backtrack.

**A sliding window is the same idea applied to a contiguous subarray or substring**: instead of checking every possible start-and-end pair (an O(n²) count of subarrays), maintain a "window" — a `[left, right]` range — and grow or shrink it one side at a time, updating a running answer incrementally rather than recomputing it from scratch for every window.

**Both techniques exist because of the same underlying insight**: for many problems, most of the pairs or subarrays a brute-force approach would check are *provably* not worth checking, given what's already been learned from checking a related pair or window. Recognizing which comparisons are safe to skip — and being able to justify *why* they're safe to skip — is the actual skill, not just knowing "use two pointers" as an incantation.

## 4. Core Concepts (L2)

**Opposite-direction two pointers** (start and end, moving toward each other) are the right shape when the problem has a monotonic property as the pointers close in — Section 7's Problem 1 (Container With Most Water) is the canonical example: moving the pointer at the *shorter* line is always safe, because the current area is already capped by that shorter height, so keeping it and moving the other pointer can only ever match or shrink the width without any chance of a taller limiting height.

**Same-direction two pointers** (both moving forward, at different speeds or with a gap) show up in problems like cycle detection or "remove the Nth node from the end" (Section 7 of [Linked Lists](linked-lists-and-in-place-manipulation.md) covers this shape directly for list problems) and in-place array compaction.

**A sliding window is "fixed-size" when the window length is given directly** (Section 7's Problem 2, Sliding Window Maximum, with a fixed `k`) or **"variable-size"** when the window grows and shrinks based on a condition (e.g., "smallest window containing all characters of `t`" — `minWindow`, referenced in Section 16, is the canonical variable-size example). The fixed-size case only ever needs to add one element and remove one element per step; the variable-size case needs an explicit rule for when to shrink the window from the left.

**A monotonic deque (as used for Sliding Window Maximum) is a specialized two-pointer-adjacent structure**: instead of tracking two index positions directly, it maintains a deque of *candidate* indices in decreasing order of value, evicting any index whose value can never again be the answer because a larger, more recent value has already dominated it. This is the same "evict dominated candidates" idea [Stacks and the Monotonic Stack](stacks-and-monotonic-stack.md) uses for the *entire* array in one left-to-right pass, here restricted to whatever's still inside the current window.

## 5. How It Works Internally (L3)

**The amortized-cost argument is what makes a sliding-window or monotonic-deque solution genuinely O(n) rather than looking like it might be O(n·k) or worse.** In Sliding Window Maximum (Section 7, Problem 2), the inner `while` loops that expire and evict elements look, at first glance, like they could run up to `k` times per outer iteration — but each index is pushed onto the deque exactly once and popped at most once, across the *entire* run of the algorithm, not per window. Summed across all `n` outer iterations, the total number of push and pop operations is bounded by `2n`, not `n·k` — this is precisely the amortized-analysis reasoning [Algorithmic Complexity](../01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md#4-core-concepts-l2) introduces generally, applied to a specific, concrete data structure here.

**The "safe to skip" justification for opposite-direction two pointers is a proof by contradiction, not an intuition to take on faith.** For Container With Most Water: suppose the shorter line is at `lo` and you moved `hi` inward instead of `lo`. Every area you could form with the new `hi'` and the old `lo` has width strictly less than or equal to before, and height still capped by the same shorter line at `lo` (since `hi'` is somewhere between `lo` and the old `hi`, and could only be taller, not shorter, than the eliminated `hi` — but the *limiting* height is still `height[lo]`, unchanged). So no area found this way can exceed what's already been checked. This is the general shape of *every* two-pointer correctness argument in this pattern: identify exactly what moving a specific pointer can and cannot produce, and show the discarded region is provably dominated.

## 6. Practical Usage

- **Reach for opposite-direction two pointers when the array is sorted, or when a comparison at the two ends tells you something monotonic** (which side to move) — Container With Most Water, and the classic Two Sum on a sorted array (referenced in Section 16).
- **Reach for a sliding window when the problem asks about a contiguous subarray or substring** satisfying some condition (a fixed length, a sum target, "contains all of X") — Sliding Window Maximum here, `minWindow` (Section 16) for the variable-size case.
- **Reach for the "two-pass prefix/suffix" variant** (Section 7, Problem 3) when a problem needs information from both directions simultaneously without being able to compute it in one pass alone — a related but structurally distinct technique from pointer-movement two-pointers, grouped here because it solves the same class of "avoid the O(n²) recomputation" problem.

## 7. Examples

**Problem 1 — LC 11, Container With Most Water.** Two pointers starting at both ends, always advancing the pointer at the *shorter* line.

```java
static int maxArea(int[] height) {
    int lo = 0, hi = height.length - 1, best = 0;
    while (lo < hi) {
        int area = (hi - lo) * Math.min(height[lo], height[hi]);
        best = Math.max(best, area);
        if (height[lo] < height[hi]) lo++; else hi--;
    }
    return best;
}
```

**Retrospective:** advancing the shorter side is provably safe because the *current* area is already bounded by the shorter line's height — keeping that same shorter line and moving the other pointer inward can only ever produce a smaller or equal width with, at best, an equal limiting height, so it can never beat the current area; the only way to potentially find a *larger* area is to move past the shorter line and hope for a taller one. **Complexity:** O(n) time, O(1) space.

**Problem 2 — LC 239, Sliding Window Maximum.** A monotonic decreasing deque of *indices* — the maximum for each window is always the front of the deque, maintained in O(1) amortized per element.

```java
static int[] maxSlidingWindow(int[] nums, int k) {
    Deque<Integer> deque = new ArrayDeque<>();
    for (int i = 0; i < n; i++) {
        while (!deque.isEmpty() && deque.peekFirst() <= i - k) deque.pollFirst(); // expire out-of-window
        while (!deque.isEmpty() && nums[deque.peekLast()] < nums[i]) deque.pollLast(); // evict smaller values
        deque.offerLast(i);
        if (i >= k - 1) result[i - k + 1] = nums[deque.peekFirst()];
    }
}
```

**Retrospective:** evicting every smaller value from the back of the deque before inserting the current index is safe because any smaller element to the left of a larger one can *never* become the window maximum again — once a bigger value arrives, the smaller one is strictly dominated for the remainder of its time in any shared window. **Complexity:** O(n) time — Section 5 covers exactly why the nested-looking `while` loops don't make this O(n·k).

**Problem 3 — LC 238, Product of Array Except Self.** Two passes — a running prefix product, then a running suffix product multiplied in on a second pass — avoiding division entirely.

```java
static int[] productExceptSelf(int[] nums) {
    int[] result = new int[nums.length];
    result[0] = 1;
    for (int i = 1; i < nums.length; i++) result[i] = result[i - 1] * nums[i - 1];
    int suffix = 1;
    for (int i = nums.length - 1; i >= 0; i--) {
        result[i] *= suffix;
        suffix *= nums[i];
    }
    return result;
}
```

**Retrospective:** the naive approach — compute the total product, then divide by each element — breaks the moment any element is zero and also violates the problem's "no division" constraint; computing prefix and suffix products separately sidesteps both issues, since a zero at position `i` propagates correctly through the suffix product for `j < i` and the prefix product for `j > i`. **Complexity:** O(n) time, O(1) extra space (excluding the output array).

**Problem 4 — LC 189, Rotate Array.** In-place right rotation via three reversals — no auxiliary array needed.

```java
static void rotate(int[] nums, int k) {
    int n = nums.length;
    k %= n;
    reverse(nums, 0, n - 1);
    reverse(nums, 0, k - 1);
    reverse(nums, k, n - 1);
}
```

**Retrospective:** reversing the entire array first produces the correct *relative* order of both the "wrapped" segment and the "stayed" segment, but with each segment internally backward — reversing each segment individually afterward fixes internal order while preserving relative position. The `k %= n` guard is essential: `k` can legally exceed `n`, and without the modulo, `reverse(nums, 0, k-1)` throws rather than silently misbehaving — a constraint that must be actively checked, not assumed (Section 9). **Complexity:** O(n) time, O(1) space.

**Problem 5 — LC 31, Next Permutation.** Find the rightmost ascent, swap it with the smallest larger value to its right, then reverse the suffix.

```java
static void nextPermutation(int[] nums) {
    int i = n - 2;
    while (i >= 0 && nums[i] >= nums[i + 1]) i--; // find rightmost ascending pair
    if (i >= 0) {
        int j = n - 1;
        while (nums[j] <= nums[i]) j--; // find rightmost element greater than nums[i]
        swap(nums, i, j);
    }
    reverse(nums, i + 1, n - 1); // the suffix is always descending here; reverse makes it the smallest arrangement
}
```

**Retrospective:** the suffix after the rightmost ascent is, by definition, non-increasing, meaning it's already at its lexicographically *largest* arrangement — reversing it produces the *smallest*, which is exactly what "next permutation" needs. Swapping with the smallest suffix value still greater than `nums[i]` is what makes the result the *immediate* next permutation rather than some larger one. **Complexity:** O(n) time, O(1) space.

## 8. Common Mistakes

- **Reaching for a nested loop out of habit before checking whether a monotonic property makes two pointers or a sliding window applicable.** The mechanical tell: if the brute force is "for every `i`, scan some range depending on `i`," and that range has a monotonic relationship to `i`, a linear pass is very likely possible.
- **Forgetting to bound `k` (or an equivalent parameter) against the actual array length**, exactly the Rotate Array bug in Section 7: `k %= n` isn't a defensive nicety, it's required correctness, since the problem's own constraints allow `k` to exceed `n`.
- **Using a sliding window's shrink condition incorrectly** — shrinking too eagerly (missing valid windows) or not eagerly enough (never shrinking, degenerating into an O(n²) scan in disguise) is the most common variable-size sliding-window bug, and is exactly why `minWindow`-style problems are consistently rated tougher than fixed-size window problems like Sliding Window Maximum.

## 9. Edge Cases

- **An already-sorted or already-rotated-to-identity input** (Rotate Array with `k` a multiple of `n`, Next Permutation on `[3,2,1]` wrapping to `[1,2,3]`) — every one of Section 7's five problems has a real, verified test case for exactly this kind of degenerate input (see Section 10).
- **Arrays containing zero, for any technique involving a running product or division-avoidance** (Product of Array Except Self) — a zero anywhere in the array changes which positions in the result are themselves zero, and more than one zero forces the *entire* result to be zero, both real edge cases the practice code's test suite exercises directly.
- **A window or pointer range that never needs to move at all** — an array of length 1 or 2, where the "two pointers converge" loop condition (`lo < hi`) may never execute its body, or executes exactly once; a correct implementation must not assume at least one iteration happens.

## 10. Performance Implications

Real, executed verification from `practice/java/week-23/arrays-two-pointers/` (OpenJDK 21.0.12), re-run while writing this chapter:

```
  PASS  LC11 maxArea(9 heights) = 49
  PASS  LC11 maxArea([1,1]) = 1
  PASS  LC239 maxSlidingWindow(k=3) = [3,3,5,5,6,7]
  PASS  LC239 maxSlidingWindow single element = [1]
  PASS  LC238 productExceptSelf([1,2,3,4]) = [24,12,8,6]
  PASS  LC238 productExceptSelf(with zero) = [0,0,9,0,0]
  PASS  LC189 rotate([1..7], k=3) = [5,6,7,1,2,3,4]
  PASS  LC189 rotate([-1,-100,3,99], k=2) = [3,99,-1,-100]
  PASS  LC31 nextPermutation([1,2,3]) = [1,3,2]
  PASS  LC31 nextPermutation([3,2,1]) wraps to [1,2,3]
  PASS  LC31 nextPermutation([1,1,5]) = [1,5,1]
Week 23 — Arrays/Two-Pointers (LC 11, 239, 238, 189, 31): 11/11 assertions passed
```

All five solutions are O(n) time, O(1) extra space (Product of Array Except Self excludes the required output array from that count, as is standard). The practical performance implication worth internalizing over any specific number: every one of these replaces what would naturally be written as an O(n²) brute force, and the gap between O(n) and O(n²) at realistic interview-scale inputs (thousands to low millions of elements) is exactly the gap [Algorithmic Complexity's own measurements](../01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md#10-performance-implications) quantify directly — milliseconds versus multiple seconds at `n = 10,000,000`.

## 11. Trade-offs

| Choice | Gains | Costs |
|---|---|---|
| Opposite-direction two pointers | O(n) time, O(1) space, no extra data structure | Requires a provable monotonic property — doesn't apply to every array problem |
| Monotonic deque (sliding window) | O(n) amortized even though it looks like it could be worse | More complex to implement correctly than a naive re-scan per window; easy to get the eviction condition backwards |
| Two-pass prefix/suffix | Avoids division and its zero-value failure mode entirely | Requires O(n) extra thought about what each pass accumulates — less immediately obvious than a single-pass approach |
| In-place reversal-based rotation | O(1) space — genuinely in-place | Three separate reversal calls are less immediately readable than an O(n)-space auxiliary-array approach |

## 12. Senior-Level Considerations (L3)

The Senior-level skill is being able to *justify* why a two-pointer or sliding-window approach is correct — the proof-by-contradiction style argument in Section 5 — rather than only being able to produce the code from memorized pattern-matching. An interviewer probing past a correct-but-unexplained solution is checking for exactly this: can the candidate defend why moving the shorter line's pointer (Section 5) can never lose the optimal answer, on the spot, for a variant of the problem they haven't seen before.

## 13. Staff/System-Level Considerations (L4)

At Staff scope, the relevant transfer isn't the interview problems themselves — it's recognizing the same "eliminate provably-dominated candidates instead of re-scanning" pattern inside real production code. A service that repeatedly re-scans an already-processed prefix of a large collection on every incremental update (rather than maintaining a running pointer or window into it) is paying an avoidable O(n²) cost in production for exactly the reason a naive brute-force interview solution does — and the fix is the same underlying idea: maintain enough incremental state (a pointer, a window, a monotonic structure) to avoid redoing work already provably settled. Recognizing this transfer is what separates "I can solve LeetCode problems" from "I can see the same inefficiency pattern in a code review of a batch-processing job."

## 14. Production Scenarios

No existing `production-cookbook/` entry has an array/two-pointer algorithmic root cause specifically; [Boundary Condition Bug Behind a 95% Coverage Figure](../../production-cookbook/boundary-condition-bug-behind-a-95-percent-coverage-figure.md) is a genuinely related, if more general, example — a boundary-condition bug (the exact failure category Section 9's edge cases exist to prevent) that shipped despite high test coverage, because the specific boundary case wasn't covered by any test, only by line coverage of the surrounding code.

> Planned reference: a dedicated `production-cookbook/` entry for an off-by-one or unbounded-parameter bug in array/window logic (e.g., an unguarded rotation-offset or window-size parameter exceeding collection length) would be a natural, non-duplicative addition to that deliverable's own backlog.

## 15. Interview Questions

### Question 1 — Given a sorted array, find two numbers that sum to a target. What's the most efficient approach?

**Why interviewers ask it.** It's the canonical, simplest opposite-direction two-pointer problem — a fast check for whether a candidate reaches for O(n) two pointers or defaults to an O(n²) nested loop or an O(n) extra-space hash set when the input's sortedness makes something even simpler and space-free possible.

**Expected answer.** Two pointers starting at both ends: if the sum is too small, move the left pointer right (increasing the sum); if too large, move the right pointer left (decreasing it); if equal, return. O(n) time, O(1) space — better than a hash-set approach's O(n) *extra space*, specifically because the array is already sorted.

**Minimum acceptable answer.** Produces a correct O(n) or better solution, even if it's the hash-set approach rather than two pointers.

**Strong Senior answer.** Names two pointers specifically as exploiting the sortedness the hash-set approach ignores, and can justify the pointer-movement rule (Section 5's style of argument): moving left inward when the sum is too small is safe because the current left value, paired with anything smaller than the current right, can never reach the target.

**Staff-level extension.** Generalizes to: "what if the array weren't sorted, and sorting it first cost O(n log n)?" — correctly identifies that sorting first plus two pointers (O(n log n) total) is still asymptotically better than nothing, but that the original hash-set approach (O(n), unsorted) actually wins here, a genuine, non-obvious trade-off worth explicitly comparing rather than assuming two pointers always wins.

**Common mistakes.** Defaulting straight to nested loops without checking whether the array's sortedness (a detail candidates sometimes skim past in the problem statement) changes the optimal approach.

**Follow-up questions.** "What if the array has duplicates and you need all unique pairs, not just one?" (Requires explicit duplicate-skipping logic while advancing pointers — the exact complication Three Sum, referenced in Section 16, builds on top of this base pattern.)

### Question 2 — Why is the Sliding Window Maximum solution O(n) and not O(n·k), given the nested-looking while loops inside the main loop?

**Why interviewers ask it.** It directly tests the amortized-analysis reasoning from Section 5 — whether a candidate can look past a superficially-nested loop structure and correctly account for total work across the entire run, exactly the kind of complexity-analysis nuance [Algorithmic Complexity](../01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md#8-common-mistakes) names as a common mistake in general.

**Expected answer.** Each index is pushed onto the deque exactly once (during the main loop's single pass) and popped from the deque at most once (either from the front, when it expires out of the window, or from the back, when a larger value evicts it) — never both from the same eviction pass repeatedly. Summed across the whole array, total push and pop operations are bounded by `2n`, not `n × k`, even though any single iteration's inner `while` loops could, in isolation, look unbounded.

**Minimum acceptable answer.** States the answer is O(n) and gestures at "each element is only processed a constant number of times overall," even without a precise amortized argument.

**Strong Senior answer.** Explicitly walks through the "each index pushed once, popped at most once" accounting, connecting it directly to `ArrayList.add()`'s own amortized-O(1) reasoning as a structurally identical style of argument, even though the mechanism (resize-and-copy vs. deque eviction) is completely different.

**Staff-level extension.** Names a real category of code review finding this generalizes to: any loop whose *worst-case single-iteration* cost looks expensive but whose *total, summed-across-all-iterations* cost is actually bounded — recognizing when a review comment "this inner loop could be O(n) per iteration" is a real concern versus an amortized non-issue is a genuinely valuable, transferable skill.

**Common mistakes.** Assuming any nested-looking loop is automatically worse than O(n) without doing the summed-cost accounting — the mirror image of [Algorithmic Complexity's own Section 8](../01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md#8-common-mistakes) common mistake, applied here to *underestimating* rather than overestimating efficiency.

**Follow-up questions.** "Could you solve this with a different data structure and get the same complexity?" (A balanced BST or a max-heap with lazy deletion can also solve it, typically at O(n log k) rather than O(n) — a real, worthwhile complexity comparison to be able to state precisely.)

## 16. Coding/Practice Exercises

- Run the [existing practice code](../../practice/java/week-23/arrays-two-pointers/) yourself (`javac -d out src/*.java && java -cp out Main`) and confirm the same 11/11 assertions pass on your machine.
- This pattern has additional, real, already-solved problems beyond this chapter's five: `twoSumSorted` (LC 167) in [`practice/java/week-01/src/Problems.java`](../../practice/java/week-01/src/Problems.java), and `threeSum` (LC 15), `trap` (LC 42, Trapping Rain Water), and `minWindow` (LC 76, the canonical variable-size sliding window) in [`practice/java/week-11/mixed-review/src/MixedReviewProblems.java`](../../practice/java/week-11/mixed-review/src/MixedReviewProblems.java) — study `minWindow` specifically as the variable-size counterpart to this chapter's fixed-size Sliding Window Maximum.
- Attempt LC 3 (Longest Substring Without Repeating Characters) from scratch, applying the variable-size sliding-window shrink-condition reasoning from Section 8, before checking any existing solution.

## 17. Debugging Exercises

**Symptom:** an in-place array rotation function works correctly in every manual test during development, but throws `ArrayIndexOutOfBoundsException` intermittently once deployed and receiving real, varied input.

**Diagnose:** check whether the rotation amount `k` is ever validated against the array's actual length before use — Section 7's Problem 4 and Section 8 both name this exact bug: `k` can legally exceed `n` per most rotation-problem specifications, and skipping `k %= n` means any `k` larger than the array's length reaches a reversal call with an out-of-bounds index. Confirm by checking whether the failing inputs in production correlate with a rotation amount larger than the array being rotated — a very fast, high-confidence check before touching any code.

## 18. Design Exercises

**Design constraint:** a monitoring system must report the maximum value observed in the last `k` measurements of a continuously arriving real-time metric stream, recomputed after every single new measurement, without the per-measurement cost growing as `k` grows.

Design this using the monotonic-deque technique from Section 4/5 directly: state why a naive "keep the last `k` measurements and scan for the max each time" approach is O(k) per measurement — a real, production-relevant cost for a metric with sub-second arrival and a `k` large enough to represent a meaningful time window — and how the monotonic-deque approach reduces this to amortized O(1) per measurement, using the exact eviction argument from Section 7's Sliding Window Maximum. Name the memory trade-off: the deque holds at most `k` indices, so this isn't free — it trades a bounded amount of extra memory for the amortized time improvement.

## 19. Further Reading

- [Two-pointer technique — Wikipedia](https://en.wikipedia.org/wiki/Two-pointer_technique) — a general overview of the pattern family this chapter's opposite-direction and same-direction variants both belong to.
- [Linked Lists and In-Place Manipulation](linked-lists-and-in-place-manipulation.md) — the same-direction two-pointer variant (fast/slow pointers), applied to linked structures rather than arrays.
- [Stacks and the Monotonic Stack](stacks-and-monotonic-stack.md) — the full-array version of the "evict dominated candidates" idea this chapter's monotonic deque restricts to a sliding window.

## 20. Mastery Checklist

| Level | You can... | Verify with |
|---|---|---|
| L1 | Explain, in plain language, what a two-pointer technique and a sliding window are, and why both avoid a nested-loop rescan | [Section 3](#3-foundation-l1) |
| L2 | Distinguish opposite-direction from same-direction two pointers, and fixed-size from variable-size sliding windows, and match a new problem to the right one | [Interview Question 1](#question-1--given-a-sorted-array-find-two-numbers-that-sum-to-a-target-whats-the-most-efficient-approach) |
| L3 | Produce a proof-by-contradiction-style correctness argument for a two-pointer solution, and give the amortized-cost accounting for why a monotonic-deque sliding window is O(n), not O(n·k) | [Section 10's real verification](#10-performance-implications), [Section 5](#5-how-it-works-internally-l3) |
| L4 | Recognize the same "eliminate provably-dominated candidates" pattern inside real production code outside of an interview context, and design a system component (Section 18) using it deliberately | [Debugging Exercise](#17-debugging-exercises), [Section 13](#13-staffsystem-level-considerations-l4) |
