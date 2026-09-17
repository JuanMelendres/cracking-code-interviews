---
title: "Coding Interview Pattern-Recognition Methodology"
slug: coding-interview-pattern-recognition-methodology
document_type: syllabus-topic
domain: 03-data-structures-algorithms
topic_id: T-2120
status: canonical
version: 1.0
last_updated: 2026-09-17
mastery_levels_covered: [L1, L2, L3, L4]
prerequisites:
  - ../01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md
related:
  - arrays-two-pointers-and-sliding-window.md
  - hashing-patterns-and-frequency-maps.md
  - binary-search-and-search-on-answer.md
  - linked-lists-and-in-place-manipulation.md
  - stacks-and-monotonic-stack.md
  - heaps-top-k-and-k-way-merge.md
  - trees-bst-and-traversal-patterns.md
  - graphs-bfs-dfs-and-shortest-paths.md
  - backtracking-and-pruning.md
  - dynamic-programming.md
  - intervals-merging-and-sweep-line.md
  - greedy-and-the-exchange-argument.md
  - bit-manipulation.md
  - tries-and-prefix-structures.md
  - design-style-coding-problems.md
  - sorting-algorithms.md
  - ../20-interview-preparation/coding/coding-interview-communication-protocol.md
practice: []
production_scenarios: []
interview_paths: [interview-emergency-sprint, junior-to-mid, senior-to-staff]
official_references: []
source_history: []
---

# Coding Interview Pattern-Recognition Methodology

> **Why this chapter exists.** This domain has 18 real, deep, pattern-specific chapters (`T-2101`–`T-2119`), each answering "how does this one pattern work." None of them answers a different, earlier question a reader facing an unfamiliar problem actually has first: **"which of these 18 patterns should I even be looking at?"** This chapter is that missing first step — a real methodology for going from a problem statement you've never seen to a specific pattern and data structure, plus a worked signal-spotting walkthrough on real, frequently-asked problems. It deliberately does not re-solve problems already fully worked in this domain's other chapters — every worked example below ends by naming the exact canonical chapter for the full solution, per this repository's own no-duplication rule.

This is a genuinely new topic — assigned `T-2120`, continuing this domain's own `T-2100`–`T-2199` reserved range (the next ID past `T-2119`, Sorting Algorithms, the domain's previous gap-audit addition). Placed first in this domain's reading order (see `.pages`), ahead of the pattern chapters themselves, since its entire purpose is to be read before them.

## 1. Why This Matters

An interviewer rarely hands you a problem that says "use a sliding window." They hand you a problem statement, and the single skill that separates a candidate who solves it in 15 minutes from one who solves it in 40 (or not at all) is **pattern recognition** — reading the constraints and the problem shape and correctly narrowing 18 possible patterns down to 1 or 2 candidates, fast, before writing any code. This is a learnable, mechanical skill, not innate talent: the same handful of signals (a sorted input, a "contiguous subarray" phrase, a small bounded `n`, an "optimal substructure" shape) recur across hundreds of different-sounding problems. This chapter makes that mapping explicit instead of leaving it to be absorbed implicitly after solving enough problems by trial and error.

## 2. Prerequisites

[Algorithmic Complexity and Big-O](../01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md) — this chapter's constraint-to-complexity heuristic (Section 5) assumes you can already read "O(n log n)" and reason about what it means for a given input size.

## 3. Foundation (L1) — the five-step method, in plain terms

Every coding-interview problem, regardless of which pattern it turns out to need, is worked the same five steps, in this order:

