---
title: "Interview Question Bank — 03-data-structures-algorithms"
document_type: interview-question-bank
domain: 20-interview-preparation
status: in progress
version: 1.0
last_updated: 2026-09-13
related:
  - ../../03-data-structures-algorithms/INDEX.md
  - 02-java-language-core.md
  - ../../../00-project/interview-question-bank-plan.md
---

# Interview Question Bank — Data Structures and Algorithms

Part of the multi-domain compendium. See
[`06-databases.md`](06-databases.md) for the tier-explanation format and
`00-project/interview-question-bank-plan.md` for the full 22-domain plan and
sourcing discipline.

**Honest count for this domain:** 18 chapters yielded 36 deep questions. This domain
has **no Flashcards sections at all** — its chapters follow the Coding Interview
Standard template (recognition signals, brute force, optimized approach, complexity,
edge cases) rather than the canonical Handbook Chapter template, so there's no
lighter quick-fire layer to mine here, unlike the other domains covered so far. There
is also no dedicated Junior Fundamentals chapter in this domain (every chapter's
`## 15. Interview Questions` targets Senior/Staff depth directly, even though the
chapters themselves cover L1-L4) — Junior/Mid tiers below are honestly derived from
each chapter's own "Minimum acceptable answer" bar (a correct-but-suboptimal
solution) rather than invented as a separate, simpler question.

---

## Arrays, Two Pointers, and Sliding Window

### Q1 — Given a sorted array, find two numbers that sum to a target. What's the most efficient approach?

