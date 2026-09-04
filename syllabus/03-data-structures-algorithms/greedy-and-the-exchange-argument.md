---
title: "Greedy and the Exchange Argument"
slug: greedy-and-the-exchange-argument
document_type: syllabus-topic
domain: 03-data-structures-algorithms
topic_id: T-2112
status: draft
version: 1.0
last_updated: 2026-09-03
mastery_levels_covered: [L1, L2, L3, L4]
prerequisites:
  - dynamic-programming.md
related:
  - dynamic-programming.md
  - stacks-and-monotonic-stack.md
practice: ../../practice/java/week-20/greedy/
production_scenarios: []
interview_paths: [interview-emergency-sprint, senior-to-staff]
official_references: []
source_history:
  - study-packs/week-20/02-greedy-coding-practice.md
---

# Greedy and the Exchange Argument

> **Provenance.** The five worked problems and retrospectives in Sections 7 and 15 are elevated from `study-packs/week-20/02-greedy-coding-practice.md` — real, compiled, executed code (`practice/java/week-20/greedy/`), re-verified on OpenJDK 21.0.12 while writing this chapter (10/10 assertions passing).

This is Master Topic Register **T-1413** (IWI 4.8, high frequency). A greedy algorithm makes the locally-best choice at each step and never reconsiders it — [Dynamic Programming](dynamic-programming.md) is the natural point of contrast, since DP problems require considering multiple options and remembering results, while a genuine greedy problem's defining property is that reconsideration is provably never necessary.

## 1. Why This Matters

Greedy algorithms are usually short and fast (often O(n) or O(n log n)) — but correctly recognizing *when* a greedy approach is actually valid, versus when it looks tempting but produces a wrong answer, is the entire interview skill. Unlike DP, where "did I memoize correctly" is a mechanical check, greedy correctness requires an actual argument (an exchange argument, or an equivalent proof sketch) for why the locally-best choice is always at least as good as any alternative — and being able to produce that argument, not just the code, is what separates a strong greedy answer from a lucky one.

## 2. Prerequisites

[Dynamic Programming](dynamic-programming.md) — understanding when DP is necessary (a choice must be reconsidered based on later information) is the direct contrast that clarifies when greedy suffices instead (a choice, once made, is provably never worth reconsidering).

## 3. Foundation (L1)

**A greedy algorithm makes the choice that looks best right now, commits to it permanently, and moves on — it never backtracks or reconsiders an earlier decision.** This works only when the problem has a specific property: the locally-best choice at each step is provably part of *some* globally optimal solution, meaning no future information could ever make an earlier greedy choice look wrong in hindsight.

**An exchange argument is the standard way to prove a greedy choice is safe**: assume some optimal solution makes a different choice than the greedy one at some step, then show that swapping in the greedy choice instead either produces an equally good or strictly better solution — proving the greedy choice is never worse.

## 4. Core Concepts (L2)

**Implicit BFS-layer expansion** (Jump Game II, Section 7 Problem 1) is greedy specifically because committing to "jump as far as the current reachable layer allows" is always at least as good as any less-greedy choice — farther reach can only help future jumps, never hurt them, which is exactly the exchange-argument property.

**Running-total-reset greedy** (Gas Station, Section 7 Problem 2) exploits a specific, provable fact: if starting at station `i` and the tank goes negative at station `j`, no station between `i` and `j` could have been a valid start either, since each would arrive at `j` with an equal-or-smaller tank — licensing jumping the candidate start straight to `j+1` rather than testing every index individually.

**Closed-form greedy formulas** (Task Scheduler, Section 7 Problem 3) skip simulation entirely when the problem's structure permits deriving the answer directly from a small number of aggregate facts (here, the maximum task frequency and how many tasks are tied at that frequency).

**Last-occurrence-tracking greedy** (Partition Labels, Section 7 Problem 4) extends a partition's boundary to cover every character's last appearance seen so far — a partition can only be "closed" once every character inside it is guaranteed not to reappear later.

**Monotonic-stack greedy** (Remove K Digits, Section 7 Problem 5) combines this chapter's greedy principle with [Stacks'](stacks-and-monotonic-stack.md) monotonic-eviction mechanism: greedily removing any digit that's larger than an incoming smaller one always produces a smaller-or-equal result, since the removed digit occupied a more significant position.

## 5. How It Works Internally (L3)