1. **Understand the problem, out loud.** Restate it in your own words. State the input type and size, the output type, and at least one edge case (empty input, a single element, duplicates, negative numbers) — not because you're stalling, but because the answer to "how big can `n` get?" directly tells you what complexity is even acceptable (Section 5 below turns this into a concrete rule).
2. **Look for signals.** A signal is a word or shape in the problem that points at a specific pattern — "contiguous subarray," "sorted," "shortest path," "count the ways," "top K." Section 4's table is this step made explicit: a lookup from signal to pattern.
3. **Get a brute-force solution first, even a bad one.** A working O(n²) or O(2ⁿ) solution you can state in one sentence ("try every pair," "try every subset") is worth more than silence — it proves you understand the problem, and it's the thing you optimize *from* in step 4.
4. **Optimize using the pattern the signals pointed to.** This is where the specific pattern chapter (Section 4's rightmost column) takes over — the technique that turns the brute force's redundant repeated work into something that reuses previous work instead.
5. **Trace it on a small example, then state the final complexity.** Pick a 3–5 element input, walk your optimized solution through it by hand, check it against the brute force's answer for the same input, and only then state the Big-O you actually achieved — not the one you hoped for.

This five-step shape is the "simple" method the pattern recognition below plugs into — steps 1, 3, and 5 are the same for every problem; steps 2 and 4 are what the rest of this chapter makes concrete.

## 4. Core Concepts (L2) — the signal-to-pattern table

This is the chapter's central deliverable: a real lookup table from problem signal to pattern, core data structure, typical achievable complexity, and the exact canonical chapter with the full technique and worked solutions. Ranked by real interview-weight-index (IWI) data from this project's own Master Topic Register (`00-project/knowledge-architecture-blueprint.md`), highest first — not a guess at popularity.

| Rank (IWI) | Signal / problem shape | Pattern | Core data structure | Typical complexity | Canonical chapter |
|---|---|---|---|---|---|
| 1 (6.3) | "contiguous subarray/substring," a running sum/count over a range, "find pair/triplet in a sorted array" | Two pointers / sliding window | Array, two indices or a `[left,right]` window | O(n) | [Arrays, Two Pointers, and Sliding Window](arrays-two-pointers-and-sliding-window.md) |
| 2 (6.25) | "shortest path" (unweighted), "connected components," "number of islands," "course schedule"/dependency order, "cheapest path" (weighted) | Graph traversal | Adjacency list; BFS/DFS/Union-Find/Dijkstra | O(V+E) or O(E log V) | [Graphs: BFS, DFS, Topological Sort, Dijkstra, Union-Find](graphs-bfs-dfs-and-shortest-paths.md) |
| 3 (6.2) | "design a data structure with O(1) `get`/`put`," "implement an iterator" | Design-style composition | Combined structures (e.g., `HashMap` + doubly linked list for an LRU cache) | Usually O(1) amortized per operation | [Design-Style Coding Problems](design-style-coding-problems.md) |
| 4 (5.85) | "count the number of ways," "minimum/maximum cost to reach," "optimal substructure," a knapsack-shaped constraint (capacity + items) | Dynamic programming | 1D/2D memo table (array or map) | O(n), O(n·k), or O(n²), depending on state space | [Dynamic Programming](dynamic-programming.md) |
| 5 (5.8) | "binary tree," BST validate/insert/search, "lowest common ancestor," "serialize/deserialize a tree" | Tree traversal | Tree/BST; recursion or an explicit stack | O(n) (O(log n) for balanced-BST search) | [Trees, BST, and Traversal Patterns](trees-bst-and-traversal-patterns.md) |
| 6 (5.75) | "thread-safe," "producer-consumer," "implement a rate limiter" under concurrent access | Concurrency coding | Locks, atomics, or concurrent collections | Depends — correctness under concurrency is the real target, not just Big-O | [Concurrency Coding Problems](concurrency-coding-problems.md) |
| 7 (5.5) | "kth largest/smallest," "top K frequent," "merge K sorted lists," "median of a stream" | Heap / priority queue | `PriorityQueue` (min-heap or max-heap) | O(n log k) | [Heaps, Top-K, and K-Way Merge](heaps-top-k-and-k-way-merge.md) |
| 8 (5.4) | Input is sorted (or can be), "find X in a rotated sorted array," "minimize/maximize a feasible value" (search on the answer, not the array) | Binary search | Sorted array, or an abstract answer-space | O(log n) | [Binary Search, Including Search-on-Answer](binary-search-and-search-on-answer.md) |
| 9 (5.2) | "next greater/smaller element," "valid parentheses," "daily temperatures"-style "how far until a bigger value" | (Monotonic) stack | Stack, kept monotonic (increasing or decreasing) | O(n) amortized | [Stacks and the Monotonic Stack](stacks-and-monotonic-stack.md) |
| 10 (5.1) | "all combinations/permutations/subsets," N-Queens, word search on a grid, "generate every valid X" | Backtracking | Recursion + explicit choice/undo state | Exponential, bounded by real pruning | [Backtracking and Pruning](backtracking-and-pruning.md) |
| 11 (5.0) | Reverse/detect a cycle in/merge/remove the Nth-from-end of a linked list | Linked-list manipulation | Linked list, fast/slow (or dummy-head) pointers | O(n) | [Linked Lists and In-Place Manipulation](linked-lists-and-in-place-manipulation.md) |
| 11 (5.0, tie) | "merge overlapping ranges," "meeting rooms," "insert an interval" | Intervals / sweep line | Array of intervals, sorted by start (or a sweep over event points) | O(n log n) | [Intervals, Merging, and Sweep Line](intervals-merging-and-sweep-line.md) |
| 13 (4.8) | "activity selection," a locally-optimal choice that's provably never wrong ("always pick the earliest deadline") | Greedy | Array + sort; correctness needs an explicit exchange argument | O(n log n) | [Greedy and the Exchange Argument](greedy-and-the-exchange-argument.md) |
| 14 (4.7) | "autocomplete," "longest common prefix among many words," "does any word start with this prefix" | Trie | Prefix tree (trie) | O(L) per word (L = word length) | [Tries and Prefix Structures](tries-and-prefix-structures.md) |
| 15 (4.4) | "single number" (XOR trick), count set bits, "generate every subset via a bitmask" | Bit manipulation | `int`/`long` used as a bitset | O(n), or O(2ⁿ) for bitmask DP | [Bit Manipulation](bit-manipulation.md) |
| 16 (4.2) | "range sum/update query," "count of smaller elements after self," large-scale substring matching | Advanced structures | Segment tree, Fenwick tree, or rolling hash | O(log n) per operation | [Advanced Structures](advanced-structures-segment-tree-fenwick-rolling-hash.md) |
| — | "count duplicates," "group by," "first unique character," "is this an anagram of that" | Hashing / frequency map | `HashMap`/`HashSet` | O(n) | [Hashing Patterns and Frequency Maps](hashing-patterns-and-frequency-maps.md) |
| — | "sort this," or "is the naive sort here actually the bottleneck" | Sorting fundamentals | Array | O(n log n) comparison-based lower bound | [Sorting Algorithms](sorting-algorithms.md) |

Hashing and Sorting aren't ranked in the IWI table above because the Master Topic Register scores them as foundational primitives other patterns build on (T-1403's own register entry is IWI 6.0, genuinely high — it's left unranked in this list only because "does this problem involve counting/grouping/deduplication" is a signal that co-occurs inside most of the other 16 rows rather than standing alone as its own problem category).

## 5. How It Works Internally (L3) — reading constraints as a complexity budget

**The single most underused signal is the constraint on `n` itself** — competitive-programming and interview problems alike are usually solvable in roughly 10⁸ basic operations within a typical time limit, so the stated bound on input size tells you, before you write a line of code, roughly which row of the table above you're even allowed to land on:

| Constraint on `n` | Complexity this budget roughly allows | What that rules in |
|---|---|---|
| `n ≤ ~20` | O(2ⁿ) or O(n!) | Brute-force subsets/permutations, unpruned backtracking |
| `n ≤ ~500` | O(n³) | Triple-nested loops, naive DP over two dimensions plus a scan |
| `n ≤ ~5,000` | O(n²) | Naive pairwise comparison, unoptimized DP tables |
| `n ≤ ~10⁶` | O(n log n) or O(n) | Sorting-based approaches, single-pass hashing/two-pointer scans |
| `n` unbounded, streaming, or `n ≤ ~10⁹` | O(log n) or O(1) | Binary search on the answer, math/formula-based solutions |

This is a heuristic, not a proof — a problem with a tight per-operation constant or an unusual time limit can shift these bounds — but it's a genuinely reliable first filter: if `n ≤ 20`, spending interview time hunting for an O(n) trick is very likely solving the wrong problem, and if `n ≤ 10⁹`, an O(n²) idea can be discarded on sight without even tracing it.

**A second internal signal**: whether the problem asks for *a* valid answer versus *the optimal* answer changes which pattern applies even when the surface phrasing looks similar. "Find a path" (existence) is a BFS/DFS reachability question; "find the shortest path" (optimal, unweighted) is still BFS, but now tracking distance; "find the cheapest path" (optimal, weighted) needs Dijkstra, a genuinely different algorithm — the word "cheapest" or an edge weight in the problem statement is the signal that rules out plain BFS.

## 6. Practical Usage — three worked signal-spotting walkthroughs

Each walkthrough below stops at "which pattern, and why" — the full step-by-step solution already exists in the named canonical chapter, and reproducing it here would duplicate it rather than teach something new. What's new here is the *reasoning trace* from problem statement to pattern choice, which those chapters (correctly) don't re-derive every time since they already assume you've arrived at the right pattern.

**Walkthrough 1 — "Given an array of integers, find two numbers that add up to a target" (Two Sum).**
Step 1 (understand): array input, one target integer, output is a pair of indices or values, `n` unstated but treat as potentially large.
Step 2 (signals): "two numbers," "add up to a target" — this is a pair-lookup problem. If the array is already sorted, "two numbers summing to X" is the canonical opposite-direction two-pointer signal (Section 4, row 1). If it is *not* sorted and sorting isn't free (you need original indices preserved), the signal shifts to "for each element, have I already seen its complement" — a hashing/frequency-map problem (Section 4, hashing row) instead, trading the O(n log n) sort for O(n) time and O(n) extra space.
Step 3 (brute force): check every pair, O(n²) — one sentence, as prescribed in Section 3.
Step 4 (optimize): pick two-pointer or hash-map per the signal above.
→ Full solutions and complexity proofs: [Arrays, Two Pointers, and Sliding Window](arrays-two-pointers-and-sliding-window.md) and [Hashing Patterns and Frequency Maps](hashing-patterns-and-frequency-maps.md).

**Walkthrough 2 — "Given a grid of 1s (land) and 0s (water), count the number of islands" (Number of Islands).**
Step 1 (understand): 2D grid input, output is a single integer count, edge cases include an empty grid and a grid of all water.
Step 2 (signals): "count the number of [connected groups]" in a grid is the canonical connected-components signal — a grid is just a graph where each cell is a node and its up/down/left/right neighbors are edges. "Count the number of islands" is structurally the same question as "count the number of connected components," which Section 4's graph row covers directly.
Step 3 (brute force): for each unvisited land cell, flood-fill (BFS or DFS) to mark its whole island visited, incrementing a counter once per flood-fill started — this *is* close to the optimal solution already, because graph traversal is naturally linear; there usually isn't a slower "obvious" brute force to state first here, which is itself a useful thing to say out loud in the interview (not every problem has a meaningfully worse brute force to warm up with).
Step 4 (optimize): none needed beyond picking BFS or DFS and a visited-set, per the canonical chapter.
→ Full solution and complexity proof: [Graphs: BFS, DFS, Topological Sort, Dijkstra, Union-Find](graphs-bfs-dfs-and-shortest-paths.md).

**Walkthrough 3 — "Given a set of coin denominations and a target amount, find the minimum number of coins to make that amount" (Coin Change).**
Step 1 (understand): array of coin values, one integer target, output is a single integer (or "impossible").
Step 2 (signals): "minimum number of [coins] to reach [a target]" is the canonical dynamic-programming signal — it has **optimal substructure** (the minimum coins to make amount `k` is `1 + min` over every coin `c` of the minimum coins to make `k - c`) and **overlapping subproblems** (the sub-amount `k - c` recurs across many different top-level choices), the two properties Section 4's DP row names directly.
Step 3 (brute force): try every combination of coins recursively without memoizing repeated sub-amounts — exponential, and stating *why* it's exponential (the same sub-amount gets recomputed from scratch every time it's reached via a different combination of coins) is exactly the observation that motivates step 4.
Step 4 (optimize): memoize by remaining amount (top-down) or build a 1D table indexed by amount from 0 up (bottom-up) — a state space of size `target`, per coin, giving O(amount × number of coins).
→ Full solution and complexity proof: [Dynamic Programming](dynamic-programming.md).

