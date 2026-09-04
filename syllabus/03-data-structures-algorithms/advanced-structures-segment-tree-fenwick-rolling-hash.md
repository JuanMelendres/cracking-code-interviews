---
title: "Advanced Structures: Segment Tree, Fenwick Tree, and Rolling Hash"
slug: advanced-structures-segment-tree-fenwick-rolling-hash
document_type: syllabus-topic
domain: 03-data-structures-algorithms
topic_id: T-2117
status: draft
version: 1.0
last_updated: 2026-09-03
mastery_levels_covered: [L1, L2, L3, L4]
prerequisites:
  - binary-search-and-search-on-answer.md
  - hashing-patterns-and-frequency-maps.md
related:
  - binary-search-and-search-on-answer.md
  - hashing-patterns-and-frequency-maps.md
  - trees-bst-and-traversal-patterns.md
practice: ../../practice/java/advanced-structures/
production_scenarios: []
interview_paths: [senior-to-staff]
official_references: []
source_history:
  - practice/java/advanced-structures/README.md
---

# Advanced Structures: Segment Tree, Fenwick Tree, and Rolling Hash

> **Provenance.** The eight worked problems and retrospectives in Sections 7 and 15 are elevated from `practice/java/advanced-structures/README.md` — real, compiled, executed code (`practice/java/advanced-structures/{fenwick,segment-tree,rolling-hash}/`), re-verified on OpenJDK 21.0.12 while writing this chapter (12/12, 13/13, and 8/8 assertions passing, 33 total).

This is Master Topic Register **T-1418** (IWI 4.2, occasional frequency). **This entire domain is explicitly outside Plan C's core roadmap** — `00-project/learning-roadmap.md` §5 deliberately excludes the Expert tier these three structures belong to, as "the most common misallocation in senior interview prep." This chapter exists because the underlying practice code was already built to close this gap anyway; treat it as recognition-level polish on an already-complete core program, not a prerequisite for it.

## 1. Why This Matters

These three structures answer a shared question — "update part of a large collection, then query an aggregate over part of it, repeatedly, without recomputing from scratch each time" — using more sophisticated tree- or hash-based structures than the basic array or hash map techniques covered elsewhere in this domain. They appear far less often in interviews than the patterns in this domain's other chapters (this pattern's IWI of 4.2 is the lowest of all 18 D14 patterns), but recognizing them cold when they *do* appear — usually at Staff-level loops probing for genuine depth — is a real, if occasional, differentiator.

## 2. Prerequisites

[Binary Search](binary-search-and-search-on-answer.md) — Longest Duplicate Substring (Section 7, Problem 8) binary-searches over candidate lengths, reusing that chapter's monotonic-feasibility template directly. [Hashing Patterns](hashing-patterns-and-frequency-maps.md) — coordinate compression (Section 4) maps arbitrary values into a dense hash-map-backed rank space.

## 3. Foundation (L1)

**A Fenwick tree (Binary Indexed Tree, BIT) supports two operations over a fixed-size array in O(log n): point update (add a value at an index) and prefix-sum query (sum of everything up to an index)** — dramatically faster than an O(n) prefix re-scan after every update, at a much smaller constant-factor cost than the more general segment tree.

**A segment tree covers a range with a binary tree of sub-ranges, each internal node summarizing its two children (sum, max, or another combinable aggregate)**, so both point/range updates and range queries run in O(log n) instead of O(n) — a strict generalization of what a Fenwick tree does, supporting a wider range of aggregate operations at a somewhat higher constant-factor cost.

**A rolling hash recomputes a fixed- or variable-length window's hash in O(1) as the window slides one position**, instead of rehashing the entire window from scratch on every slide — the same amortized-per-step idea behind a sliding window's O(1) incremental update, applied to a hash computation specifically.

## 4. Core Concepts (L2)

