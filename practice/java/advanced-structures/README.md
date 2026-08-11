# Advanced Structures (T-1418) — Supplemental, Outside Plan C's Core Roadmap

**Status:** optional supplement, not part of the numbered `study-packs/week-01`–`week-25` sequence. `00-project/learning-roadmap.md` §5 states Plan C **deliberately excludes** the entire Expert tier — T-1418 (Advanced Structures: segment tree, Fenwick/BIT, rolling hash) among it — as "the most common misallocation in senior interview prep." This directory exists because the user explicitly asked to close the gap anyway, not because the roadmap changed its recommendation. Treat this material as recognition-level polish on top of an already-complete core program, not a prerequisite for it.

**8 problems total, split across the structure the register names: segment tree, Fenwick/BIT, rolling hash.** Brings T-1418 from 0/8 to 8/8. Batch 1 (this file's current content) covers the 3 Fenwick/BIT problems; segment tree and rolling hash problems land in later bounded batches, per `CLAUDE.md`'s instruction against generating an entire deliverable in one operation.

---

## Batch 1 — Fenwick Tree / Binary Indexed Tree (3 problems)

**All code below was compiled and executed — see `fenwick/` for the real source, and the Verification section below for the exact commands and real pass counts.**

A **Fenwick tree** (Binary Indexed Tree, BIT) supports two operations over a fixed-size array in O(log n): point update (add 1 at an index) and prefix-sum query (sum of everything up to an index). All three problems below reduce to "count things up to a boundary, over an index space that grows one element at a time" — exactly what a BIT is for — after a coordinate-compression step maps arbitrary integer values onto a dense 1..m rank space the tree can index into.

### Problem 1 — LC 315 Count of Smaller Numbers After Self

**Pattern:** for each index, count how many elements to its right are strictly smaller. Process the array **right to left**, coordinate-compress the values into ranks, and query the BIT — which only holds elements already seen, i.e. everything to the current index's right — for the count of ranks strictly below the current element's rank, before inserting it.

```java
static List<Integer> countSmaller(int[] nums) {
    int n = nums.length;
    long[] uniq = Arrays.stream(nums).asLongStream().distinct().sorted().toArray();
    int m = uniq.length;
    Fenwick bit = new Fenwick(m);
    Integer[] result = new Integer[n];
    for (int i = n - 1; i >= 0; i--) {
        int rank = lowerBound(uniq, nums[i]) + 1; // 1-indexed rank of nums[i] itself
        result[i] = bit.query(rank - 1);           // count of already-seen values strictly less than nums[i]
        bit.update(rank);
    }
    return Arrays.asList(result);
}
```

**Retrospective:** the right-to-left scan is what makes "already in the BIT" mean "to the right" — the entire problem collapses to one query-then-insert per element once that direction is fixed. A brute-force O(n²) comparison is the obvious first answer; the BIT's value is turning "count smaller to the right" into a running, incrementally-maintained structure instead of re-scanning. **Complexity:** O(n log n) total (n inserts/queries, each O(log m), m ≤ n).

### Problem 2 — LC 493 Reverse Pairs

**Pattern:** count pairs `i < j` with `nums[i] > 2 * nums[j]` — a variant of Problem 1's shape, but with an asymmetric doubling condition and the scan direction reversed (left to right, `j` plays the "current" role and already-inserted values are the earlier `i`s). The BIT tracks how many already-inserted values are `<= 2 * nums[j]`; the rest — `inserted so far` minus that count — are `> 2 * nums[j]` and each forms a qualifying pair with the current `j`.

```java
static int reversePairs(int[] nums) {
    int n = nums.length;
    long[] uniq = Arrays.stream(nums).asLongStream().distinct().sorted().toArray();
    int m = uniq.length;
    Fenwick bit = new Fenwick(m);
    int count = 0, inserted = 0;
    for (int i = 0; i < n; i++) {
        long threshold = 2L * nums[i];
        int leRank = countLessOrEqual(uniq, threshold); // # of uniq values <= threshold
        int alreadyLE = bit.query(leRank);               // # of already-inserted values <= threshold
        count += inserted - alreadyLE;                   // the rest are strictly greater -> qualify
        int rank = lowerBound(uniq, nums[i]) + 1;
        bit.update(rank);
        inserted++;
    }
    return count;
}
```

**Retrospective:** the first (wrong) implementation of this problem queried "already-inserted values `v` with `2*v < nums[i]`" — a plausible-looking mirror of the doubling condition that is actually backwards: the LC493 definition requires the *earlier* value to be the large one (`nums[i] > 2*nums[j]` for `i < j`), not the later one. That version passed 2 of 4 test cases (including, misleadingly, the first) and silently produced wrong counts on an ascending array and a mixed one — caught by the test suite, not by inspection, which is exactly the kind of subtle off-by-direction bug this problem's known reputation for tripping up candidates comes from. The fix: reframe the query as "how many already-inserted values are `> 2*nums[j]`," computed as `total inserted - count(<= threshold)`, which correctly places the doubling on the *later* index's value while counting the *earlier* index's values against it. **Complexity:** O(n log n).

### Problem 3 — LC 327 Count of Range Sum

**Pattern:** count contiguous subarray sums falling within `[lower, upper]`. Convert to prefix sums `P[0..n]` (`P[0] = 0`); a range sum `nums[i..j-1] = P[j] - P[i]` is in range iff `P[j] - upper <= P[i] <= P[j] - lower`. Insert prefix values into a BIT as the scan proceeds left to right, and for each `j` query the count of already-inserted `P[i]` (`i < j`) whose *value* falls in that window — via two rank-boundary binary searches into the coordinate-compressed prefix-value space, then a BIT range query (the difference of two prefix-sum queries).

```java
static int countRangeSum(int[] nums, int lower, int upper) {
    int n = nums.length;
    long[] prefix = new long[n + 1];
    for (int i = 0; i < n; i++) prefix[i + 1] = prefix[i] + nums[i];
    long[] uniq = Arrays.stream(prefix).distinct().sorted().toArray();
    Fenwick bit = new Fenwick(uniq.length);
    int count = 0;
    insert(bit, uniq, prefix[0]);
    for (int j = 1; j <= n; j++) {
        long lo = prefix[j] - upper, hi = prefix[j] - lower;
        int loIdx = lowerBoundInclusive(uniq, lo); // first index with uniq[idx] >= lo
        int hiIdx = upperBoundInclusive(uniq, hi); // first index with uniq[idx] > hi
        count += bit.query(hiIdx) - bit.query(loIdx);
        insert(bit, uniq, prefix[j]);
    }
    return count;
}
```

**Retrospective:** this is the same BIT-over-coordinate-compressed-values shape as Problems 1 and 2, but querying a *window* (two boundaries) instead of a single one-sided threshold — the natural generalization once the prefix-sum reduction is in place. The reduction itself (range sum → difference of two prefix sums → range query over prefix values) is the part that's easy to state and easy to get backwards under pressure; getting the window direction right (`P[j]-upper <= P[i] <= P[j]-lower`, not the reverse) is the specific trap this problem is known for. **Complexity:** O(n log n).

---

## Verification

| Problem | Status | Real assertions |
|---|---|---|
| LC 315 Count of Smaller Numbers After Self | **Executed.** All passed on first run. | 4/4 pass |
| LC 493 Reverse Pairs | **Executed.** First implementation had the doubling condition backwards — 2/4 assertions failed on run, including a plausible-looking initial pass on the readme example alone; corrected before citing as passing (see Problem 2's retrospective). | 4/4 pass |
| LC 327 Count of Range Sum | **Executed.** All passed on first run. | 4/4 pass |
| **Batch 1 total** | | **12/12 pass** |

```
cd practice/java/advanced-structures/fenwick
javac -d out src/*.java && java -cp out Main
```

Source: [`practice/java/advanced-structures/fenwick/`](fenwick/)

## Coverage impact

| Register item | Before | After this batch |
|---|---|---|
| T-1418 Advanced Structures (Fenwick/BIT sub-pattern) | 0/8 | 3/8 |

Remaining: 3 segment-tree problems, 2 rolling-hash problems, in later bounded batches.

## Errata addressed this batch

**LC 493 Reverse Pairs** — the doubling condition's direction was initially implemented backwards (see Problem 2's retrospective above); caught by the test suite before being cited as passing, not by inspection. Documented here per this project's standing rule to record real errata rather than silently fix and move on.