**Canonical treatment:** [§15, Q1](../../03-data-structures-algorithms/arrays-two-pointers-and-sliding-window.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** Produces a correct O(n) or better solution, even if it's the hash-set approach rather than two pointers.
- **Senior:** Names two pointers specifically as exploiting the sortedness the hash-set approach ignores, and can justify the pointer-movement rule.
- **Staff:** Generalizes to "what if the array weren't sorted, and sorting first cost O(n log n)?" — correctly compares against the hash-set approach's real trade-off.

### Q2 — Why is the Sliding Window Maximum solution O(n) and not O(n·k), given the nested-looking loops?

**Canonical treatment:** [§15, Q2](../../03-data-structures-algorithms/arrays-two-pointers-and-sliding-window.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** States the answer is O(n) and gestures at "each element is only processed a constant number of times," without a precise amortized argument.
- **Senior:** Explicitly walks through the "each index pushed once, popped at most once" accounting.
- **Staff:** Names the real code-review category this generalizes to — any loop whose worst-case single-iteration cost looks expensive but whose total summed cost is actually bounded.

---

## Advanced Structures — Segment Tree, Fenwick, Rolling Hash

### Q1 — When would you choose a Fenwick tree over a full segment tree, given a segment tree can do everything a Fenwick tree can?

**Canonical treatment:** [§ Interview Questions, Q1](../../03-data-structures-algorithms/advanced-structures-segment-tree-fenwick-rolling-hash.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** States that Fenwick trees are simpler, without precisely naming the aggregate-operation limitation.
- **Senior:** Names the specific limitation (prefix-sum-only, not arbitrary aggregates) as the actual reason to choose one over the other.
- **Staff:** Connects this to a real system-design decision — choosing the narrower, cheaper structure because production never needs the general one's extra capability.

### Q2 — Why does a rolling-hash substring matcher need a collision guard, but a DNA-sequence matcher using a similar-looking technique doesn't?

**Canonical treatment:** [§ Interview Questions, Q2](../../03-data-structures-algorithms/advanced-structures-segment-tree-fenwick-rolling-hash.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** States that one needs a collision guard and the other doesn't, without the lossless-vs-lossy distinction.
- **Senior:** Explains the exact bit-width math (4 symbols × 10 characters = 20 bits, fits losslessly) as the reason the DNA case has zero collision risk.
- **Staff:** Generalizes to a broader engineering discipline: never trust an unguarded hash match as proof of equality unless it's provably lossless.

---

## Backtracking and Pruning

### Q1 — Generate all permutations of an array with duplicates, without producing duplicate permutations.

**Canonical treatment:** [§ Interview Questions, Q1](../../03-data-structures-algorithms/backtracking-and-pruning.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** Produces a correct solution via a less elegant approach (generate then deduplicate with a `Set`).
- **Senior:** Produces the sort-plus-skip-condition approach directly, and can justify the `!used[i-1]` condition precisely.
- **Staff:** Explains the complexity trade-off between naive-then-deduplicate and pruning approaches.

### Q2 — Why does N-Queens use three boolean arrays for columns/diagonals instead of checking the board directly?

**Canonical treatment:** [§ Interview Questions, Q2](../../03-data-structures-algorithms/backtracking-and-pruning.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** Produces a correct O(n)-per-check solution, and can identify the check as an optimization target when prompted.
- **Senior:** Derives the `row - col` / `row + col` diagonal-identity facts directly and implements the O(1) version unprompted.
- **Staff:** Generalizes: any time a backtracking check scans already-committed state, ask whether it can be maintained incrementally instead.

---

## Binary Search and Search on Answer

### Q1 — Find the smallest `x` such that a monotonic `feasible(x)` returns true. How would you find it efficiently?

**Canonical treatment:** [§ Interview Questions, Q1](../../03-data-structures-algorithms/binary-search-and-search-on-answer.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** Produces the binary-search loop correctly, without explicitly naming monotonicity as the justification.
- **Senior:** States the monotonicity requirement explicitly and unprompted.
- **Staff:** Connects this to a real capacity-planning application — finding a minimum viable resource size via monotonic feasibility experiments.

### Q2 — Why does finding the minimum in a rotated sorted array need a different comparison than finding a specific target?

**Canonical treatment:** [§ Interview Questions, Q2](../../03-data-structures-algorithms/binary-search-and-search-on-answer.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** Recognizes the two are different problems, without precisely naming why.
- **Senior:** Derives the exact comparison (`nums[mid] > nums[hi]`) and explains why comparing against `hi` rather than `lo` matters.
- **Staff:** Names this as an instance of a general discipline: treating superficially similar problems as requiring independent correctness arguments.

---

## Bit Manipulation

### Q1 — Every element appears twice except one. Find it in O(1) extra space.

**Canonical treatment:** [§ Interview Questions, Q1](../../03-data-structures-algorithms/bit-manipulation.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** Produces a correct O(n)-space hash-set solution.
- **Senior:** Produces the XOR solution directly and states XOR's algebraic properties that make it work.
- **Staff:** Connects this to a real space-optimization scenario where this exact trade-off matters.

### Q2 — Why does `n & (n - 1)` clear the lowest set bit, and how does that help count set bits efficiently?

**Canonical treatment:** [§ Interview Questions, Q2](../../03-data-structures-algorithms/bit-manipulation.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** Knows the formula and that it clears the lowest set bit, without a precise bit-level derivation.
- **Senior:** Derives the borrow-propagation mechanism precisely, and states why this beats a fixed 32-iteration loop for sparse bit patterns.
- **Staff:** Connects this to a broader principle about algorithm cost being proportional to actual problem size rather than a fixed worst-case-sized loop.

---

## Concurrency Coding Problems

### Q1 — Implement a bounded blocking queue with concurrent producers/consumers, without `java.util.concurrent`'s built-ins.

**Canonical treatment:** [§ Interview Questions, Q1](../../03-data-structures-algorithms/concurrency-coding-problems.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** Produces a broadly correct implementation, even using `if` instead of `while` for the wait condition (a real, flagged bug).
- **Senior:** Uses `while` correctly and can explain spurious wakeup as the JLS-permitted reason it's required.
- **Staff:** Explains the `notify()`-vs-`notifyAll()` trade-off precisely and discusses when a more targeted design would be worth the added complexity.

### Q2 — Five philosophers, shared forks. How do you prevent deadlock?

**Canonical treatment:** [§ Interview Questions, Q2](../../03-data-structures-algorithms/concurrency-coding-problems.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** Recognizes the naive approach deadlocks and proposes some fix, even a less elegant one (a global semaphore).
- **Senior:** Produces the asymmetric-ordering fix specifically and explains why breaking the pattern for one participant is sufficient.
- **Staff:** Names the real, documented production incidents this failure mode causes and proposes a consistent lock-ordering convention.

---

## Design-Style Coding Problems

### Q1 — Design a data structure with O(1) get/put, evicting the least recently used entry at capacity.

**Canonical treatment:** [§ Interview Questions, Q1](../../03-data-structures-algorithms/design-style-coding-problems.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** Produces a correct design, even if reasoning about why doubly-linked (not singly) is required needs prompting.
- **Senior:** Explicitly states why a doubly-linked list is required — O(1) removal of an arbitrary node needs a reference to its predecessor.
- **Staff:** Extends directly to LFU Cache unprompted, identifying the additional `frequency -> LinkedHashSet<Node>` layer.

### Q2 — When would you choose a single list with a position pointer over two stacks for back/forward navigation history?

**Canonical treatment:** [§ Interview Questions, Q2](../../03-data-structures-algorithms/design-style-coding-problems.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** Produces a correct two-stack design, without recognizing the simpler alternative unprompted.
- **Senior:** Recognizes and produces the single-list-plus-pointer design directly.
- **Staff:** Generalizes the underlying recognition skill: any design problem whose two apparent "structures" are actually two views of one ordered collection is a candidate for this simplification.

---

## Dynamic Programming

### Q1 — Walk me through your process for solving a DP problem you haven't seen before.

**Canonical treatment:** [§ Interview Questions, Q1](../../03-data-structures-algorithms/dynamic-programming.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** Describes some version of "figure out the smaller subproblem" without a fully structured process.
- **Senior:** Explicitly separates the state-definition step from the recurrence-derivation step, and demonstrates it on an unfamiliar variant.
- **Staff:** Connects this to a broader principle — the same "identify the smaller repeated subproblem, cache it" thinking transfers directly to real caching/memoization system design decisions.

### Q2 — What's the difference between 0/1 knapsack and unbounded knapsack, and how does the code differ?

**Canonical treatment:** [§ Interview Questions, Q2](../../03-data-structures-algorithms/dynamic-programming.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** Correctly implements both, without articulating the precise loop-direction mechanism unprompted.
- **Senior:** States the loop-direction mechanism precisely and unprompted, with a concrete pair of worked examples.
- **Staff:** Generalizes to a broader principle about DP correctness whenever an in-place rolling-array update reads from the array it's writing to.

---

## Graphs — BFS, DFS, and Shortest Paths

### Q1 — Find the shortest path from a source to every other node in a weighted graph. What algorithm, and what assumption does it rely on?

**Canonical treatment:** [§ Interview Questions, Q1](../../03-data-structures-algorithms/graphs-bfs-dfs-and-shortest-paths.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** Names Dijkstra and produces a broadly correct implementation, without stating the non-negative-weight assumption unprompted.
- **Senior:** States the non-negative-weight assumption unprompted and explains concretely why a negative edge breaks the "once popped, final" guarantee.
- **Staff:** Names Bellman-Ford as the correct alternative for negative edges and states the real trade-off (O(V·E) vs O(E log V)).

### Q2 — Why does Union-Find with path compression have such a fast (near-constant) amortized time complexity?

**Canonical treatment:** [§ Interview Questions, Q2](../../03-data-structures-algorithms/graphs-bfs-dfs-and-shortest-paths.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** States that path compression makes `find()` fast over repeated calls, without naming the inverse Ackermann function specifically.
- **Senior:** Names the inverse Ackermann function and states, approximately, why it's "effectively constant" in practice.
- **Staff:** Connects this to a broader principle about amortized data structures and the specific shape of Union-Find's amortization.

---

## Greedy Algorithms and the Exchange Argument

### Q1 — How do you know when a greedy approach is actually correct, rather than just a plausible-looking heuristic?

**Canonical treatment:** [§ Interview Questions, Q1](../../03-data-structures-algorithms/greedy-and-the-exchange-argument.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** States that greedy needs "some kind of proof" it's correct, without describing the exchange-argument technique specifically.
- **Senior:** Applies the exchange argument concretely to a specific problem, not just in the abstract.
- **Staff:** Connects this to a real system-design risk — a production heuristic assumed safe without this rigor can silently misbehave on uncovered input patterns.

### Q2 — In Gas Station, why does one failed starting point let you skip testing every station between the failure and its start?

**Canonical treatment:** [§ Interview Questions, Q2](../../03-data-structures-algorithms/greedy-and-the-exchange-argument.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** States that stations between the start and failure "also fail," without the precise cumulative-sum argument.
- **Senior:** Derives the cumulative-sum argument precisely and unprompted.
- **Staff:** Generalizes this "if a prefix fails, no sub-prefix within it could have succeeded" reasoning as a broader technique.

---

## Hashing Patterns and Frequency Maps

### Q1 — How would you check if a string has all unique characters?

**Canonical treatment:** [§ Interview Questions, Q1](../../03-data-structures-algorithms/hashing-patterns-and-frequency-maps.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** Produces a correct `HashSet`-based O(n) solution.
- **Senior:** Names the bounded-alphabet space nuance unprompted and can produce the O(1)-extra-space bit-vector alternative for ASCII.
- **Staff:** Connects this to a real trade-off — a bit-vector approach is faster but far less flexible if the character set later needs to expand.

### Q2 — Why does Subarray Sum Equals K need a hash map instead of a sliding window, when Minimum Size Subarray Sum uses one successfully?

**Canonical treatment:** [§ Interview Questions, Q2](../../03-data-structures-algorithms/hashing-patterns-and-frequency-maps.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** States that negative numbers are the reason, without the precise monotonicity mechanism.
- **Senior:** Derives the algebraic identity (`prefixSum[i] == prefixSum[j] - k`) as the actual mechanism.
- **Staff:** Generalizes: whenever a technique's correctness secretly depends on an unstated assumption, checking the problem's actual constraints before committing is a transferable discipline.

---

## Heaps, Top-K, and K-Way Merge

### Q1 — Find the k largest elements in a stream, without storing the entire stream.

**Canonical treatment:** [§ Interview Questions, Q1](../../03-data-structures-algorithms/heaps-top-k-and-k-way-merge.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** Produces a correct solution, even "keep a sorted list of size k" rather than a heap specifically.
- **Senior:** Explains why a min-heap (not max-heap) is correct for tracking the largest k.
- **Staff:** Connects this directly to a real streaming/monitoring system design applying the same technique continuously.

### Q2 — Why does keeping the top k largest elements use a min-heap rather than a max-heap?

**Canonical treatment:** [§ Interview Questions, Q2](../../03-data-structures-algorithms/heaps-top-k-and-k-way-merge.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** States "a min-heap is used to know what to evict," without a fully precise justification.
- **Senior:** Generalizes the principle explicitly: whichever extreme needs *eviction* is the extreme the heap type should expose via its root.
- **Staff:** Connects this to a comparator-design discipline more broadly — deriving ordering from what `peek()`/`poll()` actually needs to return.

---

## Intervals — Merging and Sweep Line

### Q1 — Given meeting intervals, find the minimum number of conference rooms required.

**Canonical treatment:** [§ Interview Questions, Q1](../../03-data-structures-algorithms/intervals-merging-and-sweep-line.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** Produces a correct but less efficient solution (O(n²) pairwise overlap check).
- **Senior:** Produces the heap-based O(n log n) solution directly and explains why checking only the heap's minimum is sufficient.
- **Staff:** Connects this to a real capacity-planning application for determining minimum concurrent resource capacity.

### Q2 — Why does Minimum Number of Arrows to Burst Balloons sort by end coordinate rather than start?

**Canonical treatment:** [§ Interview Questions, Q2](../../03-data-structures-algorithms/intervals-merging-and-sweep-line.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** States that sorting by end is correct here, without a full justification.
- **Senior:** Explains the urgency argument precisely, and contrasts it against Meeting Rooms II's start-time sort.
- **Staff:** Generalizes the principle: the correct sort key is always determined by which endpoint determines "urgency" for the specific greedy decision being made.

---

## Linked Lists and In-Place Manipulation

### Q1 — How would you detect a cycle in a linked list, using O(1) extra space?

**Canonical treatment:** [§ Interview Questions, Q1](../../03-data-structures-algorithms/linked-lists-and-in-place-manipulation.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** Produces a correct cycle-detection solution, even the O(n)-space hash-set version.
- **Senior:** Produces the O(1)-space Floyd's-cycle version directly and explains the gap-closing correctness argument.
- **Staff:** Extends to finding where the cycle begins and can gesture at the number-theory reason the reset-and-restart step works.

### Q2 — Why does copying a linked list with a `random` pointer require two passes?

**Canonical treatment:** [§ Interview Questions, Q2](../../03-data-structures-algorithms/linked-lists-and-in-place-manipulation.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** States that `random` can point forward, without a precise mechanism for why one pass structurally can't work.
- **Senior:** Can also describe the O(1)-space interleaving alternative as a genuine follow-up technique.
- **Staff:** Generalizes the underlying principle — any single-pass algorithm with forward references needs a pre-pass or a pending-reference structure, a pattern recurring beyond linked lists (e.g., compiler symbol-table construction).

---

## Sorting Algorithms

### Q1 — What's QuickSort's worst-case time complexity, and what specific input triggers it?

**Canonical treatment:** [§ Interview Questions, Q1](../../03-data-structures-algorithms/sorting-algorithms.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** States "O(n²) worst case," without naming a specific triggering input.
- **Senior:** Correctly names sorted/reverse-sorted input as the trigger for a naive pivot strategy, and names randomized/median-of-three as the fix.
- **Staff:** Connects this to why Java's real `Arrays.sort(int[])` uses Dual-Pivot QuickSort and why object arrays use TimSort for its stability guarantee.

### Q2 — Is `Collections.sort()` in Java stable? How do you know, and why would it matter?

**Canonical treatment:** [§ Interview Questions, Q2](../../03-data-structures-algorithms/sorting-algorithms.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** States that `Collections.sort()` is stable, without naming TimSort specifically.
- **Senior:** Names TimSort and the Javadoc guarantee, with a concrete scenario where stability matters.
- **Staff:** Contrasts this with `Arrays.sort(int[])`'s different, non-stable algorithm, showing awareness that "Java's sort" is two genuinely different algorithms depending on element type.

---

## Stacks and the Monotonic Stack

### Q1 — Find the next greater element for every position, in O(n) time.

**Canonical treatment:** [§ Interview Questions, Q1](../../03-data-structures-algorithms/stacks-and-monotonic-stack.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** Produces a correct O(n²) brute force, then improves to O(n) with the monotonic-stack hint.
- **Senior:** Produces the O(n) monotonic-stack solution directly and states the amortized-cost argument when asked.
- **Staff:** Recognizes and names the transfer to Largest Rectangle in Histogram unprompted, identifying it as the same primitive applied to widths.

### Q2 — Why is the two-stack queue design O(1) amortized, when a single `pop()` can trigger an O(n) transfer?

**Canonical treatment:** [§ Interview Questions, Q2](../../03-data-structures-algorithms/stacks-and-monotonic-stack.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** States the answer is O(1) amortized and gestures at "each element only moves once," without the precise total-cost framing.
- **Senior:** Explicitly connects this to `ArrayList.add()`'s own amortized-O(1) resize argument as a structurally identical style of reasoning.
- **Staff:** Names the real system-design implication — amortized O(1) doesn't bound any single operation's worst-case latency, which can silently violate a strict per-request SLA.

---

## Trees, BST, and Traversal Patterns

### Q1 — How would you find the diameter of a binary tree?

**Canonical treatment:** [§ Interview Questions, Q1](../../03-data-structures-algorithms/trees-bst-and-traversal-patterns.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** Correctly identifies diameter isn't necessarily root-to-leaf, even if the first algorithm attempt is O(n²).
- **Senior:** Produces the O(n) single-pass solution directly, explicitly naming the return-value-vs-side-channel split.
- **Staff:** Generalizes the return-value-vs-side-channel distinction to other tree problems unprompted (e.g., maximum path sum).

### Q2 — Why does an in-order traversal of a BST visit nodes in sorted order, and how does that help find the k-th smallest efficiently?

**Canonical treatment:** [§ Interview Questions, Q2](../../03-data-structures-algorithms/trees-bst-and-traversal-patterns.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** States that in-order traversal of a BST is sorted and uses that fact correctly, without a full structural proof.
- **Senior:** Explains why the iterative version specifically enables early termination that recursion wouldn't get without extra plumbing.
- **Staff:** Connects this to a broader trade-off — iterative for early-termination value, recursive when full traversal is needed anyway.

---

## Tries and Prefix Structures

### Q1 — Why use a trie instead of a hash set for a dictionary supporting "does any word start with this prefix" queries?

**Canonical treatment:** [§ Interview Questions, Q1](../../03-data-structures-algorithms/tries-and-prefix-structures.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** States that a trie is "better for prefixes" without precisely quantifying the complexity difference.
- **Senior:** States both complexities precisely and explains the shared-path mechanism that makes the trie's cost independent of collection size.
- **Staff:** Connects this to a real system — an autocomplete feature where query latency must stay flat regardless of dictionary size.

### Q2 — How would you find the maximum XOR of any two numbers in an array, faster than checking every pair?

**Canonical treatment:** [§ Interview Questions, Q2](../../03-data-structures-algorithms/tries-and-prefix-structures.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** Recognizes this is faster than O(n²) brute force, without producing the full binary-trie solution unprompted.
- **Senior:** Produces the binary-trie solution and explains why greedily preferring the opposite bit at each level is provably optimal.
- **Staff:** Connects binary tries to a real system application — IP routing's longest-prefix-match.

---

## Related

- [`06-databases.md`](06-databases.md)
- [`02-java-collections.md`](02-java-collections.md), [`02-java-concurrency.md`](02-java-concurrency.md), [`02-java-jvm-internals.md`](02-java-jvm-internals.md), [`02-java-language-core.md`](02-java-language-core.md)
- [`00-project/interview-question-bank-plan.md`](../../../00-project/interview-question-bank-plan.md)