**Gas Station's exchange-argument proof, precisely**: suppose the tank goes negative starting from index `i` at index `j` (meaning the cumulative gas-minus-cost sum from `i` to `j` is negative). For *any* station `k` strictly between `i` and `j`, the cumulative sum from `i` to `k` must be non-negative (otherwise the tank would have gone negative even earlier, at `k`, contradicting that `j` was the first failure point) — meaning the cumulative sum from `k` to `j` is *more* negative than the sum from `i` to `j` (since removing a non-negative prefix from a negative total makes it more negative, not less). So starting at `k` instead of `i` would reach `j` with an equal-or-worse tank, meaning `k` can never be a valid start either. This is what licenses jumping the candidate start index straight past the entire failed range to `j+1`, rather than testing each intermediate index individually — the exchange argument made completely explicit.

**Task Scheduler's closed-form derivation, precisely**: the most frequent task (frequency `maxFreq`) forces `(maxFreq - 1)` complete "cooldown windows," each of size `(n + 1)` (the task itself, plus `n` slots that must be filled by other tasks or left idle) — think of laying out `maxFreq` copies of the most frequent task with exactly `n` slots between consecutive copies. The final formula adds `maxCount` (however many *other* tasks are tied at that same maximum frequency, since each needs its own slot in the final, otherwise-idle window). Whenever there are enough total distinct tasks to fill every idle slot across all windows, the true answer is simply the total task count (no idle time needed at all) — which is exactly why the result is `Math.max(formula, tasks.length)`, not the formula alone.

**Remove K Digits' greedy-correctness argument**: a larger digit immediately followed by a smaller one is always worth removing the larger one, because doing so strictly decreases the resulting number from a more significant digit position — no alternative removal choice at that point could ever produce a smaller result. A monotonic-increasing stack enforces exactly this rule automatically: whenever the incoming digit is smaller than the stack's top, the top is popped (if removals remain), repeating until the stack's top is no longer larger than the incoming digit.

## 6. Practical Usage

- **Before committing to a greedy approach, attempt to state the exchange argument explicitly** — if you can't articulate why the locally-best choice is never wrong, the problem may actually require DP (which considers multiple options) instead.
- **Look for "if this fails, nothing before the failure point could have worked either" reasoning** (Gas Station) as a strong signal that a greedy reset/skip technique applies.
- **Recognize when a problem's answer can be derived from a small number of aggregate statistics** (Task Scheduler) rather than requiring simulation at all — a closed-form greedy formula is often faster and simpler than an equivalent simulated approach.

## 7. Examples

**Problem 1 — LC 45, Jump Game II.**

```java
static int jump(int[] nums) {
    int jumps = 0, currentEnd = 0, farthest = 0;
    for (int i = 0; i < nums.length - 1; i++) {
        farthest = Math.max(farthest, i + nums[i]);
        if (i == currentEnd) {
            jumps++;
            currentEnd = farthest;
        }
    }
    return jumps;
}
```

**Retrospective:** greedy, not DP, because committing to the current layer's farthest reach is never worse than any less-greedy choice. **Complexity:** O(n) time, O(1) space — better than an O(n²) DP formulation.

**Problem 2 — LC 134, Gas Station.**

```java
static int canCompleteCircuit(int[] gas, int[] cost) {
    int totalTank = 0, currentTank = 0, startIndex = 0;
    for (int i = 0; i < gas.length; i++) {
        int diff = gas[i] - cost[i];
        totalTank += diff;
        currentTank += diff;
        if (currentTank < 0) {
            startIndex = i + 1;
            currentTank = 0;
        }
    }
    return totalTank >= 0 ? startIndex : -1;
}
```

**Retrospective:** see Section 5's exchange-argument proof. **Complexity:** O(n) time, O(1) space, versus O(n²) brute force.

**Problem 3 — LC 621, Task Scheduler.**

```java
static int leastInterval(char[] tasks, int n) {
    int[] freq = new int[26];
    for (char t : tasks) freq[t - 'A']++;
    int maxFreq = 0;
    for (int f : freq) maxFreq = Math.max(maxFreq, f);
    int maxCount = 0;
    for (int f : freq) if (f == maxFreq) maxCount++;
    int formula = (maxFreq - 1) * (n + 1) + maxCount;
    return Math.max(formula, tasks.length);
}
```

**Retrospective:** see Section 5's closed-form derivation. **Complexity:** O(tasks.length + 26) — effectively O(n).

**Problem 4 — LC 763, Partition Labels.**