## 7. Examples

The three walkthroughs in Section 6 are this chapter's worked examples by design — each one demonstrates the signal-spotting reasoning on a real, frequently-asked problem without duplicating the canonical chapter's own full solution and Java implementation.

## 8. Common Mistakes

- **Jumping straight to code before finishing step 1 (understand) and step 2 (signals).** The single most common failure mode in a coding interview isn't "didn't know the pattern" — it's "started coding a pattern that doesn't fit before finishing reading the constraints," discovering the mismatch halfway through, and running out of time redoing it.
- **Forcing a recently-practiced pattern onto a problem that doesn't actually match its signals** — solved five sliding-window problems in a row and reaching for a window on the sixth problem out of momentum rather than because the signals (Section 4) actually point there.
- **Ignoring the stated constraint on `n` entirely.** A candidate who writes an O(n²) solution for `n ≤ 10⁶` (or spends 20 minutes hunting for an O(n) trick when `n ≤ 20` makes brute force trivially fine) is demonstrating that Section 5's heuristic wasn't applied, not that the pattern itself was hard.
- **Treating "I've seen this exact problem before" as the strategy.** Memorized solutions to a fixed problem list don't transfer to the inevitable variation an interviewer introduces — recognizing the *signal*, not the exact problem, is what generalizes.