**Coordinate compression, the technique underlying all three Fenwick-tree problems** (Section 7, Problems 1–3): a Fenwick tree indexes into a dense, contiguous `1..m` range — but real problem inputs are arbitrary integers, possibly sparse and widely spread out. Mapping each distinct input value to its rank among all distinct values (via a sorted, deduplicated array and binary search) converts an arbitrary integer domain into the dense index space a Fenwick tree requires, at the cost of one O(n log n) sort upfront.

**Static bottom-up array segment tree vs. dynamic lazily-created segment tree** (Section 7, Problems 4 and 5): when the underlying domain is small and fully known upfront, a flat, iterative array-based segment tree (leaves at fixed positions, no recursion) is the simplest and fastest implementation. When the domain is enormous (`[0, 1e9]`) and not knowable upfront, a dynamic tree creates child nodes lazily, only along the paths actual updates touch — trading a small per-node overhead for supporting a domain far too large to ever materialize as a flat array.

**Skipping true lazy propagation when a shortcut's precondition holds** (The Skyline Problem, Section 7 Problem 6): real lazy propagation (pushing a pending update down to children eagerly) is only needed when a range could be *read* before every pending update below it has been fully applied. When every leaf is read exactly once, in a single final pass, accumulating the maximum value along the root-to-leaf path during that one read achieves the same correct result as eager pushdown would have, without ever needing to implement it — a real, but precondition-dependent, optimization.

**Exact bit-packing vs. probabilistic polynomial hashing** (Section 7, Problems 7 and 8): when an alphabet is small enough that a fixed-length window fits losslessly into a machine word (4 DNA bases into 2 bits each, a 10-character window into 20 bits), the resulting "hash" is actually an exact, collision-free encoding — no collision guard needed at all. When the alphabet is too large for this (26 letters, a variable-length window), a real polynomial rolling hash with modular arithmetic is required instead, and collisions become possible — requiring an explicit, real character-by-character comparison to confirm any hash match before trusting it.

## 5. How It Works Internally (L3)

**Reverse Pairs' real, documented directional bug** (Section 7, Problem 2): the first implementation queried "already-inserted values `v` with `2*v < nums[i]`" — a plausible-looking mirror of the problem's doubling condition that is actually backwards. LeetCode's definition requires the *earlier* index's value to be the large one (`nums[i] > 2 * nums[j]` for `i < j`), not the later one. That backwards version passed 2 of 4 test cases — including, misleadingly, the very first one checked — and silently produced wrong counts on an ascending array and a mixed one, caught only by the full test suite, not by inspection. The fix reframes the query as "how many already-inserted values are `> 2 * nums[j]`," computed as `total inserted - count(<= threshold)` — correctly placing the doubling on the *later* index's value while counting the *earlier* index's values against it.

**My Calendar III's single-field dual-purpose invariant** (Section 7, Problem 5): `node.val` is defined to always mean "the current max overlap count anywhere under this node, including this node's own pending `lazy` contribution" — which is what makes the root's `val` directly readable as the global answer after every booking, with no separate query pass ever needed. Nodes are created lazily, only along the O(log(range)) path a given booking's range actually touches, keeping the tree sparse regardless of how enormous the coordinate domain (`[0, 1e9]`) is.