```java
static List<Integer> partitionLabels(String s) {
    int[] lastIndex = new int[26];
    for (int i = 0; i < s.length(); i++) lastIndex[s.charAt(i) - 'a'] = i;
    List<Integer> result = new ArrayList<>();
    int start = 0, end = 0;
    for (int i = 0; i < s.length(); i++) {
        end = Math.max(end, lastIndex[s.charAt(i) - 'a']);
        if (i == end) {
            result.add(end - start + 1);
            start = i + 1;
        }
    }
    return result;
}
```

**Retrospective:** a partition can only close once every character inside it has had its last occurrence accounted for. **Complexity:** O(n) time, O(1) space.

**Problem 5 — LC 402, Remove K Digits.**

```java
static String removeKdigits(String num, int k) {
    StringBuilder stack = new StringBuilder();
    for (char c : num.toCharArray()) {
        while (k > 0 && stack.length() > 0 && stack.charAt(stack.length() - 1) > c) {
            stack.deleteCharAt(stack.length() - 1);
            k--;
        }
        stack.append(c);
    }
    while (k > 0 && stack.length() > 0) {
        stack.deleteCharAt(stack.length() - 1);
        k--;
    }
    int i = 0;
    while (i < stack.length() - 1 && stack.charAt(i) == '0') i++;
    String result = stack.substring(i);
    return result.isEmpty() ? "0" : result;
}
```

**Retrospective:** see Section 5's monotonic-stack-greedy argument. Leading zeros must be stripped from the result. **Complexity:** O(n) time, O(n) space.

## 8. Common Mistakes

- **Applying a greedy approach without an exchange argument, based purely on the fact that the code "seems to work" on a few test cases** — a greedy solution that hasn't been proven correct can pass small test cases by luck and fail on adversarial input.
- **Missing the `Math.max(formula, tasks.length)` guard in a closed-form greedy formula** (Task Scheduler) — the formula alone assumes idle slots are needed, which isn't true once there are enough distinct tasks to fill every slot.
- **Forgetting to strip leading zeros from a numeric-string result** (Remove K Digits) — a valid numeric result can't have them, and the greedy digit-removal logic alone doesn't prevent them from appearing.

## 9. Edge Cases

- **`n = 0` (no cooldown required) in Task Scheduler** (verified case, correctly returning just the total task count) — the closed-form formula must degrade gracefully to "no idle time needed" when there's no cooldown constraint at all.
- **An empty result after all removals in Remove K Digits** (verified `"10", k=2` case, correctly returning `"0"`, not an empty string) — a numeric result of zero digits isn't valid; it must become the string `"0"`.
- **No valid starting station exists at all in Gas Station** (verified case, correctly returning `-1`) — the total tank check (`totalTank >= 0`) is what distinguishes "a valid start exists somewhere" from "no valid start exists anywhere," separate from the search for *which* index it is.

## 10. Performance Implications

Real, executed verification from `practice/java/week-20/greedy/` (OpenJDK 21.0.12), re-run while writing this chapter:

```
  PASS  LC45 jump([2,3,1,1,4]) = 2
  PASS  LC45 jump([2,3,0,1,4]) = 2
  PASS  LC134 canCompleteCircuit start index = 3
  PASS  LC134 canCompleteCircuit no solution = -1
  PASS  LC621 leastInterval(AAABBB, n=2) = 8
  PASS  LC621 leastInterval(AAABBB, n=0) = 6 (no cooldown needed)
  PASS  LC763 partitionLabels("ababcbacadefegdehijhklij")
  PASS  LC402 removeKdigits("1432219", 3)
  PASS  LC402 removeKdigits("10200", 1) -- leading zeros stripped
  PASS  LC402 removeKdigits("10", 2) -- empty result becomes "0"
Week 20 — Greedy (LC 45, 134, 621, 763, 402): 10/10 assertions passed
```

Every solution here is O(n) or O(n log n), each replacing what would otherwise require an O(n²) brute force (Jump Game II, Gas Station) or a DP-shaped exploration of multiple choices — the practical performance lesson is that a correctly-identified and correctly-proven greedy approach is often dramatically faster than the DP or brute-force alternative a candidate might otherwise reach for.

## 11. Trade-offs

| Choice | Gains | Costs |
|---|---|---|
| Greedy (when provably valid) | Simple, fast (often O(n) or O(n log n)), no memoization needed | Only valid when an exchange argument (or equivalent) actually holds — silently wrong otherwise |
| DP | Correct even when greedy isn't valid — considers and remembers multiple options | Typically slower and more complex than an equivalent greedy solution when greedy would have worked |
| Closed-form formula (when derivable) | Fastest possible — no simulation at all | Requires recognizing the problem's structure permits a formula in the first place; not always possible |

