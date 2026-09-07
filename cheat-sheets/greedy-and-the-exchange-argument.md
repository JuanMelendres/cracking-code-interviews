---
title: "Cheat Sheet: Greedy and the Exchange Argument"
slug: greedy-and-the-exchange-argument
document_type: cheat-sheet
domain: 03-data-structures-algorithms
topic_id: T-2112
canonical: ../syllabus/03-data-structures-algorithms/greedy-and-the-exchange-argument.md
last_updated: 2026-09-06
---

# Greedy and the Exchange Argument

**Canonical chapter:** [`syllabus/03-data-structures-algorithms/greedy-and-the-exchange-argument.md`](../syllabus/03-data-structures-algorithms/greedy-and-the-exchange-argument.md)

## Core Mental Model

A greedy algorithm makes the choice that looks best right now, commits to it permanently, and never backtracks. This works only when the locally-best choice at each step is provably part of *some* globally optimal solution — no future information could ever make an earlier greedy choice look wrong in hindsight. Unlike DP, where "did I memoize correctly" is a mechanical check, greedy correctness requires an actual argument, and being able to produce that argument — not just the code — is what separates a strong greedy answer from a lucky one.

## Essential Definitions

- **Exchange argument** — assume some optimal solution makes a different choice than the greedy one at some step; show that swapping in the greedy choice instead produces an equally good or strictly better solution.
- **Greedy vs. DP** — DP is the natural point of contrast: DP problems require considering multiple options and remembering results; a genuine greedy problem's defining property is that reconsideration is provably never necessary.

## Recognition Signals / When to Use This Pattern

| Signal in the problem | Technique | Complexity |
|---|---|---|
| Reachability via jumps where farther reach can only help, never hurt | Implicit BFS-layer expansion, commit to farthest reach in the current layer | O(n) |
| "If starting point X fails partway, nothing before the failure point could have worked either" | Running-total-reset greedy — jump the candidate start past the failed range | O(n) vs. O(n²) brute force |
| Answer derivable from a small number of aggregate facts (max frequency, count at max) | Closed-form greedy formula, no simulation | O(n) |
| A partition/boundary that must cover every element's last occurrence seen so far | Last-occurrence-tracking greedy | O(n) |
| "Remove k digits/elements to make the smallest possible result" | Monotonic-stack greedy: evict anything larger than an incoming smaller value | O(n) |

## Common Mistakes

- Applying a greedy approach without an exchange argument, based purely on "the code seems to work" on a few test cases — a greedy solution that hasn't been proven correct can pass small tests by luck and fail on adversarial input.
- Missing a `Math.max(formula, totalCount)` guard in a closed-form greedy formula — the formula alone may assume idle/wasted capacity is needed, which isn't true once enough other items exist to fill every slot.
- Forgetting to strip leading zeros (or an equivalent normalization step) from a greedy result — the greedy removal logic alone doesn't prevent an invalid-looking output.

## Complexity Reference

- Jump Game II: O(n) time, O(1) space — better than an O(n²) DP formulation.
- Gas Station: O(n) time, O(1) space, versus O(n²) brute force.
- Task Scheduler: O(tasks.length + 26), effectively O(n).
- Partition Labels: O(n) time, O(1) space.
- Remove K Digits: O(n) time, O(n) space.

## Interview Answer Skeleton

**30-sec:** A greedy algorithm commits to the locally-best choice at every step and never reconsiders; it's only valid when an exchange argument can show that choice is never worse than any alternative — otherwise DP (which considers and remembers multiple options) is needed instead.

**2-min:** Before committing to a greedy approach, attempt to state the exchange argument explicitly: assume an optimal solution differs from the greedy choice at some step, then show swapping in the greedy choice never hurts. For example, in Gas Station, if the tank goes negative starting from `i` at `j`, every station strictly between `i` and `j` must also fail — its cumulative sum to `j` is even more negative than starting from `i` — so the search can jump straight to `j+1` instead of testing every intermediate index.

**Whiteboard:** Draw the Gas Station cumulative-sum line: plot the running total of `gas[i] - cost[i]` across all stations, mark where it first goes negative, and shade the region between the current candidate start and that failure point to show why every station in that shaded region is provably also a failing start.

**Staff-level framing:** Recognizing when a system's scheduling or resource-allocation logic can be greedy (fast and simple) versus when it genuinely requires a more expensive optimization approach (DP, ILP, full search) is a real architecture decision — a system incorrectly built around an unproven greedy assumption produces subtly wrong results under specific, possibly rare, input patterns, exactly analogous to a greedy interview solution that passes easy cases but fails an adversarial one.

## Production Warning Signs

- **Symptom:** a resource-scheduling feature built around a greedy "always assign the most urgent request first" heuristic produces correct results in staging but occasionally produces a demonstrably suboptimal allocation in production under specific traffic patterns.
- **Diagnose:** the greedy heuristic was likely never validated with an exchange argument, only observed to "work" on the traffic patterns staging happened to exercise. Construct an exchange argument for the specific greedy rule: assume an optimal allocation makes a different choice at some step, and check whether swapping in the greedy choice can be shown to never produce a worse outcome. If a counterexample is findable, the rule needs to be replaced with a provably correct alternative (often DP), not patched with special-case heuristics.

## Related

- syllabus/03-data-structures-algorithms/dynamic-programming.md
- syllabus/03-data-structures-algorithms/stacks-and-monotonic-stack.md