**Longest Duplicate Substring's binary-search-over-length reduction** (Section 7, Problem 8): "does a duplicate of length `L` exist" is monotonic in `L` — if one exists at length `L`, a duplicate also trivially exists at every shorter length (just truncate it). This monotonicity licenses binary-searching over candidate lengths (exactly [Binary Search's](binary-search-and-search-on-answer.md#4-core-concepts-l2) answer-space technique), reducing the problem to O(log n) feasibility checks, each an O(n) rolling-hash scan — turning what would otherwise be an O(n²) or worse brute force into O(n log n) expected. The collision guard (an explicit O(len) character comparison before trusting any hash match) is not optional polish here: a real polynomial rolling hash over a 26-letter alphabet with a single modulus *will* collide on sufficiently long or adversarial inputs, and trusting an unguarded hash match would silently return a wrong answer — a genuinely worse failure mode for an interview answer than a slower-but-correct one.

## 6. Practical Usage

- **Reach for a Fenwick tree specifically for prefix-sum-and-point-update problems over a domain you can coordinate-compress** — simpler and faster (smaller constant factor) than a full segment tree when a segment tree's extra generality (arbitrary range aggregates, not just prefix sums) isn't needed.
- **Reach for a dynamic segment tree specifically when the domain is too large or too dynamic to coordinate-compress upfront** (My Calendar III's `[0, 1e9]` range with bookings arriving online, not known in advance).
- **Never trust a hash match as a substring/subarray equality proof without an explicit character-by-character (or element-by-element) confirmation**, unless the "hash" is provably an exact, lossless encoding (bit-packing over a small alphabet) rather than a genuinely lossy hash function.

## 7. Examples

**Problem 1 — LC 315, Count of Smaller Numbers After Self.**

```java
static List<Integer> countSmaller(int[] nums) {
    int n = nums.length;
    long[] uniq = Arrays.stream(nums).asLongStream().distinct().sorted().toArray();
    Fenwick bit = new Fenwick(uniq.length);
    Integer[] result = new Integer[n];
    for (int i = n - 1; i >= 0; i--) {
        int rank = lowerBound(uniq, nums[i]) + 1;
        result[i] = bit.query(rank - 1);
        bit.update(rank);
    }
    return Arrays.asList(result);
}
```

**Retrospective:** the right-to-left scan is what makes "already in the BIT" mean "to the right." **Complexity:** O(n log n).

**Problem 2 — LC 493, Reverse Pairs.**

```java
static int reversePairs(int[] nums) {
    Fenwick bit = new Fenwick(uniq.length);
    int count = 0, inserted = 0;
    for (int i = 0; i < n; i++) {
        long threshold = 2L * nums[i];
        int leRank = countLessOrEqual(uniq, threshold);
        int alreadyLE = bit.query(leRank);
        count += inserted - alreadyLE;
        bit.update(lowerBound(uniq, nums[i]) + 1);
        inserted++;
    }
    return count;
}
```

**Retrospective:** see Section 5's real, documented directional-bug correction. **Complexity:** O(n log n).

**Problem 3 — LC 327, Count of Range Sum.**

```java
static int countRangeSum(int[] nums, int lower, int upper) {
    long[] prefix = new long[n + 1];
    Fenwick bit = new Fenwick(uniq.length);
    for (int j = 1; j <= n; j++) {
        long lo = prefix[j] - upper, hi = prefix[j] - lower;
        count += bit.query(upperBoundInclusive(uniq, hi)) - bit.query(lowerBoundInclusive(uniq, lo));
        insert(bit, uniq, prefix[j]);
    }
    return count;
}
```

**Retrospective:** the same BIT-over-coordinate-compressed-values shape as Problems 1–2, querying a window instead of a single threshold. **Complexity:** O(n log n).

**Problem 4 — LC 307, Range Sum Query - Mutable.**

```java
static final class NumArray {
    final int n; final int[] tree;
    NumArray(int[] nums) {
        n = nums.length; tree = new int[2 * n];
        for (int i = 0; i < n; i++) tree[n + i] = nums[i];
        for (int i = n - 1; i > 0; i--) tree[i] = tree[2 * i] + tree[2 * i + 1];
    }
    void update(int index, int val) {
        int pos = index + n; tree[pos] = val;
        for (pos /= 2; pos >= 1; pos /= 2) tree[pos] = tree[2 * pos] + tree[2 * pos + 1];
    }
}
```

**Retrospective:** the canonical iterative, flat-array segment tree — no recursion overhead. **Complexity:** O(log n) per update/query, O(n) build.

**Problem 5 — LC 732, My Calendar III.**

```java
private void update(Node node, int lo, int hi, int l, int r, int delta) {
    if (r < lo || hi < l) return;
    if (l <= lo && hi <= r) { node.val += delta; node.lazy += delta; return; }
    int mid = lo + (hi - lo) / 2;
    if (node.left == null) node.left = new Node();
    if (node.right == null) node.right = new Node();
    update(node.left, lo, mid, l, r, delta);
    update(node.right, mid + 1, hi, l, r, delta);
    node.val = node.lazy + Math.max(node.left.val, node.right.val);
}
```

**Retrospective:** see Section 5's single-field dual-purpose invariant. **Complexity:** O(log(range)) per booking.

**Problem 6 — LC 218, The Skyline Problem.**

```java
void collect(int node, int start, int end, int inherited, int[] result) {
    int cur = Math.max(inherited, tree[node]);
    if (start == end) { result[start] = cur; return; }
    int mid = (start + end) / 2;
    collect(2 * node, start, mid, cur, result);
    collect(2 * node + 1, mid + 1, end, cur, result);
}
```

**Retrospective:** see Section 4's lazy-propagation-shortcut argument. **Complexity:** O(n log n).

**Problem 7 — LC 187, Repeated DNA Sequences.**

```java
static List<String> findRepeatedDnaSequences(String s) {
    int mask = (1 << 20) - 1;
    int hash = 0;
    Map<Integer, Integer> seen = new HashMap<>();
    for (int i = 0; i < n; i++) {
        hash = ((hash << 2) | code[s.charAt(i) - 'A']) & mask;
        if (i >= 9) {
            int count = seen.merge(hash, 1, Integer::sum);
            if (count == 2) result.add(s.substring(i - 9, i + 1));
        }
    }
    return result;
}
```

**Retrospective:** see Section 4's exact-bit-packing argument — no collision risk exists at all here. **Complexity:** O(n).

**Problem 8 — LC 1044, Longest Duplicate Substring.**

```java
private static int search(int[] nums, int len) {
    long h = 0;
    for (int i = 0; i < len; i++) h = (h * BASE + nums[i]) % MOD;
    Map<Long, List<Integer>> seen = new HashMap<>();
    for (int start = 1; start + len <= n; start++) {
        h = ((h - nums[start - 1] * highOrder % MOD + MOD) * BASE + nums[start + len - 1]) % MOD;
        List<Integer> candidates = seen.get(h);
        if (candidates != null) for (int idx : candidates) if (matches(nums, idx, start, len)) return start;
        seen.computeIfAbsent(h, k -> new ArrayList<>()).add(start);
    }
    return -1;
}
```

**Retrospective:** see Section 5's binary-search-over-length reduction. **Complexity:** O(n log n) expected.

## 8. Common Mistakes

- **Getting a doubling or comparison direction backwards in a coordinate-compressed query** — Reverse Pairs' own real, documented bug (Section 5), caught by the test suite, not by inspection.
- **Trusting a rolling-hash match without an explicit collision-guard comparison**, when the hash isn't a provably exact, lossless encoding — Section 5/9 names this as a genuinely worse failure mode (silently wrong) than a slower, always-correct approach.
- **Reaching for a full segment tree when a Fenwick tree would suffice** (prefix-sum-and-point-update only, no need for arbitrary range aggregates) — not incorrect, but unnecessary implementation complexity and constant-factor overhead.
- **Attempting to coordinate-compress a domain that's too large or arrives online, rather than recognizing a dynamic segment tree is needed instead** (My Calendar III, Section 4).

## 9. Edge Cases

- **A hand-derived expected test value that was itself wrong**, caught before running the suite: the source material documents a real correction to its own Skyline Problem test data (a two-building overlap case initially expected to omit an intermediate transition point) — worth noting as a real instance of test-data errors being just as possible as implementation errors, and needing the same rigor to catch.
- **Duplicate values in the input to a Fenwick-tree-backed counting problem** (Count of Range Sum's verified `[1,1,1]` case, correctly counting all 6 qualifying contiguous ranges) — coordinate compression must handle repeated values correctly via deduplication, not naively assign each occurrence a distinct rank.
- **A window length where no duplicate exists at all** in Longest Duplicate Substring's binary search — the feasibility check must correctly report infeasibility, not a false positive from an unguarded hash collision.

## 10. Performance Implications

Real, executed verification from `practice/java/advanced-structures/` (OpenJDK 21.0.12), re-run while writing this chapter:

```
Advanced Structures — Fenwick/BIT (LC 315, 493, 327): 12/12 assertions passed
Advanced Structures — Segment Tree (LC 307, 732, 218): 13/13 assertions passed
Advanced Structures — Rolling Hash (LC 187, 1044): 8/8 assertions passed
```

The performance lesson specific to this chapter is choosing the *right-sized* structure for the actual requirement: a Fenwick tree's smaller constant factor over a full segment tree matters when only prefix sums (not arbitrary range aggregates) are needed; a dynamic segment tree's per-node overhead is the necessary cost of supporting a domain too large to coordinate-compress; and an exact bit-packed encoding's total absence of collision risk (Repeated DNA Sequences) versus a real polynomial hash's genuine, real collision risk (Longest Duplicate Substring) is a direct function of whether the alphabet is small enough to pack losslessly — not a stylistic choice.

## 11. Trade-offs

| Choice | Gains | Costs |
|---|---|---|
| Fenwick tree | Smallest constant factor of the three structures; simple to implement | Only supports prefix-sum-style aggregates, not arbitrary range operations |
| Static array segment tree | O(log n) point update/range query, supports arbitrary combinable aggregates | Requires the domain to be small and known upfront |
| Dynamic segment tree | Supports domains far too large to materialize as an array | Higher per-node overhead; more complex implementation (node creation, potentially real lazy propagation) |
| Exact bit-packed hash | Zero collision risk, extremely fast | Only works when the alphabet is small enough to pack losslessly into a machine word |
| Polynomial rolling hash | Works for any alphabet and variable-length windows | Genuine collision risk — requires an explicit, real collision guard to stay correct |

## 12. Senior-Level Considerations (L3)

The Senior-level skill for this specific pattern is knowing *when not to reach for it* — recognizing that a simpler technique from this domain's other chapters (a plain hash map, a simple sort, a monotonic stack) already solves the actual problem at hand, and reserving these three structures for the genuinely narrow set of problems (repeated range updates/queries at scale, or genuine hash-collision-sensitive substring matching) where the added implementation complexity is actually justified by the problem's real requirements.

## 13. Staff/System-Level Considerations (L4)

At Staff scope, these three structures' real production analogs are genuinely consequential: a Fenwick or segment tree's incremental range-aggregate maintenance is the same underlying idea behind a real-time analytics dashboard that must answer "sum/max/count over this time range" continuously as new events arrive, without re-scanning full history on every query; a dynamic segment tree's lazy node creation over an enormous domain is the same technique behind sparse spatial-indexing structures (a quadtree or similar) used in mapping and geospatial systems; and the rolling-hash collision-guard discipline (Section 5/8) generalizes directly to any production system relying on a hash for approximate matching (deduplication, plagiarism detection, content fingerprinting) — where trusting an unguarded hash match, exactly as this chapter's own Longest Duplicate Substring problem warns, risks a silently wrong result at real scale.

## 14. Production Scenarios

No existing `production-cookbook/` entry has a segment-tree/Fenwick-tree/rolling-hash-specific algorithmic root cause, consistent with this pattern's status as explicitly outside the core roadmap (Section 1).

> Planned reference: a future `production-cookbook/` entry covering a real hash-collision-related data-integrity incident (e.g., a content-deduplication system that trusted an unguarded hash match and merged two genuinely distinct records) would be a natural, non-duplicative addition connecting this chapter's Section 5/13 collision-guard lesson to a genuine production system.

## 15. Interview Questions

### Question 1 — When would you choose a Fenwick tree over a full segment tree, given a segment tree can do everything a Fenwick tree can?

**Why interviewers ask it.** It's a rare but real Staff-level depth check, testing whether a candidate actually understands the trade-off between the two structures rather than only knowing that "more general" always means "better."

**Expected answer.** A Fenwick tree is simpler to implement (a single flat array, a handful of bit-manipulation-based operations) and has a smaller constant factor than a segment tree, specifically for prefix-sum-and-point-update operations. A segment tree's added generality — supporting arbitrary combinable aggregates (max, min, GCD, not just sum) and arbitrary range updates, not just prefix sums — comes at the cost of more implementation complexity and a larger constant factor for the specific subset of operations a Fenwick tree already handles well.

**Minimum acceptable answer.** States that Fenwick trees are simpler, even without precisely naming the aggregate-operation limitation.

**Strong Senior answer.** Names the specific limitation (prefix-sum-only, not arbitrary aggregates) as the actual reason to choose one over the other, not just "Fenwick trees are easier to code."

**Staff-level extension.** Connects this to a real system-design decision (Section 13) — choosing the narrower, cheaper structure specifically because the actual production requirement never needs the more general one's extra capability, avoiding unnecessary complexity.

**Common mistakes.** Treating the two structures as interchangeable, or assuming the more general structure is always the better default choice regardless of the actual requirement.

**Follow-up questions.** "How would coordinate compression change if the input values could be updated after insertion, not just queried?" (A genuinely harder variant — coordinate compression assumes the full value set is known upfront; online updates to previously unseen values would require a different technique, like a dynamic segment tree instead.)

### Question 2 — Why is a collision guard (an explicit character-by-character comparison) necessary for a rolling-hash-based substring matching algorithm, but not for a DNA-sequence-matching algorithm using a similar-looking technique?

**Why interviewers ask it.** It's a precise test of whether "rolling hash" is understood as one undifferentiated technique or two structurally different approaches (exact bit-packing vs. genuinely lossy hashing) with different collision guarantees.

**Expected answer.** The DNA-sequence case has only 4 possible symbols, so a fixed-length window (10 characters) packs losslessly into a small number of bits (20 bits for 2 bits per base) — this is an exact, reversible encoding, not a lossy hash, so two genuinely different windows can never produce the same encoded value, and no collision guard is needed. The general substring-matching case has a 26-letter alphabet and variable-length windows, which can't be packed losslessly into a fixed-size integer this way — the "hash" there is a genuinely lossy function (via modular arithmetic), meaning two different substrings *can* produce the same hash value by coincidence, and trusting a match without verifying it character-by-character risks a silently wrong answer.

**Minimum acceptable answer.** States that one approach needs a collision guard and the other doesn't, even without precisely explaining the lossless-vs-lossy distinction.

**Strong Senior answer.** Explains the exact bit-width math (4 symbols × 10 characters = 20 bits, fits losslessly in an `int`) as the specific reason the DNA case has zero collision risk.

**Staff-level extension.** Generalizes the principle to a broader engineering discipline: never trust an unguarded hash match as proof of equality unless the hash is provably a lossless, reversible encoding of the full input — a real, consequential distinction in any production system relying on hashing for deduplication or matching (Section 13).

**Common mistakes.** Treating all "hash-based" substring techniques as uniformly needing (or uniformly not needing) a collision guard, rather than deriving the answer from whether the specific encoding is lossless.

**Follow-up questions.** "What would happen if the DNA alphabet had 5 symbols instead of 4?" (2 bits per base would no longer suffice to distinguish 5 values, requiring 3 bits per base — a real, concrete design constraint driven directly by the alphabet's actual size.)

## 16. Coding/Practice Exercises

- Run the [existing practice code](../../practice/java/advanced-structures/) yourself (`fenwick/`, `segment-tree/`, `rolling-hash/`) and confirm the same 12/12, 13/13, and 8/8 assertions pass.
- Reproduce Reverse Pairs' real, documented directional bug (Section 5) yourself: implement the backwards version first, run it against an ascending array, and confirm it produces a wrong count — then apply the fix and confirm the count becomes correct.
- Implement a segment tree supporting range-minimum-query instead of range-sum, adapting the flat, iterative array-based structure from Section 7, Problem 4 — confirm the combine operation (`Math.min` instead of `+`) is the only change needed to the core structure.

## 17. Debugging Exercises

**Symptom:** a substring-deduplication feature using a rolling hash occasionally, rarely, incorrectly merges two genuinely different substrings as duplicates.

**Diagnose:** this is precisely Section 5/8's collision-guard warning made real — check whether the implementation trusts a hash match directly as proof of equality, without a follow-up character-by-character (or element-by-element) comparison. Confirm by constructing or searching for a genuine hash collision for the specific hash function and modulus in use (a real, findable pair of distinct inputs producing the same hash value), and checking whether the deduplication logic incorrectly treats them as identical. The fix, exactly as Longest Duplicate Substring (Section 7, Problem 8) demonstrates, is adding an explicit equality check after any hash match, before trusting it.

## 18. Design Exercises

**Design constraint:** design a real-time analytics system that must answer "what is the sum of all events in this time range" continuously, as new events arrive at a high rate, without re-scanning the full event history on every query.

Design this using a Fenwick tree (or segment tree, if range-max or another non-sum aggregate is also needed) indexed by a coordinate-compressed or bucketed time dimension, directly applying this chapter's Section 4/13 technique: each new event becomes an O(log n) point update; each range-sum query becomes an O(log n) prefix-difference query, both dramatically faster than an O(n) full re-scan as the event history grows. State explicitly the choice between a static structure (if the time range and bucket granularity are fixed and known upfront) versus a dynamic segment tree (if the time range must extend indefinitely into the future without a known upper bound) — the same static-vs-dynamic trade-off Section 4 draws between Range Sum Query - Mutable and My Calendar III.

## 19. Further Reading

- [Binary Search, Including Search-on-Answer](binary-search-and-search-on-answer.md) — the monotonic-feasibility-over-a-candidate-range technique Longest Duplicate Substring (Section 5) applies directly to search length.
- [Hashing Patterns and Frequency Maps](hashing-patterns-and-frequency-maps.md) — the coordinate-compression technique (Section 4) is a direct application of that chapter's hash-map-based O(1) lookup principle.
- [Trees, BSTs, and Traversal Patterns](trees-bst-and-traversal-patterns.md) — segment trees and Fenwick trees are both, at their core, specialized binary tree structures built on the same recursive/hierarchical principles that chapter establishes for ordinary binary trees.

## 20. Mastery Checklist

| Level | You can... | Verify with |
|---|---|---|
| L1 | Explain, in plain language, what a Fenwick tree, a segment tree, and a rolling hash each let you do faster than a naive approach | [Section 3](#3-foundation-l1) |
| L2 | Choose the right structure (Fenwick vs. segment tree, static vs. dynamic, exact bit-packing vs. polynomial hash) for a new problem's specific requirements | [Interview Question 1](#question-1--when-would-you-choose-a-fenwick-tree-over-a-full-segment-tree-given-a-segment-tree-can-do-everything-a-fenwick-tree-can) |
| L3 | Derive the coordinate-compression technique, the dynamic segment tree's lazy-node-creation mechanism, and the exact-vs-lossy hashing distinction | [Section 10's real verification](#10-performance-implications), [Section 5](#5-how-it-works-internally-l3) |
| L4 | Diagnose a real hash-collision-caused data-integrity bug (Section 17), and design a real-time analytics system using these structures deliberately (Section 18) | [Debugging Exercise](#17-debugging-exercises), [Section 13](#13-staffsystem-level-considerations-l4) |