## 12. Senior-Level Considerations (L3)

The Senior-level skill is being able to produce or sketch an exchange argument on demand, not just intuit that a greedy approach "feels right." An interviewer probing a candidate's greedy solution with "why does this always work" is checking for exactly this — the ability to reason about *why* reconsidering an earlier choice could never help, not just confidence that it happens not to hurt on the examples tried so far.

## 13. Staff/System-Level Considerations (L4)

At Staff scope, recognizing when a system's scheduling or resource-allocation logic can be greedy (and thus fast and simple) versus when it genuinely requires a more expensive optimization approach (DP, ILP, or a full search) is a real, consequential architecture decision — a system incorrectly built around a greedy assumption that doesn't actually hold will produce subtly wrong results under specific, possibly rare, input patterns, exactly analogous to a greedy interview solution that passes easy test cases but fails an adversarial one. The exchange-argument discipline (Section 3/12) is the same rigor needed to validate that a production scheduling heuristic is actually correct, not merely "seems to work in practice so far."

## 14. Production Scenarios

No existing `production-cookbook/` entry has a greedy-algorithm-specific root cause.

> Planned reference: a future `production-cookbook/` entry covering a real scheduling or resource-allocation system that used an unproven greedy heuristic, which worked correctly for typical traffic but produced a subtly wrong allocation under a specific, rarer input pattern, would be a natural, non-duplicative addition connecting this chapter's exchange-argument discipline to a genuine incident.

## 15. Interview Questions

### Question 1 — How do you know when a greedy approach is actually correct for a given problem, rather than just a plausible-looking heuristic?

**Why interviewers ask it.** It directly tests whether "exchange argument" is a real, applied reasoning tool or an unfamiliar term — the single most important theoretical distinction this entire pattern rests on.

**Expected answer.** Attempt to construct an exchange argument: assume an optimal solution makes a different choice than the greedy one at some step, then show that swapping in the greedy choice instead produces a result that's at least as good. If this swap can always be shown not to hurt, the greedy choice is safe at that step; if a counterexample can be constructed where the swap makes things strictly worse, greedy isn't valid and a different approach (often DP) is needed.

**Minimum acceptable answer.** States that greedy needs "some kind of proof" it's correct, even without describing the exchange-argument technique specifically.

**Strong Senior answer.** Applies the exchange argument concretely to a specific problem (e.g., Gas Station, Section 5) rather than describing it only in the abstract.

**Staff-level extension.** Connects this to a real system-design risk (Section 13): a production heuristic assumed safe without this rigor can silently misbehave on input patterns the original testing never covered — the same failure mode as an unproven greedy interview solution passing easy cases.

**Common mistakes.** Treating "it worked on the examples I tried" as sufficient evidence of correctness — exactly the trap an adversarial test case (or an adversarial production input) is designed to expose.

**Follow-up questions.** "Can you think of a problem where a tempting greedy approach is actually wrong?" (A classic example: unweighted job scheduling with deadlines where naive earliest-deadline-first ordering intuitively feels right but requires an actual exchange-argument proof, or the well-known counterexample of using a greedy coin-change approach with a non-canonical coin system, where greedy fails and DP is required.)

### Question 2 — In the Gas Station problem, why does finding one failed starting point let you skip testing every station between the failure and its start, rather than checking each one individually?

**Why interviewers ask it.** It's a direct test of the exchange-argument reasoning (Section 5) applied to a specific, well-known problem, checking whether a candidate can derive the "skip to `j+1`" optimization rather than settling for a correct-but-slower O(n²) approach that tests every starting index independently.

**Expected answer.** If starting at station `i` and the tank goes negative at station `j`, then for any station `k` strictly between `i` and `j`, the cumulative sum from `i` to `k` must be non-negative (otherwise the failure would have occurred at `k`, not `j`) — which means the sum from `k` to `j` is even *more* negative than the sum from `i` to `j`. So `k` would fail even sooner than `i` did, meaning `k` can never be a valid starting point either, and testing it individually would be redundant.

**Minimum acceptable answer.** States that stations between the start and the failure point "also fail," even without the precise cumulative-sum argument for why.

**Strong Senior answer.** Derives the cumulative-sum argument precisely and unprompted, connecting it directly to why the algorithm can safely jump the candidate start to `j+1` in one step.