## 9. Edge Cases

The "edge case" this chapter's own methodology needs to handle is **ambiguous or conflicting signals** — a real, common situation, not a rare one:

- A problem can genuinely match two patterns at once (Walkthrough 1's Two Sum matches both two-pointer *and* hashing, depending on whether the array is sorted and whether original indices matter) — when this happens, say both candidates out loud and state the deciding factor, rather than silently picking one.
- A problem's surface wording can point at the wrong pattern — "shortest path" strongly suggests BFS, but if edges are weighted, BFS silently gives a wrong answer rather than erroring, since BFS's shortest-path guarantee specifically requires unweighted edges (Section 5's second signal exists precisely to catch this).
- A problem with no clean pattern match at all (some design-style problems, Section 4's row 3) is itself a signal — reach for "what data structure gives me the operation complexity this needs" directly, rather than continuing to search the table for a pattern that isn't there.

## 10. Performance Implications

This chapter's own contribution to performance is choosing the *right* complexity class before writing code, not micro-optimizing a chosen algorithm's constant factor — Section 5's constraint-to-complexity table exists specifically so that complexity class is chosen deliberately, from the stated input bound, rather than discovered too late (a solution that times out) or over-engineered too early (spending backtracking-level effort on a problem `n ≤ 20` never needed more than brute force for).

## 11. Trade-offs

**Speed of pattern identification vs. certainty.** Committing to a pattern within the first minute of reading a problem is fast but risks the ambiguous-signal trap (Section 9); spending five minutes methodically checking every row of Section 4's table against the problem is more certain but eats real interview time. The five-step method (Section 3) resolves this by design: step 3 (a brute force, stated fast) buys time to let step 2's signal-matching settle correctly before step 4 commits to an optimized pattern.

## 12. Senior-Level Considerations (L3)

A Senior candidate is expected to narrate the *signal-to-pattern* reasoning out loud, not just arrive at the right pattern silently — explicitly naming "this is a connected-components problem because [reason]" before writing BFS code demonstrates the same transferable skill this whole chapter teaches, and is exactly what [Coding Interview Communication Protocol](../20-interview-preparation/coding/coding-interview-communication-protocol.md) (T-1419, the register's own highest-IWI item at 6.4) separately covers in depth — that chapter is the *how to narrate*, this chapter is the *what to narrate about*.

## 13. Staff/System-Level Considerations (L4)

Pattern recognition isn't only an interview skill — the same constraint-to-complexity reasoning (Section 5) is exactly what a Staff engineer applies when choosing a real production algorithm: an O(n²) deduplication pass that was fine at 10,000 records becomes a real production incident at 10,000,000, and recognizing "this is the same signal as Section 4's hashing row, and the input size crossed Section 5's O(n²)-is-no-longer-safe threshold" is the identical mental move, applied to a production code review instead of a whiteboard.

## 14. Production Scenarios

No dedicated production-cookbook entry is cited here (an honest gap, not a placeholder): this chapter is a methodology synthesis over the 18 pattern chapters, several of which (see `03-data-structures-algorithms/INDEX.md`'s own documented rationale) already explain, per pattern, why they don't cite a production scenario either — inventing one here would contradict `production-cookbook/README.md`'s rule against writing a scenario that wasn't first worked out from real practice code.

## 15. Interview Questions

**Q1. "Walk me through how you'd approach a coding problem you've never seen before."**
*Why interviewers ask it:* this tests process, not memorized answers — it's often asked before the actual coding problem, specifically to see whether a candidate has a repeatable method or is hoping to pattern-match from memory alone.
*Minimum acceptable answer:* mentions reading the problem carefully and starting with a brute force.
*Strong Senior answer:* states the five-step method by name (understand → signals → brute force → optimize → verify), and can point at specific signals (constraint size, keywords like "contiguous" or "shortest path") that route to specific patterns — essentially summarizing Sections 3–5 of this chapter in their own words.
*Staff-level extension:* connects the same method to real production algorithm selection (Section 13) — this isn't an interview-only skill, it's how they'd evaluate a teammate's PR that introduces an O(n²) loop over a collection whose size assumptions might not hold in a year.
*Common mistakes:* naming a specific data structure ("I'd use a hash map") before restating the problem or its constraints; skipping the brute-force step entirely, leaving the interviewer no signal that the candidate understands the problem before optimizing it.
*Follow-ups:* "What if two patterns both seem to fit?" (Section 9); "How do you decide if brute force is actually good enough?" (Section 5's constraint table).
*Evaluation criteria (1–5):* 1 — jumps to code with no stated process; 3 — states brute-force-then-optimize but can't name concrete signals; 5 — names specific signals, ties them to the constraint on `n`, and can handle the interviewer naming a second, similar problem on the spot.

**Q2. "A teammate's code passes all tests but times out on the hidden large-input test case. How do you debug that?"**
*Why interviewers ask it:* tests whether "pattern recognition" is understood as an ongoing diagnostic skill, not just a one-time step 2 decision made at the start.
*Minimum acceptable answer:* suggests checking the algorithm's Big-O against the input size.
*Strong Senior answer:* walks through Section 5's constraint-to-complexity table directly — asks what the actual input bound is, computes what complexity class that bound requires, and compares it against the submitted solution's real complexity to locate the mismatch precisely (e.g., "this is O(n²) but `n` can be 10⁶, so it needs to be O(n log n) or better — that's a hashing or sorting-based fix, not a rewrite from scratch").
*Staff-level extension:* frames this as exactly the same review a Staff engineer performs on a production PR before merge, not a skill that only applies to interview timers.
*Common mistakes:* guessing at "probably needs caching" without first establishing what complexity class the input size actually demands.
*Follow-ups:* "How would you have caught this before submitting?" (Section 5, applied proactively rather than reactively).
*Evaluation criteria (1–5):* 1 — no structured diagnostic approach; 3 — checks Big-O but doesn't connect it back to the stated input bound; 5 — reconstructs the exact required complexity class from the bound and names the specific pattern that achieves it.

## 16. Coding/Practice Exercises

- For five problems you've already solved from other chapters in this domain, write down (without looking at the solution) which row of Section 4's table you'd match them to, and why — then check against the chapter they actually live in.
- Take a problem you don't recognize (any unfamiliar LeetCode-style prompt) and run only steps 1–2 of Section 3's method: restate it, state the constraint on `n`, and name your top two pattern candidates from Section 4 — stop before solving it. This isolates the recognition skill from the solving skill.

## 17. Debugging Exercises

- Given a real solution that times out, apply Section 5's constraint table to compute the complexity the input bound actually requires, then compare that against the submitted solution's real Big-O to state precisely which pattern substitution would close the gap (per Interview Question 2 above).

## 18. Design Exercises

- Not applicable in the usual system-design sense — this chapter's "design" analog is Section 9's ambiguous-signal handling: given a problem statement engineered to plausibly match two different rows of Section 4's table, practice stating both candidates and the specific deciding factor between them out loud.

## 19. Further Reading

- [Algorithmic Complexity and Big-O](../01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md) — the complexity-class vocabulary Section 5's heuristic depends on.
- [Coding Interview Communication Protocol](../20-interview-preparation/coding/coding-interview-communication-protocol.md) (T-1419) — how to narrate the reasoning this chapter teaches you to have.
- Every row of Section 4's table links to that pattern's own canonical chapter — the natural next read once a problem has been routed to it.

## 20. Mastery Checklist

- [ ] I can state the five-step method (Section 3) from memory, in my own words.
- [ ] Given an unfamiliar problem statement, I can name at least one concrete signal in it and match it to a row in Section 4's table within a minute.
- [ ] I can compute, from a stated constraint on `n`, roughly which complexity class is required (Section 5) without looking up the table.
- [ ] I can name a real problem where two patterns both plausibly apply, and state the deciding factor between them (Section 9).
- [ ] I can connect this same reasoning to a production code-review scenario, not just an interview whiteboard (Section 13).