**Staff-level extension.** Generalizes this "if a prefix fails, no sub-prefix within the failed range could have succeeded either" reasoning as a broader technique applicable beyond this specific problem — any problem where failure at a point implies failure at every intermediate point within a specific range is a candidate for this same skip-ahead optimization.

**Common mistakes.** Testing every starting index independently in O(n²) without recognizing the skip-ahead optimization is available and provably correct, not just a heuristic speedup.

**Follow-up questions.** "How would you verify `totalTank >= 0` is the correct final check for whether *any* valid start exists?" (If total gas is less than total cost across the entire circuit, no starting point could possibly complete the circuit, regardless of where it starts — a separate, simpler necessary condition from the specific starting-index search.)

## 16. Coding/Practice Exercises

- Run the [existing practice code](../../practice/java/week-20/greedy/) yourself and confirm the same 10/10 assertions pass.
- Attempt to construct an exchange-argument proof, in your own words, for Partition Labels (Section 7, Problem 4) before reading Section 5's argument for a different problem — practice producing this kind of proof on a problem this chapter doesn't already derive it for.
- Compare Remove K Digits' monotonic-stack-greedy approach (Section 7, Problem 5) directly against [Stacks' own monotonic-stack chapter](stacks-and-monotonic-stack.md) — identify precisely which parts of the eviction logic are the same underlying mechanism versus which parts are specific to this problem's numeric-string context.

## 17. Debugging Exercises

**Symptom:** a resource-scheduling feature built around a greedy "always assign the most urgent request first" heuristic produces correct results in staging tests but occasionally produces a demonstrably suboptimal (not merely different, but provably worse) allocation in production under specific traffic patterns.

**Diagnose:** this is the real-world instance of Section 12/13's core warning — the greedy heuristic was likely never actually validated with an exchange argument, only observed to "work" on the traffic patterns staging happened to exercise. Walk through constructing an exchange argument for the specific greedy rule in question: assume an optimal allocation makes a different choice at some step, and check whether swapping in the greedy choice can be shown to never produce a worse outcome — if a counterexample is findable (and the production failure is itself evidence one exists), the greedy rule needs to be replaced with a provably correct alternative (often DP), not patched with special-case heuristics for the specific failure pattern observed.

## 18. Design Exercises

**Design constraint:** design a job-scheduling system that must select the maximum number of non-overlapping jobs from a set of candidate jobs, each with a fixed start and end time, to run on a single machine.

Design this as a greedy, sort-by-end-time approach, directly analogous to [Intervals' Minimum Number of Arrows](intervals-merging-and-sweep-line.md#4-core-concepts-l2) technique: sort jobs by end time, then greedily select each job whose start time is at or after the previously selected job's end time. State the exchange argument explicitly: for any optimal solution, the job ending earliest among all candidates can always be swapped in for whatever job the optimal solution selected first, without reducing the total count, since an earlier end time can only ever free up more room for subsequent selections, never less — the same underlying "greedy choice can only help never hurt" reasoning as every technique in this chapter.

## 19. Further Reading

- [Dynamic Programming](dynamic-programming.md) — the direct point of contrast: problems where a choice genuinely must be reconsidered based on later information, unlike the problems in this chapter.
- [Stacks and the Monotonic Stack](stacks-and-monotonic-stack.md) — the monotonic-eviction mechanism Remove K Digits (Section 4/5) combines with this chapter's greedy correctness principle.

## 20. Mastery Checklist

| Level | You can... | Verify with |
|---|---|---|
| L1 | Explain, in plain language, what makes an algorithm "greedy" and why it never reconsiders a choice | [Section 3](#3-foundation-l1) |
| L2 | Recognize the five greedy sub-patterns in this chapter (BFS-layer expansion, running-total reset, closed-form formula, last-occurrence tracking, monotonic-stack greedy) in a new problem | [Interview Question 2](#question-2--in-the-gas-station-problem-why-does-finding-one-failed-starting-point-let-you-skip-testing-every-station-between-the-failure-and-its-start-rather-than-checking-each-one-individually) |
| L3 | Construct or sketch an exchange argument proving a greedy approach's correctness for a new, unfamiliar problem | [Section 10's real verification](#10-performance-implications), [Section 5](#5-how-it-works-internally-l3) |
| L4 | Diagnose a real production scheduling regression as an unproven-greedy-heuristic failure (Section 17), and design a real job-scheduling system using a proven greedy technique deliberately (Section 18) | [Debugging Exercise](#17-debugging-exercises), [Section 13](#13-staffsystem-level-considerations-l4) |
